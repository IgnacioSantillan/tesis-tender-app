---
id: MVP-INCREMENT-004
title: Prediccion climatica, ubicaciones de secado y notificaciones automaticas
category: Implementation Planning
version: 1.1.0
status: Draft
language: Spanish
author: Juan Santillan
created: 2026-07-11
updated: 2026-07-11
related:
  - MVP-INCREMENTS-001
  - BACKLOG-001
  - PB-020
  - PB-021
  - BACKEND-WEATHER-001
  - ANDROID-WEATHER-001
  - ANDROID-LOCATION-001
  - BACKEND-PUSH-004
  - ANDROID-PUSH-005
---

# MVP-INCREMENT-004 - Prediccion climatica, ubicaciones de secado y notificaciones automaticas

## Proposito

Definir el proximo incremento funcional del MVP de TenderApp, orientado a consolidar el flujo principal entre dashboard, ubicacion de secado, datos meteorologicos, prediccion y notificaciones automaticas.

Este incremento toma como base lo ya implementado en Android, backend, Supabase, Open-Meteo, Render y Firebase Cloud Messaging, y lo organiza en un flujo de valor verificable para el usuario y para la tesis.

## Objetivo de valor

Permitir que TenderApp recomiende y automatice acciones asociadas al secado de ropa, utilizando informacion climatica realista, ubicacion de secado y notificaciones al usuario.

El usuario deberia poder:

- Registrar una carga de lavado.
- Asociarla a una ubicacion concreta de secado.
- Obtener una recomendacion basada en condiciones climaticas reales.
- Visualizar una estimacion aproximada de secado.
- Visualizar una ventana horaria recomendada y las condiciones previstas para las proximas horas.
- Recibir una alerta automatica vinculada al momento estimado de secado o a cambios desfavorables del clima.
- Comparar, antes de crear la carga, que programas permiten tener la ropa lavada y seca antes de una hora objetivo.

## Alcance principal

- Ajustar el dashboard para mostrar carga activa, clima, progreso y recomendacion.
- Integrar o refinar Open-Meteo como fuente realista de datos climaticos.
- Considerar temperatura, humedad, viento, probabilidad de precipitacion y ubicacion de secado.
- Incorporar ubicaciones de secado dentro del hogar: interior, balcon, tender exterior, patio o lavadero.
- Vincular cada carga de lavado con su ubicacion de secado.
- Revisar el ABM de lavarropas y su relacion con cargas de lavado.
- Calcular tiempo estimado de secado segun clima, tipo de ropa, programa de lavado y ubicacion.
- Exponer tramos horarios de pronostico para que el dashboard explique por que una ventana de tendido es mas conveniente que otra.
- Generar notificaciones automaticas para tender, retirar ropa o advertir riesgo climatico.
- Cancelar o actualizar notificaciones pendientes cuando cambie el estado de una carga.
- Validar el flujo completo end-to-end.
- Combinar duracion de lavado y prediccion de secado para recomendar solamente los programas temporalmente factibles.

## Componentes afectados

### Android

- Dashboard.
- Nueva carga.
- Historial.
- Settings.
- Repositorios remotos de clima, cargas y lavarropas.
- Estado de permisos y preferencias de notificacion.

### Backend

- Endpoints de weather.
- Endpoints de laundry-loads.
- Modulo de predictions.
- Modulo de notifications.
- Reglas de actualizacion/cancelacion de eventos pendientes.

### Supabase

- Tabla de cargas de lavado.
- Tabla de lavarropas.
- Tabla de ubicaciones o atributos de ubicacion de secado.
- Tabla de eventos de notificacion.
- Relaciones entre usuario, carga, ubicacion, prediccion y notificacion.

### Infraestructura

- Render como backend desplegado.
- Open-Meteo como proveedor meteorologico.
- Firebase Cloud Messaging como proveedor de push.
- Variables de entorno y secretos ya configurados.

## Desglose propuesto de tareas

