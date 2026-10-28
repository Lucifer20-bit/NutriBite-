package com.example

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Database(
    entities = [
        MealLog::class,
        WeeklyMealPlan::class,
        GroceryItem::class,
        WaterLog::class,
        UserProfile::class
    ],
    version = 1,
    exportSchema = false
)
abstract class NutriBiteDatabase : RoomDatabase() {

    abstract fun dao(): NutriBiteDao

    companion object {
        @Volatile
        private var INSTANCE: NutriBiteDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): NutriBiteDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NutriBiteDatabase::class.java,
                    "nutribite_database"
                )
                    .addCallback(NutriBiteDatabaseCallback(scope))
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class NutriBiteDatabaseCallback(
            private val scope: CoroutineScope
        ) : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database.dao())
                    }
                }
            }

            private suspend fun populateInitialData(dao: NutriBiteDao) {
                val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

                // 1. Initial User Profile
                val initialProfile = UserProfile(
                    id = 1,
                    age = 27,
                    gender = "MALE",
                    weightKg = 72.5f,
                    heightCm = 178f,
                    activityLevel = "MODERATE",
                    goal = "MAINTAIN",
                    targetCalories = 2200,
                    targetProteinG = 150f,
                    targetCarbsG = 225f,
                    targetFatG = 65f,
                    targetWaterMl = 2800,
                    remindersEnabled = true,
                    breakfastReminderTime = "08:00",
                    lunchReminderTime = "12:30",
                    dinnerReminderTime = "19:00",
                    hydrationReminderHours = 2
                )
                dao.saveUserProfile(initialProfile)

                // 2. Pre-seed today's meals for an immediate vibrant experience
                dao.insertMealLog(
                    MealLog(
                        date = today,
                        mealType = "BREAKFAST",
                        foodName = "Steel-Cut Oatmeal with Blueberries & Chia",
                        servingDesc = "1 bowl (280g)",
                        calories = 310,
                        proteinG = 11f,
                        carbsG = 54f,
                        fatG = 6f,
                        fiberG = 8f,
                        isFavorite = true
                    )
                )
                dao.insertMealLog(
                    MealLog(
                        date = today,
                        mealType = "BREAKFAST",
                        foodName = "Greek Yogurt (0%) & Honey",
                        servingDesc = "170g container",
                        calories = 140,
                        proteinG = 17f,
                        carbsG = 15f,
                        fatG = 0.5f,
                        fiberG = 0f,
                        isFavorite = true
                    )
                )
                dao.insertMealLog(
                    MealLog(
                        date = today,
                        mealType = "LUNCH",
                        foodName = "Grilled Chicken Breast & Herb Quinoa Bowl",
                        servingDesc = "1 bowl (320g)",
                        calories = 490,
                        proteinG = 52f,
                        carbsG = 45f,
                        fatG = 9f,
                        fiberG = 6f,
                        isFavorite = true
                    )
                )
                dao.insertMealLog(
                    MealLog(
                        date = today,
                        mealType = "SNACKS",
                        foodName = "Green Apple & Raw Almonds",
                        servingDesc = "1 medium apple + 25g nuts",
                        calories = 230,
                        proteinG = 6f,
                        carbsG = 26f,
                        fatG = 13f,
                        fiberG = 5.5f,
                        isFavorite = false
                    )
                )

                // 3. Pre-seed water intake
                dao.insertWaterLog(WaterLog(date = today, amountMl = 500))
                dao.insertWaterLog(WaterLog(date = today, amountMl = 350))
                dao.insertWaterLog(WaterLog(date = today, amountMl = 500))

                // 4. Pre-seed Weekly Meal Plan
                val samplePlans = listOf(
                    WeeklyMealPlan(dayOfWeek = 0, mealType = "BREAKFAST", recipeName = "Protein Pancakes with Berries", calories = 380, proteinG = 28f, carbsG = 46f, fatG = 7f, ingredients = "Oat flour, Egg whites, Whey protein, Blueberries"),
                    WeeklyMealPlan(dayOfWeek = 0, mealType = "LUNCH", recipeName = "Grilled Salmon & Asparagus", calories = 510, proteinG = 44f, carbsG = 12f, fatG = 28f, ingredients = "Salmon fillet, Olive oil, Lemon, Asparagus"),
                    WeeklyMealPlan(dayOfWeek = 0, mealType = "DINNER", recipeName = "Turkey Chili & Brown Rice", calories = 520, proteinG = 42f, carbsG = 55f, fatG = 12f, ingredients = "Ground turkey, Kidney beans, Diced tomatoes, Brown rice"),
                    WeeklyMealPlan(dayOfWeek = 1, mealType = "BREAKFAST", recipeName = "Avocado Toast & Poached Eggs", calories = 350, proteinG = 16f, carbsG = 28f, fatG = 19f, ingredients = "Sourdough bread, Avocado, Eggs, Red pepper flakes"),
                    WeeklyMealPlan(dayOfWeek = 1, mealType = "LUNCH", recipeName = "Mediterranean Tuna Wrap", calories = 430, proteinG = 36f, carbsG = 38f, fatG = 14f, ingredients = "Whole wheat wrap, Canned tuna, Greek yogurt, Celery, Spinach"),
                    WeeklyMealPlan(dayOfWeek = 1, mealType = "DINNER", recipeName = "Tofu Vegetable Stir-Fry", calories = 410, proteinG = 24f, carbsG = 48f, fatG = 14f, ingredients = "Firm tofu, Broccoli, Bell peppers, Soy sauce, Sesame oil"),
                    WeeklyMealPlan(dayOfWeek = 2, mealType = "BREAKFAST", recipeName = "Berry Chia Seed Parfait", calories = 320, proteinG = 20f, carbsG = 38f, fatG = 9f, ingredients = "Greek yogurt, Chia seeds, Strawberries, Honey"),
                    WeeklyMealPlan(dayOfWeek = 2, mealType = "LUNCH", recipeName = "Chicken Quinoa Bowl", calories = 480, proteinG = 45f, carbsG = 42f, fatG = 10f, ingredients = "Chicken breast, Quinoa, Cucumber, Feta cheese, Olive oil"),
                    WeeklyMealPlan(dayOfWeek = 2, mealType = "DINNER", recipeName = "Lean Beef Burger with Sweet Potato Wedges", calories = 560, proteinG = 42f, carbsG = 48f, fatG = 18f, ingredients = "Lean beef patty, Sweet potato, Lettuce, Tomato"),
                    WeeklyMealPlan(dayOfWeek = 3, mealType = "BREAKFAST", recipeName = "Spinach & Mushroom Omelette", calories = 310, proteinG = 22f, carbsG = 5f, fatG = 21f, ingredients = "Eggs, Spinach, Cremini mushrooms, Cheddar"),
                    WeeklyMealPlan(dayOfWeek = 3, mealType = "LUNCH", recipeName = "Lentil Soup & Rustic Bread", calories = 390, proteinG = 22f, carbsG = 60f, fatG = 6f, ingredients = "Brown lentils, Carrots, Onions, Garlic, Rye bread"),
                    WeeklyMealPlan(dayOfWeek = 3, mealType = "DINNER", recipeName = "Baked Cod & Herb Roasted Potatoes", calories = 440, proteinG = 38f, carbsG = 42f, fatG = 11f, ingredients = "Cod fillet, Baby potatoes, Rosemary, Olive oil")
                )
                dao.insertPlans(samplePlans)

                // 5. Initial Grocery Checklist items
                val initialGroceries = listOf(
                    GroceryItem(name = "Rolled Oats", category = "Grains", quantity = "1 kg bag", isChecked = false),
                    GroceryItem(name = "Chicken Breast", category = "Protein", quantity = "800g", isChecked = false),
                    GroceryItem(name = "Greek Yogurt (0%)", category = "Dairy", quantity = "2 large tubs", isChecked = true),
                    GroceryItem(name = "Fresh Blueberries", category = "Produce", quantity = "250g", isChecked = false),
                    GroceryItem(name = "Baby Spinach", category = "Produce", quantity = "1 bag (300g)", isChecked = false),
                    GroceryItem(name = "Salmon Fillets", category = "Protein", quantity = "400g", isChecked = false),
                    GroceryItem(name = "Organic Eggs", category = "Dairy", quantity = "1 dozen", isChecked = true),
                    GroceryItem(name = "Extra Virgin Olive Oil", category = "Pantry", quantity = "500ml", isChecked = false),
                    GroceryItem(name = "Sweet Potatoes", category = "Produce", quantity = "1 kg", isChecked = false)
                )
                dao.insertGroceryItems(initialGroceries)
            }
        }
    }
}
