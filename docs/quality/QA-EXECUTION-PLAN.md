# Plan de QA y cierre del MVP

Estado: paquete de QA listo para ejecucion y revision.

## Proposito

Este documento convierte los criterios de aceptacion en un plan practico de verificacion para los cinco incrementos. No crea una matriz de trazabilidad independiente: el tablero Kanban, los criterios QA y la evidencia son suficientes para revisar el estado del MVP.

## Tipos de control

| Tipo | Uso | Resultado esperado |
|---|---|---|
| Unitario | Reglas, calculadoras, validaciones y mapeos. | Suite aprobada o fallo localizado. |
| Integracion | Android, backend, Supabase, clima y notificaciones. | Contrato y frontera comprobados. |
| Smoke HTTP | Health, OpenAPI y rutas protegidas. | Codigo HTTP y respuesta registrada. |
| Manual Android | Autenticacion, navegacion, formularios y estados. | Recorrido observado en dispositivo o emulador. |
| Visual | Dashboard, Nueva carga, Historial y Ajustes. | Captura redactada y criterio visual revisado. |
| Seguridad | Secretos, propiedad de datos y permisos. | Ausencia de exposicion y limitaciones documentadas. |

## Checklist por incremento

### Incremento 1. Preparacion tecnica y tecnologica

- [x] Proyecto Android compila con el entorno configurado.
- [x] Backend TypeScript compila.
- [x] La navegacion base existe.
- [x] El dominio inicial esta separado de la interfaz.
- [x] No se publican secretos ni configuraciones locales.

### Incremento 2. Acceso y configuracion del entorno domestico

- [x] Registro e inicio de sesion estan implementados.
- [x] La sesion se comunica con las rutas protegidas.
- [x] Hogar y ubicacion de secado se pueden representar.
- [x] Lavarropas del usuario se gestionan sin borrar cargas historicas.
- [ ] La persistencia de sesion debe revisarse frente a APIs deprecated.

### Incremento 3. Gestion de cargas y recomendacion meteorologica

- [x] Se puede crear una carga con sus datos de dominio.
- [x] El historial muestra cargas y estados.
- [x] El clima llega mediante el backend.
- [x] La prediccion devuelve puntaje, veredicto, duracion orientativa y razones.
- [x] La lluvia exterior puede forzar un resultado desfavorable.
- [x] Las ventanas horarias y la hora objetivo tienen pruebas.
- [x] La estimacion energetica se presenta como orientativa.

### Incremento 4. Seguimiento y automatizacion

- [x] Los estados de carga se actualizan y conservan historial.
- [x] El registro FCM y el opt-in estan representados.
- [x] Las notificaciones inmediatas tienen evidencia.
- [x] Los eventos de notificacion se auditan.
- [ ] Falta observar un evento vencido procesado por el scheduler.

### Incremento 5. Integracion, validacion y cierre

- [x] Las pruebas Android estan registradas.
- [x] Las pruebas backend estan registradas.
- [x] El smoke test del backend y Supabase esta registrado.
- [x] Las capturas finales estan redactadas y seleccionadas.
- [x] La documentacion publica no contiene bloques internos de asistencia tecnica.
- [ ] Revisar RLS con pruebas negativas entre dos usuarios controlados.
- [ ] Revisar el guion final de demostracion.

## Criterios de cierre QA

El MVP puede presentarse como revisable cuando:

1. Las pruebas automatizadas disponibles pasan en un entorno configurado.
2. Las capacidades principales tienen evidencia automatizada, manual o visual.
3. Los pendientes no se presentan como validaciones cerradas.
4. Los secretos y datos personales no aparecen en el repositorio ni en las capturas.
5. La prediccion se describe como heuristica orientativa y no como precision fisica.
6. El tablero Kanban refleja los pendientes reales.

## Resultado actual

El paquete QA esta preparado para la defensa del MVP. Permanecen como pendientes de cierre operativo la observacion diferida del scheduler, la revisión negativa de RLS, la revisión de persistencia de sesion y la validacion final del guion de demostracion.

## Evidencia

- `docs/quality/QA-002-Acceptance-Criteria-Matrix.md`
- `docs/quality/QA-003-Implementation-Review-Gate.md`
- `docs/quality/QA-005-MVP-Deployment-Review-Gate.md`
- `evidence/qa-runs/`
- `evidence/smoke-tests/`
