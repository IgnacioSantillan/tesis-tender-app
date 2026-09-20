# Bitacora de evidencia incremental

Este archivo registra el avance tecnico que alimenta la redaccion de la tesis. Su objetivo es conservar la relacion entre documentacion, aprobacion humana, implementacion y evidencia verificable.

## Regla de registro

Cada tarea aprobada debe registrar:

- Fecha.
- Sprint o documento de implementacion relacionado.
- Documentos leidos o usados como contexto.
- Alcance aprobado.
- Archivos modificados.
- Verificacion ejecutada.
- Uso previsto dentro de la tesis.
- Evidencia pendiente, si corresponde.

## TASK-002-DASHBOARD

Fecha: 2026-07-05.

Estado: implementado y verificado por build.

Origen documental:

- `docs/09-implementation/SPRINT-003-Sprint-02-Dashboard-MVP.md`
- `docs/06-design/UI-004-Dashboard-Screen-Specification.md`
- `docs/06-design/UI-006-Weather-Verdict-Card.md`
- `docs/06-design/prototypes/Asistente de Lavado.dc.html`
- `docs/11-quality/QA-003-Implementation-Review-Gate.md`

Alcance aprobado:

- Implementar una primera pantalla dashboard con estado mock.
- Mostrar veredicto climatico, metricas, formulario resumido de carga y estado actual.
- Mantener datos mock y evitar logica de negocio real dentro de composables.

Archivos principales:

- `app/src/main/java/com/tesis_pro/tenderapp/ui/screens/DashboardScreen.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/ui/TenderApp.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/navigation/TenderDestination.kt`

Verificacion:

- `:app:assembleDebug` ejecutado con `JAVA_HOME=E:\android\jbr`.
- Resultado: build exitoso.

Uso previsto en la tesis:

- Capitulo 3: evidencia del metodo incremental con aprobacion humana.
- Capitulo 4: evidencia de transformacion de especificacion UX/prototipo a implementacion Android.
- Capitulo 5: insumo para evaluar trazabilidad y compilabilidad del incremento.

Evidencia pendiente:

- Captura visual del dashboard en emulador o dispositivo.
- Registro de revision humana posterior a la captura.

## TASK-003-MOCKUP-SCREENS

Fecha: 2026-07-05.

Estado: implementado y verificado por build.

Origen documental:

- `docs/09-implementation/SPRINT-003-Sprint-02-Dashboard-MVP.md`
- `docs/06-design/UI-005-New-Load-Flow-Specification.md`
- `docs/06-design/prototypes/Asistente de Lavado.dc.html`
- `docs/11-quality/QA-003-Implementation-Review-Gate.md`

Alcance aprobado:

- Reemplazar placeholders por pantallas mock reales.
- Implementar pantallas de nueva carga, historial y ajustes.
- No agregar persistencia, backend, ViewModels ni nuevas dependencias.

Archivos principales:

- `app/src/main/java/com/tesis_pro/tenderapp/ui/screens/NewLoadScreen.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/ui/screens/HistoryScreen.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/ui/screens/SettingsScreen.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/ui/screens/PlaceholderScreen.kt`

Verificacion:

- `:app:assembleDebug` ejecutado con `JAVA_HOME=E:\android\jbr`.
- Resultado: build exitoso.

Uso previsto en la tesis:

- Capitulo 3: ejemplo de tarea pequena, aprobada y controlada por QA.
- Capitulo 4: evidencia de expansion del MVP visual desde dashboard hacia navegacion basica completa.
- Capitulo 5: insumo para discutir avance incremental, limites de mocks y deuda tecnica controlada.

Evidencia pendiente:

- Capturas de las pantallas `New load`, `History` y `Settings`.
- Revision de consistencia visual contra el prototipo.

## TASK-004-DESIGN-TOKENS

Fecha: 2026-07-05.

Estado: implementado y verificado por build.

Origen documental:

- `docs/06-design/UI-001-Design-System.md`
- Archivos de referencia provistos por el autor:
  - `Color.kt`
  - `Shape.kt`
  - `Theme.kt`
  - `Type.kt`
- `docs/11-quality/QA-003-Implementation-Review-Gate.md`

Alcance aprobado:

- Incorporar la paleta visual actualizada al theme Compose de TenderApp.
- Mantener el entrypoint `TenderAppTheme`.
- Agregar tokens de forma y roles semanticos para estados de recomendacion.
- No modificar navegacion, datos, repositorios ni dependencias.

Archivos principales:

- `app/src/main/java/com/tesis_pro/tenderapp/ui/theme/Color.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/ui/theme/Shape.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/ui/theme/Theme.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/ui/theme/Type.kt`
- `docs/06-design/UI-001-Design-System.md`

Verificacion:

- `:app:assembleDebug` ejecutado con `JAVA_HOME=E:\android\jbr`.
- Resultado: build exitoso.

Uso previsto en la tesis:

- Capitulo 3: evidencia de incorporacion incremental de un insumo de diseno provisto por revision humana.
- Capitulo 4: evidencia de alineacion entre prototipo visual, sistema de diseno y theme Android.
- Capitulo 5: insumo para evaluar consistencia visual y control de cambios sobre la interfaz.

Evidencia pendiente:

- Capturas comparativas antes/despues de la aplicacion de tokens.
- Revision visual contra el prototipo actualizado.

## TASK-005-DOMAIN-MODEL

Fecha: 2026-07-05.

Estado: implementado y verificado por build.

Origen documental:

- `docs/09-implementation/SPRINT-004-Sprint-03-Data-and-Prediction.md`
- `docs/05-domain/entities/ENT-002-Washer.md`
- `docs/05-domain/entities/ENT-003-Laundry-Load.md`
- `docs/05-domain/entities/ENT-004-Weather-Snapshot.md`
- `docs/05-domain/entities/ENT-005-Drying-Prediction.md`
- `docs/05-domain/entities/ENT-007-Location.md`
- `docs/05-domain/entities/ENT-008-Washing-Program.md`
- `docs/05-domain/entities/ENT-009-Clothing-Type.md`
- `docs/05-domain/business-rules/BR-005-Explainable-Recommendation.md`
- `docs/04-architecture/ARC-004-Repository-and-Data-Flow.md`
- `docs/11-quality/QA-001-Definition-of-Done.md`

Alcance aprobado:

- Crear modelos de dominio puros para TenderApp.
- No implementar algoritmo de prediccion todavia.
- No agregar Room nuevo, backend, ViewModel ni UI.
- Mantener los modelos independientes de anotaciones de persistencia.

Archivos principales:

- `app/src/main/java/com/tesis_pro/tenderapp/domain/model/ClothingType.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/domain/model/DryingMethod.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/domain/model/DryingPrediction.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/domain/model/DryingVerdict.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/domain/model/LaundryLoad.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/domain/model/LaundryLoadStatus.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/domain/model/Washer.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/domain/model/WasherType.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/domain/model/WashingProgram.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/domain/model/WeatherCondition.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/domain/model/WeatherLocation.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/domain/model/WeatherSnapshot.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/data/repository/LocalLaundryRepository.kt`

Verificacion:

- `:app:assembleDebug` ejecutado con `JAVA_HOME=E:\android\jbr`.
- Resultado: build exitoso.

Uso previsto en la tesis:

- Capitulo 3: evidencia de avance controlado desde documentacion de dominio hacia implementacion.
- Capitulo 4: evidencia de separacion entre modelos de dominio, persistencia local y UI.
- Capitulo 5: insumo para evaluar trazabilidad entre entidades documentadas y codigo Kotlin.

Evidencia pendiente:

- Tests unitarios del algoritmo cuando se implemente `TASK-006-DRYING-SUITABILITY-ALGORITHM`.
- Tabla de trazabilidad final entre entidades `ENT-*` y clases Kotlin.

## TASK-006-DRYING-SUITABILITY-ALGORITHM

Fecha: 2026-07-05.

Estado: implementado y verificado por tests y build.

Origen documental:

- `docs/09-implementation/SPRINT-004-Sprint-03-Data-and-Prediction.md`
- `docs/09-implementation/CODEX_LOOP.md`
- `docs/09-implementation/CODEX-001-Codex-Execution-Harness.md`
- `docs/07-prompts/CTX-003-Domain-Context.md`
- `docs/07-prompts/PROMPT-010-Single-Task-Implementation.md`
- `docs/08-agents/AG-003-Android-Agent.md`
- `docs/08-agents/AG-006-Testing-Agent.md`
- `docs/08-agents/AG-007-Documentation-Agent.md`
- `docs/05-domain/requirements/REQ-004-Drying-Prediction-Engine.md`
- `docs/05-domain/use-cases/UC-004-Calculate-Drying-Prediction.md`
- `docs/05-domain/business-rules/BR-001-Drying-Suitability-Score.md`
- `docs/05-domain/business-rules/BR-002-Rain-Risk-Override.md`
- `docs/05-domain/business-rules/BR-003-Forecast-Freshness.md`
- `docs/05-domain/business-rules/BR-005-Explainable-Recommendation.md`

Agentes aplicados como roles documentales:

- Android Agent: implementacion Kotlin pura en capa de dominio.
- Testing Agent: cobertura unitaria de escenarios principales del algoritmo.
- Documentation Agent: registro del incremento en la bitacora de evidencia.

Alcance aprobado:

- Crear un algoritmo inicial y testable de idoneidad de secado.
- Clasificar la recomendacion como `GOOD`, `CAUTION` o `BAD`.
- Considerar lluvia, humedad, viento, temperatura, nubosidad y metodo de secado.
- Incluir una explicacion corta para el usuario.
- No conectar todavia el algoritmo con UI, ViewModel, Room ni backend.

Archivos principales:

- `app/src/main/java/com/tesis_pro/tenderapp/domain/prediction/DryingSuitabilityCalculator.kt`
- `app/src/test/java/com/tesis_pro/tenderapp/domain/prediction/DryingSuitabilityCalculatorTest.kt`

Verificacion:

- `:app:testDebugUnitTest` ejecutado con `JAVA_HOME=E:\android\jbr`.
- Resultado: tests exitosos.
- `:app:assembleDebug` ejecutado con `JAVA_HOME=E:\android\jbr`.
- Resultado: build exitoso.

Uso previsto en la tesis:

- Capitulo 3: evidencia de uso de prompts/agentes especializados dentro de una tarea controlada.
- Capitulo 4: evidencia de primera logica de dominio desacoplada de UI y persistencia.
- Capitulo 5: insumo para evaluar testabilidad, explicabilidad y trazabilidad de reglas de negocio.

Evidencia pendiente:

- Revisar umbrales con criterio meteorologico o fuente tecnica externa antes de considerarlos definitivos.
- Conectar el algoritmo a repositorio/ViewModel en un incremento posterior.

## TASK-007-REPOSITORY-INTERFACES

Fecha: 2026-07-05.

Estado: implementado y verificado por tests y build.

Origen documental:

- `docs/09-implementation/SPRINT-004-Sprint-03-Data-and-Prediction.md`
- `docs/04-architecture/ARC-004-Repository-and-Data-Flow.md`
- `docs/07-prompts/PROMPT-002-Repository-and-Data-Flow-Generation.md`
- `docs/08-agents/AG-003-Android-Agent.md`
- `docs/08-agents/AG-007-Documentation-Agent.md`

Alcance aprobado:

- Crear contratos de repositorio para el dominio de clima, lavarropas y prediccion.
- Mantener los contratos independientes de Room, Compose y backend.
- Agregar una implementacion local minima para prediccion que encapsula la calculadora del dominio.
- No conectar todavia ViewModels ni pantallas.

Archivos principales:

- `app/src/main/java/com/tesis_pro/tenderapp/domain/repository/DryingPredictionRepository.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/domain/repository/WasherRepository.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/domain/repository/WeatherRepository.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/data/repository/LocalDryingPredictionRepository.kt`

Verificacion:

- `:app:testDebugUnitTest` ejecutado con `JAVA_HOME=E:\android\jbr`.
- Resultado: tests exitosos.
- `:app:assembleDebug` ejecutado con `JAVA_HOME=E:\android\jbr`.
- Resultado: build exitoso.

Uso previsto en la tesis:

- Capitulo 3: evidencia de aplicacion del patron Repository bajo control documental.
- Capitulo 4: evidencia de separacion entre contratos de dominio, fuentes de datos y UI.
- Capitulo 5: insumo para evaluar mantenibilidad y preparacion para offline/cache.

Evidencia pendiente:

- Implementar repositorios locales concretos para clima y lavarropas cuando existan DAOs o fuentes mock aprobadas.
- Conectar contratos a ViewModels en una tarea posterior.

## TASK-008-ROOM-PLACEHOLDERS

Fecha: 2026-07-05.

Estado: implementado y verificado por tests y build.

Origen documental:

- `docs/09-implementation/SPRINT-004-Sprint-03-Data-and-Prediction.md`
- `docs/04-architecture/ARC-004-Repository-and-Data-Flow.md`
- `docs/04-architecture/ARC-005-Offline-First-Synchronization.md`
- `docs/07-prompts/PROMPT-002-Repository-and-Data-Flow-Generation.md`
- `docs/08-agents/AG-003-Android-Agent.md`
- `docs/08-agents/AG-006-Testing-Agent.md`
- `docs/08-agents/AG-007-Documentation-Agent.md`

Alcance aprobado:

- Agregar placeholders Room para cache local de lavarropas y clima.
- Registrar nuevos DAOs en la base local.
- No implementar migraciones productivas, repositorios concretos de clima/lavarropas ni conexion con UI.
- Mantener Room como cache local segun la arquitectura offline-first.

Archivos principales:

- `app/src/main/java/com/tesis_pro/tenderapp/data/local/TenderDatabase.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/data/local/dao/WasherDao.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/data/local/dao/WeatherSnapshotDao.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/data/local/entity/WasherEntity.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/data/local/entity/WeatherSnapshotEntity.kt`

Verificacion:

- `:app:testDebugUnitTest` ejecutado con `JAVA_HOME=E:\android\jbr`.
- Resultado: tests exitosos.
- `:app:assembleDebug` ejecutado con `JAVA_HOME=E:\android\jbr`.
- Resultado: build exitoso.

Uso previsto en la tesis:

- Capitulo 3: evidencia de avance incremental desde contratos de dominio hacia persistencia local.
- Capitulo 4: evidencia de tratamiento de Room como cache local dentro de la arquitectura offline-first.
- Capitulo 5: insumo para evaluar mantenibilidad y separacion entre entidades Room y modelos de dominio.

Evidencia pendiente:

- Definir migraciones reales antes de persistencia productiva.
- Implementar mappers/repositorios locales para `WasherRepository` y `WeatherRepository`.

## TASK-009-DASHBOARD-VIEWMODEL

Fecha: 2026-07-05.

Estado: implementado y verificado por tests y build.

Origen documental:

- `docs/09-implementation/SPRINT-004-Sprint-03-Data-and-Prediction.md`
- `docs/07-prompts/PROMPT-001-Android-Feature-Generation.md`
- `docs/07-prompts/PROMPT-002-Repository-and-Data-Flow-Generation.md`
- `docs/08-agents/AG-003-Android-Agent.md`
- `docs/08-agents/AG-006-Testing-Agent.md`
- `docs/08-agents/AG-007-Documentation-Agent.md`
- `docs/04-architecture/ARC-004-Repository-and-Data-Flow.md`

Alcance aprobado:

- Exponer estado de dashboard desde un ViewModel.
- Usar datos mock de dominio y el repositorio local de prediccion.
- Mantener UI Compose sin logica de prediccion.
- No agregar dependencias nuevas ni conectar backend/Room real todavia.

Archivos principales:

- `app/src/main/java/com/tesis_pro/tenderapp/ui/screens/DashboardViewModel.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/ui/screens/DashboardScreen.kt`
- `app/src/test/java/com/tesis_pro/tenderapp/ui/screens/DashboardViewModelTest.kt`

Verificacion:

- `:app:testDebugUnitTest` ejecutado con `JAVA_HOME=E:\android\jbr`.
- Resultado: tests exitosos.
- `:app:assembleDebug` ejecutado con `JAVA_HOME=E:\android\jbr`.
- Resultado: build exitoso.

Uso previsto en la tesis:

- Capitulo 3: evidencia de cierre de sprint con una tarea pequena y verificable.
- Capitulo 4: evidencia de transicion desde UI mock hacia estado derivado de dominio/repositorio.
- Capitulo 5: insumo para evaluar separacion MVVM, testabilidad y trazabilidad de sprint.

Evidencia pendiente:

- Reemplazar instanciacion manual del ViewModel por DI/factory cuando se defina la estrategia de inyeccion.
- Conectar datos reales de Room/backend en sprints posteriores.

## TASK-010-API-CONTRACT-REVIEW

Fecha: 2026-07-05.

Estado: implementado como actualizacion documental.

Origen documental:

- `docs/09-implementation/SPRINT-005-Sprint-04-Backend-and-Sync.md`
- `docs/09-implementation/backend/API-001-Backend-API-Contract-Draft.md`
- `docs/09-implementation/backend/BACKEND-000-Project-Setup-and-Module-Structure.md`
- `docs/09-implementation/backend/MOD-002-Backend-Module-Plan.md`
- `docs/04-architecture/ARC-003-Backend-Architecture.md`
- `docs/04-architecture/ADR-004-Backend-Strategy.md`
- `docs/04-architecture/ARC-005-Offline-First-Synchronization.md`
- `docs/07-prompts/CTX-004-Backend-Context.md`
- `docs/08-agents/AG-004-Backend-Agent.md`

Alcance aprobado:

- Revisar el contrato API antes de crear DTOs Android o Retrofit.
- Agregar convenciones de contrato, limite de autenticacion, envelope de errores y DTOs iniciales.
- Mantener Supabase como detalle del backend, no como contrato Android.
- No implementar backend ni cliente remoto todavia.

Archivos principales:

- `docs/09-implementation/backend/API-001-Backend-API-Contract-Draft.md`

Verificacion:

- Cambio documental; no se ejecuto build.
- El contrato queda listo para derivar DTOs Android en la siguiente tarea.

Uso previsto en la tesis:

- Capitulo 3: evidencia de control documental previo a implementacion tecnica.
- Capitulo 4: evidencia de definicion de frontera Backend/API/Android.
- Capitulo 5: insumo para evaluar trazabilidad entre requisitos, arquitectura y contrato.

Evidencia pendiente:

- Convertir el contrato draft a OpenAPI YAML/JSON si el sprint lo requiere.
- Implementar DTOs Android aislados de modelos de dominio.

## BACKEND-TASK-001-CREATE-BACKEND-PROJECT-SKELETON

Fecha: 2026-07-05.

Estado: implementado como skeleton de backend.

Origen documental:

- `docs/07-prompts/PROMPT-011-Backend-Sprint-and-Task-Execution.md`
- `docs/09-implementation/backend/BACKEND-SPRINT-TASK-INDEX.md`
- `docs/09-implementation/backend/BACKEND-SPRINT-001-Backend-Foundation.md`
- `docs/09-implementation/backend/BACKEND-TASK-001-Create-Backend-Project-Skeleton.md`
- `docs/09-implementation/backend/API-001-Backend-API-Contract-Draft.md`
- `docs/09-implementation/backend/BACKEND-000-Project-Setup-and-Module-Structure.md`
- `docs/09-implementation/backend/MOD-002-Backend-Module-Plan.md`
- `docs/04-architecture/ARC-003-Backend-Architecture.md`
- `docs/04-architecture/ADR-004-Backend-Strategy.md`
- `docs/11-quality/TEST-002-Backend-Testing-Strategy.md`

Alcance aprobado:

- Crear la carpeta `backend/` al mismo nivel que `app/`.
- Inicializar un skeleton NestJS minimo.
- Exponer estructura base para `GET /api/v1/health`.
- No instalar dependencias todavia.
- No modificar Android.
- No configurar Supabase, autenticacion, persistencia ni proveedor meteorologico.

Archivos principales:

- `backend/README.md`
- `backend/package.json`
- `backend/tsconfig.json`
- `backend/tsconfig.build.json`
- `backend/nest-cli.json`
- `backend/src/main.ts`
- `backend/src/app.module.ts`
- `backend/src/app.controller.ts`
- `backend/src/app.service.ts`

Verificacion:

- Se verifico la estructura de archivos generada.
- No se ejecuto build backend porque las dependencias fueron declaradas pero no instaladas en este incremento.

Uso previsto en la tesis:

- Capitulo 3: evidencia de aplicacion del arnes documental backend antes de implementar funcionalidades.
- Capitulo 4: evidencia de creacion de la frontera backend entre Android y servicios de infraestructura.
- Capitulo 5: insumo para evaluar separacion de responsabilidades y trazabilidad de sprints backend.

Evidencia pendiente:

- Ejecutar `npm install` y validar `npm run build` cuando se apruebe la instalacion de dependencias.
- Agregar baseline de tests backend en `BACKEND-TASK-005`.

## BACKEND-TASK-002-CONFIGURE-ENVIRONMENT-AND-VALIDATION

Fecha: 2026-07-05.

Estado: implementado como configuracion backend inicial.

Origen documental:

- `docs/09-implementation/backend/BACKEND-SPRINT-001-Backend-Foundation.md`
- `docs/09-implementation/backend/BACKEND-TASK-002-Configure-Environment-and-Validation.md`
- `docs/04-architecture/ARC-003-Backend-Architecture.md`
- `docs/04-architecture/ADR-004-Backend-Strategy.md`
- `docs/04-architecture/ARC-007-Security-and-Privacy-Architecture.md`
- `docs/07-prompts/PROMPT-011-Backend-Sprint-and-Task-Execution.md`

Alcance aprobado:

- Agregar variables de entorno de ejemplo para desarrollo local.
- Agregar validacion basica de `NODE_ENV`, `PORT`, `API_PREFIX` y `API_VERSION`.
- Mantener secretos reales fuera del repositorio.
- No agregar dependencias nuevas.
- No modificar Android.

Archivos principales:

- `backend/.env.example`
- `backend/src/config/environment.ts`
- `backend/src/main.ts`
- `backend/src/app.service.ts`
- `backend/README.md`

Verificacion:

- Se verifico manualmente la estructura y el contenido de archivos.
- No se ejecuto build backend porque las dependencias aun no fueron instaladas.

Uso previsto en la tesis:

- Capitulo 3: evidencia de control de configuracion y secretos dentro del flujo incremental.
- Capitulo 4: evidencia de preparacion del backend para ejecucion local y versionado API.
- Capitulo 5: insumo para evaluar seguridad basica y mantenibilidad.

Evidencia pendiente:

- Instalar dependencias y ejecutar `npm run build`.
- Definir si se incorporara `@nestjs/config` o `dotenv` en una tarea posterior.

## BACKEND-TASK-003-ADD-HEALTH-CHECK-AND-API-VERSIONING

Fecha: 2026-07-05.

Estado: implementado y verificado por build backend.

Origen documental:

- `docs/09-implementation/backend/BACKEND-SPRINT-001-Backend-Foundation.md`
- `docs/09-implementation/backend/BACKEND-TASK-003-Add-Health-Check-and-API-Versioning.md`
- `docs/09-implementation/backend/API-001-Backend-API-Contract-Draft.md`
- `docs/09-implementation/backend/MOD-002-Backend-Module-Plan.md`
- `docs/04-architecture/ARC-003-Backend-Architecture.md`
- `docs/04-architecture/ADR-004-Backend-Strategy.md`

Alcance aprobado:

- Mantener `/api/v1` como prefijo configurable de API.
- Exponer health check en un modulo backend dedicado.
- Mantener Android sin cambios.
- Instalar dependencias backend para validar compilacion local.
- Actualizar `.gitignore` para excluir `.env`, `node_modules`, `dist`, logs y archivos temporales.

Archivos principales:

- `.gitignore`
- `backend/package-lock.json`
- `backend/README.md`
- `backend/src/app.module.ts`
- `backend/src/app.service.ts`
- `backend/src/health/health.controller.ts`
- `backend/src/health/health.module.ts`
- `backend/src/health/health.service.ts`

Verificacion:

- `npm.cmd install` ejecutado en `backend/`; genero `node_modules` y `package-lock.json`.
- `npm.cmd run build` ejecutado en `backend/`.
- Resultado: build exitoso.

Uso previsto en la tesis:

- Capitulo 3: evidencia de instalacion controlada de dependencias y validacion de build.
- Capitulo 4: evidencia de health endpoint versionado dentro de la frontera backend.
- Capitulo 5: insumo para evaluar reproducibilidad mediante `package-lock.json`.

Evidencia pendiente:

- Agregar tests backend en `BACKEND-TASK-005`.
- Agregar Swagger/OpenAPI en `BACKEND-TASK-004`.

## TASK-011-NOTIFICATION-SCHEDULING-ABSTRACTION

Fecha: 2026-07-05.

Estado: implementado y verificado por tests y build Android.

Origen documental:

- `docs/09-implementation/SPRINT-006-Sprint-05-Notifications-and-Polish.md`
- `docs/05-domain/requirements/REQ-006-Notification-Management.md`
- `docs/05-domain/use-cases/UC-006-Receive-Rain-Alert.md`
- `docs/05-domain/business-rules/BR-004-Notification-Eligibility.md`
- `docs/04-architecture/ARC-006-Notification-Architecture.md`
- `docs/09-implementation/BACKLOG-001-Product-Backlog.md`

Alcance aprobado:

- Retomar el carril Android y pausar tareas backend.
- Crear una abstraccion de scheduling de notificaciones.
- Implementar policy de elegibilidad sin WorkManager ni APIs Android todavia.
- Agregar tests unitarios de la regla de opt-in/evento accionable.

Archivos principales:

- `app/src/main/java/com/tesis_pro/tenderapp/domain/notification/NotificationKind.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/domain/notification/NotificationPreferences.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/domain/notification/NotificationScheduleRequest.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/domain/notification/NotificationScheduleResult.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/domain/notification/NotificationScheduler.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/domain/notification/NotificationEligibilityPolicy.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/domain/notification/NotificationSkipReason.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/data/notification/LocalNotificationScheduler.kt`
- `app/src/test/java/com/tesis_pro/tenderapp/domain/notification/NotificationEligibilityPolicyTest.kt`

Verificacion:

- `:app:testDebugUnitTest` ejecutado con `JAVA_HOME=E:\android\jbr`.
- Resultado: tests exitosos.
- `:app:assembleDebug` ejecutado con `JAVA_HOME=E:\android\jbr`.
- Resultado: build exitoso.

Uso previsto en la tesis:

- Capitulo 3: evidencia de cambio controlado de carril backend a carril Android.
- Capitulo 4: evidencia de arquitectura staged para notificaciones locales.
- Capitulo 5: insumo para evaluar control de trabajo en background y cumplimiento de BR-004.

Evidencia pendiente:

- Decidir si se agregara WorkManager en una tarea posterior.
- Conectar preferencias reales de usuario antes de activar scheduling en runtime.

## TASK-012-UI-STATES-POLISH

Fecha: 2026-07-06.

Estado: implementado y verificado por tests y build Android.

Origen documental:

- `docs/FR5-INDEX.md`
- `docs/FR6-INDEX.md`
- `docs/CONTEXT-002-Codex-Reading-Protocol.md`
- `docs/CONTEXT-003-Task-Context-Packs.md`
- `docs/09-implementation/SPRINT-006-Sprint-05-Notifications-and-Polish.md`
- `docs/11-quality/QA-001-Definition-of-Done.md`

Alcance aprobado:

- Retomar la tarea desde el carril Android.
- Agregar estados `Loading`, `Empty`, `Error` y `Content` al dashboard.
- Mantener el dashboard conectado al ViewModel y al estado calculado existente.
- No conectar backend, Supabase, WorkManager ni fuentes remotas.

Archivos principales:

- `app/src/main/java/com/tesis_pro/tenderapp/ui/screens/DashboardScreen.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/ui/screens/DashboardViewModel.kt`
- `app/src/test/java/com/tesis_pro/tenderapp/ui/screens/DashboardViewModelTest.kt`

Verificacion:

- `:app:testDebugUnitTest` ejecutado con `JAVA_HOME=E:\android\jbr`.
- Resultado: tests exitosos.
- `:app:assembleDebug` ejecutado con `JAVA_HOME=E:\android\jbr`.
- Resultado: build exitoso.

Uso previsto en la tesis:

- Capitulo 3: evidencia de aplicacion explicita del protocolo FR5/FR6 al retomar contexto.
- Capitulo 4: evidencia de mejora de estados UI sin mezclar logica remota.
- Capitulo 5: insumo para evaluar robustez de interfaz y preparacion para demo MVP.

Evidencia pendiente:

- Capturar screenshots de estados principales cuando se ejecute la app en emulador/dispositivo.
- Aplicar estados equivalentes a pantallas de historial, nueva carga y ajustes si el review gate lo pide.

## TASK-013-SUPABASE-SECRET-HYGIENE

Fecha: 2026-07-06.

Estado: implementado y verificado por scan local y build backend.

Origen documental:

- `docs/04-architecture/ARC-007-Security-and-Privacy-Architecture.md`
- `docs/09-implementation/backend/BACKEND-TASK-006-Configure-Supabase-Client.md`
- `docs/09-implementation/backend/SUPABASE-001-Project-Setup-Checklist.md`
- `docs/11-quality/QA-004-Supabase-Security-Review.md`

Alcance aprobado:

- Quitar credenciales Supabase hardcodeadas del backend.
- Mantener `.env` ignorado y `.env.example` sin secretos.
- Evitar health checks Supabase acoplados al controller.
- Documentar el hallazgo de seguridad y la necesidad de rotar la service role key expuesta.
- No modificar Android.

Archivos principales:

- `backend/src/health/health.controller.ts`
- `backend/src/main.ts`
- `backend/.env.example`
- `backend/README.md`
- `docs/11-quality/QA-004-Supabase-Security-Review.md`

Verificacion:

- Scan local de archivos versionables para detectar tokens/keys hardcodeadas.
- Resultado: no se detectaron tokens Supabase ni URL del proyecto en archivos versionables; solo placeholders/documentacion.
- `npm.cmd run build` ejecutado en `backend/`.
- Resultado: build exitoso.

Uso previsto en la tesis:

- Capitulo 3: evidencia de control humano y correccion de riesgo durante integracion asistida por IA.
- Capitulo 4: evidencia de frontera backend segura para infraestructura Supabase.
- Capitulo 5: insumo para discutir seguridad, trazabilidad y calidad en iteraciones incrementales.

Evidencia pendiente:

- Rotar en Supabase la service role key que fue expuesta durante el spike local.
- Implementar cliente Supabase mediante adapter/env en `BACKEND-TASK-006`.

## BACKEND-TASK-004-TO-009-BACKEND-SUPABASE-BOUNDARY

Fecha: 2026-07-06.

Estado: implementado y verificado por build, tests, escaneo local de secretos y comprobacion real minima de Supabase Auth.

Origen documental:

- `docs/09-implementation/backend/BACKEND-SPRINT-001-Backend-Foundation.md`
- `docs/09-implementation/backend/BACKEND-SPRINT-002-Authentication-and-Supabase-Boundary.md`
- `docs/09-implementation/backend/BACKEND-TASK-004-Configure-Swagger-OpenAPI.md`
- `docs/09-implementation/backend/BACKEND-TASK-005-Add-Backend-Testing-Baseline.md`
- `docs/09-implementation/backend/BACKEND-TASK-006-Configure-Supabase-Client.md`
- `docs/09-implementation/backend/BACKEND-TASK-007-Implement-Authentication-Boundary.md`
- `docs/09-implementation/backend/BACKEND-TASK-008-Implement-User-Profile-Endpoints.md`
- `docs/09-implementation/backend/BACKEND-TASK-009-Define-RLS-Security-Assumptions.md`
- `docs/09-implementation/backend/API-001-Backend-API-Contract-Draft.md`
- `docs/09-implementation/backend/DB-001-Database-Schema-Planning.md`
- `docs/04-architecture/ARC-003-Backend-Architecture.md`
- `docs/04-architecture/ARC-007-Security-and-Privacy-Architecture.md`
- `docs/04-architecture/ADR-004-Backend-Strategy.md`
- `docs/11-quality/QA-004-Supabase-Security-Review.md`
- Supabase official documentation on Row Level Security and API keys.

Alcance aprobado:

- Completar la fundacion backend con Swagger/OpenAPI y baseline de tests.
- Configurar un adapter Supabase backend-only usando variables de entorno.
- Validar uso de claves nuevas `sb_secret_...` y evitar formatos legacy JWT.
- Implementar una frontera de autenticacion basada en bearer token y Supabase Auth.
- Implementar `GET /api/v1/me` como primer endpoint protegido.
- Documentar supuestos de RLS y modelo de propiedad de filas antes de expandir endpoints de dominio.
- No modificar Android en este bloque.

Archivos principales:

- `backend/src/main.ts`
- `backend/src/health/dto/health-response.dto.ts`
- `backend/src/auth/*`
- `backend/src/supabase/*`
- `backend/src/users/*`
- `backend/jest.config.js`
- `backend/README.md`
- `backend/package.json`
- `backend/package-lock.json`
- `docs/09-implementation/backend/SEC-001-RLS-Security-Assumptions.md`
- `docs/09-implementation/backend/SUPABASE-001-Project-Setup-Checklist.md`
- `docs/11-quality/QA-004-Supabase-Security-Review.md`

Verificacion:

- `npm.cmd run build` ejecutado en `backend/`.
- Resultado: build exitoso.
- `npm.cmd test` ejecutado en `backend/`.
- Resultado: 7 suites exitosas, 14 tests exitosos.
- Escaneo local de archivos versionables para detectar claves Supabase accionables.
- Resultado: 0 coincidencias accionables.
- Peticion real a Supabase Auth settings con publishable key local.
- Resultado: HTTP 200.
- Peticion real a Supabase REST root con secret key local.
- Resultado: HTTP 401; se interpreta como conectividad real confirmada, pero no como validacion de permisos/esquema.

Uso previsto en la tesis:

- Capitulo 3: evidencia de metodologia incremental, lectura documental obligatoria, aprobacion humana y control de seguridad.
- Capitulo 4: evidencia de construccion de la frontera Backend/API/Supabase y de separacion entre cliente Android e infraestructura.
- Capitulo 5: insumo para evaluar trazabilidad, reproducibilidad de tests, manejo de secretos y validacion parcial contra servicio real.

Evidencia pendiente:

- Ejecutar una validacion real contra tablas/policies cuando el schema y RLS esten aplicados.
- Capturar evidencia de Supabase Security Advisor.
- Registrar revision humana de `SEC-001` antes de considerar cerrada la politica de RLS.

## BACKEND-TASK-010-WASHER-ENDPOINTS

Fecha: 2026-07-06.

Estado: implementado y verificado por build, tests y escaneo local de secretos.

Origen documental:

- `docs/09-implementation/backend/BACKEND-SPRINT-003-Core-Domain-API.md`
- `docs/09-implementation/backend/BACKEND-TASK-010-Implement-Washer-Endpoints.md`
- `docs/09-implementation/backend/API-001-Backend-API-Contract-Draft.md`
- `docs/09-implementation/backend/SEC-001-RLS-Security-Assumptions.md`
- `docs/05-domain/entities/ENT-002-Washer.md`
- `docs/05-domain/requirements/REQ-002-Washer-Management.md`
- `docs/05-domain/use-cases/UC-002-Manage-Washer.md`

Alcance aprobado:

- Implementar endpoints protegidos para listar, crear, actualizar y eliminar lavarropas.
- Mantener Android sin cambios.
- Usar DTOs explicitos y validacion manual sin agregar dependencias nuevas.
- Filtrar cada query por `user_id` autenticado.

Archivos principales:

- `backend/src/washers/*`
- `backend/src/app.module.ts`
- `backend/README.md`

Verificacion:

- `npm.cmd run build` ejecutado en `backend/`.
- Resultado: build exitoso.
- `npm.cmd test` ejecutado en `backend/`.
- Resultado: tests exitosos.
- Escaneo local de archivos versionables para detectar claves Supabase accionables.
- Resultado: 0 coincidencias accionables.

Uso previsto en la tesis:

- Capitulo 4: evidencia de primer CRUD de dominio detras de la frontera backend/Supabase.
- Capitulo 5: insumo para evaluar autorizacion por usuario, modularidad y testabilidad.

Evidencia pendiente:

- Validar endpoints contra tablas reales cuando el schema `washers` este aplicado en Supabase.
- Agregar prueba de integracion HTTP cuando se defina una estrategia e2e.

## BACKEND-TASK-011-LAUNDRY-LOAD-ENDPOINTS

Fecha: 2026-07-06.

Estado: implementado y verificado por build, tests y escaneo local de secretos.

Origen documental:

- `docs/09-implementation/backend/BACKEND-SPRINT-003-Core-Domain-API.md`
- `docs/09-implementation/backend/BACKEND-TASK-011-Implement-Laundry-Load-Endpoints.md`
- `docs/09-implementation/backend/API-001-Backend-API-Contract-Draft.md`
- `docs/09-implementation/backend/SEC-001-RLS-Security-Assumptions.md`
- `docs/05-domain/entities/ENT-003-Laundry-Load.md`
- `docs/05-domain/entities/ENT-008-Washing-Program.md`
- `docs/05-domain/entities/ENT-009-Clothing-Type.md`
- `docs/05-domain/use-cases/UC-003-Create-Laundry-Load.md`

Alcance aprobado:

- Implementar endpoints protegidos para listar y crear cargas de ropa.
- Implementar actualizacion de estado de carga.
- Mantener Android sin cambios.
- Usar DTOs explicitos y validacion manual sin agregar dependencias nuevas.
- Filtrar cada query por `user_id` autenticado.

Archivos principales:

- `backend/src/laundry-loads/*`
- `backend/src/app.module.ts`
- `backend/README.md`

Verificacion:

- `npm.cmd run build` ejecutado en `backend/`.
- Resultado: build exitoso.
- `npm.cmd test` ejecutado en `backend/`.
- Resultado: tests exitosos.
- Escaneo local de archivos versionables para detectar claves Supabase accionables.
- Resultado: 0 coincidencias accionables.

Uso previsto en la tesis:

- Capitulo 4: evidencia de expansion del backend desde lavarropas hacia el evento central del dominio.
- Capitulo 5: insumo para evaluar trazabilidad entre entidades, contrato API y servicios backend.

Evidencia pendiente:

- Validar endpoints contra tablas reales cuando el schema `laundry_loads` este aplicado en Supabase.
- Conectar el flujo de prediccion cuando se implemente `BACKEND-TASK-014`.

## BACKEND-TASK-012-WEATHER-PROVIDER-ABSTRACTION

Fecha: 2026-07-06.

Estado: implementado y verificado por build, tests y escaneo local de secretos.

Origen documental:

- `docs/09-implementation/backend/BACKEND-SPRINT-003-Core-Domain-API.md`
- `docs/09-implementation/backend/BACKEND-TASK-012-Implement-Weather-Provider-Abstraction.md`
- `docs/09-implementation/backend/API-001-Backend-API-Contract-Draft.md`
- `docs/05-domain/entities/ENT-004-Weather-Snapshot.md`
- `docs/05-domain/requirements/REQ-003-Weather-Forecast-Integration.md`
- `docs/05-domain/business-rules/BR-003-Forecast-Freshness.md`

Alcance aprobado:

- Crear una abstraccion backend para proveedor meteorologico.
- Implementar un proveedor mock deterministico.
- Exponer endpoints protegidos de clima actual y forecast.
- No conectar proveedor meteorologico externo ni agregar claves nuevas.
- Mantener Android sin cambios.

Archivos principales:

- `backend/src/weather/*`
- `backend/src/app.module.ts`
- `backend/README.md`

Verificacion:

- `npm.cmd run build` ejecutado en `backend/`.
- Resultado: build exitoso.
- `npm.cmd test` ejecutado en `backend/`.
- Resultado: tests exitosos.
- Escaneo local de archivos versionables para detectar claves Supabase accionables.
- Resultado: 0 coincidencias accionables.

Uso previsto en la tesis:

- Capitulo 4: evidencia de preparacion del backend para integrar datos meteorologicos sin acoplar el contrato a un proveedor externo.
- Capitulo 5: insumo para evaluar testabilidad y reemplazabilidad de integraciones externas.

Evidencia pendiente:

- Seleccionar proveedor meteorologico real.
- Definir politica de cache/frescura concreta antes de consumir datos reales.

## BACKEND-TASK-013-API-DTOS-AND-VALIDATION

Fecha: 2026-07-06.

Estado: implementado y verificado por build, tests, generacion Swagger y escaneo local de secretos.

Origen documental:

- `docs/09-implementation/backend/BACKEND-TASK-013-Implement-API-DTOs-and-Validation.md`
- `docs/09-implementation/backend/API-001-Backend-API-Contract-Draft.md`
- `docs/09-implementation/backend/MOD-002-Backend-Module-Plan.md`
- `docs/04-architecture/ARC-003-Backend-Architecture.md`
- `docs/04-architecture/ADR-004-Backend-Strategy.md`
- `docs/11-quality/QA-001-Definition-of-Done.md`

Alcance aprobado:

- Consolidar la forma transversal de errores del backend.
- Mantener DTOs explicitos y validaciones manuales existentes sin agregar dependencias nuevas.
- Exponer el envelope `{ code, message, traceId }` definido por `API-001`.
- Registrar metadata Swagger para respuestas de error.
- No modificar Android ni infraestructura.

Archivos principales:

- `backend/src/common/api-error-code.ts`
- `backend/src/common/api-error-response.dto.ts`
- `backend/src/common/api-error-responses.decorator.ts`
- `backend/src/common/api-exception.filter.ts`
- `backend/src/common/api-exception.filter.spec.ts`
- `backend/src/main.ts`
- `backend/src/health/health.controller.ts`
- `backend/src/users/users.controller.ts`
- `backend/src/washers/washers.controller.ts`
- `backend/src/laundry-loads/laundry-loads.controller.ts`
- `backend/src/weather/weather.controller.ts`
- `backend/README.md`

Verificacion:

- `npm.cmd run build` ejecutado en `backend/`.
- Resultado: build exitoso.
- `npm.cmd test` ejecutado en `backend/`.
- Resultado: 17 suites exitosas, 42 tests exitosos.
- Generacion de documento Swagger ejecutada con variables Supabase dummy validas.
- Resultado: `SWAGGER_DOCUMENT=ok`.
- Escaneo local de archivos versionables para detectar claves Supabase accionables.
- Resultado: 0 coincidencias accionables.

Uso previsto en la tesis:

- Capitulo 4: evidencia de estabilizacion del contrato API y de manejo uniforme de errores antes de integrar nuevos modulos.
- Capitulo 5: insumo para evaluar calidad, mantenibilidad y trazabilidad del contrato backend consumido por Android.

Evidencia pendiente:

- Validar el envelope mediante pruebas HTTP e2e cuando se incorpore una estrategia de integracion.
- Revisar si se incorpora `class-validator` en una tarea posterior o si la validacion manual sigue siendo suficiente para el MVP.

## BACKEND-TASK-014-DRYING-PREDICTION-ENDPOINT

Fecha: 2026-07-06.

Estado: implementado y verificado por build, tests, generacion Swagger y escaneo local de secretos.

Origen documental:

- `docs/09-implementation/backend/BACKEND-TASK-014-Implement-Drying-Prediction-Endpoint.md`
- `docs/09-implementation/backend/API-001-Backend-API-Contract-Draft.md`
- `docs/09-implementation/backend/MOD-002-Backend-Module-Plan.md`
- `docs/04-architecture/ARC-003-Backend-Architecture.md`
- `docs/04-architecture/ADR-004-Backend-Strategy.md`
- `docs/05-domain/requirements/REQ-004-Drying-Prediction-Engine.md`
- `docs/05-domain/use-cases/UC-004-Calculate-Drying-Prediction.md`
- `docs/05-domain/business-rules/BR-001-Drying-Suitability-Score.md`
- `docs/05-domain/business-rules/BR-002-Rain-Risk-Override.md`
- `docs/05-domain/business-rules/BR-003-Forecast-Freshness.md`
- `docs/05-domain/business-rules/BR-005-Explainable-Recommendation.md`
- `docs/11-quality/QA-001-Definition-of-Done.md`

Alcance aprobado:

- Implementar `POST /api/v1/predictions/drying` como endpoint protegido.
- Crear DTOs explicitos para request y response.
- Validar campos requeridos y enums sin agregar dependencias nuevas.
- Orquestar la prediccion a partir del `WeatherService` backend.
- Usar una heuristica rule-based inicial alineada con el baseline Android.
- No persistir predicciones ni modificar Android.

Archivos principales:

- `backend/src/predictions/*`
- `backend/src/app.module.ts`
- `backend/src/weather/weather.module.ts`
- `backend/README.md`

Verificacion:

- `npm.cmd run build` ejecutado en `backend/`.
- Resultado: build exitoso.
- `npm.cmd test` ejecutado en `backend/`.
- Resultado: 21 suites exitosas, 50 tests exitosos.
- Generacion de documento Swagger ejecutada con variables Supabase dummy validas.
- Resultado: `SWAGGER_DOCUMENT=ok`.
- Escaneo local de archivos versionables para detectar claves Supabase accionables.
- Resultado: 0 coincidencias accionables.

Uso previsto en la tesis:

- Capitulo 4: evidencia de incorporacion del nucleo de recomendacion al backend sin exponer detalles de Supabase ni depender todavia de un proveedor meteorologico real.
- Capitulo 5: insumo para evaluar trazabilidad entre reglas de negocio, contrato API, tests y arquitectura backend.

Evidencia pendiente:

- Validar la heuristica con criterios meteorologicos externos antes de considerarla definitiva.
- Decidir si la prediccion se persiste junto con `laundry_loads` o permanece como calculo bajo demanda.
- Ejecutar pruebas HTTP e2e cuando se defina la estrategia de integracion.

## BACKEND-TASK-015-NOTIFICATION-SCHEDULING-BOUNDARY

Fecha: 2026-07-06.

Estado: implementado y verificado por build, tests, generacion Swagger y escaneo local de secretos.

Origen documental:

- `docs/09-implementation/backend/BACKEND-TASK-015-Implement-Notification-Scheduling-Boundary.md`
- `docs/09-implementation/backend/BACKEND-SPRINT-004-Prediction,-Notifications-and-Deployment-Readiness.md`
- `docs/09-implementation/backend/API-001-Backend-API-Contract-Draft.md`
- `docs/04-architecture/ARC-003-Backend-Architecture.md`
- `docs/04-architecture/ADR-004-Backend-Strategy.md`
- `docs/04-architecture/ARC-006-Notification-Architecture.md`
- `docs/05-domain/requirements/REQ-006-Notification-Management.md`
- `docs/05-domain/use-cases/UC-006-Receive-Rain-Alert.md`
- `docs/05-domain/business-rules/BR-004-Notification-Eligibility.md`
- `docs/05-domain/entities/ENT-006-Notification.md`
- `docs/11-quality/QA-001-Definition-of-Done.md`

Alcance aprobado:

- Implementar `POST /api/v1/notifications/register-device` como endpoint protegido.
- Crear DTOs explicitos para registro de dispositivo.
- Validar token, plataforma, proveedor push y opt-in sin agregar dependencias nuevas.
- Respetar `BR-004` marcando el registro como habilitado o deshabilitado segun opt-in.
- No implementar envio real de push, FCM, persistencia de tokens ni cambios Android.

Archivos principales:

- `backend/src/notifications/*`
- `backend/src/app.module.ts`
- `backend/README.md`

Verificacion:

- `npm.cmd run build` ejecutado en `backend/`.
- Resultado: build exitoso.
- `npm.cmd test` ejecutado en `backend/`.
- Resultado: 24 suites exitosas, 58 tests exitosos.
- Generacion de documento Swagger ejecutada con variables Supabase dummy validas.
- Resultado: `SWAGGER_DOCUMENT=ok`.
- Escaneo local de archivos versionables para detectar claves Supabase accionables.
- Resultado: 0 coincidencias accionables.

Uso previsto en la tesis:

- Capitulo 4: evidencia de implementacion staged para notificaciones, separando contrato backend de complejidad push real.
- Capitulo 5: insumo para evaluar trazabilidad entre regla de opt-in, contrato API y decisiones de simplicidad arquitectonica.

Evidencia pendiente:

- Definir persistencia de tokens y RLS antes de activar registro durable.
- Decidir proveedor push real y credenciales solo cuando exista tarea aprobada.
- Conectar Android al endpoint cuando se implemente el cliente remoto.

## BACKEND-TASK-016-DEPLOYMENT-AND-RELEASE-CHECKLIST

Fecha: 2026-07-07.

Estado: implementado y verificado por build, tests, generacion Swagger y escaneo local de secretos.

Origen documental:

- `docs/09-implementation/backend/BACKEND-TASK-016-Add-Deployment-and-Release-Checklist.md`
- `docs/09-implementation/backend/BACKEND-SPRINT-004-Prediction,-Notifications-and-Deployment-Readiness.md`
- `docs/09-implementation/backend/API-001-Backend-API-Contract-Draft.md`
- `docs/09-implementation/backend/MOD-002-Backend-Module-Plan.md`
- `docs/09-implementation/ENV-001-Local-Development-Environment.md`
- `docs/09-implementation/RELEASE-001-Release-Strategy.md`
- `docs/09-implementation/backend/SUPABASE-001-Project-Setup-Checklist.md`
- `docs/09-implementation/backend/SEC-001-RLS-Security-Assumptions.md`
- `docs/04-architecture/ARC-003-Backend-Architecture.md`
- `docs/04-architecture/ADR-004-Backend-Strategy.md`
- `docs/11-quality/QA-001-Definition-of-Done.md`

Alcance aprobado:

- Cerrar `BACKEND-SPRINT-004` con un checklist de readiness backend.
- Cubrir validacion local, preparacion staging y demo de tesis.
- Documentar variables de entorno, seguridad Supabase, evidencia de release y limitaciones conocidas.
- No seleccionar hosting, CI/CD, Docker, Terraform ni infraestructura nueva.
- No modificar Android ni cambiar versiones.

Archivos principales:

- `docs/09-implementation/backend/DEPLOY-001-Backend-Deployment-and-Release-Checklist.md`
- `docs/09-implementation/backend/BACKEND-SPRINT-TASK-INDEX.md`
- `backend/README.md`
- `tesis-final/evidence/increment-log.md`
- `tesis-final/chapters/04-desarrollo.md`

Verificacion:

- `npm.cmd run build` ejecutado en `backend/`.
- Resultado: build exitoso.
- `npm.cmd test` ejecutado en `backend/`.
- Resultado: tests exitosos.
- Generacion de documento Swagger ejecutada con variables Supabase dummy validas.
- Resultado: `SWAGGER_DOCUMENT=ok`.
- Escaneo local de archivos versionables para detectar claves Supabase accionables.
- Resultado: 0 coincidencias accionables.

Uso previsto en la tesis:

- Capitulo 4: evidencia de cierre de sprint backend mediante control de readiness y preparacion de demo.
- Capitulo 5: insumo para evaluar reproducibilidad, seguridad, trazabilidad y limitaciones reconocidas.

Evidencia pendiente:

- Ejecutar checklist contra un entorno staging/demo real cuando se seleccione hosting.
- Capturar evidencia HTTP/e2e sin secretos para la defensa.
- Definir canal de distribucion de backend y Android para la demo final.

## INTEGRATION-TASK-001-BACKEND-SUPABASE-ADAPTER

Fecha: 2026-07-07.

Estado: implementado y verificado por build, tests, generacion Swagger y escaneo local de secretos.

Origen documental:

- `docs/00-governance/APPROVAL-007-FR7-Baseline-Record.md`
- `docs/FR7-INDEX.md`
- `docs/README-FR7-MVP-DEPLOYMENT-INTEGRATION.md`
- `docs/09-implementation/integration/INTEGRATION-SPRINT-001-Vertical-Slice-MVP.md`
- `docs/09-implementation/integration/INTEGRATION-TASK-001-Backend-Supabase-Adapter.md`
- `docs/09-implementation/integration/INTEGRATION-002-Backend-Supabase-Adapter.md`
- `docs/09-implementation/backend/BACKEND-DEPLOY-001-Render-Railway-Deployment-Plan.md`
- `docs/09-implementation/MVP-DEPLOY-001-Deployed-MVP-Checklist.md`
- `docs/11-quality/QA-005-MVP-Deployment-Review-Gate.md`
- `docs/07-prompts/PROMPT-012-MVP-Integration-Execution.md`

Alcance aprobado:

- Validar el adapter Supabase centralizado dentro del backend.
- Agregar `GET /api/v1/health/supabase` como verificacion publica de conectividad.
- Mantener secretos fuera de respuestas, logs, Android y documentos.
- No desplegar todavia en Render/Railway.
- No modificar Android.

Archivos principales:

- `backend/src/health/dto/supabase-health-response.dto.ts`
- `backend/src/health/health.controller.ts`
- `backend/src/health/health.controller.spec.ts`
- `backend/src/health/health.module.ts`
- `backend/src/health/health.service.ts`
- `backend/src/health/health.service.spec.ts`
- `backend/src/supabase/supabase.service.ts`
- `backend/README.md`
- `docs/09-implementation/backend/DEPLOY-001-Backend-Deployment-and-Release-Checklist.md`

Verificacion:

- `npm.cmd run build` ejecutado en `backend/`.
- Resultado: build exitoso.
- `npm.cmd test` ejecutado en `backend/`.
- Resultado: 25 suites exitosas, 62 tests exitosos.
- Generacion de documento Swagger ejecutada con variables Supabase dummy validas.
- Resultado: `SWAGGER_DOCUMENT=ok`.
- Escaneo local de archivos versionables para detectar claves Supabase accionables.
- Resultado: 0 coincidencias accionables.

Uso previsto en la tesis:

- Capitulo 4: evidencia del inicio de FR7, donde la arquitectura pasa de backend local a readiness de integracion MVP.
- Capitulo 5: insumo para evaluar seguridad de secretos, centralizacion del adapter y preparacion para despliegue publico.

Evidencia pendiente:

- Ejecutar `GET /api/v1/health/supabase` contra entorno local con `.env` real y contra Render/Railway cuando exista URL publica.
- Capturar resultado de smoke test sin exponer secretos.

## INTEGRATION-TASK-002-BACKEND-LAUNDRY-API-USING-SUPABASE

Fecha: 2026-07-07.

Estado: implementado y verificado por build, tests, generacion Swagger y escaneo local de secretos.

Origen documental:

- `docs/FR7-INDEX.md`
- `docs/09-implementation/integration/INTEGRATION-SPRINT-001-Vertical-Slice-MVP.md`
- `docs/09-implementation/integration/INTEGRATION-TASK-002-Backend-Laundry-API-Using-Supabase.md`
- `docs/09-implementation/integration/INTEGRATION-002-Backend-Supabase-Adapter.md`
- `docs/09-implementation/MVP-DEPLOY-001-Deployed-MVP-Checklist.md`
- `docs/09-implementation/backend/DB-001-Database-Schema-Planning.md`
- `docs/09-implementation/backend/SEC-001-RLS-Security-Assumptions.md`
- `docs/11-quality/QA-005-MVP-Deployment-Review-Gate.md`
- `docs/07-prompts/PROMPT-012-MVP-Integration-Execution.md`

Alcance aprobado:

- Conectar y validar la API de cargas de ropa contra Supabase mediante un adapter/data source.
- Mantener el contrato HTTP existente sin modificar Android.
- Mantener `user_id` como restriccion de propiedad en operaciones de lectura, creacion y actualizacion.
- Ocultar errores Supabase detras de errores backend.
- No agregar dependencias ni exponer secretos.

Archivos principales:

- `backend/src/laundry-loads/laundry-loads.supabase-data-source.ts`
- `backend/src/laundry-loads/laundry-loads.supabase-data-source.spec.ts`
- `backend/src/laundry-loads/laundry-loads.service.ts`
- `backend/src/laundry-loads/laundry-loads.service.spec.ts`
- `backend/src/laundry-loads/laundry-loads.module.ts`
- `backend/README.md`

Verificacion:

- `npm.cmd run build` ejecutado en `backend/`.
- Resultado: build exitoso.
- `npm.cmd test` ejecutado en `backend/`.
- Resultado: 26 suites exitosas, 65 tests exitosos.
- Generacion de documento Swagger ejecutada con variables Supabase dummy validas.
- Resultado: `SWAGGER_DOCUMENT=ok`.
- Escaneo local de archivos versionables para detectar claves Supabase accionables.
- Resultado: 0 coincidencias accionables.

Uso previsto en la tesis:

- Capitulo 4: evidencia de que la capa backend avanza desde endpoints aislados hacia una integracion MVP con Supabase.
- Capitulo 5: insumo para evaluar separacion de responsabilidades, seguridad por usuario y mantenibilidad del adapter.

Evidencia pendiente:

- Ejecutar los endpoints de `laundry-loads` contra tablas reales y RLS aplicadas.
- Registrar smoke test HTTP sin exponer tokens.

## INTEGRATION-TASK-003-ANDROID-RETROFIT-CLIENT

Fecha: 2026-07-07.

Estado: implementado, verificado por tests unitarios Android/build debug y validado manualmente en flujo de email.

Origen documental:

- `docs/FR7-INDEX.md`
- `docs/09-implementation/integration/INTEGRATION-SPRINT-001-Vertical-Slice-MVP.md`
- `docs/09-implementation/integration/INTEGRATION-TASK-003-Android-Retrofit-Client.md`
- `docs/09-implementation/integration/INTEGRATION-003-Android-Retrofit-Integration.md`
- `docs/09-implementation/integration/INTEGRATION-001-Android-Backend-Environment.md`
- `docs/09-implementation/android/MOD-001-Android-Module-Plan.md`
- `docs/04-architecture/ARC-002-Android-Application-Architecture.md`
- `docs/07-prompts/PROMPT-012-MVP-Integration-Execution.md`
- `docs/11-quality/QA-005-MVP-Deployment-Review-Gate.md`

Alcance aprobado:

- Incorporar Retrofit, converter Gson y logging OkHttp en Android.
- Definir una URL base configurable por `BuildConfig`.
- Agregar permiso de Internet.
- Crear el contrato tipado para health checks backend.
- Verificar normalizacion de URL base y construccion del cliente.
- No conectar todavia el dashboard ni persistir datos remotos.

Archivos principales:

- `gradle/libs.versions.toml`
- `app/build.gradle.kts`
- `app/src/main/AndroidManifest.xml`
- `app/src/main/java/com/tesis_pro/tenderapp/data/remote/BackendConfig.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/data/remote/BackendApiClient.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/data/remote/TenderBackendApi.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/data/remote/dto/HealthResponseDto.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/data/remote/dto/SupabaseHealthResponseDto.kt`
- `app/src/test/java/com/tesis_pro/tenderapp/data/remote/BackendConfigTest.kt`
- `app/src/test/java/com/tesis_pro/tenderapp/data/remote/BackendApiClientTest.kt`
- `gradle.properties`

Verificacion:

- `.\gradlew.bat --no-daemon :app:testDebugUnitTest` ejecutado con `JAVA_HOME=E:\android\jbr`.
- Resultado: build de tests exitoso.
- `.\gradlew.bat --no-daemon :app:assembleDebug` ejecutado con `JAVA_HOME=E:\android\jbr`.
- Resultado: build debug exitoso.

Uso previsto en la tesis:

- Capitulo 4: evidencia del inicio de la integracion Android-backend, pasando de UI local/mock a una frontera remota tipada.
- Capitulo 5: insumo para evaluar trazabilidad entre backlog, arquitectura MVVM/repositorio y readiness de despliegue MVP.

Evidencia pendiente:

- Reemplazar la URL release placeholder por la URL publica aprobada de Render o Railway.
- Crear el data source remoto y conectarlo al repositorio Android en el siguiente incremento.
- Ejecutar smoke test end-to-end contra backend desplegado sin exponer credenciales.

## INTEGRATION-TASK-004-ANDROID-REPOSITORY-REMOTE-DATA-SOURCE

Fecha: 2026-07-07.

Estado: implementado y verificado por tests unitarios Android y build debug.

Origen documental:

- `docs/FR7-INDEX.md`
- `docs/09-implementation/integration/INTEGRATION-SPRINT-001-Vertical-Slice-MVP.md`
- `docs/09-implementation/integration/INTEGRATION-TASK-004-Android-Repository-Remote-Data-Source.md`
- `docs/04-architecture/ARC-004-Repository-and-Data-Flow.md`
- `docs/04-architecture/ARC-005-Offline-First-Synchronization.md`
- `docs/09-implementation/android/MOD-001-Android-Module-Plan.md`
- `docs/07-prompts/PROMPT-002-Repository-and-Data-Flow-Generation.md`
- `docs/07-prompts/PROMPT-012-MVP-Integration-Execution.md`
- `docs/11-quality/QA-001-Definition-of-Done.md`
- `docs/11-quality/QA-003-Implementation-Review-Gate.md`
- `docs/11-quality/QA-005-MVP-Deployment-Review-Gate.md`

Alcance aprobado:

- Extender el contrato Retrofit Android con endpoints protegidos de `laundry-loads`.
- Crear DTOs Android alineados con el contrato backend.
- Crear una fuente remota de datos para cargas de ropa.
- Ocultar Retrofit detras de un resultado remoto tipado.
- Mapear errores de red, autenticacion, backend y respuesta invalida hacia estados consumibles por capas superiores.
- No conectar todavia la UI ni hardcodear tokens.

Archivos principales:

- `app/src/main/java/com/tesis_pro/tenderapp/data/remote/TenderBackendApi.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/data/remote/RemoteDataResult.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/data/remote/RemoteLaundryDataSource.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/data/remote/dto/LaundryLoadDtos.kt`
- `app/src/test/java/com/tesis_pro/tenderapp/data/remote/RemoteLaundryDataSourceTest.kt`

Verificacion:

- `.\gradlew.bat --no-daemon :app:testDebugUnitTest` ejecutado con `JAVA_HOME=E:\android\jbr`.
- Resultado: build de tests exitoso.
- `.\gradlew.bat --no-daemon :app:assembleDebug` ejecutado con `JAVA_HOME=E:\android\jbr`.
- Resultado: build debug exitoso.

Uso previsto en la tesis:

- Capitulo 4: evidencia de la transicion desde cliente Retrofit generico hacia una frontera de datos remotos de dominio.
- Capitulo 5: insumo para evaluar separacion de responsabilidades, manejo de errores y preparacion del dashboard backend-driven.

Evidencia pendiente:

- Integrar la fuente remota con un repositorio Android que coordine cache local y backend.
- Incorporar obtencion real de token de sesion cuando el flujo de auth Android este aprobado.
- Conectar el dashboard al backend en `INTEGRATION-TASK-005`.

## INTEGRATION-TASK-005-DASHBOARD-CONSUMES-BACKEND

Fecha: 2026-07-07.

Estado: implementado y verificado por tests unitarios Android y build debug.

Origen documental:

- `docs/FR7-INDEX.md`
- `docs/09-implementation/integration/INTEGRATION-SPRINT-001-Vertical-Slice-MVP.md`
- `docs/09-implementation/integration/INTEGRATION-TASK-005-Dashboard-Consumes-Backend.md`
- `docs/04-architecture/ARC-002-Android-Application-Architecture.md`
- `docs/04-architecture/ARC-004-Repository-and-Data-Flow.md`
- `docs/06-design/UI-004-Dashboard-Screen-Specification.md`
- `docs/06-design/UI-006-Weather-Verdict-Card.md`
- `docs/07-prompts/PROMPT-003-Compose-Screen-Generation.md`
- `docs/07-prompts/PROMPT-012-MVP-Integration-Execution.md`
- `docs/11-quality/QA-001-Definition-of-Done.md`
- `docs/11-quality/QA-003-Implementation-Review-Gate.md`
- `docs/11-quality/QA-005-MVP-Deployment-Review-Gate.md`

Alcance aprobado:

- Hacer que el dashboard consuma una frontera de repositorio conectada al backend.
- Exponer estados `loading`, `error`, `empty` y `success` desde el ViewModel.
- Mantener Retrofit oculto detras de data source y repository.
- Mantener los composables como consumidores de estado, sin logica de red ni prediccion.
- No hardcodear tokens ni implementar auth Android completa.

Archivos principales:

- `app/src/main/java/com/tesis_pro/tenderapp/domain/repository/DashboardRepository.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/data/repository/BackendDashboardRepository.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/ui/screens/DashboardViewModel.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/ui/screens/DashboardScreen.kt`
- `app/src/test/java/com/tesis_pro/tenderapp/ui/screens/DashboardViewModelTest.kt`
- `app/src/test/java/com/tesis_pro/tenderapp/data/repository/BackendDashboardRepositoryTest.kt`

Verificacion:

- `.\gradlew.bat --no-daemon :app:testDebugUnitTest` ejecutado con `JAVA_HOME=E:\android\jbr`.
- Resultado: build de tests exitoso.
- `.\gradlew.bat --no-daemon :app:assembleDebug` ejecutado con `JAVA_HOME=E:\android\jbr`.
- Resultado: build debug exitoso.

Uso previsto en la tesis:

- Capitulo 4: evidencia de que el dashboard deja de ser puramente mock y pasa a depender de una frontera backend-aware.
- Capitulo 5: insumo para evaluar estados de UI, separacion MVVM/repositorio y avance hacia una demo vertical FR7.

Evidencia pendiente:

- Inyectar token real de sesion cuando auth Android este implementado.
- Validar el estado real contra backend local/desplegado y Supabase.
- Registrar smoke test end-to-end en `INTEGRATION-TASK-006`.

## INTEGRATION-TASK-006-END-TO-END-SMOKE-TEST

Fecha: 2026-07-07.

Estado: smoke test local registrado; smoke desde telefono fisico y backend desplegado pendiente.

Origen documental:

- `docs/FR7-INDEX.md`
- `docs/09-implementation/integration/INTEGRATION-SPRINT-001-Vertical-Slice-MVP.md`
- `docs/09-implementation/integration/INTEGRATION-TASK-006-End-to-End-Smoke-Test.md`
- `docs/09-implementation/integration/INTEGRATION-004-End-to-End-Smoke-Test.md`
- `docs/09-implementation/MVP-DEPLOY-001-Deployed-MVP-Checklist.md`
- `docs/09-implementation/backend/BACKEND-DEPLOY-001-Render-Railway-Deployment-Plan.md`
- `docs/07-prompts/PROMPT-012-MVP-Integration-Execution.md`
- `docs/11-quality/QA-005-MVP-Deployment-Review-Gate.md`

Alcance ejecutado:

- Verificar build y tests backend.
- Verificar tests unitarios Android y build debug.
- Levantar backend local con `backend/.env` sin exponer secretos.
- Consultar `GET /api/v1/health`.
- Consultar `GET /api/v1/health/supabase`.
- Registrar resultado de smoke test en evidencia de tesis.

Archivos principales:

- `tesis-final/evidence/smoke-tests/SMOKE-2026-07-07-FR7-LOCAL.md`
- `tesis-final/evidence/README.md`

Verificacion:

- `npm.cmd run build` ejecutado en `backend/`.
- Resultado: build exitoso.
- `npm.cmd test` ejecutado en `backend/`.
- Resultado: 26 suites exitosas, 65 tests exitosos.
- `.\gradlew.bat --no-daemon :app:testDebugUnitTest` ejecutado con `JAVA_HOME=E:\android\jbr`.
- Resultado: build de tests exitoso.
- `.\gradlew.bat --no-daemon :app:assembleDebug` ejecutado con `JAVA_HOME=E:\android\jbr`.
- Resultado: build debug exitoso.
- `GET http://127.0.0.1:3000/api/v1/health`.
- Resultado: HTTP 200, `status: ok`.
- `GET http://127.0.0.1:3000/api/v1/health/supabase`.
- Resultado: HTTP 200, `status: ok`, `message: null`.

Uso previsto en la tesis:

- Capitulo 4: evidencia de integracion local del vertical slice FR7.
- Capitulo 5: insumo para analizar la diferencia entre verificacion local automatizada y validacion desplegada desde telefono fisico.

Evidencia pendiente:

- Configurar URL publica real de Render o Railway.
- Validar health endpoints desde telefono fisico.
- Reemplazar URL release Android placeholder.
- Ejecutar la app contra backend desplegado y capturar resultado visible.

## INTEGRATION-TASK-006-RENDER-SMOKE-UPDATE

Fecha: 2026-07-07.

Estado: backend publico validado; Android release actualizado.

Origen documental:

- `docs/09-implementation/integration/INTEGRATION-TASK-006-End-to-End-Smoke-Test.md`
- `docs/09-implementation/integration/INTEGRATION-004-End-to-End-Smoke-Test.md`
- `docs/09-implementation/MVP-DEPLOY-001-Deployed-MVP-Checklist.md`
- `docs/11-quality/QA-005-MVP-Deployment-Review-Gate.md`

Alcance ejecutado:

- Validar endpoint publico `GET /api/v1/health`.
- Validar endpoint publico `GET /api/v1/docs`.
- Validar endpoint publico `GET /api/v1/health/supabase`.
- Actualizar URL release Android hacia Render.
- Verificar build Android debug y release.

Archivos principales:

- `app/build.gradle.kts`
- `tesis-final/evidence/smoke-tests/SMOKE-2026-07-07-FR7-RENDER.md`

Verificacion:

- `GET https://tesis-t85s.onrender.com/api/v1/health`.
- Resultado: HTTP 200, `status: ok`.
- `GET https://tesis-t85s.onrender.com/api/v1/docs`.
- Resultado: HTTP 200.
- `GET https://tesis-t85s.onrender.com/api/v1/health/supabase`.
- Resultado: HTTP 200, `status: ok`, `message: null`.
- `.\gradlew.bat --no-daemon :app:testDebugUnitTest` ejecutado con `JAVA_HOME=E:\android\jbr`.
- Resultado: build de tests exitoso.
- `.\gradlew.bat --no-daemon :app:assembleDebug` ejecutado con `JAVA_HOME=E:\android\jbr`.
- Resultado: build debug exitoso.
- `.\gradlew.bat --no-daemon :app:assembleRelease` ejecutado con `JAVA_HOME=E:\android\jbr`.
- Resultado: build release exitoso.

Uso previsto en la tesis:

- Capitulo 4: evidencia de despliegue publico backend, conectividad Supabase y actualizacion Android hacia URL real.
- Capitulo 5: evidencia para discutir verificacion incremental, despliegue remoto y cierre de divergencias entre local y Render.

Evidencia pendiente:

- Ejecucion desde telefono fisico o emulador con captura del dashboard.

## ANDROID-TASK-AUTH-001-SESSION-TOKEN-PROVIDER

Fecha: 2026-07-07.

Estado: implementado y verificado por tests unitarios Android y build debug.

Origen documental:

- `docs/09-implementation/BACKLOG-001-Product-Backlog.md`
- `docs/09-implementation/SPRINT-005-Sprint-04-Backend-and-Sync.md`
- `docs/09-implementation/integration/INTEGRATION-SPRINT-001-Vertical-Slice-MVP.md`
- `docs/05-domain/requirements/REQ-001-Authentication.md`
- `docs/04-architecture/ARC-007-Security-and-Privacy-Architecture.md`
- `docs/11-quality/QA-001-Definition-of-Done.md`

Alcance aprobado:

- Permitir que Android envie un access token bearer configurable al repositorio remoto.
- Mantener el comportamiento actual cuando no exista token configurado.
- No implementar login completo ni hardcodear credenciales.
- No exponer claves Supabase privilegiadas en Android.

Archivos principales:

- `app/build.gradle.kts`
- `app/src/main/java/com/tesis_pro/tenderapp/domain/repository/SessionTokenProvider.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/data/auth/BuildConfigSessionTokenProvider.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/ui/screens/DashboardViewModel.kt`
- `app/src/test/java/com/tesis_pro/tenderapp/data/auth/BuildConfigSessionTokenProviderTest.kt`

Verificacion:

- `.\gradlew.bat --no-daemon :app:testDebugUnitTest` ejecutado con `JAVA_HOME=E:\android\jbr`.
- Resultado: tests exitosos.
- `.\gradlew.bat --no-daemon :app:assembleDebug` ejecutado con `JAVA_HOME=E:\android\jbr`.
- Resultado: build debug exitoso.

Uso previsto en la tesis:

- Capitulo 4: evidencia de respuesta incremental ante una prueba real donde Android alcanza el backend pero el endpoint protegido rechaza la peticion por autenticacion.
- Capitulo 5: insumo para evaluar higiene de secretos, separacion de responsabilidades y avance controlado hacia auth real.

Evidencia pendiente:

- Probar desde telefono fisico con un access token valido de Supabase Auth.
- Reemplazar el proveedor temporal por un flujo de login persistente cuando se apruebe PB-004 completo.

## ANDROID-TASK-AUTH-002-AUTH-UI-SHELL

Fecha: 2026-07-07.

Estado: implementado y verificado por tests unitarios Android y build debug.

Origen documental:

- `docs/09-implementation/BACKLOG-001-Product-Backlog.md`
- `docs/09-implementation/SPRINT-001-Four-Month-Delivery-Plan.md`
- `docs/06-design/FLOW-002-Onboarding-and-Authentication-Flow.md`
- `docs/05-domain/requirements/REQ-001-Authentication.md`
- `docs/05-domain/use-cases/UC-001-Authenticate-User.md`
- `docs/05-domain/requirements/NFR-003-Privacy-and-Security.md`
- `docs/04-architecture/ARC-002-Android-Application-Architecture.md`
- `docs/04-architecture/ARC-007-Security-and-Privacy-Architecture.md`
- `docs/09-implementation/backend/API-001-Backend-API-Contract-Draft.md`

Alcance aprobado:

- Agregar una primera pantalla Android de sesion accesible desde Ajustes.
- Capturar email y password en estado de ViewModel.
- Mostrar si existe token de smoke configurado.
- No autenticar todavia contra Supabase ni hardcodear credenciales.
- No agregar una pestana nueva a la navegacion principal.

Archivos principales:

- `app/src/main/java/com/tesis_pro/tenderapp/ui/screens/AuthScreen.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/ui/screens/AuthViewModel.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/ui/screens/SettingsScreen.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/ui/TenderApp.kt`
- `app/src/test/java/com/tesis_pro/tenderapp/ui/screens/AuthViewModelTest.kt`

Verificacion:

- `.\gradlew.bat --no-daemon :app:testDebugUnitTest` ejecutado con `JAVA_HOME=E:\android\jbr`.
- Resultado: tests exitosos.
- `.\gradlew.bat --no-daemon :app:assembleDebug` ejecutado con `JAVA_HOME=E:\android\jbr`.
- Resultado: build debug exitoso.
- Escaneo de secretos en `app/`.
- Resultado: sin coincidencias.

Uso previsto en la tesis:

- Capitulo 4: evidencia de avance gradual desde token temporal hacia una experiencia de autenticacion controlada.
- Capitulo 5: insumo para evaluar alcance incremental, UX minima y preservacion de la seguridad antes de integrar login real.

Evidencia pendiente:

- Implementar servicio real de autenticacion y persistencia segura de sesion.
- Capturar pantalla de la vista de sesion en telefono o emulador.

## ANDROID-TASK-AUTH-003-AUTH-REPOSITORY-BOUNDARY

Fecha: 2026-07-07.

Estado: implementado y verificado por tests unitarios Android y build debug.

Origen documental:

- `docs/FR7-INDEX.md`
- `docs/09-implementation/BACKLOG-001-Product-Backlog.md`
- `docs/05-domain/requirements/REQ-001-Authentication.md`
- `docs/05-domain/use-cases/UC-001-Authenticate-User.md`
- `docs/04-architecture/ARC-002-Android-Application-Architecture.md`
- `docs/04-architecture/ARC-007-Security-and-Privacy-Architecture.md`
- `docs/09-implementation/backend/API-001-Backend-API-Contract-Draft.md`

Alcance aprobado:

- Crear una frontera `AuthRepository` en Android.
- Modelar un resultado de autenticacion tipado y una sesion minima.
- Conectar `AuthViewModel` a la frontera de repositorio.
- Mantener una implementacion `SmokeAuthRepository` reemplazable para validar el token de smoke configurado.
- No implementar todavia Supabase Auth real ni persistencia segura.

Archivos principales:

- `app/src/main/java/com/tesis_pro/tenderapp/domain/model/AuthSession.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/domain/repository/AuthRepository.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/data/auth/SmokeAuthRepository.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/ui/screens/AuthViewModel.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/ui/screens/AuthScreen.kt`
- `app/src/test/java/com/tesis_pro/tenderapp/data/auth/SmokeAuthRepositoryTest.kt`
- `app/src/test/java/com/tesis_pro/tenderapp/ui/screens/AuthViewModelTest.kt`

Verificacion:

- `.\gradlew.bat --no-daemon :app:testDebugUnitTest` ejecutado con `JAVA_HOME=E:\android\jbr`.
- Resultado: tests exitosos.
- `.\gradlew.bat --no-daemon :app:assembleDebug` ejecutado con `JAVA_HOME=E:\android\jbr`.
- Resultado: build debug exitoso.
- Escaneo de secretos en `app/`.
- Resultado: sin coincidencias.

Uso previsto en la tesis:

- Capitulo 4: evidencia de separacion entre UI de autenticacion, dominio y data layer antes de integrar un proveedor real.
- Capitulo 5: insumo para evaluar como el sistema documental evita acoplar credenciales o detalles de Supabase a la interfaz.

Evidencia pendiente:

- Decidir e implementar el proveedor real de autenticacion.
- Persistir la sesion de forma segura cuando se apruebe el mecanismo definitivo.

## ANDROID-AUTH-MVP-TASK-PLAN

Fecha: 2026-07-07.

Estado: implementado como actualizacion documental de alcance MVP.

Origen documental:

- `docs/09-implementation/BACKLOG-001-Product-Backlog.md`
- `docs/09-implementation/MVP-001-MVP-Scope-Definition.md`
- `docs/06-design/FLOW-002-Onboarding-and-Authentication-Flow.md`
- `docs/09-implementation/backend/SUPABASE-001-Project-Setup-Checklist.md`
- `docs/FR7-INDEX.md`
- `docs/05-domain/requirements/REQ-001-Authentication.md`
- `docs/05-domain/use-cases/UC-001-Authenticate-User.md`

Alcance aprobado:

- Desglosar PB-004 en tareas MVP concretas.
- Incluir sign in, registro, estado de verificacion de email, recuperacion de contrasena, sign out, persistencia segura y smoke test protegido.
- Declarar email/password como alcance MVP y Google OAuth como opcional.
- Mantener la regla de no exponer service-role keys ni secretos backend en Android.

Archivos principales:

- `docs/09-implementation/BACKLOG-001-Product-Backlog.md`
- `docs/09-implementation/MVP-001-MVP-Scope-Definition.md`
- `docs/06-design/FLOW-002-Onboarding-and-Authentication-Flow.md`
- `docs/09-implementation/backend/SUPABASE-001-Project-Setup-Checklist.md`
- `docs/09-implementation/android/ANDROID-AUTH-001-MVP-Auth-Task-Plan.md`
- `docs/FR7-INDEX.md`

Verificacion:

- Revision documental local.
- No se ejecuto build porque el cambio no modifica codigo.

Uso previsto en la tesis:

- Capitulo 4: evidencia de ajuste de alcance a partir de la deteccion de una brecha entre REQ-001 y las tareas ejecutables.
- Capitulo 5: insumo para evaluar trazabilidad, control de alcance y rol de la revision humana en el sistema documental.

Evidencia pendiente:

- Implementar `ANDROID-AUTH-003` con configuracion publica de proveedor.
- Ejecutar smoke test real de registro, verificacion, login y llamada protegida al backend.

## ANDROID-AUTH-003-PUBLIC-PROVIDER-CONFIG

Fecha: 2026-07-07.

Estado: implementado y verificado por tests unitarios Android, build debug y escaneo de secretos.

Origen documental:

- `docs/09-implementation/BACKLOG-001-Product-Backlog.md`
- `docs/09-implementation/android/ANDROID-AUTH-001-MVP-Auth-Task-Plan.md`
- `docs/09-implementation/backend/SUPABASE-001-Project-Setup-Checklist.md`
- `docs/04-architecture/ARC-007-Security-and-Privacy-Architecture.md`
- `docs/05-domain/requirements/NFR-003-Privacy-and-Security.md`

Alcance aprobado:

- Agregar configuracion publica de Supabase Auth en Android.
- Leer `SUPABASE_URL` y `SUPABASE_PUBLISHABLE_KEY` desde Gradle properties o variables de entorno.
- Mantener valores vacios por defecto para no versionar datos del proyecto.
- Validar que la URL publica use HTTPS.
- No agregar SDK ni login real todavia.
- No exponer service-role keys ni secretos backend en Android.

Archivos principales:

- `app/build.gradle.kts`
- `app/src/main/java/com/tesis_pro/tenderapp/data/auth/SupabaseAuthConfig.kt`
- `app/src/test/java/com/tesis_pro/tenderapp/data/auth/SupabaseAuthConfigTest.kt`
- `docs/09-implementation/android/ANDROID-AUTH-001-MVP-Auth-Task-Plan.md`

Verificacion:

- `.\gradlew.bat --no-daemon :app:testDebugUnitTest` ejecutado con `JAVA_HOME=E:\android\jbr`.
- Resultado: tests exitosos.
- `.\gradlew.bat --no-daemon :app:assembleDebug` ejecutado con `JAVA_HOME=E:\android\jbr`.
- Resultado: build debug exitoso.
- Escaneo de secretos en `app/`, `docs/` y `tesis-final/`.
- Resultado: sin secretos nuevos; solo menciones documentales conocidas a claves backend.

Uso previsto en la tesis:

- Capitulo 4: evidencia de preparacion segura para conectar Android con Supabase Auth.
- Capitulo 5: insumo para evaluar higiene de secretos y separacion entre configuracion publica y credenciales privilegiadas.

Evidencia pendiente:

- Cargar valores reales mediante entorno local o configuracion segura del build.
- Implementar `ANDROID-AUTH-004` para inicio de sesion real.

## IMPLEMENTATION-STAGES-001-PROTOTYPE-MVP-MAP

Fecha: 2026-07-07.

Estado: implementado como reorganizacion documental de `09-implementation`.

Origen documental:

- `docs/09-implementation/MVP-001-MVP-Scope-Definition.md`
- `docs/09-implementation/BACKLOG-001-Product-Backlog.md`
- `docs/09-implementation/SPRINT-001-Four-Month-Delivery-Plan.md`
- `docs/09-implementation/IMPL-000-Implementation-Foundation-Overview.md`
- `docs/FR7-INDEX.md`

Alcance aprobado:

- Reordenar la implementacion por etapas de prototipado incremental.
- Mantener el mockup como primera etapa del MVP.
- Mapear artefactos existentes a PROTO-001, PROTO-002, PROTO-003 y MVP-DEMO.
- Evitar mover archivos para no romper referencias existentes.
- Definir lanes de trabajo: Android UI, Android Data, Backend API, Supabase, Infrastructure, Quality y Thesis Evidence.

Archivos principales:

- `docs/09-implementation/IMPLEMENTATION-STAGES-001-Prototype-and-MVP-Stage-Map.md`
- `docs/09-implementation/SPRINT-001-Four-Month-Delivery-Plan.md`
- `docs/09-implementation/IMPL-000-Implementation-Foundation-Overview.md`
- `docs/FR7-INDEX.md`

Verificacion:

- Revision documental local.
- No se ejecuto build porque el cambio no modifica codigo.

Uso previsto en la tesis:

- Capitulo 3: evidencia del sistema documental como mecanismo de replanificacion incremental.
- Capitulo 4: evidencia de que el desarrollo del caso de uso se organiza en prototipos sucesivos hasta la demo funcional.
- Capitulo 5: insumo para evaluar trazabilidad, control de alcance y continuidad entre mockup, backend y Supabase.

Evidencia pendiente:

- Capturar screenshots por etapa de prototipo.
- Actualizar el plan si aparecen nuevas pantallas MVP no cubiertas por PROTO-001.

## ANDROID-AUTH-004-EMAIL-PASSWORD-SIGN-IN

Fecha: 2026-07-07.

Estado: implementado y verificado por tests unitarios Android, build debug y escaneo de secretos. Smoke test real con Supabase pendiente de credenciales publicas y cuenta de prueba.

Origen documental:

- `docs/09-implementation/BACKLOG-001-Product-Backlog.md`
- `docs/09-implementation/android/ANDROID-AUTH-001-MVP-Auth-Task-Plan.md`
- `docs/09-implementation/backend/SUPABASE-001-Project-Setup-Checklist.md`
- `docs/04-architecture/ARC-007-Security-and-Privacy-Architecture.md`
- `docs/05-domain/requirements/REQ-001-Authentication.md`
- Supabase Docs: password-based auth and `signInWithPassword`.

Alcance aprobado:

- Implementar inicio de sesion email/password contra Supabase Auth usando configuracion publica.
- Mantener la integracion detras de `AuthRepository`.
- Aislar DTOs Supabase en capa `data/auth`.
- Mapear token de acceso, refresh token, expiracion y estado de verificacion de email hacia `AuthSession`.
- Manejar configuracion faltante, credenciales invalidas, red no disponible y errores desconocidos.
- No persistir sesion todavia.
- No exponer service-role keys ni secretos backend en Android.

Archivos principales:

- `app/src/main/java/com/tesis_pro/tenderapp/domain/model/AuthSession.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/domain/repository/AuthRepository.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/data/auth/SupabaseAuthApi.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/data/auth/SupabaseAuthApiClient.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/data/auth/SupabaseAuthRepository.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/data/auth/dto/SupabaseAuthDtos.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/ui/screens/AuthViewModel.kt`
- `app/src/test/java/com/tesis_pro/tenderapp/data/auth/SupabaseAuthRepositoryTest.kt`
- `app/src/test/java/com/tesis_pro/tenderapp/ui/screens/AuthViewModelTest.kt`

Verificacion:

- `.\gradlew.bat --no-daemon :app:testDebugUnitTest` ejecutado con `JAVA_HOME=E:\android\jbr`.
- Resultado: tests exitosos.
- `.\gradlew.bat --no-daemon :app:assembleDebug` ejecutado con `JAVA_HOME=E:\android\jbr`.
- Resultado: build debug exitoso.
- Escaneo de secretos en `app/`, `docs/` y `tesis-final/`.
- Resultado: sin secretos nuevos; solo menciones documentales conocidas a claves backend.

Uso previsto en la tesis:

- Capitulo 4: evidencia del paso desde configuracion publica hacia autenticacion real detras de una frontera de repositorio.
- Capitulo 5: insumo para evaluar separacion de responsabilidades, testabilidad y seguridad de secretos.

Evidencia pendiente:

- Ejecutar sign in real con `SUPABASE_URL`, `SUPABASE_PUBLISHABLE_KEY` y una cuenta de prueba.
- Implementar registro, verificacion de email y recuperacion de contrasena.

## ANDROID-AUTH-005-REGISTRATION-EMAIL-VERIFICATION-STATE

Fecha: 2026-07-07.

Estado: implementado y verificado por tests unitarios Android, build debug y escaneo de secretos. Validacion real con cuenta de prueba pendiente.

Origen documental:

- `docs/09-implementation/android/ANDROID-AUTH-001-MVP-Auth-Task-Plan.md`
- `docs/04-architecture/ARC-002-Android-Application-Architecture.md`
- `docs/04-architecture/ARC-004-Repository-and-Data-Flow.md`
- `docs/11-quality/QA-001-Definition-of-Done.md`
- Supabase Docs: password-based auth and `signUp`.

Alcance aprobado:

- Agregar registro email/password al contrato `AuthRepository`.
- Implementar `POST /auth/v1/signup` detras de la frontera Retrofit.
- Representar el resultado de registro pendiente de verificacion sin inventar una sesion local.
- Permitir alternar la pantalla Android entre inicio de sesion y registro.
- Mantener la persistencia de sesion y recuperacion de contrasena fuera de este incremento.

Archivos principales:

- `app/src/main/java/com/tesis_pro/tenderapp/domain/repository/AuthRepository.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/data/auth/SupabaseAuthApi.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/data/auth/SupabaseAuthRepository.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/data/auth/dto/SupabaseAuthDtos.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/ui/screens/AuthScreen.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/ui/screens/AuthViewModel.kt`
- `app/src/test/java/com/tesis_pro/tenderapp/data/auth/SupabaseAuthRepositoryTest.kt`
- `app/src/test/java/com/tesis_pro/tenderapp/ui/screens/AuthViewModelTest.kt`

Verificacion:

- `.\gradlew.bat --no-daemon :app:testDebugUnitTest` ejecutado con `JAVA_HOME=E:\android\jbr`.
- Resultado: tests exitosos.
- `.\gradlew.bat --no-daemon :app:assembleDebug` ejecutado con `JAVA_HOME=E:\android\jbr`.
- Resultado: build debug exitoso.
- Escaneo de secretos en `app/`, `docs/` y `tesis-final/`.
- Resultado: sin secretos nuevos; solo menciones documentales conocidas a claves backend.

Uso previsto en la tesis:

- Capitulo 4: evidencia de evolucion incremental del flujo de autenticacion desde login hacia registro verificable.
- Capitulo 5: insumo para evaluar como el sistema documental separa proveedor externo, estado UI, repositorio y seguridad de credenciales.

Evidencia pendiente:

- Crear o usar una cuenta de prueba real para registrar, verificar email e iniciar sesion.
- Implementar recuperacion de contrasena.

## ANDROID-AUTH-006-PASSWORD-RECOVERY-REQUEST

Fecha: 2026-07-07.

Estado: implementado y verificado por tests unitarios Android, build debug y escaneo de secretos. Smoke test real con proveedor pendiente.

Origen documental:

- `docs/09-implementation/android/ANDROID-AUTH-001-MVP-Auth-Task-Plan.md`
- `docs/04-architecture/ARC-002-Android-Application-Architecture.md`
- `docs/04-architecture/ARC-004-Repository-and-Data-Flow.md`
- `docs/11-quality/QA-001-Definition-of-Done.md`
- Supabase Docs: `resetPasswordForEmail`.

Alcance aprobado:

- Agregar solicitud de recuperacion de contrasena al contrato `AuthRepository`.
- Implementar la llamada de recuperacion detras de la frontera Retrofit/Supabase Auth.
- Agregar modo de pantalla para pedir el email sin requerir password.
- Registrar el resultado como email de recuperacion enviado.
- No implementar todavia cambio efectivo de password, deep links ni persistencia de sesion.

Archivos principales:

- `app/src/main/java/com/tesis_pro/tenderapp/domain/repository/AuthRepository.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/data/auth/SupabaseAuthApi.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/data/auth/SupabaseAuthRepository.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/data/auth/dto/SupabaseAuthDtos.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/ui/screens/AuthScreen.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/ui/screens/AuthViewModel.kt`
- `app/src/test/java/com/tesis_pro/tenderapp/data/auth/SupabaseAuthRepositoryTest.kt`
- `app/src/test/java/com/tesis_pro/tenderapp/ui/screens/AuthViewModelTest.kt`

Verificacion:

- `.\gradlew.bat --no-daemon :app:testDebugUnitTest` ejecutado con `JAVA_HOME=E:\android\jbr`.
- Resultado: tests exitosos.
- `.\gradlew.bat --no-daemon :app:assembleDebug` ejecutado con `JAVA_HOME=E:\android\jbr`.
- Resultado: build debug exitoso.
- Escaneo de secretos en `app/`, `docs/` y `tesis-final/`.
- Resultado: sin secretos nuevos; solo menciones documentales conocidas a claves backend.

Uso previsto en la tesis:

- Capitulo 4: evidencia de ampliacion gradual del flujo de autenticacion MVP.
- Capitulo 5: insumo para evaluar control de alcance, trazabilidad de fuentes tecnicas y separacion entre UI, ViewModel y repositorio.

Evidencia pendiente:

- Ejecutar provider smoke test con una cuenta real y confirmar recepcion del email.
- Definir deep link o mecanismo de retorno antes de implementar cambio efectivo de password.

## ANDROID-AUTH-007-SESSION-PERSISTENCE-SIGN-OUT

Fecha: 2026-07-07.

Estado: implementado y verificado por tests unitarios Android y build debug. Smoke test de sign out en dispositivo pendiente.

Origen documental:

- `docs/09-implementation/android/ANDROID-AUTH-001-MVP-Auth-Task-Plan.md`
- `docs/04-architecture/ARC-002-Android-Application-Architecture.md`
- `docs/04-architecture/ARC-004-Repository-and-Data-Flow.md`
- `docs/11-quality/QA-001-Definition-of-Done.md`
- Android Developers: `Cryptography` and `EncryptedSharedPreferences`.

Alcance aprobado:

- Agregar frontera `AuthSessionStore` para leer, guardar y limpiar sesiones.
- Persistir email, access token, refresh token, expiracion y estado de verificacion.
- Usar almacenamiento cifrado Android para la sesion local.
- Restaurar el estado de sesion al abrir la pantalla de autenticacion.
- Limpiar la sesion local al ejecutar sign out.
- Permitir que el dashboard use el token persistido y mantenga el token configurable como fallback de smoke test.

Archivos principales:

- `gradle/libs.versions.toml`
- `app/build.gradle.kts`
- `app/src/main/java/com/tesis_pro/tenderapp/domain/repository/AuthSessionStore.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/data/auth/SecureAuthSessionStore.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/data/auth/CompositeSessionTokenProvider.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/ui/screens/AuthScreen.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/ui/screens/AuthViewModel.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/ui/screens/DashboardScreen.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/ui/screens/DashboardViewModel.kt`
- `app/src/test/java/com/tesis_pro/tenderapp/ui/screens/AuthViewModelTest.kt`

Verificacion:

- `.\gradlew.bat --no-daemon :app:testDebugUnitTest` ejecutado con `JAVA_HOME=E:\android\jbr`.
- Resultado: tests exitosos.
- `.\gradlew.bat --no-daemon :app:assembleDebug` ejecutado con `JAVA_HOME=E:\android\jbr`.
- Resultado: build debug exitoso.
- Escaneo de secretos en `app/`, `docs/` y `tesis-final/`.
- Resultado: sin secretos nuevos; solo menciones documentales conocidas a claves backend.

Observaciones:

- La implementacion usa AndroidX Security Crypto como solucion MVP.
- La API `EncryptedSharedPreferences` compila con warnings de deprecacion; se conserva como decision temporal documentada y revisable antes de produccion.

Uso previsto en la tesis:

- Capitulo 4: evidencia de transicion desde sesion en memoria hacia persistencia local protegida.
- Capitulo 5: insumo para evaluar trazabilidad de deuda tecnica, seguridad incremental y documentacion de decisiones asistidas por IA.

Evidencia pendiente:

- Smoke test en telefono fisico: iniciar sesion, cerrar app, reabrir, validar dashboard protegido y ejecutar sign out.
- Revisar alternativa no deprecated para almacenamiento seguro antes de una version productiva.

## ANDROID-AUTH-008-BACKEND-TOKEN-PROPAGATION

Fecha: 2026-07-07.

Estado: implementado y verificado por tests unitarios Android, build debug y escaneo de secretos. Evidencia end-to-end pendiente.

Origen documental:

- `docs/09-implementation/android/ANDROID-AUTH-001-MVP-Auth-Task-Plan.md`
- `docs/09-implementation/BACKLOG-001-Product-Backlog.md`
- `docs/04-architecture/ARC-004-Repository-and-Data-Flow.md`
- `docs/11-quality/QA-003-Implementation-Review-Gate.md`

Alcance aprobado:

- Verificar que el dashboard use el token de sesion disponible para llamadas protegidas.
- Mantener el fallback de token configurable solo como soporte de smoke test.
- Cubrir el encadenamiento de proveedores de token con pruebas unitarias.
- No ejecutar aun el smoke test completo de registro, verificacion, login y llamada protegida.

Archivos principales:

- `app/src/main/java/com/tesis_pro/tenderapp/data/auth/CompositeSessionTokenProvider.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/data/repository/BackendDashboardRepository.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/data/remote/RemoteLaundryDataSource.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/ui/screens/DashboardViewModel.kt`
- `app/src/test/java/com/tesis_pro/tenderapp/data/auth/CompositeSessionTokenProviderTest.kt`
- `app/src/test/java/com/tesis_pro/tenderapp/data/repository/BackendDashboardRepositoryTest.kt`
- `app/src/test/java/com/tesis_pro/tenderapp/data/remote/RemoteLaundryDataSourceTest.kt`

Verificacion:

- `.\gradlew.bat --no-daemon :app:testDebugUnitTest` ejecutado con `JAVA_HOME=E:\android\jbr`.
- Resultado: tests exitosos.
- `.\gradlew.bat --no-daemon :app:assembleDebug` ejecutado con `JAVA_HOME=E:\android\jbr`.
- Resultado: build debug exitoso.
- Escaneo de secretos en `app/`, `docs/` y `tesis-final/`.
- Resultado: sin secretos nuevos; solo menciones documentales conocidas a claves backend.

Uso previsto en la tesis:

- Capitulo 4: evidencia de integracion entre autenticacion Android y consumo protegido del backend.
- Capitulo 5: insumo para evaluar trazabilidad entre backlog, arquitectura de repositorios y pruebas.

Evidencia pendiente:

- Ejecutar `QA-AUTH-001`: registrar usuario, verificar email, iniciar sesion y validar dashboard protegido contra backend desplegado.

## ANDROID-AUTH-008A-AUTH-FIRST-NAVIGATION-GATE

Fecha: 2026-07-07.

Estado: implementado y verificado por tests unitarios Android, build debug y escaneo de secretos.

Origen documental:

- `docs/09-implementation/android/ANDROID-AUTH-001-MVP-Auth-Task-Plan.md`
- `docs/09-implementation/BACKLOG-001-Product-Backlog.md`
- Revision humana del flujo inicial de la aplicacion.

Alcance aprobado:

- Hacer que una instalacion sin sesion local persistida abra primero la pantalla de autenticacion.
- Ocultar la navegacion principal mientras la ruta activa es `auth`.
- Agregar una accion `Continue` para entrar al dashboard despues de iniciar sesion o restaurar sesion.
- No modificar reglas de backend ni persistencia de sesion.

Archivos principales:

- `app/src/main/java/com/tesis_pro/tenderapp/ui/TenderApp.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/ui/screens/AuthScreen.kt`

Verificacion:

- `.\gradlew.bat --no-daemon :app:testDebugUnitTest` ejecutado con `JAVA_HOME=E:\android\jbr`.
- Resultado: tests exitosos.
- `.\gradlew.bat --no-daemon :app:assembleDebug` ejecutado con `JAVA_HOME=E:\android\jbr`.
- Resultado: build debug exitoso.
- Escaneo de secretos en `app/`, `docs/` y `tesis-final/`.
- Resultado: sin secretos nuevos; solo menciones documentales conocidas a claves backend.

Uso previsto en la tesis:

- Capitulo 4: evidencia de revision humana durante la integracion del flujo MVP.
- Capitulo 5: insumo para evaluar como el sistema documental absorbe feedback de usabilidad durante el desarrollo asistido por IA.

## ANDROID-AUTH-008B-SUPABASE-LOCAL-CONFIG-POLISH

Fecha: 2026-07-07.

Estado: implementado y verificado por tests unitarios Android, build debug y escaneo de secretos.

Origen documental:

- `docs/09-implementation/ENV-001-Local-Development-Environment.md`
- `docs/09-implementation/android/ANDROID-AUTH-001-MVP-Auth-Task-Plan.md`
- Revision humana posterior al spike de conexion real con Supabase.

Alcance aprobado:

- Remover hardcodeos temporales de `SUPABASE_URL` y `SUPABASE_PUBLISHABLE_KEY` del build Android.
- Remover logs de debug de configuracion publica en `SupabaseAuthRepository`.
- Permitir que Android lea valores publicos Supabase desde Gradle properties, variables de entorno o `local.properties` ignorado.
- Mantener prohibido versionar secretos o service-role keys.

Archivos principales:

- `app/build.gradle.kts`
- `app/src/main/java/com/tesis_pro/tenderapp/data/auth/SupabaseAuthRepository.kt`
- `docs/09-implementation/ENV-001-Local-Development-Environment.md`
- `docs/09-implementation/android/ANDROID-AUTH-001-MVP-Auth-Task-Plan.md`

Verificacion:

- `.\gradlew.bat --no-daemon :app:testDebugUnitTest` ejecutado con `JAVA_HOME=E:\android\jbr`.
- Resultado: tests exitosos.
- `.\gradlew.bat --no-daemon :app:assembleDebug` ejecutado con `JAVA_HOME=E:\android\jbr`.
- Resultado: build debug exitoso.
- Escaneo de secretos en `app/`, `docs/` y `tesis-final/`.
- Resultado: sin credenciales reales nuevas; solo placeholders de test y menciones documentales conocidas.

Uso previsto en la tesis:

- Capitulo 4: evidencia de conversion de un spike manual con credenciales hacia configuracion local controlada.
- Capitulo 5: insumo para evaluar higiene de configuracion y respuesta a pruebas reales con Supabase.

## SPRINT-007-UIUX-PROTOTYPE-COMPLETION

Fecha: 2026-07-07.

Estado: implementado como planificacion documental.

Origen documental:

- `docs/09-implementation/BACKLOG-001-Product-Backlog.md`
- `docs/09-implementation/IMPLEMENTATION-STAGES-001-Prototype-and-MVP-Stage-Map.md`
- `docs/09-implementation/SPRINT-001-Four-Month-Delivery-Plan.md`
- `docs/06-design/prototypes/Asistente de Lavado.dc.html`
- `docs/06-design/UI-001-Design-System.md`
- `docs/06-design/UI-002-Color-and-Typography.md`
- `docs/06-design/UI-003-Component-Catalog.md`
- `docs/06-design/FLOW-002-Onboarding-and-Authentication-Flow.md`
- `docs/06-design/UX-001-Mobile-First-UX-Principles.md`

Alcance aprobado:

- Crear un sprint UI/UX especifico para completar el prototipo Android MVP.
- Incorporar localizacion por idioma del sistema y tema por configuracion del sistema como primera tarea.
- Mantener el mockup como etapa inicial del MVP y como evidencia de tesis.
- Desglosar tareas UI/UX pequenas y revisables antes de continuar con pulido visual amplio.

Archivos principales:

- `docs/09-implementation/SPRINT-007-Sprint-UIUX-Prototype-Completion.md`
- `docs/09-implementation/BACKLOG-001-Product-Backlog.md`
- `docs/09-implementation/SPRINT-001-Four-Month-Delivery-Plan.md`
- `docs/09-implementation/IMPLEMENTATION-STAGES-001-Prototype-and-MVP-Stage-Map.md`
- `docs/09-implementation/IMPL-000-Implementation-Foundation-Overview.md`
- `docs/FR5-INDEX.md`
- `docs/FR7-INDEX.md`

Verificacion:

- Revision documental local.
- No se ejecuto build porque el cambio no modifica codigo.

Uso previsto en la tesis:

- Capitulo 3: evidencia de replanificacion incremental basada en feedback humano.
- Capitulo 4: evidencia de organizacion del prototipado UI/UX como parte del caso de uso TenderApp.
- Capitulo 5: insumo para evaluar trazabilidad entre mockup, tareas de implementacion, screenshots y MVP funcional.

Siguiente tarea recomendada:

- `ANDROID-UI-001 - Localization and system theme foundation`.

## ANDROID-UI-001-LOCALIZATION-AND-SYSTEM-THEME-FOUNDATION

Fecha: 2026-07-07.

Estado: implementado y verificado por tests unitarios Android y build debug.

Origen documental:

- `docs/09-implementation/SPRINT-007-Sprint-UIUX-Prototype-Completion.md`
- `docs/09-implementation/BACKLOG-001-Product-Backlog.md`
- `docs/06-design/FLOW-002-Onboarding-and-Authentication-Flow.md`
- `docs/06-design/UX-001-Mobile-First-UX-Principles.md`
- `docs/07-prompts/PROMPT-003-Compose-Screen-Generation.md`
- `docs/11-quality/QA-001-Definition-of-Done.md`
- Android Developers: localizacion con recursos y Compose/Material 3.

Agentes aplicados como roles documentales:

- Android Agent: conversion Compose a `stringResource` y recursos Android.
- UI Agent: preservacion de idioma/tema por defecto del sistema.
- Testing Agent: ejecucion de tests y build.
- Documentation Agent: registro de task, evidencia y fuentes.
- Review Agent: separacion de supuestos, pendientes y alcance.

Alcance aprobado:

- Implementar la primera task del sprint UI/UX.
- Trabajar en paralelo sobre 3 a 4 superficies auditables: navegacion, autenticacion, ajustes y labels estaticos iniciales del dashboard.
- Usar idioma del sistema operativo por defecto, no forzar espanol.
- Mantener tema claro/oscuro segun preferencia del sistema.
- Dejar New Load, History y pulido visual profundo para tareas posteriores.

Archivos principales:

- `app/src/main/res/values/strings.xml`
- `app/src/main/res/values-es/strings.xml`
- `app/src/main/java/com/tesis_pro/tenderapp/navigation/TenderDestination.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/ui/TenderApp.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/ui/screens/AuthScreen.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/ui/screens/AuthViewModel.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/ui/screens/SettingsScreen.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/ui/screens/DashboardScreen.kt`
- `app/src/test/java/com/tesis_pro/tenderapp/ui/screens/AuthViewModelTest.kt`
- `docs/09-implementation/android/ANDROID-UI-001-Localization-and-System-Theme-Foundation.md`
- `docs/09-implementation/SPRINT-007-Sprint-UIUX-Prototype-Completion.md`

Verificacion:

- `.\gradlew.bat --no-daemon :app:testDebugUnitTest` con `JAVA_HOME=E:\android\jbr`.
- Resultado: exitoso.
- `.\gradlew.bat --no-daemon :app:assembleDebug` con `JAVA_HOME=E:\android\jbr`.
- Resultado: exitoso.

Observaciones:

- El sandbox bloqueo inicialmente la descarga del wrapper Gradle; se ejecuto la verificacion con permiso de red.
- Una corrida paralela produjo ruido de cache incremental Kotlin; las verificaciones finales se repitieron en secuencia y finalizaron exitosamente.
- Persisten advertencias conocidas de deprecacion en `SecureAuthSessionStore`, ya registradas como deuda tecnica MVP.

Uso previsto en la tesis:

- Capitulo 4: evidencia de traduccion del prototipo y decisiones UX hacia recursos Android localizables.
- Capitulo 5: insumo para evaluar control de alcance, trazabilidad y respuesta a feedback humano sobre idioma/tema.

Evidencia pendiente:

- Capturas comparativas con idioma del dispositivo en ingles y espanol.
- Localizar/pulir New Load, History y el resto del dashboard en `ANDROID-UI-003` a `ANDROID-UI-005`.

## ANDROID-UI-002-AUTH-AND-ONBOARDING-VISUAL-POLISH

Fecha: 2026-07-07.

Estado: implementado y verificado por tests unitarios Android y build debug.

Origen documental:

- `docs/09-implementation/SPRINT-007-Sprint-UIUX-Prototype-Completion.md`
- `docs/09-implementation/android/ANDROID-UI-001-Localization-and-System-Theme-Foundation.md`
- `docs/06-design/FLOW-002-Onboarding-and-Authentication-Flow.md`
- `docs/06-design/UX-001-Mobile-First-UX-Principles.md`
- `docs/11-quality/QA-001-Definition-of-Done.md`

Alcance aprobado:

- Continuar con la siguiente task del sprint UI/UX.
- Pulir visualmente la pantalla inicial de autenticacion/onboarding.
- Mantener la logica de Supabase, repositorios y sesion sin cambios.
- Conservar todos los textos visibles en recursos Android localizables.

Archivos principales:

- `app/src/main/java/com/tesis_pro/tenderapp/ui/screens/AuthScreen.kt`
- `app/src/main/res/values/strings.xml`
- `app/src/main/res/values-es/strings.xml`
- `docs/09-implementation/android/ANDROID-UI-002-Auth-and-Onboarding-Visual-Polish.md`
- `docs/09-implementation/SPRINT-007-Sprint-UIUX-Prototype-Completion.md`

Verificacion:

- `.\gradlew.bat --no-daemon :app:testDebugUnitTest` con `JAVA_HOME=E:\android\jbr`.
- Resultado: exitoso.
- `.\gradlew.bat --no-daemon :app:assembleDebug` con `JAVA_HOME=E:\android\jbr`.
- Resultado: exitoso.

Observaciones:

- La pantalla de autenticacion ahora incluye un bloque superior orientado a clima/lavado, titulos por modo y CTA principal de ancho completo.
- Las acciones secundarias se organizaron verticalmente para evitar truncamientos en espanol.
- La verificacion paralela genero ruido temporal de cache incremental Kotlin; la verificacion final secuencial fue exitosa.

Uso previsto en la tesis:

- Capitulo 4: evidencia de evolucion del prototipo navegable hacia una experiencia de onboarding mas coherente.
- Capitulo 5: insumo para evaluar feedback humano, localizacion y mejora incremental de UI sin modificar logica de dominio.

Siguiente tarea recomendada:

- `ANDROID-UI-003 - Dashboard visual refinement`.

## ANDROID-UI-003-DASHBOARD-VISUAL-REFINEMENT

Fecha: 2026-07-07.

Estado: implementado y verificado por tests unitarios Android y build debug.

Origen documental:

- `docs/09-implementation/SPRINT-007-Sprint-UIUX-Prototype-Completion.md`
- `docs/06-design/UI-004-Dashboard-Screen-Specification.md`
- `docs/06-design/UI-006-Weather-Verdict-Card.md`
- `docs/06-design/UX-001-Mobile-First-UX-Principles.md`
- `docs/11-quality/QA-001-Definition-of-Done.md`

Alcance aprobado:

- Continuar con la siguiente task del sprint UI/UX.
- Refinar el dashboard como superficie principal de veredicto rapido.
- Mantener la logica de prediccion, backend y Supabase sin cambios.
- Trasladar labels visibles del ViewModel a recursos Compose donde fuera razonable.

Archivos principales:

- `app/src/main/java/com/tesis_pro/tenderapp/ui/screens/DashboardScreen.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/ui/screens/DashboardViewModel.kt`
- `app/src/test/java/com/tesis_pro/tenderapp/ui/screens/DashboardViewModelTest.kt`
- `app/src/main/res/values/strings.xml`
- `app/src/main/res/values-es/strings.xml`
- `docs/09-implementation/android/ANDROID-UI-003-Dashboard-Visual-Refinement.md`

Verificacion:

- `.\gradlew.bat --no-daemon :app:testDebugUnitTest` con `JAVA_HOME=E:\android\jbr`.
- Resultado: exitoso.
- `.\gradlew.bat --no-daemon :app:assembleDebug` con `JAVA_HOME=E:\android\jbr`.
- Resultado: exitoso.

Observaciones:

- El dashboard ahora usa colores semanticos del theme segun `DryingVerdict`.
- El ViewModel conserva datos y enums de dominio, mientras Compose resuelve labels localizados.
- Los mensajes de error provenientes de backend/proveedor siguen como texto externo y requieren normalizacion posterior.

Uso previsto en la tesis:

- Capitulo 4: evidencia de refinamiento de la pantalla central del caso TenderApp.
- Capitulo 5: insumo para evaluar separacion MVVM, localizacion y consistencia visual basada en documentacion.

Siguiente tarea recomendada:

- `ANDROID-UI-004 - New load flow completion`.

## ANDROID-UI-004-NEW-LOAD-FLOW-COMPLETION

Fecha: 2026-07-07.

Estado: implementado y verificado por tests unitarios Android y build debug.

Origen documental:

- `docs/09-implementation/SPRINT-007-Sprint-UIUX-Prototype-Completion.md`
- `docs/06-design/UI-005-New-Load-Flow-Specification.md`
- `docs/06-design/UX-001-Mobile-First-UX-Principles.md`
- `docs/11-quality/QA-001-Definition-of-Done.md`

Alcance aprobado:

- Continuar con la siguiente task del sprint UI/UX.
- Completar la pantalla de nueva carga como flujo mock representativo.
- Localizar labels y valores visibles en ingles/espanol.
- Mantener deshabilitado el submit real hasta una tarea de integracion backend.

Archivos principales:

- `app/src/main/java/com/tesis_pro/tenderapp/ui/screens/NewLoadScreen.kt`
- `app/src/main/res/values/strings.xml`
- `app/src/main/res/values-es/strings.xml`
- `docs/09-implementation/android/ANDROID-UI-004-New-Load-Flow-Completion.md`

Verificacion:

- `.\gradlew.bat --no-daemon :app:testDebugUnitTest` con `JAVA_HOME=E:\android\jbr`.
- Resultado: exitoso.
- `.\gradlew.bat --no-daemon :app:assembleDebug` con `JAVA_HOME=E:\android\jbr`.
- Resultado: exitoso.

Observaciones:

- La pantalla sigue usando estado mock para conservar alcance y auditabilidad.
- El flujo conserva defaults rapidos: lavarropas principal, ropa diaria mixta, programa Eco 40 y centrifugado alto.
- La creacion real de cargas queda pendiente para una tarea de integracion con backend/Supabase.

Uso previsto en la tesis:

- Capitulo 4: evidencia del prototipo de flujo de carga de ropa dentro del MVP.
- Capitulo 5: insumo para evaluar progreso incremental sin mezclar UI, persistencia y backend en una sola tarea.

Siguiente tarea recomendada:

- `ANDROID-UI-005 - History and active load state UI`.

## ANDROID-UI-005-HISTORY-AND-ACTIVE-LOAD-STATE-UI

Fecha: 2026-07-07.

Estado: implementado y verificado por tests unitarios Android y build debug.

Origen documental:

- `docs/09-implementation/SPRINT-007-Sprint-UIUX-Prototype-Completion.md`
- `docs/06-design/UX-001-Mobile-First-UX-Principles.md`
- `docs/11-quality/QA-001-Definition-of-Done.md`

Alcance aprobado:

- Continuar con la siguiente task del sprint UI/UX.
- Completar History como pantalla mock representativa.
- Diferenciar carga activa y cargas anteriores.
- Agregar estado vacio y textos localizados.
- Mantener fuera de alcance backend, Room, filtros y acciones de edicion.

Archivos principales:

- `app/src/main/java/com/tesis_pro/tenderapp/ui/screens/HistoryScreen.kt`
- `app/src/main/res/values/strings.xml`
- `app/src/main/res/values-es/strings.xml`
- `docs/09-implementation/android/ANDROID-UI-005-History-and-Active-Load-State-UI.md`

Verificacion:

- `.\gradlew.bat --no-daemon :app:testDebugUnitTest` con `JAVA_HOME=E:\android\jbr`.
- Resultado: exitoso.
- `.\gradlew.bat --no-daemon :app:assembleDebug` con `JAVA_HOME=E:\android\jbr`.
- Resultado: exitoso.

Observaciones:

- La pantalla separa secciones `Active load` y `Past loads`.
- El estado vacio existe como componente, aunque el mock por defecto muestra datos representativos.
- La integracion real con historial persistido queda para una tarea de datos/integracion posterior.

Uso previsto en la tesis:

- Capitulo 4: evidencia de expansion del prototipo hacia continuidad entre cargas activas e historial.
- Capitulo 5: insumo para evaluar avance UI incremental con datos representativos y alcance controlado.

Siguiente tarea recomendada:

- `ANDROID-UI-006 - Washer management UI`.

## ANDROID-UI-006-WASHER-MANAGEMENT-UI

Fecha: 2026-07-07.

Estado: implementado y verificado por tests unitarios Android y build debug.

Origen documental:

- `docs/09-implementation/SPRINT-007-Sprint-UIUX-Prototype-Completion.md`
- `docs/05-domain/entities/ENT-002-Washer.md`
- `docs/06-design/UI-003-Component-Catalog.md`
- `docs/07-prompts/PROMPT-003-Compose-Screen-Generation.md`
- `docs/11-quality/QA-001-Definition-of-Done.md`

Alcance aprobado:

- Continuar con la siguiente task del sprint UI/UX.
- Agregar gestion representativa de lavarropas dentro de Ajustes.
- Mostrar nombre, tipo, ubicacion, capacidad, estado y marca de lavarropas principal.
- Localizar labels y valores visibles en ingles/espanol.
- Mantener fuera de alcance backend, Room, Supabase y formularios de alta/edicion.

Archivos principales:

- `app/src/main/java/com/tesis_pro/tenderapp/ui/screens/SettingsScreen.kt`
- `app/src/main/res/values/strings.xml`
- `app/src/main/res/values-es/strings.xml`
- `docs/09-implementation/android/ANDROID-UI-006-Washer-Management-UI.md`
- `docs/09-implementation/SPRINT-007-Sprint-UIUX-Prototype-Completion.md`

Verificacion:

- `.\gradlew.bat --no-daemon :app:testDebugUnitTest` con `JAVA_HOME=E:\android\jbr`.
- Resultado: exitoso.
- `.\gradlew.bat --no-daemon :app:assembleDebug` con `JAVA_HOME=E:\android\jbr`.
- Resultado: exitoso.

Observaciones:

- La seccion queda dentro de Ajustes para mantener compacta la navegacion del prototipo actual.
- El boton de agregar lavarropas queda visible pero deshabilitado hasta una tarea de integracion/datos.

Uso previsto en la tesis:

- Capitulo 4: evidencia de incorporacion de una entidad de dominio documentada en el prototipo UI.
- Capitulo 5: insumo para evaluar trazabilidad entre entidad Washer, sprint UI/UX y prototipo navegable.

Siguiente tarea recomendada:

- `ANDROID-UI-007 - Settings, notification and preferences UI`.

## ANDROID-UI-007-SETTINGS-NOTIFICATION-AND-PREFERENCES-UI

Fecha: 2026-07-07.

Estado: implementado y verificado por tests unitarios Android y build debug.

Origen documental:

- `docs/09-implementation/SPRINT-007-Sprint-UIUX-Prototype-Completion.md`
- `docs/06-design/UI-001-Design-System.md`
- `docs/06-design/UI-002-Color-and-Typography.md`
- `docs/06-design/UI-003-Component-Catalog.md`
- `docs/06-design/UX-001-Mobile-First-UX-Principles.md`
- `docs/07-prompts/PROMPT-003-Compose-Screen-Generation.md`
- `docs/11-quality/QA-001-Definition-of-Done.md`

Alcance aprobado:

- Continuar con la siguiente task del sprint UI/UX.
- Pulir Ajustes como superficie de cuenta, idioma/tema, notificaciones, lavarropas y valores del hogar.
- Mostrar estado representativo de verificacion de email.
- Mostrar canal de notificacion, horario silencioso y estados activos/pausados.
- Agregar metodo de secado y anticipacion de aviso como preferencias del hogar.
- Mantener fuera de alcance permisos push, persistencia, backend, Supabase y edicion real.

Archivos principales:

- `app/src/main/java/com/tesis_pro/tenderapp/ui/screens/SettingsScreen.kt`
- `app/src/main/res/values/strings.xml`
- `app/src/main/res/values-es/strings.xml`
- `docs/09-implementation/android/ANDROID-UI-007-Settings-Notification-and-Preferences-UI.md`
- `docs/09-implementation/SPRINT-007-Sprint-UIUX-Prototype-Completion.md`

Verificacion:

- `.\gradlew.bat --no-daemon :app:testDebugUnitTest` con `JAVA_HOME=E:\android\jbr`.
- Resultado: exitoso.
- `.\gradlew.bat --no-daemon :app:assembleDebug` con `JAVA_HOME=E:\android\jbr`.
- Resultado: exitoso.

Observaciones:

- Los switches continuan como estado representativo de solo lectura.
- Idioma y tema siguen delegados al sistema operativo, segun el alcance aprobado del sprint.

Uso previsto en la tesis:

- Capitulo 4: evidencia de consolidacion del prototipo de Ajustes como parte del MVP navegable.
- Capitulo 5: insumo para evaluar trazabilidad entre feedback UX, localizacion y control de alcance.

Siguiente tarea recomendada:

- `ANDROID-UI-008 - UI evidence package`.

## ANDROID-UI-008-UI-EVIDENCE-PACKAGE

Fecha: 2026-07-08.

Estado: preparado como checklist de evidencia; capturas pendientes.

Origen documental:

- `docs/09-implementation/SPRINT-007-Sprint-UIUX-Prototype-Completion.md`
- `docs/09-implementation/android/ANDROID-UI-008-UI-Evidence-Package.md`
- `docs/06-design/screenshots/`
- `docs/09-implementation/IMPLEMENTATION-STAGES-001-Prototype-and-MVP-Stage-Map.md`

Alcance aprobado:

- Preparar el paquete de evidencia visual del prototipo UI/UX.
- Definir pantallas, estados y nombres sugeridos para screenshots.
- No afirmar capturas que todavia no existen.
- Mantener separado el cierre visual de la integracion desplegada.

Archivos principales:

- `docs/09-implementation/android/ANDROID-UI-008-UI-Evidence-Package.md`
- `docs/09-implementation/SPRINT-007-Sprint-UIUX-Prototype-Completion.md`
- `docs/09-implementation/IMPLEMENTATION-STAGES-001-Prototype-and-MVP-Stage-Map.md`

Verificacion:

- Revision documental local.
- La carpeta `docs/06-design/screenshots/` no expuso archivos desde el entorno de ejecucion.

Uso previsto en la tesis:

- Capitulo 4: lista de evidencia visual necesaria para sostener el prototipo navegable.
- Capitulo 5: base para evaluar trazabilidad entre mockup, pantallas implementadas y revision humana.

Evidencia pendiente:

- Capturas manuales desde emulador o telefono fisico.
- Registro de filenames reales en esta bitacora.

## INTEGRATION-EVIDENCE-001-DEPLOYED-SMOKE-BASELINE

Fecha: 2026-07-08.

Estado: smoke protegido aprobado con deuda de normalizacion de datos.

Origen documental:

- `docs/09-implementation/integration/INTEGRATION-TASK-006-End-to-End-Smoke-Test.md`
- `docs/09-implementation/integration/INTEGRATION-EVIDENCE-001-Deployed-Smoke-Baseline.md`
- `docs/09-implementation/MVP-DEPLOY-001-Deployed-MVP-Checklist.md`
- `docs/09-implementation/backend/BACKEND-DEPLOY-003-Health-And-Supabase-Verification.md`

Alcance aprobado:

- Intentar la verificacion desplegada contra Render.
- Registrar resultados sin incluir secretos.
- Separar fallo de disponibilidad/despliegue de fallo confirmado de codigo backend.

Resultados:

- `GET https://tesis-t85s.onrender.com/api/v1/docs`: no alcanzable desde shell local.
- `GET https://tesis-t85s.onrender.com/api/v1/health`: no alcanzable desde shell local.
- `GET https://tesis-t85s.onrender.com/api/v1/health/supabase`: no alcanzable desde shell local.
- Logs de Render observados por el autor: Nest arranca correctamente y registra rutas `/api/v1/health`, `/api/v1/health/supabase`, `users`, `washers`, `laundry-loads`, `weather`, `predictions` y `notifications`.
- Validacion manual informada por el autor: Android puede alcanzar el backend desplegado para constatar estado.
- Evidencia de navegador informada por el autor: `GET https://tesis-t85s.onrender.com/api/v1/health` devuelve `status: "ok"` y `version: "0.1.0"`.
- Evidencia de navegador informada por el autor: `GET https://tesis-t85s.onrender.com/api/v1/health/supabase` devuelve `status: "ok"`, host de proyecto Supabase, timestamp y `message: null`.
- Logs Android informados por el autor: `GET https://tesis-t85s.onrender.com/api/v1/laundry-loads` devuelve HTTP 500.
- Accion documental/tecnica: se agrega `supabase/migrations/001_initial_schema.sql` para versionar el schema minimo que espera el backend.
- Primer intento de aplicar la migracion: Supabase rechaza `laundry_loads_clothing_type_check` porque existen filas historicas con valores fuera del enum del backend.
- Ajuste aplicado al artefacto SQL: constraints de enums como `NOT VALID`, para proteger nuevas filas y permitir inspeccion/normalizacion posterior de datos existentes.
- Segundo intento de aplicar la migracion: Supabase reporta exito y no devuelve filas, por lo que el schema minimo MVP queda aplicado.
- Reintento protegido: `GET /api/v1/laundry-loads` devuelve HTTP 200 con filas reales de Supabase.
- Observacion tecnica: las filas existentes incluyen valores legacy en minuscula (`light`, `quick`, `washing`, `completed`) y `locationId` nulo en algunos casos, por lo que se registra una deuda de normalizacion antes de considerarlas datos finales para UI.

Uso previsto en la tesis:

- Capitulo 4: evidencia de transicion desde prototipo UI hacia verificacion desplegada.
- Capitulo 5: insumo para discutir bloqueos reales de infraestructura y trazabilidad de intentos fallidos.

Siguiente tarea recomendada:

- Normalizar datos legacy de `laundry_loads` o agregar mapeo defensivo backend antes de consumirlos como datos finales en Android.

## INTEGRATION-TASK-007-LAUNDRY-LOAD-RESPONSE-NORMALIZATION

Fecha: 2026-07-08.

Estado: implementado y verificado por tests y build backend.

Origen documental:

- `docs/09-implementation/integration/INTEGRATION-EVIDENCE-001-Deployed-Smoke-Baseline.md`
- `docs/09-implementation/integration/INTEGRATION-TASK-007-Laundry-Load-Response-Normalization.md`
- `docs/09-implementation/MVP-DEPLOY-001-Deployed-MVP-Checklist.md`

Alcance aprobado:

- Normalizar en backend la respuesta de `laundry_loads` para proteger Android de datos legacy.
- Convertir valores legacy en minuscula a los enums esperados por el contrato.
- Reemplazar `location_id` nulo por un fallback estable.
- Normalizar timestamps Supabase a formato ISO UTC con `Z`.
- Agregar cobertura unitaria para el mapeo legacy.

Archivos principales:

- `backend/src/laundry-loads/laundry-loads.mapper.ts`
- `backend/src/laundry-loads/laundry-loads.service.spec.ts`
- `docs/09-implementation/integration/INTEGRATION-TASK-007-Laundry-Load-Response-Normalization.md`

Verificacion:

- `npm.cmd test` en `backend/`.
- Resultado: 26 suites exitosas, 66 tests exitosos.
- `npm.cmd run build` en `backend/`.
- Resultado: build exitoso.

Uso previsto en la tesis:

- Capitulo 4: evidencia de respuesta incremental a datos reales inconsistentes durante integracion.
- Capitulo 5: insumo para evaluar robustez, trazabilidad y frontera backend como adaptador de datos.

Evidencia pendiente:

- Redeploy backend en Render.
- Reintentar `GET /api/v1/laundry-loads` y comprobar que Android ya no recibe valores legacy.

## ANDROID-INTEGRATION-001-AUTHENTICATED-LAUNDRY-HISTORY

Fecha: 2026-07-08.

Estado: implementado y verificado por build Android.

Origen documental:

- `docs/09-implementation/android/ANDROID-INTEGRATION-001-Authenticated-Laundry-History.md`
- `docs/09-implementation/BACKLOG-001-Product-Backlog.md`
- `docs/09-implementation/IMPLEMENTATION-STAGES-001-Prototype-and-MVP-Stage-Map.md`
- `docs/04-architecture/ARC-004-Repository-and-Data-Flow.md`

Alcance aprobado:

- Conectar la pantalla History con datos protegidos del backend.
- Reutilizar la sesion Supabase persistida para enviar bearer token.
- Mantener Compose como capa de renderizado de estados.
- Registrar como MVP/PROTO-003 las funcionalidades pendientes de visualizacion realista, carga de lavarropas, clima y ubicacion centralizada.

Archivos principales:

- `app/src/main/java/com/tesis_pro/tenderapp/data/repository/BackendLaundryRepository.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/ui/screens/HistoryViewModel.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/ui/screens/HistoryScreen.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/data/remote/RemoteLaundryDataSource.kt`
- `docs/09-implementation/android/ANDROID-INTEGRATION-001-Authenticated-Laundry-History.md`

Verificacion:

- `.\gradlew.bat --no-daemon :app:assembleDebug` con `JAVA_HOME=E:\android\jbr`.
- Resultado: build exitoso.

Uso previsto en la tesis:

- Capitulo 4: evidencia de transicion desde historial mock hacia datos reales autenticados.
- Capitulo 5: insumo para evaluar trazabilidad entre backend desplegado, Supabase Auth y UI Android.

Evidencia pendiente:

- Captura manual de History con sesion real.
- Probar en telefono luego del redeploy backend con normalizacion de `laundry-loads`.

## ANDROID-LAUNDRY-001-CREATE-LAUNDRY-LOAD-FROM-ANDROID

Fecha: 2026-07-08.

Estado: implementado y verificado por build Android.

Origen documental:

- `docs/09-implementation/android/ANDROID-LAUNDRY-001-Create-Laundry-Load-From-Android.md`
- `docs/09-implementation/BACKLOG-001-Product-Backlog.md`
- `docs/09-implementation/IMPLEMENTATION-STAGES-001-Prototype-and-MVP-Stage-Map.md`
- `docs/04-architecture/ARC-004-Repository-and-Data-Flow.md`

Alcance aprobado:

- Conectar la pantalla New Load con `POST /api/v1/laundry-loads`.
- Reutilizar la sesion Supabase persistida para llamadas protegidas.
- Crear una carga MVP con valores predeterminados seguros.
- Mostrar estados localizados de envio, exito y error.

Archivos principales:

- `app/src/main/java/com/tesis_pro/tenderapp/data/repository/BackendLaundryRepository.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/ui/screens/NewLoadViewModel.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/ui/screens/NewLoadScreen.kt`
- `app/src/main/res/values/strings.xml`
- `app/src/main/res/values-es/strings.xml`

Verificacion:

- `.\gradlew.bat --no-daemon :app:assembleDebug` con `JAVA_HOME=E:\android\jbr`.
- Resultado: build exitoso.

Uso previsto en la tesis:

- Capitulo 4: evidencia de mutacion real desde Android hacia backend y Supabase.
- Capitulo 5: insumo para evaluar paso de prototipo visual a MVP funcional incremental.

Evidencia pendiente:

- Validar desde telefono que la carga creada aparece en History.
- Agregar captura de New Load con exito y de History luego de refrescar.

## ANDROID-LOCATION-001-CENTRALIZED-WEATHER-LOCATION

Fecha: 2026-07-08.

Estado: implementado y verificado por build Android.

Origen documental:

- `docs/09-implementation/android/ANDROID-LOCATION-001-Centralized-Weather-Location.md`
- `docs/09-implementation/BACKLOG-001-Product-Backlog.md`
- `docs/09-implementation/IMPLEMENTATION-STAGES-001-Prototype-and-MVP-Stage-Map.md`
- `docs/04-architecture/ARC-004-Repository-and-Data-Flow.md`

Alcance aprobado:

- Centralizar la ubicacion meteorologica del hogar.
- Evitar constantes duplicadas `home` / `Home patio` en pantallas y ViewModels.
- Preparar el consumo posterior de clima real del backend.

Archivos principales:

- `app/src/main/java/com/tesis_pro/tenderapp/domain/repository/HouseholdSettingsRepository.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/data/repository/LocalHouseholdSettingsRepository.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/ui/screens/DashboardViewModel.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/ui/screens/NewLoadViewModel.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/ui/screens/SettingsScreen.kt`

Verificacion:

- `.\gradlew.bat --no-daemon :app:assembleDebug` con `JAVA_HOME=E:\android\jbr`.
- Resultado: build exitoso.

Uso previsto en la tesis:

- Capitulo 4: evidencia de preparacion incremental para prediccion meteorologica real.
- Capitulo 5: insumo para evaluar separacion entre configuracion del hogar, UI y logica de prediccion.

Evidencia pendiente:

- Conectar dashboard a clima backend usando esta ubicacion.
- Definir si la ubicacion sera editable o persistida antes de la demo final.

## BACKEND-WEATHER-001-OPEN-METEO-PROVIDER

Fecha: 2026-07-08.

Estado: implementado y verificado por tests y build backend.

Origen documental:

- `docs/09-implementation/backend/BACKEND-WEATHER-001-Open-Meteo-Provider.md`
- `docs/09-implementation/backend/BACKEND-TASK-012-Implement-Weather-Provider-Abstraction.md`
- `docs/09-implementation/backend/API-001-Backend-API-Contract-Draft.md`
- `docs/09-implementation/BACKLOG-001-Product-Backlog.md`

Alcance aprobado:

- Agregar proveedor meteorologico real detras de la frontera backend.
- Usar Open-Meteo para el MVP por no requerir API key en el flujo no comercial.
- Mantener `mock` como proveedor por defecto.
- Resolver `home` a coordenadas MVP de Buenos Aires para evitar enviar ids internos a APIs externas.
- Mapear current/hourly forecast a `WeatherSnapshotResponseDto`.

Archivos principales:

- `backend/src/weather/open-meteo-weather.provider.ts`
- `backend/src/weather/weather-provider.config.ts`
- `backend/src/weather/weather-location-registry.ts`
- `backend/src/weather/weather.module.ts`
- `backend/.env.example`
- `docs/09-implementation/backend/BACKEND-WEATHER-001-Open-Meteo-Provider.md`

Verificacion:

- `npm.cmd test` en `backend/`.
- Resultado: 29 suites exitosas, 74 tests exitosos.
- `npm.cmd run build` en `backend/`.
- Resultado: build exitoso.

Uso previsto en la tesis:

- Capitulo 4: evidencia de sustitucion controlada de un mock por un proveedor externo real.
- Capitulo 5: insumo para evaluar seguridad, trazabilidad y desacoplamiento entre Android y servicios externos.

Evidencia pendiente:

- Configurar `WEATHER_PROVIDER=open-meteo` en Render.
- Ejecutar smoke test contra `/api/v1/weather/current?locationId=home`.
- Conectar Android Dashboard al clima backend.

## ANDROID-WEATHER-001-DASHBOARD-CONSUMES-BACKEND-WEATHER

Fecha: 2026-07-08.

Estado: implementado y verificado por tests y build Android.

Origen documental:

- `docs/09-implementation/android/ANDROID-WEATHER-001-Dashboard-Consumes-Backend-Weather.md`
- `docs/09-implementation/backend/BACKEND-WEATHER-001-Open-Meteo-Provider.md`
- `docs/09-implementation/BACKLOG-001-Product-Backlog.md`
- `docs/09-implementation/IMPLEMENTATION-STAGES-001-Prototype-and-MVP-Stage-Map.md`

Alcance aprobado:

- Consumir `/api/v1/weather/current` desde Android.
- Usar la ubicacion meteorologica centralizada.
- Enviar bearer token de sesion a endpoint protegido.
- Usar clima backend para prediccion de dashboard cuando este disponible.
- Mantener fallback local si el clima backend falla.

Archivos principales:

- `app/src/main/java/com/tesis_pro/tenderapp/data/remote/RemoteWeatherDataSource.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/data/remote/dto/WeatherDtos.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/data/remote/TenderBackendApi.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/data/repository/BackendDashboardRepository.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/ui/screens/DashboardViewModel.kt`

Verificacion:

- `.\gradlew.bat --no-daemon :app:testDebugUnitTest` con `JAVA_HOME=E:\android\jbr`.
- Resultado: tests exitosos.
- `.\gradlew.bat --no-daemon :app:assembleDebug` con `JAVA_HOME=E:\android\jbr`.
- Resultado: build exitoso.
- `.\gradlew.bat --no-daemon :app:assembleDebug` con `JAVA_HOME=E:\android\jbr`.
- Resultado: build exitoso.
- Validacion manual: registro con email nuevo genero link Supabase con `redirect_to=com.tesispro.tenderapp://auth-callback`.
- Validacion manual: al abrir el link de verificacion en Android, TenderApp abrio el flujo de login y mostro la accion para navegar al dashboard.
- No se registraron tokens, enlaces completos ni datos sensibles en la evidencia versionada.

Uso previsto en la tesis:

- Capitulo 4: evidencia de integracion entre Android, backend y proveedor meteorologico externo.
- Capitulo 5: insumo para evaluar robustez incremental con fallback y contrato estable.

Evidencia pendiente:

- Probar en telefono contra Render con `WEATHER_PROVIDER=open-meteo`.
- Capturar dashboard con datos meteorologicos reales.

## ANDROID-VISUAL-001-REALISTIC-DASHBOARD-HISTORY-VISUALS

Fecha: 2026-07-08.

Estado: implementado y verificado por tests unitarios Android y build debug.

Origen documental:

- `docs/09-implementation/android/ANDROID-VISUAL-001-Realistic-Dashboard-History-Visuals.md`
- `docs/09-implementation/BACKLOG-001-Product-Backlog.md`
- `docs/09-implementation/IMPLEMENTATION-STAGES-001-Prototype-and-MVP-Stage-Map.md`
- `docs/06-design/prototypes/Asistente de Lavado.dc.html`

Alcance aprobado:

- Registrar como tarea MVP la mejora UI/UX aplicada sobre la mayoria de pantallas.
- Preservar los cambios visuales introducidos manualmente por el autor.
- Revisar compatibilidad con tests y contrato backend.
- Mantener observabilidad de errores sin romper tests JVM locales.
- No agregar nuevas dependencias visuales ni endpoints backend.

Archivos principales:

- `app/src/main/java/com/tesis_pro/tenderapp/ui/components/TenderVisuals.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/ui/screens/AuthScreen.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/ui/screens/DashboardScreen.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/ui/screens/HistoryScreen.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/ui/screens/NewLoadScreen.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/ui/screens/SettingsScreen.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/ui/theme/Color.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/ui/screens/AuthViewModel.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/data/remote/RemoteLaundryDataSource.kt`

Verificacion:

- `.\gradlew.bat --no-daemon :app:testDebugUnitTest` con `JAVA_HOME=E:\android\jbr`.
- Resultado: tests exitosos.
- `.\gradlew.bat --no-daemon :app:assembleDebug` con `JAVA_HOME=E:\android\jbr`.
- Resultado: build exitoso.
- `git diff --check`.
- Resultado: sin errores de patch; solo advertencias de line endings.
- Escaneo basico de secretos en carpetas del proyecto.
- Resultado: sin coincidencias.

Observaciones:

- La mejora UI/UX fue introducida manualmente por el autor y revisada por Codex.
- Codex no elimino logs de depuracion; los envolvio defensivamente para evitar fallos de unit tests locales por `android.util.Log`.
- Android mantiene request de creacion de cargas en enums uppercase, alineado con la validacion actual del backend.
- Android tolera respuestas legacy lowercase para proteger la UI ante datos historicos en Supabase.

Uso previsto en la tesis:

- Capitulo 4: evidencia de iteracion visual humano-IA sobre un prototipo conectado.
- Capitulo 5: insumo para discutir control de calidad, preservacion de observabilidad y trazabilidad entre prototipo, UI real y verificacion.

Evidencia pendiente:

- Capturas manuales de las pantallas mejoradas en telefono o emulador.
- Comparacion visual contra el prototipo HTML y screenshots de `docs/06-design/screenshots/`.
- Datos demo normalizados en Supabase antes de capturar evidencia final.

## SUPABASE-DATA-001-DEMO-DATA-NORMALIZATION

Fecha: 2026-07-08.

Estado: migracion preparada; aplicacion manual pendiente.

Origen documental:

- `docs/09-implementation/integration/SUPABASE-DATA-001-Demo-Data-Normalization.md`
- `docs/09-implementation/BACKLOG-001-Product-Backlog.md`
- `docs/09-implementation/IMPLEMENTATION-STAGES-001-Prototype-and-MVP-Stage-Map.md`
- `docs/09-implementation/integration/INTEGRATION-EVIDENCE-001-Deployed-Smoke-Baseline.md`

Alcance aprobado:

- Preparar la normalizacion de datos demo en Supabase.
- Corregir valores legacy de `laundry_loads` hacia el contrato backend.
- Completar ubicacion `home` para filas sin ubicacion.
- Asegurar un lavarropas principal por usuario con datos demo.
- Agregar tabla minima `user_locations` con RLS para evidencia y futura persistencia de ubicacion.
- No ejecutar mutaciones directas contra Supabase desde Codex.

Archivos principales:

- `supabase/migrations/002_demo_data_normalization.sql`
- `docs/09-implementation/integration/SUPABASE-DATA-001-Demo-Data-Normalization.md`
- `docs/09-implementation/BACKLOG-001-Product-Backlog.md`
- `docs/09-implementation/IMPLEMENTATION-STAGES-001-Prototype-and-MVP-Stage-Map.md`

Verificacion:

- Revision local del SQL contra el schema existente `001_initial_schema.sql`.
- Revision local de contratos backend para enums de lavarropas y cargas.
- No se ejecuto contra Supabase desde Codex.

Observaciones:

- La migracion es idempotente y no elimina filas historicas.
- La normalizacion se limita al proyecto demo/staging de tesis.
- Las constraints enum se validan al final de la migracion, despues de limpiar valores conocidos.

Uso previsto en la tesis:

- Capitulo 4: evidencia de estabilizacion de datos reales antes de capturar pantallas finales.
- Capitulo 5: insumo para analizar la diferencia entre robustez defensiva del backend y saneamiento real de datos.

Evidencia pendiente:

- Aplicar `supabase/migrations/002_demo_data_normalization.sql` en Supabase SQL Editor.
- Ejecutar consultas de verificacion documentadas.
- Reintentar Dashboard, History y New Load desde Android conectado a Render.
- Capturar evidencia visual sin secretos.

## SUPABASE-DATA-001

Fecha: 2026-07-08.

Estado: aplicado manualmente en Supabase SQL Editor.

Origen documental:

- `docs/09-implementation/integration/SUPABASE-DATA-001-Demo-Data-Normalization.md`
- `docs/09-implementation/integration/INTEGRATION-EVIDENCE-001-Deployed-Smoke-Baseline.md`
- `docs/09-implementation/MVP-DEPLOY-001-Deployed-MVP-Checklist.md`
- `docs/09-implementation/IMPLEMENTATION-STAGES-001-Prototype-and-MVP-Stage-Map.md`

Alcance aprobado:

- Normalizar valores enum heredados en `laundry_loads` al contrato del backend.
- Asegurar un lavarropas primario por usuario.
- Crear tabla `user_locations` con politica RLS y fila `home` por usuario demo.
- Vincular cargas huerfanas al lavarropas primario del usuario.
- Validar constraints tras la normalizacion.

Archivos principales:

- `supabase/migrations/002_demo_data_normalization.sql`

Verificacion:

- Query de diagnostico confirmo 0 filas con status invalido tras la normalizacion.
- Todos los `clothing_type`, `washing_program`, `status` y `location_id` validos segun el contrato backend.
- Tabla `user_locations` creada con fila `home` / "Home patio" / `is_primary = true` para el usuario demo.
- Bloqueador encontrado y resuelto: constraint `laundry_loads_status_check` estaba en estado VALID; se normalizaron los valores, se elimino el constraint y se reincorporo como VALID directo.
- Ajuste posterior del artefacto versionado: `002_demo_data_normalization.sql` fue endurecido para eliminar constraints enum antes de normalizar y recrearlas despues, evitando repetir el fallo en otra base con datos de spike.

Uso previsto en la tesis:

- Capitulo 4: evidencia de la iteracion de calidad de datos previa a la captura de evidencia final del MVP.
- Capitulo 5: soporte para evaluar trazabilidad entre documentacion de integracion y aplicacion manual verificada.

Evidencia pendiente:

- Smoke test Android conectado contra Render con usuario de prueba (login, Dashboard, History, New Load).
- Capturas de pantalla del dispositivo/emulador para evidencia visual de la tesis.

## ANDROID-WASHER-001-CONNECTED-WASHER-SELECTION-AND-CREATION

Fecha: 2026-07-09.

Estado: implementado y verificado por tests unitarios Android y build debug.

Origen documental:

- `docs/09-implementation/android/ANDROID-WASHER-001-Connected-Washer-Selection-And-Creation.md`
- `docs/09-implementation/BACKLOG-001-Product-Backlog.md`
- `docs/09-implementation/IMPLEMENTATION-STAGES-001-Prototype-and-MVP-Stage-Map.md`
- `docs/09-implementation/MVP-KNOWN-LIMITATIONS-001.md`
- `docs/FR7-INDEX.md`

Alcance aprobado:

- Resolver la inconsistencia detectada entre backend/Supabase y Android para lavarropas.
- Agregar endpoints Android para listar y crear lavarropas protegidos.
- Cargar lavarropas reales en Ajustes y Nueva carga usando la sesion Supabase persistida.
- Permitir seleccionar lavarropas, tipo de ropa y programa antes de crear una carga.
- Permitir crear un lavarropas demo desde Ajustes.
- Mantener edicion/eliminacion de lavarropas fuera del alcance MVP.

Archivos principales:

- `app/src/main/java/com/tesis_pro/tenderapp/data/remote/TenderBackendApi.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/data/remote/dto/WasherDtos.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/data/remote/RemoteWasherDataSource.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/data/repository/BackendWasherRepository.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/ui/screens/NewLoadViewModel.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/ui/screens/NewLoadScreen.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/ui/screens/SettingsViewModel.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/ui/screens/SettingsScreen.kt`
- `app/src/test/java/com/tesis_pro/tenderapp/data/remote/RemoteWasherDataSourceTest.kt`

Verificacion:

- `.\gradlew.bat --no-daemon :app:testDebugUnitTest` con `JAVA_HOME=E:\android\jbr`.
- Resultado: tests exitosos.
- `.\gradlew.bat --no-daemon :app:assembleDebug` con `JAVA_HOME=E:\android\jbr`.
- Resultado: build exitoso.

Uso previsto en la tesis:

- Capitulo 4: evidencia de cierre de brecha entre contrato backend, datos Supabase y experiencia Android.
- Capitulo 5: insumo para evaluar trazabilidad de limitaciones MVP y correccion incremental guiada por auditoria documental.

Evidencia pendiente:

- Prueba manual en telefono/emulador: iniciar sesion, crear lavarropas en Ajustes, seleccionar opciones en Nueva carga, crear carga y verificar Historial.
- Capturas de pantalla para evidencia visual final del MVP-DEMO.

## MVP-SMOKE-TEST-001-MANUAL-ANDROID-MVP-SMOKE

Fecha: 2026-07-09.

Estado: validado manualmente por el autor de la tesis.

Origen documental:

- `docs/09-implementation/integration/MVP-SMOKE-TEST-001-Manual-Android-Smoke-Test.md`
- `docs/09-implementation/integration/INTEGRATION-EVIDENCE-001-Deployed-Smoke-Baseline.md`
- `docs/09-implementation/MVP-DEPLOY-001-Deployed-MVP-Checklist.md`
- `docs/09-implementation/IMPLEMENTATION-STAGES-001-Prototype-and-MVP-Stage-Map.md`
- `docs/FR7-INDEX.md`

Alcance validado:

- Login correcto.
- Backend accesible desde el flujo Android.
- Dashboard correcto.
- Settings correcto.
- Lavarropas visibles/creados en Settings, con observacion de lavarropas 3 y 4.
- New Load correcto.
- Historial correcto.

Interpretacion:

- La prueba de humo manual confirma el camino principal del MVP:

```text
Android -> Backend Render -> Supabase
```

- El flujo validado cubre autenticacion, lectura de datos, creacion/listado de lavarropas, creacion de carga e historial.
- La evidencia es manual y funcional; no reemplaza una suite E2E automatizada.

Uso previsto en la tesis:

- Capitulo 4: evidencia de integracion funcional del MVP desplegado.
- Capitulo 5: insumo para evaluar que el sistema documental guio un flujo incremental hasta una vertical slice demostrable.

Evidencia pendiente:

- Capturas de pantalla desde telefono o emulador.
- Revision final del guion de demo.
- Cierre del gate `QA-005` cuando se incorporen las capturas.

## MVP-INCREMENTS-001-INCREMENTAL-DEVELOPMENT-AND-AUTOMATION

Fecha: 2026-07-09.

Estado: actualizacion documental aplicada.

Origen documental:

- Recomendaciones externas recibidas sobre anteproyecto y MVP.
- `docs/09-implementation/MVP-INCREMENTS-001-Incremental-Development-And-Automation.md`
- `docs/09-implementation/MVP-001-MVP-Scope-Definition.md`
- `docs/09-implementation/BACKLOG-001-Product-Backlog.md`
- `docs/09-implementation/SPRINT-001-Four-Month-Delivery-Plan.md`
- `docs/04-architecture/ARC-006-Notification-Architecture.md`
- `docs/11-quality/QA-002-Acceptance-Criteria-Matrix.md`

Alcance aprobado:

- Incorporar explicitamente automatizacion simple y notificaciones al alcance del MVP.
- Reescribir el enfoque de desarrollo como incrementos funcionales, no como Scrum obligatorio ni como analisis/diseno tradicional.
- Proponer cuatro incrementos de valor para el MVP.
- Ajustar criterios de aceptacion y backlog para que las notificaciones queden como tarea controlable.
- Actualizar metodologia y desarrollo en `tesis-final` con lenguaje academico.

Archivos principales:

- `docs/09-implementation/MVP-INCREMENTS-001-Incremental-Development-And-Automation.md`
- `docs/09-implementation/MVP-001-MVP-Scope-Definition.md`
- `docs/09-implementation/BACKLOG-001-Product-Backlog.md`
- `docs/09-implementation/SPRINT-001-Four-Month-Delivery-Plan.md`
- `docs/09-implementation/IMPLEMENTATION-STAGES-001-Prototype-and-MVP-Stage-Map.md`
- `docs/04-architecture/ARC-006-Notification-Architecture.md`
- `docs/11-quality/QA-002-Acceptance-Criteria-Matrix.md`
- `tesis-final/chapters/01-introduccion.md`
- `tesis-final/chapters/03-metodologia.md`
- `tesis-final/chapters/04-desarrollo.md`

Verificacion:

- Cambio documental; no requiere build Android/backend.
- `git diff --check` recomendado antes de commit.

Uso previsto en la tesis:

- Anteproyecto: seccion de metodologia/enfoque incremental.
- Anteproyecto: alcance actualizado del MVP con automatizacion simple.
- Capitulo 4: consideraciones de desarrollo del software, arquitectura de notificaciones y validacion incremental.

Evidencia pendiente:

- Implementar o validar `ANDROID-NOTIF-001` a `ANDROID-NOTIF-004` si se decide cerrar notificaciones locales/simuladas antes de la entrega final.
- Capturar evidencia visual de preferencias/alertas en pantalla de Ajustes.

## ANDROID-NOTIF-001-EDITABLE-NOTIFICATION-PREFERENCES

Fecha: 2026-07-09.

Estado: implementado y verificado por tests unitarios Android y build debug.

Origen documental:

- `docs/09-implementation/MVP-INCREMENTS-001-Incremental-Development-And-Automation.md`
- `docs/09-implementation/BACKLOG-001-Product-Backlog.md`
- `docs/09-implementation/android/ANDROID-NOTIF-001-Editable-Notification-Preferences.md`
- `docs/04-architecture/ARC-006-Notification-Architecture.md`
- `docs/05-domain/business-rules/BR-004-Notification-Eligibility.md`
- `docs/11-quality/QA-002-Acceptance-Criteria-Matrix.md`

Alcance aprobado:

- Conectar las preferencias de notificacion de Ajustes a estado editable del ViewModel.
- Incorporar opt-in global de notificaciones.
- Incorporar categorias MVP: mejor momento para tender, recordatorio de retiro y riesgo de lluvia.
- Deshabilitar categorias cuando el opt-in global esta apagado.
- No implementar todavia permisos Android, WorkManager, push remoto ni persistencia.

Archivos principales:

- `app/src/main/java/com/tesis_pro/tenderapp/ui/screens/SettingsViewModel.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/ui/screens/SettingsScreen.kt`
- `app/src/main/res/values/strings.xml`
- `app/src/main/res/values-es/strings.xml`
- `app/src/test/java/com/tesis_pro/tenderapp/ui/screens/SettingsViewModelTest.kt`
- `docs/09-implementation/android/ANDROID-NOTIF-001-Editable-Notification-Preferences.md`
- `docs/09-implementation/BACKLOG-001-Product-Backlog.md`

Verificacion:

- `.\gradlew.bat --no-daemon :app:testDebugUnitTest` con `JAVA_HOME=E:\android\jbr`.
- Resultado: tests exitosos.
- `.\gradlew.bat --no-daemon :app:assembleDebug` con `JAVA_HOME=E:\android\jbr`.
- Resultado: build exitoso.

Uso previsto en la tesis:

- Capitulo 4: evidencia de paso desde una pantalla de ajustes visual a un limite funcional controlable.
- Capitulo 5: insumo para evaluar trazabilidad entre backlog, arquitectura de notificaciones y comportamiento verificable.

Evidencia pendiente:

- Captura manual de Ajustes mostrando preferencias activas y pausadas.
- Implementar `ANDROID-NOTIF-002` para simular o programar el recordatorio de mejor momento para tender.

## ANDROID-NOTIF-002-IDEAL-HANGING-TIME-REMINDER

Fecha: 2026-07-09.

Estado: implementado y verificado por tests unitarios Android y build debug.

Origen documental:

- `docs/09-implementation/BACKLOG-001-Product-Backlog.md`
- `docs/09-implementation/android/ANDROID-NOTIF-002-Ideal-Hanging-Time-Reminder.md`
- `docs/09-implementation/android/ANDROID-NOTIF-001-Editable-Notification-Preferences.md`
- `docs/04-architecture/ARC-006-Notification-Architecture.md`
- `docs/05-domain/business-rules/BR-004-Notification-Eligibility.md`
- `docs/11-quality/QA-002-Acceptance-Criteria-Matrix.md`

Alcance aprobado:

- Simular localmente el recordatorio de mejor momento para tender luego de crear una carga.
- Respetar opt-in global y categoria `IDEAL_HANGING_TIME`.
- Compartir preferencias de notificacion entre Ajustes y Nueva carga durante la sesion.
- Mostrar feedback visible cuando el recordatorio queda encolado.
- No implementar todavia permisos Android, WorkManager, push remoto ni persistencia.

Archivos principales:

- `app/src/main/java/com/tesis_pro/tenderapp/domain/notification/ScheduleIdealHangingReminderUseCase.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/domain/notification/NotificationPreferencesRepository.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/data/notification/InMemoryNotificationPreferencesRepository.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/data/notification/LocalNotificationScheduler.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/ui/screens/NewLoadViewModel.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/ui/screens/NewLoadScreen.kt`
- `app/src/test/java/com/tesis_pro/tenderapp/domain/notification/ScheduleIdealHangingReminderUseCaseTest.kt`

Verificacion:

- `.\gradlew.bat --no-daemon :app:testDebugUnitTest` con `JAVA_HOME=E:\android\jbr`.
- Resultado: tests exitosos.
- `.\gradlew.bat --no-daemon :app:assembleDebug` con `JAVA_HOME=E:\android\jbr`.
- Resultado: build exitoso.

Uso previsto en la tesis:

- Capitulo 4: evidencia de automatizacion simple dentro del MVP sin introducir infraestructura push prematura.
- Capitulo 5: insumo para evaluar trazabilidad entre preferencias, regla de elegibilidad y accion automatizada verificable.

Evidencia pendiente:

- Captura manual de Nueva carga mostrando el mensaje de recordatorio encolado.
- Implementar `ANDROID-NOTIF-003` para recordatorio de retiro o carga lista.

## ANDROID-NOTIF-003-PICKUP-REMINDER

Fecha: 2026-07-10.

Estado: implementado y verificado por tests unitarios Android y build debug.

Origen documental:

- `docs/09-implementation/BACKLOG-001-Product-Backlog.md`
- `docs/09-implementation/android/ANDROID-NOTIF-003-Pickup-Reminder.md`
- `docs/09-implementation/android/ANDROID-NOTIF-001-Editable-Notification-Preferences.md`
- `docs/09-implementation/android/ANDROID-NOTIF-002-Ideal-Hanging-Time-Reminder.md`
- `docs/04-architecture/ARC-006-Notification-Architecture.md`
- `docs/05-domain/business-rules/BR-004-Notification-Eligibility.md`
- `docs/11-quality/QA-002-Acceptance-Criteria-Matrix.md`

Alcance aprobado:

- Simular localmente el recordatorio de retiro/carga lista luego de crear una carga.
- Respetar opt-in global y categoria `DRYING_COMPLETE`.
- Compartir preferencias de notificacion entre Ajustes y Nueva carga durante la sesion.
- Mostrar feedback visible para estados con recordatorio de tender, retiro o ambos.
- No implementar todavia permisos Android, WorkManager, push remoto ni persistencia.

Archivos principales:

- `app/src/main/java/com/tesis_pro/tenderapp/domain/notification/SchedulePickupReminderUseCase.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/ui/screens/NewLoadViewModel.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/ui/screens/NewLoadScreen.kt`
- `app/src/main/res/values/strings.xml`
- `app/src/main/res/values-es/strings.xml`
- `app/src/test/java/com/tesis_pro/tenderapp/domain/notification/SchedulePickupReminderUseCaseTest.kt`

Verificacion:

- `.\gradlew.bat --no-daemon :app:testDebugUnitTest` con `JAVA_HOME=E:\android\jbr`.
- Resultado: tests exitosos.
- `.\gradlew.bat --no-daemon :app:assembleDebug` con `JAVA_HOME=E:\android\jbr`.
- Resultado: build exitoso.

Uso previsto en la tesis:

- Capitulo 4: evidencia de una segunda categoria de automatizacion MVP implementada con la misma frontera controlada.
- Capitulo 5: insumo para evaluar reutilizacion de reglas de elegibilidad y trazabilidad entre preferencias y acciones automatizadas.

Evidencia pendiente:

- Captura manual de Nueva carga mostrando el mensaje de recordatorios de tender y retiro.
- Implementar `ANDROID-NOTIF-004` para estado de alerta por riesgo de lluvia.

## ANDROID-NOTIF-004-RAIN-RISK-ALERT-STATE

Fecha: 2026-07-10.

Estado: implementado y verificado por tests unitarios Android y build debug.

Origen documental:

- `docs/09-implementation/BACKLOG-001-Product-Backlog.md`
- `docs/09-implementation/android/ANDROID-NOTIF-004-Rain-Risk-Alert-State.md`
- `docs/09-implementation/SPRINT-006-Sprint-05-Notifications-and-Polish.md`
- `docs/04-architecture/ARC-006-Notification-Architecture.md`
- `docs/04-architecture/ARC-004-Repository-and-Data-Flow.md`
- `docs/05-domain/business-rules/BR-002-Rain-Risk-Override.md`
- `docs/05-domain/business-rules/BR-004-Notification-Eligibility.md`
- `docs/05-domain/requirements/REQ-006-Notification-Management.md`
- `docs/05-domain/use-cases/UC-006-Receive-Rain-Alert.md`
- `docs/06-design/UI-001-Design-System.md`
- `docs/06-design/UI-004-Dashboard-Screen-Specification.md`
- `docs/06-design/UI-006-Weather-Verdict-Card.md`
- `docs/11-quality/QA-001-Definition-of-Done.md`
- `docs/11-quality/QA-002-Acceptance-Criteria-Matrix.md`
- `docs/11-quality/QA-003-Implementation-Review-Gate.md`

Alcance aprobado:

- Mostrar en Dashboard un estado de alerta por riesgo de lluvia.
- Derivar el estado fuera de los Composables.
- Usar roles semanticos de advertencia/riesgo del sistema visual.
- Mantener el comportamiento como alerta local/deterministica MVP, sin push ni monitoreo de fondo.

Archivos principales:

- `app/src/main/java/com/tesis_pro/tenderapp/ui/screens/DashboardViewModel.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/ui/screens/DashboardScreen.kt`
- `app/src/main/res/values/strings.xml`
- `app/src/main/res/values-es/strings.xml`
- `app/src/test/java/com/tesis_pro/tenderapp/ui/screens/DashboardViewModelTest.kt`
- `docs/09-implementation/android/ANDROID-NOTIF-004-Rain-Risk-Alert-State.md`
- `docs/09-implementation/BACKLOG-001-Product-Backlog.md`

Verificacion:

- `.\gradlew.bat --no-daemon :app:testDebugUnitTest` con `JAVA_HOME=E:\android\jbr`.
- Resultado: tests exitosos.
- `.\gradlew.bat --no-daemon :app:assembleDebug` con `JAVA_HOME=E:\android\jbr`.
- Resultado: build exitoso.

Uso previsto en la tesis:

- Capitulo 4: evidencia de alerta de riesgo meteorologico en el MVP sin complejidad push prematura.
- Capitulo 5: insumo para evaluar trazabilidad entre regla de lluvia, UI semantica y criterio de aceptacion PB-019.

Evidencia pendiente:

- Captura manual del Dashboard con alta probabilidad de lluvia.
- Ejecutar `QA-NOTIF-001` para validar opt-in/opt-out y evidencia de automatizacion completa.

## QA-NOTIF-001-NOTIFICATION-AUTOMATION-VALIDATION

Fecha: 2026-07-10.

Estado: validacion documental implementada y verificada por tests unitarios Android y build debug.

Origen documental:

- `docs/09-implementation/BACKLOG-001-Product-Backlog.md`
- `docs/09-implementation/android/QA-NOTIF-001-Notification-Validation.md`
- `docs/09-implementation/android/ANDROID-NOTIF-001-Editable-Notification-Preferences.md`
- `docs/09-implementation/android/ANDROID-NOTIF-002-Ideal-Hanging-Time-Reminder.md`
- `docs/09-implementation/android/ANDROID-NOTIF-003-Pickup-Reminder.md`
- `docs/09-implementation/android/ANDROID-NOTIF-004-Rain-Risk-Alert-State.md`
- `docs/04-architecture/ARC-006-Notification-Architecture.md`
- `docs/05-domain/business-rules/BR-004-Notification-Eligibility.md`
- `docs/11-quality/QA-001-Definition-of-Done.md`
- `docs/11-quality/QA-002-Acceptance-Criteria-Matrix.md`
- `docs/11-quality/QA-003-Implementation-Review-Gate.md`

Alcance aprobado:

- Consolidar la evidencia de preferencias, recordatorio de tender, recordatorio de retiro y alerta de lluvia.
- Validar trazabilidad contra PB-019 y QA-002.
- Registrar que el MVP usa automatizacion local/simulada y no push productivo.
- Marcar PB-019 como cerrado para el alcance MVP.

Archivos principales:

- `docs/09-implementation/android/QA-NOTIF-001-Notification-Validation.md`
- `docs/09-implementation/BACKLOG-001-Product-Backlog.md`
- `tesis-final/evidence/increment-log.md`

Verificacion:

- `.\gradlew.bat --no-daemon :app:testDebugUnitTest` con `JAVA_HOME=E:\android\jbr`.
- Resultado: tests exitosos.
- `.\gradlew.bat --no-daemon :app:assembleDebug` con `JAVA_HOME=E:\android\jbr`.
- Resultado: build exitoso.
- `git diff --check`.
- Escaneo basico de secretos.

Uso previsto en la tesis:

- Capitulo 4: evidencia de cierre de un bloque de automatizacion MVP mediante tareas pequenas y auditables.
- Capitulo 5: soporte para discutir trazabilidad entre requisitos, arquitectura, tests, backlog y evidencia.

Evidencia pendiente:

- Capturas manuales de Ajustes, Nueva carga y Dashboard con alerta de lluvia.
- Revision humana del cierre de PB-019.

## PUSH-PLAN-001-REAL-PUSH-NOTIFICATIONS-PLAN

Fecha: 2026-07-10.

Estado: plan documental implementado.

Origen documental:

- `AGENTS.md`
- `docs/README.md`
- `docs/FR6-INDEX.md`
- `docs/CONTEXT-000-Master-Context.md`
- `docs/CONTEXT-001-Documentation-Map.md`
- `docs/CONTEXT-002-Codex-Reading-Protocol.md`
- `docs/CONTEXT-003-Task-Context-Packs.md`
- `docs/FR5-INDEX.md`
- `docs/09-implementation/BACKLOG-001-Product-Backlog.md`
- `docs/04-architecture/ARC-006-Notification-Architecture.md`
- `docs/09-implementation/backend/BACKEND-TASK-015-Implement-Notification-Scheduling-Boundary.md`
- `docs/07-prompts/PROMPT-008-Sprint-Planning.md`
- `docs/07-prompts/PROMPT-010-Single-Task-Implementation.md`
- `docs/08-agents/AG-000-Agent-Harness-Overview.md`
- `docs/11-quality/QA-001-Definition-of-Done.md`
- `docs/11-quality/QA-003-Implementation-Review-Gate.md`
- Firebase Cloud Messaging Android documentation.
- Firebase Admin SDK send/setup documentation.
- Android notification runtime permission documentation.

Alcance aprobado:

- Definir la transicion desde notificaciones locales/simuladas MVP hacia push real.
- Registrar PB-020 como bloque de integracion entre Android, backend, Supabase, Render y Firebase Cloud Messaging.
- Separar tareas de Supabase, backend, Android, infraestructura y QA.
- No modificar codigo Android ni backend todavia.
- No agregar secretos, dependencias Firebase ni migraciones ejecutables en esta tarea.

Archivos principales:

- `docs/09-implementation/integration/PUSH-PLAN-001-Real-Push-Notifications-Plan.md`
- `docs/09-implementation/BACKLOG-001-Product-Backlog.md`
- `tesis-final/evidence/increment-log.md`

Verificacion:

- Cambio documental; no requiere build Android/backend.
- `git diff --check` ejecutado sin errores; solo advirtio normalizacion futura LF/CRLF.
- Escaneo basico de secretos ejecutado sobre los archivos modificados; unico match fue una referencia historica textual a `sb_secret_...`, no una clave real.

Uso previsto en la tesis:

- Capitulo 3: evidencia de planificacion incremental antes de incorporar una integracion externa sensible.
- Capitulo 4: soporte para explicar el paso de automatizacion MVP local a notificaciones push reales.
- Capitulo 5: insumo para evaluar trazabilidad entre arquitectura, backlog, tareas, seguridad y evidencia.

Evidencia pendiente:

- Aprobacion humana de PB-020.
- Migracion Supabase `SUPABASE-PUSH-001`.
- Configuracion Firebase/Render sin secretos versionados.
- Smoke test real `QA-PUSH-001` en dispositivo.

## SUPABASE-PUSH-001-DEVICE-PUSH-REGISTRATION-SCHEMA

Fecha: 2026-07-10.

Estado: migracion preparada y lista para aplicacion manual.

Origen documental:

- `AGENTS.md`
- `docs/FR6-INDEX.md`
- `docs/CONTEXT-000-Master-Context.md`
- `docs/CONTEXT-001-Documentation-Map.md`
- `docs/CONTEXT-002-Codex-Reading-Protocol.md`
- `docs/CONTEXT-003-Task-Context-Packs.md`
- `docs/FR5-INDEX.md`
- `docs/09-implementation/BACKLOG-001-Product-Backlog.md`
- `docs/09-implementation/integration/PUSH-PLAN-001-Real-Push-Notifications-Plan.md`
- `docs/09-implementation/backend/DB-001-Database-Schema-Planning.md`
- `docs/09-implementation/backend/API-001-Backend-API-Contract-Draft.md`
- `docs/04-architecture/ARC-003-Backend-Architecture.md`
- `docs/04-architecture/ADR-004-Backend-Strategy.md`
- `docs/04-architecture/ARC-006-Notification-Architecture.md`
- `docs/05-domain/business-rules/BR-004-Notification-Eligibility.md`
- `docs/05-domain/requirements/REQ-006-Notification-Management.md`
- `docs/05-domain/use-cases/UC-006-Receive-Rain-Alert.md`
- `docs/11-quality/QA-001-Definition-of-Done.md`
- `docs/11-quality/QA-003-Implementation-Review-Gate.md`

Alcance aprobado:

- Crear una migracion idempotente para registrar dispositivos push y eventos de notificacion.
- Mantener la persistencia de destinatarios push como responsabilidad del backend.
- Activar RLS y evitar politicas directas de escritura desde Android.
- Documentar aplicacion manual y queries de verificacion.
- No modificar Android, backend ni configuracion Firebase todavia.

Archivos principales:

- `supabase/migrations/003_push_notifications.sql`
- `docs/09-implementation/integration/SUPABASE-PUSH-001-Device-Push-Registration-Schema.md`
- `docs/09-implementation/BACKLOG-001-Product-Backlog.md`
- `docs/09-implementation/backend/DB-001-Database-Schema-Planning.md`
- `tesis-final/evidence/increment-log.md`

Verificacion:

- Pendiente ejecutar `git diff --check`.
- Pendiente escaneo basico de secretos.
- Pendiente aplicacion manual en Supabase SQL Editor.
- Primer intento manual detecto una tabla `notification_events` preexistente sin columna `device_registration_id`; la migracion fue endurecida con `alter table ... add column if not exists` antes de crear indices.

Uso previsto en la tesis:

- Capitulo 3: evidencia de separacion entre planificacion, migracion de datos e implementacion de codigo.
- Capitulo 4: soporte para explicar la frontera Backend -> Supabase para notificaciones push reales.
- Capitulo 5: insumo para evaluar trazabilidad y seguridad antes de integrar un proveedor externo.

Evidencia pendiente:

- Aplicar `supabase/migrations/003_push_notifications.sql` manualmente.
- Ejecutar queries de verificacion de tablas, indices y politicas.
- Implementar `BACKEND-PUSH-001` para preparar configuracion Firebase Admin sin secretos versionados.

## BACKEND-PUSH-001-FIREBASE-ADMIN-CONFIGURATION-BOUNDARY

Fecha: 2026-07-10.

Estado: implementado y verificado por tests backend y build.

Origen documental:

- `docs/09-implementation/BACKLOG-001-Product-Backlog.md`
- `docs/09-implementation/integration/PUSH-PLAN-001-Real-Push-Notifications-Plan.md`
- `docs/09-implementation/integration/SUPABASE-PUSH-001-Device-Push-Registration-Schema.md`
- `docs/04-architecture/ARC-006-Notification-Architecture.md`
- `docs/09-implementation/backend/MOD-002-Backend-Module-Plan.md`
- `docs/07-prompts/CTX-004-Backend-Context.md`
- `docs/11-quality/QA-001-Definition-of-Done.md`

Alcance aprobado:

- Agregar una frontera de configuracion Firebase Admin en backend.
- Mantener push deshabilitado por defecto con `PUSH_PROVIDER=disabled`.
- Validar variables de Firebase solo cuando `PUSH_PROVIDER=fcm`.
- Documentar variables para Render/Firebase sin versionar secretos.
- No instalar `firebase-admin`, no enviar mensajes FCM y no modificar Android.
- Sanear un desajuste de testabilidad en `OpenMeteoWeatherProvider` que impedia ejecutar la suite completa backend.

Archivos principales:

- `backend/src/notifications/firebase-admin.config.ts`
- `backend/src/notifications/firebase-admin.config.spec.ts`
- `backend/src/weather/open-meteo-weather.provider.ts`
- `backend/.env.example`
- `backend/README.md`
- `docs/09-implementation/backend/BACKEND-PUSH-001-Firebase-Admin-Configuration-Boundary.md`
- `docs/09-implementation/BACKLOG-001-Product-Backlog.md`
- `tesis-final/evidence/increment-log.md`

Verificacion:

- `npm test -- --runInBand` en `backend/`: 30 suites exitosas, 81 tests exitosos.
- `npm run build` en `backend/`: build exitoso.
- `git diff --check` acotado a archivos del task: sin errores; solo advertencias LF/CRLF.
- Escaneo basico de secretos sobre archivos modificados: sin matches.

Uso previsto en la tesis:

- Capitulo 4: evidencia de preparacion segura de una integracion externa antes de activar entrega push real.
- Capitulo 5: soporte para discutir control de secretos, configuracion por entorno y avance incremental.

Evidencia pendiente:

- Configurar credenciales reales en Render mediante variable segura o secret file.
- Implementar `BACKEND-PUSH-002` para persistir registros de dispositivo en Supabase.

## BACKEND-PUSH-002-PERSIST-PROTECTED-DEVICE-REGISTRATION

Fecha: 2026-07-10.

Estado: implementado y verificado por tests backend y build.

Origen documental:

- `docs/09-implementation/BACKLOG-001-Product-Backlog.md`
- `docs/09-implementation/integration/PUSH-PLAN-001-Real-Push-Notifications-Plan.md`
- `docs/09-implementation/integration/SUPABASE-PUSH-001-Device-Push-Registration-Schema.md`
- `docs/09-implementation/backend/BACKEND-PUSH-001-Firebase-Admin-Configuration-Boundary.md`
- `docs/04-architecture/ARC-006-Notification-Architecture.md`
- `docs/05-domain/business-rules/BR-004-Notification-Eligibility.md`
- `docs/11-quality/QA-001-Definition-of-Done.md`

Alcance aprobado:

- Persistir `POST /api/v1/notifications/register-device` en Supabase.
- Asociar el registro al usuario autenticado resuelto por el backend.
- Guardar el token FCM recibido como `fcm_registration_token`.
- Respetar `notificationOptIn` mediante `notification_opt_in` y `disabled_at`.
- Mantener fuera de alcance Android token retrieval y envio FCM.

Archivos principales:

- `backend/src/notifications/notifications.supabase-data-source.ts`
- `backend/src/notifications/notifications.supabase-data-source.spec.ts`
- `backend/src/notifications/notifications.service.ts`
- `backend/src/notifications/notifications.service.spec.ts`
- `backend/src/notifications/notifications.controller.ts`
- `backend/src/notifications/notifications.controller.spec.ts`
- `backend/src/notifications/notifications.module.ts`
- `backend/README.md`
- `docs/09-implementation/backend/BACKEND-PUSH-002-Persist-Device-Registration.md`
- `docs/09-implementation/BACKLOG-001-Product-Backlog.md`
- `tesis-final/evidence/increment-log.md`

Verificacion:

- `npm test -- --runInBand` en `backend/`: 31 suites exitosas, 84 tests exitosos.
- `npm run build` en `backend/`: build exitoso.
- `git diff --check` acotado a archivos del task: sin errores; solo advertencias LF/CRLF.
- Escaneo basico de secretos sobre archivos modificados: sin matches.

Uso previsto en la tesis:

- Capitulo 4: evidencia de cierre de la frontera Android -> Backend -> Supabase para registro push.
- Capitulo 5: soporte para evaluar seguridad de ownership, RLS/backend boundary y trazabilidad incremental.

Evidencia pendiente:

- Implementar `ANDROID-PUSH-001` para obtener token FCM y permisos Android.
- Validar manualmente `register-device` contra Render con token real de dispositivo.

## ANDROID-PUSH-001-FIREBASE-MESSAGING-FOUNDATION

Fecha: 2026-07-10.

Estado: implementado y verificado por tests unitarios Android y build debug.

Origen documental:

- `docs/09-implementation/BACKLOG-001-Product-Backlog.md`
- `docs/09-implementation/integration/PUSH-PLAN-001-Real-Push-Notifications-Plan.md`
- `docs/04-architecture/ARC-002-Android-Application-Architecture.md`
- `docs/09-implementation/android/MOD-001-Android-Module-Plan.md`
- `docs/07-prompts/CTX-002-Android-Architecture-Context.md`
- `docs/11-quality/QA-001-Definition-of-Done.md`
- Firebase Cloud Messaging Android documentation.
- Android notification runtime permission documentation.

Alcance aprobado:

- Agregar Firebase Messaging a Android sobre la configuracion Firebase existente.
- Declarar el servicio `FirebaseMessagingService`.
- Persistir localmente el ultimo FCM registration token recibido.
- Crear una frontera para recuperar el token actual.
- Modelar el estado del permiso runtime de notificaciones en Android 13+.
- No registrar todavia el token contra backend ni pedir permiso al iniciar la app.

Archivos principales:

- `gradle/libs.versions.toml`
- `app/build.gradle.kts`
- `app/src/main/AndroidManifest.xml`
- `app/src/main/java/com/tesis_pro/tenderapp/data/notification/FirebasePushTokenStore.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/data/notification/FirebasePushTokenProvider.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/data/notification/NotificationPermissionState.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/data/notification/TenderFirebaseMessagingService.kt`
- `app/src/test/java/com/tesis_pro/tenderapp/data/notification/NotificationPermissionPolicyTest.kt`
- `docs/09-implementation/android/ANDROID-PUSH-001-Firebase-Messaging-Foundation.md`

Verificacion:

- `.\gradlew.bat --no-daemon :app:testDebugUnitTest` con `JAVA_HOME=E:\android\jbr`.
- Resultado: tests exitosos.
- `.\gradlew.bat --no-daemon :app:assembleDebug` con `JAVA_HOME=E:\android\jbr`.
- Resultado: build exitoso.
- `processDebugGoogleServices` resolvio la configuracion Android Firebase existente.

Uso previsto en la tesis:

- Capitulo 4: evidencia de integracion incremental de Firebase Messaging del lado Android.
- Capitulo 5: soporte para discutir control de permisos, separacion Android/backend y transicion desde notificaciones simuladas a push real.

Evidencia pendiente:

- Implementar `ANDROID-PUSH-002` para registrar el token/FID contra backend despues del login.
- Ejecutar `QA-PUSH-001` con dispositivo o emulador capaz de recibir FCM.

## ANDROID-PUSH-002-REGISTER-PUSH-RECIPIENT-WITH-BACKEND

Fecha: 2026-07-10.

Estado: implementado y verificado por tests unitarios Android y build debug.

Origen documental:

- `docs/09-implementation/BACKLOG-001-Product-Backlog.md`
- `docs/09-implementation/integration/PUSH-PLAN-001-Real-Push-Notifications-Plan.md`
- `docs/09-implementation/backend/BACKEND-PUSH-002-Persist-Device-Registration.md`
- `docs/09-implementation/android/ANDROID-PUSH-001-Firebase-Messaging-Foundation.md`
- `docs/04-architecture/ARC-002-Android-Application-Architecture.md`
- `docs/09-implementation/android/MOD-001-Android-Module-Plan.md`
- `docs/07-prompts/CTX-002-Android-Architecture-Context.md`
- `docs/11-quality/QA-001-Definition-of-Done.md`
- Firebase Cloud Messaging Android documentation.
- Android notification runtime permission documentation.

Alcance aprobado:

- Registrar el destinatario push Android contra el backend protegido despues de un login exitoso.
- Recuperar el FCM registration token mediante la frontera creada en `ANDROID-PUSH-001`.
- Enviar `deviceToken`, `platform`, `pushProvider`, `notificationOptIn` y `appVersion`.
- Mantener el registro push como best-effort para no bloquear la sesion de usuario.
- No solicitar permisos runtime ni enviar push real todavia.

Archivos principales:

- `app/src/main/java/com/tesis_pro/tenderapp/data/remote/dto/NotificationDtos.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/data/remote/TenderNotificationApi.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/data/remote/BackendNotificationApiClient.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/data/remote/RemoteNotificationDataSource.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/data/notification/PostLoginPushRegistrar.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/ui/screens/AuthScreen.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/ui/screens/AuthViewModel.kt`
- `app/src/test/java/com/tesis_pro/tenderapp/data/remote/RemoteNotificationDataSourceTest.kt`
- `app/src/test/java/com/tesis_pro/tenderapp/data/notification/PostLoginPushRegistrarTest.kt`
- `app/src/test/java/com/tesis_pro/tenderapp/ui/screens/AuthViewModelTest.kt`
- `docs/09-implementation/android/ANDROID-PUSH-002-Register-Push-Recipient-With-Backend.md`

Verificacion:

- `.\gradlew.bat --no-daemon :app:testDebugUnitTest` con `JAVA_HOME=E:\android\jbr`.
- Resultado: tests exitosos.
- `.\gradlew.bat --no-daemon :app:assembleDebug` con `JAVA_HOME=E:\android\jbr`.
- Resultado: build exitoso.

Uso previsto en la tesis:

- Capitulo 4: evidencia de integracion Android -> Backend para persistencia de destinatarios push.
- Capitulo 5: soporte para evaluar seguridad, trazabilidad y separacion de responsabilidades entre app, backend, Firebase y Supabase.

Evidencia pendiente:

- Validar manualmente que Render reciba `POST /api/v1/notifications/register-device` desde un dispositivo autenticado.
- Implementar `BACKEND-PUSH-003` para disponer de una ruta controlada de envio push de prueba.
- Ejecutar `QA-PUSH-001` con evidencia de recepcion en dispositivo.

## BACKEND-PUSH-003-PROTECTED-TEST-PUSH-ENDPOINT

Fecha: 2026-07-10.

Estado: implementado y verificado por tests backend y build.

Origen documental:

- `docs/09-implementation/BACKLOG-001-Product-Backlog.md`
- `docs/09-implementation/integration/PUSH-PLAN-001-Real-Push-Notifications-Plan.md`
- `docs/09-implementation/backend/BACKEND-PUSH-001-Firebase-Admin-Configuration-Boundary.md`
- `docs/09-implementation/backend/BACKEND-PUSH-002-Persist-Device-Registration.md`
- `docs/09-implementation/android/ANDROID-PUSH-002-Register-Push-Recipient-With-Backend.md`
- `docs/04-architecture/ARC-006-Notification-Architecture.md`
- `docs/11-quality/QA-001-Definition-of-Done.md`
- Firebase Admin SDK setup documentation.
- Firebase Cloud Messaging Admin SDK send documentation.

Alcance aprobado:

- Agregar `firebase-admin` como dependencia backend.
- Crear un gateway de Firebase Admin para envio FCM controlado.
- Exponer `POST /api/v1/notifications/test-push` protegido por autenticacion.
- Resolver el dispositivo desde el ultimo registro Android activo del usuario autenticado.
- Enviar un mensaje generico `SYSTEM_TEST`.
- No implementar aun orquestacion de eventos de dominio ni auditoria persistente en `notification_events`.

Archivos principales:

- `backend/package.json`
- `backend/package-lock.json`
- `backend/src/notifications/firebase-admin-messaging.gateway.ts`
- `backend/src/notifications/dto/test-push-request.dto.ts`
- `backend/src/notifications/dto/test-push-response.dto.ts`
- `backend/src/notifications/notifications.controller.ts`
- `backend/src/notifications/notifications.service.ts`
- `backend/src/notifications/notifications.supabase-data-source.ts`
- `backend/src/notifications/notifications.module.ts`
- `backend/src/notifications/notifications.controller.spec.ts`
- `backend/src/notifications/notifications.service.spec.ts`
- `backend/src/notifications/notifications.supabase-data-source.spec.ts`
- `backend/src/weather/open-meteo-weather.provider.spec.ts`
- `backend/README.md`
- `docs/09-implementation/backend/BACKEND-PUSH-003-Protected-Test-Push-Endpoint.md`
- `docs/09-implementation/BACKLOG-001-Product-Backlog.md`

Verificacion:

- `npm test -- --runInBand` en `backend/`: 31 suites exitosas, 88 tests exitosos.
- `npm run build` en `backend/`: build exitoso.

Observaciones:

- El ajuste del test `OpenMeteoWeatherProvider` respeta el constructor actual del provider y actualiza la inyeccion de test mediante `OPEN_METEO_BASE_URL` y `globalThis.fetch`.
- Firebase documenta FID como destino recomendado y registration token como ruta de migracion; este task usa `fcm_registration_token` porque es el valor que Android registra actualmente.

Uso previsto en la tesis:

- Capitulo 4: evidencia de integracion controlada backend -> Firebase Cloud Messaging.
- Capitulo 5: soporte para evaluar seguridad, trazabilidad y separacion entre canal de prueba y orquestacion de dominio.

Evidencia pendiente:

- Desplegar en Render con `firebase-admin` y credenciales Firebase Admin configuradas.
- Ejecutar `QA-PUSH-001`: login Android, registro de dispositivo, llamada a `test-push`, recepcion en dispositivo.
- Registrar captura/log de Render y captura del dispositivo sin exponer tokens.

## QA-PUSH-001-REAL-PUSH-SMOKE-TEST-RUNBOOK

Fecha: 2026-07-10.

Estado: runbook preparado; ejecucion manual pendiente.

Origen documental:

- `AGENTS.md`
- `docs/README.md`
- `docs/FR6-INDEX.md`
- `docs/CONTEXT-000-Master-Context.md`
- `docs/CONTEXT-001-Documentation-Map.md`
- `docs/CONTEXT-002-Codex-Reading-Protocol.md`
- `docs/CONTEXT-003-Task-Context-Packs.md`
- `docs/FR5-INDEX.md`
- `docs/09-implementation/BACKLOG-001-Product-Backlog.md`
- `docs/09-implementation/integration/PUSH-PLAN-001-Real-Push-Notifications-Plan.md`
- `docs/09-implementation/backend/BACKEND-PUSH-003-Protected-Test-Push-Endpoint.md`
- `docs/09-implementation/MVP-DEPLOY-001-Deployed-MVP-Checklist.md`
- `docs/09-implementation/integration/MVP-SMOKE-TEST-001-Manual-Android-Smoke-Test.md`
- `docs/11-quality/QA-001-Definition-of-Done.md`
- `docs/11-quality/QA-003-Implementation-Review-Gate.md`

Alcance aprobado:

- Preparar el runbook de validacion manual de push real.
- Registrar precondiciones de Render, Firebase Admin, Supabase y Android.
- Agregar nota explicita sobre cold start de Render para no confundir demora inicial con fallo real.
- Mantener tokens y secretos fuera de la evidencia.
- No declarar el smoke test como pasado hasta que el dispositivo reciba o registre el push.

Archivos principales:

- `docs/09-implementation/integration/QA-PUSH-001-Real-Push-Smoke-Test.md`
- `docs/09-implementation/BACKLOG-001-Product-Backlog.md`
- `docs/09-implementation/MVP-DEPLOY-001-Deployed-MVP-Checklist.md`
- `docs/09-implementation/backend/BACKEND-DEPLOY-003-Health-And-Supabase-Verification.md`
- `tesis-final/evidence/increment-log.md`

Verificacion:

- Cambio documental; no requiere build.
- Pendiente ejecutar smoke test real despues de deploy y captura de evidencia.

Uso previsto en la tesis:

- Capitulo 3: evidencia de control humano y trazabilidad antes de validar una integracion externa.
- Capitulo 4: runbook para demostrar el flujo Android -> Render -> Supabase -> Firebase -> dispositivo.
- Capitulo 5: soporte para diferenciar limitaciones de infraestructura gratuita, proveedor push y comportamiento de la app.

Evidencia pendiente:

- Health checks en Render despues del cold start.
- Registro Android de dispositivo contra backend desplegado.
- Respuesta `SENT` de `test-push`.
- Captura/log de recepcion en dispositivo sin exponer tokens.

## ANDROID-AUTH-009-SUPABASE-AUTH-REDIRECT-CONFIGURATION

Fecha: 2026-07-10.

Estado: implementado y verificado por tests unitarios Android y build debug.

Origen documental:

- `AGENTS.md`
- `docs/README.md`
- `docs/FR6-INDEX.md`
- `docs/CONTEXT-000-Master-Context.md`
- `docs/CONTEXT-001-Documentation-Map.md`
- `docs/CONTEXT-002-Codex-Reading-Protocol.md`
- `docs/FR5-INDEX.md`
- `docs/09-implementation/BACKLOG-001-Product-Backlog.md`
- `docs/09-implementation/android/ANDROID-AUTH-001-MVP-Auth-Task-Plan.md`
- Supabase Auth signUp documentation.
- Supabase Auth resetPasswordForEmail documentation.
- Supabase Auth Redirect URLs documentation.

Alcance aprobado:

- Agregar `SUPABASE_AUTH_REDIRECT_URL` como configuracion publica Android.
- Enviar `redirect_to` en signup y password recovery cuando el valor esta configurado.
- Mantener el valor opcional para no romper builds locales.
- Documentar que Supabase `Site URL` y allowed redirect URLs deben configurarse en dashboard.
- No implementar aun deep links Android ni pantalla final de cambio de password.

Archivos principales:

- `app/build.gradle.kts`
- `app/src/main/java/com/tesis_pro/tenderapp/data/auth/SupabaseAuthConfig.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/data/auth/SupabaseAuthApi.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/data/auth/SupabaseAuthRepository.kt`
- `app/src/test/java/com/tesis_pro/tenderapp/data/auth/SupabaseAuthConfigTest.kt`
- `app/src/test/java/com/tesis_pro/tenderapp/data/auth/SupabaseAuthRepositoryTest.kt`
- `docs/09-implementation/android/ANDROID-AUTH-009-Supabase-Auth-Redirect-Configuration.md`
- `docs/09-implementation/android/ANDROID-AUTH-001-MVP-Auth-Task-Plan.md`
- `docs/09-implementation/BACKLOG-001-Product-Backlog.md`

Verificacion:

- `.\gradlew.bat --no-daemon :app:testDebugUnitTest` con `JAVA_HOME=E:\android\jbr`.
- Resultado: tests exitosos.
- `.\gradlew.bat --no-daemon :app:assembleDebug` con `JAVA_HOME=E:\android\jbr`.
- Resultado: build exitoso.

Uso previsto en la tesis:

- Capitulo 4: evidencia de ajuste incremental del flujo de autenticacion real con Supabase.
- Capitulo 5: soporte para analizar configuracion externa, callbacks y limites entre app y proveedor.

Evidencia pendiente:

- Configurar en Supabase Auth URL Configuration el deep link permitido.
- Ejecutar `QA-AUTH-001` sin registrar tokens ni enlaces sensibles.

## ANDROID-ARCH-001-REPOSITORY-CONTRACT-GAP-IDENTIFIED

Fecha: 2026-07-10.

Estado: brecha arquitectonica identificada; implementacion pendiente.

Origen documental:

- `AGENTS.md`
- `docs/09-implementation/BACKLOG-001-Product-Backlog.md`
- `docs/09-implementation/android/ANDROID-WASHER-001-Connected-Washer-Selection-And-Creation.md`
- `docs/09-implementation/android/ANDROID-WEATHER-001-Dashboard-Consumes-Backend-Weather.md`
- `docs/04-architecture/ARC-004-Repository-and-Data-Flow.md`

Observacion:

- `domain.repository.WasherRepository` y `domain.repository.WeatherRepository` fueron creados como contratos de dominio tempranos.
- Los incrementos de integracion posteriores usan implementaciones concretas `BackendWasherRepository` y `BackendDashboardRepository` directamente desde ViewModels.
- Esta situacion no rompe el MVP actual, pero deja una deuda de arquitectura: alinear contratos de dominio con repositorios backend o retirar/refactorizar contratos no usados.

Uso previsto en la tesis:

- Capitulo 4: evidencia de deuda tecnica identificada durante avance incremental.
- Capitulo 5: ejemplo de como el sistema documental permite detectar desalineaciones entre arquitectura prevista e implementacion real.

Evidencia pendiente:

- Ejecutar `ANDROID-ARCH-001` como incremento separado.
- Verificar que ViewModels dependan de contratos cuando corresponda sin introducir abstracciones innecesarias.

## ANDROID-AUTH-010-AUTH-CALLBACK-DEEP-LINK-HANDLING

Fecha: 2026-07-10.

Estado: implementado y verificado por tests unitarios Android y build debug.

Origen documental:

- `AGENTS.md`
- `docs/README.md`
- `docs/FR6-INDEX.md`
- `docs/CONTEXT-000-Master-Context.md`
- `docs/CONTEXT-001-Documentation-Map.md`
- `docs/CONTEXT-002-Codex-Reading-Protocol.md`
- `docs/CONTEXT-003-Task-Context-Packs.md`
- `docs/FR5-INDEX.md`
- `docs/09-implementation/BACKLOG-001-Product-Backlog.md`
- `docs/09-implementation/android/ANDROID-AUTH-001-MVP-Auth-Task-Plan.md`
- `docs/09-implementation/android/ANDROID-AUTH-009-Supabase-Auth-Redirect-Configuration.md`
- `docs/04-architecture/ARC-002-Android-Application-Architecture.md`
- `docs/04-architecture/ARC-004-Repository-and-Data-Flow.md`
- `docs/07-prompts/PROMPT-010-Single-Task-Implementation.md`
- `docs/11-quality/QA-001-Definition-of-Done.md`

Alcance aprobado:

- Declarar el deep link Android `com.tesispro.tenderapp://auth-callback`.
- Capturar callbacks iniciales y callbacks recibidos con la app abierta.
- Enviar el callback al flujo de autenticacion.
- Parsear tokens Supabase desde query o fragment.
- Guardar sesion local si el callback contiene `access_token` y email recuperable.
- Mantener fuera de alcance la pantalla final de cambio de password.

Archivos principales:

- `app/src/main/AndroidManifest.xml`
- `app/src/main/java/com/tesis_pro/tenderapp/MainActivity.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/ui/TenderApp.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/ui/screens/AuthScreen.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/ui/screens/AuthViewModel.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/data/auth/SupabaseAuthCallbackParser.kt`
- `app/src/test/java/com/tesis_pro/tenderapp/data/auth/SupabaseAuthCallbackParserTest.kt`
- `app/src/test/java/com/tesis_pro/tenderapp/ui/screens/AuthViewModelTest.kt`
- `docs/09-implementation/android/ANDROID-AUTH-010-Auth-Callback-Deep-Link-Handling.md`
- `docs/09-implementation/BACKLOG-001-Product-Backlog.md`
- `docs/09-implementation/android/ANDROID-AUTH-001-MVP-Auth-Task-Plan.md`

Verificacion:

- `.\gradlew.bat --no-daemon :app:testDebugUnitTest` con `JAVA_HOME=E:\android\jbr`.
- Resultado: tests exitosos.
- `.\gradlew.bat --no-daemon :app:assembleDebug` con `JAVA_HOME=E:\android\jbr`.
- Resultado: build exitoso.

Uso previsto en la tesis:

- Capitulo 4: evidencia de cierre incremental entre configuracion Supabase, deep links Android y persistencia de sesion.
- Capitulo 5: soporte para analizar integraciones externas con callbacks, pruebas unitarias y limites de alcance controlados.

Evidencia pendiente:

- Ejecutar smoke manual de password recovery con Supabase.
- Implementar pantalla final para actualizar password despues del recovery callback.
- Registrar capturas sin exponer tokens ni enlaces sensibles.

## QA-AUTH-001-END-TO-END-AUTH-SMOKE

Fecha: 2026-07-10.

Estado: validado manualmente por el autor.

Origen documental:

- `docs/09-implementation/BACKLOG-001-Product-Backlog.md`
- `docs/09-implementation/android/ANDROID-AUTH-001-MVP-Auth-Task-Plan.md`
- `docs/09-implementation/android/ANDROID-AUTH-009-Supabase-Auth-Redirect-Configuration.md`
- `docs/09-implementation/android/ANDROID-AUTH-010-Auth-Callback-Deep-Link-Handling.md`

Alcance validado:

- Registro con email nuevo desde Android.
- Recepcion de email Supabase con `redirect_to=com.tesispro.tenderapp://auth-callback`.
- Apertura del link de verificacion en Android.
- Retorno a TenderApp en el flujo de login.
- Disponibilidad de accion para navegar al dashboard.
- Navegacion efectiva al dashboard.
- Visualizacion de informacion autenticada en dashboard.

Resultado:

- `QA-AUTH-001` se considera validado manualmente para el MVP actual.
- No se versionaron tokens, enlaces completos, claves ni credenciales.

Uso previsto en la tesis:

- Capitulo 4: evidencia de integracion real Android -> Supabase Auth -> callback Android -> dashboard.
- Capitulo 5: soporte para evaluar trazabilidad entre backlog, tareas incrementales, verificacion tecnica y validacion humana.

Evidencia pendiente:

- Guardar capturas o video redaccionados como anexo visual.
- Validar password recovery end-to-end cuando exista pantalla de actualizacion de password.

## ANDROID-SETTINGS-001-CUSTOM-WASHER-CREATION-FORM

Fecha: 2026-07-10.

Estado: implementado y verificado por tests unitarios Android.

Origen documental:

- `AGENTS.md`
- `docs/FR6-INDEX.md`
- `docs/FR5-INDEX.md`
- `docs/CONTEXT-000-Master-Context.md`
- `docs/CONTEXT-001-Documentation-Map.md`
- `docs/CONTEXT-002-Codex-Reading-Protocol.md`
- `docs/CONTEXT-003-Task-Context-Packs.md`
- `docs/09-implementation/BACKLOG-001-Product-Backlog.md`
- `docs/09-implementation/SPRINT-007-Sprint-UIUX-Prototype-Completion.md`
- `docs/09-implementation/android/ANDROID-UI-006-Washer-Management-UI.md`
- `docs/09-implementation/android/ANDROID-UI-007-Settings-Notification-and-Preferences-UI.md`
- `docs/09-implementation/android/ANDROID-WASHER-001-Connected-Washer-Selection-And-Creation.md`
- `docs/06-design/prototypes/Asistente de Lavado.dc.html`
- `docs/06-design/UI-001-Design-System.md`
- `docs/06-design/UI-003-Component-Catalog.md`
- `docs/07-prompts/PROMPT-003-Compose-Screen-Generation.md`
- `docs/08-agents/AG-005-UI-Agent.md`
- `docs/11-quality/QA-001-Definition-of-Done.md`

Alcance aprobado:

- Mantener administracion de lavarropas dentro de Settings para el MVP actual.
- Reemplazar el alta default/demo por un formulario editable.
- Permitir configurar nombre, tipo, capacidad, etiqueta energetica, uso de agua y lavarropas principal.
- Enviar los valores custom al backend de lavarropas.
- Mantener ubicacion como contexto del hogar porque el contrato actual de lavarropas no persiste ubicacion por equipo.
- Localizar textos visibles en ingles y espanol.
- No agregar una pestana top-level nueva.

Archivos principales:

- `app/src/main/java/com/tesis_pro/tenderapp/ui/screens/SettingsViewModel.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/ui/screens/SettingsScreen.kt`
- `app/src/main/res/values/strings.xml`
- `app/src/main/res/values-es/strings.xml`
- `app/src/test/java/com/tesis_pro/tenderapp/ui/screens/SettingsViewModelTest.kt`
- `docs/09-implementation/android/ANDROID-SETTINGS-001-Custom-Washer-Creation-Form.md`
- `docs/09-implementation/BACKLOG-001-Product-Backlog.md`
- `docs/FR5-INDEX.md`

Verificacion:

- `.\gradlew.bat --no-daemon :app:testDebugUnitTest` con `JAVA_HOME=E:\android\jbr`.
- Resultado: tests exitosos.

Uso previsto en la tesis:

- Capitulo 4: evidencia de evolucion desde prototipo/mockup hacia UI conectada y configurable.
- Capitulo 5: soporte para discutir decisiones UX incrementales, alcance controlado y trazabilidad entre observacion humana, backlog y codigo.

Evidencia pendiente:

- Probar manualmente el formulario en dispositivo y capturar evidencia visual sin datos sensibles.
- Ejecutar `ANDROID-SETTINGS-002` para controles manuales de idioma/tema del prototipo.
- Ejecutar `ANDROID-NOTIF-005` para limpiar textos visibles de notificaciones.

## ANDROID-SETTINGS-002-LANGUAGE-THEME-OVERRIDE

Fecha: 2026-07-10.

Estado: implementado y verificado por tests unitarios Android y build debug.

Origen documental:

- `AGENTS.md`
- `docs/FR6-INDEX.md`
- `docs/FR5-INDEX.md`
- `docs/CONTEXT-000-Master-Context.md`
- `docs/CONTEXT-001-Documentation-Map.md`
- `docs/CONTEXT-002-Codex-Reading-Protocol.md`
- `docs/09-implementation/BACKLOG-001-Product-Backlog.md`
- `docs/09-implementation/SPRINT-007-Sprint-UIUX-Prototype-Completion.md`
- `docs/09-implementation/android/ANDROID-SETTINGS-001-Custom-Washer-Creation-Form.md`
- `docs/06-design/prototypes/Asistente de Lavado.dc.html`

Alcance aprobado:

- Agregar override manual de idioma: sistema, espanol, ingles.
- Agregar override manual de tema: sistema, claro, oscuro.
- Persistir preferencias localmente en Android.
- Aplicar tema desde el limite `TenderAppTheme`.
- Aplicar idioma mediante contexto localizado para Compose.
- Mantener persistencia backend fuera de alcance.

Archivos principales:

- `app/src/main/java/com/tesis_pro/tenderapp/data/settings/AppAppearancePreferences.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/MainActivity.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/ui/TenderApp.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/ui/screens/SettingsScreen.kt`
- `app/src/main/res/values/strings.xml`
- `app/src/main/res/values-es/strings.xml`
- `docs/09-implementation/android/ANDROID-SETTINGS-002-Language-Theme-Override.md`
- `docs/09-implementation/BACKLOG-001-Product-Backlog.md`

Verificacion:

- `.\gradlew.bat --no-daemon :app:testDebugUnitTest` con `JAVA_HOME=E:\android\jbr`.
- Resultado: tests exitosos.
- `.\gradlew.bat --no-daemon :app:assembleDebug` con `JAVA_HOME=E:\android\jbr`.
- Resultado: build exitoso.

Uso previsto en la tesis:

- Capitulo 4: evidencia de alineacion incremental entre prototipo original y app Android.
- Capitulo 5: soporte para analizar decisiones de alcance UX, preferencias locales y consistencia multilenguaje.

Evidencia pendiente:

- Validar manualmente en dispositivo cambios de idioma y tema.
- Capturar screenshots redaccionadas para evidencia visual.
- Ejecutar `ANDROID-NOTIF-005` para textos visibles de notificaciones.

## DEPLOYED-BACKEND-PUSH-READINESS-CHECK

Fecha: 2026-07-10.

Estado: verificacion publica ejecutada; endpoints base operativos.

Origen documental:

- `docs/09-implementation/integration/QA-PUSH-001-Real-Push-Smoke-Test.md`
- `docs/09-implementation/backend/BACKEND-PUSH-003-Protected-Test-Push-Endpoint.md`
- `docs/09-implementation/backend/BACKEND-DEPLOY-003-Health-And-Supabase-Verification.md`

Alcance:

- Verificar que Render responde despues de cold start.
- Confirmar conectividad Supabase desde backend desplegado.
- Confirmar que Swagger desplegado expone la ruta `notifications/test-push`.
- No llamar endpoints protegidos ni almacenar tokens.

Resultados:

- `GET https://tesis-t85s.onrender.com/api/v1/health`: HTTP 200, `status: ok`, `version: 0.1.0`.
- `GET https://tesis-t85s.onrender.com/api/v1/health/supabase`: HTTP 200, `status: ok`, `projectHost: rozbtrmvldvsjdudnmxo.supabase.co`, `message: null`.
- `GET https://tesis-t85s.onrender.com/api/v1/docs-json`: HTTP 200.
- Ruta `notifications/test-push`: presente en Swagger JSON desplegado.

Uso previsto en la tesis:

- Evidencia intermedia de readiness antes de ejecutar el smoke push real con dispositivo.
- Registro de que el cold start de Render fue contemplado y que la instancia respondio correctamente.

Evidencia pendiente:

- Llamada protegida `test-push` con bearer token redaccionado.
- Recepcion de push en dispositivo Android.

## ANDROID-NOTIF-005 - Textos localizables de notificaciones

Fecha: 2026-07-10.

Estado: implementado y verificado.

Origen documental:

- `AGENTS.md`
- `docs/README.md`
- `docs/FR6-INDEX.md`
- `docs/FR5-INDEX.md`
- `docs/CONTEXT-000-Master-Context.md`
- `docs/CONTEXT-001-Documentation-Map.md`
- `docs/CONTEXT-002-Codex-Reading-Protocol.md`
- `docs/09-implementation/BACKLOG-001-Product-Backlog.md`
- `docs/09-implementation/SPRINT-007-Sprint-UIUX-Prototype-Completion.md`
- `docs/09-implementation/android/ANDROID-NOTIF-002-Ideal-Hanging-Time-Reminder.md`
- `docs/09-implementation/android/ANDROID-NOTIF-003-Pickup-Reminder.md`
- `docs/04-architecture/ARC-006-Notification-Architecture.md`

Alcance:

- Reemplazar textos finales hardcodeados de notificaciones por claves semanticas.
- Agregar recursos Android en ingles y espanol para titulos y mensajes de notificacion.
- Mantener el dominio sin dependencia de `Context` ni recursos Android.
- Preservar la elegibilidad y simulacion local existente.

Archivos principales:

- `app/src/main/java/com/tesis_pro/tenderapp/domain/notification/NotificationScheduleRequest.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/domain/notification/ScheduleIdealHangingReminderUseCase.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/domain/notification/SchedulePickupReminderUseCase.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/domain/notification/NotificationEligibilityPolicy.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/data/notification/NotificationContentResolver.kt`
- `app/src/main/res/values/strings.xml`
- `app/src/main/res/values-es/strings.xml`
- `app/src/test/java/com/tesis_pro/tenderapp/domain/notification/NotificationEligibilityPolicyTest.kt`
- `app/src/test/java/com/tesis_pro/tenderapp/domain/notification/ScheduleIdealHangingReminderUseCaseTest.kt`
- `app/src/test/java/com/tesis_pro/tenderapp/domain/notification/SchedulePickupReminderUseCaseTest.kt`
- `docs/09-implementation/android/ANDROID-NOTIF-005-Localized-Notification-Content.md`

Uso previsto en la tesis:

- Capitulo 4: evidencia de control incremental entre UX multilenguaje y arquitectura Android.
- Capitulo 5: discusion sobre separacion entre claves semanticas de dominio y resolucion localizable en la capa Android.

Verificacion:

- `.\gradlew.bat --no-daemon :app:testDebugUnitTest` con `JAVA_HOME=E:\android\jbr`.
- Resultado: tests exitosos.
- `.\gradlew.bat --no-daemon :app:assembleDebug` con `JAVA_HOME=E:\android\jbr`.
- Resultado: build exitoso.

Evidencia pendiente:

- Conectar el resolver al limite real de notificacion cuando exista delivery nativo o WorkManager.

## BACKEND-PUSH-004 - Evento de dominio conectado a push real

Fecha: 2026-07-10.

Estado: implementado y verificado.

Origen documental:

- `AGENTS.md`
- `docs/README.md`
- `docs/FR6-INDEX.md`
- `docs/FR5-INDEX.md`
- `docs/CONTEXT-000-Master-Context.md`
- `docs/CONTEXT-001-Documentation-Map.md`
- `docs/CONTEXT-002-Codex-Reading-Protocol.md`
- `docs/CONTEXT-003-Task-Context-Packs.md`
- `docs/09-implementation/BACKLOG-001-Product-Backlog.md`
- `docs/09-implementation/integration/PUSH-PLAN-001-Real-Push-Notifications-Plan.md`
- `docs/09-implementation/integration/QA-PUSH-001-Real-Push-Smoke-Test.md`
- `docs/09-implementation/backend/BACKEND-PUSH-003-Protected-Test-Push-Endpoint.md`
- `docs/04-architecture/ARC-006-Notification-Architecture.md`
- `docs/11-quality/QA-001-Definition-of-Done.md`
- `docs/11-quality/QA-002-Acceptance-Criteria-Matrix.md`
- `docs/11-quality/QA-003-Implementation-Review-Gate.md`

Alcance:

- Conectar el estado `COMPLETED` de una carga de ropa con un intento de push `DRYING_COMPLETE`.
- Usar el ultimo dispositivo FCM activo del usuario autenticado.
- Auditar el resultado en `notification_events` con `SENT`, `SKIPPED` o `FAILED`.
- Evitar que una falla de FCM rompa la actualizacion de estado de la carga.

Archivos principales:

- `backend/src/laundry-loads/laundry-loads.service.ts`
- `backend/src/laundry-loads/laundry-loads.module.ts`
- `backend/src/laundry-loads/laundry-loads.service.spec.ts`
- `backend/src/notifications/notifications.service.ts`
- `backend/src/notifications/notifications.module.ts`
- `backend/src/notifications/notifications.supabase-data-source.ts`
- `backend/src/notifications/notifications.service.spec.ts`
- `backend/src/notifications/notifications.supabase-data-source.spec.ts`
- `docs/09-implementation/backend/BACKEND-PUSH-004-Connect-Domain-Notification-Events.md`
- `docs/09-implementation/integration/QA-PUSH-001-Real-Push-Smoke-Test.md`

Verificacion:

- `npm test -- --runInBand`.
- Resultado: tests exitosos.
- `npm run build`.
- Resultado: build exitoso.

Uso previsto en la tesis:

- Capitulo 4: evidencia de una vertical backend-Supabase-Firebase vinculada a una accion de dominio.
- Capitulo 5: discusion sobre trazabilidad y auditabilidad de eventos en un MVP asistido por IA.

Evidencia pendiente:

- Desplegar en Render.
- Ejecutar `QA-PUSH-001` con dispositivo Android y evidencia redaccionada.

## SUPABASE-PUSH-002 - Reset canonico de notification_events

Fecha: 2026-07-10.

Estado: preparado para aplicacion manual.

Contexto:

- Se inspecciono el esquema real de Supabase y se detecto una tabla `notification_events` legacy vacia.
- La tabla legacy contenia columnas obligatorias `type`, `title`, `body` y estados en minuscula.
- El backend PB-020 usa un modelo canonico mas limpio basado en `category`, `title_key`, `body_key`, `payload`, estados en mayuscula y campos de auditoria de proveedor.

Decision:

- Como la tabla estaba vacia, se eligio resetearla al modelo canonico en vez de mantener compatibilidad legacy.
- La migracion incluye una guarda: si existen filas, falla antes de borrar la tabla.

Archivos principales:

- `supabase/README.md`
- `supabase/migrations/004_notification_events_canonical_reset.sql`
- `docs/09-implementation/integration/SUPABASE-PUSH-001-Device-Push-Registration-Schema.md`
- `docs/09-implementation/backend/BACKEND-PUSH-004-Connect-Domain-Notification-Events.md`
- `backend/src/notifications/notifications.supabase-data-source.ts`
- `backend/src/notifications/notifications.service.ts`
- `backend/src/notifications/notifications.supabase-data-source.spec.ts`

Uso previsto en la tesis:

- Capitulo 4: evidencia de ajuste controlado entre base real y contrato backend.
- Capitulo 5: decision tecnica justificable: eliminar deuda de compatibilidad cuando no hay datos productivos que preservar.

Evidencia pendiente:

- Aplicar manualmente `supabase/migrations/004_notification_events_canonical_reset.sql`.
- Verificar que `notification_events` ya no tenga `type`, `title` ni `body`.
- Repetir el `PATCH` de carga completada y confirmar fila `DRYING_COMPLETE`.

## ANDROID-PUSH-003 - Registro push con sesion restaurada

Fecha: 2026-07-10.

Estado: implementado y verificado.

Contexto:

- QA real confirmo que `notification_events` registra `DRYING_COMPLETE` con `SKIPPED`.
- El error observado fue `NO_ACTIVE_FCM_DEVICE`.
- La consulta a `device_push_registrations` para el usuario autenticado devolvio cero filas.
- Se identifico que la app podia iniciar directo en Dashboard con sesion persistida, sin crear `AuthViewModel`; por lo tanto no ejecutaba `registerAfterLogin`.

Alcance:

- Registrar el destinatario FCM al iniciar la app si existe sesion almacenada.
- Reutilizar el limite backend existente `POST /notifications/register-device`.
- Mantener el registro como best-effort y no bloquear navegacion.
- No borrar usuarios de Supabase para ocultar el problema.

Archivos principales:

- `app/src/main/java/com/tesis_pro/tenderapp/data/notification/StoredSessionPushRegistration.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/ui/TenderApp.kt`
- `app/src/test/java/com/tesis_pro/tenderapp/data/notification/StoredSessionPushRegistrationTest.kt`
- `docs/09-implementation/android/ANDROID-PUSH-003-Restored-Session-Push-Registration.md`

Verificacion:

- `.\gradlew.bat --no-daemon :app:testDebugUnitTest` con `JAVA_HOME=E:\android\jbr`.
- Resultado: tests exitosos.
- `.\gradlew.bat --no-daemon :app:assembleDebug` con `JAVA_HOME=E:\android\jbr`.
- Resultado: build exitoso.

Uso previsto en la tesis:

- Capitulo 4: evidencia de hardening guiado por QA del circuito Android-Backend-Supabase-Firebase.
- Capitulo 5: ejemplo de trazabilidad entre evento auditado, causa raiz y correccion incremental.

Evidencia pendiente:

- Abrir Android con sesion almacenada y verificar `POST /notifications/register-device` en Render.
- Confirmar fila activa en `device_push_registrations`.
- Repetir carga `COMPLETED` hasta obtener `SENT`.

## BACKEND-PUSH-005 - Hardening de persistencia de registro push

Fecha: 2026-07-10.

Estado: implementado y verificado por tests/build backend.

Contexto:

- Android confirmo login exitoso y llamadas reales a `POST https://tesis-t85s.onrender.com/api/v1/notifications/register-device`.
- El endpoint desplegado respondio HTTP 500.
- La evidencia previa mostraba eventos `DRYING_COMPLETE` con `SKIPPED` y `NO_ACTIVE_FCM_DEVICE`.
- En Render se observo ademas un warning de auditoria con error serializado como `[object Object]`, insuficiente para diagnostico.

Decision tecnica:

- No borrar usuarios ni ocultar el problema de integracion.
- Mantener Android consumiendo el backend.
- Reemplazar el `upsert` Supabase con `onConflict: fcm_registration_token` por una secuencia explicita `lookup -> update -> insert`.
- Mejorar logs operacionales de backend para mostrar campos seguros `code`, `message`, `details` y `hint` sin registrar tokens.

Archivos principales:

- `backend/src/notifications/notifications.supabase-data-source.ts`
- `backend/src/notifications/notifications.supabase-data-source.spec.ts`
- `backend/src/notifications/notifications.service.ts`
- `docs/09-implementation/backend/BACKEND-PUSH-005-Device-Registration-Persistence-Hardening.md`
- `docs/09-implementation/BACKLOG-001-Product-Backlog.md`
- `docs/09-implementation/integration/QA-PUSH-001-Real-Push-Smoke-Test.md`

Verificacion:

- `npm test -- --runInBand`.
- Resultado: 31 suites backend exitosas, 94 tests exitosos.
- `npm run build`.
- Resultado: build backend exitoso.

Uso previsto en la tesis:

- Capitulo 4: evidencia de un ciclo real de depuracion entre Android, Render, Supabase y backend.
- Capitulo 5: soporte para analizar auditabilidad, trazabilidad de errores y endurecimiento incremental de integraciones externas.

Evidencia pendiente:

- Desplegar el cambio en Render.
- Repetir login Android sin registrar tokens en evidencia.
- Confirmar fila activa en `device_push_registrations`.
- Repetir finalizacion de carga y confirmar evento `DRYING_COMPLETE` con estado distinto de `SKIPPED/NO_ACTIVE_FCM_DEVICE` cuando exista dispositivo activo.

## QA-PUSH-001 - Avance de smoke real con dispositivo registrado

Fecha: 2026-07-10.

Estado: validacion manual parcial; falla aislada en delivery FCM.

Evidencia observada:

- Android registro el destinatario push al iniciar con sesion restaurada: `startup registration: registered`.
- El backend desplegado respondio correctamente a clima: `GET /api/v1/weather/current?locationId=home` con HTTP 200.
- Al completar la carga `3ac4b7d1-c0f5-48bd-b129-181ed650b2b3`, Render registro: `Laundry completion push skipped`.
- La fila de auditoria ya contiene `device_registration_id`.
- El resultado cambio de `NO_ACTIVE_FCM_DEVICE` a `FCM_SEND_FAILED`.

Interpretacion:

- La frontera Android -> backend -> Supabase para registro de dispositivo queda validada.
- El problema restante ya no es ausencia de dispositivo, sino envio efectivo mediante Firebase Cloud Messaging o configuracion Firebase Admin.
- El proximo diagnostico debe revisar logs formateados de Render, alineacion de proyecto Firebase, credenciales de service account y origen del token FCM de Android.

Uso previsto en la tesis:

- Capitulo 4: evidencia de narrowing incremental de fallas en una integracion externa real.
- Capitulo 5: ejemplo de como la auditoria de eventos permite distinguir entre falta de destinatario y falla del proveedor push.

Evidencia pendiente:

- Capturar el error FCM formateado sin tokens ni credenciales.
- Ejecutar `test-push` protegido contra el mismo usuario.
- Confirmar si el error corresponde a credenciales Firebase Admin, proyecto Firebase no alineado, token invalido/no registrado o permisos de dispositivo.

## QA-PUSH-001 - Backend a FCM validado como SENT

Fecha: 2026-07-10.

Estado: validado backend -> Firebase Cloud Messaging; visualizacion Android pendiente.

Evidencia observada:

- Supabase `notification_events` registro `DRYING_COMPLETE` con `status = SENT`.
- El evento contiene `device_registration_id`.
- El evento contiene `provider_message_id` de Firebase: `projects/tesis-tender-app/messages/...`.
- `error_code` quedo en `null`.

Interpretacion:

- El backend desplegado ya tiene `PUSH_PROVIDER=fcm` correctamente configurado.
- Firebase Admin acepto el mensaje y devolvio identificador de proveedor.
- El hito backend -> Supabase -> FCM queda validado.
- El telefono no mostro notificacion visible, por lo que el siguiente problema se ubica en Android display path, permiso o canal de notificacion.

Uso previsto en la tesis:

- Capitulo 4: evidencia de integracion real con Firebase Cloud Messaging.
- Capitulo 5: soporte para separar aceptacion del proveedor de visualizacion final en el dispositivo.

Evidencia pendiente:

- Captura visual o Logcat de recepcion/display en Android.
- Validar foreground y background.
- Confirmar permiso `POST_NOTIFICATIONS` concedido.

## ANDROID-PUSH-004 - Visualizacion Android de notificaciones FCM

Fecha: 2026-07-10.

Estado: implementado y verificado por build/tests; validacion visual pendiente.

Contexto:

- FCM acepto el mensaje (`SENT`) pero el celular no mostro una notificacion visible.
- Logcat reporto: `Missing Default Notification Channel metadata in AndroidManifest. Default value will be used.`

Alcance:

- Declarar el canal default de Firebase Messaging en `AndroidManifest.xml`.
- Crear el canal `tenderapp_laundry_alerts` en el inicio de la aplicacion.
- Manejar `onMessageReceived` en `TenderFirebaseMessagingService`.
- Mostrar una notificacion local con titulo/body recibido o fallback localizado.
- Respetar permiso `POST_NOTIFICATIONS` en Android 13+.

Archivos principales:

- `app/src/main/AndroidManifest.xml`
- `app/src/main/java/com/tesis_pro/tenderapp/TenderApplication.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/data/notification/TenderFirebaseMessagingService.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/data/notification/LaundryNotificationChannels.kt`
- `app/src/main/java/com/tesis_pro/tenderapp/data/notification/FcmNotificationRenderer.kt`
- `app/src/main/res/values/strings.xml`
- `app/src/main/res/values-es/strings.xml`
- `docs/09-implementation/android/ANDROID-PUSH-004-FCM-Notification-Display.md`

Verificacion:

- `.\gradlew.bat --no-daemon :app:assembleDebug`.
- Resultado: build exitoso.
- `.\gradlew.bat --no-daemon :app:testDebugUnitTest`.
- Resultado: tests exitosos.

Observacion:

- Durante `assembleDebug`, Kotlin reporto una falla de compilacion incremental por cache ya registrado, hizo fallback a compilacion no incremental y termino con `BUILD SUCCESSFUL`.

Uso previsto en la tesis:

- Capitulo 4: evidencia del ultimo tramo del circuito push, donde `SENT` del proveedor no implica automaticamente visualizacion final.
- Capitulo 5: ejemplo de QA incremental guiado por Logcat y evidencia de proveedor.

Evidencia pendiente:

- Instalar APK nuevo.
- Conceder permiso de notificaciones si Android lo solicita.
- Repetir evento `DRYING_COMPLETE`.
- Confirmar si desaparece el warning de canal default y aparece notificacion visible.

## QA-PUSH-001 - Visualizacion Android observada y brecha de permiso

Fecha: 2026-07-10.

Estado: notificacion visible observada; brecha de permiso identificada.

Evidencia observada:

- El celular mostro la notificacion despues del ajuste de canal/display.
- Se detecto que TenderApp no habia pedido previamente el permiso Android de notificaciones.
- La falta de permiso runtime explicaba por que el usuario podia tener preferencias internas activas sin delivery visible.

Interpretacion:

- El circuito backend -> FCM -> Android display queda funcional.
- Falta sincronizar la capa de Settings con el permiso runtime de Android para que el usuario entienda y pueda habilitar notificaciones desde la app.
- En Android 12 o menor el permiso no es requerido; en Android 13+ debe solicitarse explicitamente.

Uso previsto en la tesis:

- Capitulo 4: evidencia de validacion real en dispositivo y ajuste final de experiencia de permisos.
- Capitulo 5: caso concreto de diferencia entre preferencia de producto y permiso de plataforma.

## ANDROID-PUSH-005 - Sincronizacion de permiso de notificaciones en Settings

Fecha: 2026-07-10.

Estado: implementado y verificado por tests/build; validacion manual pendiente.

Alcance:

- Leer estado runtime de permiso de notificaciones desde Settings.
- Mostrar si el permiso no es requerido, concedido o requerido.
- Permitir solicitar `POST_NOTIFICATIONS` desde Settings en Android 13+.
- Pausar efectivamente las categorias de notificacion si el permiso runtime falta.
- Mantener sin cambios backend, Supabase y Firebase Admin.

Archivos principales:

- `app/src/main/java/com/tesis_pro/tenderapp/ui/screens/SettingsScreen.kt`
- `app/src/main/res/values/strings.xml`
- `app/src/main/res/values-es/strings.xml`
- `docs/09-implementation/android/ANDROID-PUSH-005-Settings-Notification-Permission-Sync.md`
- `docs/09-implementation/BACKLOG-001-Product-Backlog.md`

Verificacion:

- `.\gradlew.bat --no-daemon :app:testDebugUnitTest`.
- Resultado: tests exitosos.
- `.\gradlew.bat --no-daemon :app:assembleDebug`.
- Resultado: build exitoso.

## MVP-INCREMENT-004 - Definicion formal del proximo incremento

Fecha: 2026-07-11.

Estado: documentado; implementacion pendiente por tareas controlables.

Decision:

- El proximo incremento se orienta a consolidar el flujo principal de TenderApp: dashboard, ubicacion de secado, datos meteorologicos, prediccion y notificaciones automaticas.
- El incremento queda nombrado como `Prediccion climatica, ubicaciones de secado y notificaciones automaticas`.
- Se crea `PB-021` para agrupar las tareas del incremento sin mezclarlo con la validacion push ya realizada en `PB-020`.

Objetivo de valor:

- Permitir que TenderApp recomiende y automatice acciones asociadas al secado de ropa, utilizando informacion climatica realista, ubicacion de secado y notificaciones al usuario.

Documentos actualizados:

- `docs/09-implementation/MVP-INCREMENT-004-Weather-Drying-Notifications.md`.
- `docs/09-implementation/MVP-INCREMENTS-001-Incremental-Development-And-Automation.md`.
- `docs/09-implementation/BACKLOG-001-Product-Backlog.md`.
- `docs/09-implementation/IMPLEMENTATION-STAGES-001-Prototype-and-MVP-Stage-Map.md`.

Proxima tarea recomendada:

- `WEATHER-PRED-001`: normalizar el contrato de clima usado por prediccion.

Uso previsto en la tesis:

- Capitulo 3: justificacion del enfoque incremental.
- Capitulo 4: planificacion del incremento funcional que integra backend, Android, clima, prediccion y notificaciones.
- Capitulo 5: evidencia de control de alcance posterior a observaciones del anteproyecto.

## WEATHER-PRED-001 - Contrato de clima preparado para prediccion

Fecha: 2026-07-11.

Estado: implementado y verificado por tests/build.

Alcance:

- Se extendio `WeatherSnapshotResponseDto` sin romper campos existentes.
- Se agrego `source` para explicitar si el dato proviene de Open-Meteo o mock.
- Se agrego `precipitationMillimeters` para diferenciar probabilidad de lluvia de cantidad estimada.
- Se agrego `forecastLeadMinutes` para diferenciar dato actual de ventana futura.
- Se actualizaron provider Open-Meteo, provider mock y fixtures de tests.

Actualizacion de harness:

- `ARC-006` fue actualizado para reflejar que FCM real ya esta validado.
- `MVP-001`, `FR5-INDEX` y `AGENTS.md` fueron actualizados para apuntar al Incremento 4 y al foco `PB-021`.

Uso previsto en la tesis:

- Capitulo 4: evidencia de normalizacion de contrato antes de prediccion y automatizacion.
- Capitulo 5: ejemplo de mantenimiento del harness documental cuando el estado real del sistema cambia.

Verificacion:

- `npm.cmd test`.
- Resultado: 31 suites exitosas, 94 tests exitosos.
- `npm.cmd run build`.
- Resultado: build NestJS exitoso.

## DOMAIN-LOCATION-001 - Modelo controlado de ubicaciones de secado

Fecha: 2026-07-11.

Estado: implementado y verificado por tests/build.

Alcance:

- Se separo `locationId` como ubicacion meteorologica/hogar de `dryingLocationId` como ubicacion fisica de secado.
- Se formalizaron ubicaciones de secado: `INDOOR`, `BALCONY`, `OUTDOOR_LINE`, `PATIO`, `LAUNDRY_ROOM`.
- Se definieron perfiles con metodo de secado por defecto, exposicion meteorologica, factor de flujo de aire, exposicion a lluvia y retencion de humedad.
- Se agrego `dryingLocationId` al contrato backend de cargas de lavado.
- Se agrego migracion Supabase `005_drying_locations.sql`.
- Se mantuvo compatibilidad con Android actual usando default `PATIO` cuando el cliente no envia `dryingLocationId`.

Uso previsto en la tesis:

- Capitulo 4: evidencia de refinamiento del dominio antes de aplicar la prediccion climatica completa.
- Capitulo 5: ejemplo de decision incremental para separar conceptos que estaban mezclados en un unico campo.

Verificacion:

- `npm.cmd test`.
- Resultado: 32 suites exitosas, 99 tests exitosos.
- `npm.cmd run build`.
- Resultado: build NestJS exitoso.

## BACKEND-PRED-001 - Prediccion de secado con clima y ubicacion

Fecha: 2026-07-11.

Estado: implementado y verificado por tests/build.

Alcance:

- La prediccion acepta `dryingLocationId` opcional.
- Si el cliente no envia ubicacion de secado, el backend usa `PATIO` para mantener compatibilidad.
- El calculo ajusta score y tiempo estimado segun exposicion a lluvia, flujo de aire y retencion de humedad de la ubicacion.
- La respuesta de prediccion agrega `dryingLocationId` y `dryingLocationLabel`.
- La explicacion textual incluye la ubicacion usada para que la recomendacion sea comprensible.

Uso previsto en la tesis:

- Capitulo 4: evidencia del paso desde contrato meteorologico a recomendacion funcional.
- Capitulo 5: ejemplo de explicabilidad de una regla heuristica en lugar de una caja negra.

Verificacion:

- `npm.cmd test`.
- Resultado: 32 suites exitosas, 102 tests exitosos.
- `npm.cmd run build`.
- Resultado: build NestJS exitoso.

## ANDROID-LOAD-LOCATION-001 - Seleccion de ubicacion de secado en nueva carga

Fecha: 2026-07-11.

Estado: implementado y verificado por tests/build.

Alcance:

- Android incorpora el enum `DryingLocation` alineado con el contrato backend.
- La pantalla Nueva carga permite seleccionar interior, balcon, tendedero exterior, patio o lavadero.
- `NewLoadViewModel` conserva la seleccion en estado y usa `PATIO` como valor por defecto.
- La creacion remota de carga envia `dryingLocationId` separado de `locationId`.
- `locationId` continua representando la ubicacion meteorologica del hogar.

Uso previsto en la tesis:

- Capitulo 4: evidencia de integracion entre UI movil, dominio y contrato backend.
- Capitulo 5: ejemplo de separacion incremental entre ubicacion climatica y ubicacion efectiva de secado.

Verificacion:

- `.\gradlew.bat --no-daemon :app:testDebugUnitTest`.
- Resultado: tests exitosos.
- `.\gradlew.bat --no-daemon :app:assembleDebug`.
- Resultado: build exitoso.

## ANDROID-DASH-REAL-001 - Dashboard con carga activa, clima y prediccion backend

Fecha: 2026-07-11.

Estado: implementado y verificado por tests/build.

Alcance:

- Android agrega un boundary Retrofit para `POST /predictions/drying`.
- Se incorpora `RemotePredictionDataSource` para mapear la respuesta backend a `DryingPrediction`.
- `BackendDashboardRepository` carga la carga activa, clima y prediccion backend cuando existe una carga activa.
- `DashboardViewModel` usa la prediccion backend cuando esta disponible y conserva la prediccion local como fallback.
- `LaundryLoad` incorpora `dryingLocation` para que dashboard respete la ubicacion elegida al crear la carga.
- La tarjeta de carga activa muestra lavarropas, tipo de ropa, programa, ubicacion de secado y tiempo estimado.

Uso previsto en la tesis:

- Capitulo 4: evidencia de integracion funcional Android-backend para recomendacion climatica.
- Capitulo 5: ejemplo de fallback controlado y evolucion incremental desde mock/local hacia backend real.

Verificacion:

- `.\gradlew.bat --no-daemon :app:testDebugUnitTest`.
- Resultado: tests exitosos.
- `.\gradlew.bat --no-daemon :app:assembleDebug`.
- Resultado: build exitoso.

## BACKEND-NOTIF-AUTO-001 - Automatizacion de eventos pendientes de notificacion

Fecha: 2026-07-11.

Estado: implementado y verificado por tests/build.

Alcance:

- Al crear una carga, el backend registra un evento pendiente `IDEAL_HANGING_TIME`.
- Al pasar una carga a `WASHING` o `DRYING`, se cancelan eventos pendientes de tender que ya no aplican.
- Al pasar una carga a `DRYING`, el backend registra un evento pendiente `DRYING_COMPLETE`.
- Al pasar una carga a `COMPLETED` o `CANCELLED`, se cancelan eventos pendientes obsoletos.
- Al completar una carga, se conserva el despacho real de push FCM ya validado.
- `NotificationsSupabaseDataSource` agrega soporte para cancelar eventos pendientes por carga y categoria.

Uso previsto en la tesis:

- Capitulo 4: evidencia de automatizacion backend auditable vinculada al ciclo de vida de una carga.
- Capitulo 5: ejemplo de trazabilidad entre estado de dominio, efectos secundarios y registros en Supabase.

Verificacion:

- `npm.cmd test`.
- Resultado: 32 suites exitosas, 106 tests exitosos.
- `npm.cmd run build`.
- Resultado: build NestJS exitoso.

## UX-PRINCIPLES-001 - Principios psicologicos de UX aplicados al MVP

Fecha: 2026-07-11.

Estado: documentado como insumo operativo.

Alcance:

- Se registraron seis principios UX aportados por el usuario: smart defaults, goal gradient effect, reciprocity, IKEA effect, loss aversion y contrast effect.
- Se tradujeron a decisiones concretas para TenderApp: formularios prellenados, defaults razonables, personalizacion de lavarropas, warnings accionables y contraste entre estados.
- Se dejo explicito que el video/resumen funciona como insumo de diseno, no como fuente academica primaria, hasta contar con metadatos verificables.

Uso previsto en la tesis:

- Capitulo 4: evidencia de incorporacion controlada de criterios UX durante el desarrollo incremental.
- Capitulo 5: ejemplo de conversion de insumos no estructurados en artefactos de trabajo auditables.

## WASHER-VALIDATION-001 - Validacion controlada de metadata de lavarropas

Fecha: 2026-07-11.

Estado: implementado y verificado por tests/build.

Alcance:

- Se reemplazo la etiqueta energetica libre en Android Settings por un selector controlado.
- Se definio la escala compatible `A+++`, `A++`, `A+`, `A`, `B`, `C`, `D`, `E`, `F`, `G`.
- Se mantuvo `A` como valor por defecto y se agrego la opcion `Sin dato` / `Unknown` para evitar datos inventados.
- Se agrego validacion backend para normalizar etiquetas soportadas y rechazar valores fuera de escala.
- Se mantuvo Supabase como persistencia textual para no introducir una migracion innecesaria en este incremento.

Uso previsto en la tesis:

- Capitulo 4: evidencia de mejora incremental de UX y consistencia de contrato.
- Capitulo 5: ejemplo de conversion de un campo libre en una decision controlada por dominio y backend.

Verificacion:

- `npm.cmd test`.
- Resultado: 32 suites exitosas, 107 tests exitosos.
- `npm.cmd run build`.
- Resultado: build NestJS exitoso.
- `.\gradlew.bat --no-daemon :app:testDebugUnitTest`.
- Resultado: tests Android exitosos.
- `.\gradlew.bat --no-daemon :app:assembleDebug`.
- Resultado: build Android exitoso.

Observacion:

- Una ejecucion paralela inicial de Gradle mostro interferencia de cache Kotlin/resolucion durante `assembleDebug`; al repetir el build de forma aislada, el resultado fue exitoso.

## QA-INCREMENT-004 - Validacion end-to-end del Incremento 4

Fecha: 2026-07-11.

Estado: artefacto de validacion creado; evidencia manual final pendiente de captura.

Alcance:

- Se definio el recorrido de QA para el flujo usuario -> Android -> Render backend -> Supabase -> Open-Meteo/prediccion -> notification_events -> FCM/Android.
- Se consolidaron precondiciones, pasos manuales, criterios de aceptacion y evidencia esperada.
- Se agregaron criterios de aceptacion para `PB-020` y `PB-021` en la matriz de calidad.
- Se mantuvo separado el estado "implementado" del estado "validado visualmente con evidencia", para no cerrar la tesis sin capturas y logs revisables.
- Se verificaron endpoints publicos desplegados: `/api/v1/health` y `/api/v1/health/supabase` respondieron HTTP 200 desde PowerShell con red habilitada.

Uso previsto en la tesis:

- Capitulo 4: cierre del incremento funcional principal del MVP.
- Capitulo 5: evidencia de trazabilidad entre backlog, implementacion, pruebas y validacion humana.

Evidencia pendiente:

- Capturas Android de Settings, New Load, Dashboard e History.
- Captura o registro redacted de `notification_events`.
- Captura o log redacted de notificacion visible o clasificacion de fallo.

## EVIDENCE-SCREENSHOTS-001 - Organizacion de capturas v1/v2

Fecha: 2026-07-11.

Estado: documentado como evidencia visual inicial.

Alcance:

- Se registro la estructura `tesis-final/evidence/screenshots/v1` para capturas del diseno inicial simple.
- Se registro la estructura `tesis-final/evidence/screenshots/v2` para capturas de la version visual actual.
- Se agrego un indice local de screenshots con reglas de uso, proposito, contenido y pendientes de clasificacion.
- Se renombraron las capturas con nombres explicitos en espanol, sin acentos para compatibilidad con Git/Windows/Markdown.
- Se agregaron titulos y notas sugeridas para figuras siguiendo la convencion APA 7.
- Se clasificaron las capturas `v2` por pantalla: login, nueva carga, dashboard, historial, ajustes, recuperar clave y registro.
- Se marco `02-v2-login.png` como evidencia que requiere redaccion o reemplazo por cuenta de prueba antes de usarla como figura final, porque contiene un correo visible.
- Se agrego `12-v2-notificaciones.png` como evidencia visual del circuito push visible en Android, incluyendo notificacion de prueba y notificacion funcional de retiro de carga.

Uso previsto en la tesis:

- Capitulo 4: evidencia visual de evolucion incremental del MVP.
- Anexos: comparacion entre prototipo inicial, version refinada y validaciones del flujo.

Pendiente:

- Seleccionar cuales capturas cierran `QA-INCREMENT-004`.
- Redactar epigrafes finales para figuras de la tesis.
- Capturar evidencia adicional de `notification_events` sin secretos si se requiere trazabilidad de base de datos.

## ANDROID-LOAD-UX-002 - Selectores compactos en Nueva carga

Fecha: 2026-07-11.

Estado: implementado y verificado por tests/build.

Alcance:

- Se reemplazaron listas largas de botones por selectores compactos tipo desplegable en `NewLoadScreen`.
- Se agrego un resumen rapido de seleccion en la tarjeta de vista previa de estimacion.
- Se cambio la etiqueta visible de "mock" a "vista previa" para mejorar la percepcion de producto sin afirmar una prediccion final no calculada en esa pantalla.
- Se mantuvieron los defaults inteligentes: ropa mixta, programa eco, patio y lavarropas principal cuando existe.

Principios UX aplicados:

- Smart defaults.
- Reduccion de fatiga de decision.
- Goal gradient effect.
- Accion siguiente obvia.

Uso previsto en la tesis:

- Capitulo 4: evidencia de refinamiento UX posterior a validacion funcional.
- Capitulo 5: ejemplo de mejora incremental guiada por artefactos de diseno y evidencia.

Verificacion:

- `.\gradlew.bat --no-daemon :app:testDebugUnitTest`.
- Resultado: tests exitosos.
- `.\gradlew.bat --no-daemon :app:assembleDebug`.
- Resultado: build exitoso.

## ANDROID-SETTINGS-003 - Selectores compactos de lavarropas

Fecha: 2026-07-11.

Estado: implementado y verificado por tests/build.

Alcance:

- Se reemplazaron las grillas de botones de tipo de lavarropas y etiqueta energetica por selectores compactos.
- Se conservaron los defaults inteligentes del formulario: carga frontal, 7 kg, etiqueta A, 45 litros y lavarropas principal.
- Se mantuvo intacto el contrato con backend para crear lavarropas.
- Se dejo planificado `ANDROID-SETTINGS-004` para ubicacion del hogar y seleccion de lugar de secado con apoyo de mapa.

Principios UX aplicados:

- Smart defaults.
- Reduccion de fatiga de decision.
- IKEA effect mediante personalizacion del lavarropas.
- Goal gradient effect porque el formulario inicia casi completo.

Uso previsto en la tesis:

- Capitulo 4: evidencia de refinamiento de configuracion del hogar.
- Capitulo 5: trazabilidad entre validacion manual, feedback de usuario y mejora incremental del MVP.

Verificacion:

- `.\gradlew.bat --no-daemon :app:testDebugUnitTest --tests com.tesis_pro.tenderapp.ui.screens.SettingsViewModelTest`.
- Resultado: tests de Settings exitosos.
- `.\gradlew.bat --no-daemon :app:assembleDebug`.
- Resultado: build exitoso.

## ANDROID-SETTINGS-004 - Ubicacion del hogar y default de secado

Fecha: 2026-07-11.

Estado: implementado y verificado por tests/build.

Alcance:

- Se agrego configuracion editable de ubicacion del hogar en Settings.
- Se evito pedir coordenadas manuales; el usuario configura una ubicacion legible del hogar.
- Se agrego seleccion de ubicacion/lugar de secado predeterminado.
- Se persistieron los valores localmente con `SharedPreferences`.
- Se conecto New Load para iniciar con el default de secado configurado.
- Se conecto Dashboard/backend weather flow para leer la ubicacion del hogar persistida cuando exista.
- Se agrego handoff a mapa externo mediante intent `geo:` usando la ubicacion legible como busqueda visual.

Principios UX aplicados:

- Smart defaults.
- IKEA effect por personalizacion del hogar.
- Reduccion de fatiga de decision al no exigir coordenadas manuales.
- Reciprocity porque la app sigue funcionando aun sin configurar mapa.

Uso previsto en la tesis:

- Capitulo 4: evidencia de consolidacion del circuito hogar, clima, prediccion y carga.
- Capitulo 5: ejemplo de evolucion controlada desde valor estatico a preferencia configurable.

Verificacion:

- `.\gradlew.bat --no-daemon :app:testDebugUnitTest --tests com.tesis_pro.tenderapp.ui.screens.SettingsViewModelTest`.
- Resultado: tests de Settings exitosos.
- `.\gradlew.bat --no-daemon :app:assembleDebug`.
- Resultado: build exitoso.

## ANDROID-SETTINGS-005 - Ubicacion actual del dispositivo

Fecha: 2026-07-11.

Estado: implementado y verificado por tests/build.

Alcance:

- Se agrego accion "Usar mi ubicacion actual" en Settings.
- Se solicitan permisos runtime de ubicacion desde `MainActivity`.
- Se obtiene la ultima ubicacion conocida mediante `LocationManager`.
- Se guardan latitud y longitud internamente, sin mostrarlas como campos al usuario.
- Se muestra un estado visual cuando la ubicacion actual queda vinculada.
- Se agregaron mensajes localizados para permiso denegado y ubicacion no disponible.
- El handoff a mapa externo usa coordenadas cuando existen.

Principios UX aplicados:

- Reduccion de fatiga de decision: el usuario no escribe coordenadas.
- Smart defaults: si no hay ubicacion real, el flujo sigue usando el hogar configurado.
- Reciprocity: la ubicacion es opcional.
- IKEA effect: el hogar queda configurado con una accion personal y visible.

Uso previsto en la tesis:

- Capitulo 4: evidencia de mejora de usabilidad posterior a revision manual.
- Capitulo 5: ejemplo de ajuste incremental guiado por observacion de flujo real.

Verificacion:

- `.\gradlew.bat --no-daemon :app:testDebugUnitTest --tests com.tesis_pro.tenderapp.ui.screens.SettingsViewModelTest`.
- Resultado: tests de Settings exitosos.
- `.\gradlew.bat --no-daemon :app:assembleDebug`.
- Resultado: build exitoso.

## ANDROID-PUSH-005 - Correccion de crash en solicitud de permiso

Fecha: 2026-07-10.

Estado: implementado y verificado por tests/build.

Evidencia observada:

- Logcat capturo `IllegalStateException: No ActivityResultRegistryOwner was provided via LocalActivityResultRegistryOwner`.
- El crash ocurria al entrar en `SettingsScreen`, donde se habia registrado `rememberLauncherForActivityResult`.

Decision aplicada:

- Mover el registro del permission launcher a `MainActivity`, que es dueña del Activity Result registry.
- Pasar a `SettingsScreen` solo el estado `notificationPermissionState` y el callback `onRequestNotificationPermission`.
- Mantener Settings como composable controlado por estado, sin registrar contratos de Activity desde la pantalla.

Uso previsto en la tesis:

- Capitulo 4: evidencia de ajuste incremental posterior a QA real en dispositivo.
- Capitulo 5: ejemplo de limite entre UI declarativa Compose y APIs de ciclo de vida Android.

Verificacion:

- `.\gradlew.bat --no-daemon :app:testDebugUnitTest`.
- Resultado: tests exitosos.
- `.\gradlew.bat --no-daemon :app:assembleDebug`.
- Resultado: build exitoso.

Observacion:

- Una primera ejecucion paralela de Gradle fallo por interferencia de cache Kotlin/recursos. Al repetir `assembleDebug` solo, el build fue exitoso.

Uso previsto en la tesis:

- Capitulo 4: evidencia de cierre del circuito push desde infraestructura hasta permiso de plataforma.
- Capitulo 5: discusion sobre QA incremental y plataformas moviles con permisos runtime dependientes de version.

Evidencia pendiente:

- Instalar APK nuevo.
- Validar Settings en Android 13+ con permiso denegado.
- Conceder permiso desde Settings.
- Confirmar que las notificaciones siguen apareciendo con el permiso sincronizado.

## MVP-INCREMENT-004B - Cierre funcional de flujo principal

Fecha: 2026-07-12.

Estado: documentado como guia de continuidad y cierre de MVP.

Origen documental:

- Analisis de estado actual provisto por el autor del proyecto.
- `AGENTS.md`.
- `docs/README.md`.
- `docs/FR6-INDEX.md`.
- `docs/CONTEXT-000-Master-Context.md`.
- `docs/CONTEXT-001-Documentation-Map.md`.
- `docs/CONTEXT-002-Codex-Reading-Protocol.md`.
- `docs/CONTEXT-003-Task-Context-Packs.md`.
- `docs/FR5-INDEX.md`.
- `docs/09-implementation/BACKLOG-001-Product-Backlog.md`.
- `docs/09-implementation/MVP-INCREMENTS-001-Incremental-Development-And-Automation.md`.
- `docs/09-implementation/MVP-INCREMENT-004-Weather-Drying-Notifications.md`.
- `docs/09-implementation/IMPLEMENTATION-STAGES-001-Prototype-and-MVP-Stage-Map.md`.
- `docs/07-prompts/PROMPT-005-Documentation-Update.md`.
- `docs/08-agents/AG-000-Agent-Harness-Overview.md`.
- `docs/08-agents/AG-007-Documentation-Agent.md`.
- `docs/11-quality/QA-001-Definition-of-Done.md`.

Alcance:

- Se registro el estado actual como MVP funcional avanzado.
- Se documento la brecha restante hacia una demo solida y producto mas real.
- Se creo `MVP-INCREMENT-004B` para cerrar el circuito principal.
- Se agrego `PB-022` al backlog como bloque de cierre funcional.
- Se separo la persistencia remota de ubicacion en `BACKEND-USER-LOCATION-001`.
- Se definio `ANDROID-LOCATION-002` para sincronizar Settings/currentLocation con backend y consumidores Android.
- Se dejaron planificadas las siguientes tareas: update de estado de carga, prediccion accionable y smoke final.

Archivos principales:

- `docs/09-implementation/MVP-INCREMENT-004B-Functional-Flow-Closure.md`.
- `docs/09-implementation/backend/BACKEND-USER-LOCATION-001-Persist-Household-Location.md`.
- `docs/09-implementation/android/ANDROID-LOCATION-002-Remote-Household-Location-Sync.md`.
- `docs/09-implementation/BACKLOG-001-Product-Backlog.md`.
- `docs/09-implementation/MVP-INCREMENTS-001-Incremental-Development-And-Automation.md`.
- `docs/09-implementation/IMPLEMENTATION-STAGES-001-Prototype-and-MVP-Stage-Map.md`.
- `docs/FR5-INDEX.md`.
- `tesis-final/evidence/increment-log.md`.

Verificacion:

- Cambio documental; no se ejecuto build.
- Se revisara con `git diff --check`.

Uso previsto en la tesis:

- Capitulo 3: evidencia de replanificacion incremental asistida por IA a partir de validacion del sistema.
- Capitulo 4: organizacion del cierre tecnico del MVP TenderApp.
- Capitulo 5: soporte para discutir brechas entre prototipo, MVP demostrable y producto real.

Proxima tarea recomendada:

- `BACKEND-USER-LOCATION-001`, porque `ANDROID-LOCATION-002` requiere un contrato backend aprobado para actualizar la ubicacion remota del usuario y `updated_at`.

## BACKEND-USER-LOCATION-001 - Persistencia remota de ubicacion del hogar

Fecha: 2026-07-12.

Estado: implementado y verificado por tests/build.

Origen documental:

- `docs/09-implementation/MVP-INCREMENT-004B-Functional-Flow-Closure.md`.
- `docs/09-implementation/backend/BACKEND-USER-LOCATION-001-Persist-Household-Location.md`.
- `docs/09-implementation/BACKLOG-001-Product-Backlog.md`.
- `docs/09-implementation/backend/API-001-Backend-API-Contract-Draft.md`.
- `docs/04-architecture/ARC-003-Backend-Architecture.md`.
- `docs/09-implementation/backend/MOD-002-Backend-Module-Plan.md`.

Alcance:

- Se agrego `PUT /api/v1/users/me/location` como endpoint protegido.
- Se agregaron DTOs para request y response de ubicacion del usuario.
- Se agrego validacion manual de `locationId`, `label`, `latitude` y `longitude`.
- Se persiste la ubicacion en `public.user_locations` mediante backend/Supabase service role.
- Se actualiza `updated_at` explicitamente.
- Se desmarca la ubicacion primaria anterior antes de guardar la nueva.
- `GET /api/v1/me` devuelve `defaultLocationId` desde la ubicacion primaria cuando existe.

Archivos principales:

- `backend/src/users/users.controller.ts`.
- `backend/src/users/users.service.ts`.
- `backend/src/users/users.validation.ts`.
- `backend/src/users/dto/save-user-location-request.dto.ts`.
- `backend/src/users/dto/user-location-response.dto.ts`.
- `backend/src/users/users.controller.spec.ts`.
- `backend/src/users/users.service.spec.ts`.
- `docs/09-implementation/backend/BACKEND-USER-LOCATION-001-Persist-Household-Location.md`.
- `docs/09-implementation/backend/API-001-Backend-API-Contract-Draft.md`.
- `docs/09-implementation/BACKLOG-001-Product-Backlog.md`.
- `docs/09-implementation/MVP-INCREMENT-004B-Functional-Flow-Closure.md`.

Verificacion:

- `npm.cmd test -- --runInBand users`.
- Resultado: 2 suites exitosas, 7 tests exitosos.
- `npm.cmd run build`.
- Resultado: build NestJS exitoso.

Uso previsto en la tesis:

- Capitulo 4: evidencia de cierre de frontera backend entre configuracion movil y persistencia remota.
- Capitulo 5: ejemplo de correccion incremental de una brecha detectada por validacion manual del usuario.

Evidencia pendiente:

- Integrar Android Settings con este endpoint en `ANDROID-LOCATION-002`.
- Verificar manualmente que Supabase actualiza `user_locations.updated_at` al usar la app.

## BACKEND-WEATHER-LOCATION-001 - Uso de ubicacion persistida en clima y prediccion

Fecha: 2026-07-12.

Estado: implementado y verificado por tests/build.

Origen documental:

- `docs/09-implementation/MVP-INCREMENT-004B-Functional-Flow-Closure.md`.
- `docs/09-implementation/BACKLOG-001-Product-Backlog.md`.
- `docs/09-implementation/backend/BACKEND-USER-LOCATION-001-Persist-Household-Location.md`.
- `docs/09-implementation/backend/BACKEND-WEATHER-LOCATION-001-Use-User-Location.md`.

Alcance:

- `WeatherService` resuelve ubicaciones con contexto del usuario autenticado.
- `locationId=home` usa la ubicacion primaria de `user_locations` cuando existe.
- Un id especifico, como `current-location`, usa la fila correspondiente de `user_locations`.
- Si no hay fila o faltan coordenadas, se conserva el fallback estatico del MVP.
- Los providers de clima reciben coordenadas ya resueltas.
- `PredictionsController` y `PredictionsService` pasan el usuario autenticado hacia el flujo de clima.

Archivos principales:

- `backend/src/weather/weather.service.ts`.
- `backend/src/weather/weather.controller.ts`.
- `backend/src/weather/weather-provider.ts`.
- `backend/src/weather/mock-weather.provider.ts`.
- `backend/src/weather/open-meteo-weather.provider.ts`.
- `backend/src/weather/weather.module.ts`.
- `backend/src/predictions/predictions.controller.ts`.
- `backend/src/predictions/predictions.service.ts`.
- Tests de `weather` y `predictions`.

Verificacion:

- `npm.cmd test -- --runInBand weather predictions`.
- Resultado: 10 suites exitosas, 25 tests exitosos.
- `npm.cmd run build`.
- Resultado: build NestJS exitoso.

Uso previsto en la tesis:

- Capitulo 4: evidencia de conexion entre preferencia del usuario, proveedor meteorologico y prediccion.
- Capitulo 5: ejemplo de cierre de brecha detectada en QA manual antes de smoke final.

Proxima tarea recomendada:

- `ANDROID-LOCATION-002`, para que Settings llame `PUT /api/v1/users/me/location` y el Dashboard/New Load consuman la ubicacion efectiva.

## ANDROID-LOCATION-002 - Sincronizacion remota de ubicacion del hogar

Fecha: 2026-07-12.

Estado: implementado y verificado por tests/build; validacion manual Supabase pendiente.

Origen documental:

- `docs/09-implementation/android/ANDROID-LOCATION-002-Remote-Household-Location-Sync.md`.
- `docs/09-implementation/MVP-INCREMENT-004B-Functional-Flow-Closure.md`.
- `docs/09-implementation/BACKLOG-001-Product-Backlog.md`.
- `docs/04-architecture/ARC-004-Repository-and-Data-Flow.md`.
- `docs/07-prompts/CTX-002-Android-Architecture-Context.md`.

Alcance:

- Se agrego `PUT /users/me/location` al contrato Retrofit `TenderBackendApi`.
- Se agregaron DTOs Android para guardar y recibir ubicacion de usuario.
- Se agrego `RemoteUserLocationDataSource`.
- Se agrego `UserLocationRepository` y `BackendUserLocationRepository`.
- `SettingsViewModel` sincroniza la ubicacion del hogar con backend antes de guardar localmente.
- Se mantiene `locationId = home` para que backend resuelva esa ubicacion a la fila primaria del usuario.
- Si la sincronizacion remota falla, no se marca la configuracion local como guardada y se muestra mensaje localizado.

Archivos principales:

- `app/src/main/java/com/tesis_pro/tenderapp/data/remote/TenderBackendApi.kt`.
- `app/src/main/java/com/tesis_pro/tenderapp/data/remote/RemoteUserLocationDataSource.kt`.
- `app/src/main/java/com/tesis_pro/tenderapp/data/remote/dto/UserLocationDtos.kt`.
- `app/src/main/java/com/tesis_pro/tenderapp/data/repository/BackendUserLocationRepository.kt`.
- `app/src/main/java/com/tesis_pro/tenderapp/domain/repository/UserLocationRepository.kt`.
- `app/src/main/java/com/tesis_pro/tenderapp/ui/screens/SettingsViewModel.kt`.
- `app/src/main/java/com/tesis_pro/tenderapp/ui/screens/SettingsScreen.kt`.
- `app/src/main/res/values/strings.xml`.
- `app/src/main/res/values-es/strings.xml`.
- Tests Android afectados por el nuevo contrato `TenderBackendApi`.

Verificacion:

- `.\gradlew.bat --no-daemon :app:testDebugUnitTest --tests com.tesis_pro.tenderapp.ui.screens.SettingsViewModelTest`.
- Resultado: build de tests exitoso.
- `.\gradlew.bat --no-daemon :app:testDebugUnitTest`.
- Resultado: tests Android exitosos.
- `.\gradlew.bat --no-daemon :app:assembleDebug`.
- Resultado: build Android exitoso.

Uso previsto en la tesis:

- Capitulo 4: evidencia de integracion Android-backend para preferencia de ubicacion del hogar.
- Capitulo 5: evidencia de cierre de brecha detectada entre UI local y persistencia remota real.

Evidencia pendiente:

- Instalar la APK actualizada.
- Usar Settings > ubicacion actual > guardar valores.
- Confirmar en Supabase que `public.user_locations` actualiza `latitude`, `longitude`, `is_primary` y `updated_at`.
- Confirmar que Dashboard/clima usa la ubicacion efectiva al refrescar.

## ANDROID-LOAD-UPDATE-001 - Actualizacion de estado de carga desde Android

Fecha: 2026-07-12.

Estado: implementado y verificado por tests/build; validacion manual end-to-end pendiente.

Origen documental:

- `docs/09-implementation/android/ANDROID-LOAD-UPDATE-001-Update-Laundry-Status.md`.
- `docs/09-implementation/MVP-INCREMENT-004B-Functional-Flow-Closure.md`.
- `docs/09-implementation/BACKLOG-001-Product-Backlog.md`.
- `docs/09-implementation/backend/BACKEND-PUSH-004-Connect-Domain-Notification-Events.md`.
- `docs/09-implementation/backend/BACKEND-NOTIF-AUTO-001-Pending-Notification-Automation.md`.

Alcance:

- `BackendLaundryRepository` expone una mutacion de estado mediante `LaundryStatusUpdater`.
- Dashboard permite avanzar la carga activa de forma secuencial.
- History permite avanzar cargas activas con la misma secuencia controlada.
- La secuencia soportada es `PLANNED -> WASHING -> DRYING -> COMPLETED`.
- Los textos de accion se agregaron en ingles y espanol.
- Dashboard refresca datos luego de una mutacion exitosa y muestra error si falla.

Archivos principales:

- `app/src/main/java/com/tesis_pro/tenderapp/data/repository/BackendLaundryRepository.kt`.
- `app/src/main/java/com/tesis_pro/tenderapp/ui/screens/DashboardViewModel.kt`.
- `app/src/main/java/com/tesis_pro/tenderapp/ui/screens/DashboardScreen.kt`.
- `app/src/main/java/com/tesis_pro/tenderapp/ui/screens/HistoryViewModel.kt`.
- `app/src/main/java/com/tesis_pro/tenderapp/ui/screens/HistoryScreen.kt`.
- `app/src/main/res/values/strings.xml`.
- `app/src/main/res/values-es/strings.xml`.
- `app/src/test/java/com/tesis_pro/tenderapp/ui/screens/DashboardViewModelTest.kt`.

Verificacion:

- `.\gradlew.bat --no-daemon :app:testDebugUnitTest --tests com.tesis_pro.tenderapp.ui.screens.DashboardViewModelTest`.
- Resultado: exitoso.
- `.\gradlew.bat --no-daemon :app:testDebugUnitTest`.
- Resultado: exitoso.
- `.\gradlew.bat --no-daemon :app:assembleDebug`.
- Resultado: exitoso.

Uso previsto en la tesis:

- Capitulo 4: evidencia de cierre de flujo entre UI Android, backend REST, Supabase y eventos de notificacion.
- Capitulo 5: ejemplo de incremento pequeno, verificable y trazable, orientado a cerrar una brecha observada durante QA manual.

Evidencia pendiente:

- Validar en dispositivo que el boton de Dashboard/History actualiza el estado real.
- Confirmar en Supabase que `laundry_loads.status` cambia.
- Confirmar que el cambio a `COMPLETED` genera/audita la notificacion esperada cuando FCM esta activo.

## ANDROID-WEATHER-SOURCE-001 - Visibilidad de fuente meteorologica en Dashboard

Fecha: 2026-07-12.

Estado: implementado, verificado por tests especificos y validado manualmente en dispositivo.

Origen documental:

- `docs/09-implementation/android/ANDROID-WEATHER-SOURCE-001-Weather-Source-Visibility.md`.
- `docs/09-implementation/android/ANDROID-WEATHER-001-Dashboard-Consumes-Backend-Weather.md`.
- `docs/09-implementation/backend/BACKEND-WEATHER-001-Open-Meteo-Provider.md`.
- `docs/09-implementation/backend/BACKEND-WEATHER-LOCATION-001-Use-User-Location.md`.
- `docs/09-implementation/MVP-INCREMENT-004B-Functional-Flow-Closure.md`.

Motivo:

- En QA manual, Dashboard mostro valores compatibles con el mock meteorologico (`24 C`, `48%`, `18 km/h`) antes de actualizarse a valores reales mas cercanos al clima observado.
- La ubicacion persistida del hogar estaba correcta, por lo que el problema principal era la falta de visibilidad de fuente y fallback en Android.

Alcance:

- `WeatherSnapshotResponseDto` conserva `source`.
- `WeatherSnapshot` incorpora `WeatherDataSource`.
- El mapper remoto convierte `OPEN_METEO`, `MOCK` o valores desconocidos.
- Dashboard muestra `Open-Meteo`, `Mock backend` o `Fallback local` como fuente visible.
- Se agregaron tests de mapeo y fallback visible.

Archivos principales:

- `app/src/main/java/com/tesis_pro/tenderapp/domain/model/WeatherSnapshot.kt`.
- `app/src/main/java/com/tesis_pro/tenderapp/data/remote/dto/WeatherDtos.kt`.
- `app/src/main/java/com/tesis_pro/tenderapp/data/remote/WeatherDtoMapper.kt`.
- `app/src/main/java/com/tesis_pro/tenderapp/ui/screens/DashboardViewModel.kt`.
- `app/src/test/java/com/tesis_pro/tenderapp/data/remote/RemoteWeatherDataSourceTest.kt`.
- `app/src/test/java/com/tesis_pro/tenderapp/ui/screens/DashboardViewModelTest.kt`.

Verificacion:

- `.\gradlew.bat --no-daemon :app:testDebugUnitTest --tests com.tesis_pro.tenderapp.data.remote.RemoteWeatherDataSourceTest --tests com.tesis_pro.tenderapp.ui.screens.DashboardViewModelTest`.
- Resultado: exitoso.

Uso previsto en la tesis:

- Capitulo 4: evidencia de robustecimiento posterior a QA manual y de trazabilidad entre proveedor real, mock y fallback.
- Capitulo 5: ejemplo de como una observacion manual genera un incremento pequeno de calidad y auditabilidad del producto.

Validacion manual:

- Autor confirmo que Dashboard muestra `Open-Meteo` como fuente visible.
- Autor observo valores climaticos cercanos al estado real luego de la actualizacion del backend.

## ANDROID-PRED-002 - Prediccion accionable en Dashboard

Fecha: 2026-07-12.

Estado: implementado y verificado por test especifico; validacion manual en dispositivo pendiente.

Origen documental:

- `docs/09-implementation/android/ANDROID-PRED-002-Actionable-Dashboard-Prediction.md`.
- `docs/09-implementation/android/ANDROID-DASH-REAL-001-Backend-Prediction-Dashboard.md`.
- `docs/09-implementation/backend/BACKEND-PRED-001-Drying-Prediction-With-Location.md`.
- `docs/09-implementation/MVP-INCREMENT-004B-Functional-Flow-Closure.md`.

Alcance:

- Se agrego `ActionRecommendationUi` al estado de Dashboard.
- `DashboardViewModel` deriva la proxima accion desde estado de carga, veredicto y tiempo estimado.
- Dashboard muestra una tarjeta de `Proxima accion`.
- Se agregaron textos en ingles y espanol.
- Se agregaron tests para condiciones favorables, desfavorables y carga en secado.

Archivos principales:

- `app/src/main/java/com/tesis_pro/tenderapp/ui/screens/DashboardViewModel.kt`.
- `app/src/main/java/com/tesis_pro/tenderapp/ui/screens/DashboardScreen.kt`.
- `app/src/main/res/values/strings.xml`.
- `app/src/main/res/values-es/strings.xml`.
- `app/src/test/java/com/tesis_pro/tenderapp/ui/screens/DashboardViewModelTest.kt`.

Verificacion:

- `.\gradlew.bat --no-daemon :app:testDebugUnitTest --tests com.tesis_pro.tenderapp.ui.screens.DashboardViewModelTest`.
- Resultado: exitoso.

Uso previsto en la tesis:

- Capitulo 4: evidencia de transformacion de una prediccion tecnica en una recomendacion operativa para el usuario.
- Capitulo 5: ejemplo de mejora incremental de UX basada en el criterio de que la accion siguiente debe ser evidente.

Evidencia pendiente:

- Instalar APK actualizada.
- Confirmar visualmente que Dashboard muestra `Proxima accion`.
- Capturar ejemplo con fuente `Open-Meteo` y recomendacion desfavorable cuando el score sea bajo.

## QA-INCREMENT-004B - Paquete de evidencia del cierre funcional

Fecha: 2026-07-12.

Estado: paquete de evidencia preparado; smoke manual final pendiente.

Origen documental:

- `docs/09-implementation/integration/QA-INCREMENT-004B-Functional-Flow-Validation.md`.
- `docs/09-implementation/MVP-INCREMENT-004B-Functional-Flow-Closure.md`.
- `docs/09-implementation/BACKLOG-001-Product-Backlog.md`.
- `tesis-final/evidence/screenshots/README.md`.

Estado actual resumido:

- Backend Render operativo con cold start conocido.
- Supabase Auth validado en login, registro y redireccion por deep link.
- Ubicacion del hogar sincronizada desde Settings hacia backend/Supabase.
- Dashboard muestra fuente `Open-Meteo` cuando el backend responde con clima real.
- Dashboard muestra prediccion, score y `Proxima accion`.
- Nueva carga usa lavarropas, ropa, programa y ubicacion de secado controlados.
- History permite observar y avanzar estados de cargas activas.
- Push real con FCM fue validado previamente y depende de permiso Android, opt-in y registro del dispositivo.
- Evidencia visual reciente organizada en `screenshots/v3` y `screenshots/v4`.
- El autor informo evidencia de `notification_events` para el cierre del circuito de notificaciones.
- El autor observo un mensaje funcional al actualizar el estado de una carga, indicando que la ropa ya esta lista.
- El evento se considera parte del flujo validado: secando/colgado -> finalizado -> ropa lista o guardar ropa.

Capturas nuevas indexadas:

- `tesis-final/evidence/screenshots/v3/01-v3-inicio-dashboard-prediccion-back-open.png`.
- `tesis-final/evidence/screenshots/v3/02-v3-nueva-carga-ui-desplegables-opcion.png`.
- `tesis-final/evidence/screenshots/v3/03-v3-historial-cargas-actualizables.png`.
- `tesis-final/evidence/screenshots/v3/04-v3-historial-cargas-marca-secando.png`.
- `tesis-final/evidence/screenshots/v3/05-v3-ajustes-valores-hogas-localizacion.png`.
- `tesis-final/evidence/screenshots/v3/06-ajustes-lavarropas-mejora-ui.png`.
- `tesis-final/evidence/screenshots/v4/01-v4-dasboard-con-recomendacion-disponible.png`, con dashboard, mensajes dinamicos, recomendacion acorde a actualizaciones de estado y evidencia del evento de ropa lista/guardar ropa cuando corresponda.

Pendientes para marcar Passed:

- Confirmar una ejecucion completa del smoke final en dispositivo.
- Vincular la evidencia `notification_events` informada por el autor a una captura o transcripcion redaccionada si se usa en la tesis final.
- Confirmar en Supabase o History el cambio de estado realizado desde Android.
- Definir la etiqueta final de UX para el cierre de ciclo: `ropa lista`, `retirar ropa` o `guardar ropa`.
- Revisar capturas v3/v4 para asegurar que no contengan datos personales visibles.

Uso previsto en la tesis:

- Capitulo 4: evidencia del cierre integrado entre Android, backend, Supabase, Open-Meteo, Firebase y documentacion incremental.
- Capitulo 5: ejemplo de QA incremental asistido por IA, donde observaciones manuales generan tareas pequenas y trazables.

## ANDROID-NEWLOAD-PRED-001 - Tarjeta dinamica de decision en Nueva carga

Fecha: 2026-07-12.

Estado: implementado; verificacion automatizada en curso.

Origen documental:

- `docs/09-implementation/android/ANDROID-NEWLOAD-PRED-001-Dynamic-New-Load-Decision-Card.md`.
- `docs/09-implementation/MVP-INCREMENT-004B-Functional-Flow-Closure.md`.
- `docs/09-implementation/BACKLOG-001-Product-Backlog.md`.
- `docs/06-design/UI-005-New-Load-Flow-Specification.md`.
- `docs/06-design/UI-006-Weather-Verdict-Card.md`.

Alcance:

- Se reemplazo la tarjeta estatica de Nueva carga por una vista previa dinamica.
- La decision cambia segun tipo de ropa, programa y ubicacion de secado.
- Se muestra perfil de decision, tiempo estimado, puntaje de seleccion y nota de recalculo posterior con clima real.
- Se mantuvo intacta la creacion backend de cargas.
- Se agregaron textos en ingles y espanol.
- Se registro como tarea futura la responsabilidad ABM de datos: cargas descartadas y lavarropas retirados con historial conservado.

Uso previsto en la tesis:

- Capitulo 4: evidencia de mejora UX orientada a decision antes de registrar datos.
- Capitulo 5: ejemplo de deteccion manual de brecha y conversion en incremento pequeno, trazable y verificable.

## ANDROID-NEWLOAD-PRED-002 - Alineacion de Nueva carga con clima actual

Fecha: 2026-07-12.

Estado: implementado; verificacion automatizada en curso.

Origen:

- Observacion manual: Dashboard mostraba clima actual y score moderado, mientras Nueva carga mostraba `Good option to start` y `90/100`.
- `docs/09-implementation/android/ANDROID-NEWLOAD-PRED-002-New-Load-Weather-Alignment.md`.

Alcance:

- Nueva carga consulta el clima actual del hogar cuando hay sesion/backend disponible.
- La tarjeta de decision usa el mismo calculo de idoneidad que el fallback de Dashboard sobre el clima recibido.
- Si el clima no esta disponible, la tarjeta informa fallback por seleccion.
- La tarjeta expone la fuente: Open-Meteo, mock backend, fallback local o fallback por seleccion.
- Se agrego test para evitar que un clima frio/humedo/nublado mantenga un score artificialmente alto.

Uso previsto en la tesis:

- Capitulo 4: evidencia de QA manual detectando una inconsistencia entre pantallas conectadas.
- Capitulo 5: ejemplo de correccion incremental orientada a coherencia de experiencia y responsabilidad sobre los datos mostrados al usuario.

## ANDROID-NEWLOAD-PRED-003 - Prediccion backend en Nueva carga

Fecha: 2026-07-12.

Estado: implementado; verificacion automatizada en curso.

Origen:

- Observacion manual: Nueva carga seguia mostrando score y duracion distintos a Dashboard, aun usando clima actual.
- `docs/09-implementation/android/ANDROID-NEWLOAD-PRED-003-Backend-Prediction-Preview.md`.

Alcance:

- Nueva carga consulta `predictions/drying` con ropa, programa, ubicacion del hogar y ubicacion de secado seleccionadas.
- La tarjeta prioriza la prediccion backend sobre el calculo local.
- El calculo local queda como fallback cuando no hay prediccion backend.
- El texto de acompanamiento aclara que la tarjeta corresponde a la seleccion actual del formulario y que Dashboard puede referirse a otra carga activa.
- Se agrego cobertura para garantizar que score y duracion backend prevalezcan.

Uso previsto en la tesis:

- Capitulo 4: evidencia de consolidacion de una fuente de verdad para prediccion.
- Capitulo 5: ejemplo de auditoria manual fina sobre coherencia de datos entre pantallas.

## ANDROID-DATA-LIFECYCLE-001A - Descartar carga activa

Fecha: 2026-07-12.

Estado: implementado; verificacion automatizada en curso.

Origen:

- Observacion manual: el MVP necesitaba acciones basicas de ciclo de vida de datos para cargas y lavarropas.
- `docs/09-implementation/android/ANDROID-DATA-LIFECYCLE-001A-Discard-Laundry-Load.md`.
- `docs/09-implementation/MVP-INCREMENT-004B-Functional-Flow-Closure.md`.

Alcance:

- Dashboard permite descartar una carga activa enviando `CANCELLED` al backend.
- Historial permite descartar cargas activas sin eliminarlas fisicamente.
- Las cargas canceladas permanecen como historial auditable.
- La accion queda localizada en ingles y espanol.
- La baja logica de lavarropas queda separada como tarea posterior porque requiere contrato backend/Supabase para preservar historial.

Uso previsto en la tesis:

- Capitulo 4: evidencia de decisiones de ciclo de vida de datos orientadas a trazabilidad.
- Capitulo 5: ejemplo de cierre incremental de una brecha detectada por QA manual sin ampliar innecesariamente el alcance.

## THESIS-EVIDENCE-001 - Sintesis incremental para redaccion

Fecha: 2026-07-12.

Estado: implementado como actualizacion documental.

Origen documental:

- `AGENTS.md`.
- `docs/CONTEXT-000-Master-Context.md`.
- `docs/CONTEXT-001-Documentation-Map.md`.
- `docs/CONTEXT-002-Codex-Reading-Protocol.md`.
- `docs/CONTEXT-003-Task-Context-Packs.md`.
- `docs/10-thesis/TH-003-Thesis-Chapter-Map.md`.
- `docs/10-thesis/TH-006-Evidence-and-Citation-Strategy.md`.
- `docs/10-thesis/TH-007-APA-Writing-Rules-for-Generated-Drafts.md`.
- `docs/03-research/RES-002-Source-Credibility-Framework.md`.
- `docs/03-research/RES-003-Reference-Management-BibTeX-and-APA.md`.
- `tesis-final/evidence/increment-log.md`.

Alcance:

- Se creo una sintesis incremental para convertir la bitacora extensa en un mapa narrativo util para la tesis.
- Se agrego un `README` para `tesis-final/references`.
- Se actualizo `tesis-final/README.md` y `tesis-final/evidence/README.md` para indicar como usar la sintesis.
- Se actualizo `docs/03-research/literature/source-register.md` para registrar fuentes tecnicas y evidencia del proyecto.
- Se actualizo `docs/03-research/literature/standards-mapping.md` para vincular tecnologias reales del MVP con fuentes oficiales.
- Se ajusto `generar_tesis_tenderapp.py` para incluir la sintesis incremental como anexo del documento generado.
- Se actualizo `tesis-final/references/technical-source-map.md` con el uso de evidencia propia del proyecto.

Archivos principales:

- `tesis-final/evidence/increment-synthesis.md`.
- `tesis-final/references/README.md`.
- `tesis-final/evidence/README.md`.
- `tesis-final/README.md`.
- `tesis-final/tesis.md`.
- `tesis-final/references/technical-source-map.md`.
- `docs/03-research/literature/source-register.md`.
- `docs/03-research/literature/standards-mapping.md`.
- `generar_tesis_tenderapp.py`.

Verificacion:

- `python -m py_compile generar_tesis_tenderapp.py`.
- Resultado: sintaxis del generador verificada.
- No se regenero el `.docx` en esta tarea para evitar conflicto con archivos temporales de Word abiertos en `tesis-final`.

Uso previsto en la tesis:

- Capitulo 3: explicar metodologia incremental y actividades recurrentes por incremento.
- Capitulo 4: mapear arquitectura, implementacion y decisiones por incremento.
- Capitulo 5: organizar evidencia de validacion, screenshots, smoke tests y pendientes.

Evidencia pendiente:

- Regenerar `TenderApp-Tesis-App-v3.docx` cuando el documento no este abierto en Word.
- Expandir el manuscrito fuente si se busca una version cercana a 50 paginas reales.

## THESIS-FORMAT-001 - Figuras, tablas, notas APA y Mermaid

Fecha: 2026-07-12.

Estado: implementado como actualizacion documental y regeneracion DOCX.

Origen documental:

- `AGENTS.md`.
- `docs/CONTEXT-000-Master-Context.md`.
- `docs/CONTEXT-001-Documentation-Map.md`.
- `docs/CONTEXT-002-Codex-Reading-Protocol.md`.
- `docs/10-thesis/TH-006-Evidence-and-Citation-Strategy.md`.
- `docs/10-thesis/TH-007-APA-Writing-Rules-for-Generated-Drafts.md`.
- `tesis-final/evidence/screenshots/README.md`.
- `docs/12-assets/diagrams/thesis/README.md`.

Alcance:

- Se definio una guia formal para figuras, tablas, notas APA, capturas y diagramas Mermaid.
- Se agregaron titulos y notas a tablas principales de evidencia, capturas y smoke tests.
- Se corrigio el generador para reconocer `Tabla N` y `Nota.` como elementos academicos separados.
- Se corrigio el generador para respetar bloques de codigo Markdown y evitar que tablas de ejemplo se integren como tablas reales.
- Se documentaron los `.mmd` como fuentes programaticas editables, los `.md` como wrappers de previsualizacion y las imagenes exportadas como artefactos finales solo cuando sean necesarias.
- Se crearon wrappers `.md` para los diagramas Mermaid principales de tesis.
- Se regenero `tesis-final/TenderApp-Tesis-App-v4.docx`.

Archivos principales:

- `tesis-final/evidence/apa-visual-evidence-guide.md`.
- `tesis-final/evidence/screenshots/README.md`.
- `tesis-final/evidence/smoke-tests/SMOKE-2026-07-07-FR7-LOCAL.md`.
- `tesis-final/evidence/smoke-tests/SMOKE-2026-07-07-FR7-RENDER.md`.
- `docs/12-assets/diagrams/thesis/README.md`.
- `docs/12-assets/diagrams/thesis/THESIS-001-incremental-roadmap.md`.
- `docs/12-assets/diagrams/thesis/THESIS-002-system-context.md`.
- `docs/12-assets/diagrams/thesis/THESIS-003-android-mvvm-flow.md`.
- `docs/12-assets/diagrams/thesis/THESIS-004-backend-integration-flow.md`.
- `docs/12-assets/diagrams/thesis/THESIS-005-auth-flow.md`.
- `docs/12-assets/diagrams/thesis/THESIS-006-database-er.md`.
- `docs/09-implementation/MVP-INCREMENTS-001-Incremental-Development-And-Automation.md`.
- `docs/10-thesis/TH-007-APA-Writing-Rules-for-Generated-Drafts.md`.
- `generar_tesis_tenderapp.py`.
- `tesis-final/TenderApp-Tesis-App-v4.docx`.

Verificacion:

- `python -m py_compile generar_tesis_tenderapp.py`.
- `python generar_tesis_tenderapp.py --with-images`.
- Inspeccion estructural del DOCX generado:
  - 6 tablas.
  - 6 rotulos `Tabla N`.
  - 23 imagenes.
  - 23 rotulos `Figura N`.
  - 31 notas `Nota.`.

Limitacion:

- La renderizacion visual a PNG no pudo completarse porque el entorno local del renderer no tiene instalado `pdf2image`.
- La verificacion realizada fue estructural, no visual pagina por pagina.

Pendientes:

- Instalar o habilitar dependencias de render (`pdf2image` y stack necesario) si se requiere QA visual completa.
- Exportar a PNG/SVG solo los diagramas Mermaid seleccionados para Word/PDF final.
- Revisar capturas definitivas para ocultar datos personales.
- Confirmar numeracion final de tablas y figuras cuando se cierre el orden del manuscrito.

## DOCS-ARCH-ANDROID-BACKEND-001 - Navegación Android y trazabilidad con backend

Fecha: 2026-07-12.

Estado: implementado como actualización documental.

Origen documental:

- `AGENTS.md`.
- `docs/README.md`.
- `docs/FR6-INDEX.md`.
- `docs/CONTEXT-000-Master-Context.md`.
- `docs/CONTEXT-001-Documentation-Map.md`.
- `docs/CONTEXT-002-Codex-Reading-Protocol.md`.
- `docs/CONTEXT-003-Task-Context-Packs.md`.
- `docs/FR5-INDEX.md`.
- `docs/04-architecture/ARC-002-Android-Application-Architecture.md`.
- `docs/04-architecture/ARC-003-Backend-Architecture.md`.
- `docs/04-architecture/ARC-004-Repository-and-Data-Flow.md`.
- `docs/09-implementation/android/MOD-001-Android-Module-Plan.md`.
- `docs/09-implementation/backend/MOD-002-Backend-Module-Plan.md`.
- `docs/07-prompts/PROMPT-005-Documentation-Update.md`.
- `docs/08-agents/AG-007-Documentation-Agent.md`.
- `docs/11-quality/QA-001-Definition-of-Done.md`.
- `docs/11-quality/QA-003-Implementation-Review-Gate.md`.

Alcance:

- Se documentó la navegación Android actual como equivalente textual a un `NavGraph` de Jetpack Compose.
- Se vinculó cada pantalla principal con ViewModel, repositorios, data sources, endpoints backend, controllers NestJS y persistencia o proveedor externo.
- Se registró qué endpoints ya forman parte del flujo Android y cuáles existen en backend pero quedan pendientes de consumo pleno.
- Se creó un diagrama Mermaid editable para sostener la explicación de trazabilidad en tesis y anexos.
- Se actualizó el índice FR2 y el mapa de fuentes técnicas para que la evidencia quede localizable.

Archivos principales:

- `docs/04-architecture/ARC-008-Android-Backend-Navigation-Traceability.md`.
- `docs/12-assets/diagrams/thesis/THESIS-007-android-navigation-backend-traceability.mmd`.
- `docs/12-assets/diagrams/thesis/THESIS-007-android-navigation-backend-traceability.md`.
- `docs/12-assets/diagrams/thesis/README.md`.
- `docs/FR2-INDEX.md`.
- `tesis-final/evidence/increment-synthesis.md`.
- `tesis-final/references/technical-source-map.md`.

Verificación:

- Inspección de rutas Android en `TenderDestination.kt` y `TenderApp.kt`.
- Inspección de ViewModels, repositorios y data sources Android mediante búsqueda en código fuente.
- Inspección de controllers backend NestJS para health, users, washers, laundry-loads, weather, predictions y notifications.
- Verificación textual posterior con `rg` para localizar `ARC-008`, `THESIS-007` y `DOCS-ARCH-ANDROID-BACKEND-001`.

Uso previsto en la tesis:

- Capítulo de consideraciones para el desarrollo del software: explicar navegación Compose, MVVM, repositorios y frontera backend.
- Capítulo de desarrollo e implementación: justificar la integración Android-backend-Supabase/Open-Meteo/Firebase.
- Anexos: incluir el diagrama Mermaid o su exportación estática si el formato final Word/PDF lo requiere.

Pendientes:

- Generar una especificación Swagger/OpenAPI consolidada para backend si se decide documentar contratos HTTP con mayor detalle.
- Exportar `THESIS-007` como imagen solo cuando se cierre el documento final de tesis o anexos.

## DOCS-BACKEND-OPENAPI-001 - Documentación Swagger/OpenAPI del backend

Fecha: 2026-07-12.

Estado: implementado como actualización documental.

Origen documental:

- `AGENTS.md`.
- `docs/README.md`.
- `docs/FR6-INDEX.md`.
- `docs/CONTEXT-000-Master-Context.md`.
- `docs/CONTEXT-001-Documentation-Map.md`.
- `docs/CONTEXT-002-Codex-Reading-Protocol.md`.
- `docs/CONTEXT-003-Task-Context-Packs.md`.
- `docs/FR5-INDEX.md`.
- `docs/04-architecture/ARC-003-Backend-Architecture.md`.
- `docs/04-architecture/ADR-004-Backend-Strategy.md`.
- `docs/04-architecture/ARC-008-Android-Backend-Navigation-Traceability.md`.
- `docs/09-implementation/backend/API-001-Backend-API-Contract-Draft.md`.
- `docs/09-implementation/backend/BACKEND-TASK-004-Configure-Swagger-OpenAPI.md`.
- `docs/09-implementation/backend/MOD-002-Backend-Module-Plan.md`.
- `docs/09-implementation/backend/DB-001-Database-Schema-Planning.md`.
- `docs/07-prompts/CTX-004-Backend-Context.md`.
- `docs/07-prompts/PROMPT-005-Documentation-Update.md`.
- `docs/08-agents/AG-004-Backend-Agent.md`.
- `docs/11-quality/QA-001-Definition-of-Done.md`.
- `docs/11-quality/QA-003-Implementation-Review-Gate.md`.

Corrección de ruta documental:

- `CONTEXT-003` menciona `docs/09-implementation/API-001-Backend-API-Contract-Draft.md` y `docs/09-implementation/DB-001-Database-Schema-Planning.md`.
- En este repositorio, los artefactos vigentes están bajo `docs/09-implementation/backend/`.

Alcance:

- Se documentó la configuración Swagger/OpenAPI actual en NestJS.
- Se registró la ruta Swagger por defecto: `/api/v1/docs`.
- Se listaron tags, endpoints, seguridad Bearer y DTOs principales.
- Se aclaró la relación entre `ARC-008`, `API-001` y Swagger/OpenAPI.
- Se registraron brechas pendientes para anexos, exportación OpenAPI y endpoints de desarrollo.

Archivos principales:

- `docs/09-implementation/backend/DOCS-BACKEND-OPENAPI-001-Swagger-OpenAPI-Contract-Documentation.md`.
- `docs/FR5-INDEX.md`.
- `docs/04-architecture/ARC-008-Android-Backend-Navigation-Traceability.md`.
- `tesis-final/evidence/increment-synthesis.md`.
- `tesis-final/references/technical-source-map.md`.

Verificación:

- Inspección de `backend/src/main.ts` para confirmar `DocumentBuilder`, `SwaggerModule.createDocument` y `SwaggerModule.setup`.
- Inspección de controllers backend para health, users, washers, laundry-loads, weather, predictions y notifications.
- Inspección de DTOs decorados con `@ApiProperty` y decorators comunes de errores.
- Verificación textual posterior con `rg` para localizar `DOCS-BACKEND-OPENAPI-001`.

Uso previsto en la tesis:

- Capítulo de arquitectura backend: describir contrato REST y frontera Android-backend.
- Capítulo de desarrollo: mostrar que el backend expone documentación OpenAPI revisable.
- Anexos técnicos: capturar Swagger UI o exportar JSON OpenAPI si se requiere evidencia formal.

Pendientes:

- Capturar Swagger UI desplegado en Render si se quiere evidencia visual.
- Exportar OpenAPI JSON solo si se decide versionar contratos entre incrementos.
- Revisar si el endpoint `notifications/test-push` debe marcarse como desarrollo antes de una entrega final.

## ALGO-DRYING-REF-001 - Referencias y heuristica de recomendacion de secado

Fecha: 2026-07-12.

Estado: implementado como actualizacion documental.

Origen documental:

- `AGENTS.md`.
- `docs/README.md`.
- `docs/FR6-INDEX.md`.
- `docs/CONTEXT-000-Master-Context.md`.
- `docs/CONTEXT-001-Documentation-Map.md`.
- `docs/CONTEXT-002-Codex-Reading-Protocol.md`.
- `docs/CONTEXT-003-Task-Context-Packs.md`.
- `docs/FR1-INDEX.md`.
- `docs/FR5-INDEX.md`.
- `docs/05-domain/business-rules/BR-001-Drying-Suitability-Score.md`.
- `docs/05-domain/business-rules/BR-002-Rain-Risk-Override.md`.
- `docs/05-domain/business-rules/BR-003-Forecast-Freshness.md`.
- `docs/05-domain/business-rules/BR-005-Explainable-Recommendation.md`.
- `docs/05-domain/entities/ENT-004-Weather-Snapshot.md`.
- `docs/05-domain/entities/ENT-005-Drying-Prediction.md`.
- `docs/05-domain/use-cases/UC-004-Calculate-Drying-Prediction.md`.
- `docs/05-domain/requirements/REQ-004-Drying-Prediction-Engine.md`.
- `docs/09-implementation/MVP-INCREMENT-004-Weather-Drying-Notifications.md`.
- `docs/09-implementation/backend/BACKEND-PRED-001-Drying-Prediction-With-Location.md`.
- `docs/09-implementation/backend/WEATHER-PRED-001-Normalize-Weather-Contract.md`.
- `docs/03-research/RES-002-Source-Credibility-Framework.md`.
- `docs/03-research/RES-003-Reference-Management-BibTeX-and-APA.md`.

Alcance:

- Se creo `ALG-001-Drying-Recommendation-Heuristic.md` para documentar la heuristica actual de recomendacion de secado.
- Se vincularon variables climaticas, ubicacion de secado, tipo de ropa, metodo de secado, puntaje, veredicto y duracion estimada.
- Se incorporo FAO 56 como referencia tecnica para justificar variables ambientales asociadas a procesos de evaporacion.
- Se reutilizo Open-Meteo como fuente oficial de las variables meteorologicas consumidas por el backend.
- Se actualizo la trazabilidad de FR1, Incremento 4, bibliografia, mapa tecnico de fuentes y sintesis incremental.

Verificacion:

- Inspeccion de `backend/src/predictions/drying-prediction-calculator.ts`.
- Inspeccion de `backend/src/predictions/drying-prediction-calculator.spec.ts`.
- Revision de documentacion oficial de Open-Meteo y FAO 56.

Pendientes:

- Calibrar pesos y duraciones con historial real de cargas.
- Evaluar variables adicionales de Open-Meteo, como radiacion, evapotranspiracion de referencia o deficit de presion de vapor.
- Capturar evidencia visual de predicciones en Dashboard y Nueva carga para anexos.

## THESIS-STRUCT-UCASAL-001 - Adaptacion de modelos UCASAL a estructura de tesis TenderApp

Fecha: 2026-07-12.

Estado: implementado como actualizacion documental.

Origen documental:

- `AGENTS.md`.
- `docs/README.md`.
- `docs/FR6-INDEX.md`.
- `docs/CONTEXT-000-Master-Context.md`.
- `docs/CONTEXT-001-Documentation-Map.md`.
- `docs/CONTEXT-002-Codex-Reading-Protocol.md`.
- `docs/CONTEXT-003-Task-Context-Packs.md`.
- `docs/FR5-INDEX.md`.
- `docs/10-thesis/TH-003-Thesis-Chapter-Map.md`.
- `docs/10-thesis/TH-006-Evidence-and-Citation-Strategy.md`.
- `docs/10-thesis/TH-007-APA-Writing-Rules-for-Generated-Drafts.md`.
- `docs/03-research/RES-002-Source-Credibility-Framework.md`.
- `docs/03-research/RES-003-Reference-Management-BibTeX-and-APA.md`.
- `tesis-final/references/modelo1.pdf`.
- `tesis-final/references/modelo2.pdf`.

Alcance:

- Se inspeccionaron los modelos locales agregados en `tesis-final/references`.
- Se definio `modelo1.pdf` como referencia principal para una estructura UCASAL simple y defendible.
- Se definio `modelo2.pdf` como referencia secundaria para fortalecer el marco teorico y la narrativa tecnica.
- Se documento una estructura propuesta para la tesis de TenderApp centrada en desarrollo incremental, Kanban, evidencia y producto funcional.

Archivos principales:

- `tesis-final/references/ucasal-model-guidance.md`.
- `tesis-final/references/README.md`.

Pendientes:

- Confirmar requisitos formales de UCASAL para portada, margenes, interlineado y tribunal.
- Ajustar el generador de tesis para consumir la estructura propuesta.
- Simplificar capturas finales y consolidarlas en un unico tema visual.

## THESIS-OUTPUT-001 - Documento base UCASAL en Markdown y DOCX

Fecha: 2026-07-12.

Estado: implementado como salida generada.

Origen documental:

- `tesis-final/references/ucasal-model-guidance.md`.
- `tesis-final/references/modelo1.pdf`.
- `tesis-final/references/modelo2.pdf`.
- `docs/10-thesis/TH-003-Thesis-Chapter-Map.md`.
- `docs/10-thesis/TH-006-Evidence-and-Citation-Strategy.md`.
- `docs/10-thesis/TH-007-APA-Writing-Rules-for-Generated-Drafts.md`.
- `docs/03-research/literature/bibliography.bib`.

Alcance:

- Se creo la carpeta `tesis-output/` para separar salidas generadas de fuentes vivas.
- Se genero una estructura Markdown editable con titulos, subtitulos, indices y placeholders.
- Se genero una base `.docx` con formato academico: Times New Roman, hoja A4, margenes formales, interlineado 1,5, sangria de primera linea, titulos jerarquicos, indices, encabezado, pie de pagina y tablas con estilo compatible con APA.
- Se incluyo un marco teorico amplio y editable centrado en Android, Kotlin, Compose, MVVM, repositorios, REST, NestJS, Supabase, Firebase, Open-Meteo, variables climaticas, heuristicas, Kanban y desarrollo asistido por inteligencia artificial.

Archivos principales:

- `tesis-output/generar_estructura_tesis.py`.
- `tesis-output/TenderApp-Tesis-Estructura-UCASAL.md`.
- `tesis-output/generar_docx_ucasal.py`.
- `tesis-output/TenderApp-Tesis-Base-UCASAL.docx`.
- `tesis-output/README.md`.

Verificacion:

- Se ejecuto el generador Markdown.
- Se ejecuto el generador DOCX.
- Se verifico estructuralmente el DOCX con `python-docx`: 227 parrafos, 6 tablas y encabezados jerarquicos detectados.

Pendiente:

- El render automatico a PNG no pudo completarse porque no se encontro LibreOffice/`soffice` en el entorno.
- Abrir el DOCX en Word, actualizar el indice general y revisar visualmente saltos de pagina, tablas y placeholders.

## THESIS-THEORY-DRYING-001 - Refuerzo del marco teorico sobre clima, heuristica y score

Fecha: 2026-07-12.

Estado: implementado como actualizacion de salida generada.

Origen documental:

- `docs/05-domain/algorithms/ALG-001-Drying-Recommendation-Heuristic.md`.
- `docs/05-domain/business-rules/BR-001-Drying-Suitability-Score.md`.
- `docs/05-domain/business-rules/BR-002-Rain-Risk-Override.md`.
- `backend/src/predictions/drying-prediction-calculator.ts`.
- `tesis-output/TenderApp-Tesis-Base-UCASAL.docx` editado manualmente por el autor.

Alcance:

- Se reforzo el esquema base de tesis con una explicacion mas completa del nucleo funcional: prediccion meteorologica, prediccion de secado, variables climaticas, ubicacion de secado, heuristica, score, veredicto, duracion estimada y limites.
- Se preservo el `.docx` ya editado manualmente por el autor.
- Se genero una variante nueva para copiar o trasladar contenido: `TenderApp-Tesis-Base-UCASAL-Marco-Teorico-Core.docx`.
- Se actualizo el Markdown generado `TenderApp-Tesis-Estructura-UCASAL.md`.

Pendientes:

- Trasladar manualmente al documento editado las subsecciones nuevas que correspondan.
- Revisar en Word el indice general y la numeracion de paginas.
- Completar la redaccion con citas APA finales y referencias normalizadas.

## THESIS-OUTPUT-002 - Merge pulido y conciso de tesis

Fecha: 2026-07-12.

Estado: implementado como salida generada.

Origen documental:

- `tesis-final/TenderApp-Tesis-App-v4.docx`.
- `tesis-final/tesis-tenderapp-app.md`.
- `tesis-output/TenderApp-Tesis-Base-UCASAL-Marco-Teorico-Core.docx`.
- `tesis-final/evidence/increment-synthesis.md`.
- `tesis-final/references/bibliografia.md`.
- `tesis-final/references/technical-source-map.md`.
- `docs/05-domain/algorithms/ALG-001-Drying-Recommendation-Heuristic.md`.
- `docs/04-architecture/ARC-008-Android-Backend-Navigation-Traceability.md`.
- `docs/07-prompts/PROMPT-006-Thesis-Section-Drafting.md`.
- `docs/08-agents/AG-009-Thesis-Writer-Agent.md`.
- `docs/08-agents/AG-010-Citation-Reviewer-Agent.md`.

Alcance:

- Se genero una version integrada, concisa y estable de la tesis en `tesis-output`.
- La nueva salida prioriza prosa academica, marco teorico defendible, referencias basicas en estilo APA y ausencia de tablas tecnicas pesadas.
- Se reforzo el nucleo de clima, heuristica, score, ubicacion de secado y notificaciones sin arrastrar anexos extensos de la v4.
- Se conservaron los documentos manuales existentes sin sobrescribirlos.

Archivos principales:

- `tesis-output/generar_tesis_output_pulida.py`.
- `tesis-output/TenderApp-Tesis-Output-Pulida.md`.
- `tesis-output/TenderApp-Tesis-Output-Pulida.docx`.
- `tesis-output/README.md`.

Verificacion:

- Se ejecuto el generador nuevo.
- Se verifico estructuralmente el DOCX con `python-docx`: 168 parrafos, 0 tablas, 1 seccion y encabezados jerarquicos.
- Se intento render automatico, pero no pudo completarse porque no se encontro LibreOffice/`soffice` en el entorno.

Pendientes:

- Abrir `TenderApp-Tesis-Output-Pulida.docx` en Word y revisar visualmente saltos de pagina, estilos y numeracion.
- Agregar manualmente figuras, capturas y diagramas seleccionados.
- Ajustar referencias finales segun criterio APA/UCASAL antes de entrega.

## THESIS-OUTPUT-003 - Ampliacion enfocada en TenderApp y estructura academica

Fecha: 2026-07-12.

Estado: implementado como salida generada.

Origen documental:

- `tesis-final/references/ucasal-model-guidance.md`.
- `tesis-final/references/modelo1.pdf` y `modelo2.pdf` como guias estructurales, no como fuentes bibliograficas.
- `tesis-final/evidence/increment-synthesis.md`.
- `tesis-final/references/bibliografia.md`.
- `tesis-final/references/technical-source-map.md`.
- `docs/03-research/literature/bibliography.bib`.
- `docs/05-domain/algorithms/ALG-001-Drying-Recommendation-Heuristic.md`.
- `docs/05-domain/business-rules/BR-001-Drying-Suitability-Score.md`.
- `docs/05-domain/business-rules/BR-002-Rain-Risk-Override.md`.
- `docs/10-thesis/TH-003-Thesis-Chapter-Map.md`.
- `docs/10-thesis/TH-006-Evidence-and-Citation-Strategy.md`.
- `docs/10-thesis/TH-007-APA-Writing-Rules-for-Generated-Drafts.md`.

Alcance:

- Se amplio el output pulido con foco exclusivo en TenderApp.
- Se incorporo una seccion de estado de la cuestion aplicado, sin realizar afirmaciones comerciales no verificadas.
- Se agrego una tabla de tecnologias y criterios de seleccion.
- Se agrego un cronograma incremental de duracion variable, coherente con el enfoque Kanban y no con Scrum estricto.
- Se incorporo un capitulo de factibilidad, costos y riesgos.
- Se mantuvo la separacion entre fuentes editables, evidencia del proyecto y salida Word generada.

Archivos principales:

- `tesis-output/generar_tesis_output_pulida.py`.
- `tesis-output/TenderApp-Tesis-Output-Pulida.md`.
- `tesis-output/TenderApp-Tesis-Output-003.docx`.
- `tesis-output/TenderApp-Tesis-Output-003-latest.docx` cuando el archivo principal queda bloqueado por Word.
- `tesis-output/README.md`.

Verificacion:

- Se ejecuto `python tesis-output/generar_tesis_output_pulida.py`.
- Se verifico estructuralmente `TenderApp-Tesis-Output-003-latest.docx` con `python-docx`: 214 parrafos, 5 tablas y encabezados jerarquicos.
- El generador produjo `TenderApp-Tesis-Output-003-latest.docx` porque `TenderApp-Tesis-Output-003.docx` estaba bloqueado por el sistema, probablemente abierto en Word.
- Se intento render automatico con `render_docx.py`, pero no pudo completarse porque no se encontro LibreOffice/`soffice` en el entorno (`FileNotFoundError`).

Pendientes:

- Realizar QA visual en Word o cuando LibreOffice/`soffice` este disponible.
- Actualizar el indice general desde Word si se desea numeracion de paginas real.
- Seleccionar capturas finales y diagramas exportados para anexos.

## ENERGY-PRED-001 - Planificacion de eficiencia energetica, centrifugado y costo aproximado

Fecha: 2026-07-12.

Estado: planificado; pendiente de implementacion tecnica.

Origen documental:

- Pedido del autor adjunto en `C:\Users\nacho\.codex\attachments\d41a5438-4eb5-4bc7-94d6-fa570d8dbe97\pasted-text.txt`.
- `docs/09-implementation/BACKLOG-001-Product-Backlog.md`.
- `docs/09-implementation/MVP-INCREMENT-004-Weather-Drying-Notifications.md`.
- `docs/09-implementation/MVP-INCREMENT-004B-Functional-Flow-Closure.md`.
- `docs/05-domain/algorithms/ALG-001-Drying-Recommendation-Heuristic.md`.
- `docs/04-architecture/ARC-008-Android-Backend-Navigation-Traceability.md`.
- `docs/10-thesis/TH-006-Evidence-and-Citation-Strategy.md`.

Alcance:

- Se creo el artefacto `ENERGY-PRED-001` como refinamiento post-MVP.
- Se definio que eficiencia energetica afecta costo aproximado y explicacion, mientras que centrifugado afecta humedad residual y tiempo estimado de secado.
- Se propuso usar desplegables controlados para eficiencia y rpm, con fallback cuando el dato sea desconocido.
- Se definio que no se deben inventar tarifas reales ni prometer precision economica exacta.
- Se agrego `PB-023` al backlog con tareas backend, Android, Supabase, QA y tesis.
- Se creo una defensa conceptual para explicar la diferencia entre TenderApp, apps climaticas, recordatorios y lavarropas inteligentes.

Archivos principales:

- `docs/09-implementation/ENERGY-PRED-001-Washer-Efficiency-Spin-Cost-Prediction.md`.
- `docs/10-thesis/TH-009-TenderApp-Conceptual-Defense.md`.
- `docs/09-implementation/BACKLOG-001-Product-Backlog.md`.
- `tesis-final/evidence/increment-log.md`.

Verificacion:

- Se reviso el codigo actual y se detecto que `energyLabel`, `capacityKg` y `waterUsageLiters` ya existen en el flujo de lavarropas.
- Se detecto que `spinRpm` y costo aproximado aun no forman parte del contrato de prediccion.
- No se modifico codigo de backend ni Android en esta etapa.

Pendientes:

- Aprobar la primera tarea tecnica recomendada: `ENERGY-PRED-DOC-001`.
- Luego aplicar migracion Supabase y actualizar contratos backend/Android en tareas separadas.
- Agregar capturas y smoke test cuando cambie la UI.

## ENERGY-PRED-DOC-001 - Reglas de heuristica para eficiencia, centrifugado y costo

Fecha: 2026-07-12.

Estado: implementado como actualizacion documental.

Origen documental:

- `docs/09-implementation/ENERGY-PRED-001-Washer-Efficiency-Spin-Cost-Prediction.md`.
- `docs/05-domain/algorithms/ALG-001-Drying-Recommendation-Heuristic.md`.
- `docs/09-implementation/BACKLOG-001-Product-Backlog.md`.

Alcance:

- Se actualizo `ALG-001` a version `0.2.0`.
- Se agregaron entradas nuevas a la heuristica: `washingProgram`, `spinRpm`, `washerEnergyLabel`, `washerCapacityKg`, `loadSize` y `waterUsageLiters`.
- Se definieron valores controlados para eficiencia energetica, rpm y tamano de carga.
- Se agregaron reglas de ajuste por centrifugado y carga para el tiempo estimado de secado.
- Se agregaron reglas de costo aproximado, factores por eficiencia energetica y programa de lavado.
- Se documentaron defaults y fallback para no romper datos anteriores.
- Se reforzaron limites: no medir consumo real, no inventar tarifas y no prometer exactitud economica.

Archivos principales:

- `docs/05-domain/algorithms/ALG-001-Drying-Recommendation-Heuristic.md`.
- `docs/09-implementation/ENERGY-PRED-001-Washer-Efficiency-Spin-Cost-Prediction.md`.
- `docs/09-implementation/BACKLOG-001-Product-Backlog.md`.
- `tesis-final/evidence/increment-log.md`.

Verificacion:

- Se reviso que la tarea no modifica codigo ejecutable.
- Se verifico que `ENERGY-PRED-DOC-001` quedara marcado como implementado en el plan y backlog.

Pendientes:

- `SUPABASE-ENERGY-001`: definir y aplicar migracion nullable para rpm y campos relacionados.
- `BACKEND-ENERGY-001`: actualizar DTOs, validaciones y Swagger.
- `BACKEND-ENERGY-002`: implementar calculadora ampliada y tests.
- `ANDROID-ENERGY-001/002/003`: propagar modelos, selectores y visualizacion.

## SUPABASE-ENERGY-001 - Migracion de campos para prediccion energetica

Fecha: 2026-07-12.

Estado: implementado en repositorio; pendiente de aplicacion manual en Supabase si corresponde.

Origen documental:

- `docs/09-implementation/ENERGY-PRED-001-Washer-Efficiency-Spin-Cost-Prediction.md`.
- `docs/05-domain/algorithms/ALG-001-Drying-Recommendation-Heuristic.md`.
- `docs/09-implementation/backend/DB-001-Database-Schema-Planning.md`.
- `docs/09-implementation/BACKLOG-001-Product-Backlog.md`.

Alcance:

- Se agrego `supabase/migrations/006_energy_prediction_inputs.sql`.
- Se incorporo `washers.default_spin_rpm` como valor habitual de centrifugado del lavarropas.
- Se incorporaron en `laundry_loads` los campos `spin_rpm`, `load_size` y campos de snapshot para consumo/costo aproximado.
- Todos los campos nuevos son nullable para no romper cargas ni lavarropas existentes.
- Se agregaron restricciones controladas con `not valid` para proteger escrituras nuevas sin bloquear datos historicos.
- Se actualizo el orden de migraciones en `supabase/README.md`.
- Se agrego el documento de tarea `SUPABASE-ENERGY-001` con consultas de verificacion manual.

Archivos principales:

- `supabase/migrations/006_energy_prediction_inputs.sql`.
- `supabase/README.md`.
- `docs/09-implementation/infrastructure/SUPABASE-ENERGY-001-Energy-Prediction-Inputs-Migration.md`.
- `docs/09-implementation/ENERGY-PRED-001-Washer-Efficiency-Spin-Cost-Prediction.md`.
- `docs/09-implementation/backend/DB-001-Database-Schema-Planning.md`.
- `docs/09-implementation/BACKLOG-001-Product-Backlog.md`.
- `tesis-final/evidence/increment-log.md`.

Verificacion:

- Se verifico que la migracion sea idempotente mediante `add column if not exists` y alta condicional de constraints.
- Se verifico que la tarea no modifica codigo ejecutable de backend ni Android.
- La verificacion real en Supabase debe realizarse con las consultas incluidas en el documento de tarea.

Pendientes:

- Ejecutar `006_energy_prediction_inputs.sql` en Supabase y registrar evidencia sin exponer datos sensibles.
- `BACKEND-ENERGY-001`: actualizar DTOs, validaciones y Swagger.
- `BACKEND-ENERGY-002`: ampliar calculadora de prediccion con rpm, carga y costo aproximado.
- `ANDROID-ENERGY-001/002/003`: propagar modelos, selectores y visualizacion del costo/factores.

## BACKEND-ENERGY-001 - Contrato backend para prediccion energetica

Fecha: 2026-07-12.

Estado: implementado.

Origen documental:

- `docs/09-implementation/ENERGY-PRED-001-Washer-Efficiency-Spin-Cost-Prediction.md`.
- `docs/09-implementation/infrastructure/SUPABASE-ENERGY-001-Energy-Prediction-Inputs-Migration.md`.
- `docs/05-domain/algorithms/ALG-001-Drying-Recommendation-Heuristic.md`.
- `docs/09-implementation/BACKLOG-001-Product-Backlog.md`.

Alcance:

- Se agrego `backend/src/energy/energy-prediction-types.ts` con valores controlados para rpm, tamano de carga, nivel de costo y confianza.
- Se agrego `defaultSpinRpm` al contrato de lavarropas.
- Se agregaron `spinRpm` y `loadSize` al contrato de creacion/respuesta de cargas.
- Se agregaron campos de snapshot de costo/consumo aproximado a la respuesta de cargas.
- Se agregaron entradas opcionales de energia/rpm/carga al request de prediccion.
- Se agrego `estimatedCost` a la respuesta de prediccion, devuelto como `null` hasta implementar `BACKEND-ENERGY-002`.
- Se actualizaron validaciones y tests para rechazar valores fuera de los enumerados controlados.

Archivos principales:

- `backend/src/energy/energy-prediction-types.ts`.
- `backend/src/washers/dto/save-washer-request.dto.ts`.
- `backend/src/washers/dto/washer-response.dto.ts`.
- `backend/src/washers/washers.validation.ts`.
- `backend/src/washers/washers.mapper.ts`.
- `backend/src/washers/washers.service.ts`.
- `backend/src/laundry-loads/dto/create-laundry-load-request.dto.ts`.
- `backend/src/laundry-loads/dto/laundry-load-response.dto.ts`.
- `backend/src/laundry-loads/laundry-loads.validation.ts`.
- `backend/src/laundry-loads/laundry-loads.mapper.ts`.
- `backend/src/laundry-loads/laundry-loads.supabase-data-source.ts`.
- `backend/src/predictions/dto/create-drying-prediction-request.dto.ts`.
- `backend/src/predictions/dto/drying-prediction-response.dto.ts`.
- `backend/src/predictions/drying-prediction.validation.ts`.
- `backend/src/predictions/predictions.service.ts`.
- `docs/09-implementation/backend/BACKEND-ENERGY-001-Energy-Aware-DTO-and-Swagger-Contract.md`.

Verificacion:

- `npm.cmd run build`: exitoso.
- `npm.cmd test -- --runInBand`: exitoso, 32 suites y 114 tests.

Pendientes:

- `BACKEND-ENERGY-002`: aplicar rpm, tamano de carga, eficiencia y consumo al calculo heuristico.
- `ANDROID-ENERGY-001/002/003`: consumir y visualizar los nuevos campos desde Android.
- Ejecutar y verificar la migracion `006_energy_prediction_inputs.sql` en Supabase si aun no fue aplicada.

## MVP-INCREMENT-005 - Reclasificacion de prediccion energetica como extension del MVP

Fecha: 2026-07-12.

Estado: documentado.

Origen documental:

- `docs/09-implementation/MVP-001-MVP-Scope-Definition.md`.
- `docs/09-implementation/MVP-INCREMENTS-001-Incremental-Development-And-Automation.md`.
- `docs/09-implementation/MVP-INCREMENT-005-Energy-Aware-Prediction.md`.
- `docs/09-implementation/ENERGY-PRED-001-Washer-Efficiency-Spin-Cost-Prediction.md`.
- `docs/09-implementation/BACKLOG-001-Product-Backlog.md`.

Alcance:

- Se creo `MVP-INCREMENT-005` para ordenar la prediccion energetica, centrifugado y costo aproximado como incremento funcional.
- Se reclasifico `PB-023` desde refinamiento post-MVP hacia extension controlada del MVP.
- Se actualizo `MVP-001` para incluir eficiencia del lavarropas, rpm, tamano de carga y costo aproximado como parte del alcance incremental.
- Se actualizo `MVP-INCREMENTS-001` con una quinta fila de incremento y descripcion academica del objetivo de valor.
- Se mantuvo el limite conceptual: la estimacion de costo es heuristica, no certificada ni tarifaria.

Motivo:

La implementacion de `SUPABASE-ENERGY-001` y `BACKEND-ENERGY-001` ya inicio el trabajo tecnico. Para preservar trazabilidad, el sistema documental debe reflejar que la funcionalidad dejo de ser solamente una idea futura y paso a formar parte del MVP demostrable.

Pendientes:

- `BACKEND-ENERGY-002`: activar calculo heuristico.
- `ANDROID-ENERGY-001/002/003`: completar soporte Android y UI.
- `QA-ENERGY-PRED-001`: registrar smoke test y capturas.

## BACKEND-ENERGY-002 - Calculadora de prediccion energetica

Fecha: 2026-07-12.

Estado: implementado.

Origen documental:

- `docs/09-implementation/MVP-INCREMENT-005-Energy-Aware-Prediction.md`.
- `docs/09-implementation/ENERGY-PRED-001-Washer-Efficiency-Spin-Cost-Prediction.md`.
- `docs/05-domain/algorithms/ALG-001-Drying-Recommendation-Heuristic.md`.
- `docs/09-implementation/BACKLOG-001-Product-Backlog.md`.

Alcance:

- Se amplio `DryingPredictionCalculator` para aceptar `washingProgram`, `spinRpm`, `loadSize`, `washerEnergyLabel`, `washerCapacityKg` y `waterUsageLiters`.
- `spinRpm` modifica humedad residual estimada y tiempo de secado.
- `loadSize` modifica tiempo de secado y consumo aproximado.
- `washerEnergyLabel` y `washingProgram` modifican costo relativo estimado.
- La respuesta de prediccion ahora devuelve `estimatedCost` con `amount` y `currency` en `null`, mas `level`, `confidence`, `estimatedEnergyKwh` y `estimatedWaterLiters`.
- No se inventaron tarifas reales ni costos monetarios exactos.

Archivos principales:

- `backend/src/predictions/drying-prediction-calculator.ts`.
- `backend/src/predictions/drying-prediction-calculator.spec.ts`.
- `backend/src/predictions/predictions.service.ts`.
- `backend/src/predictions/predictions.service.spec.ts`.
- `docs/09-implementation/backend/BACKEND-ENERGY-002-Energy-Aware-Prediction-Calculator.md`.
- `docs/09-implementation/MVP-INCREMENT-005-Energy-Aware-Prediction.md`.
- `docs/09-implementation/ENERGY-PRED-001-Washer-Efficiency-Spin-Cost-Prediction.md`.
- `docs/05-domain/algorithms/ALG-001-Drying-Recommendation-Heuristic.md`.
- `docs/09-implementation/BACKLOG-001-Product-Backlog.md`.

Verificacion:

- `npm.cmd run build`: exitoso.
- `npm.cmd test -- --runInBand`: exitoso, 32 suites y 117 tests.

Pendientes:

- `ANDROID-ENERGY-002`: agregar selectores compactos de rpm/tamano de carga.
- `ANDROID-ENERGY-003`: visualizar costo aproximado y factores dominantes.
- `QA-ENERGY-PRED-001`: smoke end-to-end con capturas.

## ANDROID-ENERGY-001 - Modelos y mappers Android para prediccion energetica

Fecha: 2026-07-12.

Estado: implementado.

Origen documental:

- `docs/09-implementation/MVP-INCREMENT-005-Energy-Aware-Prediction.md`.
- `docs/09-implementation/ENERGY-PRED-001-Washer-Efficiency-Spin-Cost-Prediction.md`.
- `docs/09-implementation/BACKLOG-001-Product-Backlog.md`.
- `docs/04-architecture/ARC-002-Android-Application-Architecture.md`.
- `docs/04-architecture/ARC-004-Repository-and-Data-Flow.md`.

Alcance:

- Se agregaron tipos de dominio Android para `SpinSpeedRpm`, `LoadSize` y `EstimatedWashingCost`.
- `Washer` ahora puede recibir `defaultSpinRpm`.
- `LaundryLoad` ahora puede recibir `spinRpm`, `loadSize` y snapshot de costo/consumo aproximado.
- `DryingPrediction` ahora puede recibir `estimatedCost`.
- Los DTOs remotos de lavarropas, cargas y predicciones aceptan los campos energeticos ya expuestos por backend.
- Los mappers remotos conservan compatibilidad con datos antiguos porque todos los campos nuevos son opcionales.
- El preview de Nueva carga puede enviar datos del lavarropas seleccionado hacia la prediccion backend cuando ya estan disponibles.

Archivos principales:

- `app/src/main/java/com/tesis_pro/tenderapp/domain/model/SpinSpeedRpm.kt`.
- `app/src/main/java/com/tesis_pro/tenderapp/domain/model/LoadSize.kt`.
- `app/src/main/java/com/tesis_pro/tenderapp/domain/model/EstimatedWashingCost.kt`.
- `app/src/main/java/com/tesis_pro/tenderapp/data/remote/dto/WasherDtos.kt`.
- `app/src/main/java/com/tesis_pro/tenderapp/data/remote/dto/LaundryLoadDtos.kt`.
- `app/src/main/java/com/tesis_pro/tenderapp/data/remote/dto/PredictionDtos.kt`.
- `docs/09-implementation/android/ANDROID-ENERGY-001-Android-Energy-Models-and-Mappers.md`.

Verificacion:

- `.\gradlew.bat --no-daemon :app:testDebugUnitTest`: exitoso.
- `.\gradlew.bat --no-daemon :app:assembleDebug`: exitoso.

Pendientes:

- `ANDROID-ENERGY-003`: mostrar costo aproximado y factores dominantes en Dashboard/Nueva carga.
- `QA-ENERGY-PRED-001`: validar flujo energetico end-to-end con capturas.

## ANDROID-ENERGY-002 - Selectores Android de centrifugado y tamano de carga

Fecha: 2026-07-12.

Estado: implementado.

Origen documental:

- `docs/09-implementation/MVP-INCREMENT-005-Energy-Aware-Prediction.md`.
- `docs/09-implementation/ENERGY-PRED-001-Washer-Efficiency-Spin-Cost-Prediction.md`.
- `docs/09-implementation/BACKLOG-001-Product-Backlog.md`.
- `docs/06-design/UI-001-Design-System.md`.
- `docs/06-design/UI-005-New-Load-Flow-Specification.md`.
- `docs/04-architecture/ARC-002-Android-Application-Architecture.md`.

Alcance:

- Settings incorpora un selector compacto para `defaultSpinRpm` del lavarropas.
- La lista de lavarropas muestra el centrifugado habitual cuando existe.
- Nueva carga incorpora selector de rpm del ciclo.
- Nueva carga incorpora selector de tamano de carga.
- El rpm habitual del lavarropas seleccionado funciona como valor inteligente si el usuario no eligio rpm manualmente.
- Las solicitudes de prediccion y creacion de carga envian `spinRpm` y `loadSize`.
- Se agregaron textos localizados en ingles y espanol.

Archivos principales:

- `app/src/main/java/com/tesis_pro/tenderapp/ui/screens/SettingsViewModel.kt`.
- `app/src/main/java/com/tesis_pro/tenderapp/ui/screens/SettingsScreen.kt`.
- `app/src/main/java/com/tesis_pro/tenderapp/ui/screens/NewLoadViewModel.kt`.
- `app/src/main/java/com/tesis_pro/tenderapp/ui/screens/NewLoadScreen.kt`.
- `app/src/main/res/values/strings.xml`.
- `app/src/main/res/values-es/strings.xml`.
- `docs/09-implementation/android/ANDROID-ENERGY-002-Android-Energy-Selectors.md`.

Verificacion:

- `.\gradlew.bat --no-daemon :app:testDebugUnitTest`: exitoso.
- `.\gradlew.bat --no-daemon :app:assembleDebug`: exitoso.

Pendientes:

- `QA-ENERGY-PRED-001`: validar flujo energetico end-to-end con capturas.

## ANDROID-ENERGY-003 - Visualizacion de costo aproximado y factores dominantes

Fecha: 2026-07-12.

Estado: implementado.

Origen documental:

- `docs/09-implementation/MVP-INCREMENT-005-Energy-Aware-Prediction.md`.
- `docs/09-implementation/ENERGY-PRED-001-Washer-Efficiency-Spin-Cost-Prediction.md`.
- `docs/09-implementation/BACKLOG-001-Product-Backlog.md`.
- `docs/06-design/UI-001-Design-System.md`.
- `docs/06-design/UI-004-Dashboard-Screen-Specification.md`.
- `docs/06-design/UI-005-New-Load-Flow-Specification.md`.
- `docs/06-design/prototypes/WhatsApp Image 2026-07-07 at 08.57.57.jpeg`.
- `docs/06-design/prototypes/WhatsApp Image 2026-07-07 at 08.58.09.jpeg`.

Alcance:

- Se agrego un componente Compose reutilizable `EnergyCostSummary`.
- Nueva carga muestra costo aproximado, kWh, litros de agua y confianza cuando la prediccion backend lo informa.
- Dashboard muestra el mismo resumen en la tarjeta de carga activa.
- Dashboard usa `prediction.estimatedCost` y, si no existe, el snapshot `load.estimatedWashingCost`.
- Los textos aclaran que la estimacion no representa un valor de facturacion.
- La UI sigue el criterio del mockup: informacion numerica compacta y accionable sin sobrecargar la pantalla.

Archivos principales:

- `app/src/main/java/com/tesis_pro/tenderapp/ui/components/EnergyCostSummary.kt`.
- `app/src/main/java/com/tesis_pro/tenderapp/ui/screens/NewLoadViewModel.kt`.
- `app/src/main/java/com/tesis_pro/tenderapp/ui/screens/NewLoadScreen.kt`.
- `app/src/main/java/com/tesis_pro/tenderapp/ui/screens/DashboardViewModel.kt`.
- `app/src/main/java/com/tesis_pro/tenderapp/ui/screens/DashboardScreen.kt`.
- `app/src/main/res/values/strings.xml`.
- `app/src/main/res/values-es/strings.xml`.
- `docs/09-implementation/android/ANDROID-ENERGY-003-Android-Energy-Cost-Display.md`.

Verificacion:

- `.\gradlew.bat --no-daemon :app:testDebugUnitTest`: exitoso.
- `.\gradlew.bat --no-daemon :app:assembleDebug`: exitoso.

Pendientes:

- Captura opcional de Nueva carga con resumen energetico ya cargado, si se requiere evidencia visual adicional.

## QA-ENERGY-PRED-001 - Evidencia del incremento energetico

Fecha: 2026-07-12.

Estado: evidencia registrada.

Origen documental:

- `docs/09-implementation/MVP-INCREMENT-005-Energy-Aware-Prediction.md`.
- `docs/09-implementation/ENERGY-PRED-001-Washer-Efficiency-Spin-Cost-Prediction.md`.
- `docs/09-implementation/android/ANDROID-ENERGY-003-Android-Energy-Cost-Display.md`.
- `docs/09-implementation/integration/QA-ENERGY-PRED-001-Energy-Aware-Validation.md`.
- `tesis-final/evidence/screenshots/v5/`.

Evidencia visual:

- `01-v5-dashboard-electricity-new.png`: Dashboard con recomendacion climatica, carga activa y costo aproximado de lavado.
- `02-v5-new-load-new-fields.png`: Nueva carga con selectores de programa, centrifugado y tamano de carga.
- `03-v5-washer-new-fields.png`: Settings con capacidad, uso de agua, etiqueta energetica y centrifugado habitual del lavarropas.

Resultado:

- El Incremento 5 queda respaldado por pruebas automatizadas Android, build exitoso y capturas manuales.
- La evidencia muestra que el sistema comunica costo aproximado, energia, agua y confianza sin presentar tarifa real.
- La captura de Nueva carga documenta selectores nuevos, aunque no muestra el resumen energetico final porque fue tomada durante la consulta backend.

Pendiente menor:

- Capturar Nueva carga una vez terminada la prediccion backend si se requiere evidencia visual especifica del resumen energetico en esa pantalla.

## BACKEND-ENERGY-003 - Enriquecimiento de prediccion con metadata del lavarropas

Fecha: 2026-07-12.

Estado: implementado.

Origen documental:

- `docs/09-implementation/MVP-INCREMENT-005-Energy-Aware-Prediction.md`.
- `docs/09-implementation/ENERGY-PRED-001-Washer-Efficiency-Spin-Cost-Prediction.md`.
- `docs/09-implementation/BACKLOG-001-Product-Backlog.md`.
- `docs/09-implementation/backend/BACKEND-ENERGY-003-Prediction-Washer-Metadata-Enrichment.md`.

Alcance:

- Se agrego un data source interno de predicciones para resolver metadata energetica desde Supabase.
- La prediccion por `laundryLoadId` ahora puede inferir `spinRpm`, `loadSize`, `energyLabel`, capacidad y litros desde la carga y el lavarropas del usuario autenticado.
- Los valores enviados explicitamente por Android siguen teniendo prioridad sobre la metadata inferida.
- Si no existe metadata suficiente, la prediccion mantiene fallback sin bloquear el flujo.

Archivos principales:

- `backend/src/predictions/prediction-energy-metadata.data-source.ts`.
- `backend/src/predictions/predictions.service.ts`.
- `backend/src/predictions/predictions.module.ts`.
- `backend/src/predictions/predictions.service.spec.ts`.

Verificacion:

- `npm.cmd test -- predictions --runInBand`: exitoso.
- `npm.cmd run build`: exitoso.

Pendientes:

- Desplegar backend y repetir smoke manual en Android/Render para confirmar que Dashboard deje de mostrar confianza baja cuando la carga activa esta asociada a un lavarropas con eficiencia y litros configurados.

## ANDROID-DATA-LIFECYCLE-001B - Retiro logico de lavarropas

Fecha: 2026-07-12.

Estado: implementado.

Origen documental:

- `docs/09-implementation/BACKLOG-001-Product-Backlog.md`.
- `docs/09-implementation/android/ANDROID-DATA-LIFECYCLE-001B-Washer-Retirement.md`.
- `docs/09-implementation/backend/API-001-Backend-API-Contract-Draft.md`.
- `docs/09-implementation/backend/DB-001-Database-Schema-Planning.md`.
- `docs/11-quality/QA-002-Acceptance-Criteria-Matrix.md`.

Alcance:

- Se agrego la migracion `supabase/migrations/007_washer_retirement.sql` con `washers.retired_at`.
- El backend cambio `DELETE /api/v1/washers/{id}` a retiro logico mediante `retired_at`, sin borrar fisicamente la fila.
- El listado backend de lavarropas filtra solo activos.
- Android Settings incorpora accion para retirar lavarropas.
- Android elimina el lavarropas retirado del estado activo despues de una respuesta exitosa.
- El historial de cargas conserva `washerId`, porque no se actualizan ni eliminan las cargas historicas.

Archivos principales:

- `supabase/migrations/007_washer_retirement.sql`.
- `backend/src/washers/washers.service.ts`.
- `backend/src/washers/washers.controller.ts`.
- `app/src/main/java/com/tesis_pro/tenderapp/data/remote/TenderBackendApi.kt`.
- `app/src/main/java/com/tesis_pro/tenderapp/data/remote/RemoteWasherDataSource.kt`.
- `app/src/main/java/com/tesis_pro/tenderapp/data/repository/BackendWasherRepository.kt`.
- `app/src/main/java/com/tesis_pro/tenderapp/ui/screens/SettingsViewModel.kt`.
- `app/src/main/java/com/tesis_pro/tenderapp/ui/screens/SettingsScreen.kt`.

Verificacion:

- `npm.cmd test -- washers --runInBand`: exitoso.
- `npm.cmd run build`: exitoso.
- `.\gradlew.bat --no-daemon :app:testDebugUnitTest`: exitoso.
- `.\gradlew.bat --no-daemon :app:assembleDebug`: exitoso.

Pendientes:

- Aplicar `supabase/migrations/007_washer_retirement.sql` en Supabase.
- Desplegar backend.
- Validar manualmente que el lavarropas retirado desaparece de Settings/Nueva carga y que el historial previo sigue visible.

## ANDROID-DATA-LIFECYCLE-001B - Validacion manual de desaparicion

Fecha: 2026-07-13.

Estado: validacion manual parcial registrada.

Origen documental:

- `docs/09-implementation/android/ANDROID-DATA-LIFECYCLE-001B-Washer-Retirement.md`.
- `docs/09-implementation/integration/QA-INCREMENT-004B-Functional-Flow-Validation.md`.
- `docs/09-implementation/BACKLOG-001-Product-Backlog.md`.

Resultado informado:

- El autor valido manualmente que un lavarropas retirado desaparece de la UI activa despues de aplicar el flujo.

Pendiente:

- Si se requiere cierre completo de evidencia para tesis, capturar o transcribir de forma redaccionada que las cargas historicas asociadas al lavarropas retirado siguen visibles.

## BACKLOG-STATUS-001 - Reconciliacion de estados principales

Fecha: 2026-07-13.

Estado: documentacion actualizada.

Origen documental:

- `docs/09-implementation/BACKLOG-001-Product-Backlog.md`.
- `tesis-final/evidence/increment-log.md`.

Motivo:

- El backlog principal mantenia estados antiguos para PB-020, PB-021, PB-022 y PB-023, aunque las tareas internas y la bitacora ya registraban implementacion, evidencia o smoke manual en progreso.

Decision:

- PB-020 queda como completado en implementacion, con entrega push real y sincronizacion de permisos Android documentadas.
- PB-021 queda implementado, con evidencia final seguida por PB-022.
- PB-022 queda como smoke manual en progreso.
- PB-023 queda con evidencia QA registrada y smoke de despliegue pendiente, sin declararlo completamente cerrado.

Pendiente:

- Cerrar `QA-INCREMENT-004B` cuando exista evidencia final suficiente de historial, estado, notificaciones y capturas redaccionadas.

## QA-INCREMENT-004B - Matriz de cierre de evidencia

Fecha: 2026-07-13.

Estado: documentacion QA ampliada.

Origen documental:

- `docs/09-implementation/MVP-INCREMENT-004B-Functional-Flow-Closure.md`.
- `docs/09-implementation/integration/QA-INCREMENT-004B-Functional-Flow-Validation.md`.
- `tesis-final/evidence/screenshots/README.md`.

Alcance:

- Se actualizo el estado del Incremento 4B para reflejar que el retiro logico de lavarropas ya fue implementado y validado parcialmente por desaparicion de la UI activa.
- Se agrego una matriz de cierre en `QA-INCREMENT-004B` que vincula flujos, estado QA, evidencia aceptada y pendientes.
- Se incorporaron como evidencia disponible las capturas v5, sin mezclar el Incremento 5 con el objetivo principal del cierre funcional.

Pendientes:

- Confirmar el historial posterior al retiro de lavarropas.
- Redactar cualquier captura o transcripcion que incluya correos, tokens, identificadores sensibles o datos personales.
- Capturar una vista final de cierre de carga/guardar ropa si se decide usarla como figura principal.

## DOCS-API-ENERGY-001 - Contrato API y smoke de prediccion energetica

Fecha: 2026-07-13.

Estado: documentacion actualizada.

Origen documental:

- `docs/09-implementation/backend/API-001-Backend-API-Contract-Draft.md`.
- `docs/09-implementation/integration/QA-ENERGY-PRED-001-Energy-Aware-Validation.md`.
- `docs/09-implementation/BACKLOG-001-Product-Backlog.md`.
- `docs/09-implementation/MVP-INCREMENT-005-Energy-Aware-Prediction.md`.
- `docs/09-implementation/ENERGY-PRED-001-Washer-Efficiency-Spin-Cost-Prediction.md`.

Alcance:

- Se actualizo `API-001` para incluir `defaultSpinRpm`, `spinRpm`, `loadSize`, `washerEnergyLabel`, `washerCapacityKg`, `waterUsageLiters` y `estimatedCost`.
- Se documento que la metadata energetica en predicciones es opcional y puede inferirse desde `laundryLoadId` cuando existe una carga asociada a un lavarropas del usuario.
- Se agrego a `QA-ENERGY-PRED-001` un procedimiento de smoke para Thunder/Render con dos variantes: metadata explicita y metadata inferida desde la carga.
- Se mantuvo la aclaracion de que el costo es heuristico y no representa medicion certificada ni facturacion real.

Pendiente:

- Ejecutar el smoke contra Render con token valido y respuesta redactada.
- Adjuntar captura de Thunder o Android si se quiere usar como evidencia final de despliegue.

## THESIS-UCASAL-ADAPT-001 - Adaptacion de tesis final a estructura existente

Fecha: 2026-07-13.

Estado: borrador editable generado.

Origen documental:

- `C:\Users\nacho\.codex\attachments\6c416785-32d5-42a0-9c14-999c7ed681a9\pasted-text.txt`.
- `tesis-output/TenderApp-Tesis-UCASAL-Adaptada.md`.
- `tesis-output/README.md`.
- `tesis-final/evidence/increment-synthesis.md`.
- `tesis-final/references/technical-source-map.md`.
- `tesis-final/references/bibliografia.md`.
- `tesis-final/evidence/screenshots/README.md`.
- `docs/10-thesis/TH-001-UCASAL-Structure.md`.
- `docs/10-thesis/TH-006-Evidence-and-Citation-Strategy.md`.
- `docs/10-thesis/TH-007-APA-Writing-Rules-for-Generated-Drafts.md`.

Alcance:

- Se genero una adaptacion completa en Markdown respetando la estructura solicitada: Abstract, Introduccion, Estado de la Cuestion, Definicion del Problema, Solucion Propuesta, Factibilidad, Riesgos, Resultados, Referencias y Anexos.
- Se actualizo el texto base para reflejar el MVP real: Android nativo, backend NestJS, Supabase, Open-Meteo, Firebase Cloud Messaging, Render, ubicaciones, prediccion, historial, QA, evidencia visual e Incremento 5 energetico.
- Se incluyeron tablas candidatas de comparacion, division de tareas, cronograma, cumplimiento de objetivos, costos, riesgos, FODA y pruebas.
- Se listaron figuras, anexos, archivos a sincronizar y pendientes antes del freeze documental.
- Se mantuvo como pendiente la evidencia no confirmada: historial posterior al retiro de lavarropas, smoke desplegado de prediccion energetica, redaccion de datos sensibles y seleccion final de figuras.

Pendientes:

- Trasladar o regenerar el contenido hacia `.docx` cuando se decida el documento final editable.
- Revisar visualmente el `.docx` final en Word/LibreOffice.
- Completar referencias APA definitivas desde BibTeX y fuentes verificadas.

## THESIS-UCASAL-DOCX-001 - Generacion DOCX desde adaptacion UCASAL

Fecha: 2026-07-13.

Estado: documento Word generado.

Origen documental:

- `tesis-output/TenderApp-Tesis-UCASAL-Adaptada.md`.
- `tesis-output/generar_tesis_ucasal_adaptada.py`.
- `tesis-output/TenderApp-Tesis-UCASAL-Adaptada.docx`.
- `tesis-output/README.md`.

Alcance:

- Se genero una salida `.docx` reproducible desde el borrador Markdown adaptado a estructura UCASAL.
- El generador aplica formato academico base: hoja A4, margen izquierdo de 3 cm, Times New Roman, texto de 12 pt, interlineado 1,5, sangria de primera linea e indice estatico.
- Se trasladaron capitulos, subtitulos, parrafos, listas y tablas Markdown desde la seccion `Abstract` en adelante, evitando incluir notas operativas internas en el cuerpo principal.
- Se verifico estructuralmente el documento con `python-docx`: 235 parrafos, 57 titulos, 15 tablas y 1 seccion.

Pendientes:

- Revisar visualmente el `.docx` en Word o LibreOffice, porque el render automatico no pudo ejecutarse al no encontrarse `soffice` en el entorno.
- Actualizar indices dinamicos si se decide reemplazar el indice estatico por campos de Word.
- Completar manualmente capturas, diagramas exportados, referencias APA definitivas y redaccion de datos sensibles antes del freeze documental.

## QA-ENERGY-PRED-001 - Validacion visual Android del incremento energetico

Fecha: 2026-07-13.

Estado: validado manualmente.

Origen documental:

- `docs/09-implementation/integration/QA-ENERGY-PRED-001-Energy-Aware-Validation.md`.
- `docs/09-implementation/MVP-INCREMENT-005-Energy-Aware-Prediction.md`.
- `docs/09-implementation/ENERGY-PRED-001-Washer-Efficiency-Spin-Cost-Prediction.md`.
- `tesis-final/evidence/screenshots/v5/01-v5-dashboard-electricity-new.png`.
- `tesis-final/evidence/screenshots/README.md`.

Alcance:

- El autor verifico visualmente en Android que los datos energeticos llegan a la pantalla y modifican la prediccion de gasto electrico aproximado.
- Se registro `01-v5-dashboard-electricity-new.png` como evidencia principal del Dashboard con costo aproximado, kWh, litros de agua y confianza.
- Se actualizo el estado de `QA-ENERGY-PRED-001` a validacion manual Android.
- Se marco `MVP-INCREMENT-005` y `ENERGY-PRED-001` como validados, manteniendo el caracter heuristico del costo aproximado.

Pendientes:

- Opcional: agregar captura Thunder o respuesta JSON redactada del endpoint `POST /api/v1/predictions/drying` si se desea reforzar anexos tecnicos.
- Revisar que las capturas finales usadas en la tesis no expongan correos, tokens, identificadores sensibles ni datos personales innecesarios.

## QA-INCREMENT-004B - Cierre funcional validado manualmente

Fecha: 2026-07-13.

Estado: validado manualmente.

Origen documental:

- `docs/09-implementation/integration/QA-INCREMENT-004B-Functional-Flow-Validation.md`.
- `docs/09-implementation/MVP-INCREMENT-004B-Functional-Flow-Closure.md`.
- `tesis-final/evidence/increment-synthesis.md`.
- `tesis-final/evidence/screenshots/v3/`.
- `tesis-final/evidence/screenshots/v4/`.
- `tesis-final/evidence/screenshots/v5/`.

Alcance:

- El autor confirmo que el historial posterior al retiro de un lavarropas sigue visible y consistente.
- El autor confirmo que el flujo de cierre de carga/guardar ropa funciona.
- Se registro que el Dashboard se actualiza segun el estado de la carga.
- `QA-INCREMENT-004B` paso a estado `Passed - Manual Smoke Validated`.
- `MVP-INCREMENT-004B` paso a estado `Validated`.

Decision:

- Las capturas por cada estado del Dashboard se recomiendan como evidencia visual ampliada para anexos, pero no bloquean el cierre del QA.
- Para tesis final se recomienda conservar, si es posible, capturas de carga planificada, lavando, secando/colgada, ropa lista, carga finalizada/guardada e historial posterior al retiro de lavarropas.

Pendientes no bloqueantes:

- Revisar capturas finales para ocultar correos, tokens, identificadores sensibles o datos personales.
- Definir en la redaccion final si el evento de cierre se nombra como `ropa lista`, `retirar ropa` o `guardar ropa`.

## ANDROID-I18N-001 - Pulido de localizacion para evidencia v6

Fecha: 2026-07-13.

Estado: implementado y verificado.

Origen documental:

- `docs/09-implementation/android/ANDROID-I18N-001-V6-Dashboard-And-Push-Localization.md`.
- `docs/09-implementation/android/ANDROID-UI-001-Localization-and-System-Theme-Foundation.md`.
- `docs/09-implementation/android/ANDROID-SETTINGS-002-Language-Theme-Override.md`.
- `docs/09-implementation/BACKLOG-001-Product-Backlog.md`.

Alcance:

- Se preparo la aplicacion para capturas v6 con menor deriva de textos entre ingles y espanol.
- El Dashboard conserva el veredicto, puntaje y tiempos calculados por backend, pero deja de mostrar directamente el texto `reason` remoto como explicacion visible.
- Se agregaron explicaciones localizadas por veredicto para los estados recomendado, precaucion y no recomendado.
- El renderizado foreground de FCM ahora resuelve claves de notificacion (`notificationTitleKey`, `notificationBodyKey`, `titleKey`, `bodyKey`, `title_key`, `body_key`) contra recursos Android.
- Las notificaciones foreground respetan la preferencia local de idioma configurada en Settings.

Verificacion:

- `.\gradlew.bat --no-daemon :app:testDebugUnitTest`.
- `.\gradlew.bat --no-daemon :app:assembleDebug`.

Pendiente no bloqueante:

- Si FCM muestra una notificacion en segundo plano usando directamente el payload `notification` del proveedor, Android puede mostrar el texto enviado por backend antes de que la app lo resuelva localmente. Para localizacion completa en background conviene evaluar mensajes data-only o localizacion backend-side.

## QA-FORMS-001 - Matriz de validacion de formularios para v6

Fecha: 2026-07-13.

Estado: listo para QA manual.

Origen documental:

- `docs/09-implementation/integration/QA-FORMS-001-Form-Validation-Boundary.md`.
- `docs/09-implementation/BACKLOG-001-Product-Backlog.md`.
- Validaciones backend inspeccionadas en `backend/src/washers`, `backend/src/users`, `backend/src/laundry-loads`, `backend/src/predictions` y `backend/src/notifications`.
- Validaciones Android inspeccionadas en `AuthViewModel`, `NewLoadViewModel` y `SettingsViewModel`.

Alcance:

- Se definio una matriz de QA para validar formularios, longitudes maximas, formatos, coordenadas, selectores controlados y permisos.
- La matriz cubre autenticacion, nueva carga, ABM de lavarropas, ubicacion del hogar y registro de notificaciones.
- Se separo la responsabilidad entre UI Android, backend API y persistencia Supabase.
- La tarea queda preparada para ejecutarse junto con las capturas v6.

Pendientes:

- Ejecutar manualmente la matriz y registrar evidencia en `tesis-final/evidence/screenshots/v6/` o con respuestas Thunder/Swagger redactadas.
- Clasificar cualquier falla como `UX copy issue`, `Android validation gap`, `Backend validation gap` o `Evidence only / no code change`.

## DOCS-HARNESS-AUDIT-001 - Auditoria completa del harness documental

Fecha: 2026-07-13.

Estado: implementado.

Origen documental:

- `AGENTS.md`.
- `docs/README.md`.
- `docs/FR0-INDEX.md` a `docs/FR7-INDEX.md`.
- `docs/CONTEXT-000-Master-Context.md`.
- `docs/CONTEXT-001-Documentation-Map.md`.
- `docs/CONTEXT-002-Codex-Reading-Protocol.md`.
- `docs/CONTEXT-003-Task-Context-Packs.md`.
- `docs/07-prompts/PROMPT-009-Context-Audit.md`.
- `docs/08-agents/AG-000-Agent-Harness-Overview.md`.
- `docs/02-knowledge/KB-005-Documentation-Harness-Audit.md`.

Alcance:

- Se realizo una pasada completa sobre el arbol `docs/` para detectar brechas del sistema documental de asistencia con inteligencia artificial.
- Se actualizo el contexto maestro para reconocer FR6 y FR7 como parte del baseline operativo actual.
- Se corrigieron rutas heredadas como `docs/engineering/`, el prototipo HTML con espacios en el nombre y referencias del contrato backend.
- Se agregaron context packs para integracion, despliegue, evidencia MVP, validacion de formularios y auditoria del harness.
- Se actualizo el prompt de auditoria de contexto para detectar rutas obsoletas, IDs duplicados, estados inconsistentes y evidencia vigente.
- Se actualizo el overview de agentes para incluir AG-009 y AG-010, manteniendo la regla de agentes como lentes de rol y no como autoridad autonoma.

Decisiones:

- No se modificaron masivamente estados historicos Draft/Pending.
- No se eliminaron ni convirtieron archivos `.mmd`.
- Los duplicados `PROMPT-000` y `TEST-000` quedaron registrados como brecha para una tarea posterior.

Pendientes:

- Ejecutar `DOCS-HARNESS-002` para resolver IDs duplicados.
- Ejecutar `DOCS-STATUS-001` para reconciliar estados historicos con evidencia validada.
- Evaluar `DOCS-LINKS-001` para automatizar validacion de rutas internas.

## QA-FORMS-001 - Continuacion de runbook v6

Fecha: 2026-07-17.

Estado: runbook preparado; ejecucion manual pendiente.

Origen documental:

- `docs/09-implementation/integration/QA-FORMS-001-Form-Validation-Boundary.md`.
- `docs/09-implementation/BACKLOG-001-Product-Backlog.md`.
- `tesis-final/evidence/screenshots/v6/README.md`.

Alcance:

- Se amplio `QA-FORMS-001` con un orden de ejecucion en tres pasadas: Android UI, backend API y Supabase persistence.
- Se agregaron referencias de inspeccion de codigo para validaciones Android y backend.
- Se definio la convencion de nombres para capturas y evidencia redactada v6.
- Se creo `tesis-final/evidence/screenshots/v6/README.md` como punto de entrada de evidencia.

Notas de validacion:

- El formato de email se delega principalmente a Supabase/Auth.
- Las longitudes maximas de nombre de lavarropas y etiqueta de ubicacion se validan en backend; Android debe mostrar error recuperable.
- Los casos de enums invalidos deben probarse por API, porque la UI normal usa selectores controlados.

Pendientes:

- Ejecutar la matriz manualmente en Android y Thunder/Swagger.
- Adjuntar capturas o respuestas redactadas en `tesis-final/evidence/screenshots/v6/`.
- Abrir tareas separadas si aparece un `UX copy issue`, `Android validation gap` o `Backend validation gap`.

## BACKEND-WEATHER-FALLBACK-001 - Fallback backend para clima durante QA v6

Fecha: 2026-07-17.

Estado: implementado y verificado localmente.

Origen:

- Hallazgo durante `QA-FORMS-001`: Android mostraba `LOCAL_FALLBACK` en Dashboard.
- Logs Android: `GET /api/v1/weather/current?locationId=home` y `POST /api/v1/predictions/drying` devolvian `500`.
- Health de Render verificado: `GET /api/v1/health` respondio `200`.
- Open-Meteo directo verificado desde red externa: `/v1/forecast` respondio `200`.

Alcance:

- `WeatherService` ahora degrada a una respuesta backend `MOCK` marcada como `isStale=true` cuando falla el proveedor meteorologico.
- Si falla la resolucion de `user_locations` en Supabase, el backend registra warning y usa la ubicacion estatica conocida como fallback.
- Se agregaron tests unitarios para ambos caminos de degradacion.

Verificacion:

- `npm.cmd test -- weather.service.spec.ts`.
- `npm.cmd run build`.

Pendiente:

- Desplegar en Render y validar desde Android que Dashboard deje de mostrar `LOCAL_FALLBACK`.
- Revisar logs de Render si vuelve a aparecer warning de `WeatherService`, para distinguir fallo de Supabase location vs proveedor meteorologico.

## QA-OPENAPI-001 - Smoke del contrato OpenAPI desplegado

Fecha: 2026-07-18.

Estado: validado parcialmente; contrato desplegado disponible.

Origen documental:

- `docs/09-implementation/integration/QA-OPENAPI-001-Deployed-API-Contract-Smoke.md`.
- `https://tesis-t85s.onrender.com/api/v1/docs`.
- `https://tesis-t85s.onrender.com/api/v1/docs-json`.

Alcance:

- Se verifico que Swagger UI responde `200`.
- Se verifico que OpenAPI JSON responde `200`.
- Se listo el conjunto de rutas desplegadas relevantes para QA de formularios, clima, prediccion y notificaciones.

Resultado:

- El contrato desplegado permite continuar `QA-FORMS-001` y pruebas con Thunder/Swagger.
- El endpoint YAML no esta publicado (`404`), no bloqueante porque JSON si esta disponible.

Pendientes:

- Ejecutar endpoints protegidos con bearer token valido.
- Revalidar `weather/current` y `predictions/drying` luego del deploy de `BACKEND-WEATHER-FALLBACK-001`.

## QA-SUPABASE-001 - Endpoints protegidos de diagnostico Supabase

Fecha: 2026-07-18.

Estado: implementado localmente; pendiente deploy/configuracion en Render.

Origen documental:

- `docs/09-implementation/integration/QA-SUPABASE-001-Protected-Supabase-Diagnostics.md`.
- `backend/src/qa/`.
- `backend/.env.example`.

Alcance:

- Se agrego `QaModule` con endpoints protegidos bajo `/api/v1/qa/supabase`.
- `GET /status` valida tablas Supabase mediante conteos/redaccion sin devolver filas reales.
- `GET /examples` devuelve datos sinteticos para documentacion/anexo.
- Se agrego `x-qa-key` al contrato Swagger/OpenAPI.
- Los endpoints quedan deshabilitados si `QA_DIAGNOSTICS_KEY` no existe.

Verificacion:

- `npm.cmd test -- qa-key.guard.spec.ts`.
- `npm.cmd run build`.

Pendientes:

- Configurar `QA_DIAGNOSTICS_KEY` en Render.
- Desplegar backend.
- Validar acceso con clave y error controlado sin clave.
- Usar capturas redactadas solo como anexo, manteniendo el foco de la tesis en la app.

## QA-MVP-CLOSURE-001 - Auditoria general de cierre de MVP

Fecha: 2026-07-18.

Estado: implementado como auditoria documental.

Origen documental:

- `AGENTS.md`.
- `docs/README.md`.
- `docs/FR5-INDEX.md`.
- `docs/FR6-INDEX.md`.
- `docs/FR7-INDEX.md`.
- `docs/CONTEXT-000-Master-Context.md`.
- `docs/CONTEXT-001-Documentation-Map.md`.
- `docs/CONTEXT-002-Codex-Reading-Protocol.md`.
- `docs/CONTEXT-003-Task-Context-Packs.md`.
- `docs/09-implementation/BACKLOG-001-Product-Backlog.md`.
- `tesis-final/evidence/apa-visual-evidence-guide.md`.
- `tesis-final/references/ucasal-model-guidance.md`.

Alcance:

- Se realizo una pasada general del estado de la app frente al cierre de MVP.
- Se distinguio funcionalidad defendible, evidencia pendiente y deuda no bloqueante.
- Se documento que la mejora de mapa/ubicacion debe tratarse como `ANDROID-LOCATION-TAG-001`: etiqueta visible de hogar, coordenadas ocultas y apertura de mapa como ayuda visual, no como selector obligatorio de punto exacto.
- Se preparo la recomendacion de congelar features salvo defectos que bloqueen la demo principal.

Artefacto generado:

- `docs/09-implementation/integration/QA-MVP-CLOSURE-001-General-App-Closure-Audit.md`.

Pendientes:

- Capturar evidencia v6 final.
- Ejecutar `QA-FORMS-001` manualmente.
- Validar `QA-SUPABASE-001` en Render si se quiere usar como anexo tecnico.

## THESIS-OUTPUT-004 - Borrador de cierre MVP y anexos v6

Fecha: 2026-07-18.

Estado: implementado como fuente Markdown editable.

Alcance:

- Se genero una nueva fuente de tesis enfocada en cierre de MVP, estructura aplicada, gestion de proyecto, factibilidad, recursos, cronograma, FODA, riesgos, resultados, conclusiones y anexos.
- Se creo una plantilla de anexos v6 sin imagenes incrustadas, con cuadros de dos renglones para pegar capturas en Word y notas compatibles con APA.
- Se actualizo el mapa de fuentes tecnicas con la referencia PMI/PMBOK para justificar secciones de gestion de proyecto sin afirmar una aplicacion formal completa de PMBOK.
- Se mantuvo separada la evidencia final de las fuentes editables: capturas en `tesis-final/evidence/screenshots/v6/`, tesis en `tesis-output/`, bibliografia en `docs/03-research` y `tesis-final/references`.

Artefactos generados o actualizados:

- `tesis-output/TenderApp-Tesis-Output-004-Cierre-MVP.md`.
- `tesis-final/evidence/screenshots/v6/ANEXO-CAPTURAS-V6-TEMPLATE.md`.
- `tesis-final/evidence/screenshots/v6/README.md`.
- `tesis-output/README.md`.
- `docs/03-research/literature/bibliography.bib`.
- `tesis-final/references/technical-source-map.md`.
- `tesis-final/references/bibliografia.md`.

Pendientes:

- No se incrustaron capturas por decision del autor.
- No se regenero DOCX en esta pasada; el Markdown queda como fuente estable para traslado manual o generacion posterior.
- Revisar visualmente cualquier DOCX final en Word, especialmente indices, tablas, figuras y notas.

## ANDROID-LOCATION-TAG-001 - Etiqueta visible de hogar y coordenadas ocultas

Fecha: 2026-07-18.

Estado: implementado; verificacion Android en curso.

Origen documental:

- `docs/09-implementation/integration/QA-MVP-CLOSURE-001-General-App-Closure-Audit.md`.
- `docs/09-implementation/android/ANDROID-SETTINGS-004-Household-Location-And-Drying-Defaults.md`.
- `docs/09-implementation/android/ANDROID-SETTINGS-005-Current-Location-Picker.md`.
- `docs/09-implementation/BACKLOG-001-Product-Backlog.md`.

Alcance:

- Settings ahora distingue entre etiqueta visible del hogar y punto tecnico de clima.
- `Vincular mi ubicacion actual` ya no pisa la etiqueta visible elegida por el usuario.
- La UI muestra si el punto climatico esta vinculado o si se usa el punto predeterminado.
- Las coordenadas siguen ocultas en la UI normal y se conservan solo para clima/prediccion.
- `Ver en mapa` queda como ayuda de visualizacion, no como paso principal de guardado.

Archivos modificados:

- `app/src/main/java/com/tesis_pro/tenderapp/ui/screens/SettingsViewModel.kt`.
- `app/src/main/java/com/tesis_pro/tenderapp/ui/screens/SettingsScreen.kt`.
- `app/src/main/res/values/strings.xml`.
- `app/src/main/res/values-es/strings.xml`.
- `app/src/test/java/com/tesis_pro/tenderapp/ui/screens/SettingsViewModelTest.kt`.
- `docs/09-implementation/android/ANDROID-LOCATION-TAG-001-Visible-Household-Tag.md`.

Pendiente:

- Ejecutar build/test Android.
- Capturar pantalla v6 de Settings con etiqueta visible y punto climatico vinculado.

## ANDROID-UI-ANIM-001 - Animaciones sutiles de estado del MVP

Fecha: 2026-07-18.

Estado: implementado; verificacion Android en curso.

Origen documental:

- `docs/06-design/UI-001-Design-System.md`.
- `docs/06-design/UI-003-Component-Catalog.md`.
- `docs/06-design/UI-004-Dashboard-Screen-Specification.md`.
- `docs/06-design/UI-005-New-Load-Flow-Specification.md`.
- `docs/09-implementation/BACKLOG-001-Product-Backlog.md`.
- `docs/08-agents/AG-000-Agent-Harness-Overview.md`.

Alcance:

- Se agregaron animaciones funcionales y sutiles para transiciones de estado en Dashboard, Nueva carga, Historial y Settings.
- Las barras de progreso compartidas ahora animan cambios de score o avance de carga.
- El dashboard anima cambios de veredicto climatico, alerta de lluvia, acciones disponibles y errores de actualizacion.
- Nueva carga anima el preview de decision, el score de seleccion y el feedback de resultado.
- Historial y Settings reducen saltos visuales cuando aparecen acciones, mensajes o cambios de contenido.

Restricciones aplicadas:

- No se agregaron dependencias nuevas.
- No se modifico la logica de prediccion, backend, Supabase ni autenticacion.
- No se cambio la navegacion global.

Pendiente:

- Ejecutar build Android.
- Validar visualmente en dispositivo o emulador antes de capturas finales v6.

Actualizacion posterior:

- Durante QA manual, el autor detecto que las animaciones rompian visualmente varias pantallas.
- Se revirtieron las animaciones de `ANDROID-UI-ANIM-001`.
- Se conserva el artefacto documental como intento auditado y decision de rollback.
- El fix funcional de Historial (`ANDROID-HISTORY-DISCARD-001`) se mantiene.

## ANDROID-HISTORY-DISCARD-001 - Feedback de descarte fallido en Historial

Fecha: 2026-07-18.

Estado: implementado y verificado.

Origen documental:

- `docs/09-implementation/BACKLOG-001-Product-Backlog.md`.
- `docs/09-implementation/android/ANDROID-DATA-LIFECYCLE-001A-Discard-Active-Laundry-Loads.md`.
- `docs/09-implementation/android/ANDROID-UI-005-History-and-Active-Load-State-UI.md`.
- `docs/11-quality/QA-001-Definition-of-Done.md`.

Problema observado:

- Al descartar una carga desde Historial, una falla de mutacion remota reemplazaba la lista por una pantalla de error.
- El descarte representa una transicion de estado a `CANCELLED`; no es una eliminacion fisica del registro.

Alcance:

- Historial conserva la lista visible mientras se ejecuta la mutacion.
- Si el backend no confirma el cambio, la pantalla mantiene la lista previa y muestra un error inline.
- Los botones de accion se deshabilitan durante la mutacion para evitar doble envio.
- Se agrego una interfaz minima `LaundryHistoryRepository` para testear el ViewModel sin depender del backend real.

Verificacion:

- `.\gradlew.bat --no-daemon :app:testDebugUnitTest --tests com.tesis_pro.tenderapp.ui.screens.HistoryViewModelTest`: OK.
- `.\gradlew.bat --no-daemon :app:assembleDebug`: OK.

Pendiente:

- Si Render vuelve a rechazar `PATCH /api/v1/laundry-loads/{id}/status` con `CANCELLED`, revisar logs del backend y restricciones reales de Supabase para esa fila.

## THESIS-REV-003 - Rescate selectivo de TenderApp App v4

Fecha: 2026-07-18.

Estado: implementado como revisión documental y DOCX candidato.

Origen documental:

- `docs/10-thesis/TH-010-TenderApp-Thesis-Revision-Baseline.md`.
- `tesis-final/TenderApp-Tesis-App-v4.docx`.
- `tesis-output/final/tesis-final.md`.
- `tesis-output/final/annexes-index.md`.
- `tesis-output/final/validation-report.md`.
- `tesis-output/final/generation-changelog.md`.
- `tesis-final/evidence/increment-log.md`.

Alcance:

- Se utilizó `TenderApp-Tesis-App-v4.docx` como fuente histórica de comparación y rescate selectivo.
- Se incorporaron ampliaciones compatibles con el tema vigente: metodología incremental, actividades recurrentes por incremento, fallback meteorológico, notificaciones, backend, Supabase, seguridad, relación entre pantallas/backend, validaciones y limitaciones.
- Se evitó reintroducir el enfoque anterior centrado en inteligencia artificial, sistema documental, agentes, prompts, context packs o `THESIS-009`.
- Se actualizó el índice de anexos para registrar la v4 como fuente histórica controlada, no como anexo completo.

Artefacto generado:

- `tesis-output/final/TenderApp-Tesis-Estandarizada-Anexos-Bibliografia-rev003.docx`.

Verificación:

- Auditoría DOCX: 15 `Heading 1`, 45 `Heading 2`, 14 `Heading 3`, 3 campos `TOC`, 25 campos `SEQ`, 1 campo `PAGE` y 9 imágenes en línea.
- Render visual con LibreOffice local: 50 páginas generadas.
- Búsqueda de términos prohibidos en el DOCX generado: cero apariciones de `inteligencia artificial`, `sistema documental`, `THESIS-009` y `Uso asistido`.

Pendientes:

- Revisión humana del contenido incorporado antes de promover `rev003` como versión principal.
- Actualizar campos en Word mediante `Ctrl+A` y `F9`.
- Revisar capturas finales v6 y redactar identificadores sensibles antes de entrega.
- Completar revisión APA de citas en cuerpo y referencias.

## THESIS-REV-004A - Citas APA y bibliografía autor-fecha

Fecha: 2026-07-18.

Estado: implementado como revisión documental y DOCX candidato.

Origen documental:

- `docs/10-thesis/TH-010-TenderApp-Thesis-Revision-Baseline.md`.
- `docs/03-research/RES-003-Reference-Management-BibTeX-and-APA.md`.
- `docs/03-research/literature/bibliography.bib`.
- `tesis-output/final/tesis-final.md`.
- `tesis-output/final/references.md`.
- `tesis-output/generar_tesis_docx_estandarizada.py`.

Alcance:

- Se aclaró el criterio bibliográfico: APA 7 utiliza citas autor-fecha en el cuerpo del texto, no numeración tipo `[1]`.
- Se incorporaron citas autor-fecha en el marco teórico y en las secciones técnicas principales.
- Se normalizaron autores corporativos con múltiples fuentes sin fecha mediante sufijos APA, por ejemplo `(s. f.-a)` y `(s. f.-b)`.
- Se actualizó el generador DOCX para conservar cursivas y negritas simples desde Markdown en la sección de referencias.
- Se sincronizó la referencia ISO 9001 con el archivo BibTeX canónico.

Artefacto generado:

- `tesis-output/final/TenderApp-Tesis-Estandarizada-Anexos-Bibliografia-rev005.docx`.

Verificación:

- Auditoría DOCX: 29 referencias, 29 corridas en cursiva y 29 entradas con sangría francesa.
- Auditoría de fuente Markdown: 24 marcas de cita autor-fecha detectadas en el cuerpo.
- Render visual con LibreOffice local: 50 páginas generadas.
- Revisión visual específica: páginas 37 y 38, correspondientes a la sección de referencias.

Pendientes:

- Actualizar campos automáticos en Word mediante `Ctrl+A` y `F9`.
- Revisar metadatos finales de cada referencia antes de la entrega académica.
- Confirmar si la institución acepta APA 7 autor-fecha o si solicita una variante numerada distinta.

## THESIS-DOCS-010-ALIGN - Alineación de docs/10-thesis con tesis vigente

Fecha: 2026-07-18.

Estado: implementado como actualización documental.

Origen documental:

- `docs/10-thesis/`.
- `docs/01-eds/EDS-000-Engineering-Documentation-Standard.md`.
- `docs/01-eds/EDS-003-Traceability-Model.md`.
- `docs/01-eds/EDS-004-Documentation-Lifecycle.md`.
- `docs/01-eds/EDS-005-Engineering-Principles.md`.
- `tesis-output/final/tesis-final.md`.
- `docs/10-thesis/TH-010-TenderApp-Thesis-Revision-Baseline.md`.

Problema:

- La carpeta `docs/10-thesis` conservaba una estructura inicial escrita cuando el EDS tenia mayor peso como eje academico.
- Esa estructura resultaba incompleta frente al DOCX vigente y podia reintroducir el enfoque anterior de sistema documental asistido por inteligencia artificial.

Alcance:

- Se creo un `README.md` especifico para `docs/10-thesis`.
- Se actualizaron `TH-000` a `TH-008` para centrar la tesis en TenderApp como aplicacion movil meteorologica.
- Se actualizo la relacion con `docs/01-eds`: EDS queda como antecedente historico y soporte metodologico, no como objeto principal de defensa.
- Se marcaron las reglas vigentes de APA autor-fecha, evidencias, figuras, tablas, Mermaid y DOCX.
- Se actualizaron los esqueletos de capitulos para introduccion, marco teorico, metodologia, desarrollo, resultados y conclusiones.
- Se marco `TH-009` como `Superseded` para conservarlo como apoyo historico selectivo sin usarlo como base canonica.
- Se actualizo `TH-010` para reflejar que el generador DOCX semantico y la correccion de citas ya fueron abordados.

Pendientes:

- Revision humana de los documentos actualizados antes de considerarlos `Approved`.
- Sincronizar futuras regeneraciones de DOCX contra `docs/10-thesis/README.md`, `TH-003`, `TH-006`, `TH-007` y `TH-010`.

## ANDROID-DASHBOARD-WASHER-LABEL-001 - Nombre visible de lavarropas en Dashboard

Fecha: 2026-07-18.

Estado: implementado y verificado por build local y QA visual en dispositivo.

Origen documental:

- `AGENTS.md`.
- `docs/FR5-INDEX.md`.
- `docs/FR6-INDEX.md`.
- `docs/CONTEXT-000-Master-Context.md`.
- `docs/CONTEXT-001-Documentation-Map.md`.
- `docs/CONTEXT-002-Codex-Reading-Protocol.md`.
- `docs/CONTEXT-003-Task-Context-Packs.md`.
- `docs/06-design/UI-004-Dashboard-Screen-Specification.md`.
- `docs/07-prompts/PROMPT-003-Compose-Screen-Generation.md`.
- `docs/11-quality/QA-001-Definition-of-Done.md`.

Problema:

- La tarjeta de carga activa del Dashboard podia mostrar el UUID del lavarropas asociado, lo que exponia un identificador tecnico y reducia la claridad de la experiencia de usuario.
- Algunos textos visibles en espanol conservaban errores de tildes, concordancia o puntuacion funcional.

Alcance:

- Se resolvio el nombre visible del lavarropas en `DashboardViewModel` a partir del catalogo backend de lavarropas.
- Si una carga tiene `washerId` pero no se puede resolver el nombre, la UI muestra un texto neutral (`Lavarropas vinculado`) en lugar del UUID.
- Se mantuvo el fallback `Sin lavarropas seleccionado` solo para cargas sin lavarropas asociado.
- Se corrigieron textos visibles en espanol para Dashboard, Nueva carga y ABM de lavarropas en Ajustes.
- Se agregaron pruebas unitarias para validar que el Dashboard resuelve nombres y no expone UUID cuando falta el nombre.

Verificacion:

- `./gradlew.bat :app:testDebugUnitTest --tests "com.tesis_pro.tenderapp.ui.screens.DashboardViewModelTest"` ejecutado correctamente.
- `./gradlew.bat :app:assembleDebug` ejecutado correctamente.

Pendientes:

- QA visual manual en dispositivo o emulador para confirmar que el Dashboard muestra el nombre real del lavarropas creado desde Ajustes.
- Capturar evidencia v6 si este ajuste queda incluido en el set final de pantallas.

## ANDROID-QA-POLISH-LOCATION-PREVIEW-001 - Pulido de Historial y Nueva carga

Fecha: 2026-07-18.

Estado: implementado y verificado por build local.

Origen documental:

- `AGENTS.md`.
- `docs/FR5-INDEX.md`.
- `docs/FR6-INDEX.md`.
- `docs/CONTEXT-000-Master-Context.md`.
- `docs/CONTEXT-001-Documentation-Map.md`.
- `docs/CONTEXT-002-Codex-Reading-Protocol.md`.
- `docs/CONTEXT-003-Task-Context-Packs.md`.
- `docs/06-design/UI-004-Dashboard-Screen-Specification.md`.
- `docs/11-quality/QA-001-Definition-of-Done.md`.

Problema:

- En Historial, las cargas mostraban `Ubicacion: home` porque el DTO remoto de cargas solo conserva `locationId` y el mapper Android lo usaba como etiqueta visible.
- En Nueva carga, la tarjeta de decision podia seguir mostrando `Consultando prediccion backend...` aunque ya existiera una vista previa accionable basada en Open-Meteo.

Alcance:

- `HistoryViewModel` traduce la ubicacion tecnica del backend a la etiqueta visible del hogar guardada localmente.
- Si la carga corresponde a `home`, Historial muestra la etiqueta configurada por el usuario en lugar del identificador tecnico.
- `NewLoadScreen` solo muestra el mensaje de consulta backend cuando todavia no hay una fuente util de decision.
- Se agrego una prueba unitaria para validar que Historial transforma `home` en una etiqueta visible del hogar.

Verificacion:

- `./gradlew.bat :app:testDebugUnitTest --tests "com.tesis_pro.tenderapp.ui.screens.HistoryViewModelTest" --tests "com.tesis_pro.tenderapp.ui.screens.DashboardViewModelTest"` ejecutado correctamente.
- `./gradlew.bat :app:assembleDebug` ejecutado correctamente.
- APK debug reinstalado en dispositivo Motorola Edge 30 Ultra mediante ADB.
- Capturas v6 regeneradas para `Nueva carga` e `Historial` en modo claro y oscuro.
- QA visual confirmo que `Historial` muestra `Mi ubicacion actual` en lugar de `home`.
- QA visual confirmo que `Nueva carga` no muestra el mensaje persistente `Consultando prediccion backend...` cuando ya hay decision basada en Open-Meteo.

Pendientes:

- Sin pendientes funcionales para esta microtarea.

## THESIS-REV-005 - Regeneración de tesis con capturas v6

Fecha: 2026-07-18.

Estado: generado y verificado visualmente por render PDF/PNG.

Origen documental:

- `AGENTS.md`.
- `docs/10-thesis/TH-010-TenderApp-Thesis-Revision-Baseline.md`.
- `docs/10-thesis/README.md`.
- `docs/03-research/RES-003-Reference-Management-BibTeX-and-APA.md`.
- `tesis-output/final/tesis-final.md`.
- `tesis-output/final/figures-index.md`.
- `tesis-final/evidence/screenshots/v6`.

Alcance:

- Se actualizó el generador DOCX para tomar capturas v6 seleccionadas desde `tesis-final/evidence/screenshots/v6`.
- Se reemplazaron referencias de figuras v5 por capturas v6 para Dashboard, Nueva carga, Historial, Ajustes y estados de carga en modo claro/oscuro.
- Se regeneró el documento `tesis-output/final/TenderApp-Tesis-Estandarizada-Anexos-Bibliografia-rev007.docx`.
- Se generó PDF de QA y render de 57 páginas en PNG para revisión visual.

Verificación:

- Generación DOCX ejecutada correctamente con `tesis-output/generar_tesis_docx_estandarizada.py`.
- Conversión a PDF ejecutada con LibreOffice local.
- Revisión visual por hojas de contacto generadas en `tesis-output/render-qa/rev007/contact-sheets`.
- Las capturas v6 incorporadas quedan dentro de margen, con título de figura y nota de elaboración propia.

Pendientes:

- Incorporar o descartar formalmente capturas finales de notificación push y Swagger/OpenAPI.
- Actualizar campos de índice en Word con `Ctrl+A` y `F9` antes de usar el documento como entrega final.
- Revisar manualmente datos sensibles visibles antes de exportar o enviar el documento final.

## ANDROID-SESSION-NAV-001 - Estabilización de sesión restaurada y navegación post-carga

Fecha: 2026-07-20.

Estado: implementado y verificado por pruebas unitarias focalizadas y build local.

Origen documental:

- `AGENTS.md`.
- `docs/README.md`.
- `docs/CONTEXT-000-Master-Context.md`.
- `docs/CONTEXT-001-Documentation-Map.md`.
- `docs/CONTEXT-002-Codex-Reading-Protocol.md`.
- `docs/CONTEXT-003-Task-Context-Packs.md`.
- `docs/FR5-INDEX.md`.
- `docs/FR6-INDEX.md`.
- `docs/FR7-INDEX.md`.
- `docs/06-design/UI-004-Dashboard-Screen-Specification.md`.
- `docs/06-design/UI-006-Weather-Verdict-Card.md`.
- `docs/11-quality/QA-001-Definition-of-Done.md`.

Problema:

- Al volver a abrir la app después de varios días, la sesión persistida podía mantener un access token vencido y provocar errores en endpoints protegidos hasta reloguear manualmente.
- Después de crear una nueva carga desde un estado previo sin cargas activas, Dashboard podía conservar un estado restaurado viejo o no refrescar de manera confiable.

Alcance:

- `SecureAuthSessionStore` ahora guarda vencimiento absoluto de sesión y descarta sesiones vencidas con una ventana de gracia.
- `SupabaseAuthRepository` y `SupabaseAuthCallbackParser` conservan `expires_at` cuando Supabase lo entrega.
- La navegación inferior usa Dashboard como raíz estable de pestañas.
- Nueva carga navega a Dashboard luego de una creación exitosa para forzar la lectura fresca de la carga activa.
- Se agregó `SessionExpiryPolicy` como lógica testeable de expiración.

Verificación:

- `./gradlew.bat :app:testDebugUnitTest --tests "com.tesis_pro.tenderapp.data.auth.SessionExpiryPolicyTest" --tests "com.tesis_pro.tenderapp.data.auth.SupabaseAuthRepositoryTest" --tests "com.tesis_pro.tenderapp.data.auth.SupabaseAuthCallbackParserTest" --tests "com.tesis_pro.tenderapp.ui.screens.AuthViewModelTest"` ejecutado correctamente.
- `./gradlew.bat :app:assembleDebug` ejecutado correctamente.

Pendientes:

- QA manual en dispositivo: reloguear, crear carga desde estado sin carga activa y confirmar que Dashboard muestra la nueva carga sin quedar en Ajustes.
- Definir si el siguiente incremento implementa refresh token automático o mantiene el relogueo explícito como comportamiento MVP.

## ANDROID-PRED-EMPTY-STATE-001 - Recomendación climática cuando no hay carga activa

Fecha: 2026-07-20.

Estado: implementado y verificado por pruebas unitarias y build local.

Origen documental:

- `AGENTS.md`.
- `docs/README.md`.
- `docs/CONTEXT-000-Master-Context.md`.
- `docs/CONTEXT-001-Documentation-Map.md`.
- `docs/CONTEXT-002-Codex-Reading-Protocol.md`.
- `docs/CONTEXT-003-Task-Context-Packs.md`.
- `docs/FR5-INDEX.md`.
- `docs/FR6-INDEX.md`.
- `docs/FR7-INDEX.md`.
- `docs/06-design/UI-004-Dashboard-Screen-Specification.md`.
- `docs/06-design/UI-006-Weather-Verdict-Card.md`.
- `docs/11-quality/QA-001-Definition-of-Done.md`.

Problema:

- Cuando no existía una carga activa, el Dashboard descartaba el clima obtenido por backend y mostraba solamente un estado vacío.
- Esto reducía el valor del Dashboard en el caso de uso real: antes de crear una carga, el usuario también necesita saber si las condiciones actuales son convenientes.

Alcance:

- `DashboardScreenState.Empty` ahora puede transportar una recomendación climática de referencia.
- `DashboardViewModel` construye esa recomendación con clima disponible, ubicación de secado por defecto y una carga mixta interna de referencia, sin crear ni persistir una carga real.
- `DashboardScreen` muestra el veredicto meteorológico, alerta de lluvia si corresponde, fuente de datos y una acción hacia Nueva carga.
- La navegación desde Dashboard hacia Nueva carga quedó conectada en el grafo principal.
- Se agregaron textos localizados en inglés y español.

Archivos modificados:

- `docs/09-implementation/BACKLOG-001-Product-Backlog.md`.
- `app/src/main/java/com/tesis_pro/tenderapp/ui/TenderApp.kt`.
- `app/src/main/java/com/tesis_pro/tenderapp/ui/screens/DashboardScreen.kt`.
- `app/src/main/java/com/tesis_pro/tenderapp/ui/screens/DashboardViewModel.kt`.
- `app/src/main/res/values/strings.xml`.
- `app/src/main/res/values-es/strings.xml`.
- `app/src/test/java/com/tesis_pro/tenderapp/ui/screens/DashboardViewModelTest.kt`.

Verificación:

- `$env:JAVA_HOME='E:\android\jbr'; .\gradlew.bat :app:testDebugUnitTest` ejecutado correctamente. Gradle informó una falla del daemon Kotlin y recompiló con estrategia fallback; el resultado final fue `BUILD SUCCESSFUL`.
- `$env:JAVA_HOME='E:\android\jbr'; .\gradlew.bat --no-daemon :app:assembleDebug` ejecutado correctamente.

Pendientes:

- QA manual en dispositivo: cerrar o completar cargas activas, entrar al Dashboard sin carga activa y confirmar que se muestra el panorama actual de secado con fuente `Open-Meteo`.
- Capturar evidencia v6 si esta pantalla queda incluida como estado final de demostración.

## ANDROID-WASHER-EDIT-001 - Edición de lavarropas desde Ajustes

Fecha: 2026-07-20.

Estado: implementado y verificado por pruebas unitarias focalizadas.

Origen documental:

- `AGENTS.md`.
- `docs/README.md`.
- `docs/CONTEXT-000-Master-Context.md`.
- `docs/CONTEXT-001-Documentation-Map.md`.
- `docs/CONTEXT-002-Codex-Reading-Protocol.md`.
- `docs/CONTEXT-003-Task-Context-Packs.md`.
- `docs/FR5-INDEX.md`.
- `docs/FR6-INDEX.md`.
- `docs/FR7-INDEX.md`.
- `docs/09-implementation/BACKLOG-001-Product-Backlog.md`.

Problema:

- El backend ya exponía `PUT /washers/{id}`, pero Android solo permitía listar, crear y retirar lavarropas desde Ajustes.
- Esto dejaba incompleto el ciclo ABM/CRUD visible para el usuario, especialmente después de agregar campos de eficiencia, litros de agua y centrifugado habitual.

Alcance:

- Se agregó `updateWasher` al contrato Android `TenderBackendApi`.
- `RemoteWasherDataSource` y `BackendWasherRepository` ahora soportan actualización de lavarropas.
- Settings permite seleccionar un lavarropas existente para editar, precargar el formulario, guardar cambios o cancelar edición.
- Se agregaron textos localizados para modo edición en inglés y español.
- Se mantuvo el retiro como soft-delete visual, sin eliminar historial.

Archivos modificados:

- `docs/09-implementation/BACKLOG-001-Product-Backlog.md`.
- `app/src/main/java/com/tesis_pro/tenderapp/data/remote/TenderBackendApi.kt`.
- `app/src/main/java/com/tesis_pro/tenderapp/data/remote/RemoteWasherDataSource.kt`.
- `app/src/main/java/com/tesis_pro/tenderapp/data/repository/BackendWasherRepository.kt`.
- `app/src/main/java/com/tesis_pro/tenderapp/ui/screens/SettingsViewModel.kt`.
- `app/src/main/java/com/tesis_pro/tenderapp/ui/screens/SettingsScreen.kt`.
- `app/src/main/res/values/strings.xml`.
- `app/src/main/res/values-es/strings.xml`.
- `app/src/test/java/com/tesis_pro/tenderapp/data/remote/RemoteWasherDataSourceTest.kt`.
- `app/src/test/java/com/tesis_pro/tenderapp/ui/screens/SettingsViewModelTest.kt`.
- Fakes Android que implementan `TenderBackendApi` en tests.

Verificación:

- `$env:JAVA_HOME='E:\android\jbr'; .\gradlew.bat --no-daemon :app:testDebugUnitTest --tests "com.tesis_pro.tenderapp.ui.screens.SettingsViewModelTest" --tests "com.tesis_pro.tenderapp.data.remote.RemoteWasherDataSourceTest"` ejecutado correctamente.
- `$env:JAVA_HOME='E:\android\jbr'; .\gradlew.bat --no-daemon :app:testDebugUnitTest` ejecutado correctamente.
- `$env:JAVA_HOME='E:\android\jbr'; .\gradlew.bat --no-daemon :app:assembleDebug` ejecutado correctamente.

Pendientes:

- QA manual en dispositivo: editar nombre, eficiencia, litros de agua y centrifugado de un lavarropas real; confirmar persistencia al salir y volver a Ajustes.
- Confirmar visualmente que el selector de método de secado y los controles de lavarropas se muestran localizados en español.

## ANDROID-WASHER-EDIT-001A - Ajuste de valores visibles en selectores de lavarropas

Fecha: 2026-07-20.

Estado: implementado y verificado.

Problema:

- En modo edición de lavarropas, algunos valores de desplegables no quedaban claramente visibles.
- Los lavarropas creados antes del incremento energético podían no tener `defaultSpinRpm`, por lo que el formulario mostraba un valor vacío en lugar del centrifugado habitual esperado.

Alcance:

- El formulario de edición usa `1200 rpm` como valor inicial cuando un lavarropas legado no trae centrifugado habitual.
- El selector compacto de Ajustes limita textos largos y mantiene visible el valor seleccionado.
- El DTO Android de lavarropas acepta respuestas camelCase del backend y variantes snake_case equivalentes.

Verificación:

- `$env:JAVA_HOME='E:\android\jbr'; .\gradlew.bat --no-daemon :app:testDebugUnitTest --tests "com.tesis_pro.tenderapp.ui.screens.SettingsViewModelTest" --tests "com.tesis_pro.tenderapp.data.remote.RemoteWasherDataSourceTest"` ejecutado correctamente.
- `$env:JAVA_HOME='E:\android\jbr'; .\gradlew.bat --no-daemon :app:assembleDebug` ejecutado correctamente.

Pendiente:

- QA manual en dispositivo: abrir Ajustes, editar un lavarropas y confirmar que los desplegables muestran tipo, etiqueta energética y `1200 rpm` o el valor guardado.

## ANDROID-WASHER-EDIT-001B - Integridad del formulario de lavarropas

Fecha: 2026-07-20.

Estado: implementado, pendiente de QA visual en dispositivo.

Problema:

- En el flujo de edición de lavarropas se detectaron valores visibles no localizados o poco claros en desplegables, por ejemplo tipo de lavarropas y centrifugado habitual.
- El formulario permitía dejar datos operativamente relevantes en estados débiles para el resto del MVP, especialmente capacidad, litros de agua, etiqueta energética y centrifugado habitual.
- Estos datos alimentan la creación de carga, la predicción energética aproximada y el resumen del Dashboard; por lo tanto, deben mantenerse controlados desde Ajustes.

Alcance:

- El selector de centrifugado habitual ya no ofrece la opción `Unknown spin` / `Sin dato de centrifugado`; usa `1200 rpm` como valor inicial seguro.
- El selector de etiqueta energética usa `A` como valor inicial cuando un lavarropas legado no trae dato.
- El backend normaliza alias técnicos, heredados o visibles del tipo de lavarropas, por ejemplo `front load`, `Carga frontal` y `Lavarropas secarropas`, antes de persistirlos como valores canónicos.
- Android normaliza respuestas legacy o localizadas de tipo de lavarropas hacia `WasherType` para que la UI pueda mostrarlas con `stringResource`.
- El formulario valida nombre obligatorio con máximo de 80 caracteres, capacidad doméstica entre 1 y 20 kg, y consumo de agua entre 20 y 200 L.
- Se agregaron mensajes de error localizados en español e inglés para los límites del formulario.
- Se mantuvo la validación backend existente como segunda barrera y se reforzó la validación Android para evitar envíos inválidos.

Verificación técnica:

- Se agregaron pruebas unitarias para evitar envío con nombre inválido y rangos irreales de capacidad/agua.
- `npm test -- --runInBand src/washers/washers.validation.spec.ts` ejecutado correctamente.
- `$env:JAVA_HOME='E:\android\jbr'; .\gradlew.bat --no-daemon :app:testDebugUnitTest --tests "com.tesis_pro.tenderapp.data.remote.RemoteWasherDataSourceTest"` ejecutado correctamente.
- `npm run build` en backend ejecutado correctamente.
- `$env:JAVA_HOME='E:\android\jbr'; .\gradlew.bat --no-daemon :app:assembleDebug` ejecutado correctamente.

Pendiente:

- QA manual: verificar en español que el tipo de lavarropas muestre `Carga frontal`, `Carga superior`, `Lavarropas secarropas` u `Otro`; verificar que el centrifugado muestre `1200 rpm` o el valor guardado.

Nota de ajuste posterior:

- Se corrigió la localización de opciones dentro de los desplegables de Ajustes. El valor seleccionado ya se veía localizado, pero al abrir el menú emergente algunas opciones podían mostrarse en inglés. `CompactSettingSelector` ahora materializa las etiquetas localizadas antes de renderizar `DropdownMenu`, siguiendo el patrón ya usado por Nueva carga.
- Verificación: se detuvo el daemon de Gradle antes de reintentar por una carrera de caché del compilador Kotlin; luego se ejecutaron correctamente `SettingsViewModelTest` focalizado y `:app:assembleDebug`.

## BACKEND-PRED-PREVIEW-001 - Corrección de preview de predicción sin carga persistida

Fecha: 2026-07-20.

Estado: implementado y verificado.

Problema:

- El preview de Nueva carga usa el identificador sintético `new-load-preview` para calcular una recomendación antes de persistir una carga.
- El backend intentaba enriquecer la predicción consultando `laundry_loads.id = new-load-preview`.
- Como `laundry_loads.id` es UUID en Supabase, esa consulta generaba una respuesta 400 en `/rest/v1/laundry_loads`.

Alcance:

- `PredictionEnergyMetadataDataSource` ahora detecta identificadores no UUID y devuelve metadata vacía sin consultar Supabase.
- La predicción preview conserva los datos explícitos enviados por Android, como centrifugado, tamaño de carga, etiqueta energética, capacidad y litros de agua.
- Se agregó una prueba unitaria que falla si el datasource intenta consultar Supabase con `new-load-preview`.

Verificación:

- `npm test -- --runInBand src/washers/washers.validation.spec.ts src/predictions/prediction-energy-metadata.data-source.spec.ts` ejecutado correctamente.
- `npm run build` en backend ejecutado correctamente.
- `$env:JAVA_HOME='E:\android\jbr'; .\gradlew.bat --no-daemon :app:testDebugUnitTest --tests "com.tesis_pro.tenderapp.data.remote.RemoteWasherDataSourceTest"` ejecutado correctamente.
- `$env:JAVA_HOME='E:\android\jbr'; .\gradlew.bat --no-daemon :app:assembleDebug` ejecutado correctamente.

Pendiente:

- Desplegar backend en Render y confirmar que desaparece el log 400 de Supabase asociado a `id=eq.new-load-preview`.

## BACKEND-PRED-TEMP-001 - Estimación segmentada con pronóstico horario

Fecha: 2026-07-25.

Estado: implementado y verificado localmente.

Contexto:

- La auditoría técnica detectó que `POST /api/v1/predictions/drying` calculaba la duración de secado con una fotografía puntual del clima actual.
- El backend ya tenía una frontera `WeatherProvider.getForecast` y el endpoint `GET /api/v1/weather/forecast`, pero la predicción principal usaba `getCurrentWeather`.
- Para el MVP no se justifica todavía una simulación persistente con humedad residual, versión de modelo y curva guardada. La mejora mínima defendible es usar la ventana horaria disponible para segmentar la estimación.

Alcance:

- `PredictionsService` ahora solicita `WeatherService.getForecast` para calcular predicción de secado.
- `DryingPredictionCalculator` acepta una ventana opcional de forecast y calcula el tiempo estimado como avance heurístico por intervalos.
- Si no hay forecast útil, se conserva el comportamiento puntual anterior.
- No se cambia el contrato público de `POST /api/v1/predictions/drying`.
- No se agrega migración ni persistencia de humedad residual.
- WorkManager queda separado como soporte Android futuro para reintentos y fallback local; no reemplaza FCM ni la responsabilidad backend de push.

Verificación:

- `npm.cmd test -- --runInBand src/predictions/drying-prediction-calculator.spec.ts src/predictions/predictions.service.spec.ts src/weather/open-meteo-weather.provider.spec.ts` ejecutado correctamente.
- `npm.cmd run build` en backend ejecutado correctamente.

Pendientes:

- Desplegar backend en Render y validar con Thunder/Swagger que `POST /api/v1/predictions/drying` responde correctamente.
- Comparar manualmente un caso estable contra un caso con pronóstico de empeoramiento para verificar que el tiempo estimado no se comporte como una foto estática.
- Definir `ANDROID-WORK-NOTIF-001` como incremento separado si se decide agregar WorkManager para reintento de registro FCM, sincronización de preferencias o notificación local de respaldo.

## ANDROID-WORK-NOTIF-001 - Reintento de registro push con WorkManager

Fecha: 2026-07-25.

Estado: implementado y pendiente de validacion en dispositivo.

Contexto:

- El circuito real de push ya estaba validado con Android, backend, Supabase, Firebase Cloud Messaging y Render.
- La brecha pendiente no era recibir push con WorkManager, sino robustecer el registro del dispositivo cuando fallan condiciones transitorias como red, backend dormido por cold start o refresh de token FCM.
- WorkManager se incorpora como soporte de resiliencia Android, sin reemplazar FCM ni mover al cliente responsabilidades de orquestacion backend.

Alcance:

- Se agrego WorkManager como dependencia Android.
- Se creo un worker de reintento que lee la sesion autenticada almacenada y vuelve a ejecutar el registro protegido del dispositivo.
- El trabajo requiere conectividad y usa backoff exponencial.
- El refresh de token FCM ahora encola reintento de registro.
- El registro post-login y el registro por sesion restaurada ahora encolan WorkManager si el intento directo falla.
- La notificacion local de respaldo queda diferida; el alcance aplicado fue el reintento de registro FCM.

Verificacion prevista:

- `$env:JAVA_HOME='E:\android\jbr'; .\gradlew.bat --no-daemon :app:testDebugUnitTest --tests "com.tesis_pro.tenderapp.data.notification.*"`
- `$env:JAVA_HOME='E:\android\jbr'; .\gradlew.bat --no-daemon :app:assembleDebug`

Pendientes:

- Validar en dispositivo que, tras login o refresh de token, se conserva el registro contra `/api/v1/notifications/register-device`.
- Forzar una falla transitoria del backend o red para observar el reintento posterior.
- Tratar `429` de Open-Meteo en backend con cache/throttle por ubicacion para evitar que Dashboard y predicciones generen rafagas innecesarias.

## BACKEND-WEATHER-CACHE-001 - Cache backend para Open-Meteo y proteccion contra 429

Fecha: 2026-07-25.

Estado: implementado localmente; pendiente de deploy y validacion en Render.

Contexto:

- En Render se observaron advertencias `Open-Meteo request failed with status 429` al consultar clima actual y forecast para `home`.
- El problema se produce en backend al llamar al proveedor meteorologico, no en Android ni en ADB.
- Despues de `BACKEND-PRED-TEMP-001`, la prediccion usa forecast horario, por lo que Dashboard, Nueva carga y predicciones pueden generar llamadas cercanas para la misma ubicacion.

Alcance:

- `WeatherService.getCurrentWeather` ahora reutiliza la primera entrada de la ventana de forecast.
- `WeatherService.getForecast` cachea en memoria la ventana por ubicacion resuelta.
- La clave de cache usa id de ubicacion y coordenadas redondeadas.
- Las respuestas reales se cachean por una ventana corta.
- Cuando el proveedor falla, el fallback stale tambien se cachea brevemente para evitar martillar Open-Meteo durante el mismo flujo de usuario.
- No se cambio el contrato REST consumido por Android.
- No se agrego migracion ni cache persistente.

Verificacion local:

- `npm.cmd test -- --runInBand src/weather/weather.service.spec.ts src/predictions/predictions.service.spec.ts src/weather/open-meteo-weather.provider.spec.ts`
- `npm.cmd run build`

Pendientes:

- Desplegar backend en Render.
- Validar con `/api/v1/weather/current?locationId=home`, `/api/v1/weather/forecast?locationId=home` y una prediccion desde Android o Swagger.
- Confirmar que no aparecen multiples warnings 429 para la misma ubicacion durante una interaccion corta.

Nota posterior de hardening:

- Al persistir el `429` en Render, se agrego un cooldown especial para rate limit.
- Si Open-Meteo devuelve `status 429`, el fallback stale queda cacheado por una ventana mayor que los errores transitorios genericos.
- Esta decision prioriza estabilidad del MVP y evita que Render siga insistiendo contra Open-Meteo mientras la IP o el proveedor estan rate-limited.
- Verificacion adicional: `npm.cmd test -- --runInBand src/weather/weather.service.spec.ts src/predictions/predictions.service.spec.ts` y `npm.cmd run build` ejecutados correctamente.

## ANDROID-WEATHER-SOURCE-002 - Trazabilidad visual de fallback climatico stale

Fecha: 2026-07-25.

Estado: implementado localmente; pendiente de validacion visual en dispositivo luego de instalar el APK actualizado.

Contexto:

- En Dashboard se observo el texto `Fuente: Mock backend` luego de fallas `429` de Open-Meteo.
- El backend ya informaba `isStale=true` cuando usaba un snapshot de respaldo, pero Android descartaba ese campo al mapear el DTO remoto.
- Esto podia confundir la lectura de QA: no era necesariamente un proveedor mock configurado, sino un respaldo backend por fallo o limitacion temporal del proveedor meteorologico.

Alcance:

- Se agrego `isStale` al modelo Android `WeatherSnapshot`.
- El mapper remoto conserva el campo `isStale` recibido desde backend.
- Dashboard marca `MOCK + isStale=true` como `Backend fallback`.
- La UI localiza los labels de fuente en ingles/espanol para Open-Meteo, mock backend, respaldo backend y respaldo local.
- No se modifico el contrato REST ni la heuristica de prediccion.

Verificacion prevista:

- `$env:JAVA_HOME='E:\android\jbr'; .\gradlew.bat --no-daemon :app:testDebugUnitTest --tests "com.tesis_pro.tenderapp.ui.screens.DashboardViewModelTest" --tests "com.tesis_pro.tenderapp.data.remote.RemoteWeatherDataSourceTest"`
- `$env:JAVA_HOME='E:\android\jbr'; .\gradlew.bat --no-daemon :app:assembleDebug`

Pendientes:

- Instalar el build en el dispositivo y verificar que, ante respuesta backend stale, Dashboard muestre `Fuente: Respaldo del backend` en espanol.

Validacion manual posterior:

- El dispositivo mostro `Fuente: Respaldo del backend`, confirmando que Android ya distingue un fallback stale del backend de un proveedor mock configurado.

## BACKEND-WEATHER-PROVIDER-002 - Proveedor meteorologico secundario con MET Norway

Fecha: 2026-07-25.

Estado: implementado localmente; pendiente de deploy y validacion en Render.

Contexto:

- Open-Meteo respondio correctamente desde el entorno local, pero Render siguio registrando `Open-Meteo request failed with status 429`.
- Esto sugiere una limitacion/rate limit asociada al entorno de salida de Render o a la infraestructura compartida, no necesariamente una caida global de Open-Meteo.
- Para validar que TenderApp sigue funcionando con datos meteorologicos reales aunque falle el proveedor primario, se agrego MET Norway como proveedor secundario.

Alcance:

- Se agrego `MetNoWeatherProvider` usando `locationforecast/2.0/compact`.
- Se agrego `FailoverWeatherProvider`: con `WEATHER_PROVIDER=open-meteo`, el backend intenta Open-Meteo primero y MET Norway si el primario falla.
- Se mantiene el fallback stale/mock final solo si ambos proveedores reales fallan.
- Se agrego `source='MET_NO'` al contrato backend y al modelo Android.
- Dashboard puede mostrar `Fuente: MET Norway` cuando el proveedor secundario responde.
- Se agregaron variables de entorno documentadas: `MET_NO_BASE_URL` y `MET_NO_USER_AGENT`.
- No se agregaron API keys ni nuevos secretos.

Fuentes tecnicas:

- MET Norway Locationforecast API: `https://api.met.no/weatherapi/locationforecast/2.0/documentation`.
- MET Norway API Terms/FAQ: requiere `User-Agent` identificable para evitar bloqueos o throttling.

Verificacion prevista:

- `npm.cmd test -- --runInBand src/weather/met-no-weather.provider.spec.ts src/weather/failover-weather.provider.spec.ts src/weather/weather.service.spec.ts src/predictions/predictions.service.spec.ts`
- `npm.cmd run build`
- `$env:JAVA_HOME='E:\android\jbr'; .\gradlew.bat --no-daemon :app:testDebugUnitTest --tests "com.tesis_pro.tenderapp.ui.screens.DashboardViewModelTest" --tests "com.tesis_pro.tenderapp.data.remote.RemoteWeatherDataSourceTest"`
- `$env:JAVA_HOME='E:\android\jbr'; .\gradlew.bat --no-daemon :app:assembleDebug`

Pendientes:

- Agregar `MET_NO_USER_AGENT` en Render con un valor identificable del proyecto.
- Desplegar backend.
- Validar que, cuando Open-Meteo devuelva 429, Render registre el intento de MET Norway y Dashboard muestre `Fuente: MET Norway` si el proveedor secundario responde.

## QA-WEATHER-PROVIDER-001 - Diagnostico protegido de proveedor meteorologico efectivo

Fecha: 2026-07-25.

Estado: implementado localmente; pendiente de deploy y validacion en Render.

Contexto:

- La app ya puede mostrar `Open-Meteo`, `MET Norway`, `Respaldo del backend` o `Respaldo local`.
- Para QA y evidencia conviene poder verificar desde Swagger/Thunder cual fue la fuente efectiva sin exponer secretos ni consultar logs internos.
- El modulo QA ya usaba `x-qa-key` para diagnosticos protegidos de Supabase.

Alcance:

- Se agrego `GET /api/v1/qa/weather/provider-status?locationId=home`.
- El endpoint esta protegido por `x-qa-key`.
- La respuesta informa `status`, `source`, `isStale`, metricas climaticas basicas y mensaje de diagnostico.
- Si `source='MET_NO'` y `isStale=false`, el proveedor secundario real respondio correctamente.
- Si `source='MOCK'` o `isStale=true`, el backend esta usando fallback.
- No se exponen tokens, credenciales, claves ni datos personales.

Verificacion prevista:

- `npm.cmd test -- --runInBand src/qa/qa-weather.service.spec.ts src/qa/qa-key.guard.spec.ts src/weather/failover-weather.provider.spec.ts src/weather/met-no-weather.provider.spec.ts`
- `npm.cmd run build`

Uso manual esperado:

- URL: `https://tesis-t85s.onrender.com/api/v1/qa/weather/provider-status?locationId=home`
- Header: `x-qa-key: <valor configurado en Render>`
- Evidencia esperada para proveedor secundario: `status='ok'`, `source='MET_NO'`, `isStale=false`.

## BACKEND-LAUNDRY-LOADSIZE-001 - Normalizacion de `load_size` legado en cargas

Fecha: 2026-07-25.

Estado: implementado localmente; migracion pendiente de aplicar en Supabase.

Contexto:

- Supabase rechazo un `PATCH /rest/v1/laundry_loads` con `400` por violacion de `laundry_loads_load_size_check`.
- El backend estaba actualizando solo `status`, `started_at` y `completed_at`.
- PostgreSQL valida toda la fila durante un `UPDATE`; por eso una carga antigua con `load_size='small'`, `medium`, `large` u otro valor no canonico puede bloquear un cambio de estado aunque el PATCH no modifique `load_size`.

Alcance:

- Se agrego `supabase/migrations/008_energy_prediction_value_normalization.sql`.
- La migracion normaliza `load_size`, `estimated_washing_cost_level`, `estimated_washing_cost_confidence` y `estimated_washing_cost_currency`.
- Valores canonicos en minuscula o con espacios pasan a mayuscula.
- Valores no reconocidos se limpian a `null` para respetar los contratos nullable del MVP.
- El backend ahora lee `load_size` antes de actualizar estado y lo auto-normaliza en el mismo PATCH si detecta un valor legado.

Verificacion prevista:

- `npm.cmd test -- --runInBand src/laundry-loads/laundry-loads.supabase-data-source.spec.ts src/laundry-loads/laundry-loads.service.spec.ts`
- `npm.cmd run build`

Pendientes:

- Aplicar la migracion `008_energy_prediction_value_normalization.sql` en Supabase.
- Reintentar el cambio de estado de la carga afectada.
- Confirmar que el log de Supabase ya no muestra `laundry_loads_load_size_check`.

## DASHBOARD-DRYING-PROGRESS-001 - Progreso temporal de secado en Dashboard

Fecha: 2026-07-25.

Estado: implementado localmente; pendiente de aplicar migracion y desplegar backend.

Contexto:

- Durante QA manual se observo que una carga recien colgada podia mostrarse con progreso cercano a 90%.
- El valor provenia de `suitabilityScore`, que expresa conveniencia climatica, no avance fisico de secado.
- En un escenario real, si son las 17:30 y el sol favorece el secado, la estimacion puede ser buena, pero el progreso de la carga debe empezar cerca de 0% y avanzar segun el tiempo transcurrido desde que se colgo la ropa.

Alcance:

- Se agrego `drying_started_at` a la migracion Supabase `009_laundry_drying_progress_timestamp.sql`.
- El backend expone `dryingStartedAt` en el contrato de `LaundryLoadResponseDto`.
- El backend setea `dryingStartedAt` cuando una carga pasa a estado `DRYING`.
- Android conserva `dryingStartedAt` en el modelo remoto y de dominio.
- Dashboard calcula el progreso de estado usando tiempo transcurrido sobre `estimatedDryingMinutes`.
- El `suitabilityScore` queda reservado para el indicador de conveniencia climatica.
- Los recordatorios de retiro de ropa prefieren `dryingStartedAt` cuando esta disponible.

Verificacion prevista:

- `npm.cmd test -- --runInBand src/laundry-loads/laundry-loads.supabase-data-source.spec.ts src/laundry-loads/laundry-loads.service.spec.ts src/laundry-loads/laundry-loads.controller.spec.ts`
- `npm.cmd run build`
- `$env:JAVA_HOME='E:\android\jbr'; .\gradlew.bat --no-daemon :app:testDebugUnitTest --tests "com.tesis_pro.tenderapp.ui.screens.DashboardViewModelTest" --tests "com.tesis_pro.tenderapp.data.remote.RemoteLaundryDataSourceTest" --tests "com.tesis_pro.tenderapp.domain.notification.SchedulePickupReminderUseCaseTest"`
- `$env:JAVA_HOME='E:\android\jbr'; .\gradlew.bat --no-daemon :app:assembleDebug`

Pendientes:

- Aplicar `supabase/migrations/009_laundry_drying_progress_timestamp.sql`.
- Desplegar backend en Render.
- Validar en dispositivo que al pasar una carga a `DRYING` el Dashboard inicia progreso bajo y luego avanza con el tiempo.

## BACKEND-DRYING-SNAPSHOT-001 - Snapshot de prediccion al iniciar secado

Fecha: 2026-07-25.

Estado: implementado localmente; migracion pendiente de aplicar en Supabase.

Contexto:

- Luego de separar `suitabilityScore` de progreso temporal, quedaba una brecha: si el backend recalculaba `estimatedDryingMinutes` con clima nuevo, el avance visual podia variar aunque la carga ya estuviera tendida.
- Para el MVP, el comportamiento mas comprensible es congelar la estimacion tomada al pasar a `DRYING` y tratar cambios climaticos posteriores como alertas o recomendaciones, no como retroceso de progreso.

Alcance:

- Se agrego `supabase/migrations/010_laundry_drying_prediction_snapshot.sql`.
- La tabla `laundry_loads` incorpora `drying_estimated_minutes_at_start` y `drying_estimated_pickup_at`.
- El backend calcula una prediccion al pasar una carga a `DRYING` y persiste esos campos junto con `drying_started_at`.
- Si la prediccion falla, la transicion de estado no se bloquea; queda sin snapshot y el problema se registra en logs.
- Android consume los campos `dryingEstimatedMinutesAtStart` y `dryingEstimatedPickupAt`.
- Dashboard prefiere la estimacion congelada para progreso y estimacion visible cuando la carga esta en `DRYING`.
- La notificacion pendiente `DRYING_COMPLETE` usa `scheduled_for` cuando existe `dryingEstimatedPickupAt`.

Verificacion prevista:

- `npm.cmd test -- --runInBand src/laundry-loads/laundry-loads.supabase-data-source.spec.ts src/laundry-loads/laundry-loads.service.spec.ts src/laundry-loads/laundry-loads.controller.spec.ts`
- `npm.cmd run build`
- `$env:JAVA_HOME='E:\android\jbr'; .\gradlew.bat --no-daemon :app:testDebugUnitTest --tests "com.tesis_pro.tenderapp.ui.screens.DashboardViewModelTest" --tests "com.tesis_pro.tenderapp.data.remote.RemoteLaundryDataSourceTest"`
- `$env:JAVA_HOME='E:\android\jbr'; .\gradlew.bat --no-daemon :app:assembleDebug`

Pendientes:

- Aplicar `supabase/migrations/010_laundry_drying_prediction_snapshot.sql`.
- Desplegar backend en Render.
- Validar que al pasar una carga a `DRYING`, Supabase guarde `drying_estimated_minutes_at_start`, `drying_estimated_pickup_at` y que `notification_events.scheduled_for` quede poblado.

## BACKEND-NOTIF-DUE-001 - Despacho de notificaciones pendientes vencidas

Fecha: 2026-07-25.

Estado: implementado localmente; pendiente de deploy y smoke real.

Contexto:

- El incremento anterior logro que `notification_events.scheduled_for` quede poblado cuando una carga pasa a `DRYING`.
- Para cerrar el circuito automatico del MVP, esos eventos pendientes deben poder despacharse sin interaccion del usuario.
- El alcance elegido es un endpoint protegido por `x-qa-key`, apto para ejecucion manual desde Thunder/Swagger o para configuracion posterior con Render Cron.

Alcance:

- Se agrego `POST /api/v1/notifications/dispatch-due`.
- El endpoint usa `x-qa-key` y no depende del bearer token Android.
- Busca eventos `PENDING` con `scheduled_for <= now`.
- Envia FCM al ultimo dispositivo Android activo del usuario.
- Actualiza el evento como `SENT`, `SKIPPED` o `FAILED`.
- Devuelve conteos `processed`, `sent`, `skipped` y `failed` para QA.
- No expone claves Firebase, Supabase ni datos sensibles.

Verificacion ejecutada:

- `npm.cmd test -- --runInBand src/notifications/notifications.service.spec.ts src/notifications/notifications.supabase-data-source.spec.ts src/qa/qa-key.guard.spec.ts`

Pendientes:

- Ejecutar `npm.cmd run build`.
- Desplegar backend en Render.
- Probar desde Thunder/Swagger con `x-qa-key`.
- Configurar Render Cron para invocar periodicamente el endpoint si se decide automatizar sin intervencion manual.

## BACKEND-NOTIF-DUE-002 - Runbook de smoke y Render Cron para notificaciones vencidas

Fecha: 2026-07-25.

Estado: implementado como documentacion operativa.

Contexto:

- El primer smoke manual de `POST /api/v1/notifications/dispatch-due` devolvio:

```json
{
  "processed": 0,
  "sent": 0,
  "skipped": 0,
  "failed": 0
}
```

- Ese resultado es correcto cuando no existen eventos `PENDING` con `scheduled_for <= now`.
- Para evitar interpretaciones ambiguas durante QA y captura de evidencia, se agrego un runbook especifico.

Alcance:

- Se creo `docs/09-implementation/backend/BACKEND-NOTIF-DUE-002-Render-Cron-And-Smoke-Runbook.md`.
- El documento explica precondiciones, smoke antes de `scheduled_for`, smoke despues de `scheduled_for`, resultados validos y evidencias esperadas.
- Se documento una configuracion inicial para Render Cron con `curl`, `x-qa-key` y frecuencia de cinco minutos.
- Se incorporaron fuentes oficiales de Render para Cron Jobs y variables de entorno.

Pendientes:

- Ejecutar un smoke posterior a `scheduled_for` y registrar si el resultado es `SENT`, `SKIPPED` o `FAILED`.
- Si se decide automatizar, crear el Cron Job en Render o registrarlo como configuracion versionada posterior.

## BACKEND-NOTIF-CRON-001 - Scripts versionados para Render Cron

Fecha: 2026-07-25.

Estado: implementado localmente; pendiente de crear los Cron Jobs en Render.

Contexto:

- El endpoint `POST /api/v1/notifications/dispatch-due` ya permite despachar notificaciones pendientes vencidas.
- Para que el flujo sea automatico y no dependa de Thunder/Swagger, Render debe ejecutar una tarea periodica.
- Tambien se evaluo el cold start de Render: el cron funcional de despacho cada cinco minutos puede actuar como keep-warm practico durante la demo.

Alcance:

- Se agrego `backend/scripts/dispatch-due-notifications.js`.
- Se agrego `backend/scripts/keep-warm.js`.
- Se agregaron los scripts npm `cron:dispatch-due` y `cron:keep-warm`.
- Se agregaron variables operativas en `backend/.env.example`: `BACKEND_PUBLIC_URL`, `CRON_DISPATCH_LIMIT` y `KEEP_WARM_PATH`.
- Se creo `docs/09-implementation/backend/BACKEND-NOTIF-CRON-001-Render-Cron-Command-Scripts.md`.
- Se actualizo el runbook `BACKEND-NOTIF-DUE-002` para referenciar los comandos versionados.

Decision operativa:

- El cron principal recomendado es `npm run cron:dispatch-due` cada cinco minutos.
- Ese mismo cron despierta o mantiene activo el backend en la practica porque invoca la API publica.
- Un segundo cron `npm run cron:keep-warm` queda como opcion de demo si se quiere reducir latencia de navegacion, pero no es garantia formal de disponibilidad.
- WorkManager se mantiene como responsabilidad Android para reintentos de registro FCM; el despacho vencido sigue siendo responsabilidad del backend.

Pendientes:

- Crear el Cron Job funcional en Render con root directory `backend`, build command `npm ci --include=dev`, command `npm run cron:dispatch-due` y schedule `*/5 * * * *`.
- Configurar en ese Cron Job `BACKEND_PUBLIC_URL`, `QA_DIAGNOSTICS_KEY` y `CRON_DISPATCH_LIMIT`.
- Ejecutar un smoke luego de `scheduled_for <= now` y registrar evidencia de `SENT`, `SKIPPED` o `FAILED`.

## BACKEND-NOTIF-CRON-002 - Scheduler HTTP gratuito para notificaciones vencidas

Fecha: 2026-07-25.

Estado: configurado en `cron-job.org`; pendiente de smoke con evento vencido.

Contexto:

- Render Cron puede requerir billing, por lo que se decidio usar un servicio HTTP externo gratuito para el entorno MVP.
- La decision no cambia la arquitectura: el scheduler externo no accede a Supabase ni Firebase; solo invoca el endpoint protegido del backend.

Alcance:

- Se creo `docs/09-implementation/backend/BACKEND-NOTIF-CRON-002-Free-Http-Scheduler.md`.
- Se eligio `cron-job.org` como opcion recomendada para el MVP porque soporta llamadas HTTP programadas, metodo POST, headers, body y ejecuciones de prueba.
- Se documento que UptimeRobot no es la primera opcion para este endpoint si los headers personalizados quedan asociados a planes pagos.
- Se especifico la configuracion del job: URL, metodo `POST`, header `x-qa-key`, body `{"limit":10}` y frecuencia cada cinco minutos.
- Se dejo asentado que el mismo job tambien cumple funcion practica de keep-warm del backend Render.

Pendientes:

- Capturar historial de ejecucion del job en `cron-job.org`.
- Ejecutar o esperar una corrida posterior a `scheduled_for <= now`.
- Validar que la respuesta sea `SENT`, `SKIPPED` o `FAILED` segun el estado real del dispositivo y dejar evidencia.
- Validar una notificacion Android posterior a `scheduled_for <= now` si existe dispositivo activo.

Actualizacion posterior:

- El job gratuito fue configurado en `cron-job.org`.
- Header correcto usado por el scheduler: `x-qa-key`.
- Se mantiene pendiente la evidencia del despacho real contra una fila `notification_events` vencida.

## QA-NOTIF-CRON-001 - Validacion diferida de notificaciones automaticas

Fecha: 2026-07-25.

Estado: en progreso; esperando corrida posterior a `scheduled_for <= now`.

Contexto:

- El autor confirmo una nueva validacion manual amplia de la app: login, flujo funcional, dashboard, cargas, historial, settings y comportamiento general se observan correctos.
- El ultimo punto pendiente para cerrar el circuito automatico es comprobar que `cron-job.org` invoque el backend luego de la hora programada y que el evento de notificacion quede auditado.

Alcance:

- Se creo `docs/09-implementation/integration/QA-NOTIF-CRON-001-Delayed-Automatic-Notification-Smoke.md`.
- Se actualizo `QA-INCREMENT-004B` para separar el flujo navegable de la app, ya validado manualmente, de la notificacion automatica diferida.
- Se actualizo `BACKLOG-001` con el item `QA-NOTIF-CRON-001`.

Pendientes:

- Esperar una corrida de `cron-job.org` posterior a `scheduled_for <= now`.
- Capturar historial del scheduler con respuesta.
- Capturar o transcribir redaccionada la fila `notification_events`.
- Si el resultado es `SENT`, capturar la notificacion Android.
- Si el resultado es `SKIPPED` o `FAILED`, registrar el motivo sin exponer secretos.

## ANDROID-DASH-STATUS-UX-001 - Claridad del estado de secado en Dashboard

Fecha: 2026-07-25.

Estado: implementado y compilado.

Contexto:

- Durante la validacion manual, el Dashboard mostraba una carga en `Secando` con progreso aproximado y el texto `Mejor ventana: ahora - 2 h 21 min`.
- Aunque el calculo principal funcionaba, esa frase podia interpretarse como rango horario o ventana climatica, no como duracion/restante de secado.
- Se revisaron las capturas de prototipo en `docs/06-design/prototypes/WhatsApp Image 2026-07-07 at 08.57.50.jpeg` y `WhatsApp Image 2026-07-07 at 08.58.09.jpeg`, que separan claramente estado actual, progreso y momentos de tender/recoger.

Alcance:

- Se actualizo `DashboardScreen` para etiquetar la barra como `avance estimado de secado`.
- Se agrego una nota visible cuando la carga esta en `DRYING`: el avance se calcula desde que la carga fue marcada como secando.
- Se reemplazo la tarjeta ambigua de `mejor ventana` en estado actual por dos metricas: estimacion total y proxima revision/restante.
- Se mantuvo visible la fuente de datos debajo de la recomendacion de estado.
- Se agregaron strings localizados en español e ingles.

Verificacion ejecutada:

- `$env:JAVA_HOME='E:\android\jbr'; .\gradlew.bat --no-daemon :app:compileDebugKotlin`

Pendientes:

- Validar visualmente en dispositivo que la tarjeta no se vea sobrecargada en modo claro y oscuro.
- Capturar el estado `Secando` para v6 si se decide usarlo como evidencia final.

## ANDROID-DASH-STATUS-UX-002 - Contador vivo y horarios de tendido/revision

Fecha: 2026-07-25.

Estado: implementado y compilado.

Contexto:

- Durante la validacion manual se observo que el tiempo restante de secado podia quedar visualmente ambiguo si se leia como una ventana fija.
- El prototipo de referencia mostraba dos datos mas comprensibles para el usuario: hora de tendido y hora aproximada de retiro/revision.
- El backend y Supabase ya persistian `dryingStartedAt`, `dryingEstimatedMinutesAtStart` y `dryingEstimatedPickupAt`, por lo que el ajuste correspondia principalmente a la capa Android.

Alcance:

- Se extendio `CurrentStatusUi` para recibir la hora real de inicio de secado y la hora estimada de revision/retiro.
- Se actualizo `DashboardViewModel` para mapear esos campos desde la carga activa.
- Se modifico `DashboardScreen` para recalcular el avance y el tiempo restante cada minuto mientras la carga esta en `DRYING`.
- Se reemplazaron las metricas de estado activo por `Tendida a las` y `Revisar aprox.` cuando existe snapshot de secado.
- Se agregaron strings localizados en espanol e ingles.

Verificacion ejecutada:

- `$env:JAVA_HOME='E:\android\jbr'; .\gradlew.bat --no-daemon :app:compileDebugKotlin`

Observacion sobre cron-job:

- Los logs pegados de Supabase (`checkpoint starting`, `checkpoint complete` y consultas `count_estimate` sobre `notification_events`) corresponden a actividad normal de Postgres y del dashboard de Supabase.
- No constituyen, por si solos, evidencia de error del scheduler externo.
- La validacion del cronjob debe tomarse desde los eventos de cron-job.org, el codigo HTTP devuelto por `POST /api/v1/notifications/dispatch-due` y el impacto en `notification_events`.

Pendientes:

- Validar en dispositivo que el porcentaje y el tiempo restante bajen en la pantalla sin refresco manual.
- Capturar evidencia v6 del estado `Secando` con horarios de tendido/revision visibles.
- Completar `QA-NOTIF-CRON-001` cuando exista un evento pendiente vencido y el scheduler lo procese.

## BACKEND-PRED-WINDOW-001 y ANDROID-DASH-HOURLY-001 - Ventana horaria explicable

Fecha: 2026-07-25.

Estado: implementado y compilado.

Contexto:

- La validacion manual mostro que el Dashboard ya podia explicar una carga activa y su avance, pero faltaba una lectura mas parecida al prototipo: ver proximas horas, ventana de tendido y conveniencia relativa.
- El backend ya consultaba un forecast y el calculo de secado ya podia usar varios snapshots horarios, pero esa informacion quedaba oculta para Android.
- Para el MVP y la tesis conviene que el usuario vea por que se recomienda tender en cierto horario, sin interpretar manualmente temperatura, humedad, viento o lluvia.

Alcance backend:

- Se extendio `DryingPredictionResponseDto` con `recommendedHangWindowStart`, `recommendedHangWindowEnd` y `hourlySlots`.
- Se agrego un resultado horario por slot con hora, veredicto, score, temperatura, humedad, viento y probabilidad de lluvia.
- La ventana recomendada toma el primer tramo `GOOD`; si no existe, usa el mejor tramo disponible por score.
- Se mantuvo la heuristica base: no se incorporo machine learning ni un nuevo proveedor climatico.

Alcance Android:

- Se extendio el DTO remoto y el modelo `DryingPrediction` para recibir ventana recomendada y slots horarios.
- Se agrego al Dashboard una tarjeta `Ventana recomendada para tender`.
- Se agrego un carrusel horizontal de proximas horas con hora, veredicto, temperatura, lluvia y score.
- La visualizacion usa los colores semanticos existentes para condiciones buenas, cautelosas o malas.

Verificacion ejecutada:

- `cd backend; npm run build`
- `$env:JAVA_HOME='E:\android\jbr'; .\gradlew.bat --no-daemon :app:compileDebugKotlin`

Observaciones:

- La primera verificacion Android emitio un problema de cache incremental de Kotlin, pero el proceso termino sin error de build. Se recomienda repetir en modo limpio/in-process si vuelve a aparecer.
- La notificacion automatica de inicio de ventana queda separada como `BACKEND-NOTIF-HANG-WINDOW-001`, porque requiere crear evento `IDEAL_HANGING_TIME`, validarlo con cron-job.org y registrar evidencia de `notification_events`.

Pendientes:

- Validar visualmente en dispositivo el carrusel horario en modo claro y oscuro.
- Confirmar en Swagger que `POST /api/v1/predictions/drying` expone los nuevos campos.
- Implementar y validar `BACKEND-NOTIF-HANG-WINDOW-001`.

## BACKEND-NOTIF-HANG-WINDOW-001 - Notificacion de inicio de ventana recomendada

Fecha: 2026-07-25.

Estado: implementado y probado en backend; QA externo pendiente.

Contexto:

- El Dashboard ahora expone una ventana recomendada para tender basada en `recommendedHangWindowStart` y `recommendedHangWindowEnd`.
- Para completar el valor de automatizacion, esa ventana debe poder convertirse en un evento push programado.
- La infraestructura previa ya tenia `notification_events`, categoria `IDEAL_HANGING_TIME`, endpoint `dispatch-due` y cron-job.org configurado.

Alcance:

- Se extendio `NotificationsService.scheduleIdealHangingTime` para aceptar `scheduledFor` y `windowEnd`.
- Al crear una carga, `LaundryLoadsService` calcula la prediccion y agenda `IDEAL_HANGING_TIME` con `scheduled_for = recommendedHangWindowStart`.
- El payload del evento conserva `laundryLoadId`, `recommendedHangWindowStart` y `recommendedHangWindowEnd`.
- Si la prediccion falla, la creacion de la carga no se bloquea; queda registrado un warning operativo.
- Se actualizaron tests unitarios de `LaundryLoadsService` y `NotificationsService`.

Verificacion ejecutada:

- `cd backend; npm test -- laundry-loads.service.spec.ts notifications.service.spec.ts`
- `cd backend; npm run build`

Pendientes:

- Crear una carga cuyo `recommendedHangWindowStart` quede en el futuro cercano.
- Verificar en Supabase que `notification_events.category = IDEAL_HANGING_TIME` tenga `scheduled_for` no nulo.
- Esperar ejecucion de cron-job.org y validar que el evento pase a `SENT`, `SKIPPED` o `FAILED` con causa registrada.
- Capturar evidencia v6 si Android recibe el push localizado.

## ANDROID-DASH-ENERGY-CARD-001 - Card independiente de consumo estimado

Fecha: 2026-07-25.

Estado: implementado y compilado.

Contexto:

- Tras incorporar la ventana horaria recomendada, el resumen de electricidad/agua podia quedar visualmente enterrado dentro de la tarjeta de carga activa.
- La estimacion energetica forma parte del alcance ampliado del MVP y conviene que sea visible para capturas de evidencia y defensa funcional.
- No se elimino el estimador; se reubico su presentacion.

Alcance:

- Se extrajo `EnergyCostSummary` de `ActiveLoadCard`.
- Se agrego `EnergyEstimateCard` como tarjeta independiente del Dashboard.
- La tarjeta se muestra solo cuando `estimatedCost` no es nulo.
- Se actualizo el preview del Dashboard con datos de consumo de ejemplo para validar visualmente la seccion.

Verificacion ejecutada:

- `$env:JAVA_HOME='E:\android\jbr'; .\gradlew.bat --no-daemon "-Dkotlin.compiler.execution.strategy=in-process" :app:compileDebugKotlin`

Pendientes:

- Validar en dispositivo que la tarjeta aparezca cuando backend devuelve `estimatedCost`.
- Capturar evidencia v6 del Dashboard con ventana horaria y consumo estimado visibles.

## BACKEND-LOAD-ENERGY-SNAPSHOT-001 - Persistencia de consumo estimado al crear carga

Fecha: 2026-07-25.

Estado: implementado y probado en backend; despliegue y QA visual pendientes.

Contexto:

- Durante la validacion manual se detecto que Nueva carga mostraba una estimacion de electricidad/agua/costo, pero la carga creada no siempre quedaba persistida con los campos `estimated_washing_*`.
- Como consecuencia, el Dashboard podia no mostrar la tarjeta de consumo estimado para cargas creadas recientemente, aun cuando la prediccion previa habia calculado esos datos.
- El problema estaba en la frontera backend/Supabase: la prediccion se calculaba, pero el snapshot energetico no se grababa como parte del alta de la carga.

Alcance:

- Se agrego una operacion de persistencia `updateEnergyEstimate` en el data source Supabase de cargas.
- Al crear una carga, `LaundryLoadsService` calcula la prediccion, persiste `estimated_washing_energy_kwh`, `estimated_washing_water_liters`, `estimated_washing_cost_amount`, `estimated_washing_cost_currency`, `estimated_washing_cost_level` y `estimated_washing_cost_confidence`.
- La misma prediccion calculada se reutiliza para programar la notificacion de ventana ideal de tendido, evitando recalcular dos veces el mismo escenario.
- Si la persistencia del snapshot energetico falla, la carga no se bloquea; se conserva la carga creada y se registra el warning operativo correspondiente.
- Si la programacion de la notificacion ideal falla, la carga creada tampoco se bloquea; el backend registra el warning y mantiene el alta funcional.

Verificacion ejecutada:

- `cd backend; npm test -- drying-prediction-calculator.spec.ts laundry-loads.service.spec.ts laundry-loads.supabase-data-source.spec.ts`
- `cd backend; npm run build`

Pendientes:

- Desplegar backend en Render.
- Crear una carga nueva desde Android y verificar en Supabase que los campos `estimated_washing_*` queden poblados.
- Capturar evidencia v6 del Dashboard mostrando la tarjeta de consumo estimado con una carga nueva real.

## BACKEND-PRED-TIME-CLAMP-001 - Correccion de ventana y retiro estimado en pasado

Fecha: 2026-07-25.

Estado: implementado y probado en backend; despliegue y QA visual pendientes.

Contexto:

- En una prueba manual realizada alrededor de las 19:25, una carga nueva mostro `faltan 0 minutos` y una hora aproximada de secado previa, por ejemplo 19:21.
- La causa probable era que el proveedor climatico devolvia un slot horario ya iniciado, por ejemplo 19:00, y la heuristica lo usaba como inicio literal de la ventana recomendada.
- Para el usuario, esa lectura era contradictoria: una carga recien creada no deberia indicar retiro estimado anterior al momento actual.

Alcance:

- Se ajusto el calculador de prediccion para que la ventana recomendada no comience antes del instante de calculo cuando el slot climatico seleccionado ya esta en progreso.
- `recommendedHangAt` y `recommendedHangWindowStart` se ajustan al momento actual si el forecast seleccionado pertenece a una hora ya iniciada.
- `estimatedPickupAt` se calcula desde ese inicio corregido, evitando tiempos restantes en cero o fechas estimadas anteriores a la creacion de la carga.
- La ventana horaria mantiene su semantica de recomendacion por slots y conserva el resto de la heuristica climatica.

Verificacion ejecutada:

- `cd backend; npm test -- drying-prediction-calculator.spec.ts laundry-loads.service.spec.ts laundry-loads.supabase-data-source.spec.ts`
- `cd backend; npm run build`

Pendientes:

- Desplegar backend en Render.
- Crear una carga nueva cerca de un cambio de hora y verificar que `recommendedHangAt`, `recommendedHangWindowStart` y `estimatedPickupAt` no queden en el pasado.
- Validar en Android que el Dashboard muestre tiempo restante positivo y horarios coherentes.

## ANDROID-DASH-HOURLY-UX-002 - Slots horarios mas visuales y menos textuales

Fecha: 2026-07-25.

Estado: implementado y compilado.

Contexto:

- La ventana recomendada para tender ya mostraba informacion util, pero cada slot horario repetia texto de veredicto junto con temperatura, lluvia y score.
- En revision de UX se definio que el usuario ya ve suficientes datos y que la lectura rapida mejora si el veredicto se comunica principalmente con color e iconografia meteorologica.
- La decision sigue el criterio de "menos es mas": mantener la informacion necesaria, pero reducir carga textual.

Alcance:

- Se reemplazo el texto visible de veredicto por `WeatherGlyph` reutilizando los iconos propios de TenderApp.
- Los slots horarios usan sol para condiciones buenas, nube para precaucion y lluvia para riesgo.
- Se redujo el ancho de cada slot para mejorar el escaneo horizontal.
- La lluvia se muestra en formato compacto y el veredicto textual queda disponible como descripcion de accesibilidad.

Verificacion ejecutada:

- `$env:JAVA_HOME='E:\android\jbr'; .\gradlew.bat --no-daemon "-Dkotlin.compiler.execution.strategy=in-process" :app:compileDebugKotlin`

Pendientes:

- Validar visualmente en dispositivo en modo claro y oscuro.
- Capturar evidencia v6 de la ventana horaria simplificada si queda como version final de pantalla.

## ANDROID-NEWLOAD-UX-004 y ANDROID-UI-V7-FINAL-PASS-001 - Pulido visual v7 y evidencia en dispositivo

Fecha: 2026-07-25.

Estado: implementado, compilado, instalado y capturado en dispositivo fisico.

Contexto:

- Despues del ajuste de la ventana horaria del Dashboard, Nueva carga todavia tenia una tarjeta de decision demasiado textual: barra de progreso, score, fuente, nota explicativa y resumen competian por atencion.
- Para la evidencia final se priorizo una lectura mas escaneable y consistente con Dashboard: icono meteorologico, color semantico y senales compactas.
- El objetivo fue mantener todos los datos utiles sin aumentar complejidad ni descargar iconos externos con riesgo de licenciamiento o drift visual.

Alcance:

- Se compacto la tarjeta de decision de Nueva carga.
- Se reemplazo la barra textual por tres senales rapidas: `Puntaje`, `Secado` y `Fuente`.
- Se mantuvo el icono meteorologico propio `WeatherGlyph` y los colores semanticos existentes.
- Se corrigio la localizacion visible en Historial para que los programas no aparezcan como enums crudos (`QUICK`, `ECO`).
- Se instalo la app en un Motorola conectado por ADB y se capturo evidencia v7 en modo claro y oscuro.

Evidencia generada:

- `tesis-final/evidence/screenshots/v7/01-v7-dashboard-window-light.png`
- `tesis-final/evidence/screenshots/v7/02-v7-new-load-decision-light.png`
- `tesis-final/evidence/screenshots/v7/03-v7-history-light.png`
- `tesis-final/evidence/screenshots/v7/04-v7-settings-light.png`
- `tesis-final/evidence/screenshots/v7/05-v7-settings-dark.png`
- `tesis-final/evidence/screenshots/v7/06-v7-dashboard-window-dark.png`

Verificacion ejecutada:

- `$env:JAVA_HOME='E:\android\jbr'; .\gradlew.bat --no-daemon "-Dkotlin.compiler.execution.strategy=in-process" :app:compileDebugKotlin`
- `$env:JAVA_HOME='E:\android\jbr'; .\gradlew.bat --no-daemon "-Dkotlin.compiler.execution.strategy=in-process" :app:installDebug`
- Captura por ADB desde dispositivo `ZY22GRJ5TD`.

Observaciones:

- La pantalla Ajustes muestra el bloque Cuenta como `Sesion requerida / Abrir sesion` aunque otras pantallas pudieron consumir datos. Queda como posible desalineacion menor del estado de sesion en Settings.
- En Historial, la etiqueta de ubicacion `Mi ubicacion actual hogar` proviene del dato visible actual y puede revisarse como polish de copy/dato, no como bloqueo funcional.

Pendientes:

- Revisar manualmente las capturas v7 y seleccionar cuales pasan a la tesis final.
- Validar si el bloque Cuenta de Settings debe reflejar la sesion actual o abrir un flujo explicito de autenticacion.

## QA-PUSH-V7-001 - Evidencia visual de notificacion push real

Fecha: 2026-07-25.

Estado: validado manualmente y documentado.

Contexto:

- El circuito de notificaciones automaticas ya habia sido integrado con Android, backend NestJS, Supabase, Firebase Cloud Messaging y Render.
- Durante la validacion manual se recibio una notificacion de carga lista en el dispositivo fisico conectado por ADB.
- Para la tesis se requiere conservar evidencia visual trazable del comportamiento observado, sin inventar resultados ni reemplazar la validacion funcional por una captura aislada.

Alcance:

- Se abrio el panel de notificaciones del dispositivo fisico mediante ADB.
- Se capturo la pantalla del sistema con la notificacion recibida.
- Se agrego la captura al set de evidencia v7 y se actualizo el indice correspondiente.

Evidencia generada:

- `tesis-final/evidence/screenshots/v7/07-v7-push-notification-ready.png`

Verificacion ejecutada:

- `E:\sdk_android\platform-tools\adb.exe devices`
- `E:\sdk_android\platform-tools\adb.exe shell cmd statusbar expand-notifications`
- `E:\sdk_android\platform-tools\adb.exe shell screencap -p /sdcard/tenderapp-v7-push-notification.png`
- `E:\sdk_android\platform-tools\adb.exe pull /sdcard/tenderapp-v7-push-notification.png tesis-final\evidence\screenshots\v7\07-v7-push-notification-ready.png`

Pendientes:

- Seleccionar manualmente si la captura se incorpora al cuerpo principal de la tesis o queda como anexo de validacion.
- Mantener una validacion diferida del cron job para confirmar ejecucion automatica recurrente fuera de la prueba manual.

## ANDROID-SETTINGS-NOTIF-UX-001 - Pulido de preferencias de notificaciones en Settings

Fecha: 2026-07-25.

Estado: implementado y verificado.

Contexto:

- La seccion de notificaciones en Ajustes todavia conservaba filas estaticas propias de prototipo: `Entrega: Push y dentro de la app` y `Horario silencioso: 22:00 - 07:00`.
- Ese tratamiento era funcionalmente inocuo, pero visualmente podia interpretarse como mockup no integrado.
- Para el cierre del MVP se priorizo que la pantalla represente mejor el circuito real: permiso Android, opt-in, eventos auditados por backend, push FCM y preferencias por tipo de alerta.

Alcance:

- Se reemplazo la fila estatica de entrega por un bloque de canales activos: push del sistema y eventos dentro de la app.
- Se agrego estado configurable para horario silencioso en preferencias de notificacion.
- Se agrego control para activar o pausar horario silencioso y seleccionar hora de inicio/fin mediante desplegables simples.
- Se mantuvo el alcance local/in-memory existente para preferencias de notificaciones; no se introdujo nueva persistencia remota en esta tarea.
- Se actualizaron textos en espanol e ingles.
- Se agrego prueba unitaria de ViewModel para el horario silencioso configurable.

Verificacion ejecutada:

- `$env:JAVA_HOME='E:\android\jbr'; .\gradlew.bat --stop`
- `$env:JAVA_HOME='E:\android\jbr'; .\gradlew.bat --no-daemon "-Dkotlin.compiler.execution.strategy=in-process" :app:compileDebugKotlin :app:testDebugUnitTest --tests "com.tesis_pro.tenderapp.ui.screens.SettingsViewModelTest"`

Observaciones:

- No se implemento un selector horario modal tipo reloj. Para el MVP se uso un selector Material simple por desplegable, que reduce riesgo y evita crear una experiencia parcialmente persistida.
- La persistencia de preferencias de notificaciones sigue siendo local/in-memory como antes. Si se desea que sobreviva reinicios o sincronice con backend, corresponde abrir una tarea posterior.

Pendientes:

- Capturar nueva evidencia visual de Settings si esta version se incluye en v7/v8.
- Evaluar `ANDROID-NOTIF-PREFS-PERSIST-001` para persistir preferencias en almacenamiento local o backend.

## ANDROID-NOTIF-PREFS-PERSIST-001 - Persistencia local de preferencias de notificaciones

Fecha: 2026-07-25.

Estado: implementado y verificado.

Contexto:

- La seccion de notificaciones de Settings ya exponia opt-in, categorias y horario silencioso.
- Sin embargo, el estado todavia dependia del repositorio in-memory cuando se construian flujos reales de la aplicacion.
- Para cerrar el MVP con mayor consistencia, las preferencias debian sobrevivir la sesion de pantalla y alimentar el registro FCM con el opt-in elegido por el usuario.

Alcance:

- Se agrego `SharedPreferencesNotificationPreferencesRepository` como repositorio local persistente para preferencias de notificaciones.
- Settings usa el repositorio persistente en la app real.
- Nueva carga usa el mismo repositorio persistente para decidir elegibilidad de recordatorios locales/simulados.
- El registro FCM posterior a login y el registro al restaurar sesion envian al backend el `notificationOptIn` vigente.
- Se mantuvo `InMemoryNotificationPreferencesRepository` como soporte simple para tests y construcciones no persistentes.
- Se agrego una prueba para verificar que el registro push envia `notificationOptIn = false` cuando el proveedor lo indica.

Verificacion ejecutada:

- `$env:JAVA_HOME='E:\android\jbr'; .\gradlew.bat --no-daemon "-Dkotlin.compiler.execution.strategy=in-process" :app:compileDebugKotlin :app:testDebugUnitTest --tests "com.tesis_pro.tenderapp.ui.screens.SettingsViewModelTest" --tests "com.tesis_pro.tenderapp.data.notification.PostLoginPushRegistrarTest"`

Observaciones:

- La persistencia es local en el dispositivo. No se sincroniza aun con una tabla de preferencias de usuario en Supabase.
- El horario silencioso queda persistido, pero su aplicacion efectiva sobre dispatch backend queda como mejora posterior si se requiere control server-side.

Pendientes:

- Evaluar `BACKEND-NOTIF-PREFS-001` solo si se decide sincronizar preferencias por usuario en backend/Supabase.
- Capturar evidencia visual nueva de Settings despues de validar manualmente el flujo en dispositivo.

## THESIS-KANBAN-ANNEX-001 - Incrementos, Kanban y unidades funcionales

Fecha: 2026-07-25.

Estado: implementado y verificado.

Contexto:

- Se detecto que algunas secciones de la tesis sintetizaban el desarrollo en pocos incrementos y podian omitir el Incremento 6.
- La bitacora real muestra una evolucion mas amplia, con tareas Android, backend, Supabase, clima, prediccion, notificaciones, UX, energia, QA y generacion documental.
- Para la defensa academica conviene explicar el avance como incrementos de valor gestionados con una lectura Kanban adaptada, sin afirmar Scrum formal ni Kanban certificado.

Alcance:

- Se normalizo la tesis a 10 incrementos de valor mas cierre MVP.
- Se actualizo la narrativa del desarrollo incremental para incluir el Incremento 6 y separar autenticacion, cargas/lavarropas, clima/prediccion, notificaciones, UX y energia.
- Se agrego una seccion de lectura Kanban del proceso incremental.
- Se creo `tesis-output/final/kanban-annex.md`.
- Se creo `tesis-output/final/functional-units-annex.md`.
- Se actualizo `tesis-output/final/annexes-index.md`.
- Se actualizo `tesis-output/final/tables-index.md`.
- Se actualizo `tesis-output/generar_tesis_docx_estandarizada.py` para incorporar los anexos nuevos.
- Se genero `tesis-output/final/TenderApp-Tesis-Estandarizada-Anexos-Bibliografia-rev009.docx`.

Verificacion ejecutada:

- `python tesis-output\generar_tesis_docx_estandarizada.py --output tesis-output\final\TenderApp-Tesis-Estandarizada-Anexos-Bibliografia-rev009.docx`
- Conversion manual con LibreOffice local a PDF.
- Rasterizacion del PDF a 70 paginas PNG.
- Revision visual por hojas de contacto, con foco en anexos E y F.

Observaciones:

- Kanban se presenta como marco practico de flujo, no como adopcion formal certificada.
- Las unidades funcionales no reemplazan al backlog tecnico; sirven para explicar el alcance de producto y su trazabilidad.
- No se anexan bitacoras completas ni documentos internos extensos.

Pendientes:

- Actualizar campos automaticos de Word mediante `Ctrl+A` y `F9`.
- Revisar humanamente los nombres finales de incrementos antes de entrega.
- Completar observacion diferida del scheduler de notificaciones si se usa como evidencia final.

## THESIS-GAP-REVIEW-001 - Auditoría académica, técnica y metodológica

Fecha: 2026-07-25.

Estado: informe generado; correcciones pendientes de aprobación e implementación.

Contexto:

- Se revisó la tesis como documento académico centrado en TenderApp, sin reintroducir el enfoque anterior sobre inteligencia artificial o sistema documental.
- La auditoría contrastó la fuente Markdown, el DOCX rev009, anexos, referencias, evidencia, bitácora, código de la heurística y pruebas actuales.
- El objetivo fue distinguir estructura disponible, evidencia comprobada, afirmaciones que requieren ajuste y riesgos de defensa.

Resultado:

- Se creó `tesis-output/final/thesis-gap-review.md`.
- El dictamen es `requiere correcciones importantes antes de la entrega final`.
- No se detectó una desalineación temática P0 en la fuente canónica.
- Los principales gaps son la falta de evaluación empírica de la heurística, reproducibilidad incompleta de parámetros en el cuerpo, evidencia end-to-end pendiente del scheduler, trazabilidad débil entre afirmación y evidencia, antecedentes genéricos y revisión APA incompleta.
- Se identificó una diferencia arquitectónica que debe explicitarse: backend como fuente canónica y cálculos locales Android como fallback degradado.

Verificación ejecutada:

- `cd backend; npm test -- --runInBand`
  - Resultado: 36 de 37 suites y 147 de 150 pruebas aprobadas.
  - Pendiente: actualizar tres expectativas de `weather-provider.config.spec.ts` después de incorporar MET Norway.
- `$env:JAVA_HOME='E:\android\jbr'; .\gradlew.bat --no-daemon "-Dkotlin.compiler.execution.strategy=in-process" :app:testDebugUnitTest`
  - Resultado: `BUILD SUCCESSFUL`.
- Revisión visual de las hojas de contacto del render DOCX rev009, 70 páginas.

Próxima tarea recomendada:

- `THESIS-CORE-VALIDATION-001 - Formalizar y validar la heurística de recomendación`.

## THESIS-CORE-VALIDATION-001 - Validación del núcleo heurístico

Fecha: 2026-07-25.

Estado: implementado y verificado; revisión humana pendiente.

Objetivo:

- Convertir la heurística de recomendación en un componente reproducible y defendible sin afirmar precisión física o estadística no observada.

Alcance:

- Se declaró `backend/src/predictions/drying-prediction-calculator.ts` como fuente canónica.
- Se documentó el cálculo Android como fallback degradado.
- Se agregó `backend/src/predictions/drying-prediction-core-validation.spec.ts`.
- Se verificaron seis escenarios exactos y sensibilidad monotónica.
- Se corrigieron expectativas de configuración meteorológica para MET Norway.
- Se creó `tesis-output/final/heuristic-validation-annex.md`.
- Se actualizó `ALG-001`, backlog, tesis, índices, referencias y generador DOCX.
- Se incorporaron fuentes científicas específicas sobre secado textil y humedad residual.

Resultados controlados:

| Escenario | Puntaje | Veredicto | Tiempo |
|---|---:|---|---:|
| Exterior favorable, ropa liviana | 92 | `GOOD` | 100 min |
| Humedad alta, ropa liviana | 64 | `CAUTION` | 158 min |
| Lluvia alta en exterior | 17 | `BAD` | 230 min |
| Mismo clima lluvioso en interior | 88 | `GOOD` | 437 min |
| Ropa pesada, 600 rpm, carga grande | 92 | `GOOD` | 303 min |
| Ropa liviana, 1400 rpm, carga pequeña | 92 | `GOOD` | 76 min |

Verificación ejecutada:

- `cd backend; npm test -- --runInBand`
  - Resultado: 38 suites y 158 pruebas aprobadas.
- `cd backend; npm run build`
  - Resultado: compilación correcta.
- `git diff --check`
  - Resultado: sin errores de whitespace; advertencias locales de normalización LF/CRLF.

Interpretación:

- La heurística es determinista y consistente con las reglas evaluadas.
- La sensibilidad se mantiene en la dirección esperada para humedad, lluvia, tipo de ropa, centrifugado y tamaño de carga.
- El secado interior puede conservar un veredicto favorable frente a lluvia y, al mismo tiempo, estimar una duración mayor por ventilación reducida.
- No se afirma precisión del tiempo estimado contra secado real.

Pendiente:

- Diseñar y ejecutar `QA-HEURISTIC-EMPIRICAL-001` si se desea calibrar con observaciones reales.
- Revisión y aprobación humana del documento `ALG-001` y del Anexo G.

## MVP-TIME-FEASIBILITY-001 - Planificacion de lavado y secado por hora objetivo

Fecha: 2026-07-26.

Estado: implementado localmente y verificado; despliegue y smoke en dispositivo pendientes.

Objetivo:

- Permitir que el usuario indique cuando necesita tener la ropa lavada y seca, y comparar que programas pueden cumplir esa hora segun el tiempo de lavado y la prediccion meteorologica de secado.

Alcance implementado:

- Se formalizaron cuatro duraciones controladas: QUICK 30 min, DELICATE 45 min, NORMAL 60 min y ECO 90 min.
- Se agrego `POST /api/v1/predictions/completion-options`.
- El backend consulta una sola ventana meteorologica y evalua todos los programas.
- Cada alternativa devuelve fin de lavado, inicio de secado, tiempo de secado, hora estimada de finalizacion, margen y factibilidad.
- La respuesta conserva alternativas no factibles para comparacion, pero recomienda solo las que llegan al objetivo.
- Android incorporo el contrato remoto y los modelos de dominio.
- Nueva carga permite elegir `Ropa lista antes de` mediante Material 3 TimePicker.
- La UI muestra lavado + secado, hora estimada y margen por programa; tocar una alternativa selecciona ese programa.
- Una hora ya vencida se mueve a su proxima ocurrencia local.
- Las respuestas asincronas obsoletas no reemplazan una seleccion mas nueva.
- Los textos visibles fueron agregados en espanol e ingles.

Verificacion ejecutada:

- `cd backend; npm test -- --runInBand`
  - Resultado: 40 suites y 170 pruebas aprobadas.
- `cd backend; npm run build`
  - Resultado: compilacion correcta.
- `$env:JAVA_HOME='E:\android\jbr'; .\gradlew.bat --no-daemon :app:testDebugUnitTest :app:assembleDebug`
  - Resultado: 138 pruebas aprobadas y APK debug ensamblado.
- `git diff --check`
  - Resultado: sin errores de whitespace; solo advertencias locales LF/CRLF.

Interpretacion:

- La funcionalidad responde una decision de usuario previa a crear la carga: si comienza en el momento previsto, que programas permiten tener la ropa lista antes de la hora elegida.
- El resultado es una estimacion explicable; no garantiza tiempos fisicos ni reemplaza datos certificados del fabricante.
- Cuando la cobertura meteorologica no alcanza, cada alternativa lo informa mediante `usesForecastExtrapolation`.

Pendientes:

- Desplegar el backend con el nuevo endpoint.
- Instalar el APK actualizado y ejecutar el smoke de `QA-COMPLETION-OPTIONS-001`.
- Capturar evidencia visual en espanol despues de la aprobacion humana.

## DOCS-THESIS-DIAGRAMS-002 - Actualizacion de diagramas y estructura de tesis

Fecha: 2026-07-26.

Estado: implementado y verificado visualmente; revision humana pendiente.

Objetivo:

- Reconciliar los diagramas y la estructura academica con el estado real de TenderApp, incluyendo diez incrementos, failover meteorologico, FCM real, scheduler, snapshot de secado y planificacion por hora objetivo.

Documentos y activos corregidos:

- Se actualizaron THESIS-001 a THESIS-008.
- Se agregaron THESIS-010 para planificacion por hora objetivo y THESIS-011 para ciclo de notificaciones.
- Se marco THESIS-009 como artefacto interno excluido de la tesis.
- Cada `.mmd` conserva un wrapper `.md` con proposito, ubicacion, nota APA, estado y limites.
- Se creo `TH-011-Figure-and-Diagram-Integration-Map.md`.
- Se sincronizaron el mapa de capitulos, marco teorico, estrategia de evidencia, checklist, baseline, indices y generador DOCX.
- Se corrigio `MVP-INCREMENTS-001` para reflejar diez incrementos y eliminar la afirmacion obsoleta de push futuro.

Graficos:

- Mermaid CLI 11.16.0 renderizo diez PNG sin errores.
- Los PNG se revisaron visualmente y quedaron en `tesis-output/final/assets/figures/`.
- THESIS-002 y THESIS-010 se reorganizaron para lectura vertical.
- THESIS-001, THESIS-004 y THESIS-007 se documentaron para orientacion horizontal o anexo.

Verificacion:

- Los wrappers coinciden exactamente con sus fuentes `.mmd`.
- Los diez diagramas permitidos renderizaron correctamente.
- Se revisaron encuadre, legibilidad, orden visual y ausencia de cortes.
- El generador produjo una candidata temporal de 79 paginas sin sobrescribir `rev010`.
- LibreOffice con perfil aislado convirtio la candidata temporal a PDF.
- Se inspeccionaron las paginas afectadas por Figuras 1, 19 y 20; captions, imagenes y notas permanecen dentro de margenes.
- El ancho de imagen del generador se ajusto segun relacion de aspecto para aprovechar mejor la pagina sin exceder el presupuesto vertical.
- `git diff --check` no reporto errores de whitespace.

Pendientes:

- No se regenero ni sobrescribio la candidata `rev010`, que contiene cambios manuales del autor.
- Falta smoke desplegado de `MVP-TIME-FEASIBILITY-001`.
- Falta evidencia diferida de una ejecucion vencida del scheduler.
- La numeracion y ubicacion definitiva de algunas figuras requieren revision humana.

## THESIS-DELIVERY-REV011-001 - Cierre académico y recuperación del plan de finalización

Fecha: 2026-07-26.

Estado: candidata `rev011` generada y verificada; aprobación humana y dos evidencias operativas pendientes.

Documentación y roles aplicados:

- Se leyó el harness obligatorio, los índices FR5/FR6, los contextos operativos, el backlog, los criterios de calidad, la arquitectura de tesis y los artefactos de investigación pertinentes.
- Se aplicaron las perspectivas de investigación, Android, backend, testing, documentación, revisión, escritura de tesis y control de citas.
- La tarea se mantuvo vinculada a `PB-024`, `QA-COMPLETION-OPTIONS-001` y `THESIS-DELIVERY-REV011-001`.

Plan de finalización:

- Se comprobó que el error visible en Android coincidía con un backend desplegado sin la ruta nueva.
- Se publicó el estado local pendiente y Render incorporó `POST /api/v1/predictions/completion-options`.
- El contrato desplegado aparece en OpenAPI y rechaza solicitudes sin autenticación con `401`, comportamiento esperado.
- Android ahora permite reintentar la consulta sin perder el programa, la hora objetivo ni los demás datos seleccionados.
- Se agregó una prueba de recuperación luego de un error remoto.

Verificación técnica:

- Backend: 40 suites y 170 pruebas aprobadas; build TypeScript correcto.
- Android: 139 pruebas aprobadas; `:app:assembleDebug` correcto.
- `git diff --check`: sin errores de whitespace; solo advertencias locales de normalización LF/CRLF.
- Queda pendiente el smoke autenticado desde un dispositivo conectado, porque no había un dispositivo ADB disponible durante esta ejecución.

Cierre académico:

- Se reforzaron citas autor-fecha y referencias APA 7.
- Se amplió el fundamento del secado textil y de la heurística con fuentes primarias, sin convertir los parámetros de TenderApp en afirmaciones científicas no calibradas.
- Se actualizaron resultados, discusión, conclusiones, limitaciones, índices de figuras y tablas.
- Se integraron 19 figuras y 24 tablas en la candidata `rev011`.
- Word actualizó los campos automáticos del documento.
- LibreOffice produjo 87 páginas y se realizó revisión visual completa mediante hojas de contacto y páginas críticas.
- No se detectaron recortes, superposiciones ni elementos fuera de margen.
- La candidata conserva como tema central a TenderApp y excluye el sistema documental asistido por inteligencia artificial como objeto principal.

Pendientes:

- Smoke autenticado del plan de finalización y, si corresponde, captura redactada.
- Evidencia de un evento vencido procesado por el scheduler.
- Revisión institucional de portada, datos académicos y normas particulares.
- Lectura y aprobación humana final del autor y de la dirección de tesis.

## THESIS-ANNEX-CLOSURE-001 - Cierre de anexos restantes

Fecha: 2026-07-26.

Estado: implementado y verificado; aprobación humana pendiente.

Alcance:

- Se completó el Anexo C con evidencia automatizada, validación funcional y controles diferidos.
- Se completó el Anexo D con una síntesis de diez incrementos y cierre del MVP.
- Se incorporó el Anexo H para contrato API, despliegue y límites de la evidencia.
- Se incorporó el Anexo I para seguridad, persistencia, RLS, secretos y privacidad.
- Se actualizaron índices, pendientes y generador DOCX.

Verificación:

- Se generó `TenderApp-Tesis-Estandarizada-Anexos-Bibliografia-rev012.docx` sin sobrescribir revisiones previas.
- Word actualizó los índices y campos automáticos.
- La auditoría estructural obtuvo 847 párrafos, 32 tablas, 122 títulos semánticos y 19 imágenes.
- LibreOffice produjo un PDF de 102 páginas.
- Las 102 páginas fueron inspeccionadas visualmente mediante 17 hojas de contacto; no se observaron recortes, superposiciones ni tablas ilegibles.
- Se detectó y corrigió una referencia interna obsoleta que indicaba `rev011` y 87 páginas.

Pendientes preservados:

- smoke autenticado del plan de finalización;
- evento vencido procesado por el scheduler;
- precisión empírica del tiempo de secado y evaluación formal de usabilidad;
- lectura y aprobación institucional humana.

## THESIS-AUTHOR-VOICE-001 - Perfil de redacción del autor

Fecha: 2026-08-01.

Estado: implementado; aprobación humana del perfil pendiente.

Fuente analizada:

- `Tesis Juan Ignacio Santillán Moio -Avances-.docx`, obra original aportada por el autor.
- SHA-256: `B76EABFF54A78F4D91815A054D17C4680FF01394F345C82C9387D74263C66011`.

Resultados:

- Se analizaron 38 páginas, 341 párrafos con contenido, 59 títulos semánticos, 4 tablas y 6 imágenes.
- La muestra contiene aproximadamente 6.277 palabras de cuerpo y oraciones de 15,5 palabras en promedio.
- Se identificó una voz directa, aplicada e impersonal moderada, con progresión frecuente entre problema, necesidad y solución.
- Se creó `docs/10-thesis/TH-012-Author-Voice-and-Writing-Profile.md`.
- Se actualizaron el mapa de lectura, `PROMPT-006`, AG-009 y la baseline `TH-010`.
- El documento histórico se utiliza solo como fuente de estilo; no aporta hechos, bibliografía ni contenido temático a TenderApp.

Próximo control:

- Aplicar el perfil a Introducción, núcleo heurístico y Conclusiones.
- Presentar las tres muestras al autor antes de generar `rev013`.

## THESIS-VOICE-REV013-001 - Muestra controlada de voz del autor

Fecha: 2026-08-01.

Estado: muestra generada; aprobación humana pendiente.

Resultado:

- Se aplicó `TH-012` a Introducción, fundamentos de la heurística y Conclusiones.
- Se conservó el contenido factual y la bibliografía del manuscrito vigente.
- Se redujo la densidad de oraciones y se reforzó la progresión problema, necesidad, solución y límite.
- La muestra quedó en `tesis-output/review/THESIS-VOICE-REV013-SAMPLE.md`.
- No se modificó `tesis-output/final/tesis-final.md` ni se generó `rev013`.

Gate:

- El autor debe revisar el tono y señalar expresiones que no utilizaría.
- La aplicación global del perfil queda bloqueada hasta esa aprobación.

### Devolución humana de la muestra

Fecha: 2026-08-01.

- El autor confirmó que la redacción se parece más a su forma de explicar problemas.
- La lectura en voz alta fue considerada natural.
- El grado de formalidad fue considerado adecuado para la exigencia académica.
- No se detectaron inicialmente giros claramente ajenos a la voz del autor.
- Se aprobó aplicar esta dirección a la nueva iteración en lugar de conservar mayor cantidad de `rev012`.
- Se solicitó permitir explicaciones más desarrolladas del porqué y el cómo.
- Se creó `TH-013-Author-Writing-Questionnaire.md` para completar preferencias no inferibles desde la muestra histórica.

### Respuestas al cuestionario de voz

Fecha: 2026-08-01.

- Se respondieron quince de las dieciséis preguntas de `TH-013`.
- Se confirmó el uso preferente de voz impersonal en pasado mediante expresiones como `se decidió` y `se verificó`.
- Se descartó la expresión `el proyecto adoptó` como ajena a la voz del autor.
- Se confirmó una progresión desde problema técnico hacia definiciones, ejemplos y solución.
- Se priorizó la explicación extensa del porqué y el cómo en varios párrafos conectados.
- Se confirmó el uso de prosa antes de listas y de diagramas antes del detalle técnico cuando sea posible.
- Se conservarán términos técnicos en inglés con explicación funcional en español.
- Los resultados presentarán primero lo que funcionó y luego sus limitaciones.
- Las conclusiones serán reflexivas e integrarán respuesta, alcance, límites y trabajo futuro.
- Permanece pendiente la pregunta 12 sobre la forma preferida de integrar citas.
- Se registró que FAO 56, punto de rocío y comparaciones temporales requieren límites y evidencia antes de convertirse en afirmaciones del manuscrito.

### Cierre del perfil de voz

Fecha: 2026-08-01.

- El autor indicó que las citas deben ubicarse normalmente al final de la explicación.
- La motivación se redactará como una necesidad real observada en el entorno cercano, sin individualizar a la persona que originó el comentario.
- Las observaciones meteorológicas y de secado fueron informales; se conservarán como motivación y no como validación experimental.
- `TH-013` quedó completado y aprobado.
- `TH-012` alcanzó la versión `1.0.0` con estado `Approved for rev013 drafting`.
- El gate de `THESIS-VOICE-REV013-001` quedó completado.

## THESIS-VOICE-REV013-002 - Aplicación integral de voz y candidata rev013

Fecha: 2026-08-01.

Estado: implementación y QA documental completados; aprobación humana final pendiente.

Resultado:

- Se aplicó `TH-012` y `TH-013` a los capítulos centrales de `tesis-output/final/tesis-final.md`.
- Se reforzó la progresión problema, necesidad, solución y límite.
- Se desarrollaron con mayor profundidad el núcleo heurístico, la definición del problema, la factibilidad, los riesgos, los resultados y las conclusiones.
- Se conservaron citas, cifras, tablas, figuras, anexos y estados de validación.
- Se generó `TenderApp-Tesis-Estandarizada-Anexos-Bibliografia-rev013.docx` sin sobrescribir `rev012`.
- El generador fue corregido para iniciar `Abstract` y cada capítulo en una página nueva.
- La portada fue actualizada al 1 de agosto de 2026.

Verificación:

- SHA-256 de `rev013`: `A6BCBF617DCB0AD164E452A29B337D9E5EFC2BA376595ED0CC9107B75A8523AF`.
- 884 párrafos, 32 tablas y 19 imágenes.
- 15 títulos `Heading 1`, 48 `Heading 2` y 59 `Heading 3`.
- 3 campos `TOC` y 36 campos `SEQ`.
- Índices y campos actualizados mediante Microsoft Word.
- 111 páginas renderizadas con LibreOffice y PyMuPDF.
- Diez hojas de contacto revisadas y páginas críticas inspeccionadas en tamaño original.
- No se detectaron recortes, superposiciones, referencias rotas ni vestigios del enfoque temático anterior.

Pendientes preservados:

- aprobación humana completa de la candidata;
- revisión institucional y metadatos APA finales;
- calibración empírica y evaluación formal de usabilidad;
- observaciones diferidas del scheduler y del plan de finalización todavía no registradas.

## THESIS-REV014-ACADEMIC-CLEAN-001 - Limpieza académica y candidata rev014

Fecha: 2026-08-01.

Estado: implementación y QA documental completados; aprobación humana final pendiente.

Resultado:

- Se utilizó como entrada la `rev013` guardada manualmente por el autor y se preservaron sus 21 recursos gráficos.
- Se generó `TenderApp-Tesis-Estandarizada-Anexos-Bibliografia-rev014.docx` sin sobrescribir la revisión anterior.
- Se retiraron del cuerpo visible códigos de tareas, rutas locales, referencias como `ALG-001` e instrucciones propias del proceso de generación.
- La explicación de la heurística quedó integrada al texto mediante sus variables, alcance, limitaciones y relación con la Tabla 4.
- Se normalizaron notas de figuras y anexos para identificar elaboración propia o evidencia de la aplicación sin exponer rutas de trabajo.
- Se corrigió el orden de una cita parentética múltiple y se mantuvo el sistema autor-fecha.
- Se actualizaron la fuente Markdown, los anexos y el generador para que futuras revisiones mantengan el mismo criterio.
- Se agregó un sanitizador DOCX reutilizable para preservar ediciones manuales mientras se eliminan rastros internos controlados.

Verificación:

- SHA-256 de `rev014`: `246D056748F0AFE99DBBD26925778F7C40AD83D0FE45AFB13A32788577D176A0`.
- 878 párrafos, 34 tablas, 21 imágenes, 3 campos `TOC` y 34 campos `SEQ`.
- 110 páginas renderizadas mediante LibreOffice y PyMuPDF.
- Sin páginas en blanco, bloques fuera del área de página ni identificadores internos detectados en el contenido visible.
- Páginas críticas del índice, marco heurístico, figuras y anexos inspeccionadas en tamaño original.

Pendientes preservados:

- lectura y aprobación humana integral de `rev014`;
- revisión institucional y bibliográfica final;
- calibración empírica y evaluación formal de usabilidad.

## RESEARCH-EVIDENCE-MATRIX-001 - Sustento científico y candidata rev015

Fecha: 2026-08-02.

Estado: implementación y QA documental completados; aprobación académica humana pendiente.

Resultado:

- Se verificaron diez fuentes científicas centrales mediante identificadores DOI, páginas editoriales, Crossref y el portal oficial de FAO.
- Se creó una matriz de uso académico real que separa aporte bibliográfico, aplicación en TenderApp y límite de inferencia.
- Se actualizaron el registro de fuentes, la extracción sobre secado textil y el mapa de fuentes del marco teórico.
- Se integró la matriz como Tabla 5 del fundamento heurístico y se retiró la tabla técnica genérica que ocupaba ese número.
- Se generó `TenderApp-Tesis-Estandarizada-Anexos-Bibliografia-rev015.docx` a partir de la versión manual vigente de `rev014`, sin sobrescribirla.
- Los 24 recursos gráficos de la entrada fueron preservados.

Verificación:

- SHA-256: `0246C17F498880324B5C7FE2BED1011E8C7D167BA1C8C25228BD841CE98DBFCC`.
- 882 párrafos, 35 tablas, 24 recursos gráficos, 3 campos `TOC` y 34 campos `SEQ`.
- Índices y campos actualizados mediante Microsoft Word.
- 112 páginas exportadas y rasterizadas con PyMuPDF.
- Siete hojas de contacto y las páginas críticas 26 a 28 revisadas visualmente.
- Sin recortes, superposiciones ni tablas fuera de margen.

Límite preservado:

- La evidencia científica respalda variables, relaciones y límites del modelo, pero no valida los pesos, umbrales ni duraciones definidos para el MVP.
- La calibración empírica con cargas reales y la evaluación formal de usabilidad permanecen pendientes.

## THESIS-ENGINEERING-BASIS-001 - Marco de ingeniería y candidata rev016b

Fecha: 2026-08-02.

Estado: implementación y QA documental completados; aprobación académica humana pendiente.

Resultado:

- Se verificó el uso real y el límite académico de las principales normas y guías de ingeniería utilizadas como referencia.
- Se creó una matriz que separa normas publicadas, especificaciones, prácticas profesionales y convenciones internas.
- Se actualizó ISO/IEC 25010 desde la edición 2011 hacia la edición vigente de 2023.
- Se integró el Anexo J con la cadena de trazabilidad aplicada y un ejemplo del circuito de notificaciones.
- Se dejó explícito que el marco documental apoya el desarrollo, pero no es el objeto de la tesis ni acredita certificación.
- Se generó `rev016b` desde `rev015`, preservando sus 24 recursos gráficos.

Verificación:

- SHA-256: `8B9FF961E8A5439AAF811354E3B17E820B0EAA82A63DBE7214970B96897B754F`.
- 923 párrafos, 38 tablas, 24 recursos gráficos, 3 campos `TOC` y 34 campos `SEQ`.
- 117 páginas exportadas y rasterizadas.
- Diez hojas de contacto y páginas críticas inspeccionadas visualmente.
- Sin páginas en blanco, contenido fuera de margen, filas divididas de forma incorrecta ni títulos de tabla huérfanos.

Límites preservados:

- no se afirma cumplimiento normativo integral;
- no se incorporan documentos operativos completos;
- la revisión institucional y la aprobación humana continúan pendientes;
- `rev016` queda supersedida por `rev016b` debido al bloqueo de la primera salida durante el ajuste final.

## THESIS-HEURISTIC-EVOLUTION-001 - Evolución heurística y candidata rev017

Fecha: 2026-08-02.

Estado: implementación y QA documental completados; aprobación académica humana pendiente.

Resultado:

- Se reconstruyeron diez refinamientos verificables del algoritmo mediante historial de cambios, migraciones, código canónico y pruebas.
- Se separaron fundamento científico, decisión heurística del MVP y evidencia de consistencia.
- Se incorporó al cuerpo una explicación autosuficiente de la evolución del modelo.
- Se agregó el Anexo K con evolución funcional, relación entre fuentes y decisiones, capas actuales y camino de calibración.
- Se creó una fuente Mermaid editable y un wrapper Markdown; la exportación gráfica queda pendiente.
- Se generó `rev017` desde `rev016b`, preservando sus 24 recursos gráficos.

Verificación:

- SHA-256: `68D239F3BF0BD4D3887DC77A728164A995D57D3DC97BC2107DDBD34671E1561D`.
- 965 párrafos, 41 tablas, 24 recursos gráficos, 3 campos `TOC` y 34 campos `SEQ`.
- 2 suites y 16 pruebas focalizadas del calculador aprobadas.
- 123 páginas exportadas y rasterizadas.
- Once hojas de contacto y páginas 27 y 119 a 123 inspeccionadas visualmente.
- Sin páginas en blanco, contenido fuera de margen, recortes, superposiciones ni tablas ilegibles.

Límites preservados:

- la consistencia automatizada no se presenta como precisión empírica;
- los pesos y duraciones se mantienen como decisiones heurísticas del MVP;
- el modelo híbrido se formula como trabajo futuro;
- la calibración real, la evaluación de usabilidad y la aprobación humana continúan pendientes.

## THESIS-MERMAID-METHODOLOGY-001 - Metodología de diagramas

Fecha: 2026-08-02.

Estado: documentación y validación estructural completadas; integración DOCX consolidada pendiente.

Resultado:

- Se formalizó el ciclo entre fuente `.mmd`, wrapper `.md`, exportación y figura académica.
- Se creó el Anexo L y se actualizaron mapa, índices, generador y README de assets.
- Se diferenciaron diagramas de producto, diagramas metodológicos y fuentes operativas internas.
- Se mantuvo excluido el pipeline documental interno de la tesis.

Verificación:

- 12 fuentes y wrappers sincronizados;
- 10 exportaciones requeridas presentes y posteriores a sus fuentes;
- 0 errores del validador reproducible.

Pendientes:

- exportar la evolución heurística si se incorpora como figura;
- revisar visualmente cualquier exportación futura;
- integrar el Anexo L en la siguiente candidata consolidada.

## THESIS-PMBOK-TAILORING-001 - Adaptación de dirección del proyecto

Fecha: 2026-08-02.

Estado: documentación completada; integración DOCX consolidada pendiente.

Resultado:

- Se creó el Anexo M con adaptación de alcance, cronograma, costos, calidad, recursos, riesgos, interesados, comunicaciones y proveedores.
- Se distinguieron rangos estimados, hitos observables y un Gantt formal no disponible.
- Se incorporaron matrices de interesados y dependencias externas.
- Se documentaron herramientas no aplicadas para evitar sobredimensionar el proceso.

Límites:

- PMBOK continúa como referencia conceptual;
- las fechas de commits no se interpretan como dedicación continua;
- no se presenta valor ganado, PMO, adquisiciones ni Gantt histórico inventado;
- la integración en Word queda para la candidata consolidada.

## THESIS-GANTT-TRADITIONAL-001 - Línea base temporal tradicional

Fecha: 2026-08-02.

Estado: documentación y validación estructural completadas; aprobación humana y exportación final pendientes.

Resultado:

- Se reemplazaron los rangos genéricos por una línea base central de 60 jornadas laborables.
- Se asignaron duraciones variables a los diez incrementos y al cierre del MVP según el trabajo esperado y sus dependencias.
- Se documentaron tres escenarios: 45 jornadas en condición optimista, 60 en la línea base y 75 con ajuste por riesgo.
- Se creó el Gantt editable `THESIS-013` con fecha académica de referencia entre el 6 de julio y el 25 de septiembre de 2026.
- Se ampliaron el Anexo M, el manuscrito, los índices, el catálogo de diagramas y las fuentes de planificación.

Verificación:

- la suma de la línea base es de 60 jornadas;
- la fuente `.mmd` y el bloque Mermaid del wrapper permanecen sincronizados;
- la ejecución observada continúa expresada por separado como cuatro semanas y un mínimo aproximado de 110 horas;
- GAO, NASA y PMBOK respaldan el método de planificación, no las duraciones particulares de TenderApp;
- no se afirmó un factor exacto de productividad ni causalidad atribuible a una herramienta.

Pendientes:

- aprobación humana de duraciones y fechas de referencia;
- exportación horizontal y revisión visual del Gantt;
- integración de los anexos M y N en una candidata DOCX consolidada posterior a `rev017`.

## THESIS-INCREMENTS-DEEPENING-001 - Profundización de los incrementos

Fecha: 2026-08-02.

Estado: documentación completada; integración DOCX consolidada pendiente.

Resultado:

- Se diferenciaron incremento, unidad funcional, tarea, criterio de aceptación, evidencia e historia de usuario.
- Se creó el Anexo N con el ciclo técnico recurrente y una ficha de objetivo, construcción, verificación y límite para cada uno de los diez incrementos.
- Se vinculó el relato incremental con las unidades funcionales existentes sin transformar retrospectivamente todo el backlog en historias de usuario.
- Se reforzó la sección 4.10 y se actualizaron índices, plan incremental y generador.
- El generador quedó preparado para representar las fichas N.3.1 a N.3.10 como encabezados Word de cuarto nivel.

Límites:

- no se asignaron duraciones definitivas ni se generó el Gantt en esta tarea;
- las fechas de commits y evidencias se mantienen como hitos, no como medición de dedicación continua;
- la tarea posterior deberá estimar jornadas realistas de desarrollo tradicional y distinguirlas de la cronología observada;
- la estimación se reconstruirá `ex ante`, con un horizonte favorable inicial de dos a tres meses y duraciones variables según investigación, implementación, integración y validación;
- los bloqueantes descubiertos durante la ejecución se registrarán como desviaciones o replanificaciones, no como conocimiento disponible al formular el plan inicial;
- el autor reconstruye una duración observada de cuatro semanas y un mínimo aproximado de 110 horas: cerca de 50 horas durante la primera semana y al menos 20 en cada una de las tres siguientes;
- el dato no procede de una herramienta de seguimiento horario y no se utilizará para afirmar un factor exacto de productividad ni atribuir causalidad a una herramienta;
- la integración en Word queda para la candidata consolidada.

## THESIS-REV018-GANTT-INTEGRATION-001 - Cronograma reproducible e integración DOCX

Fecha: 2026-08-02.

Estado: implementación y validación documental completadas; aprobación humana pendiente.

Resultado:

- Se estructuró la planificación tradicional en cuatro fases y once actividades.
- Se incorporaron duración, inicio, fin y predecesor en la Tabla 10.
- Se creó una fuente CSV como dato canónico del cronograma y un validador que comprueba días hábiles, dependencias y suma total.
- Se regeneró `THESIS-013`, se exportó la Figura 20 y se integró en orientación horizontal dentro del Anexo M.
- Se integraron los anexos M y N en `rev018` sin sobrescribir revisiones anteriores.

Verificación:

- 60 jornadas laborables entre el 6 de julio y el 25 de septiembre de 2026;
- 1144 párrafos, 50 tablas, 20 recursos gráficos, 3 campos `TOC` y 37 campos `SEQ`;
- 146 páginas renderizadas, sin páginas vacías;
- revisión visual completa mediante hojas de contacto;
- revisión ampliada de la Tabla 10 y la Figura 20;
- corrección de una fila dividida entre páginas antes de la regeneración final.

Límites:

- las fechas representan una línea base académica, no la reconstrucción histórica exacta;
- la ejecución observada continúa expresada por separado como cuatro semanas y un mínimo aproximado de 110 horas;
- no se afirma un multiplicador de productividad ni causalidad atribuible a una herramienta;
- las jornadas y fechas requieren aprobación humana antes de la entrega institucional.

## THESIS-RELEVANCE-AUDIT-001 - Relevancia académica de los anexos J a N

Fecha: 2026-08-03.

Estado: auditoría completada; decisiones de reducción pendientes de aprobación humana.

Resultado:

- Se preservó como baseline humana la copia corregida de `rev018`, con SHA-256 `69037FC731A4771BB8C7740D9665E436C654D2661871EA28C8A66881CA2AA6CB`.
- Microsoft Word informó 145 páginas, 30.350 palabras, 3.227 párrafos y 50 tablas.
- Los anexos J a N ocupan 30 páginas y concentran 20 tablas.
- Cada subsección fue clasificada como conservar, resumir, trasladar o retirar.
- Se recomendó conservar K, reducir J, M y N, y retirar L del manuscrito sin eliminar sus fuentes operativas.
- Se detectó que el DOCX contiene 27 revisiones manuales todavía no sincronizadas con Markdown y el generador.

Límites:

- no se modificó ni regeneró el DOCX;
- no se aplicaron recortes a los anexos;
- el rango objetivo de 13 a 16 páginas requiere aprobación humana;
- una nueva candidata debe denominarse `rev019` y preservar `rev018`.

Siguiente paso recomendado:

- ejecutar `THESIS-SOURCE-SYNC-001` antes de cualquier regeneración.

## THESIS-SOURCE-SYNC-001 - Sincronización de revisión humana y fuentes canónicas

Fecha: 2026-08-03.

Estado: completado; `rev018` preservada y fuentes preparadas para una futura `rev019`.

Resultado:

- Se comparó la generación original de `rev018` con la copia corregida por el autor y se conservaron ambos hashes como trazabilidad.
- Se trasladaron 26 decisiones editoriales a `tesis-final.md`, las fuentes de anexos y el generador DOCX.
- La barra `/` inicial se clasificó como artefacto accidental y no fue reproducida.
- La sigla manual `M.V.P.` se normalizó como `MVP`.
- Se incorporaron portada institucional, páginas de aprobación y dedicatorias al generador.
- Se retiraron notas operativas de portada, índices y referencias, además de la sección obsoleta `N.5`.
- Se agregó `validate_thesis_source_sync.py` para detectar regresiones antes de una nueva generación.
- Se documentaron casos similares que requieren revisión selectiva: `.mmd`, IDs de tareas, commits, rutas, capturas versionadas y siglas técnicas.
- Una generación temporal produjo 145 páginas, 30.350 palabras y 50 tablas; Word exportó 145 páginas a PDF sin páginas vacías.
- Se revisaron visualmente portada, aprobación, dedicatorias e índice, y se confirmó que el hash de `rev018` no cambió.

Límites:

- no se sobrescribió ni se editó la candidata humana `rev018`;
- no se aplicó todavía la reducción de anexos J a N;
- no se generó una candidata entregable;
- LibreOffice headless no pudo convertir el archivo temporal y se utilizó Word como fallback controlado;
- `rev019` deberá producirse después de aprobar las decisiones de relevancia y pasar la validación visual completa.

## THESIS-REV019-RELEVANCE-INTEGRATION-001 - Reducción académica y candidata rev019

Fecha: 2026-08-03.

Estado: completado; decisiones de relevancia aplicadas y candidata revisada.

Resultado:

- Se aplicó la aprobación humana de `THESIS-RELEVANCE-AUDIT-001`.
- Los anexos J, K, M y N se redujeron de 30 a 16 páginas sin retirar el sustento central de la heurística.
- El Anexo L salió del manuscrito, pero su fuente editable se conservó como documentación operativa.
- El Gantt se trasladó junto a la Tabla 10 y se renumeró como Figura 1 para conservar el orden académico de aparición; las figuras anteriores pasaron a 2-20.
- Se generó `TenderApp-Tesis-Estandarizada-Anexos-Bibliografia-rev019.docx`.

Verificación:

- SHA-256 de `rev019`: `38C7825C3942514195184ACB437FAF2A0BBBD470C0F36BF4642CD7C3041250F4`;
- `rev018` preservada con SHA-256 `69037FC731A4771BB8C7740D9665E436C654D2661871EA28C8A66881CA2AA6CB`;
- 42 tablas, 20 recursos gráficos, 3 campos `TOC`, 35 campos `SEQ` y 3 secciones;
- 132 páginas según Word y 133 páginas en el render LibreOffice;
- ninguna página vacía y una página horizontal para el Gantt;
- revisión visual completa mediante nueve hojas de contacto y revisión ampliada de páginas críticas;
- validadores de sincronización y relevancia aprobados.

Límites:

- la línea base de 60 jornadas continúa como estimación académica reproducible, no como reconstrucción histórica exacta;
- su duración y fechas requieren aprobación académica;
- la precisión empírica de la heurística permanece como trabajo futuro;
- la siguiente revisión recomendada es capítulo por capítulo, sin ampliar nuevamente los anexos.

## THESIS-REV020-CHAPTER-POLISH-001 - Revisión del Capítulo 1

Fecha: 2026-08-04.

Estado: fuente revisada; aprobación humana y generación de `rev020` pendientes.

Resultado:

- Se contrastó la introducción con el anteproyecto, la baseline académica, APA 7 y el perfil de voz del autor.
- Se reforzaron contexto y justificación mediante literatura científica ya registrada.
- El objetivo general se reformuló alrededor de un MVP Android que produce recomendaciones explicables.
- La Tabla 1 quedó alineada con siete objetivos verificables del producto.
- Se distinguió validación funcional de calibración experimental.
- La metodología se presentó como desarrollo incremental con flujo Kanban adaptado, sin afirmar Scrum ni una aplicación formal completa.
- Se retiraron expresiones operativas internas que no aportaban a la defensa.

Verificación:

- aproximadamente 1780 palabras y ocho encabezados en el capítulo;
- cinco referencias existentes comprobadas;
- ausencia del enfoque documental anterior y de términos operativos impropios;
- `git diff --check` aprobado;
- `rev019` no fue regenerada ni sobrescrita.

Siguiente paso:

- revisión humana del Capítulo 1 y, después de su aprobación, revisión segmentada del Capítulo 2.

## THESIS-REV020-CHAPTER-POLISH-002 - Estado de la cuestión

Fecha: 2026-08-04.

Estado: fuente revisada; aprobación humana y generación de `rev020` pendientes.

Resultado:

- Se registró la aprobación humana del Capítulo 1.
- Los antecedentes se organizaron mediante tres líneas convergentes: información meteorológica, fundamentos del secado textil y asistencia digital doméstica.
- Se aclaró que la comparación es conceptual y no constituye estudio exhaustivo de mercado ni afirmación de novedad absoluta.
- La Tabla 3 separa alternativas de asistencia, automatización, arquitectura, modelo de decisión y propuesta.
- Se justificaron el backend REST y la heurística explicable como decisiones proporcionales al MVP.
- Se conservó como limitación la falta de calibración empírica del tiempo de secado.

Verificación:

- aproximadamente 930 palabras y dos apartados revisados;
- siete alternativas diferenciadas;
- cinco referencias canónicas comprobadas;
- `git diff --check` aprobado;
- `rev019` no fue regenerada ni sobrescrita.

Siguiente paso:

- aprobación humana de 2.1 y 2.2 y revisión temática del marco teórico 2.3.

## THESIS-REV020-PROFESSOR-CANDIDATE-001 - Candidata para revisión docente

Fecha: 2026-08-04.

Estado: completado; candidata estable generada y verificada.

Resultado:

- Se registró la aprobación humana del Capítulo 1 y de los apartados 2.1 y 2.2.
- El apartado 2.3 recibió una depuración conservadora para distinguir fundamento externo, decisiones del proyecto y evidencia de implementación.
- Se retiraron expresiones operativas impropias del marco teórico y se preservó el sustento científico de la heurística.
- Se corrigió una página vacía entre factibilidad y riesgos mediante un ajuste editorial del cierre de recursos humanos.
- Se generó `TenderApp-Tesis-Estandarizada-Anexos-Bibliografia-rev020.docx` sin modificar `rev019`.
- Se creó `professor-review-guide-rev020.md` para orientar la devolución académica.

Verificación:

- SHA-256 de `rev020`: `3EC82B1281CC303C5D36279D3444324F502ABEABD84BA78988AAE04736C2541D`;
- `rev019` preservada con SHA-256 `38C7825C3942514195184ACB437FAF2A0BBBD470C0F36BF4642CD7C3041250F4`;
- 133 páginas según Word y 134 páginas según LibreOffice;
- 42 tablas, 20 recursos gráficos, 146 encabezados semánticos, 3 campos `TOC` y 35 campos `SEQ`;
- render completo sin páginas vacías y con una página horizontal prevista para el Gantt;
- revisión visual representativa de índices, Capítulo 2, Gantt, referencias, diagramas, anexos y cierre;
- validadores de sincronización y relevancia aprobados;
- `git diff --check` aprobado.

Límite:

- los capítulos 3 a 7 y la extensión de los anexos no se reabrirán hasta recibir devolución docente;
- la precisión empírica de la heurística continúa declarada como trabajo futuro.

## PRED-TIMELINE-CONSISTENCY-001 - Consistencia temporal en Nueva carga

Fecha: 2026-08-10.

Estado: implementado y validado de forma automatizada; smoke visual en dispositivo pendiente.

Resultado:

- Se reprodujo una inconsistencia donde la estimación segmentada incluía clima anterior a la ventana recomendada para tender.
- La prueba inicial demostró una diferencia de 149 frente a 121 minutos para una misma ventana efectiva.
- El backend ahora integra el secado desde `recommendedHangAt`; el tiempo previo queda clasificado como espera.
- Nueva carga presenta lavado, espera cuando corresponde, secado y tiempo total estimado.
- Los textos fueron localizados en español e inglés y se aclaró la diferencia entre vista previa y plan completo.

Verificación:

- backend: 3 suites y 20 pruebas aprobadas;
- Android: pruebas de `NewLoadViewModel` y `RemotePredictionDataSource` aprobadas;
- Android: `assembleDebug` aprobado;
- no se modificaron datos de Supabase ni cargas existentes.

Pendiente:

- validar visualmente el desglose en un dispositivo, en español e inglés, cuando vuelva a estar disponible.

## THESIS-ANTEPROJECT-UCASAL-001 - Alineación reglamentaria del anteproyecto

Fecha: 2026-08-10.

Estado: completado; dato del profesor guía y confirmación administrativa pendientes.

Resultado:

- Se auditó `TenderApp-Anteproyecto-v1.docx` contra el artículo 3 del reglamento de trabajos finales de la Facultad de Ingeniería de UCASAL.
- Se preservó la versión v1 y se redactó una fuente Markdown actualizada, centrada exclusivamente en TenderApp.
- Se incorporaron profesor guía como pendiente visible, alcance funcional y técnico, institución de referencia, metodología, plan de 60 jornadas, riesgos, bibliografía y responsabilidad individual.
- Se generó `TenderApp-Anteproyecto-UCASAL-v2.docx` con estilos semánticos, índice automático, tablas académicas y formato conservador compatible con las orientaciones generales de UCASAL.
- Se registró una matriz de cumplimiento independiente y una referencia BibTeX del reglamento oficial.

Verificación:

- 12 páginas A4 renderizadas e inspeccionadas;
- dos tablas sin recortes ni desbordes;
- citas y referencias limitadas a fuentes reales;
- ausencia del sistema documental y de la asistencia con inteligencia artificial como objeto central;
- `git diff --check` aprobado al cierre de la tarea.

Pendientes:

- completar nombre, apellido y conformidad del profesor guía;
- confirmar carátula, nota de elevación y firmas requeridas por la cátedra;
- actualizar el índice si se introducen cambios manuales posteriores.

## THESIS-ADMIN-NOTE-001 - Solicitud de cambio de tema y anteproyecto

Fecha: 2026-08-10.

Estado: documento generado; datos administrativos y firmas pendientes.

Resultado:

- Se verificó que el Anexo II del reglamento de Ingeniería corresponde al cronograma y que el modelo institucional de nota se encuentra en el Anexo III.
- Se adaptó esa estructura para solicitar a la Decana y a la Comisión de Trabajos Finales el reemplazo del proyecto anterior por el anteproyecto vigente de TenderApp.
- La nota identifica ambos títulos, explica la reformulación de alcance, solicita autorización sin presumirla e indica el nuevo anteproyecto como adjunto.
- DNI, legajo, fecha y profesor guía quedaron resaltados para evitar la incorporación de datos inventados.
- Se generó `TenderApp-Nota-Solicitud-Cambio-Proyecto-UCASAL-v2.docx`, de una página, con Times New Roman 12, estructura formal y espacios de firma. La primera salida sin sufijo quedó preservada al detectarse abierta en Word y no constituye la entrega vigente.

Verificación:

- generador compilado;
- render PDF de una página;
- inspección visual completa sin recortes, superposiciones ni firmas aisladas;
- `git diff --check` aprobado al cierre de la tarea.

Pendiente:

- completar los datos resaltados y obtener las firmas o conformidades que indique la Facultad.

## 2026-08-25 - Alineación con anteproyecto presentado y evidencia v8

Tarea relacionada: `THESIS-ANTEPROJECT-UCASAL-002`.

Resultado:

- se contrastó el manuscrito con el anteproyecto presentado y se confirmó la estructura de cinco incrementos secuenciales en doce semanas;
- se mantuvo Kanban como apoyo operativo y se retiraron comparaciones residuales con Scrum del relato académico activo;
- se regeneró el Gantt en formato apto para página vertical desde su fuente CSV verificable;
- se incorporó la captura original v8 de Nueva carga, donde una alternativa distingue lavado, espera meteorológica y secado;
- se normalizó la numeración de 21 figuras y se eliminaron rótulos sueltos sin imagen del cuerpo.

Verificación:

- validadores de relevancia, sincronización, Mermaid y cronograma aprobados;
- DOCX con 869 párrafos, 42 tablas, 21 recursos gráficos, 3 campos `TOC`, 36 campos `SEQ` y 0 comentarios;
- PDF de 125 páginas renderizado mediante LibreOffice;
- Figura 1, Figura 2, referencias visuales y Anexo A inspeccionados en tamaño original;
- salida vigente: `TenderApp-Tesis-Estandarizada-Anexos-Bibliografia-rev026.docx`.

Pendientes:

- revisión y aprobación académica de la directora;
- ejecución documentada del piloto doméstico y de las pruebas con usuarios voluntarios.

## 2026-08-26 - Reintegración de evidencia funcional en el cuerpo

Tarea relacionada: `THESIS-REV027-RESULTS-FIGURES-001`.

Resultado:

- se preservó `rev026` como revisión humana y se trasladaron siete correcciones editoriales a la fuente canónica;
- las capturas del dashboard, nueva carga, historial, notificación push y plan de finalización se integraron en la sección 7.1;
- los diagramas de arquitectura y apoyo metodológico permanecieron en anexos;
- se normalizó la numeración consecutiva de 21 figuras y se regeneraron sus referencias internas;
- se generó `TenderApp-Tesis-Estandarizada-Anexos-Bibliografia-rev027.docx`.

Verificación:

- validador de relevancia aprobado;
- índice general, índice de figuras e índice de tablas actualizados en Word;
- 132 páginas renderizadas con LibreOffice y revisadas mediante hojas de contacto;
- Figuras 2 y 6 verificadas en tamaño original, sin recortes ni separación de sus notas;
- SHA-256 de `rev027`: `3C7620CEF79C9E7C991162747ED1D8BACC5526CE556C3B839E306DFDFF00E89A`.

Pendientes:

- revisión académica de la selección final de capturas;
- actualización manual de índices si el autor vuelve a editar el DOCX;
- ejecución y registro de las pruebas con usuarios voluntarios.

## 2026-08-30 - Revisión por observaciones de la profesora y depuración posterior a página 52

Tarea relacionada: `THESIS-REV028-PROFESSOR-FEEDBACK-001`.

Estado: implementación y QA documental completados; aprobación académica humana pendiente.

Resultado:

- se preservaron el documento comentado por la profesora y `rev027` como entradas sin sobrescritura;
- se incorporaron correcciones sobre evidencia prevista, tareas priorizadas, endpoints de estado, incrementos y planificación temporal;
- la inteligencia artificial generativa quedó declarada una sola vez como apoyo documental supervisado, sin desplazar el tema central de TenderApp;
- los diagramas necesarios para comprender la solución y las capturas funcionales principales se integraron en el cuerpo;
- el Anexo A quedó limitado a variantes visuales complementarias;
- se depuraron explicaciones equivalentes en desarrollo incremental, resultados, discusión y anexos;
- se generó `TenderApp-Tesis-Estandarizada-Anexos-Bibliografia-rev028.docx`.

Verificación:

- validadores de relevancia y sincronización aprobados;
- generador y validadores compilados correctamente;
- índices y campos de Word actualizados;
- 129 páginas renderizadas e inspeccionadas mediante once hojas de contacto;
- páginas 52 y 54 revisadas en tamaño original para verificar la continuidad del Gantt;
- SHA-256: `E2608377DB40B1378761E4264483BB1273C5240337D04F19DB2B2E57755D7098`.

Pendientes:

- confirmar con la profesora la ubicación y redacción institucional de la declaración sobre inteligencia artificial;
- completar y documentar las pruebas con usuarios voluntarios;
- tratar cualquier corrección manual posterior como una nueva baseline.
