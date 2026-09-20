---
id: MVP-INCREMENT-005
title: Prediccion energetica, centrifugado y costo aproximado
category: Implementation Planning
version: 0.8.0
status: Validated
language: Spanish
author: Juan Santillan
created: 2026-07-12
updated: 2026-07-13
related:
  - MVP-INCREMENTS-001
  - MVP-001
  - BACKLOG-001
  - PB-023
  - ENERGY-PRED-001
  - ALG-001
---

# MVP-INCREMENT-005 - Prediccion energetica, centrifugado y costo aproximado

## Proposito

Ordenar como incremento funcional de TenderApp la ampliacion de la recomendacion de lavado/secado mediante datos del lavarropas y del ciclo: eficiencia energetica, revoluciones de centrifugado, tamano de carga, consumo estimado y costo aproximado.

Este incremento surge despues de cerrar el flujo principal de clima, ubicacion, prediccion y notificaciones. Ya no se trata solamente de un refinamiento posterior: pasa a ser una extension controlada del MVP demostrable porque mejora la utilidad central de la aplicacion y fortalece la defensa de la tesis.

## Objetivo de valor

Permitir que TenderApp no solo responda si el clima favorece el secado, sino tambien si la carga y el lavarropas elegidos hacen razonable iniciar el lavado desde el punto de vista de tiempo, humedad residual y costo aproximado.

El usuario deberia poder:

- configurar o visualizar la eficiencia energetica del lavarropas;
- definir revoluciones habituales o del ciclo;
- indicar tamano de carga desde opciones simples;
- recibir una estimacion aproximada de tiempo de secado ajustada por centrifugado;
- visualizar costo o consumo relativo sin prometer precision economica exacta;
- comprender que factor domina la recomendacion.

## Justificacion dentro del MVP

El MVP original se centraba en clima, ubicacion de secado y notificaciones. Sin embargo, durante la validacion del producto se detecto que la decision real del usuario no depende solo del clima. Tambien intervienen:

- el tipo de lavarropas;
- la eficiencia energetica;
- el programa de lavado;
- el centrifugado;
- el tamano de la carga;
- el consumo aproximado.

Por eso, este incremento se incorpora como **MVP Incremento 5**. Su alcance sigue siendo acotado y heuristico, pero permite que TenderApp sea defendible como asistente de decision domestica y no solo como una aplicacion meteorologica con recordatorios.

## Alcance principal

- Incorporar campos persistentes para rpm, tamano de carga y costo/consumo aproximado.
- Exponer esos campos en contratos backend y OpenAPI/Swagger.
- Extender la heuristica de prediccion con centrifugado, carga y costo relativo.
- Actualizar Android para consumir los nuevos campos.
- Agregar desplegables compactos en Settings y Nueva carga.
- Mostrar costo aproximado o nivel relativo de costo en Dashboard/Nueva carga.
- Mantener localizacion en espanol e ingles.
- Validar el flujo end-to-end con tests y evidencia.

## Componentes afectados

### Supabase

- `washers.default_spin_rpm`.
- `laundry_loads.spin_rpm`.
- `laundry_loads.load_size`.
- Campos nullable de snapshot de energia, agua y costo aproximado.

### Backend

- DTOs de lavarropas.
- DTOs de cargas.
- DTOs de prediccion.
- Validaciones de valores controlados.
- Calculadora heuristica de prediccion.
- Swagger/OpenAPI.

### Android

- Modelos de dominio y DTOs remotos.
- Mappers.
- Settings de lavarropas.
- Nueva carga.
- Dashboard / tarjeta de decision.
- Strings localizados.

### Tesis

- Marco de defensa conceptual: TenderApp interpreta variables domesticas y climaticas.
- Evidencia de enfoque incremental: documentacion, migracion, backend, UI, pruebas y capturas.
- Explicacion de heuristica: no es medicion certificada ni machine learning.

## Tareas del incremento

