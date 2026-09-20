import { ArgumentsHost, Catch, ExceptionFilter, HttpException, HttpStatus } from '@nestjs/common';
import { randomUUID } from 'crypto';
import { apiErrorCodeForStatus } from './api-error-code';

interface HeaderMap {
  [key: string]: string | string[] | undefined;
}

interface ApiRequestLike {
  headers?: HeaderMap;
}

interface ApiResponseLike {
  status(statusCode: number): {
    json(body: unknown): void;
  };
}

@Catch()
export class ApiExceptionFilter implements ExceptionFilter {
  catch(exception: unknown, host: ArgumentsHost): void {
    const http = host.switchToHttp();
    const request = http.getRequest<ApiRequestLike>();
    const response = http.getResponse<ApiResponseLike>();
    const status = getHttpStatus(exception);

    response.status(status).json({
      code: apiErrorCodeForStatus(status),
      message: getSafeMessage(exception, status),
      traceId: getTraceId(request),
    });
  }
}

function getHttpStatus(exception: unknown): number {
  if (exception instanceof HttpException) {
    return exception.getStatus();
  }

  return HttpStatus.INTERNAL_SERVER_ERROR;
}

function getSafeMessage(exception: unknown, status: number): string {
  if (status >= HttpStatus.INTERNAL_SERVER_ERROR) {
    return 'Internal server error';
  }

  if (!(exception instanceof HttpException)) {
    return 'Unexpected error';
  }

  const response = exception.getResponse();

  if (typeof response === 'string') {
    return response;
  }

  if (isErrorResponseObject(response)) {
    const message = response.message;

    if (Array.isArray(message)) {
      return message.join('; ');
    }

    if (typeof message === 'string' && message.trim().length > 0) {
      return message;
    }
  }

  return exception.message;
}

function isErrorResponseObject(value: unknown): value is { message?: string | string[] } {
  return typeof value === 'object' && value !== null;
}

function getTraceId(request: ApiRequestLike): string {
  const header = request.headers?.['x-request-id'] ?? request.headers?.['x-correlation-id'];

  if (Array.isArray(header)) {
    return header[0] ?? randomUUID();
  }

  return header ?? randomUUID();
}
