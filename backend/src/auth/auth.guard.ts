import { CanActivate, ExecutionContext, Injectable, UnauthorizedException } from '@nestjs/common';
import { AuthService } from './auth.service';
import { AuthenticatedRequest } from './authenticated-request';

@Injectable()
export class AuthGuard implements CanActivate {
  constructor(private readonly authService: AuthService) {}

  async canActivate(context: ExecutionContext): Promise<boolean> {
    const request = context.switchToHttp().getRequest<AuthenticatedRequest>();
    const token = extractBearerToken(request.headers.authorization);

    request.user = await this.authService.verifyAccessToken(token);

    return true;
  }
}

export function extractBearerToken(authorizationHeader: string | string[] | undefined): string {
  const value = Array.isArray(authorizationHeader) ? authorizationHeader[0] : authorizationHeader;

  if (!value) {
    throw new UnauthorizedException('Missing Authorization header');
  }

  const [scheme, token, extra] = value.trim().split(/\s+/);

  if (scheme !== 'Bearer' || !token || extra) {
    throw new UnauthorizedException('Authorization header must use Bearer token format');
  }

  return token;
}
