package com.example

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class NutriBiteRepository(private val dao: NutriBiteDao) {

    fun getLogsForDate(date: String): Flow<List<MealLog>> = dao.getLogsForDate(date)

    fun getFavoriteMeals(): Flow<List<MealLog>> = dao.getFavoriteMeals()

    fun getRecentLogs(limit: Int = 120): Flow<List<MealLog>> = dao.getRecentLogs(limit)

    suspend fun addMealLog(log: MealLog): Long = dao.insertMealLog(log)

    suspend fun updateMealLog(log: MealLog) = dao.updateMealLog(log)

    suspend fun deleteMealLog(id: Long) = dao.deleteMealLog(id)

    suspend fun toggleFavorite(id: Long, isFavorite: Boolean) = dao.setMealFavorite(id, isFavorite)

    // Weekly Plan
    fun getAllWeeklyPlans(): Flow<List<WeeklyMealPlan>> = dao.getAllWeeklyPlans()

    suspend fun saveWeeklyPlan(plan: WeeklyMealPlan) = dao.insertPlan(plan)

    suspend fun deleteWeeklyPlan(id: Long) = dao.deletePlan(id)

    // Grocery items
    fun getAllGroceryItems(): Flow<List<GroceryItem>> = dao.getAllGroceryItems()

    suspend fun addGroceryItem(item: GroceryItem): Long = dao.insertGroceryItem(item)

    suspend fun toggleGroceryItem(id: Long, isChecked: Boolean) = dao.setGroceryChecked(id, isChecked)

    suspend fun deleteGroceryItem(id: Long) = dao.deleteGroceryItem(id)

    suspend fun clearCheckedGroceryItems() = dao.deleteCheckedGroceryItems()

    suspend fun clearAllGroceryItems() = dao.clearAllGroceryItems()

    suspend fun generateGroceryFromWeeklyPlans(): Int {
        val plans = dao.getAllWeeklyPlans().first()
        if (plans.isEmpty()) return 0

        val parsedItems = mutableListOf<GroceryItem>()
        val seenNames = mutableSetOf<String>()

        for (plan in plans) {
            val ingredientList = plan.ingredients
                .split(",", "\n", ";")
                .map { it.trim() }
                .filter { it.isNotBlank() }

            for (ingredient in ingredientList) {
                val cleanName = ingredient.replaceFirstChar { it.uppercase() }
                if (!seenNames.contains(cleanName.lowercase())) {
                    seenNames.add(cleanName.lowercase())
                    val category = categorizeIngredient(cleanName)
                    parsedItems.add(
                        GroceryItem(
                            name = cleanName,
                            category = category,
                            quantity = "1 portion",
                            isChecked = false,
                            sourceMeal = plan.recipeName
                        )
                    )
                }
            }
        }

        if (parsedItems.isNotEmpty()) {
            dao.insertGroceryItems(parsedItems)
        }
        return parsedItems.size
    }

    private fun categorizeIngredient(name: String): String {
        val lower = name.lowercase()
        return when {
            lower.contains("chicken") || lower.contains("turkey") || lower.contains("beef") ||
            lower.contains("salmon") || lower.contains("tuna") || lower.contains("cod") ||
            lower.contains("fish") || lower.contains("steak") || lower.contains("tofu") ||
            lower.contains("whey") || lower.contains("protein") || lower.contains("patty") -> "Protein"

            lower.contains("egg") || lower.contains("yogurt") || lower.contains("milk") ||
            lower.contains("cheese") || lower.contains("cheddar") || lower.contains("feta") -> "Dairy"

            lower.contains("apple") || lower.contains("berry") || lower.contains("blueberries") ||
            lower.contains("strawberries") || lower.contains("spinach") || lower.contains("asparagus") ||
            lower.contains("avocado") || lower.contains("mushrooms") || lower.contains("broccoli") ||
            lower.contains("tomato") || lower.contains("lettuce") || lower.contains("celery") ||
            lower.contains("cucumber") || lower.contains("onion") || lower.contains("garlic") ||
            lower.contains("potato") || lower.contains("peppers") || lower.contains("lemon") -> "Produce"

            lower.contains("oat") || lower.contains("rice") || lower.contains("quinoa") ||
            lower.contains("sourdough") || lower.contains("bread") || lower.contains("wrap") ||
            lower.contains("lentil") || lower.contains("bean") || lower.contains("flour") -> "Grains"

            else -> "Pantry"
        }
    }

    // Water
    fun getWaterLogsForDate(date: String): Flow<List<WaterLog>> = dao.getWaterLogsForDate(date)

    fun getTotalWaterForDate(date: String): Flow<Int?> = dao.getTotalWaterForDate(date)

    suspend fun addWater(date: String, amountMl: Int) {
        dao.insertWaterLog(WaterLog(date = date, amountMl = amountMl))
    }

    suspend fun deleteWaterLog(id: Long) = dao.deleteWaterLog(id)

    suspend fun resetWaterForDate(date: String) = dao.resetWaterForDate(date)

    // Profile
    fun getUserProfile(): Flow<UserProfile?> = dao.getUserProfile()

    suspend fun saveUserProfile(profile: UserProfile) = dao.saveUserProfile(profile)
}
