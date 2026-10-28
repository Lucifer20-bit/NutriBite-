package com.example

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

enum class AppScreen(val label: String) {
    LOG("Log"),
    PLANNER("Planner"),
    GROCERY("Grocery"),
    WATER("Hydration"),
    DASHBOARD("Trends")
}

data class DailyNutritionSummary(
    val totalCalories: Int = 0,
    val targetCalories: Int = 2200,
    val remainingCalories: Int = 2200,
    val totalProteinG: Float = 0f,
    val targetProteinG: Float = 150f,
    val totalCarbsG: Float = 0f,
    val targetCarbsG: Float = 225f,
    val totalFatG: Float = 0f,
    val targetFatG: Float = 65f,
    val totalFiberG: Float = 0f,
    val waterMl: Int = 0,
    val targetWaterMl: Int = 2800,
    val calorieProgress: Float = 0f,
    val proteinProgress: Float = 0f,
    val carbsProgress: Float = 0f,
    val fatProgress: Float = 0f,
    val waterProgress: Float = 0f
)

class NutriBiteViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: NutriBiteRepository

    init {
        val database = NutriBiteDatabase.getDatabase(application, viewModelScope)
        repository = NutriBiteRepository(database.dao())
    }

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    private val _currentDate = MutableStateFlow(dateFormat.format(Date()))
    val currentDate: StateFlow<String> = _currentDate.asStateFlow()

    private val _currentScreen = MutableStateFlow(AppScreen.LOG)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    // Dialog & UI Visibility states
    val isAddMealDialogOpen = MutableStateFlow(false)
    val targetMealTypeForAdd = MutableStateFlow(MealType.BREAKFAST)
    val isBioCalculatorOpen = MutableStateFlow(false)
    val isAddGroceryDialogOpen = MutableStateFlow(false)
    val isAddPlanDialogOpen = MutableStateFlow(false)
    val selectedPlanDay = MutableStateFlow(0) // 0=Mon .. 6=Sun
    val showFavoritesOnly = MutableStateFlow(false)

    // User Profile
    val userProfile: StateFlow<UserProfile> = repository.getUserProfile()
        .combine(MutableStateFlow(Unit)) { profile, _ ->
            profile ?: UserProfile()
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserProfile()
        )

    // Daily Meal Logs for currently selected date
    val todayLogs: StateFlow<List<MealLog>> = _currentDate
        .flatMapLatest { date -> repository.getLogsForDate(date) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Favorites
    val favoriteMeals: StateFlow<List<MealLog>> = repository.getFavoriteMeals()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Weekly plans
    val weeklyPlans: StateFlow<List<WeeklyMealPlan>> = repository.getAllWeeklyPlans()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Grocery items
    val groceryItems: StateFlow<List<GroceryItem>> = repository.getAllGroceryItems()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Water Intake
    val waterMl: StateFlow<Int> = _currentDate
        .flatMapLatest { date -> repository.getTotalWaterForDate(date) }
        .combine(MutableStateFlow(Unit)) { amount, _ -> amount ?: 0 }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    // Recent Logs for 30-day Trends
    val recentLogs: StateFlow<List<MealLog>> = repository.getRecentLogs(150)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Dynamic Macro Summary calculation
    val nutritionSummary: StateFlow<DailyNutritionSummary> = combine(
        todayLogs,
        userProfile,
        waterMl
    ) { logs, profile, water ->
        var cals = 0
        var protein = 0f
        var carbs = 0f
        var fat = 0f
        var fiber = 0f

        for (log in logs) {
            cals += log.calories
            protein += log.proteinG
            carbs += log.carbsG
            fat += log.fatG
            fiber += log.fiberG
        }

        val targetCals = profile.targetCalories
        val targetProt = profile.targetProteinG
        val targetCarb = profile.targetCarbsG
        val targetFt = profile.targetFatG
        val targetWtr = profile.targetWaterMl

        DailyNutritionSummary(
            totalCalories = cals,
            targetCalories = targetCals,
            remainingCalories = targetCals - cals,
            totalProteinG = protein,
            targetProteinG = targetProt,
            totalCarbsG = carbs,
            targetCarbsG = targetCarb,
            totalFatG = fat,
            targetFatG = targetFt,
            totalFiberG = fiber,
            waterMl = water,
            targetWaterMl = targetWtr,
            calorieProgress = if (targetCals > 0) (cals.toFloat() / targetCals).coerceIn(0f, 1f) else 0f,
            proteinProgress = if (targetProt > 0f) (protein / targetProt).coerceIn(0f, 1f) else 0f,
            carbsProgress = if (targetCarb > 0f) (carbs / targetCarb).coerceIn(0f, 1f) else 0f,
            fatProgress = if (targetFt > 0f) (fat / targetFt).coerceIn(0f, 1f) else 0f,
            waterProgress = if (targetWtr > 0) (water.toFloat() / targetWtr).coerceIn(0f, 1f) else 0f
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DailyNutritionSummary()
    )

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun setDate(date: String) {
        _currentDate.value = date
    }

    fun stepDate(days: Int) {
        try {
            val calendar = Calendar.getInstance()
            dateFormat.parse(_currentDate.value)?.let {
                calendar.time = it
            }
            calendar.add(Calendar.DAY_OF_YEAR, days)
            _currentDate.value = dateFormat.format(calendar.time)
        } catch (_: Exception) {
            _currentDate.value = dateFormat.format(Date())
        }
    }

    fun setToday() {
        _currentDate.value = dateFormat.format(Date())
    }

    fun clearUserMessage() {
        _userMessage.value = null
    }

    // --- Actions ---

    fun openAddMeal(mealType: MealType) {
        targetMealTypeForAdd.value = mealType
        isAddMealDialogOpen.value = true
    }

    fun addMealLog(
        foodName: String,
        servingDesc: String,
        calories: Int,
        proteinG: Float,
        carbsG: Float,
        fatG: Float,
        fiberG: Float,
        mealType: MealType,
        isFavorite: Boolean = false
    ) {
        viewModelScope.launch {
            val log = MealLog(
                date = _currentDate.value,
                mealType = mealType.name,
                foodName = foodName.trim(),
                servingDesc = servingDesc.ifBlank { "1 serving" },
                calories = calories,
                proteinG = proteinG,
                carbsG = carbsG,
                fatG = fatG,
                fiberG = fiberG,
                isFavorite = isFavorite
            )
            repository.addMealLog(log)
            isAddMealDialogOpen.value = false
            _userMessage.value = "Added $foodName to ${mealType.displayName}"
        }
    }

    fun logFavoriteMealDirectly(favorite: MealLog, targetMealType: MealType) {
        viewModelScope.launch {
            val copy = MealLog(
                date = _currentDate.value,
                mealType = targetMealType.name,
                foodName = favorite.foodName,
                servingDesc = favorite.servingDesc,
                calories = favorite.calories,
                proteinG = favorite.proteinG,
                carbsG = favorite.carbsG,
                fatG = favorite.fatG,
                fiberG = favorite.fiberG,
                isFavorite = true
            )
            repository.addMealLog(copy)
            _userMessage.value = "Logged favorite: ${favorite.foodName}"
        }
    }

    fun deleteMealLog(id: Long) {
        viewModelScope.launch {
            repository.deleteMealLog(id)
            _userMessage.value = "Meal deleted"
        }
    }

    fun toggleFavorite(id: Long, isFavorite: Boolean) {
        viewModelScope.launch {
            repository.toggleFavorite(id, !isFavorite)
            _userMessage.value = if (!isFavorite) "Saved to favorites" else "Removed from favorites"
        }
    }

    // Water actions
    fun addWater(amountMl: Int) {
        viewModelScope.launch {
            repository.addWater(_currentDate.value, amountMl)
            _userMessage.value = "+$amountMl ml water logged"
        }
    }

    fun resetWaterToday() {
        viewModelScope.launch {
            repository.resetWaterForDate(_currentDate.value)
            _userMessage.value = "Water log reset"
        }
    }

    // Weekly Plan Actions
    fun addWeeklyPlan(
        dayOfWeek: Int,
        mealType: MealType,
        recipeName: String,
        calories: Int,
        proteinG: Float,
        carbsG: Float,
        fatG: Float,
        ingredients: String
    ) {
        viewModelScope.launch {
            val plan = WeeklyMealPlan(
                dayOfWeek = dayOfWeek,
                mealType = mealType.name,
                recipeName = recipeName.trim(),
                calories = calories,
                proteinG = proteinG,
                carbsG = carbsG,
                fatG = fatG,
                ingredients = ingredients.trim()
            )
            repository.saveWeeklyPlan(plan)
            isAddPlanDialogOpen.value = false
            _userMessage.value = "Added to weekly plan"
        }
    }

    fun deleteWeeklyPlan(id: Long) {
        viewModelScope.launch {
            repository.deleteWeeklyPlan(id)
            _userMessage.value = "Meal plan removed"
        }
    }

    fun generateGroceryList() {
        viewModelScope.launch {
            val count = repository.generateGroceryFromWeeklyPlans()
            if (count > 0) {
                _userMessage.value = "Generated $count grocery items from weekly meal plans!"
                _currentScreen.value = AppScreen.GROCERY
            } else {
                _userMessage.value = "No ingredients found in your meal plans yet."
            }
        }
    }

    // Grocery actions
    fun toggleGroceryItem(id: Long, isChecked: Boolean) {
        viewModelScope.launch {
            repository.toggleGroceryItem(id, !isChecked)
        }
    }

    fun deleteGroceryItem(id: Long) {
        viewModelScope.launch {
            repository.deleteGroceryItem(id)
            _userMessage.value = "Item removed"
        }
    }

    fun addCustomGrocery(name: String, category: String, quantity: String) {
        viewModelScope.launch {
            repository.addGroceryItem(
                GroceryItem(
                    name = name.trim(),
                    category = category.ifBlank { "Produce" },
                    quantity = quantity.ifBlank { "1 item" },
                    isChecked = false
                )
            )
            isAddGroceryDialogOpen.value = false
            _userMessage.value = "Added $name to checklist"
        }
    }

    fun clearCheckedGroceries() {
        viewModelScope.launch {
            repository.clearCheckedGroceryItems()
            _userMessage.value = "Completed items cleared"
        }
    }

    fun clearAllGroceries() {
        viewModelScope.launch {
            repository.clearAllGroceryItems()
            _userMessage.value = "Grocery list cleared"
        }
    }

    // Bioscience Profile Update
    fun applyBioscienceTargets(
        age: Int,
        gender: String,
        weightKg: Float,
        heightCm: Float,
        activityLevel: String,
        goal: String
    ) {
        viewModelScope.launch {
            val targets = NutriBiteBioscience.computeOptimalTargets(
                weightKg = weightKg,
                heightCm = heightCm,
                age = age,
                gender = gender,
                activityLevel = activityLevel,
                goal = goal
            )

            val updated = userProfile.value.copy(
                age = age,
                gender = gender,
                weightKg = weightKg,
                heightCm = heightCm,
                activityLevel = activityLevel,
                goal = goal,
                targetCalories = targets.calories,
                targetProteinG = targets.proteinG,
                targetCarbsG = targets.carbsG,
                targetFatG = targets.fatG,
                targetWaterMl = targets.waterMl
            )
            repository.saveUserProfile(updated)
            isBioCalculatorOpen.value = false
            _userMessage.value = "Calculated TDEE (${targets.tdee} kcal) & updated macro targets!"
        }
    }

    fun updateReminders(
        enabled: Boolean,
        breakfast: String,
        lunch: String,
        dinner: String,
        hydrationInterval: Int
    ) {
        viewModelScope.launch {
            val updated = userProfile.value.copy(
                remindersEnabled = enabled,
                breakfastReminderTime = breakfast,
                lunchReminderTime = lunch,
                dinnerReminderTime = dinner,
                hydrationReminderHours = hydrationInterval
            )
            repository.saveUserProfile(updated)
            _userMessage.value = "Reminder schedules updated"
        }
    }
}
