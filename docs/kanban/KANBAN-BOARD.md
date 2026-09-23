# Tablero Kanban de TenderApp

Estado: tablero curado para revision academica.

## Proposito

Este tablero representa el flujo de trabajo del MVP sin convertir la tesis en una lista de tareas. Cada tarjeta pertenece a uno de los cinco incrementos de valor y debe avanzar por estados visibles antes de considerarse cerrada.

## Politicas del tablero

- Una tarjeta debe tener alcance y criterio de aceptacion.
- El trabajo en curso se limita a una tarjeta principal por frente.
- Implementado no equivale a validado.
- Una tarjeta no pasa a Cerrado sin evidencia o una limitacion explicitamente registrada.
- Los secretos, datos personales y configuraciones locales quedan fuera del tablero publico.

## Columnas

| Columna | Criterio de entrada | Criterio de salida |
|---|---|---|
| Pendiente | Necesidad identificada y priorizada. | Alcance aprobado y criterio de aceptacion definido. |
| En desarrollo | La implementacion esta activa. | Codigo o documento integrado y revisable. |
| En validacion | El cambio esta implementado. | Prueba, smoke test, build o comprobacion manual registrada. |
| Documentado | La validacion tiene resultado. | Evidencia, limite y referencia al incremento publicados. |
| Cerrado | La tarjeta cumple su alcance. | Revisión final dentro del MVP. |

## Tablero actual

### Pendiente

| ID | Incremento | Tarjeta | Criterio de aceptacion | Evidencia esperada |
|---|---|---|---|---|
| K-05 | 4 | Observar scheduler con evento vencido | Un evento programado vencido cambia a un estado final auditable. | Registro de ejecucion y `notification_events` redactado. |
| K-06 | 5 | Revisar RLS con dos usuarios controlados | Un usuario no puede leer ni modificar datos del otro. | Pruebas negativas o revisión documentada. |
| K-07 | 5 | Revisar persistencia de sesion | La sesion conserva comportamiento esperado y la limitacion queda documentada. | Prueba Android y nota de version de API. |

### En desarrollo

| ID | Incremento | Tarjeta | Criterio de aceptacion | Evidencia esperada |
|---|---|---|---|---|
| K-08 | 5 | Preparar demostracion final | El recorrido de defensa puede ejecutarse desde autenticacion hasta resultado. | Guion breve y checklist de demostracion. |

### En validacion

| ID | Incremento | Tarjeta | Criterio de aceptacion | Evidencia esperada |
|---|---|---|---|---|
| K-04 | 5 | Regresion del MVP | Las pruebas Android/backend y el build ejecutan correctamente en un entorno configurado. | Resultado QA y logs resumidos. |

### Documentado

| ID | Incremento | Tarjeta | Criterio de aceptacion | Evidencia esperada |
|---|---|---|---|---|
| K-03 | 4 | Notificaciones push inmediatas | El evento elegible se registra y la entrega inmediata queda observada. | Prueba, evento auditado y captura redactada. |
| K-02 | 3 | Prediccion y ventanas horarias | La salida incluye puntaje, veredicto, duracion orientativa, razones y ventana. | Pruebas deterministas y capturas v7/v8. |

### Cerrado

| ID | Incremento | Tarjeta | Resultado | Evidencia |
|---|---|---|---|---|
| K-01 | 1 | Base Android, backend y dominio | Proyecto integrado y compilable. | Codigo, arquitectura y build. |
| K-09 | 2 | Acceso y contexto domestico | Autenticacion, hogar, ubicaciones y lavarropas implementados. | Codigo, migraciones y capturas seleccionadas. |

## Lectura por incremento

| Incremento | Resultado que debe poder demostrarse |
|---|---|
| 1. Preparacion tecnica y tecnologica | Base Android/backend, dominio, persistencia inicial y criterios de calidad. |
| 2. Acceso y configuracion domestica | Sesion, hogar, ubicaciones de secado y lavarropas. |
| 3. Cargas y recomendacion meteorologica | Carga persistida, clima, prediccion, ventanas y estimaciones. |
| 4. Seguimiento y automatizacion | Estados, progreso, push, eventos auditados y scheduler. |
| 5. Integracion, validacion y cierre | Regresion, QA, evidencia final y limites declarados. |

## Evidencia relacionada

- `evidence/increment-log.md`
- `evidence/increment-synthesis.md`
- `docs/quality/QA-EXECUTION-PLAN.md`
- `evidence/qa-runs/`
- `evidence/smoke-tests/`
- `REVIEW-GUIDE.md`
