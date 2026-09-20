import { getFirebaseAdminConfig } from './firebase-admin.config';

describe('getFirebaseAdminConfig', () => {
  it('defaults to disabled push provider', () => {
    expect(getFirebaseAdminConfig({})).toEqual({
      provider: 'disabled',
    });
  });

  it('accepts Firebase application default credentials path', () => {
    expect(
      getFirebaseAdminConfig({
        PUSH_PROVIDER: 'fcm',
        FIREBASE_PROJECT_ID: 'tenderapp-demo',
        GOOGLE_APPLICATION_CREDENTIALS: '/etc/secrets/firebase-service-account.json',
      }),
    ).toEqual({
      provider: 'fcm',
      projectId: 'tenderapp-demo',
      credentialSource: {
        type: 'application-default',
        credentialsPath: '/etc/secrets/firebase-service-account.json',
      },
    });
  });

  it('accepts service account JSON when project id matches', () => {
    const config = getFirebaseAdminConfig({
      PUSH_PROVIDER: 'fcm',
      FIREBASE_PROJECT_ID: 'tenderapp-demo',
      FIREBASE_SERVICE_ACCOUNT_JSON: JSON.stringify({
        project_id: 'tenderapp-demo',
        client_email: 'firebase-adminsdk-test@tenderapp-demo.iam.gserviceaccount.com',
        private_key: 'placeholder-private-key',
      }),
    });

    expect(config.provider).toBe('fcm');
    expect(config.projectId).toBe('tenderapp-demo');
    expect(config.credentialSource).toMatchObject({
      type: 'service-account-json',
      projectId: 'tenderapp-demo',
      clientEmail: 'firebase-adminsdk-test@tenderapp-demo.iam.gserviceaccount.com',
    });
  });

  it('rejects unknown push providers', () => {
    expect(() => getFirebaseAdminConfig({ PUSH_PROVIDER: 'other' })).toThrow(
      'PUSH_PROVIDER must be either disabled or fcm',
    );
  });

  it('requires Firebase project id when FCM is enabled', () => {
    expect(() =>
      getFirebaseAdminConfig({
        PUSH_PROVIDER: 'fcm',
        GOOGLE_APPLICATION_CREDENTIALS: '/etc/secrets/firebase-service-account.json',
      }),
    ).toThrow('FIREBASE_PROJECT_ID is required');
  });

  it('requires a credential source when FCM is enabled', () => {
    expect(() =>
      getFirebaseAdminConfig({
        PUSH_PROVIDER: 'fcm',
        FIREBASE_PROJECT_ID: 'tenderapp-demo',
      }),
    ).toThrow('FCM push requires GOOGLE_APPLICATION_CREDENTIALS or FIREBASE_SERVICE_ACCOUNT_JSON');
  });

  it('rejects service account JSON for a different project', () => {
    expect(() =>
      getFirebaseAdminConfig({
        PUSH_PROVIDER: 'fcm',
        FIREBASE_PROJECT_ID: 'tenderapp-demo',
        FIREBASE_SERVICE_ACCOUNT_JSON: JSON.stringify({
          project_id: 'other-project',
          client_email: 'firebase-adminsdk-test@other-project.iam.gserviceaccount.com',
          private_key: 'placeholder',
        }),
      }),
    ).toThrow('FIREBASE_SERVICE_ACCOUNT_JSON project_id must match FIREBASE_PROJECT_ID');
  });
});
