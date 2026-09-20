import { BadRequestException } from '@nestjs/common';
import { validateWasherInput } from './washers.validation';

describe('validateWasherInput', () => {
  it('normalizes a valid washer request', () => {
    expect(
      validateWasherInput({
        name: ' Main washer ',
        type: 'FRONT_LOAD',
        capacityKg: 7,
        energyLabel: ' a++ ' as never,
        waterUsageLiters: 45,
        defaultSpinRpm: 1200,
        isPrimary: true,
      }),
    ).toEqual({
      name: 'Main washer',
      type: 'FRONT_LOAD',
      capacityKg: 7,
      energyLabel: 'A++',
      waterUsageLiters: 45,
      defaultSpinRpm: 1200,
      isPrimary: true,
    });
  });

  it('rejects unsupported washer types', () => {
    expect(() =>
      validateWasherInput({
        name: 'Main washer',
        type: 'INVALID' as never,
      }),
    ).toThrow(BadRequestException);
  });

  it('normalizes washer type aliases from UI labels or legacy values', () => {
    expect(
      validateWasherInput({
        name: 'Main washer',
        type: 'Carga frontal' as never,
      }).type,
    ).toBe('FRONT_LOAD');

    expect(
      validateWasherInput({
        name: 'Main washer',
        type: 'front load' as never,
      }).type,
    ).toBe('FRONT_LOAD');

    expect(
      validateWasherInput({
        name: 'Main washer',
        type: 'Lavarropas secarropas' as never,
      }).type,
    ).toBe('WASHER_DRYER');
  });

  it('rejects unsupported energy labels', () => {
    expect(() =>
      validateWasherInput({
        name: 'Main washer',
        type: 'FRONT_LOAD',
        energyLabel: 'Z' as never,
      }),
    ).toThrow(BadRequestException);
  });

  it('rejects negative numeric metadata', () => {
    expect(() =>
      validateWasherInput({
        name: 'Main washer',
        type: 'TOP_LOAD',
        capacityKg: -1,
      }),
    ).toThrow(BadRequestException);
  });

  it('rejects unsupported default spin speeds', () => {
    expect(() =>
      validateWasherInput({
        name: 'Main washer',
        type: 'FRONT_LOAD',
        defaultSpinRpm: 750 as never,
      }),
    ).toThrow(BadRequestException);
  });
});
