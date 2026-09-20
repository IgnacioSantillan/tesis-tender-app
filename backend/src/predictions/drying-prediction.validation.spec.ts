import { BadRequestException } from '@nestjs/common';
import { validateDryingPredictionInput } from './drying-prediction.validation';

describe('validateDryingPredictionInput', () => {
  it('normalizes a valid request', () => {
    expect(
      validateDryingPredictionInput({
        laundryLoadId: ' load-1 ',
        clothingType: 'MIXED',
        washingProgram: 'NORMAL',
        dryingMethod: 'OUTDOOR',
        locationId: ' home ',
        dryingLocationId: ' lavadero ' as never,
        spinRpm: 1200,
        loadSize: 'SMALL',
        washerEnergyLabel: ' a+ ' as never,
        washerCapacityKg: 7,
        waterUsageLiters: 45,
      }),
    ).toEqual({
      laundryLoadId: 'load-1',
      clothingType: 'MIXED',
      washingProgram: 'NORMAL',
      dryingMethod: 'OUTDOOR',
      locationId: 'home',
      dryingLocationId: 'LAUNDRY_ROOM',
      spinRpm: 1200,
      loadSize: 'SMALL',
      washerEnergyLabel: 'A+',
      washerCapacityKg: 7,
      waterUsageLiters: 45,
    });
  });

  it('defaults missing drying location to patio', () => {
    expect(
      validateDryingPredictionInput({
        laundryLoadId: 'load-1',
        clothingType: 'MIXED',
        washingProgram: 'NORMAL',
        dryingMethod: 'OUTDOOR',
        locationId: 'home',
      }).dryingLocationId,
    ).toBe('PATIO');
  });

  it('rejects unsupported clothing types', () => {
    expect(() =>
      validateDryingPredictionInput({
        laundryLoadId: 'load-1',
        clothingType: 'SOCKS' as never,
        washingProgram: 'NORMAL',
        dryingMethod: 'OUTDOOR',
        locationId: 'home',
      }),
    ).toThrow(BadRequestException);
  });

  it('rejects missing location ids', () => {
    expect(() =>
      validateDryingPredictionInput({
        laundryLoadId: 'load-1',
        clothingType: 'MIXED',
        washingProgram: 'NORMAL',
        dryingMethod: 'OUTDOOR',
        locationId: '',
      }),
    ).toThrow(BadRequestException);
  });

  it('rejects unsupported drying locations', () => {
    expect(() =>
      validateDryingPredictionInput({
        laundryLoadId: 'load-1',
        clothingType: 'MIXED',
        washingProgram: 'NORMAL',
        dryingMethod: 'OUTDOOR',
        locationId: 'home',
        dryingLocationId: 'ROOF' as never,
      }),
    ).toThrow(BadRequestException);
  });

  it('rejects unsupported energy-aware prediction fields', () => {
    expect(() =>
      validateDryingPredictionInput({
        laundryLoadId: 'load-1',
        clothingType: 'MIXED',
        washingProgram: 'NORMAL',
        dryingMethod: 'OUTDOOR',
        locationId: 'home',
        spinRpm: 750 as never,
      }),
    ).toThrow(BadRequestException);

    expect(() =>
      validateDryingPredictionInput({
        laundryLoadId: 'load-1',
        clothingType: 'MIXED',
        washingProgram: 'NORMAL',
        dryingMethod: 'OUTDOOR',
        locationId: 'home',
        loadSize: 'HUGE' as never,
      }),
    ).toThrow(BadRequestException);

    expect(() =>
      validateDryingPredictionInput({
        laundryLoadId: 'load-1',
        clothingType: 'MIXED',
        washingProgram: 'NORMAL',
        dryingMethod: 'OUTDOOR',
        locationId: 'home',
        washerEnergyLabel: 'Z' as never,
      }),
    ).toThrow(BadRequestException);
  });
});
