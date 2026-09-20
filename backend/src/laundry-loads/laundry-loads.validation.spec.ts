import { BadRequestException } from '@nestjs/common';
import { validateCreateLaundryLoadInput, validateLaundryLoadStatus } from './laundry-loads.validation';

describe('laundry load validation', () => {
  it('normalizes a valid create request', () => {
    expect(
      validateCreateLaundryLoadInput({
        washerId: ' washer-1 ',
        clothingType: 'MIXED',
        washingProgram: 'NORMAL',
        locationId: ' home ',
        dryingLocationId: ' balcon ' as never,
        spinRpm: 1200,
        loadSize: 'MEDIUM',
      }),
    ).toEqual({
      washerId: 'washer-1',
      clothingType: 'MIXED',
      washingProgram: 'NORMAL',
      locationId: 'home',
      dryingLocationId: 'BALCONY',
      spinRpm: 1200,
      loadSize: 'MEDIUM',
    });
  });

  it('defaults missing drying location to patio', () => {
    expect(
      validateCreateLaundryLoadInput({
        clothingType: 'MIXED',
        washingProgram: 'NORMAL',
        locationId: 'home',
      }).dryingLocationId,
    ).toBe('PATIO');
  });

  it('rejects unsupported clothing types', () => {
    expect(() =>
      validateCreateLaundryLoadInput({
        clothingType: 'WOOL' as never,
        washingProgram: 'NORMAL',
        locationId: 'home',
      }),
    ).toThrow(BadRequestException);
  });

  it('rejects unsupported statuses', () => {
    expect(() => validateLaundryLoadStatus('LOST')).toThrow(BadRequestException);
  });

  it('rejects unsupported drying locations', () => {
    expect(() =>
      validateCreateLaundryLoadInput({
        clothingType: 'MIXED',
        washingProgram: 'NORMAL',
        locationId: 'home',
        dryingLocationId: 'ROOF' as never,
      }),
    ).toThrow(BadRequestException);
  });

  it('rejects unsupported spin speeds and load sizes', () => {
    expect(() =>
      validateCreateLaundryLoadInput({
        clothingType: 'MIXED',
        washingProgram: 'NORMAL',
        locationId: 'home',
        spinRpm: 750 as never,
      }),
    ).toThrow(BadRequestException);

    expect(() =>
      validateCreateLaundryLoadInput({
        clothingType: 'MIXED',
        washingProgram: 'NORMAL',
        locationId: 'home',
        loadSize: 'HUGE' as never,
      }),
    ).toThrow(BadRequestException);
  });
});
