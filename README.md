# tesis-tender-app

Repositorio curado para compartir con la direccion y el tribunal la implementacion y la evidencia tecnica de TenderApp.

## Proposito

Este repositorio complementa la tesis. El objeto academico es TenderApp: una aplicacion movil para recomendar el secado de ropa a partir de condiciones meteorologicas. La documentacion de ingenieria, las bitacoras y las pruebas se incluyen como evidencia de trazabilidad, no como contribucion academica independiente.

## Estado

Repositorio curado para revision academica. El contenido se organiza mediante cinco incrementos Kanban y un paquete QA, sin secretos, datos personales ni artefactos de compilacion.

## Estructura prevista

- `app/`: aplicacion Android.
- `backend/`: API REST NestJS.
- `supabase/`: migraciones y documentacion del esquema.
- `docs/`: arquitectura, requisitos, decisiones y pruebas seleccionadas.
- `docs/kanban/KANBAN-BOARD.md`: tablero Kanban de los cinco incrementos.
- `docs/quality/QA-EXECUTION-PLAN.md`: checklist y pendientes de QA.
- `evidence/`: capturas y resultados redactados.
- `evidence/increment-log.md`: bitacora resumida por incremento.

## Reglas de revision

- No se incluyen tokens, claves, correos completos, coordenadas precisas ni archivos `.env`.
- No se incluyen carpetas `build/`, `node_modules/` ni logs locales.
- La evidencia debe indicar si fue automatizada, manual, visual o pendiente.
- Las afirmaciones de la tesis deben contrastarse con el codigo y la evidencia disponible.

## Ejecucion

Las instrucciones de configuracion y ejecucion se encuentran en `REVIEW-GUIDE.md` y en los README de cada componente.
