import { Body, Controller, Get, Param, Patch, Post, Req, UseGuards } from '@nestjs/common';
import { ApiBearerAuth, ApiOkResponse, ApiOperation, ApiTags } from '@nestjs/swagger';
import { AuthGuard } from '../auth/auth.guard';
import { AuthenticatedRequest } from '../auth/authenticated-request';
import { ApiProtectedErrorResponses } from '../common/api-error-responses.decorator';
import { CreateLaundryLoadRequestDto } from './dto/create-laundry-load-request.dto';
import { LaundryLoadResponseDto } from './dto/laundry-load-response.dto';
import { UpdateLaundryLoadStatusRequestDto } from './dto/update-laundry-load-status-request.dto';
import { LaundryLoadsService } from './laundry-loads.service';

@ApiTags('laundry-loads')
@ApiBearerAuth()
@ApiProtectedErrorResponses()
@UseGuards(AuthGuard)
@Controller('laundry-loads')
export class LaundryLoadsController {
  constructor(private readonly laundryLoadsService: LaundryLoadsService) {}

  @Get()
  @ApiOperation({ summary: 'List laundry loads owned by the authenticated user' })
  @ApiOkResponse({ type: LaundryLoadResponseDto, isArray: true })
  list(@Req() request: AuthenticatedRequest): Promise<LaundryLoadResponseDto[]> {
    return this.laundryLoadsService.listLaundryLoads(requireUser(request));
  }

  @Post()
  @ApiOperation({ summary: 'Create a laundry load for the authenticated user' })
  @ApiOkResponse({ type: LaundryLoadResponseDto })
  create(
    @Req() request: AuthenticatedRequest,
    @Body() body: CreateLaundryLoadRequestDto,
  ): Promise<LaundryLoadResponseDto> {
    return this.laundryLoadsService.createLaundryLoad(requireUser(request), body);
  }

  @Patch(':id/status')
  @ApiOperation({ summary: 'Update a laundry load status owned by the authenticated user' })
  @ApiOkResponse({ type: LaundryLoadResponseDto })
  updateStatus(
    @Req() request: AuthenticatedRequest,
    @Param('id') loadId: string,
    @Body() body: UpdateLaundryLoadStatusRequestDto,
  ): Promise<LaundryLoadResponseDto> {
    return this.laundryLoadsService.updateLaundryLoadStatus(requireUser(request), loadId, body);
  }
}

function requireUser(request: AuthenticatedRequest) {
  if (!request.user) {
    throw new Error('Authenticated user missing after guard execution');
  }

  return request.user;
}
