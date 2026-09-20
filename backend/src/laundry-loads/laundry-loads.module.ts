import { Module } from '@nestjs/common';
import { AuthModule } from '../auth/auth.module';
import { NotificationsModule } from '../notifications/notifications.module';
import { PredictionsModule } from '../predictions/predictions.module';
import { SupabaseModule } from '../supabase/supabase.module';
import { LaundryLoadsController } from './laundry-loads.controller';
import { LaundryLoadsService } from './laundry-loads.service';
import { LaundryLoadsSupabaseDataSource } from './laundry-loads.supabase-data-source';

@Module({
  imports: [AuthModule, SupabaseModule, NotificationsModule, PredictionsModule],
  controllers: [LaundryLoadsController],
  providers: [LaundryLoadsService, LaundryLoadsSupabaseDataSource],
})
export class LaundryLoadsModule {}
