import { Injectable, Logger } from '@nestjs/common';
import { LoadSize, LOAD_SIZES, SpinSpeedRpm, SPIN_SPEED_RPMS } from '../energy/energy-prediction-types';
import { SupabaseService } from '../supabase/supabase.service';
import { WasherEnergyLabel, WASHER_ENERGY_LABELS } from '../washers/dto/washer-energy-label';

interface LaundryLoadEnergyRow {
  washer_id: string | null;
  spin_rpm: number | null;
  load_size: string | null;
}

interface WasherEnergyRow {
  energy_label: string | null;
  capacity_kg: number | string | null;
  water_usage_liters: number | string | null;
  default_spin_rpm: number | null;
}

export interface PredictionEnergyMetadata {
  spinRpm: SpinSpeedRpm | null;
  loadSize: LoadSize | null;
  washerEnergyLabel: WasherEnergyLabel | null;
  washerCapacityKg: number | null;
  waterUsageLiters: number | null;
}

const EMPTY_METADATA: PredictionEnergyMetadata = {
  spinRpm: null,
  loadSize: null,
  washerEnergyLabel: null,
  washerCapacityKg: null,
  waterUsageLiters: null,
};

@Injectable()
export class PredictionEnergyMetadataDataSource {
  private readonly logger = new Logger(PredictionEnergyMetadataDataSource.name);

  constructor(private readonly supabaseService: SupabaseService) {}

  async resolveForLaundryLoad(userId: string, laundryLoadId: string): Promise<PredictionEnergyMetadata> {
    if (!isUuid(laundryLoadId)) {
      return EMPTY_METADATA;
    }

    const client = this.supabaseService.getClient();
    const { data: load, error: loadError } = await client
      .from('laundry_loads')
      .select('washer_id,spin_rpm,load_size')
      .eq('id', laundryLoadId)
      .eq('user_id', userId)
      .maybeSingle();

    if (loadError) {
      this.logger.warn(`Unable to load prediction energy metadata for laundry load ${laundryLoadId}`);
      return EMPTY_METADATA;
    }

    if (!load) {
      return EMPTY_METADATA;
    }

    const loadRow = load as LaundryLoadEnergyRow;
    const washer = loadRow.washer_id ? await this.loadWasher(userId, loadRow.washer_id) : null;

    return {
      spinRpm: normalizeSpinRpm(loadRow.spin_rpm) ?? normalizeSpinRpm(washer?.default_spin_rpm),
      loadSize: normalizeLoadSize(loadRow.load_size),
      washerEnergyLabel: normalizeEnergyLabel(washer?.energy_label),
      washerCapacityKg: normalizePositiveNumber(washer?.capacity_kg),
      waterUsageLiters: normalizePositiveNumber(washer?.water_usage_liters),
    };
  }

  private async loadWasher(userId: string, washerId: string): Promise<WasherEnergyRow | null> {
    const { data, error } = await this.supabaseService
      .getClient()
      .from('washers')
      .select('energy_label,capacity_kg,water_usage_liters,default_spin_rpm')
      .eq('id', washerId)
      .eq('user_id', userId)
      .maybeSingle();

    if (error) {
      this.logger.warn(`Unable to load washer energy metadata for washer ${washerId}`);
      return null;
    }

    return data ? (data as WasherEnergyRow) : null;
  }
}

function normalizeSpinRpm(value: unknown): SpinSpeedRpm | null {
  return typeof value === 'number' && SPIN_SPEED_RPMS.includes(value as SpinSpeedRpm) ? (value as SpinSpeedRpm) : null;
}

function normalizeLoadSize(value: unknown): LoadSize | null {
  return typeof value === 'string' && LOAD_SIZES.includes(value as LoadSize) ? (value as LoadSize) : null;
}

function normalizeEnergyLabel(value: unknown): WasherEnergyLabel | null {
  if (typeof value !== 'string') {
    return null;
  }

  const normalized = value.trim().toUpperCase();
  return WASHER_ENERGY_LABELS.includes(normalized as WasherEnergyLabel) ? (normalized as WasherEnergyLabel) : null;
}

function normalizePositiveNumber(value: unknown): number | null {
  const parsed = typeof value === 'string' ? Number(value) : value;
  return typeof parsed === 'number' && Number.isFinite(parsed) && parsed > 0 ? parsed : null;
}

function isUuid(value: string): boolean {
  return /^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$/i.test(value);
}
