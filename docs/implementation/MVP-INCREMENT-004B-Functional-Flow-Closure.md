---
id: MVP-INCREMENT-004B
title: Cierre funcional de flujo principal
category: Implementation Planning
version: 1.5.0
status: Validated
language: Spanish
author: Juan Santillan
created: 2026-07-12
updated: 2026-07-13
related:
  - MVP-INCREMENT-004
  - MVP-INCREMENTS-001
  - BACKLOG-001
  - PB-021
  - PB-022
  - ANDROID-LOCATION-002
  - ANDROID-LOAD-UPDATE-001
  - ANDROID-WEATHER-SOURCE-001
  - BACKEND-USER-LOCATION-001
---

# MVP-INCREMENT-004B - Cierre funcional de flujo principal

## Proposito

Consolidar el MVP funcional avanzado de TenderApp cerrando el circuito principal de uso entre configuracion del hogar, cargas de lavado, clima realista, prediccion, actualizacion de estado, notificaciones e historial.

Este incremento no agrega un bloque grande de producto nuevo. Ordena la brecha detectada despues de validar Android, backend, Supabase, Render, Open-Meteo y Firebase Cloud Messaging.

## Lectura del estado actual

TenderApp ya cuenta con una base funcional considerable:

- Aplicacion Android con Kotlin y Jetpack Compose.
- Autenticacion real con Supabase Auth.
- Persistencia de sesion y deep link de verificacion.
- Backend NestJS desplegado en Render.
- Integracion con Supabase.
- Gestion de lavarropas.
- Creacion de cargas de lavado.
- Historial conectado.
- Dashboard conectado al backend.
- Integracion meteorologica con Open-Meteo.
- Modelo de ubicacion del hogar y ubicaciones de secado.
- Prediccion climatica backend basada en clima y ubicacion.
- Push real con Firebase Cloud Messaging.
- Eventos auditables en Supabase.
- UI refinada respecto del mockup inicial.
- Preferencias de idioma, tema, ubicacion y notificaciones.
- Evidencia incremental para tesis.

La aplicacion puede considerarse un MVP funcional avanzado, aunque todavia requiere cierres de consistencia, QA y robustez antes de tratarla como producto real para usuarios externos.

## Estimacion operativa

| Nivel evaluado | Estimacion actual | Interpretacion |
|---|---:|---|
| MVP funcional | 90%-92% | El flujo principal esta demostrado y el cierre funcional fue validado manualmente. |
| Demo solida de tesis | 88%-92% | Es defendible con evidencia incremental; resta seleccionar capturas finales y redactar datos sensibles. |
| Producto real/publicable | 55%-70% | La base existe, pero falta robustez, UX final, QA sistematico y hardening operativo. |

## Brecha principal

El cierre pendiente no se explica por falta de pantallas, sino por integracion y consistencia del ciclo:

```text
Configurar hogar
-> crear o seleccionar lavarropas
-> crear carga
-> elegir ubicacion de secado
-> obtener clima real
-> obtener prediccion
-> ver proxima accion
-> actualizar estado de carga
-> generar/cancelar notificaciones
-> recibir push, alerta o mensaje de ropa lista
-> guardar ropa
-> ver historial actualizado
```

## Distincion de ubicaciones

El incremento mantiene separada la ubicacion meteorologica de la ubicacion de secado:

```text
homeLocation / weatherLocation:
ubicacion meteorologica del hogar o zona del usuario.

dryingLocationId:
lugar fisico donde se seca la ropa dentro o alrededor del hogar.
Ejemplos: PATIO, BALCONY, INDOOR, OUTDOOR_LINE, LAUNDRY_ROOM.
```

Esta distincion es relevante para la task `ANDROID-LOCATION-002`: la accion "usar ubicacion actual" debe sincronizar la ubicacion del hogar que alimenta clima/prediccion, no reemplazar el lugar de secado elegido para cada carga.

## Alcance del cierre

- Sincronizar efectivamente la ubicacion del hogar entre Settings, backend, Supabase y consumidores Android.
- Permitir actualizacion de estado de carga desde UI.
- Hacer mas visible y accionable la prediccion.
- Hacer que Nueva carga tambien comunique una decision dinamica antes de crear la carga.
- Registrar la responsabilidad de ciclo de vida de datos: descartar cargas y retirar lavarropas sin romper historial.
- Validar el ciclo de notificaciones automaticas segun cambios de estado, incluyendo el evento de ropa lista/guardar ropa.
- Registrar evidencia final de smoke real.
- Mantener errores y fallbacks entendibles para el usuario.

## Tareas propuestas

