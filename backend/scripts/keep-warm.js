require('dotenv').config();

const DEFAULT_BASE_URL = 'https://tesis-t85s.onrender.com';
const DEFAULT_PATH = '/api/v1/health';

function normalizeBaseUrl(value) {
  const baseUrl = (value || DEFAULT_BASE_URL).trim().replace(/\/+$/, '');
  if (!baseUrl.startsWith('http://') && !baseUrl.startsWith('https://')) {
    throw new Error('BACKEND_PUBLIC_URL must start with http:// or https://');
  }
  return baseUrl;
}

function normalizePath(value) {
  const path = (value || DEFAULT_PATH).trim();
  return path.startsWith('/') ? path : `/${path}`;
}

async function main() {
  const baseUrl = normalizeBaseUrl(process.env.BACKEND_PUBLIC_URL);
  const path = normalizePath(process.env.KEEP_WARM_PATH);
  const url = `${baseUrl}${path}`;

  const response = await fetch(url, { method: 'GET' });
  const text = await response.text();

  console.log(
    JSON.stringify(
      {
        job: 'keep-warm',
        url,
        status: response.status,
        ok: response.ok,
        bodyPreview: text.slice(0, 500),
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
        job: 'keep-warm',
        ok: false,
        error: error instanceof Error ? error.message : String(error),
      },
      null,
      2,
    ),
  );
  process.exitCode = 1;
});
