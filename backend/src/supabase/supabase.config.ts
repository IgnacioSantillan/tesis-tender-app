export interface SupabaseConfig {
  url: string;
  serviceRoleKey: string;
}

export function getSupabaseConfig(env: NodeJS.ProcessEnv = process.env): SupabaseConfig {
  return {
    url: parseSupabaseUrl(env.SUPABASE_URL),
    serviceRoleKey: parseSupabaseSecretKey(env.SUPABASE_SERVICE_ROLE_KEY),
  };
}

function parseSupabaseUrl(value: string | undefined): string {
  const url = value?.trim();

  if (!url) {
    throw new Error('SUPABASE_URL is required');
  }

  try {
    const parsed = new URL(url);

    if (parsed.protocol !== 'https:' && parsed.protocol !== 'http:') {
      throw new Error('SUPABASE_URL must use http or https');
    }

    return parsed.toString().replace(/\/$/, '');
  } catch {
    throw new Error('SUPABASE_URL must be a valid URL');
  }
}

function parseSupabaseSecretKey(value: string | undefined): string {
  const key = value?.trim();

  if (!key) {
    throw new Error('SUPABASE_SERVICE_ROLE_KEY is required');
  }

  if (!key.startsWith('sb_secret_')) {
    throw new Error('SUPABASE_SERVICE_ROLE_KEY must use the rotated sb_secret_ format');
  }

  return key;
}
