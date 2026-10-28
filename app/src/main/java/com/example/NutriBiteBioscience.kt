package com.example

import kotlin.math.roundToInt

data class FoodPreset(
    val name: String,
    val category: String,
    val defaultServing: String,
    val calories: Int,
    val proteinG: Float,
    val carbsG: Float,
    val fatG: Float,
    val fiberG: Float,
    val defaultMealType: MealType
)

data class BioscienceInsight(
    val title: String,
    val category: String,
    val tag: String,
    val summary: String,
    val recommendation: String
)

object NutriBiteBioscience {

    fun calculateBMR(weightKg: Float, heightCm: Float, age: Int, gender: String): Float {
        val base = (10f * weightKg) + (6.25f * heightCm) - (5f * age)
        return if (gender.equals("MALE", ignoreCase = true)) {
            base + 5f
        } else {
            base - 161f
        }
    }

    fun calculateTDEE(bmr: Float, activityLevel: String): Float {
        val multiplier = when (activityLevel.uppercase()) {
            "SEDENTARY" -> 1.20f
            "LIGHT" -> 1.375f
            "MODERATE" -> 1.55f
            "ACTIVE" -> 1.725f
            "VERY_ACTIVE" -> 1.90f
            else -> 1.55f
        }
        return bmr * multiplier
    }

    data class MacroTargets(
        val calories: Int,
        val proteinG: Float,
        val carbsG: Float,
        val fatG: Float,
        val waterMl: Int,
        val bmr: Int,
        val tdee: Int
    )

    fun computeOptimalTargets(
        weightKg: Float,
        heightCm: Float,
        age: Int,
        gender: String,
        activityLevel: String,
        goal: String
    ): MacroTargets {
        val bmr = calculateBMR(weightKg, heightCm, age, gender)
        val tdee = calculateTDEE(bmr, activityLevel)

        val targetCalories = when (goal.uppercase()) {
            "LOSE_FAT" -> (tdee - 450f).coerceAtLeast(1200f)
            "BUILD_MUSCLE" -> tdee + 350f
            else -> tdee
        }.roundToInt()

        // Evidence-based Protein: 2.0g per kg of bodyweight
        val proteinG = (weightKg * 2.0f).coerceIn(60f, 250f)
        val proteinCals = proteinG * 4f

        // Fat: 28% of total calories (0.8 - 1.0g per kg)
        val fatCals = targetCalories * 0.28f
        val fatG = (fatCals / 9f).coerceIn(40f, 120f)

        // Carbohydrates: remaining calories
        val remainingCals = (targetCalories - proteinCals - (fatG * 9f)).coerceAtLeast(200f)
        val carbsG = remainingCals / 4f

        // Water: 35ml per kg bodyweight + activity hydration factor
        val waterMl = (weightKg * 35f + when (activityLevel.uppercase()) {
            "ACTIVE", "VERY_ACTIVE" -> 800f
            "MODERATE" -> 500f
            else -> 300f
        }).roundToInt()

        return MacroTargets(
            calories = targetCalories,
            proteinG = (proteinG * 10).roundToInt() / 10f,
            carbsG = (carbsG * 10).roundToInt() / 10f,
            fatG = (fatG * 10).roundToInt() / 10f,
            waterMl = waterMl,
            bmr = bmr.roundToInt(),
            tdee = tdee.roundToInt()
        )
    }

    val insights = listOf(
        BioscienceInsight(
            title = "Protein Timing & Leucine Threshold",
            category = "Muscle Bioscience",
            tag = "Anabolic Signaling",
            summary = "Consuming 25-35g of high-quality protein per meal triggers the mTOR pathway via the essential amino acid leucine (~2.7g threshold), maximizing Muscle Protein Synthesis.",
            recommendation = "Distribute your protein intake evenly across 3-4 meals rather than back-loading at dinner."
        ),
        BioscienceInsight(
            title = "Thermic Effect of Food (TEF)",
            category = "Metabolic Bioenergetics",
            tag = "Energy Expenditure",
            summary = "Digestive metabolic cost burns roughly 20-30% of protein calories, compared to only 5-10% for carbohydrates and 0-3% for fats.",
            recommendation = "Prioritizing whole protein sources increases passive metabolic caloric burn during digestion."
        ),
        BioscienceInsight(
            title = "Cellular Osmolarity & Hydration",
            category = "Fluid Dynamics",
            tag = "Electrolyte Transport",
            summary = "Water absorption into cells depends on osmotic equilibrium driven by sodium, potassium, and magnesium ions. Plain water without electrolytes can dilute blood sodium.",
            recommendation = "Pair your morning hydration with a pinch of mineral salt or electrolyte-rich leafy greens."
        ),
        BioscienceInsight(
            title = "Glycemic Index & Insulin Dynamics",
            category = "Endocrine Health",
            tag = "Glucose Homeostasis",
            summary = "Combining complex carbohydrates with dietary fiber and healthy fats blunts postprandial glucose spikes and prevents mid-day fatigue.",
            recommendation = "Add chia seeds, avocado, or nuts to high-carb meals like oatmeal or toast."
        ),
        BioscienceInsight(
            title = "Circadian Digestive Efficiency",
            category = "Chronobiology",
            tag = "Metabolic Rhythm",
            summary = "Insulin sensitivity and gastric motility peak during daylight hours, while melatonin secretion in the evening downregulates pancreatic insulin release.",
            recommendation = "Consume the majority of your carbohydrate intake before 8:00 PM for optimal sleep and recovery."
        ),
        BioscienceInsight(
            title = "Microbiome & Short-Chain Fatty Acids",
            category = "Gut Bioscience",
            tag = "Prebiotic Fermentation",
            summary = "Dietary fibers like beta-glucan and inulin are fermented by gut microbiota into butyrate and propionate, which strengthen intestinal barriers and regulate appetite.",
            recommendation = "Aim for at least 28-35g of diverse fiber sources daily from legumes, oats, berries, and brassicas."
        )
    )

