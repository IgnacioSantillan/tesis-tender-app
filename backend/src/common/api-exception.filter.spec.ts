import { ArgumentsHost, BadRequestException, InternalServerErrorException, NotFoundException, UnauthorizedException } from '@nestjs/common';
import { ApiExceptionFilter } from './api-exception.filter';

describe('ApiExceptionFilter', () => {
  it('maps bad request exceptions to the API error envelope', () => {
    const { host, json, status } = createHost({ 'x-request-id': 'request-123' });
    const filter = new ApiExceptionFilter();

    filter.catch(new BadRequestException('locationId is required'), host);

    expect(status).toHaveBeenCalledWith(400);
    expect(json).toHaveBeenCalledWith({
      code: 'BAD_REQUEST',
      message: 'locationId is required',
      traceId: 'request-123',
    });
  });

  it('maps unauthorized exceptions without exposing framework response details', () => {
    const { host, json, status } = createHost({ 'x-correlation-id': 'corr-1' });
    const filter = new ApiExceptionFilter();

    filter.catch(new UnauthorizedException('Missing Authorization header'), host);

    expect(status).toHaveBeenCalledWith(401);
    expect(json).toHaveBeenCalledWith({
      code: 'UNAUTHORIZED',
      message: 'Missing Authorization header',
      traceId: 'corr-1',
    });
  });

  it('maps not found exceptions to the documented envelope shape', () => {
    const { host, json, status } = createHost({ 'x-request-id': 'request-404' });
    const filter = new ApiExceptionFilter();

    filter.catch(new NotFoundException('Washer not found'), host);

    expect(status).toHaveBeenCalledWith(404);
    expect(json).toHaveBeenCalledWith({
      code: 'NOT_FOUND',
      message: 'Washer not found',
      traceId: 'request-404',
    });
  });

  it('hides server exception messages from API clients', () => {
    const { host, json, status } = createHost({ 'x-request-id': 'request-500' });
    const filter = new ApiExceptionFilter();

    filter.catch(new InternalServerErrorException('Unable to load washers'), host);

    expect(status).toHaveBeenCalledWith(500);
    expect(json).toHaveBeenCalledWith({
      code: 'INTERNAL_SERVER_ERROR',
      message: 'Internal server error',
      traceId: 'request-500',
    });
  });

  it('generates a trace id when the request has no trace header', () => {
    const { host, json } = createHost({});
    const filter = new ApiExceptionFilter();

    filter.catch(new Error('unexpected'), host);

    const body = json.mock.calls[0]?.[0] as { traceId?: string };
    expect(body.traceId).toEqual(expect.any(String));
    expect(body.traceId?.length).toBeGreaterThan(0);
  });
});

function createHost(headers: Record<string, string | string[] | undefined>): {
  host: ArgumentsHost;
  json: jest.Mock;
  status: jest.Mock;
} {
  const json = jest.fn();
  const status = jest.fn().mockReturnValue({ json });
  const host = {
    switchToHttp: () => ({
      getRequest: () => ({ headers }),
      getResponse: () => ({ status }),
    }),
  } as ArgumentsHost;

  return { host, json, status };
}
