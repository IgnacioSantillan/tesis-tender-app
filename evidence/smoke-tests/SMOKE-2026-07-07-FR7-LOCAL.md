# SMOKE-2026-07-07-FR7-LOCAL

Fecha: 2026-07-07.

Tipo: smoke test local de integración FR7.

Estado: parcialmente exitoso; pendiente validación desde teléfono físico y backend desplegado.

## Objetivo

Registrar la primera verificación del camino Android, backend y Supabase sin exponer secretos.

## Alcance ejecutado

- Backend NestJS compilado localmente.
- Backend iniciado localmente con `backend/.env`.
- Health endpoint backend consultado localmente.
- Health endpoint Supabase backend-side consultado localmente.
- Android compilado en debug.
- Tests unitarios backend y Android ejecutados.

## Resultados

Tabla 1
Resultados del smoke test local FR7

| Control | Resultado |
|---|---|
| Backend build | OK |
| Backend tests | OK, 26 suites y 65 tests |
| Android unit tests | OK |
| Android debug build | OK |
| `GET http://127.0.0.1:3000/api/v1/health` | HTTP 200, `{"status":"ok","version":"0.1.0"}` |
| `GET http://127.0.0.1:3000/api/v1/health/supabase` | HTTP 200, `status: ok`, `message: null` |
| Direct Supabase REST check | HTTP 401; no usado como evidencia positiva porque el contrato aprobado pasa por backend |

Nota. La tabla registra controles locales ejecutados sin exponer secretos ni tokens. Fuente: elaboración propia.

## Datos no registrados

- No se registran claves Supabase.
- No se registran tokens de usuario.
- No se registra el valor completo de variables de entorno.

## Pendientes

- Configurar URL pública real de Render o Railway.
- Reemplazar el placeholder release Android `https://tenderapp-api.example.com/api/v1/`.
- Validar `/health` y `/health/supabase` desde teléfono físico.
- Ejecutar la app en teléfono o emulador y capturar estado visible del dashboard.
- Conectar token de sesión real para consultar `laundry-loads` protegidos.

## Uso en la tesis

- Capítulo 4: evidencia de integración local Android/backend/Supabase.
- Capítulo 5: evidencia para analizar límites de automatización, trazabilidad y validación incremental antes del despliegue público.


