package com.example

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.roundToInt

class ExampleUnitTest {

    @Test
    fun bmrCalculation_isAccurateForMaleAndFemale() {
        // Mifflin-St Jeor: 10 * weight(kg) + 6.25 * height(cm) - 5 * age + 5 (Men)
        // 10 * 70 + 6.25 * 175 - 5 * 25 + 5 = 700 + 1093.75 - 125 + 5 = 1673.75
        val maleBmr = NutriBiteBioscience.calculateBMR(70f, 175f, 25, "MALE")
        assertEquals(1673.75f, maleBmr, 0.1f)

        // Women: 10 * weight(kg) + 6.25 * height(cm) - 5 * age - 161 (Women)
        // 10 * 60 + 6.25 * 165 - 5 * 25 - 161 = 600 + 1031.25 - 125 - 161 = 1345.25
        val femaleBmr = NutriBiteBioscience.calculateBMR(60f, 165f, 25, "FEMALE")
        assertEquals(1345.25f, femaleBmr, 0.1f)
    }

    @Test
    fun tdeeCalculation_appliesActivityMultipliers() {
        val bmr = 1600f
        val sedentaryTdee = NutriBiteBioscience.calculateTDEE(bmr, "SEDENTARY")
        assertEquals(1920f, sedentaryTdee, 0.1f)

        val moderateTdee = NutriBiteBioscience.calculateTDEE(bmr, "MODERATE")
        assertEquals(2480f, moderateTdee, 0.1f)
    }

    @Test
    fun computeOptimalTargets_calculatesMacroPartitioning() {
        val targets = NutriBiteBioscience.computeOptimalTargets(
            weightKg = 70f,
            heightCm = 175f,
            age = 25,
            gender = "MALE",
            activityLevel = "MODERATE",
            goal = "MAINTAIN"
        )

        // Protein should be around 140g (2.0g/kg)
        assertEquals(140f, targets.proteinG, 1.0f)
        assertTrue(targets.calories > 2000)
        assertTrue(targets.waterMl >= 2500)
    }
}
