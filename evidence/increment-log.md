# Bitacora de evidencia incremental

Este archivo registra tareas representativas y evidencia verificable del desarrollo de TenderApp. La organizacion sigue cinco incrementos de valor y un flujo Kanban adaptado.

## Regla de registro

Cada tarea registra fecha, incremento, alcance, archivos principales, verificacion, evidencia y pendientes. Una tarea implementada no se considera automaticamente validada.

## Flujo Kanban aplicado

Las unidades de trabajo recorren los estados `Detectado`, `Priorizado`, `En desarrollo`, `Implementado`, `Validado`, `Documentado` y `Cerrado`. El trabajo en curso se limita para mantener cambios pequenos y revisables.

## Incremento 1. Preparacion tecnica y tecnologica

### TASK-002-DASHBOARD

- Estado: implementado y verificado por build.
- Alcance: primera pantalla Dashboard, veredicto climatico, metricas y estado mock.
- Archivos principales: `DashboardScreen.kt`, `TenderApp.kt`, `TenderDestination.kt`.
- Verificacion: `:app:assembleDebug` exitoso.
- Evidencia: codigo Android y estructura de navegacion.
- Pendiente: captura visual inicial y revision visual historica.

### TASK-003-MOCKUP-SCREENS

- Estado: implementado y verificado por build.
- Alcance: pantallas iniciales de Nueva carga, Historial y Ajustes.
- Archivos principales: `NewLoadScreen.kt`, `HistoryScreen.kt`, `SettingsScreen.kt`.
- Verificacion: `:app:assembleDebug` exitoso.
- Evidencia: navegacion y pantallas Compose.
- Pendiente: evidencia visual de la primera version.

### TASK-004-DESIGN-TOKENS

- Estado: implementado y verificado por build.
- Alcance: paleta, formas y roles semanticos del tema de la aplicacion.
- Archivos principales: `Color.kt`, `Shape.kt`, `Theme.kt`, `Type.kt`.
- Verificacion: `:app:assembleDebug` exitoso.
- Evidencia: tema Material y capturas posteriores.

### TASK-005-DOMAIN-MODEL

- Estado: implementado y verificado por build.
- Alcance: modelos de lavarropas, carga, clima, ubicacion, programa y prediccion.
- Verificacion: `:app:assembleDebug` exitoso.
- Evidencia: modelos de dominio y repositorio local inicial.

## Incremento 2. Acceso y configuracion del entorno domestico

### Acceso, sesion y contexto domestico

- Estado: implementado y validado en el flujo funcional.
- Alcance: registro, inicio de sesion, recuperacion, persistencia de sesion, hogar, ubicaciones y lavarropas.
- Evidencia: implementacion Android/backend, contratos, migraciones y capturas seleccionadas.
- Pendiente: repetir cualquier verificacion que dependa de credenciales locales o servicios externos.

## Incremento 3. Gestion de cargas y recomendacion meteorologica

### Cargas e historial

- Estado: implementado y validado en el flujo principal.
- Alcance: crear cargas, seleccionar lavarropas, tipo de ropa, programa, ubicacion, centrifugado y tamano; consultar historial y actualizar estados.
- Evidencia: endpoints REST, mapeos Android, migraciones, pruebas y capturas.

### Clima y prediccion

- Estado: implementado y validado en consistencia.
- Alcance: Open-Meteo como proveedor principal, MET Norway como respaldo, fuente visible, prediccion, ventana horaria y estimacion energetica orientativa.
- Evidencia: calculadoras, pruebas de sensibilidad, QA y capturas v7/v8.
- Limite: la duracion estimada no representa precision fisica calibrada.

## Incremento 4. Seguimiento y automatizacion

### Estados, progreso e historial operativo

- Estado: implementado.
- Alcance: seguimiento de cargas, progreso temporal, finalizacion, descarte logico y conservacion del historial.
- Evidencia: servicios, ViewModels, migraciones y capturas de estados.

### Notificaciones push

- Estado: envio inmediato validado; despacho programado implementado con observacion diferida.
- Alcance: registro FCM, permisos Android, preferencias, eventos auditados y despacho protegido.
- Evidencia: `notification_events`, pruebas, smoke tests y captura redactada.
- Pendiente: observar un evento vencido procesado por el scheduler.

## Incremento 5. Integracion, validacion y cierre

### QA y regresion

- Estado: validado dentro del alcance del MVP.
- Evidencia: pruebas Android, pruebas backend, compilacion TypeScript, smoke tests HTTP, contrato OpenAPI y capturas v7/v8.
- Limite: los resultados prueban el comportamiento cubierto, no la ausencia absoluta de defectos.

### Repositorio curado

- Estado: publicado para revision academica.
- Contenido: codigo, migraciones, arquitectura, reglas de dominio, QA, evidencia visual y esta bitacora.
- Exclusiones: secretos, configuraciones locales, dependencias instaladas, builds, logs sensibles y datos personales.

## Criterio de cierre

Una unidad se considera cerrada cuando el cambio esta integrado, su verificacion esta registrada, la evidencia puede ser revisada y sus limites permanecen explicitados. Las tareas pendientes se conservan como pendientes y no se presentan como validaciones concluidas.
