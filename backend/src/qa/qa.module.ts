import { Module } from '@nestjs/common';
import { SupabaseModule } from '../supabase/supabase.module';
import { WeatherModule } from '../weather/weather.module';
import { QaKeyGuard } from './qa-key.guard';
import { QaSupabaseController } from './qa-supabase.controller';
import { QaSupabaseService } from './qa-supabase.service';
import { QaWeatherController } from './qa-weather.controller';
import { QaWeatherService } from './qa-weather.service';

@Module({
  imports: [SupabaseModule, WeatherModule],
  controllers: [QaSupabaseController, QaWeatherController],
  providers: [QaKeyGuard, QaSupabaseService, QaWeatherService],
})
export class QaModule {}
