import { Injectable } from '@nestjs/common';
import { createClient, SupabaseClient } from '@supabase/supabase-js';
import { getSupabaseConfig, SupabaseConfig } from './supabase.config';

@Injectable()
export class SupabaseService {
  private readonly config: SupabaseConfig;
  private readonly client: SupabaseClient;

  constructor() {
    this.config = getSupabaseConfig();
    this.client = createClient(this.config.url, this.config.serviceRoleKey, {
      auth: {
        autoRefreshToken: false,
        detectSessionInUrl: false,
        persistSession: false,
      },
    });
  }

  getClient(): SupabaseClient {
    return this.client;
  }

  getProjectUrl(): string {
    return this.config.url;
  }

  getProjectHost(): string {
    return new URL(this.config.url).host;
  }

  async verifyConnectivity(): Promise<void> {
    const { error } = await this.client.auth.admin.listUsers({ page: 1, perPage: 1 });

    if (error) {
      throw error;
    }
  }
}
