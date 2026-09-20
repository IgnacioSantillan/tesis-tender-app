import { ApiProperty } from '@nestjs/swagger';
import { LAUNDRY_LOAD_STATUSES, LaundryLoadStatus } from './laundry-load-types';

export class UpdateLaundryLoadStatusRequestDto {
  @ApiProperty({ enum: LAUNDRY_LOAD_STATUSES, example: 'DRYING' })
  status!: LaundryLoadStatus;
}
