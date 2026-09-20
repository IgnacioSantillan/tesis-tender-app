import { getSupabaseConfig } from './supabase.config';

describe('getSupabaseConfig', () => {
  it('returns normalized Supabase config without exposing secrets', () => {
    const config = getSupabaseConfig({
      SUPABASE_URL: 'https://example.supabase.co/',
      SUPABASE_SERVICE_ROLE_KEY: 'sb_secret_test_key',
    });

    expect(config).toEqual({
      url: 'https://example.supabase.co',
      serviceRoleKey: 'sb_secret_test_key',
    });
  });

  it('rejects legacy JWT service role keys', () => {
    expect(() =>
      getSupabaseConfig({
        SUPABASE_URL: 'https://example.supabase.co',
        SUPABASE_SERVICE_ROLE_KEY: 'eyJlegacy.jwt.key',
      }),
    ).toThrow('SUPABASE_SERVICE_ROLE_KEY must use the rotated sb_secret_ format');
  });
});
