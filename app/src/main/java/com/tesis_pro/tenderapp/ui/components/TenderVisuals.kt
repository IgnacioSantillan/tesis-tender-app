package com.tesis_pro.tenderapp.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

/**
 * TenderApp — piezas visuales reutilizables.
 *
 * Íconos meteorológicos y de métrica dibujados con Canvas (sin dependencias de
 * icon packs) + tiles, pills y barras de progreso alineados a los tokens del tema.
 */

// ---------------------------------------------------------------------------
// Íconos de clima (dominantes en el veredicto)
// ---------------------------------------------------------------------------

enum class GlyphKind { SUN, CLOUD, RAIN }

@Composable
fun WeatherGlyph(
    kind: GlyphKind,
    tint: Color,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = w * 0.055f
        when (kind) {
            GlyphKind.SUN -> {
                val center = Offset(w / 2f, h / 2f)
                val r = w * 0.19f
                drawCircle(tint, r, center)
                for (i in 0 until 8) {
                    val a = (Math.PI / 4.0 * i).toFloat()
                    val inR = r * 1.55f
                    val outR = r * 2.15f
                    drawLine(
                        tint,
                        Offset(center.x + cos(a) * inR, center.y + sin(a) * inR),
                        Offset(center.x + cos(a) * outR, center.y + sin(a) * outR),
                        strokeWidth = stroke,
                        cap = StrokeCap.Round,
                    )
                }
            }

            GlyphKind.CLOUD -> drawCloud(tint, w, h)

            GlyphKind.RAIN -> {
                drawCloud(tint, w, h, yShift = -h * 0.08f)
                val top = h * 0.70f
                listOf(0.38f, 0.52f, 0.66f).forEach { fx ->
                    drawLine(
                        tint,
                        Offset(w * fx, top),
                        Offset(w * (fx - 0.05f), top + h * 0.16f),
                        strokeWidth = stroke,
                        cap = StrokeCap.Round,
                    )
                }
            }
        }
    }
}

private fun DrawScope.drawCloud(tint: Color, w: Float, h: Float, yShift: Float = 0f) {
    val cy = h * 0.55f + yShift
    drawRoundRect(
        tint,
        topLeft = Offset(w * 0.22f, cy - h * 0.02f),
        size = Size(w * 0.56f, h * 0.22f),
        cornerRadius = CornerRadius(h * 0.11f),
    )
    drawCircle(tint, w * 0.14f, Offset(w * 0.42f, cy - h * 0.04f))
    drawCircle(tint, w * 0.17f, Offset(w * 0.60f, cy - h * 0.09f))
    drawCircle(tint, w * 0.12f, Offset(w * 0.70f, cy))
}

// ---------------------------------------------------------------------------
// Íconos de métrica (temperatura / humedad / viento)
// ---------------------------------------------------------------------------

enum class MetricKind { TEMP, HUMIDITY, WIND }

@Composable
fun MetricIcon(
    kind: MetricKind,
    tint: Color,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = w * 0.11f
        when (kind) {
            MetricKind.TEMP -> {
                val cx = w * 0.5f
                drawRoundRect(
                    tint,
                    topLeft = Offset(cx - w * 0.09f, h * 0.10f),
                    size = Size(w * 0.18f, h * 0.52f),
                    cornerRadius = CornerRadius(w * 0.09f),
                    style = Stroke(stroke),
                )
                drawCircle(tint, w * 0.16f, Offset(cx, h * 0.74f))
            }

            MetricKind.HUMIDITY -> {
                val cx = w * 0.5f
                val top = h * 0.16f
                val bottom = h * 0.84f
                val rx = w * 0.30f
                val path = Path().apply {
                    moveTo(cx, top)
                    cubicTo(cx + rx, top + (bottom - top) * 0.45f, cx + rx * 0.9f, bottom, cx, bottom)
                    cubicTo(cx - rx * 0.9f, bottom, cx - rx, top + (bottom - top) * 0.45f, cx, top)
                    close()
                }
                drawPath(path, tint)
            }

            MetricKind.WIND -> {
                drawLine(tint, Offset(w * 0.12f, h * 0.30f), Offset(w * 0.74f, h * 0.30f), stroke, cap = StrokeCap.Round)
                drawLine(tint, Offset(w * 0.12f, h * 0.50f), Offset(w * 0.88f, h * 0.50f), stroke, cap = StrokeCap.Round)
                drawLine(tint, Offset(w * 0.12f, h * 0.70f), Offset(w * 0.58f, h * 0.70f), stroke, cap = StrokeCap.Round)
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Componentes compuestos
// ---------------------------------------------------------------------------

/** Celda de métrica: ícono + valor + etiqueta, sobre superficie clara. */
@Composable
fun MetricTile(
    kind: MetricKind,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            MetricIcon(kind, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

/** Pastilla tonal con punto opcional (para estados / etiquetas). */
@Composable
fun TonalPill(
    text: String,
    container: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
    showDot: Boolean = false,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(999.dp),
        color = container,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            if (showDot) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(contentColor),
                )
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                color = contentColor,
            )
        }
    }
}

/** Barra de progreso redondeada (score de secado / avance de carga). */
@Composable
fun StatBar(
    progress: Float,
    color: Color,
    trackColor: Color,
    modifier: Modifier = Modifier,
    height: Dp = 8.dp,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(999.dp))
            .background(trackColor),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .height(height)
                .clip(RoundedCornerShape(999.dp))
                .background(color),
        )
    }
}
