import { Module } from '@nestjs/common';
import { AppService } from './app.service';
import { AuthModule } from './auth/auth.module';
import { HealthModule } from './health/health.module';
import { LaundryLoadsModule } from './laundry-loads/laundry-loads.module';
import { NotificationsModule } from './notifications/notifications.module';
import { PredictionsModule } from './predictions/predictions.module';
import { QaModule } from './qa/qa.module';
import { SupabaseModule } from './supabase/supabase.module';
import { UsersModule } from './users/users.module';
import { WashersModule } from './washers/washers.module';
import { WeatherModule } from './weather/weather.module';

@Module({
  imports: [
    HealthModule,
    SupabaseModule,
    AuthModule,
    UsersModule,
    WashersModule,
    LaundryLoadsModule,
    WeatherModule,
    PredictionsModule,
    NotificationsModule,
    QaModule,
  ],
  controllers: [],
  providers: [AppService],
})
export class AppModule {}
