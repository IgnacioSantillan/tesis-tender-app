# Sintesis incremental y Kanban de TenderApp

Estado: version curada para revision academica.

## Proposito

Este archivo resume el desarrollo de TenderApp mediante cinco incrementos de valor y un flujo Kanban adaptado. Relaciona objetivos, trabajo realizado, evidencia y pendientes sin sustituir el historial detallado de tareas.

## Modelo de trabajo

El desarrollo se organizo como un flujo Kanban de trabajo limitado y visible:

1. Detectado: se identifica una necesidad, brecha o riesgo.
2. Priorizado: se define el alcance y el criterio de aceptacion.
3. En desarrollo: se ajustan codigo, datos, contratos o interfaz.
4. Implementado: el cambio queda integrado.
5. Validado: se ejecuta prueba automatizada, smoke test o comprobacion manual.
6. Documentado: se registra la evidencia y el pendiente correspondiente.
7. Cerrado: la unidad queda vinculada con su incremento de valor.

Cada incremento contiene actividades de especificacion, diseno, implementacion, verificacion y registro de evidencia. Los incrementos son entregas acumulativas orientadas a valor, gestionadas mediante el flujo Kanban descrito en este documento.

## Cinco incrementos de valor

| Incremento | Objetivo de valor | Resultado principal | Evidencia disponible |
|---|---|---|---|
| Incremento 1. Preparacion tecnica y tecnologica | Establecer una base integrada y verificable para construir el producto. | Arquitectura, dominio inicial, proyecto Android, backend, persistencia inicial y criterios de calidad. | Codigo base, arquitectura, modelo de dominio, configuracion de build y reglas de calidad. |
| Incremento 2. Acceso y configuracion del entorno domestico | Permitir que una persona acceda y configure el contexto donde utilizara TenderApp. | Registro, inicio de sesion, recuperacion, sesion, hogar, ubicaciones de secado y lavarropas. | Implementacion Android/backend, contratos de autenticacion, configuracion de ubicacion y pruebas de sesion. |
| Incremento 3. Gestion de cargas y recomendacion meteorologica | Transformar datos de carga y clima en una orientacion accionable. | Cargas, historial, clima, prediccion heuristica, ventanas horarias, consumo aproximado y planificacion por hora objetivo. | Calculadoras, pruebas deterministas, endpoints, migraciones y pantallas de Dashboard y Nueva carga. |
| Incremento 4. Seguimiento y automatizacion | Acompanar el ciclo de la carga y avisar cuando corresponde actuar. | Estados, progreso, historial operativo, FCM, permisos, eventos auditados, scheduler y localizacion. | Pruebas de notificaciones, eventos de auditoria, smoke tests y capturas v7. |
| Incremento 5. Integracion, validacion y cierre | Verificar el MVP completo y preparar evidencia defendible. | Regresion, escenarios controlados, correcciones, evidencia consolidada y guia de revision. | QA final, pruebas Android/backend, capturas v7/v8, smoke tests y este repositorio curado. |

## Evidencia por etapa del trabajo

| Etapa Kanban | Pregunta de control | Evidencia |
|---|---|---|
| Detectado | Que brecha o necesidad se identifico? | Bitacora incremental y requisitos. |
| Priorizado | Que alcance se aprobo para el incremento? | Criterios de aceptacion y documentos de implementacion. |
| En desarrollo | Que componentes se modificaron? | Codigo Android, backend y migraciones. |
| Validado | Como se comprobo el resultado? | Pruebas, smoke tests, build, capturas o registros manuales. |
| Documentado | Que se puede revisar? | `evidence/`, documentacion tecnica y guia de revision. |
| Cerrado | Que limite permanece? | QA final y pendientes declarados. |

## Evidencia por capitulo

| Capitulo | Evidencia recomendada |
|---|---|
| Introduccion | Problema de decision de secado, alcance y objetivos. |
| Marco teorico | Android, Compose, API REST, Supabase, clima, prediccion, notificaciones y calidad. |
| Metodologia | Flujo Kanban, cinco incrementos, criterios de aceptacion y bitacora. |
| Desarrollo e implementacion | Codigo, migraciones, contratos y decisiones de arquitectura. |
| Validacion y resultados | Pruebas, builds, smoke tests, capturas, logs y eventos auditados. |
| Conclusiones | Alcance logrado, limitaciones, pendientes y trabajos futuros. |

## Criterio de interpretacion

Una capacidad implementada no se presenta automaticamente como validada. La tesis debe distinguir entre codigo existente, prueba automatizada, observacion manual y evidencia visual. La prediccion de secado es heuristica y orientativa; las pruebas demuestran consistencia interna y no precision fisica del tiempo de secado.

## Pendientes declarados

- Observar en ejecucion diferida un evento vencido del scheduler.
- Mantener como trabajo futuro la calibracion con tiempos reales de secado.
- Ejecutar las verificaciones que requieran credenciales, Firebase o Supabase en un entorno configurado.