| Task ID | Prioridad | Item | Capa | Estado |
|---|---:|---|---|---|
| ENERGY-PRED-DOC-001 | P0 | Formalizar reglas de eficiencia, rpm y costo aproximado | Documentation/Domain | Implementado |
| SUPABASE-ENERGY-001 | P0 | Agregar campos nullable para rpm, tamano y costo/consumo aproximado | Supabase/Data | Implementado en repositorio |
| BACKEND-ENERGY-001 | P0 | Actualizar DTOs, validaciones y Swagger | Backend API | Implementado |
| BACKEND-ENERGY-002 | P0 | Ampliar calculadora de prediccion con rpm, carga y costo | Backend Domain | Implementado |
| BACKEND-ENERGY-003 | P0 | Completar predicciones de Dashboard desde carga y lavarropas asociados | Backend Integration | Implementado |
| ANDROID-ENERGY-001 | P0 | Actualizar modelos, DTOs y mappers Android | Android Data | Implementado |
| ANDROID-ENERGY-002 | P0 | Agregar selectores compactos en Settings y Nueva carga | Android UI/UX | Implementado |
| ANDROID-ENERGY-003 | P1 | Mostrar costo aproximado y factores dominantes | Android UI/UX | Implementado |
| QA-ENERGY-PRED-001 | P0 | Validar flujo energetico end-to-end | Quality/Evidence | Evidencia registrada |
| THESIS-DEFENSE-001 | P1 | Incorporar defensa conceptual a tesis | Thesis | Documentado inicialmente |

## Reglas de alcance

- La estimacion de costo es orientativa, no certificada.
- No se inventan tarifas reales.
- Si no existe tarifa configurada, se muestra nivel relativo o confianza baja.
- La eficiencia energetica afecta costo aproximado, no velocidad de secado.
- El centrifugado afecta humedad residual y tiempo estimado de secado.
- Ningun campo nuevo debe bloquear cargas o lavarropas existentes.
- Android debe usar opciones controladas, no texto libre.
- La prediccion debe seguir siendo explicable.

## Criterios de aceptacion

- Supabase soporta campos nuevos sin romper datos anteriores.
- Backend expone contratos y validaciones para los campos nuevos.
- La calculadora modifica el tiempo estimado cuando cambia el centrifugado.
- La calculadora devuelve costo/consumo aproximado o relativo.
- Android permite elegir rpm y tamano de carga desde controles compactos.
- La UI comunica el costo como aproximacion.
- Los tests cubren casos de rpm bajo/alto, eficiencia conocida/desconocida y fallback.
- La evidencia queda registrada en bitacora y capturas.

## Estado actual

Al 2026-07-12:

- `ENERGY-PRED-DOC-001` implementado.
- `SUPABASE-ENERGY-001` implementado en repositorio.
- `BACKEND-ENERGY-001` implementado y verificado con build/tests backend.
- `BACKEND-ENERGY-002` implementado y verificado con build/tests backend.
- `BACKEND-ENERGY-003` implementado y verificado con tests focalizados de prediccion y build backend.
- `ANDROID-ENERGY-001` implementado y verificado con build/tests Android.
- `ANDROID-ENERGY-002` implementado y verificado con build/tests Android.
- `ANDROID-ENERGY-003` implementado y verificado con build/tests Android.
- `QA-ENERGY-PRED-001` validado con evidencia visual `v5` y confirmacion manual Android de llegada de datos energeticos y cambios en prediccion de gasto electrico aproximado.

## Uso para la tesis

Este incremento permite argumentar que TenderApp integra informacion heterogenea en una recomendacion contextual:

```text
clima + ubicacion + carga + lavarropas + centrifugado + costo aproximado
```

La aplicacion se mantiene acotada, pero el caso de uso gana valor conceptual porque demuestra como una arquitectura Android/backend/Supabase puede transformar datos dispersos en una decision domestica comprensible.

## Changelog

| Version | Date | Change |
|---|---|---|
| 0.8.0 | 2026-07-13 | Marked Increment 5 as validated after manual Android visual smoke confirmed energy data and electricity-cost prediction changes. |
| 0.7.0 | 2026-07-12 | Added BACKEND-ENERGY-003 to enrich Dashboard predictions with washer metadata inferred from the authenticated user's laundry load. |
| 0.6.0 | 2026-07-12 | Registered v5 screenshot evidence for QA-ENERGY-PRED-001. |
| 0.5.0 | 2026-07-12 | Marked ANDROID-ENERGY-003 as implemented with approximate cost, kWh, water and confidence display in Dashboard/New Load. |
| 0.4.0 | 2026-07-12 | Marked ANDROID-ENERGY-002 as implemented with compact Android selectors for spin speed and load size. |
| 0.3.0 | 2026-07-12 | Marked ANDROID-ENERGY-001 as implemented with Android energy-aware models, DTOs, mappers and tests. |
| 0.2.0 | 2026-07-12 | Marked BACKEND-ENERGY-002 as implemented with energy-aware prediction calculator and tests. |
| 0.1.0 | 2026-07-12 | Initial Increment 5 definition for energy-aware prediction, spin speed and approximate cost. |
