import { BadRequestException, Inject, Injectable, Logger } from '@nestjs/common';
import { AuthenticatedUser } from '../auth/authenticated-user';
import { SupabaseService } from '../supabase/supabase.service';
import { WeatherSnapshotResponseDto } from './dto/weather-snapshot-response.dto';
import { ResolvedWeatherLocation, resolveWeatherLocation } from './weather-location-registry';
import { WEATHER_PROVIDER, WeatherProvider } from './weather-provider';

interface UserLocationRow {
  id: string;
  label: string;
  latitude: number | string | null;
  longitude: number | string | null;
}

interface WeatherCacheEntry {
  expiresAt: number;
  forecast: WeatherSnapshotResponseDto[];
}

@Injectable()
export class WeatherService {
  private readonly logger = new Logger(WeatherService.name);
  private readonly forecastCache = new Map<string, WeatherCacheEntry>();

  constructor(
    @Inject(WEATHER_PROVIDER) private readonly weatherProvider: WeatherProvider,
    private readonly supabaseService: SupabaseService,
  ) {}

  async getCurrentWeather(
    locationId: string | undefined,
    user?: AuthenticatedUser,
  ): Promise<WeatherSnapshotResponseDto> {
    const location = await this.resolveLocation(validateLocationId(locationId), user);
    const forecast = await this.getForecastForResolvedLocation(location);
    return forecast[0] ?? createFallbackSnapshot(location, 0);
  }

  async getForecast(
    locationId: string | undefined,
    user?: AuthenticatedUser,
  ): Promise<WeatherSnapshotResponseDto[]> {
    const location = await this.resolveLocation(validateLocationId(locationId), user);
    return this.getForecastForResolvedLocation(location);
  }

  private async getForecastForResolvedLocation(
    location: ResolvedWeatherLocation,
  ): Promise<WeatherSnapshotResponseDto[]> {
    const cacheKey = createWeatherCacheKey(location);
    const cachedForecast = this.readForecastCache(cacheKey);
    if (cachedForecast) {
      return cachedForecast;
    }

    try {
      const forecast = await this.weatherProvider.getForecast(location);
      if (forecast.length === 0) {
        throw new Error('Weather provider returned an empty forecast');
      }
      this.writeForecastCache(cacheKey, forecast);
      return forecast;
    } catch (error) {
      this.logger.warn(`Weather provider failed for forecast at ${location.id}: ${formatError(error)}`);
      const fallback = [
        createFallbackSnapshot(location, 0),
        createFallbackSnapshot(location, 2),
        createFallbackSnapshot(location, 4),
      ];
      this.writeForecastCache(cacheKey, fallback, fallbackCacheTtlForError(error));
      return fallback;
    }
  }

  private readForecastCache(cacheKey: string): WeatherSnapshotResponseDto[] | null {
    const entry = this.forecastCache.get(cacheKey);
    if (!entry) {
      return null;
    }

    if (entry.expiresAt <= Date.now()) {
      this.forecastCache.delete(cacheKey);
      return null;
    }

    return entry.forecast;
  }

  private writeForecastCache(
    cacheKey: string,
    forecast: WeatherSnapshotResponseDto[],
    ttlMilliseconds = WEATHER_CACHE_TTL_MILLISECONDS,
  ): void {
    if (forecast.length === 0) {
      return;
    }

    this.forecastCache.set(cacheKey, {
      expiresAt: Date.now() + ttlMilliseconds,
      forecast,
    });
  }

  private async resolveLocation(locationId: string, user: AuthenticatedUser | undefined): Promise<ResolvedWeatherLocation> {
    if (!user) {
      return resolveWeatherLocation(locationId);
    }

    const userLocation = await this.findUserLocation(user.id, locationId);
    return userLocation ?? resolveWeatherLocation(locationId);
  }

  private async findUserLocation(userId: string, locationId: string): Promise<ResolvedWeatherLocation | null> {
    const query = this.supabaseService
      .getClient()
      .from('user_locations')
      .select('id,label,latitude,longitude')
      .eq('user_id', userId);

    const { data, error } =
      locationId.trim().toLowerCase() === 'home'
        ? await query.eq('is_primary', true).maybeSingle()
        : await query.eq('id', locationId).maybeSingle();

    if (error) {
      this.logger.warn(`Unable to resolve user weather location for ${userId}/${locationId}: ${formatError(error)}`);
      return null;
    }

    if (!data) {
      return null;
    }

    return mapUserLocationRow(data as UserLocationRow);
  }
}

const WEATHER_CACHE_TTL_MILLISECONDS = 10 * 60 * 1000;
const WEATHER_FALLBACK_CACHE_TTL_MILLISECONDS = 2 * 60 * 1000;
const WEATHER_RATE_LIMIT_FALLBACK_CACHE_TTL_MILLISECONDS = 30 * 60 * 1000;

function createWeatherCacheKey(location: ResolvedWeatherLocation): string {
  return [
    location.id.trim().toLowerCase(),
    location.latitude.toFixed(4),
    location.longitude.toFixed(4),
  ].join('|');
}

function fallbackCacheTtlForError(error: unknown): number {
  return isRateLimitError(error)
    ? WEATHER_RATE_LIMIT_FALLBACK_CACHE_TTL_MILLISECONDS
    : WEATHER_FALLBACK_CACHE_TTL_MILLISECONDS;
}

function isRateLimitError(error: unknown): boolean {
  return formatError(error).includes('status 429');
}

function mapUserLocationRow(row: UserLocationRow): ResolvedWeatherLocation | null {
  const latitude = normalizeCoordinate(row.latitude);
  const longitude = normalizeCoordinate(row.longitude);

  if (latitude === null || longitude === null) {
    return null;
  }

  return {
    id: row.id,
    label: row.label,
    latitude,
    longitude,
  };
}

function normalizeCoordinate(value: number | string | null): number | null {
  if (value === null) {
    return null;
  }

  const normalized = typeof value === 'number' ? value : Number(value);
  return Number.isFinite(normalized) ? normalized : null;
}

function validateLocationId(value: string | undefined): string {
  if (!value || value.trim().length === 0) {
    throw new BadRequestException('locationId is required');
  }

  return value.trim();
}

function createFallbackSnapshot(location: ResolvedWeatherLocation, hourOffset: number): WeatherSnapshotResponseDto {
  const capturedAt = new Date().toISOString();
  const forecastFor = new Date(Date.now() + hourOffset * 60 * 60 * 1000).toISOString();

  return {
    location,
    source: 'MOCK',
    capturedAt,
    forecastFor,
    condition: hourOffset >= 4 ? 'PARTLY_CLOUDY' : 'CLEAR',
    temperatureCelsius: 22 - hourOffset * 0.5,
    humidityPercent: 58 + hourOffset,
    windSpeedKph: 12,
    rainProbabilityPercent: hourOffset >= 4 ? 25 : 10,
    precipitationMillimeters: hourOffset >= 4 ? 0.4 : 0,
    cloudCoverPercent: hourOffset >= 4 ? 45 : 25,
    forecastLeadMinutes: hourOffset * 60,
    isStale: true,
  };
}

function formatError(error: unknown): string {
  if (error instanceof Error) {
    return error.message;
  }

  try {
    return JSON.stringify(error);
  } catch {
    return String(error);
  }
}
