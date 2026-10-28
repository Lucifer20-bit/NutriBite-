package com.example

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.CarbsAmber
import com.example.ui.theme.FatsTeal
import com.example.ui.theme.HydrationBlue
import com.example.ui.theme.MintGreenPrimary
import com.example.ui.theme.ProteinCoral
import kotlin.math.roundToInt

@Composable
fun BioscienceCalculatorDialog(
    currentProfile: UserProfile,
    onDismiss: () -> Unit,
    onApplyTargets: (
        age: Int,
        gender: String,
        weightKg: Float,
        heightCm: Float,
        activityLevel: String,
        goal: String
    ) -> Unit
) {
    var ageText by remember { mutableStateOf(currentProfile.age.toString()) }
    var weightText by remember { mutableStateOf(currentProfile.weightKg.toString()) }
    var heightText by remember { mutableStateOf(currentProfile.heightCm.toString()) }
    var gender by remember { mutableStateOf(currentProfile.gender) }
    var activityLevel by remember { mutableStateOf(currentProfile.activityLevel) }
    var goal by remember { mutableStateOf(currentProfile.goal) }

    val calculatedTargets by remember {
        derivedStateOf {
            val age = ageText.toIntOrNull() ?: 28
            val weight = weightText.toFloatOrNull() ?: 70f
            val height = heightText.toFloatOrNull() ?: 175f
            NutriBiteBioscience.computeOptimalTargets(
                weightKg = weight,
                heightCm = height,
                age = age,
                gender = gender,
                activityLevel = activityLevel,
                goal = goal
            )
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .testTag("bio_calculator_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = MintGreenPrimary,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Column {
                            Text(
                                text = "Bioscience Metabolic Optimizer",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Mifflin-St Jeor & Bioenergetics Model",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Gender Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val isMale = gender.equals("MALE", ignoreCase = true)
                    Surface(
                        onClick = { gender = "MALE" },
                        shape = RoundedCornerShape(10.dp),
                        color = if (isMale) MintGreenPrimary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Male",
                            fontWeight = if (isMale) FontWeight.Bold else FontWeight.Normal,
                            color = if (isMale) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(vertical = 10.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }

                    val isFemale = gender.equals("FEMALE", ignoreCase = true)
                    Surface(
                        onClick = { gender = "FEMALE" },
                        shape = RoundedCornerShape(10.dp),
                        color = if (isFemale) MintGreenPrimary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Female",
                            fontWeight = if (isFemale) FontWeight.Bold else FontWeight.Normal,
                            color = if (isFemale) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(vertical = 10.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Biometrics Inputs
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = ageText,
                        onValueChange = { ageText = it },
                        label = { Text("Age") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = weightText,
                        onValueChange = { weightText = it },
                        label = { Text("Weight (kg)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = heightText,
                        onValueChange = { heightText = it },
                        label = { Text("Height (cm)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Activity Level selector
                Text(
                    text = "Physical Activity Level (PAL)",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))

                val activityOptions = listOf(
                    "SEDENTARY" to "Sedentary (desk job, 1.2x)",
                    "LIGHT" to "Light (1-2 workouts/wk, 1.375x)",
                    "MODERATE" to "Moderate (3-5 workouts/wk, 1.55x)",
                    "ACTIVE" to "Active (6-7 intense sessions/wk, 1.725x)"
                )

                activityOptions.forEach { (key, label) ->
                    val isSelected = activityLevel.equals(key, ignoreCase = true)
                    Surface(
                        onClick = { activityLevel = key },
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) MintGreenPrimary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) MintGreenPrimary else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Fitness Goal
                Text(
                    text = "Metabolic Goal",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val goals = listOf(
                        "LOSE_FAT" to "Fat Loss (-450 kcal)",
                        "MAINTAIN" to "Maintain",
                        "BUILD_MUSCLE" to "Build Muscle (+350 kcal)"
                    )
                    goals.forEach { (key, label) ->
                        val isSelected = goal.equals(key, ignoreCase = true)
                        Surface(
                            onClick = { goal = key },
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Live Science Preview Card
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MintGreenPrimary.copy(alpha = 0.08f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Base BMR: ${calculatedTargets.bmr} kcal",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Maintenance TDEE: ${calculatedTargets.tdee} kcal",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Calculated Daily Target: ${calculatedTargets.calories} kcal",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MintGreenPrimary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            MacroBadge("Protein", "${calculatedTargets.proteinG}g", ProteinCoral, Modifier.weight(1f))
                            MacroBadge("Carbs", "${calculatedTargets.carbsG}g", CarbsAmber, Modifier.weight(1f))
                            MacroBadge("Fat", "${calculatedTargets.fatG}g", FatsTeal, Modifier.weight(1f))
                            MacroBadge("Water", "${calculatedTargets.waterMl}ml", HydrationBlue, Modifier.weight(1f))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = {
                        val age = ageText.toIntOrNull() ?: 28
                        val weight = weightText.toFloatOrNull() ?: 70f
                        val height = heightText.toFloatOrNull() ?: 175f
                        onApplyTargets(age, gender, weight, height, activityLevel, goal)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("apply_bio_targets_btn"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MintGreenPrimary)
                ) {
                    Text("Apply Bio-Targets to Tracker", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun MacroBadge(label: String, value: String, color: Color, modifier: Modifier) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.12f),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = color, fontSize = 10.sp)
            Text(text = value, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = color)
        }
    }
}
