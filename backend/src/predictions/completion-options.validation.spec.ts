import { BadRequestException } from '@nestjs/common';
import { validateCompletionOptionsInput } from './completion-options.validation';

describe('validateCompletionOptionsInput', () => {
  const serverNow = new Date('2026-07-25T23:00:00.000Z');

  it('normalizes valid zoned instants', () => {
    const result = validateCompletionOptionsInput(
      validRequest({
        plannedStartAt: '2026-07-25T20:00:00-03:00',
        targetReadyAt: '2026-07-26T01:00:00-03:00',
      }),
      serverNow,
    );

    expect(result.plannedStartAt.toISOString()).toBe('2026-07-25T23:00:00.000Z');
    expect(result.targetReadyAt.toISOString()).toBe('2026-07-26T04:00:00.000Z');
  });

  it('defaults plannedStartAt to server time', () => {
    const result = validateCompletionOptionsInput(
      validRequest({
        plannedStartAt: undefined,
        targetReadyAt: '2026-07-26T01:00:00.000Z',
      }),
      serverNow,
    );

    expect(result.plannedStartAt.toISOString()).toBe(serverNow.toISOString());
  });

  it('rejects timestamps without Z or an explicit offset', () => {
    expect(() =>
      validateCompletionOptionsInput(
        validRequest({
          plannedStartAt: '2026-07-25T20:00:00',
        }),
        serverNow,
      ),
    ).toThrow(BadRequestException);

    expect(() =>
      validateCompletionOptionsInput(
        validRequest({
          targetReadyAt: '2026-07-26T01:00:00',
        }),
        serverNow,
      ),
    ).toThrow(BadRequestException);
  });

  it('rejects invalid calendar dates and UTC offsets', () => {
    expect(() =>
      validateCompletionOptionsInput(
        validRequest({
          targetReadyAt: '2026-02-30T21:00:00.000Z',
        }),
        serverNow,
      ),
    ).toThrow(BadRequestException);

    expect(() =>
      validateCompletionOptionsInput(
        validRequest({
          targetReadyAt: '2026-07-26T01:00:00+15:00',
        }),
        serverNow,
      ),
    ).toThrow(BadRequestException);
  });

  it('rejects targets equal to or before the planned start', () => {
    expect(() =>
      validateCompletionOptionsInput(
        validRequest({
          plannedStartAt: '2026-07-25T23:00:00.000Z',
          targetReadyAt: '2026-07-25T23:00:00.000Z',
        }),
        serverNow,
      ),
    ).toThrow('targetReadyAt must be after plannedStartAt');
  });
});

function validRequest(overrides: Record<string, unknown> = {}) {
  return {
    laundryLoadId: 'load-1',
    clothingType: 'MIXED' as const,
    dryingMethod: 'OUTDOOR' as const,
    locationId: 'home',
    dryingLocationId: 'PATIO' as const,
    plannedStartAt: '2026-07-25T23:00:00.000Z',
    targetReadyAt: '2026-07-26T02:00:00.000Z',
    ...overrides,
  };
}
