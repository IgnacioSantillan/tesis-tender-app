import { Module } from '@nestjs/common';
import { AuthModule } from '../auth/auth.module';
import { QaKeyGuard } from '../qa/qa-key.guard';
import { SupabaseModule } from '../supabase/supabase.module';
import { NotificationAutomationController } from './notification-automation.controller';
import { NotificationsController } from './notifications.controller';
import { FirebaseAdminMessagingGateway } from './firebase-admin-messaging.gateway';
import { NotificationsService } from './notifications.service';
import { NotificationsSupabaseDataSource } from './notifications.supabase-data-source';

@Module({
  imports: [AuthModule, SupabaseModule],
  controllers: [NotificationsController, NotificationAutomationController],
  providers: [NotificationsService, NotificationsSupabaseDataSource, FirebaseAdminMessagingGateway, QaKeyGuard],
  exports: [NotificationsService],
})
export class NotificationsModule {}
