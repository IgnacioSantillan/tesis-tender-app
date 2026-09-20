import { Body, Controller, Post, Req, UseGuards } from '@nestjs/common';
import { ApiBearerAuth, ApiOkResponse, ApiOperation, ApiTags } from '@nestjs/swagger';
import { AuthenticatedRequest } from '../auth/authenticated-request';
import { AuthGuard } from '../auth/auth.guard';
import { ApiProtectedErrorResponses } from '../common/api-error-responses.decorator';
import { DeviceRegistrationResponseDto } from './dto/device-registration-response.dto';
import { RegisterDeviceRequestDto } from './dto/register-device-request.dto';
import { TestPushRequestDto } from './dto/test-push-request.dto';
import { TestPushResponseDto } from './dto/test-push-response.dto';
import { NotificationsService } from './notifications.service';

@ApiTags('notifications')
@ApiBearerAuth()
@ApiProtectedErrorResponses()
@UseGuards(AuthGuard)
@Controller('notifications')
export class NotificationsController {
  constructor(private readonly notificationsService: NotificationsService) {}

  @Post('register-device')
  @ApiOperation({ summary: 'Register an Android push notification device token' })
  @ApiOkResponse({ type: DeviceRegistrationResponseDto })
  registerDevice(
    @Req() request: AuthenticatedRequest,
    @Body() body: RegisterDeviceRequestDto,
  ): Promise<DeviceRegistrationResponseDto> {
    if (!request.user) {
      throw new Error('Authenticated user missing after guard execution');
    }

    return this.notificationsService.registerDevice(request.user, body);
  }

  @Post('test-push')
  @ApiOperation({ summary: 'Send a protected development push notification to the latest active Android device' })
  @ApiOkResponse({ type: TestPushResponseDto })
  sendTestPush(
    @Req() request: AuthenticatedRequest,
    @Body() body: TestPushRequestDto,
  ): Promise<TestPushResponseDto> {
    if (!request.user) {
      throw new Error('Authenticated user missing after guard execution');
    }

    return this.notificationsService.sendTestPush(request.user, body);
  }
}
