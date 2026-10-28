package com.example

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface NutriBiteDao {

    // --- Meal Logs ---
    @Query("SELECT * FROM meal_logs WHERE date = :date ORDER BY timestamp ASC")
    fun getLogsForDate(date: String): Flow<List<MealLog>>

    @Query("SELECT * FROM meal_logs WHERE isFavorite = 1 GROUP BY foodName ORDER BY timestamp DESC")
    fun getFavoriteMeals(): Flow<List<MealLog>>

    @Query("SELECT * FROM meal_logs ORDER BY date DESC, timestamp DESC LIMIT :limit")
    fun getRecentLogs(limit: Int = 120): Flow<List<MealLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMealLog(log: MealLog): Long

    @Update
    suspend fun updateMealLog(log: MealLog)

    @Query("DELETE FROM meal_logs WHERE id = :id")
    suspend fun deleteMealLog(id: Long)

    @Query("UPDATE meal_logs SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun setMealFavorite(id: Long, isFavorite: Boolean)

    // --- Weekly Meal Plans ---
    @Query("SELECT * FROM weekly_meal_plans ORDER BY dayOfWeek ASC, id ASC")
    fun getAllWeeklyPlans(): Flow<List<WeeklyMealPlan>>

    @Query("SELECT * FROM weekly_meal_plans WHERE dayOfWeek = :dayOfWeek")
    fun getPlansForDay(dayOfWeek: Int): Flow<List<WeeklyMealPlan>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlan(plan: WeeklyMealPlan): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlans(plans: List<WeeklyMealPlan>)

    @Update
    suspend fun updatePlan(plan: WeeklyMealPlan)

    @Query("DELETE FROM weekly_meal_plans WHERE id = :id")
    suspend fun deletePlan(id: Long)

    @Query("DELETE FROM weekly_meal_plans WHERE dayOfWeek = :dayOfWeek")
    suspend fun clearPlansForDay(dayOfWeek: Int)

    // --- Grocery Checklist ---
    @Query("SELECT * FROM grocery_items ORDER BY isChecked ASC, category ASC, name ASC")
    fun getAllGroceryItems(): Flow<List<GroceryItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroceryItem(item: GroceryItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroceryItems(items: List<GroceryItem>)

    @Query("UPDATE grocery_items SET isChecked = :isChecked WHERE id = :id")
    suspend fun setGroceryChecked(id: Long, isChecked: Boolean)

    @Query("DELETE FROM grocery_items WHERE id = :id")
    suspend fun deleteGroceryItem(id: Long)

    @Query("DELETE FROM grocery_items WHERE isChecked = 1")
    suspend fun deleteCheckedGroceryItems()

    @Query("DELETE FROM grocery_items")
    suspend fun clearAllGroceryItems()

    // --- Water Logs ---
    @Query("SELECT * FROM water_logs WHERE date = :date ORDER BY timestamp DESC")
    fun getWaterLogsForDate(date: String): Flow<List<WaterLog>>

    @Query("SELECT SUM(amountMl) FROM water_logs WHERE date = :date")
    fun getTotalWaterForDate(date: String): Flow<Int?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWaterLog(log: WaterLog): Long

    @Query("DELETE FROM water_logs WHERE id = :id")
    suspend fun deleteWaterLog(id: Long)

    @Query("DELETE FROM water_logs WHERE date = :date")
    suspend fun resetWaterForDate(date: String)

    // --- User Profile ---
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun getUserProfile(): Flow<UserProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserProfile(profile: UserProfile)
}
