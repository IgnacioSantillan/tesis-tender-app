export type NodeEnv = 'development' | 'test' | 'production';

export interface AppConfig {
  nodeEnv: NodeEnv;
  port: number;
  apiPrefix: string;
  apiVersion: string;
}

export function getAppConfig(env: NodeJS.ProcessEnv = process.env): AppConfig {
  return {
    nodeEnv: parseNodeEnv(env.NODE_ENV),
    port: parsePort(env.PORT),
    apiPrefix: parseApiPrefix(env.API_PREFIX),
    apiVersion: env.API_VERSION?.trim() || '0.1.0',
  };
}

function parseNodeEnv(value: string | undefined): NodeEnv {
  if (value === undefined || value.trim() === '') {
    return 'development';
  }

  if (value === 'development' || value === 'test' || value === 'production') {
    return value;
  }

  throw new Error(`Invalid NODE_ENV: ${value}`);
}

function parsePort(value: string | undefined): number {
  if (value === undefined || value.trim() === '') {
    return 3000;
  }

  const parsed = Number(value);
  if (!Number.isInteger(parsed) || parsed <= 0 || parsed > 65_535) {
    throw new Error(`Invalid PORT: ${value}`);
  }

  return parsed;
}

function parseApiPrefix(value: string | undefined): string {
  const prefix = value?.trim() || 'api/v1';

  if (prefix.startsWith('/') || prefix.endsWith('/')) {
    throw new Error('API_PREFIX must not start or end with a slash');
  }

  return prefix;
}
