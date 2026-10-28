package com.example

import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey
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

@Entity(tableName = "meal_logs")
data class MealLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: String, // "YYYY-MM-DD"
    val mealType: String, // BREAKFAST, LUNCH, DINNER, SNACKS
    val foodName: String,
    val servingDesc: String,
    val calories: Int,
    val proteinG: Float,
    val carbsG: Float,
    val fatG: Float,
    val fiberG: Float = 0f,
    val isFavorite: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "weekly_meal_plans")
data class WeeklyMealPlan(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val dayOfWeek: Int, // 0 = Mon, 1 = Tue, 2 = Wed, 3 = Thu, 4 = Fri, 5 = Sat, 6 = Sun
    val mealType: String,
    val recipeName: String,
    val calories: Int,
    val proteinG: Float,
    val carbsG: Float,
    val fatG: Float,
    val ingredients: String // comma separated list
)

@Entity(tableName = "grocery_items")
data class GroceryItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val category: String, // "Produce", "Protein", "Dairy", "Grains", "Pantry"
    val quantity: String,
    val isChecked: Boolean = false,
    val sourceMeal: String = ""
)

@Entity(tableName = "water_logs")
data class WaterLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: String, // "YYYY-MM-DD"
    val amountMl: Int,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey
    val id: Int = 1,
    val age: Int = 28,
    val gender: String = "MALE", // MALE, FEMALE
    val weightKg: Float = 70.0f,
    val heightCm: Float = 175.0f,
    val activityLevel: String = "MODERATE", // SEDENTARY, LIGHT, MODERATE, ACTIVE, VERY_ACTIVE
    val goal: String = "MAINTAIN", // LOSE_FAT, MAINTAIN, BUILD_MUSCLE
    val targetCalories: Int = 2150,
    val targetProteinG: Float = 145f,
    val targetCarbsG: Float = 215f,
    val targetFatG: Float = 65f,
    val targetWaterMl: Int = 2750,
    val remindersEnabled: Boolean = true,
    val breakfastReminderTime: String = "08:00",
    val lunchReminderTime: String = "12:30",
    val dinnerReminderTime: String = "19:00",
    val hydrationReminderHours: Int = 2
)
