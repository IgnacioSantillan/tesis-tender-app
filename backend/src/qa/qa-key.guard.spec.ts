import { ServiceUnavailableException, UnauthorizedException } from '@nestjs/common';
import { QaKeyGuard } from './qa-key.guard';

describe('QaKeyGuard', () => {
  const originalKey = process.env.QA_DIAGNOSTICS_KEY;

  afterEach(() => {
    if (originalKey === undefined) {
      delete process.env.QA_DIAGNOSTICS_KEY;
    } else {
      process.env.QA_DIAGNOSTICS_KEY = originalKey;
    }
  });

  it('rejects requests when diagnostics are disabled', () => {
    delete process.env.QA_DIAGNOSTICS_KEY;

    expect(() => new QaKeyGuard().canActivate(createContext())).toThrow(ServiceUnavailableException);
  });

  it('rejects requests with a missing or invalid key', () => {
    process.env.QA_DIAGNOSTICS_KEY = 'expected-key';

    expect(() => new QaKeyGuard().canActivate(createContext('wrong-key'))).toThrow(UnauthorizedException);
  });

  it('accepts requests with the configured key', () => {
    process.env.QA_DIAGNOSTICS_KEY = 'expected-key';

    expect(new QaKeyGuard().canActivate(createContext('expected-key'))).toBe(true);
  });
});

function createContext(key?: string): never {
  return {
    switchToHttp: () => ({
      getRequest: () => ({
        headers: key ? { 'x-qa-key': key } : {},
      }),
    }),
  } as never;
}
