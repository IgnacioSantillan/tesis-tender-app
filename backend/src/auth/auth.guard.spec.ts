import { ExecutionContext, UnauthorizedException } from '@nestjs/common';
import { AuthenticatedRequest } from './authenticated-request';
import { AuthGuard, extractBearerToken } from './auth.guard';

describe('extractBearerToken', () => {
  it('extracts a bearer token', () => {
    expect(extractBearerToken('Bearer abc123')).toBe('abc123');
  });

  it('rejects missing headers', () => {
    expect(() => extractBearerToken(undefined)).toThrow(UnauthorizedException);
  });

  it('rejects malformed headers', () => {
    expect(() => extractBearerToken('Basic abc123')).toThrow(UnauthorizedException);
  });
});

describe('AuthGuard', () => {
  it('attaches authenticated user to the request', async () => {
    const request: Partial<AuthenticatedRequest> = {
      headers: {
        authorization: 'Bearer valid-token',
      },
    };
    const authService = {
      verifyAccessToken: jest.fn().mockResolvedValue({
        id: 'user-1',
        email: 'user@example.com',
      }),
    };
    const guard = new AuthGuard(authService as never);

    await expect(guard.canActivate(createHttpContext(request))).resolves.toBe(true);
    expect(request.user).toEqual({
      id: 'user-1',
      email: 'user@example.com',
    });
  });
});

function createHttpContext(request: Partial<AuthenticatedRequest>): ExecutionContext {
  return {
    switchToHttp: () => ({
      getRequest: () => request,
    }),
  } as ExecutionContext;
}
