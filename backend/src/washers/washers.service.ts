import { Injectable, InternalServerErrorException, NotFoundException } from '@nestjs/common';
import { AuthenticatedUser } from '../auth/authenticated-user';
import { SupabaseService } from '../supabase/supabase.service';
import { SaveWasherRequestDto } from './dto/save-washer-request.dto';
import { WasherResponseDto } from './dto/washer-response.dto';
import { mapWasherRow, WasherRow } from './washers.mapper';
import { validateWasherInput } from './washers.validation';

const WASHER_COLUMNS = 'id,name,type,capacity_kg,energy_label,water_usage_liters,default_spin_rpm,is_primary';

@Injectable()
export class WashersService {
  constructor(private readonly supabaseService: SupabaseService) {}

  async listWashers(user: AuthenticatedUser): Promise<WasherResponseDto[]> {
    const { data, error } = await this.supabaseService
      .getClient()
      .from('washers')
      .select(WASHER_COLUMNS)
      .eq('user_id', user.id)
      .is('retired_at', null)
      .order('created_at', { ascending: false });

    if (error) {
      throw new InternalServerErrorException('Unable to load washers');
    }

    return ((data ?? []) as WasherRow[]).map(mapWasherRow);
  }

  async createWasher(user: AuthenticatedUser, request: SaveWasherRequestDto): Promise<WasherResponseDto> {
    const input = validateWasherInput(request);
    const { data, error } = await this.supabaseService
      .getClient()
      .from('washers')
      .insert({
        user_id: user.id,
        name: input.name,
        type: input.type,
        capacity_kg: input.capacityKg,
        energy_label: input.energyLabel,
        water_usage_liters: input.waterUsageLiters,
        default_spin_rpm: input.defaultSpinRpm,
        is_primary: input.isPrimary,
      })
      .select(WASHER_COLUMNS)
      .single();

    if (error || !data) {
      throw new InternalServerErrorException('Unable to create washer');
    }

    return mapWasherRow(data as WasherRow);
  }

  async updateWasher(
    user: AuthenticatedUser,
    washerId: string,
    request: SaveWasherRequestDto,
  ): Promise<WasherResponseDto> {
    const input = validateWasherInput(request);
    const { data, error } = await this.supabaseService
      .getClient()
      .from('washers')
      .update({
        name: input.name,
        type: input.type,
        capacity_kg: input.capacityKg,
        energy_label: input.energyLabel,
        water_usage_liters: input.waterUsageLiters,
        default_spin_rpm: input.defaultSpinRpm,
        is_primary: input.isPrimary,
        updated_at: new Date().toISOString(),
      })
      .eq('id', washerId)
      .eq('user_id', user.id)
      .is('retired_at', null)
      .select(WASHER_COLUMNS)
      .maybeSingle();

    if (error) {
      throw new InternalServerErrorException('Unable to update washer');
    }

    if (!data) {
      throw new NotFoundException('Washer not found');
    }

    return mapWasherRow(data as WasherRow);
  }

  async deleteWasher(user: AuthenticatedUser, washerId: string): Promise<void> {
    const { data, error } = await this.supabaseService
      .getClient()
      .from('washers')
      .update({
        retired_at: new Date().toISOString(),
        is_primary: false,
        updated_at: new Date().toISOString(),
      })
      .eq('id', washerId)
      .eq('user_id', user.id)
      .is('retired_at', null)
      .select('id')
      .maybeSingle();

    if (error) {
      throw new InternalServerErrorException('Unable to retire washer');
    }

    if (!data) {
      throw new NotFoundException('Washer not found');
    }
  }
}