| Task ID | Prioridad | Item | Capa | Criterio de aceptacion |
|---|---:|---|---|---|
| BACKEND-USER-LOCATION-001 | P0 | Persistir ubicacion del hogar del usuario via backend | Backend/Supabase | Implementado: endpoint protegido actualiza `user_locations` y `updated_at` en Supabase sin exponer detalles de tabla al cliente. |
| BACKEND-WEATHER-LOCATION-001 | P0 | Usar ubicacion persistida para clima y prediccion | Backend | Implementado: `home` o un id de ubicacion del usuario pueden resolver coordenadas reales antes de consultar clima/prediccion. |
| ANDROID-LOCATION-002 | P0 | Sincronizacion efectiva de ubicacion del hogar | Android Data/UI | Implementado: Settings sincroniza `home` con backend antes de persistir localmente; verificacion manual de Supabase pendiente. |
| ANDROID-LOAD-UPDATE-001 | P0 | Actualizacion de estado de carga desde UI | Android Data/UI | Implementado: una carga puede avanzar `PLANNED -> WASHING -> DRYING -> COMPLETED` desde Dashboard/History y refrescar datos backend. |
| ANDROID-WEATHER-SOURCE-001 | P0 | Visibilidad de fuente meteorologica | Android Quality/UX | Implementado: Dashboard indica si los datos vienen de `Open-Meteo`, `Mock backend` o `Fallback local`. |
| ANDROID-PRED-002 | P1 | Prediccion accionable en Dashboard o Nueva carga | Android UI/Data | Implementado: Dashboard muestra proxima accion derivada de estado de carga, veredicto y estimacion. |
| ANDROID-NEWLOAD-PRED-001 | P0 | Tarjeta dinamica de decision en Nueva carga | Android UI/Data | Implementado: la tarjeta de Nueva carga cambia segun ropa, programa y ubicacion antes de crear la carga. |
| ANDROID-NEWLOAD-PRED-002 | P0 | Alinear decision de Nueva carga con clima actual | Android UI/Data | Implementado: la tarjeta usa clima actual cuando esta disponible y explicita fallback/fuente para no contradecir Dashboard. |
| ANDROID-NEWLOAD-PRED-003 | P0 | Usar prediccion backend como fuente principal en Nueva carga | Android UI/Data | Implementado: Nueva carga consulta `predictions/drying` para la seleccion actual y solo usa calculo local como fallback. |
| ANDROID-DATA-LIFECYCLE-001A | P1 | Descartar cargas activas sin borrar historial | Android Data/UI | Implementado: Dashboard e Historial permiten marcar una carga activa como `CANCELLED`. |
| ANDROID-DATA-LIFECYCLE-001B | P1 | Baja logica de lavarropas con historial preservado | Android/Backend/Data | Implementado: el backend retira el lavarropas con `retired_at`, Android lo quita de la UI activa y el autor valido manualmente su desaparicion. |
| QA-INCREMENT-004B | P0 | Smoke real de prediccion, ubicacion, update y evento de ropa lista | Quality/Evidence | Validado manualmente: historial posterior al retiro de lavarropas, cierre de carga/guardar ropa y Dashboard actualizado por estado confirmados por el autor. |

## Reglas de alcance

- No agregar mapa avanzado embebido en este incremento.
- El handoff a mapa externo puede seguir siendo visual; la seleccion real de coordenadas se trata como una task futura.
- No introducir machine learning ni IA generativa dentro de la app.
- Android debe seguir consumiendo backend para datos protegidos.
- Supabase service-role y credenciales Firebase Admin siguen siendo backend-only.
- La evidencia de tesis debe distinguir implementacion, validacion manual y limitaciones conocidas.

## Resultado esperado

Con este bloque validado, TenderApp queda aproximadamente en:

| Nivel evaluado | Estimacion posterior |
|---|---:|
| MVP funcional | 90%-92% |
| Demo de tesis | 88%-92% |
| Producto real/publicable | 65%-75% |

## Uso para la tesis

Este incremento aporta evidencia para explicar que el trabajo asistido por IA no solo genero pantallas o codigo aislado, sino que permitio cerrar un flujo funcional auditable: ubicacion real, carga, prediccion, actualizacion de estado, notificacion e historial.

## Changelog

| Version | Date | Change |
|---|---|---|
| 1.5.0 | 2026-07-13 | Marked Increment 4B as validated after author confirmed history preservation after washer retirement and load closure/guardar ropa flow. |
| 1.4.0 | 2026-07-13 | Updated data lifecycle and QA closure status after washer retirement implementation and manual disappearance validation. |
| 1.3.0 | 2026-07-12 | Added implemented laundry-load discard as the first data lifecycle slice and deferred washer retirement to a backend-safe task. |
| 1.2.0 | 2026-07-12 | Added backend prediction as New Load source of truth after QA detected score and duration drift. |
| 1.1.0 | 2026-07-12 | Added New Load current-weather alignment after QA found mismatch with Dashboard score. |
| 1.0.0 | 2026-07-12 | Added New Load dynamic decision card and data lifecycle responsibility to Increment 4B scope. |
| 0.9.0 | 2026-07-12 | Added ready-clothes / guardar ropa event to Increment 4B validation scope. |
| 0.8.0 | 2026-07-12 | Added QA-INCREMENT-004B evidence package as the next closure step. |
| 0.7.0 | 2026-07-12 | Marked ANDROID-PRED-002 as implemented with actionable Dashboard prediction. |
| 0.6.0 | 2026-07-12 | Added ANDROID-WEATHER-SOURCE-001 after manual QA detected ambiguous mock/fallback weather values in Dashboard. |
| 0.5.0 | 2026-07-12 | Marked ANDROID-LOAD-UPDATE-001 as implemented with Android status advancement from Dashboard and History. |
| 0.4.0 | 2026-07-12 | Marked ANDROID-LOCATION-002 as implemented with Settings remote synchronization. |
| 0.3.0 | 2026-07-12 | Added BACKEND-WEATHER-LOCATION-001 as implemented dependency for effective household location sync. |
| 0.2.0 | 2026-07-12 | Marked BACKEND-USER-LOCATION-001 as implemented and kept ANDROID-LOCATION-002 as next integration step. |
| 0.1.0 | 2026-07-12 | Initial definition based on current system state and MVP closure gap analysis. |
