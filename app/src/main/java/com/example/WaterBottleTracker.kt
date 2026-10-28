package com.example

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.HydrationBlue
import com.example.ui.theme.HydrationBlueLight
import kotlin.math.roundToInt

@Composable
fun WaterBottleTracker(
    waterMl: Int,
    targetWaterMl: Int,
    onAddWater: (Int) -> Unit,
    onResetWater: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progress = if (targetWaterMl > 0) (waterMl.toFloat() / targetWaterMl).coerceIn(0f, 1f) else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "water_fill"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("water_tracker_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(HydrationBlue.copy(alpha = 0.12f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.WaterDrop,
                            contentDescription = "Hydration",
                            tint = HydrationBlue,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Water Intake & Hydration",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Metabolic fluid & osmolarity target",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(
                    onClick = onResetWater,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reset water",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Bottle & Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Interactive animated water cylinder
                WaterBottleVisual(
                    fillRatio = animatedProgress,
                    modifier = Modifier
                        .width(64.dp)
                        .height(110.dp)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "$waterMl",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = HydrationBlue
                        )
                        Text(
                            text = " / $targetWaterMl ml",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 4.dp, start = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    val percent = (progress * 100).roundToInt()
                    val statusText = when {
                        percent >= 100 -> "Target Achieved! Optimal cellular state."
                        percent >= 70 -> "Great hydration flow! Almost at target."
                        percent >= 40 -> "Steady hydration. Keep sipping fluids."
                        else -> "Drink more water to fuel metabolism."
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = HydrationBlue.copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = "$percent% • $statusText",
                            style = MaterialTheme.typography.labelSmall,
                            color = HydrationBlue,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Quick Add Water Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                WaterQuickAddButton(
                    amount = 250,
                    label = "Glass",
                    iconDesc = "+250ml",
                    onClick = { onAddWater(250) },
                    modifier = Modifier.weight(1f)
                )
                WaterQuickAddButton(
                    amount = 500,
                    label = "Bottle",
                    iconDesc = "+500ml",
                    onClick = { onAddWater(500) },
                    modifier = Modifier.weight(1f)
                )
                WaterQuickAddButton(
                    amount = 750,
                    label = "Shaker",
                    iconDesc = "+750ml",
                    onClick = { onAddWater(750) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun WaterBottleVisual(
    fillRatio: Float,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val corner = 16.dp.toPx()

        // Outer glass container
        drawRoundRect(
            color = Color(0xFFE2E8F0),
            size = Size(width, height),
            cornerRadius = CornerRadius(corner, corner)
        )

        // Water Fill
        val fillHeight = height * fillRatio.coerceIn(0f, 1f)
        val fillTop = height - fillHeight

        drawRoundRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF60A5FA),
                    HydrationBlue
                ),
                startY = fillTop,
                endY = height
            ),
            topLeft = Offset(0f, fillTop),
            size = Size(width, fillHeight),
            cornerRadius = CornerRadius(corner, corner)
        )

        // Measurement tick marks
        val tickCount = 4
        for (i in 1 until tickCount) {
            val y = height * (i.toFloat() / tickCount)
            drawLine(
                color = Color.White.copy(alpha = 0.5f),
                start = Offset(width * 0.15f, y),
                end = Offset(width * 0.40f, y),
                strokeWidth = 2.dp.toPx()
            )
        }
    }
}

@Composable
private fun WaterQuickAddButton(
    amount: Int,
    label: String,
    iconDesc: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = HydrationBlue.copy(alpha = 0.08f),
        modifier = modifier.testTag("water_add_${amount}ml")
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "+$amount ml",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = HydrationBlue
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
