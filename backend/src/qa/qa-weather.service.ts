import { Injectable } from '@nestjs/common';
import { WeatherService } from '../weather/weather.service';
import { QaWeatherProviderStatusResponseDto } from './dto/qa-weather-provider-status-response.dto';

@Injectable()
export class QaWeatherService {
  constructor(private readonly weatherService: WeatherService) {}

  async getProviderStatus(locationId = 'home'): Promise<QaWeatherProviderStatusResponseDto> {
    const forecast = await this.weatherService.getForecast(locationId);
    const current = forecast[0];

    if (!current) {
      return {
        status: 'degraded',
        locationId,
        source: 'MOCK',
        isStale: true,
        checkedAt: new Date().toISOString(),
        forecastFor: new Date().toISOString(),
        temperatureCelsius: 0,
        humidityPercent: 0,
        windSpeedKph: 0,
        rainProbabilityPercent: 0,
        precipitationMillimeters: 0,
        cloudCoverPercent: 0,
        message: 'Weather service returned an empty forecast.',
      };
    }

    const isFallback = current.source === 'MOCK' || current.isStale;

    return {
      status: isFallback ? 'degraded' : 'ok',
      locationId: current.location.id || locationId,
      source: current.source,
      isStale: current.isStale,
      checkedAt: new Date().toISOString(),
      forecastFor: current.forecastFor,
      temperatureCelsius: current.temperatureCelsius,
      humidityPercent: current.humidityPercent,
      windSpeedKph: current.windSpeedKph,
      rainProbabilityPercent: current.rainProbabilityPercent,
      precipitationMillimeters: current.precipitationMillimeters,
      cloudCoverPercent: current.cloudCoverPercent,
      message: isFallback
        ? 'Weather service is using fallback data.'
        : 'Provider returned a real weather snapshot.',
    };
  }
}
