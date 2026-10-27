package com.example.data.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.BreakfastSunrise
import com.example.ui.theme.DinnerSunset
import com.example.ui.theme.LunchSun
import com.example.ui.theme.SnackMint

enum class MealType(
    val displayName: String,
    val suggestedCalorieRatio: Float,
    val color: Color
) {
    BREAKFAST("Breakfast", 0.25f, BreakfastSunrise),
    LUNCH("Lunch", 0.35f, LunchSun),
    DINNER("Dinner", 0.30f, DinnerSunset),
    SNACKS("Snacks", 0.10f, SnackMint)
}
