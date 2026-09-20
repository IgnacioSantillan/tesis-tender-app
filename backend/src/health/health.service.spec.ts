import { HealthService } from './health.service';

describe('HealthService', () => {
  const previousApiVersion = process.env.API_VERSION;
  const supabaseService = {
    getProjectHost: jest.fn().mockReturnValue('example.supabase.co'),
    verifyConnectivity: jest.fn(),
  };

  afterEach(() => {
    process.env.API_VERSION = previousApiVersion;
    jest.clearAllMocks();
  });

  it('returns ok status with the configured API version', () => {
    process.env.API_VERSION = 'test-version';
    const service = new HealthService(supabaseService as never);

    expect(service.getHealth()).toEqual({
      status: 'ok',
      version: 'test-version',
    });
  });

  it('returns ok Supabase status when connectivity succeeds', async () => {
    supabaseService.verifyConnectivity.mockResolvedValue(undefined);
    const service = new HealthService(supabaseService as never);

    await expect(service.getSupabaseHealth()).resolves.toMatchObject({
      status: 'ok',
      projectHost: 'example.supabase.co',
      message: null,
    });
    expect(supabaseService.verifyConnectivity).toHaveBeenCalled();
  });

  it('returns unavailable Supabase status without exposing error details', async () => {
    supabaseService.verifyConnectivity.mockRejectedValue(new Error('secret failure detail'));
    const service = new HealthService(supabaseService as never);

    await expect(service.getSupabaseHealth()).resolves.toMatchObject({
      status: 'unavailable',
      projectHost: 'example.supabase.co',
      message: 'Supabase connectivity check failed.',
    });
  });
});
