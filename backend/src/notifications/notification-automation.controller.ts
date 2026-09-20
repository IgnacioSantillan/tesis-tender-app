import { Body, Controller, Post, UseGuards } from '@nestjs/common';
import { ApiBody, ApiOkResponse, ApiOperation, ApiSecurity, ApiTags } from '@nestjs/swagger';
import { QaKeyGuard } from '../qa/qa-key.guard';
import { DispatchDueNotificationsRequestDto } from './dto/dispatch-due-notifications-request.dto';
import { DispatchDueNotificationsResponseDto } from './dto/dispatch-due-notifications-response.dto';
import { NotificationsService } from './notifications.service';

@ApiTags('notifications')
@ApiSecurity('qa-key')
@UseGuards(QaKeyGuard)
@Controller('notifications')
export class NotificationAutomationController {
  constructor(private readonly notificationsService: NotificationsService) {}

  @Post('dispatch-due')
  @ApiOperation({ summary: 'Dispatch due pending notification events for automation or Render Cron smoke' })
  @ApiBody({ type: DispatchDueNotificationsRequestDto, required: false })
  @ApiOkResponse({ type: DispatchDueNotificationsResponseDto })
  dispatchDueNotifications(
    @Body() body: DispatchDueNotificationsRequestDto = {},
  ): Promise<DispatchDueNotificationsResponseDto> {
    return this.notificationsService.dispatchDueNotifications(body);
  }
}
