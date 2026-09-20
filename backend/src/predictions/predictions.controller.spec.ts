import { PredictionsController } from './predictions.controller';

describe('PredictionsController', () => {
  it('delegates drying prediction requests to the service', async () => {
    const prediction = { verdict: 'GOOD' };
    const service = {
      calculateDryingPrediction: jest.fn().mockResolvedValue(prediction),
    };
    const controller = new PredictionsController(service as never);
    const request = {
      laundryLoadId: 'load-1',
      clothingType: 'MIXED' as const,
      washingProgram: 'NORMAL' as const,
      dryingMethod: 'OUTDOOR' as const,
      locationId: 'home',
    };

    await expect(
      controller.drying(
        {
          headers: {},
          user: { id: 'user-1', email: 'user@example.com' },
        },
        request,
      ),
    ).resolves.toBe(prediction);
    expect(service.calculateDryingPrediction).toHaveBeenCalledWith(request, {
      id: 'user-1',
      email: 'user@example.com',
    });
  });

  it('delegates completion option requests to the authenticated service boundary', async () => {
    const response = { recommendedPrograms: ['QUICK'] };
    const service = {
      calculateCompletionOptions: jest.fn().mockResolvedValue(response),
    };
    const controller = new PredictionsController(service as never);
    const body = {
      laundryLoadId: 'load-1',
      clothingType: 'MIXED' as const,
      dryingMethod: 'OUTDOOR' as const,
      locationId: 'home',
      targetReadyAt: '2026-07-26T01:00:00-03:00',
    };
    const user = { id: 'user-1', email: 'user@example.com' };

    await expect(
      controller.completionOptions({ headers: {}, user }, body),
    ).resolves.toBe(response);
    expect(service.calculateCompletionOptions).toHaveBeenCalledWith(body, user);
  });
});
