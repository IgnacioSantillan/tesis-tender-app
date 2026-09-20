import { CanActivate, ExecutionContext, Injectable, ServiceUnavailableException, UnauthorizedException } from '@nestjs/common';
import { timingSafeEqual } from 'crypto';

@Injectable()
export class QaKeyGuard implements CanActivate {
  canActivate(context: ExecutionContext): boolean {
    const expectedKey = process.env.QA_DIAGNOSTICS_KEY?.trim();

    if (!expectedKey) {
      throw new ServiceUnavailableException('QA diagnostics are disabled.');
    }

    const request = context.switchToHttp().getRequest<{ headers: Record<string, string | string[] | undefined> }>();
    const providedKey = normalizeHeader(request.headers['x-qa-key']);

    if (!providedKey || !constantTimeEquals(providedKey, expectedKey)) {
      throw new UnauthorizedException('Invalid QA diagnostics key.');
    }

    return true;
  }
}

function normalizeHeader(value: string | string[] | undefined): string | null {
  const raw = Array.isArray(value) ? value[0] : value;
  const normalized = raw?.trim();
  return normalized ? normalized : null;
}

function constantTimeEquals(left: string, right: string): boolean {
  const leftBuffer = Buffer.from(left);
  const rightBuffer = Buffer.from(right);

  if (leftBuffer.length !== rightBuffer.length) {
    return false;
  }

  return timingSafeEqual(leftBuffer, rightBuffer);
}
