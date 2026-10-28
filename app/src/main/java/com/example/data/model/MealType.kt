package com.example.data.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.BreakfastSunrise
import com.example.ui.theme.DinnerSunset
import com.example.ui.theme.LunchSun
import com.example.ui.theme.SnackMint

enum class MealType(
    val displayName: String,
    val suggestedPercent: Int,
    val color: Color
) {
    BREAKFAST("Breakfast", 25, BreakfastSunrise),
    LUNCH("Lunch", 35, LunchSun),
    DINNER("Dinner", 30, DinnerSunset),
    SNACKS("Snacks", 10, SnackMint);

    companion object {
        fun fromString(value: String): MealType {
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: BREAKFAST
        }
    }
}
