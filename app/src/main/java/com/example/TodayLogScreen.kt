package com.example

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MintGreenPrimary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TodayLogScreen(
    currentDate: String,
    summary: DailyNutritionSummary,
    mealLogs: List<MealLog>,
    onStepDate: (Int) -> Unit,
    onSetToday: () -> Unit,
    onOpenAddMeal: (MealType) -> Unit,
    onDeleteMeal: (Long) -> Unit,
    onToggleFavorite: (Long, Boolean) -> Unit,
    onOpenBioCalculator: () -> Unit,
    onAddWater: (Int) -> Unit,
    onResetWater: () -> Unit,
    onNavigateToFavorites: () -> Unit,
    modifier: Modifier = Modifier
) {
    val todayDate = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }
    val isToday = currentDate == todayDate

    val breakfastLogs = mealLogs.filter { it.mealType.equals("BREAKFAST", ignoreCase = true) }
    val lunchLogs = mealLogs.filter { it.mealType.equals("LUNCH", ignoreCase = true) }
    val dinnerLogs = mealLogs.filter { it.mealType.equals("DINNER", ignoreCase = true) }
    val snackLogs = mealLogs.filter { it.mealType.equals("SNACKS", ignoreCase = true) }

    // Pick a daily bioscience tip based on date hash
    val dailyTip = remember(currentDate) {
        val index = (currentDate.hashCode() and 0x7FFFFFFF) % NutriBiteBioscience.insights.size
        NutriBiteBioscience.insights[index]
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Date Navigation Bar
        item {
            DateSelectorBar(
                currentDate = currentDate,
                isToday = isToday,
                onPrev = { onStepDate(-1) },
                onNext = { onStepDate(1) },
                onToday = onSetToday,
                onFavorites = onNavigateToFavorites
            )
        }

        // Dynamic Visual Macro Progress Rings Card
        item {
            MacroProgressRings(
                summary = summary,
                onOpenBioCalculator = onOpenBioCalculator
            )
        }

        // Daily Bioscience Fact Card
        item {
            BioscienceTipCard(tip = dailyTip)
        }

        // Meal Categories
        item {
            MealCategoryCard(
                mealType = MealType.BREAKFAST,
                items = breakfastLogs,
                onAddFoodClick = { onOpenAddMeal(MealType.BREAKFAST) },
                onDeleteMeal = onDeleteMeal,
                onToggleFavorite = onToggleFavorite
            )
        }

        item {
            MealCategoryCard(
                mealType = MealType.LUNCH,
                items = lunchLogs,
                onAddFoodClick = { onOpenAddMeal(MealType.LUNCH) },
                onDeleteMeal = onDeleteMeal,
                onToggleFavorite = onToggleFavorite
            )
        }

        item {
            MealCategoryCard(
                mealType = MealType.DINNER,
                items = dinnerLogs,
                onAddFoodClick = { onOpenAddMeal(MealType.DINNER) },
                onDeleteMeal = onDeleteMeal,
                onToggleFavorite = onToggleFavorite
            )
        }

        item {
            MealCategoryCard(
                mealType = MealType.SNACKS,
                items = snackLogs,
                onAddFoodClick = { onOpenAddMeal(MealType.SNACKS) },
                onDeleteMeal = onDeleteMeal,
                onToggleFavorite = onToggleFavorite
            )
        }

        // Dedicated Water Intake Logger Card
        item {
            WaterBottleTracker(
                waterMl = summary.waterMl,
                targetWaterMl = summary.targetWaterMl,
                onAddWater = onAddWater,
                onResetWater = onResetWater
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun DateSelectorBar(
    currentDate: String,
    isToday: Boolean,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    onToday: () -> Unit,
    onFavorites: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth().testTag("date_selector_bar")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onPrev, modifier = Modifier.size(40.dp)) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Previous Day",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { onToday() }
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = if (isToday) MintGreenPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isToday) "Today ($currentDate)" else currentDate,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isToday) MintGreenPrimary else MaterialTheme.colorScheme.onSurface
                    )
                }
                if (!isToday) {
                    Text(
                        text = "Tap to return to today",
                        style = MaterialTheme.typography.labelSmall,
                        color = MintGreenPrimary
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onFavorites, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Favorites",
                        tint = com.example.ui.theme.CarbsAmber,
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(onClick = onNext, modifier = Modifier.size(40.dp)) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Next Day",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
fun BioscienceTipCard(tip: BioscienceInsight) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MintGreenPrimary.copy(alpha = 0.08f)
        ),
        modifier = Modifier.fillMaxWidth().testTag("bioscience_tip_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Science,
                        contentDescription = null,
                        tint = MintGreenPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = tip.tag,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MintGreenPrimary
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MintGreenPrimary.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = tip.category,
                        style = MaterialTheme.typography.labelSmall,
                        color = MintGreenPrimary,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = tip.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = tip.summary,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "💡 Science takeaway: ${tip.recommendation}",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
