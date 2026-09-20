import { SupabaseService } from './supabase.service';

describe('SupabaseService', () => {
  const previousUrl = process.env.SUPABASE_URL;
  const previousKey = process.env.SUPABASE_SERVICE_ROLE_KEY;

  afterEach(() => {
    process.env.SUPABASE_URL = previousUrl;
    process.env.SUPABASE_SERVICE_ROLE_KEY = previousKey;
  });

  it('creates a Supabase client from environment configuration', () => {
    process.env.SUPABASE_URL = 'https://example.supabase.co';
    process.env.SUPABASE_SERVICE_ROLE_KEY = 'sb_secret_test_key';

    const service = new SupabaseService();

    expect(service.getProjectUrl()).toBe('https://example.supabase.co');
    expect(service.getClient()).toBeDefined();
  });
});
