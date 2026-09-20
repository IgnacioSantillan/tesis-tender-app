import { Injectable, ServiceUnavailableException } from '@nestjs/common';
import { App, applicationDefault, cert, getApps, initializeApp } from 'firebase-admin/app';
import { Message, getMessaging } from 'firebase-admin/messaging';
import { FirebaseAdminConfig, getFirebaseAdminConfig } from './firebase-admin.config';

export interface PushMessageRequest {
  token: string;
  title: string;
  body: string;
  data?: Record<string, string>;
}

@Injectable()
export class FirebaseAdminMessagingGateway {
  private readonly config: FirebaseAdminConfig;
  private app: App | null = null;

  constructor() {
    this.config = getFirebaseAdminConfig();
  }

  async sendToToken(request: PushMessageRequest): Promise<string> {
    if (this.config.provider !== 'fcm') {
      throw new ServiceUnavailableException('FCM push provider is disabled');
    }

    const message: Message = {
      token: request.token,
      notification: {
        title: request.title,
        body: request.body,
      },
      data: request.data,
      android: {
        priority: 'high',
      },
    };

    return getMessaging(this.getApp()).send(message);
  }

  private getApp(): App {
    if (this.app) {
      return this.app;
    }

    const existingApp = getApps()[0];
    if (existingApp) {
      this.app = existingApp;
      return this.app;
    }

    if (this.config.provider !== 'fcm' || !this.config.projectId || !this.config.credentialSource) {
      throw new ServiceUnavailableException('FCM push provider is not configured');
    }

    const credential =
      this.config.credentialSource.type === 'application-default'
        ? applicationDefault()
        : cert({
            projectId: this.config.credentialSource.projectId,
            clientEmail: this.config.credentialSource.clientEmail,
            privateKey: this.config.credentialSource.privateKey,
          });

    this.app = initializeApp({
      credential,
      projectId: this.config.projectId,
    });

    return this.app;
  }
}
