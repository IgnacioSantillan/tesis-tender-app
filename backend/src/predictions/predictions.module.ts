import { Module } from '@nestjs/common';
import { AuthModule } from '../auth/auth.module';
import { WeatherModule } from '../weather/weather.module';
import { SupabaseModule } from '../supabase/supabase.module';
import { PredictionEnergyMetadataDataSource } from './prediction-energy-metadata.data-source';
import { PredictionsController } from './predictions.controller';
import { PredictionsService } from './predictions.service';

@Module({
  imports: [AuthModule, WeatherModule, SupabaseModule],
  controllers: [PredictionsController],
  providers: [PredictionsService, PredictionEnergyMetadataDataSource],
  exports: [PredictionsService],
})
export class PredictionsModule {}
