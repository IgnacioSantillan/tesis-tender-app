import { Injectable, InternalServerErrorException, Logger, NotFoundException } from '@nestjs/common';
import { AuthenticatedUser } from '../auth/authenticated-user';
import { NotificationsService } from '../notifications/notifications.service';
import { DryingPredictionResponseDto } from '../predictions/dto/drying-prediction-response.dto';
import { PredictionsService } from '../predictions/predictions.service';
import { CreateLaundryLoadRequestDto } from './dto/create-laundry-load-request.dto';
import { LaundryLoadResponseDto } from './dto/laundry-load-response.dto';
import { UpdateLaundryLoadStatusRequestDto } from './dto/update-laundry-load-status-request.dto';
import { mapLaundryLoadRow } from './laundry-loads.mapper';
import { LaundryLoadsSupabaseDataSource } from './laundry-loads.supabase-data-source';
import { validateCreateLaundryLoadInput, validateLaundryLoadStatus } from './laundry-loads.validation';

@Injectable()
export class LaundryLoadsService {
  private readonly logger = new Logger(LaundryLoadsService.name);

  constructor(
    private readonly dataSource: LaundryLoadsSupabaseDataSource,
    private readonly notificationsService: NotificationsService,
    private readonly predictionsService: PredictionsService,
  ) {}

  async listLaundryLoads(user: AuthenticatedUser): Promise<LaundryLoadResponseDto[]> {
    const { data, error } = await this.dataSource.listByUser(user.id);

    if (error) {
      throw new InternalServerErrorException('Unable to load laundry loads');
    }

    return (data ?? []).map(mapLaundryLoadRow);
  }

  async createLaundryLoad(
    user: AuthenticatedUser,
    request: CreateLaundryLoadRequestDto,
  ): Promise<LaundryLoadResponseDto> {
    const input = validateCreateLaundryLoadInput(request);
    const { data, error } = await this.dataSource.create({
      userId: user.id,
      washerId: input.washerId,
      clothingType: input.clothingType,
      washingProgram: input.washingProgram,
      locationId: input.locationId,
      dryingLocationId: input.dryingLocationId,
      spinRpm: input.spinRpm,
      loadSize: input.loadSize,
    });

    if (error || !data) {
      throw new InternalServerErrorException('Unable to create laundry load');
    }

    let response = mapLaundryLoadRow(data);
    const prediction = await this.calculatePredictionForLoad(user, response, 'create');
    response = await this.persistEnergyEstimateSnapshot(user, response, prediction);
    await this.scheduleIdealHangingWindowNotification(user, response, prediction);

    return response;
  }

  async updateLaundryLoadStatus(
    user: AuthenticatedUser,
    loadId: string,
    request: UpdateLaundryLoadStatusRequestDto,
  ): Promise<LaundryLoadResponseDto> {
    const status = validateLaundryLoadStatus(request.status);
    const changedAt = new Date().toISOString();
    const dryingSnapshot = status === 'DRYING'
      ? await this.calculateDryingStartSnapshot(user, loadId)
      : null;
    const { data, error } = await this.dataSource.updateStatus({
      userId: user.id,
      loadId,
      status,
      startedAt: status === 'WASHING' ? changedAt : undefined,
      dryingStartedAt: status === 'DRYING' ? changedAt : undefined,
      dryingEstimatedMinutesAtStart: dryingSnapshot?.estimatedDryingMinutes,
      dryingEstimatedPickupAt: dryingSnapshot?.estimatedPickupAt,
      completedAt: status === 'COMPLETED' ? changedAt : undefined,
    });

    if (error) {
      throw new InternalServerErrorException('Unable to update laundry load status');
    }

    if (!data) {
      throw new NotFoundException('Laundry load not found');
    }

    const response = mapLaundryLoadRow(data);

    if (response.status === 'WASHING' || response.status === 'DRYING') {
      await this.notificationsService.cancelPendingLaundryNotifications(user, response.id, ['IDEAL_HANGING_TIME']);
    }

    if (response.status === 'DRYING') {
      await this.notificationsService.scheduleDryingComplete(user, response.id, response.dryingEstimatedPickupAt);
    }

    if (response.status === 'COMPLETED' || response.status === 'CANCELLED') {
      await this.notificationsService.cancelPendingLaundryNotifications(user, response.id);
    }

    if (response.status === 'COMPLETED') {
      const result = await this.notificationsService.dispatchLaundryLoadCompleted(user, response.id);
      if (result.status !== 'SENT') {
        this.logger.log(`Laundry completion push ${result.status.toLowerCase()} for ${response.id}`);
      }
    }

    return response;
  }

