import { Injectable } from '@nestjs/common';
import { DryingLocationId } from '../drying-locations/drying-location-types';
import { LoadSize, SpinSpeedRpm } from '../energy/energy-prediction-types';
import { SupabaseService } from '../supabase/supabase.service';
import { ClothingType, LaundryLoadStatus, WashingProgram } from './dto/laundry-load-types';
import { LaundryLoadRow } from './laundry-loads.mapper';

export interface CreateLaundryLoadRecord {
  userId: string;
  washerId: string | null;
  clothingType: ClothingType;
  washingProgram: WashingProgram;
  locationId: string;
  dryingLocationId: DryingLocationId;
  spinRpm: SpinSpeedRpm | null;
  loadSize: LoadSize | null;
}

export interface UpdateLaundryLoadStatusRecord {
  userId: string;
  loadId: string;
  status: LaundryLoadStatus;
  startedAt?: string;
  dryingStartedAt?: string;
  dryingEstimatedMinutesAtStart?: number;
  dryingEstimatedPickupAt?: string;
  completedAt?: string;
}

export interface UpdateLaundryLoadEnergyEstimateRecord {
  userId: string;
  loadId: string;
  estimatedWashingEnergyKwh: number | null;
  estimatedWashingWaterLiters: number | null;
  estimatedWashingCostAmount: number | null;
  estimatedWashingCostCurrency: string | null;
  estimatedWashingCostLevel: string | null;
  estimatedWashingCostConfidence: string | null;
}

export interface LaundryLoadQueryResult<T> {
  data: T | null;
  error: unknown;
}

interface LaundryLoadEnergyFieldsRow {
  load_size: string | null;
}

const LAUNDRY_LOAD_COLUMNS =
  'id,washer_id,clothing_type,washing_program,status,location_id,drying_location_id,spin_rpm,load_size,estimated_washing_energy_kwh,estimated_washing_water_liters,estimated_washing_cost_amount,estimated_washing_cost_currency,estimated_washing_cost_level,estimated_washing_cost_confidence,created_at,started_at,drying_started_at,drying_estimated_minutes_at_start,drying_estimated_pickup_at,completed_at';

@Injectable()
export class LaundryLoadsSupabaseDataSource {
  constructor(private readonly supabaseService: SupabaseService) {}

  async listByUser(userId: string): Promise<LaundryLoadQueryResult<LaundryLoadRow[]>> {
    const { data, error } = await this.supabaseService
      .getClient()
      .from('laundry_loads')
      .select(LAUNDRY_LOAD_COLUMNS)
      .eq('user_id', userId)
      .order('created_at', { ascending: false });

    return { data: (data ?? []) as LaundryLoadRow[], error };
  }

  async findByUserAndId(userId: string, loadId: string): Promise<LaundryLoadQueryResult<LaundryLoadRow>> {
    const { data, error } = await this.supabaseService
      .getClient()
      .from('laundry_loads')
      .select(LAUNDRY_LOAD_COLUMNS)
      .eq('id', loadId)
      .eq('user_id', userId)
      .maybeSingle();

    return { data: data as LaundryLoadRow | null, error };
  }

  async create(record: CreateLaundryLoadRecord): Promise<LaundryLoadQueryResult<LaundryLoadRow>> {
    const { data, error } = await this.supabaseService
      .getClient()
      .from('laundry_loads')
      .insert({
        user_id: record.userId,
        washer_id: record.washerId,
        clothing_type: record.clothingType,
        washing_program: record.washingProgram,
        status: 'PLANNED',
        location_id: record.locationId,
        drying_location_id: record.dryingLocationId,
        spin_rpm: record.spinRpm,
        load_size: record.loadSize,
      })
      .select(LAUNDRY_LOAD_COLUMNS)
      .single();

    return { data: data as LaundryLoadRow | null, error };
  }

  async updateStatus(record: UpdateLaundryLoadStatusRecord): Promise<LaundryLoadQueryResult<LaundryLoadRow>> {
    const { data: existingRow, error: lookupError } = await this.supabaseService
      .getClient()
      .from('laundry_loads')
      .select('load_size')
      .eq('id', record.loadId)
      .eq('user_id', record.userId)
      .maybeSingle();

    if (lookupError) {
      return { data: null, error: lookupError };
    }

    const updatePayload: {
      status: LaundryLoadStatus;
      started_at: string | undefined;
      drying_started_at: string | undefined;
      drying_estimated_minutes_at_start: number | undefined;
      drying_estimated_pickup_at: string | undefined;
      completed_at: string | undefined;
      load_size?: LoadSize | null;
    } = {
      status: record.status,
      started_at: record.startedAt,
      drying_started_at: record.dryingStartedAt,
      drying_estimated_minutes_at_start: record.dryingEstimatedMinutesAtStart,
      drying_estimated_pickup_at: record.dryingEstimatedPickupAt,
      completed_at: record.completedAt,
    };
    const normalizedLoadSize = normalizePersistedLoadSize((existingRow as LaundryLoadEnergyFieldsRow | null)?.load_size ?? null);

    if ((existingRow as LaundryLoadEnergyFieldsRow | null)?.load_size !== normalizedLoadSize) {
      updatePayload.load_size = normalizedLoadSize;
    }

    const { data, error } = await this.supabaseService
      .getClient()
      .from('laundry_loads')
      .update(updatePayload)
      .eq('id', record.loadId)
      .eq('user_id', record.userId)
      .select(LAUNDRY_LOAD_COLUMNS)
      .maybeSingle();

    return { data: data as LaundryLoadRow | null, error };
  }

  async updateEnergyEstimate(
    record: UpdateLaundryLoadEnergyEstimateRecord,
  ): Promise<LaundryLoadQueryResult<LaundryLoadRow>> {
    const { data, error } = await this.supabaseService
      .getClient()
      .from('laundry_loads')
      .update({
        estimated_washing_energy_kwh: record.estimatedWashingEnergyKwh,
        estimated_washing_water_liters: record.estimatedWashingWaterLiters,
        estimated_washing_cost_amount: record.estimatedWashingCostAmount,
        estimated_washing_cost_currency: record.estimatedWashingCostCurrency,
        estimated_washing_cost_level: record.estimatedWashingCostLevel,
        estimated_washing_cost_confidence: record.estimatedWashingCostConfidence,
      })
      .eq('id', record.loadId)
      .eq('user_id', record.userId)
      .select(LAUNDRY_LOAD_COLUMNS)
      .maybeSingle();

    return { data: data as LaundryLoadRow | null, error };
  }
}

function normalizePersistedLoadSize(value: string | null): LoadSize | null {
  const normalized = value?.trim().toUpperCase() ?? '';
  return normalized === 'SMALL' || normalized === 'MEDIUM' || normalized === 'LARGE'
    ? normalized
    : null;
}