    val defaultFoodPresets = listOf(
        FoodPreset("Steel-Cut Oatmeal with Berries", "Breakfast", "1 bowl (280g)", 290, 9f, 52f, 5f, 7f, MealType.BREAKFAST),
        FoodPreset("Scrambled Eggs (3) with Spinach", "Breakfast", "3 large eggs (180g)", 240, 20f, 2f, 16f, 1f, MealType.BREAKFAST),
        FoodPreset("Avocado Whole-Grain Toast", "Breakfast", "1 slice (120g)", 210, 6f, 22f, 12f, 5f, MealType.BREAKFAST),
        FoodPreset("Greek Yogurt (0%) with Honey & Walnuts", "Breakfast", "1 cup (220g)", 230, 22f, 18f, 7f, 1f, MealType.BREAKFAST),
        FoodPreset("Protein Chia Seed Pudding", "Breakfast", "1 cup (200g)", 260, 18f, 24f, 10f, 9f, MealType.BREAKFAST),

        FoodPreset("Grilled Chicken Breast & Quinoa", "Lunch", "1 plate (320g)", 460, 48f, 42f, 8f, 5f, MealType.LUNCH),
        FoodPreset("Mediterranean Tuna Salad Bowl", "Lunch", "1 bowl (300g)", 390, 36f, 18f, 19f, 6f, MealType.LUNCH),
        FoodPreset("Turkey & Avocado Wrap", "Lunch", "1 wrap (250g)", 420, 32f, 38f, 14f, 4f, MealType.LUNCH),
        FoodPreset("Lentil Soup with Sourdough", "Lunch", "1 bowl (350g)", 380, 22f, 58f, 6f, 11f, MealType.LUNCH),
        FoodPreset("Grilled Tofu Buddha Bowl", "Lunch", "1 bowl (320g)", 410, 26f, 44f, 15f, 8f, MealType.LUNCH),

        FoodPreset("Baked Atlantic Salmon & Asparagus", "Dinner", "220g fillet + greens", 480, 42f, 6f, 28f, 3f, MealType.DINNER),
        FoodPreset("Lean Beef Steak & Sweet Potato", "Dinner", "200g steak + 150g potato", 540, 46f, 36f, 18f, 4f, MealType.DINNER),
        FoodPreset("Stir-Fry Chicken with Brown Rice", "Dinner", "1 bowl (340g)", 490, 40f, 56f, 11f, 5f, MealType.DINNER),
        FoodPreset("Cod Fillet with Roasted Veggies", "Dinner", "250g serving", 340, 38f, 20f, 9f, 6f, MealType.DINNER),
        FoodPreset("Chickpea Coconut Curry Bowl", "Dinner", "1 bowl (350g)", 450, 16f, 58f, 18f, 9f, MealType.DINNER),

        FoodPreset("Whey Protein Shake (with Water)", "Snacks", "1 scoop (30g)", 125, 25f, 2f, 1.5f, 0f, MealType.SNACKS),
        FoodPreset("Raw Almonds", "Snacks", "1 handful (30g)", 170, 6f, 6f, 15f, 3.5f, MealType.SNACKS),
        FoodPreset("Green Apple with Peanut Butter", "Snacks", "1 apple + 1 tbsp", 200, 4f, 28f, 8f, 5f, MealType.SNACKS),
        FoodPreset("Cottage Cheese (2%) with Berries", "Snacks", "1/2 cup (150g)", 140, 16f, 11f, 3f, 2f, MealType.SNACKS),
        FoodPreset("Hard-Boiled Eggs (2)", "Snacks", "2 eggs (100g)", 140, 13f, 1f, 10f, 0f, MealType.SNACKS)
    )
}
