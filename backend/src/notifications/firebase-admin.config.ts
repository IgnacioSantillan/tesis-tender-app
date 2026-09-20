export type PushProviderName = 'disabled' | 'fcm';

export type FirebaseCredentialSource =
  | {
      type: 'application-default';
      credentialsPath: string;
    }
  | {
      type: 'service-account-json';
      projectId: string;
      clientEmail: string;
      privateKey: string;
    };

export interface FirebaseAdminConfig {
  provider: PushProviderName;
  projectId?: string;
  credentialSource?: FirebaseCredentialSource;
}

export function getFirebaseAdminConfig(env: NodeJS.ProcessEnv = process.env): FirebaseAdminConfig {
  const provider = parsePushProvider(env.PUSH_PROVIDER);

  if (provider === 'disabled') {
    return { provider };
  }

  const projectId = parseRequiredValue(env.FIREBASE_PROJECT_ID, 'FIREBASE_PROJECT_ID');
  const credentialSource = parseCredentialSource(env, projectId);

  return {
    provider,
    projectId,
    credentialSource,
  };
}

function parsePushProvider(value: string | undefined): PushProviderName {
  const normalized = value?.trim().toLowerCase();

  if (!normalized) {
    return 'disabled';
  }

  if (normalized === 'disabled' || normalized === 'fcm') {
    return normalized;
  }

  throw new Error('PUSH_PROVIDER must be either disabled or fcm');
}

function parseCredentialSource(env: NodeJS.ProcessEnv, projectId: string): FirebaseCredentialSource {
  const credentialsPath = env.GOOGLE_APPLICATION_CREDENTIALS?.trim();

  if (credentialsPath) {
    return {
      type: 'application-default',
      credentialsPath,
    };
  }

  const rawServiceAccount = env.FIREBASE_SERVICE_ACCOUNT_JSON?.trim();

  if (!rawServiceAccount) {
    throw new Error('FCM push requires GOOGLE_APPLICATION_CREDENTIALS or FIREBASE_SERVICE_ACCOUNT_JSON');
  }

  return parseServiceAccountJson(rawServiceAccount, projectId);
}

function parseServiceAccountJson(value: string, expectedProjectId: string): FirebaseCredentialSource {
  let parsed: unknown;

  try {
    parsed = JSON.parse(value);
  } catch {
    throw new Error('FIREBASE_SERVICE_ACCOUNT_JSON must be valid JSON');
  }

  if (!isServiceAccountRecord(parsed)) {
    throw new Error('FIREBASE_SERVICE_ACCOUNT_JSON must include project_id, client_email and private_key');
  }

  if (parsed.project_id !== expectedProjectId) {
    throw new Error('FIREBASE_SERVICE_ACCOUNT_JSON project_id must match FIREBASE_PROJECT_ID');
  }

  return {
    type: 'service-account-json',
    projectId: parsed.project_id,
    clientEmail: parsed.client_email,
    privateKey: parsed.private_key,
  };
}

function isServiceAccountRecord(value: unknown): value is {
  project_id: string;
  client_email: string;
  private_key: string;
} {
  if (!value || typeof value !== 'object') {
    return false;
  }

  const record = value as Record<string, unknown>;

  return (
    typeof record.project_id === 'string' &&
    record.project_id.trim().length > 0 &&
    typeof record.client_email === 'string' &&
    record.client_email.trim().length > 0 &&
    typeof record.private_key === 'string' &&
    record.private_key.trim().length > 0
  );
}

function parseRequiredValue(value: string | undefined, name: string): string {
  const parsed = value?.trim();

  if (!parsed) {
    throw new Error(`${name} is required`);
  }

  return parsed;
}
