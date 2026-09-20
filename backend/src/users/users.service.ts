import { Injectable, InternalServerErrorException } from '@nestjs/common';
import { AuthenticatedUser } from '../auth/authenticated-user';
import { SupabaseService } from '../supabase/supabase.service';
import { SaveUserLocationRequestDto } from './dto/save-user-location-request.dto';
import { UserLocationResponseDto } from './dto/user-location-response.dto';
import { UserProfileResponseDto } from './dto/user-profile-response.dto';
import { validateUserLocationInput } from './users.validation';

interface ProfileRow {
  display_name: string | null;
}

interface UserLocationRow {
  id: string;
  label: string;
  latitude: number | string | null;
  longitude: number | string | null;
  is_primary: boolean | null;
  updated_at: string;
}

const USER_LOCATION_COLUMNS = 'id,label,latitude,longitude,is_primary,updated_at';

@Injectable()
export class UsersService {
  constructor(private readonly supabaseService: SupabaseService) {}

  async getCurrentProfile(user: AuthenticatedUser): Promise<UserProfileResponseDto> {
    const profile = await this.findProfile(user.id);
    const primaryLocation = await this.findPrimaryLocation(user.id);

    return {
      id: user.id,
      email: user.email,
      displayName: profile?.display_name ?? null,
      defaultLocationId: primaryLocation?.id ?? null,
    };
  }

  async saveHouseholdLocation(
    user: AuthenticatedUser,
    request: SaveUserLocationRequestDto,
  ): Promise<UserLocationResponseDto> {
    const input = validateUserLocationInput(request);
    const now = new Date().toISOString();

    const { error: resetError } = await this.supabaseService
      .getClient()
      .from('user_locations')
      .update({
        is_primary: false,
        updated_at: now,
      })
      .eq('user_id', user.id);

    if (resetError) {
      throw new InternalServerErrorException('Unable to save user location');
    }

    const { data, error } = await this.supabaseService
      .getClient()
      .from('user_locations')
      .upsert(
        {
          user_id: user.id,
          id: input.id,
          label: input.label,
          latitude: input.latitude,
          longitude: input.longitude,
          is_primary: true,
          updated_at: now,
        },
        { onConflict: 'user_id,id' },
      )
      .select(USER_LOCATION_COLUMNS)
      .single();

    if (error || !data) {
      throw new InternalServerErrorException('Unable to save user location');
    }

    return mapUserLocationRow(data as UserLocationRow);
  }

  private async findProfile(userId: string): Promise<ProfileRow | null> {
    const { data, error } = await this.supabaseService
      .getClient()
      .from('profiles')
      .select('display_name')
      .eq('id', userId)
      .maybeSingle();

    if (error) {
      throw new InternalServerErrorException('Unable to load user profile');
    }

    return data as ProfileRow | null;
  }

  private async findPrimaryLocation(userId: string): Promise<UserLocationRow | null> {
    const { data, error } = await this.supabaseService
      .getClient()
      .from('user_locations')
      .select(USER_LOCATION_COLUMNS)
      .eq('user_id', userId)
      .eq('is_primary', true)
      .maybeSingle();

    if (error) {
      throw new InternalServerErrorException('Unable to load user location');
    }

    return data as UserLocationRow | null;
  }
}

function mapUserLocationRow(row: UserLocationRow): UserLocationResponseDto {
  return {
    id: row.id,
    label: row.label,
    latitude: normalizeNumeric(row.latitude),
    longitude: normalizeNumeric(row.longitude),
    isPrimary: row.is_primary ?? false,
    updatedAt: row.updated_at,
  };
}

function normalizeNumeric(value: number | string | null): number | null {
  if (value === null) {
    return null;
  }

  return typeof value === 'number' ? value : Number(value);
}
