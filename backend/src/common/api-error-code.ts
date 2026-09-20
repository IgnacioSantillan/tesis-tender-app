import { HttpStatus } from '@nestjs/common';

export const API_ERROR_CODES = {
  BAD_REQUEST: 'BAD_REQUEST',
  UNAUTHORIZED: 'UNAUTHORIZED',
  FORBIDDEN: 'FORBIDDEN',
  NOT_FOUND: 'NOT_FOUND',
  CONFLICT: 'CONFLICT',
  INTERNAL_SERVER_ERROR: 'INTERNAL_SERVER_ERROR',
} as const;

export type ApiErrorCode = (typeof API_ERROR_CODES)[keyof typeof API_ERROR_CODES];

export function apiErrorCodeForStatus(status: number): ApiErrorCode {
  switch (status) {
    case HttpStatus.BAD_REQUEST:
      return API_ERROR_CODES.BAD_REQUEST;
    case HttpStatus.UNAUTHORIZED:
      return API_ERROR_CODES.UNAUTHORIZED;
    case HttpStatus.FORBIDDEN:
      return API_ERROR_CODES.FORBIDDEN;
    case HttpStatus.NOT_FOUND:
      return API_ERROR_CODES.NOT_FOUND;
    case HttpStatus.CONFLICT:
      return API_ERROR_CODES.CONFLICT;
    default:
      return API_ERROR_CODES.INTERNAL_SERVER_ERROR;
  }
}
