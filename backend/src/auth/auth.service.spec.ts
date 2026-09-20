import { UnauthorizedException } from '@nestjs/common';
import { AuthService } from './auth.service';

describe('AuthService', () => {
  it('returns a normalized authenticated user for a valid Supabase token', async () => {
    const service = new AuthService({
      getClient: () => ({
        auth: {
          getUser: jest.fn().mockResolvedValue({
            data: {
              user: {
                id: 'user-1',
                email: 'user@example.com',
              },
            },
            error: null,
          }),
        },
      }),
    } as never);

    await expect(service.verifyAccessToken('valid-token')).resolves.toEqual({
      id: 'user-1',
      email: 'user@example.com',
    });
  });

  it('rejects invalid Supabase tokens', async () => {
    const service = new AuthService({
      getClient: () => ({
        auth: {
          getUser: jest.fn().mockResolvedValue({
            data: { user: null },
            error: new Error('invalid token'),
          }),
        },
      }),
    } as never);

    await expect(service.verifyAccessToken('invalid-token')).rejects.toBeInstanceOf(UnauthorizedException);
  });
});
