# Evidencia visual v8 - plan de finalización

## Propósito

Registrar en un dispositivo Android real la corrección `PRED-TIMELINE-CONSISTENCY-001`, que divide el plan de finalización de Nueva carga en tres tramos comprensibles:

1. tiempo de lavado;
2. espera hasta el comienzo de la ventana meteorológica recomendada, cuando corresponda;
3. tiempo estimado de secado.

## Captura requerida

| Archivo previsto | Pantalla y estado | Evidencia esperada | Estado |
|---|---|---|---|
| `01-v8-nueva-carga-lavado-espera-secado.png` | Nueva carga, plan de finalización expandido | La alternativa Normal muestra `Lavado 1 h 0 min + espera 30 min + secado 10 h 35 min`, junto con hora estimada y demora respecto del objetivo. | Captura original persistida y revisada visualmente el 25-08-2026. |

## Criterios de captura

- Utilizar idioma español y tema claro para mantener consistencia con las figuras principales.
- Seleccionar una hora y condiciones que produzcan una espera mayor que cero; no modificar manualmente la respuesta del backend.
- Mostrar el encabezado de Nueva carga y el bloque completo del plan, sin cortar el desglose temporal.
- Ocultar correos, tokens, coordenadas exactas y otros datos personales.
- Registrar fecha, dispositivo y resultado del recorrido en `tesis-final/evidence/increment-log.md`.

## Epígrafe APA propuesto

**Figura 2.** *Plan de finalización dividido en lavado, espera meteorológica y secado.*

**Nota.** Captura de pantalla de TenderApp correspondiente al plan de finalización de una carga. La interfaz distingue el tiempo de lavado, la espera hasta una ventana meteorológica conveniente y el secado estimado antes de informar la hora de finalización. Fuente: elaboración propia.

La fuente del manuscrito y el generador integran esta evidencia como Figura 2. El archivo fue recuperado sin transformaciones desde la imagen original aportada por el autor y no contiene datos personales visibles.


