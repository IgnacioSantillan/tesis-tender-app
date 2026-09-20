# Guia de revision para profesores

## Proposito

Este repositorio contiene una seleccion curada del codigo, la documentacion y la evidencia tecnica de TenderApp. Complementa la tesis y permite revisar la relacion entre problema, implementacion, pruebas y resultados.

## Recorrido recomendado

1. Leer `README.md` para conocer el alcance y las exclusiones.
2. Revisar `docs/architecture/` para la separacion Android, backend y persistencia.
3. Revisar `docs/domain/` para reglas y requisitos del caso de uso.
4. Consultar `app/` y `backend/` para la implementacion.
5. Consultar `supabase/migrations/` para la evolucion del esquema.
6. Consultar `evidence/increment-log.md` para la bitacora de tareas.
7. Consultar `evidence/qa-runs/` y `evidence/smoke-tests/` para las verificaciones.
8. Consultar `evidence/screenshots/v7/` y `evidence/screenshots/v8/` para el resultado visual seleccionado.

## Limitaciones declaradas

- La prediccion de secado es heuristica y orientativa.
- La consistencia de las reglas no demuestra precision fisica del tiempo de secado.
- Las notificaciones push inmediatas tienen evidencia; la observacion completa del scheduler puede permanecer pendiente.
- La configuracion local y las credenciales deben ser provistas por quien ejecute el proyecto.

## Seguridad

No se incluyen claves, tokens, credenciales Firebase Admin, correos completos, coordenadas precisas, `local.properties`, `google-services.json`, archivos `.env`, dependencias instaladas ni carpetas de compilacion.
