# SMOKE-2026-07-07-FR7-RENDER

Fecha: 2026-07-07.

Tipo: smoke test desplegado sobre Render.

Estado: exitoso para health checks públicos backend y Supabase.

## URL

```text
https://tesis-t85s.onrender.com
```

## Alcance ejecutado

- Verificación de endpoint público backend.
- Verificación de endpoint público Supabase backend-side.
- Verificación de documentación Swagger publicada.
- Actualización de URL release Android.
- Build Android debug y release.

## Resultados

Tabla 1
Resultados del smoke test desplegado en Render

| Control | Resultado |
|---|---|
| `GET https://tesis-t85s.onrender.com/api/v1/health` | HTTP 200, `{"status":"ok","version":"0.1.0"}` |
| `GET https://tesis-t85s.onrender.com/api/v1/docs` | HTTP 200 |
| `GET https://tesis-t85s.onrender.com/api/v1/health/supabase` | HTTP 200, `status: ok`, `message: null` |
| Android release backend URL | `https://tesis-t85s.onrender.com/api/v1/` |
| Android unit tests | OK |
| Android debug build | OK |
| Android release build | OK |

Nota. La tabla resume la verificación pública del backend desplegado y la configuración Android asociada. Fuente: elaboración propia.

## Interpretación

El backend público está desplegado y responde tanto el health check básico como la verificación backend-side contra Supabase. El despliegue manual posterior corrigió la divergencia inicial donde `/api/v1/health/supabase` devolvía 404.

## Acción recomendada

- Ejecutar la app Android contra la URL release.
- Validar desde teléfono físico o emulador.
- Capturar pantalla del dashboard y registrar el resultado visual.

## Datos no registrados

- No se registran claves Supabase.
- No se registran tokens de usuario.
- No se registra el valor completo de variables de entorno.

## Uso en la tesis

- Capítulo 4: evidencia de primer despliegue público backend y actualización Android hacia URL real.
- Capítulo 5: evidencia de brecha entre smoke local exitoso y despliegue remoto parcial.


