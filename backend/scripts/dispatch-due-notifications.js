require('dotenv').config();

const DEFAULT_BASE_URL = 'https://tesis-t85s.onrender.com';

function normalizeBaseUrl(value) {
  const baseUrl = (value || DEFAULT_BASE_URL).trim().replace(/\/+$/, '');
  if (!baseUrl.startsWith('http://') && !baseUrl.startsWith('https://')) {
    throw new Error('BACKEND_PUBLIC_URL must start with http:// or https://');
  }
  return baseUrl;
}

function parseLimit(value) {
  const parsed = Number.parseInt(value || '10', 10);
  if (!Number.isFinite(parsed) || parsed < 1 || parsed > 100) {
    throw new Error('CRON_DISPATCH_LIMIT must be a number between 1 and 100');
  }
  return parsed;
}

async function main() {
  const qaKey = (process.env.QA_DIAGNOSTICS_KEY || '').trim();
  if (!qaKey) {
    throw new Error('QA_DIAGNOSTICS_KEY is required for notification dispatch cron');
  }

  const baseUrl = normalizeBaseUrl(process.env.BACKEND_PUBLIC_URL);
  const limit = parseLimit(process.env.CRON_DISPATCH_LIMIT);
  const url = `${baseUrl}/api/v1/notifications/dispatch-due`;

  const response = await fetch(url, {
    method: 'POST',
    headers: {
      'content-type': 'application/json',
      'x-qa-key': qaKey,
    },
    body: JSON.stringify({ limit }),
  });

  const text = await response.text();
  let body = text;
  try {
    body = JSON.parse(text);
  } catch {
    // Keep the raw response body for non-JSON errors.
  }

  console.log(
    JSON.stringify(
      {
        job: 'dispatch-due-notifications',
        url,
        status: response.status,
        ok: response.ok,
        body,
      },
      null,
      2,
    ),
  );

  if (!response.ok) {
    process.exitCode = 1;
  }
}

main().catch((error) => {
  console.error(
    JSON.stringify(
      {
        job: 'dispatch-due-notifications',
        ok: false,
        error: error instanceof Error ? error.message : String(error),
      },
      null,
      2,
    ),
  );
  process.exitCode = 1;
});