| Task ID | Prioridad | Item | Capa | Criterio de aceptacion |
|---|---:|---|---|---|
| WEATHER-PRED-001 | P0 | Normalizar contrato de clima usado por prediccion | Backend | Implementado: el backend expone fuente, precipitacion y ventana de pronostico ademas de los datos meteorologicos base. |
| DOMAIN-LOCATION-001 | P0 | Formalizar ubicaciones de secado y sus factores | Domain/Data | Implementado: las cargas soportan `dryingLocationId` controlado y separado de `locationId`. |
| BACKEND-PRED-001 | P0 | Calcular tiempo estimado de secado con clima y ubicacion | Backend | Implementado: la prediccion usa clima normalizado y perfil de ubicacion de secado. |
| ANDROID-DASH-REAL-001 | P0 | Mostrar carga activa, clima y estimacion en dashboard | Android UI/Data | Implementado: Dashboard usa carga activa, clima y prediccion backend con fallback local. |
| BACKEND-PRED-WINDOW-001 | P0 | Exponer ventana recomendada y slots horarios desde la prediccion | Backend | Implementado: el response de prediccion incluye `recommendedHangWindowStart`, `recommendedHangWindowEnd` y `hourlySlots`. |
| ANDROID-DASH-HOURLY-001 | P0 | Mostrar carrusel horario y ventana recomendada en Dashboard | Android UI/Data | Implementado: Dashboard muestra ventana de tendido y proximas horas con score, lluvia y temperatura. |
| ANDROID-LOAD-LOCATION-001 | P0 | Seleccionar ubicacion de secado al crear carga | Android UI/Data | Implementado: Android envia `dryingLocationId` al crear una carga. |
| BACKEND-NOTIF-AUTO-001 | P0 | Crear, actualizar o cancelar eventos pendientes por cambios de carga | Backend | Implementado: el backend crea y cancela eventos pendientes segun el ciclo de vida de la carga. |
| BACKEND-NOTIF-HANG-WINDOW-001 | P0 | Programar aviso automatico al inicio de la mejor ventana de tendido | Backend/Notifications | Implementado: crea evento `IDEAL_HANGING_TIME` con `scheduled_for` basado en `recommendedHangWindowStart`; QA real con cron-job.org pendiente. |
| PB-024 / MVP-TIME-FEASIBILITY-001 | P0 | Comparar hora objetivo contra lavado y secado de cada programa | Domain/Backend/Android | Implementado y probado; contrato desplegado verificado; smoke autenticado y evidencia de dispositivo pendientes. |
| QA-INCREMENT-004 | P0 | Validar flujo end-to-end del incremento | Quality/Evidence | Runbook de validacion creado; evidencia manual final pendiente de captura. |

## Reglas de alcance

- El incremento debe reutilizar las integraciones ya existentes antes de introducir nuevos proveedores o librerias.
- La prediccion puede ser heuristica y explicable; no requiere machine learning para el MVP.
- La heuristica de prediccion debe quedar documentada y trazable en `docs/05-domain/algorithms/ALG-001-Drying-Recommendation-Heuristic.md`.
- Android debe consumir datos por backend, no consultar directamente Supabase para el flujo principal.
- Las notificaciones deben respetar opt-in, permiso runtime de Android y registro FCM activo.
- Las acciones automaticas deben ser auditables en `notification_events`.
- Si una carga cambia de estado, las notificaciones pendientes deben actualizarse, cancelarse o registrarse como no aplicables.

## Validacion esperada

Flujo minimo:

1. Usuario inicia sesion.
2. Usuario crea o selecciona lavarropas.
3. Usuario registra una carga con tipo de ropa, programa y ubicacion de secado.
4. Backend consulta clima y calcula estimacion.
5. Dashboard muestra carga activa, clima, recomendacion y tiempo aproximado.
6. Backend registra evento de notificacion.
7. Android recibe o visualiza la notificacion correspondiente.
8. Historial refleja el estado actualizado de la carga.

## Uso para la tesis

Este incremento permite explicar TenderApp como un caso de uso funcional y acotado: una aplicacion movil conectada a backend, base de datos, proveedor climatico y proveedor de notificaciones.

Tambien aporta evidencia para describir el metodo de trabajo incremental asistido por IA: cada decision se documenta, se implementa en una tarea controlable, se valida y se usa como insumo para la tesis final.

## Changelog

| Version | Date | Change |
|---|---|---|
| 1.1.0 | 2026-07-25 | Added PB-024 completion planning so New Load can compare washing plus drying against a target-ready time. |
| 1.0.0 | 2026-07-25 | Marked BACKEND-NOTIF-HANG-WINDOW-001 as implemented with scheduled IDEAL_HANGING_TIME events and pending external cron validation. |
| 0.9.0 | 2026-07-25 | Added prediction hang-window and hourly-slot scope for Dashboard and the pending ideal-hanging-time notification task. |
| 0.8.0 | 2026-07-11 | Added QA-INCREMENT-004 validation artifact for Increment 4 end-to-end evidence. |
| 0.7.0 | 2026-07-11 | Marked BACKEND-NOTIF-AUTO-001 as implemented with pending notification lifecycle automation. |
| 0.6.0 | 2026-07-11 | Marked ANDROID-DASH-REAL-001 as implemented with backend prediction integration in Android dashboard. |
| 0.5.0 | 2026-07-11 | Marked ANDROID-LOAD-LOCATION-001 as implemented with drying-location selection in New Load. |
| 0.4.0 | 2026-07-11 | Marked BACKEND-PRED-001 as implemented with weather and drying-location factors. |
| 0.3.0 | 2026-07-11 | Marked DOMAIN-LOCATION-001 as implemented with controlled drying locations and Supabase migration. |
| 0.2.0 | 2026-07-11 | Marked WEATHER-PRED-001 as implemented with prediction-ready weather contract additions. |
| 0.1.0 | 2026-07-11 | Initial formal definition of MVP Increment 4 focused on weather prediction, drying locations and automatic notifications. |
