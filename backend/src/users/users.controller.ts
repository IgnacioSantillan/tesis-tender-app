import { Body, Controller, Get, Put, Req, UseGuards } from '@nestjs/common';
import { ApiBearerAuth, ApiOkResponse, ApiOperation, ApiTags } from '@nestjs/swagger';
import { AuthGuard } from '../auth/auth.guard';
import { AuthenticatedRequest } from '../auth/authenticated-request';
import { ApiProtectedErrorResponses } from '../common/api-error-responses.decorator';
import { SaveUserLocationRequestDto } from './dto/save-user-location-request.dto';
import { UserLocationResponseDto } from './dto/user-location-response.dto';
import { UserProfileResponseDto } from './dto/user-profile-response.dto';
import { UsersService } from './users.service';

@ApiTags('users')
@ApiProtectedErrorResponses()
@Controller()
export class UsersController {
  constructor(private readonly usersService: UsersService) {}

  @Get('me')
  @UseGuards(AuthGuard)
  @ApiBearerAuth()
  @ApiOperation({ summary: 'Return the authenticated user profile' })
  @ApiOkResponse({ type: UserProfileResponseDto })
  me(@Req() request: AuthenticatedRequest): Promise<UserProfileResponseDto> {
    return this.usersService.getCurrentProfile(requireUser(request));
  }

  @Put('users/me/location')
  @UseGuards(AuthGuard)
  @ApiBearerAuth()
  @ApiOperation({ summary: 'Persist the authenticated user household/weather location' })
  @ApiOkResponse({ type: UserLocationResponseDto })
  saveHouseholdLocation(
    @Req() request: AuthenticatedRequest,
    @Body() body: SaveUserLocationRequestDto,
  ): Promise<UserLocationResponseDto> {
    return this.usersService.saveHouseholdLocation(requireUser(request), body);
  }
}

function requireUser(request: AuthenticatedRequest) {
  if (!request.user) {
    throw new Error('Authenticated user missing after guard execution');
  }

  return request.user;
}
