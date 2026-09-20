import { SpinSpeedRpm } from '../energy/energy-prediction-types';
import { WasherResponseDto } from './dto/washer-response.dto';
import { WasherEnergyLabel } from './dto/washer-energy-label';
import { WasherType } from './dto/washer-type';

export interface WasherRow {
  id: string;
  name: string;
  type: WasherType;
  capacity_kg: number | null;
  energy_label: WasherEnergyLabel | null;
  water_usage_liters: number | null;
  default_spin_rpm: SpinSpeedRpm | null;
  is_primary: boolean | null;
}

export function mapWasherRow(row: WasherRow): WasherResponseDto {
  return {
    id: row.id,
    name: row.name,
    type: row.type,
    capacityKg: row.capacity_kg,
    energyLabel: row.energy_label,
    waterUsageLiters: row.water_usage_liters,
    defaultSpinRpm: row.default_spin_rpm,
    isPrimary: row.is_primary ?? false,
  };
}
