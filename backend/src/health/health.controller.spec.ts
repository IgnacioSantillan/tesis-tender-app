import { HealthController } from './health.controller';

describe('HealthController', () => {
  it('returns backend health from the service', () => {
    const service = {
      getHealth: jest.fn().mockReturnValue({ status: 'ok', version: 'test' }),
      getSupabaseHealth: jest.fn(),
    };
    const controller = new HealthController(service as never);

    expect(controller.health()).toEqual({ status: 'ok', version: 'test' });
  });

  it('returns Supabase health from the service', async () => {
    const supabaseHealth = {
      status: 'ok',
      projectHost: 'example.supabase.co',
      checkedAt: '2026-07-07T15:00:00.000Z',
      message: null,
    };
    const service = {
      getHealth: jest.fn(),
      getSupabaseHealth: jest.fn().mockResolvedValue(supabaseHealth),
    };
    const controller = new HealthController(service as never);

    await expect(controller.supabase()).resolves.toBe(supabaseHealth);
  });
});
