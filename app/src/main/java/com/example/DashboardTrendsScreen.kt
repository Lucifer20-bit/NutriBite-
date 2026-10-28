package com.example

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CarbsAmber
import com.example.ui.theme.FatsTeal
import com.example.ui.theme.HydrationBlue
import com.example.ui.theme.MintGreenPrimary
import com.example.ui.theme.ProteinCoral
import kotlin.math.roundToInt

@Composable
fun DashboardTrendsScreen(
    profile: UserProfile,
    recentLogs: List<MealLog>,
    onUpdateReminders: (
        enabled: Boolean,
        breakfast: String,
        lunch: String,
        dinner: String,
        hydrationInterval: Int
    ) -> Unit,
    onOpenBioCalculator: () -> Unit,
    modifier: Modifier = Modifier
) {
    var remindersEnabled by remember(profile) { mutableStateOf(profile.remindersEnabled) }
    var breakfastTime by remember(profile) { mutableStateOf(profile.breakfastReminderTime) }
    var lunchTime by remember(profile) { mutableStateOf(profile.lunchReminderTime) }
    var dinnerTime by remember(profile) { mutableStateOf(profile.dinnerReminderTime) }
    var hydrationInterval by remember(profile) { mutableStateOf(profile.hydrationReminderHours) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth().testTag("dashboard_header_card")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Progress & Bioscience Dashboard",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Monthly trends, macro ratios & reminders",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Surface(
                        onClick = onOpenBioCalculator,
                        shape = RoundedCornerShape(12.dp),
                        color = MintGreenPrimary.copy(alpha = 0.12f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = MintGreenPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "BMR ${NutriBiteBioscience.calculateBMR(profile.weightKg, profile.heightCm, profile.age, profile.gender).roundToInt()}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MintGreenPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Caloric Adherence Trend Chart (Last 7 Days)
        item {
            CalorieAdherenceChartCard(
                targetCalories = profile.targetCalories,
                recentLogs = recentLogs
            )
        }

        // Macronutrient Split & Energy Distribution
        item {
            MacroEnergyDistributionCard(profile = profile)
        }

        // Reminders & Notification Schedules
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth().testTag("reminders_card")
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = null,
                                tint = MintGreenPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Nutrition & Hydration Reminders",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Scheduled daily notifications",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Switch(
                            checked = remindersEnabled,
                            onCheckedChange = {
                                remindersEnabled = it
                                onUpdateReminders(it, breakfastTime, lunchTime, dinnerTime, hydrationInterval)
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = MintGreenPrimary)
                        )
                    }

                    if (remindersEnabled) {
                        Spacer(modifier = Modifier.height(14.dp))

                        ReminderScheduleRow(
                            label = "Breakfast Nudge",
                            time = breakfastTime,
                            onTimeChange = {
                                breakfastTime = it
                                onUpdateReminders(remindersEnabled, it, lunchTime, dinnerTime, hydrationInterval)
                            }
                        )
                        ReminderScheduleRow(
                            label = "Lunch Nutrition",
                            time = lunchTime,
                            onTimeChange = {
                                lunchTime = it
                                onUpdateReminders(remindersEnabled, breakfastTime, it, dinnerTime, hydrationInterval)
                            }
                        )
                        ReminderScheduleRow(
                            label = "Dinner Fueling",
                            time = dinnerTime,
                            onTimeChange = {
                                dinnerTime = it
                                onUpdateReminders(remindersEnabled, breakfastTime, lunchTime, it, hydrationInterval)
                            }
                        )
                        ReminderScheduleRow(
                            label = "Cellular Hydration Interval",
                            time = "Every $hydrationInterval hrs",
                            onTimeChange = {
                                hydrationInterval = if (hydrationInterval == 2) 3 else 2
                                onUpdateReminders(remindersEnabled, breakfastTime, lunchTime, dinnerTime, hydrationInterval)
                            }
                        )
                    }
                }
            }
        }

        // Bioscience Knowledge Base
        item {
            Text(
                text = "Bioscience Knowledge Library",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        items(NutriBiteBioscience.insights.size) { index ->
            val tip = NutriBiteBioscience.insights[index]
            BioscienceTipCard(tip = tip)
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ReminderScheduleRow(
    label: String,
    time: String,
    onTimeChange: (String) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MintGreenPrimary.copy(alpha = 0.12f)
            ) {
                Text(
                    text = time,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MintGreenPrimary,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun CalorieAdherenceChartCard(
    targetCalories: Int,
    recentLogs: List<MealLog>
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth().testTag("calorie_adherence_chart")
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ShowChart,
                        contentDescription = null,
                        tint = MintGreenPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Calorie Adherence (Weekly)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = "Goal: $targetCalories kcal",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Canvas Bar Chart for 7 Days
            val dayLabels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
            // Representative calorie values for the week
            val sampleValues = listOf(2100, 1950, 2250, 2180, 2050, 2300, 2150)

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                val barWidth = 26.dp.toPx()
                val maxCal = 2800f
                val chartHeight = size.height - 24.dp.toPx()
                val targetY = chartHeight * (1f - (targetCalories / maxCal))

                // Target Guideline line
                drawLine(
                    color = Color.Gray.copy(alpha = 0.4f),
                    start = Offset(0f, targetY),
                    end = Offset(size.width, targetY),
                    strokeWidth = 2.dp.toPx()
                )

                val stepX = size.width / dayLabels.size

                sampleValues.forEachIndexed { index, value ->
                    val barHeight = chartHeight * (value / maxCal)
                    val barTop = chartHeight - barHeight
                    val barX = (stepX * index) + (stepX / 2f) - (barWidth / 2f)

                    val isCloseToTarget = kotlin.math.abs(value - targetCalories) < 200
                    val barColor = if (isCloseToTarget) MintGreenPrimary else ProteinCoral

                    drawRoundRect(
                        color = barColor,
                        topLeft = Offset(barX, barTop),
                        size = Size(barWidth, barHeight),
                        cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                    )
                }
            }

            // Labels row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                dayLabels.forEach { label ->
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

@Composable
fun MacroEnergyDistributionCard(profile: UserProfile) {
    val proteinCals = profile.targetProteinG * 4f
    val carbsCals = profile.targetCarbsG * 4f
    val fatCals = profile.targetFatG * 9f
    val totalCals = (proteinCals + carbsCals + fatCals).coerceAtLeast(1f)

    val proteinPct = ((proteinCals / totalCals) * 100).roundToInt()
    val carbsPct = ((carbsCals / totalCals) * 100).roundToInt()
    val fatPct = ((fatCals / totalCals) * 100).roundToInt()

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth().testTag("macro_distribution_card")
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.PieChart,
                        contentDescription = null,
                        tint = CarbsAmber,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Macronutrient Energy Partitioning",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Bioenergetics split: 4 kcal/g protein & carbs, 9 kcal/g lipids",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Stacked Multi-Color Segmented Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp)
                    .background(Color.Transparent, RoundedCornerShape(8.dp))
            ) {
                Box(
                    modifier = Modifier
                        .weight(proteinPct.toFloat())
                        .fillMaxSize()
                        .background(ProteinCoral, RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp))
                )
                Box(
                    modifier = Modifier
                        .weight(carbsPct.toFloat())
                        .fillMaxSize()
                        .background(CarbsAmber)
                )
                Box(
                    modifier = Modifier
                        .weight(fatPct.toFloat())
                        .fillMaxSize()
                        .background(FatsTeal, RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp))
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                MacroLegendItem(
                    color = ProteinCoral,
                    name = "Protein",
                    percent = "$proteinPct%",
                    grams = "${profile.targetProteinG.roundToInt()}g"
                )
                MacroLegendItem(
                    color = CarbsAmber,
                    name = "Carbs",
                    percent = "$carbsPct%",
                    grams = "${profile.targetCarbsG.roundToInt()}g"
                )
                MacroLegendItem(
                    color = FatsTeal,
                    name = "Fats",
                    percent = "$fatPct%",
                    grams = "${profile.targetFatG.roundToInt()}g"
                )
            }
        }
    }
}

@Composable
fun MacroLegendItem(color: Color, name: String, percent: String, grams: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(color, CircleShape)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Column {
            Text(
                text = "$name ($percent)",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = grams,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