  private async calculateDryingStartSnapshot(
    user: AuthenticatedUser,
    loadId: string,
  ): Promise<DryingStartSnapshot | null> {
    const { data, error } = await this.dataSource.findByUserAndId(user.id, loadId);

    if (error) {
      this.logger.warn(`Unable to load laundry data for drying snapshot ${loadId}: ${formatOperationalError(error)}`);
      return null;
    }

    if (!data) {
      return null;
    }

    const load = mapLaundryLoadRow(data);

    try {
      const prediction = await this.predictionsService.calculateDryingPrediction(
        {
          laundryLoadId: load.id,
          clothingType: load.clothingType,
          washingProgram: load.washingProgram,
          dryingMethod: toDryingMethod(load.dryingLocationId),
          locationId: load.locationId,
          dryingLocationId: load.dryingLocationId,
          spinRpm: load.spinRpm,
          loadSize: load.loadSize,
        },
        user,
      );

      return {
        estimatedDryingMinutes: prediction.estimatedDryingMinutes,
        estimatedPickupAt: prediction.estimatedPickupAt,
      };
    } catch (error) {
      this.logger.warn(`Unable to calculate drying snapshot for ${loadId}: ${formatOperationalError(error)}`);
      return null;
    }
  }

  private async calculatePredictionForLoad(
    user: AuthenticatedUser,
    load: LaundryLoadResponseDto,
    context: string,
  ): Promise<DryingPredictionResponseDto | null> {
    try {
      return await this.predictionsService.calculateDryingPrediction(
        {
          laundryLoadId: load.id,
          clothingType: load.clothingType,
          washingProgram: load.washingProgram,
          dryingMethod: toDryingMethod(load.dryingLocationId),
          locationId: load.locationId,
          dryingLocationId: load.dryingLocationId,
          spinRpm: load.spinRpm,
          loadSize: load.loadSize,
        },
        user,
      );
    } catch (error) {
      this.logger.warn(`Unable to calculate ${context} prediction for ${load.id}: ${formatOperationalError(error)}`);
      return null;
    }
  }

  private async persistEnergyEstimateSnapshot(
    user: AuthenticatedUser,
    load: LaundryLoadResponseDto,
    prediction: DryingPredictionResponseDto | null,
  ): Promise<LaundryLoadResponseDto> {
    const estimatedCost = prediction?.estimatedCost;
    if (!estimatedCost) {
      return load;
    }

    const { data, error } = await this.dataSource.updateEnergyEstimate({
      userId: user.id,
      loadId: load.id,
      estimatedWashingEnergyKwh: estimatedCost.estimatedEnergyKwh,
      estimatedWashingWaterLiters: estimatedCost.estimatedWaterLiters,
      estimatedWashingCostAmount: estimatedCost.amount,
      estimatedWashingCostCurrency: estimatedCost.currency,
      estimatedWashingCostLevel: estimatedCost.level,
      estimatedWashingCostConfidence: estimatedCost.confidence,
    });

    if (error) {
      this.logger.warn(`Unable to persist energy estimate for ${load.id}: ${formatOperationalError(error)}`);
      return load;
    }

    return data ? mapLaundryLoadRow(data) : load;
  }

  private async scheduleIdealHangingWindowNotification(
    user: AuthenticatedUser,
    load: LaundryLoadResponseDto,
    prediction: DryingPredictionResponseDto | null,
  ): Promise<void> {
    if (!prediction) {
      return;
    }

    try {
      await this.notificationsService.scheduleIdealHangingTime(
        user,
        load.id,
        prediction.recommendedHangWindowStart,
        prediction.recommendedHangWindowEnd,
      );
    } catch (error) {
      this.logger.warn(`Unable to schedule ideal hanging window for ${load.id}: ${formatOperationalError(error)}`);
    }
  }
}

interface DryingStartSnapshot {
  estimatedDryingMinutes: number;
  estimatedPickupAt: string;
}

function toDryingMethod(dryingLocationId: LaundryLoadResponseDto['dryingLocationId']): 'INDOOR' | 'OUTDOOR' {
  return dryingLocationId === 'INDOOR' || dryingLocationId === 'LAUNDRY_ROOM' ? 'INDOOR' : 'OUTDOOR';
}

function formatOperationalError(error: unknown): string {
  if (!error) {
    return 'unknown';
  }

  if (error instanceof Error) {
    return `${error.name}: ${error.message}`;
  }

  if (typeof error !== 'object') {
    return String(error);
  }

  const maybeError = error as { code?: unknown; message?: unknown; details?: unknown; hint?: unknown };
  return [
    ['code', maybeError.code],
    ['message', maybeError.message],
    ['details', maybeError.details],
    ['hint', maybeError.hint],
  ]
    .filter(([, value]) => value !== undefined && value !== null && String(value).trim().length > 0)
    .map(([key, value]) => `${key}=${String(value)}`)
    .join(' ') || 'unrecognized error object';
}
