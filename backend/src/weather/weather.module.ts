import { Module } from '@nestjs/common';
import { AuthModule } from '../auth/auth.module';
import { SupabaseModule } from '../supabase/supabase.module';
import { FailoverWeatherProvider } from './failover-weather.provider';
import { MetNoWeatherProvider } from './met-no-weather.provider';
import { MockWeatherProvider } from './mock-weather.provider';
import { OpenMeteoWeatherProvider } from './open-meteo-weather.provider';
import { WeatherController } from './weather.controller';
import { WEATHER_PROVIDER } from './weather-provider';
import { getWeatherProviderConfig } from './weather-provider.config';
import { WeatherService } from './weather.service';

@Module({
  imports: [AuthModule, SupabaseModule],
  controllers: [WeatherController],
  providers: [
    WeatherService,
    FailoverWeatherProvider,
    MetNoWeatherProvider,
    MockWeatherProvider,
    OpenMeteoWeatherProvider,
    {
      provide: WEATHER_PROVIDER,
      useFactory: (
        mockProvider: MockWeatherProvider,
        openMeteoProvider: OpenMeteoWeatherProvider,
        metNoProvider: MetNoWeatherProvider,
        failoverProvider: FailoverWeatherProvider,
      ) => {
        const config = getWeatherProviderConfig();

        if (config.provider === 'open-meteo') {
          return failoverProvider;
        }

        if (config.provider === 'met-no') {
          return metNoProvider;
        }

        return mockProvider;
      },
      inject: [MockWeatherProvider, OpenMeteoWeatherProvider, MetNoWeatherProvider, FailoverWeatherProvider],
    },
  ],
  exports: [WeatherService],
})
export class WeatherModule {}
