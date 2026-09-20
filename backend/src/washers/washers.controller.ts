import { Body, Controller, Delete, Get, Param, Post, Put, Req, UseGuards } from '@nestjs/common';
import { ApiBearerAuth, ApiNoContentResponse, ApiOkResponse, ApiOperation, ApiTags } from '@nestjs/swagger';
import { AuthGuard } from '../auth/auth.guard';
import { AuthenticatedRequest } from '../auth/authenticated-request';
import { ApiProtectedErrorResponses } from '../common/api-error-responses.decorator';
import { SaveWasherRequestDto } from './dto/save-washer-request.dto';
import { WasherResponseDto } from './dto/washer-response.dto';
import { WashersService } from './washers.service';

@ApiTags('washers')
@ApiBearerAuth()
@ApiProtectedErrorResponses()
@UseGuards(AuthGuard)
@Controller('washers')
export class WashersController {
  constructor(private readonly washersService: WashersService) {}

  @Get()
  @ApiOperation({ summary: 'List washers owned by the authenticated user' })
  @ApiOkResponse({ type: WasherResponseDto, isArray: true })
  list(@Req() request: AuthenticatedRequest): Promise<WasherResponseDto[]> {
    return this.washersService.listWashers(requireUser(request));
  }

  @Post()
  @ApiOperation({ summary: 'Create a washer owned by the authenticated user' })
  @ApiOkResponse({ type: WasherResponseDto })
  create(@Req() request: AuthenticatedRequest, @Body() body: SaveWasherRequestDto): Promise<WasherResponseDto> {
    return this.washersService.createWasher(requireUser(request), body);
  }

  @Put(':id')
  @ApiOperation({ summary: 'Update a washer owned by the authenticated user' })
  @ApiOkResponse({ type: WasherResponseDto })
  update(
    @Req() request: AuthenticatedRequest,
    @Param('id') washerId: string,
    @Body() body: SaveWasherRequestDto,
  ): Promise<WasherResponseDto> {
    return this.washersService.updateWasher(requireUser(request), washerId, body);
  }

  @Delete(':id')
  @ApiOperation({ summary: 'Retire a washer owned by the authenticated user without deleting historical loads' })
  @ApiNoContentResponse()
  async remove(@Req() request: AuthenticatedRequest, @Param('id') washerId: string): Promise<void> {
    await this.washersService.deleteWasher(requireUser(request), washerId);
  }
}

function requireUser(request: AuthenticatedRequest) {
  if (!request.user) {
    throw new Error('Authenticated user missing after guard execution');
  }

  return request.user;
}
