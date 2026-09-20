import { applyDecorators } from '@nestjs/common';
import {
  ApiBadRequestResponse,
  ApiInternalServerErrorResponse,
  ApiNotFoundResponse,
  ApiUnauthorizedResponse,
} from '@nestjs/swagger';
import { ApiErrorResponseDto } from './api-error-response.dto';

export function ApiProtectedErrorResponses(): ClassDecorator & MethodDecorator {
  return applyDecorators(
    ApiBadRequestResponse({ type: ApiErrorResponseDto }),
    ApiUnauthorizedResponse({ type: ApiErrorResponseDto }),
    ApiNotFoundResponse({ type: ApiErrorResponseDto }),
    ApiInternalServerErrorResponse({ type: ApiErrorResponseDto }),
  );
}

export function ApiPublicErrorResponses(): ClassDecorator & MethodDecorator {
  return applyDecorators(ApiInternalServerErrorResponse({ type: ApiErrorResponseDto }));
}
