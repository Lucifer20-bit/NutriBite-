package com.example

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CarbsAmber
import com.example.ui.theme.CarbsAmberLight
import com.example.ui.theme.FatsTeal
import com.example.ui.theme.FatsTealLight
import com.example.ui.theme.FiberPurple
import com.example.ui.theme.MintGreenPrimary
import com.example.ui.theme.ProteinCoral
import com.example.ui.theme.ProteinCoralLight
import kotlin.math.roundToInt

@Composable
fun MacroProgressRings(
    summary: DailyNutritionSummary,
    onOpenBioCalculator: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("macro_progress_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header with Bioscience badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Daily Bio-Targets",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Real-time macronutrient balance",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    onClick = onOpenBioCalculator,
                    shape = RoundedCornerShape(12.dp),
                    color = MintGreenPrimary.copy(alpha = 0.12f),
                    modifier = Modifier.testTag("open_bio_calc_btn")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Optimize",
                            tint = MintGreenPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Bio-TDEE",
                            style = MaterialTheme.typography.labelSmall,
                            color = MintGreenPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Multi-Arc Visual Progress Rings with Canvas
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(200.dp)
            ) {
                ConcentricRingsCanvas(
                    calorieProgress = summary.calorieProgress,
                    proteinProgress = summary.proteinProgress,
                    carbsProgress = summary.carbsProgress,
                    fatProgress = summary.fatProgress,
                    modifier = Modifier.size(200.dp)
                )

                // Central Metrics
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val remaining = summary.remainingCalories
                    Text(
                        text = "${remaining.coerceAtLeast(0)}",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (remaining >= 0) "kcal left" else "kcal over",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (remaining >= 0) MaterialTheme.colorScheme.onSurfaceVariant else ProteinCoral,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${summary.totalCalories} / ${summary.targetCalories} kcal",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // Macro details row (Protein, Carbs, Fats, Fiber)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MacroPill(
                    label = "Protein",
                    current = summary.totalProteinG,
                    target = summary.targetProteinG,
                    color = ProteinCoral,
                    bgColor = ProteinCoralLight,
                    modifier = Modifier.weight(1f)
                )
                MacroPill(
                    label = "Carbs",
                    current = summary.totalCarbsG,
                    target = summary.targetCarbsG,
                    color = CarbsAmber,
                    bgColor = CarbsAmberLight,
                    modifier = Modifier.weight(1f)
                )
                MacroPill(
                    label = "Fats",
                    current = summary.totalFatG,
                    target = summary.targetFatG,
                    color = FatsTeal,
                    bgColor = FatsTealLight,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Fiber & Hydration Quick Strip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(FiberPurple, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Fiber: ${summary.totalFiberG.roundToInt()}g (Goal: 30g)",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = "💧 ${summary.waterMl} / ${summary.targetWaterMl} ml",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun ConcentricRingsCanvas(
    calorieProgress: Float,
    proteinProgress: Float,
    carbsProgress: Float,
    fatProgress: Float,
    modifier: Modifier = Modifier
) {
    val animatedCals by animateFloatAsState(
        targetValue = calorieProgress,
        animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing),
        label = "cals"
    )
    val animatedProtein by animateFloatAsState(
        targetValue = proteinProgress,
        animationSpec = tween(durationMillis = 900, delayMillis = 150, easing = FastOutSlowInEasing),
        label = "protein"
    )
    val animatedCarbs by animateFloatAsState(
        targetValue = carbsProgress,
        animationSpec = tween(durationMillis = 900, delayMillis = 300, easing = FastOutSlowInEasing),
        label = "carbs"
    )
    val animatedFat by animateFloatAsState(
        targetValue = fatProgress,
        animationSpec = tween(durationMillis = 900, delayMillis = 450, easing = FastOutSlowInEasing),
        label = "fat"
    )

    Canvas(modifier = modifier) {
        val strokeWidth = 9.dp.toPx()
        val spacing = 12.dp.toPx()

        // Ring 1: Calories (Outer)
        val r1 = (size.minDimension / 2f) - (strokeWidth / 2f)
        drawCircle(
            color = MintGreenPrimary.copy(alpha = 0.15f),
            radius = r1,
            style = Stroke(width = strokeWidth)
        )
        drawArc(
            color = MintGreenPrimary,
            startAngle = -90f,
            sweepAngle = animatedCals * 360f,
            useCenter = false,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )

        // Ring 2: Protein (Middle-Outer)
        val r2 = r1 - spacing
        drawCircle(
            color = ProteinCoral.copy(alpha = 0.15f),
            radius = r2,
            style = Stroke(width = strokeWidth)
        )
        drawArc(
            color = ProteinCoral,
            startAngle = -90f,
            sweepAngle = animatedProtein * 360f,
            useCenter = false,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )

        // Ring 3: Carbs (Middle-Inner)
        val r3 = r2 - spacing
        drawCircle(
            color = CarbsAmber.copy(alpha = 0.15f),
            radius = r3,
            style = Stroke(width = strokeWidth)
        )
        drawArc(
            color = CarbsAmber,
            startAngle = -90f,
            sweepAngle = animatedCarbs * 360f,
            useCenter = false,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )

        // Ring 4: Fat (Inner)
        val r4 = r3 - spacing
        drawCircle(
            color = FatsTeal.copy(alpha = 0.15f),
            radius = r4,
            style = Stroke(width = strokeWidth)
        )
        drawArc(
            color = FatsTeal,
            startAngle = -90f,
            sweepAngle = animatedFat * 360f,
            useCenter = false,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
    }
}

@Composable
private fun MacroPill(
    label: String,
    current: Float,
    target: Float,
    color: Color,
    bgColor: Color,
    modifier: Modifier = Modifier
) {
    val progress = if (target > 0f) (current / target).coerceIn(0f, 1f) else 0f

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(color, CircleShape)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "${current.roundToInt()}g",
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "of ${target.roundToInt()}g",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(6.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = color,
                trackColor = color.copy(alpha = 0.2f)
            )
        }
    }
}
