import { Injectable, UnauthorizedException } from '@nestjs/common';
import { SupabaseService } from '../supabase/supabase.service';
import { AuthenticatedUser } from './authenticated-user';

@Injectable()
export class AuthService {
  constructor(private readonly supabaseService: SupabaseService) {}

  async verifyAccessToken(accessToken: string): Promise<AuthenticatedUser> {
    const { data, error } = await this.supabaseService.getClient().auth.getUser(accessToken);

    if (error || !data.user) {
      throw new UnauthorizedException('Invalid or expired access token');
    }

    return {
      id: data.user.id,
      email: data.user.email ?? null,
    };
  }
}
