package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Insights
import androidx.compose.material.icons.outlined.RestaurantMenu
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material.icons.outlined.Today
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.CarbsAmber
import com.example.ui.theme.MintGreenPrimary
import com.example.ui.theme.NutriBiteTheme

class MainActivity : ComponentActivity() {

    private val viewModel: NutriBiteViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            NutriBiteTheme {
                val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
                val currentDate by viewModel.currentDate.collectAsStateWithLifecycle()
                val summary by viewModel.nutritionSummary.collectAsStateWithLifecycle()
                val mealLogs by viewModel.todayLogs.collectAsStateWithLifecycle()
                val favoriteMeals by viewModel.favoriteMeals.collectAsStateWithLifecycle()
                val weeklyPlans by viewModel.weeklyPlans.collectAsStateWithLifecycle()
                val groceryItems by viewModel.groceryItems.collectAsStateWithLifecycle()
                val recentLogs by viewModel.recentLogs.collectAsStateWithLifecycle()
                val profile by viewModel.userProfile.collectAsStateWithLifecycle()

                val isAddMealOpen by viewModel.isAddMealDialogOpen.collectAsStateWithLifecycle()
                val targetMealType by viewModel.targetMealTypeForAdd.collectAsStateWithLifecycle()
                val isBioCalcOpen by viewModel.isBioCalculatorOpen.collectAsStateWithLifecycle()
                val isAddPlanOpen by viewModel.isAddPlanDialogOpen.collectAsStateWithLifecycle()
                val selectedPlanDay by viewModel.selectedPlanDay.collectAsStateWithLifecycle()
                val isAddGroceryOpen by viewModel.isAddGroceryDialogOpen.collectAsStateWithLifecycle()
                val showFavorites by viewModel.showFavoritesOnly.collectAsStateWithLifecycle()

                val userMessage by viewModel.userMessage.collectAsStateWithLifecycle()
                val snackbarHostState = remember { SnackbarHostState() }

                LaunchedEffect(userMessage) {
                    userMessage?.let {
                        snackbarHostState.showSnackbar(it)
                        viewModel.clearUserMessage()
                    }
                }

                // Handle back press if not on LOG screen or viewing favorites
                if (showFavorites) {
                    BackHandler { viewModel.showFavoritesOnly.value = false }
                } else if (currentScreen != AppScreen.LOG) {
                    BackHandler { viewModel.navigateTo(AppScreen.LOG) }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = {
                                Text(
                                    text = "NutriBite",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 20.sp,
                                    color = MintGreenPrimary
                                )
                            },
                            actions = {
                                IconButton(
                                    onClick = {
                                        viewModel.showFavoritesOnly.value = !showFavorites
                                    },
                                    modifier = Modifier.testTag("top_bar_favorites_btn")
                                ) {
                                    Icon(
                                        imageVector = if (showFavorites) Icons.Filled.Star else Icons.Outlined.StarBorder,
                                        contentDescription = "Favorites",
                                        tint = if (showFavorites) CarbsAmber else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        )
                    },
                    bottomBar = {
                        NavigationBar(
                            modifier = Modifier
                                .fillMaxWidth()
                                .windowInsetsPadding(WindowInsets.navigationBars)
                                .testTag("main_bottom_nav"),
                            containerColor = MaterialTheme.colorScheme.surface,
                            tonalElevation = 6.dp
                        ) {
                            NavigationBarItem(
                                selected = !showFavorites && currentScreen == AppScreen.LOG,
                                onClick = {
                                    viewModel.showFavoritesOnly.value = false
                                    viewModel.navigateTo(AppScreen.LOG)
                                },
                                icon = {
                                    Icon(
                                        imageVector = if (currentScreen == AppScreen.LOG) Icons.Filled.Today else Icons.Outlined.Today,
                                        contentDescription = "Today Log"
                                    )
                                },
                                label = { Text("Log") },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MintGreenPrimary,
                                    indicatorColor = MintGreenPrimary.copy(alpha = 0.15f)
                                )
                            )

                            NavigationBarItem(
                                selected = !showFavorites && currentScreen == AppScreen.PLANNER,
                                onClick = {
                                    viewModel.showFavoritesOnly.value = false
                                    viewModel.navigateTo(AppScreen.PLANNER)
                                },
                                icon = {
                                    Icon(
                                        imageVector = if (currentScreen == AppScreen.PLANNER) Icons.Filled.CalendarMonth else Icons.Outlined.CalendarMonth,
                                        contentDescription = "Weekly Planner"
                                    )
                                },
                                label = { Text("Planner") },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MintGreenPrimary,
                                    indicatorColor = MintGreenPrimary.copy(alpha = 0.15f)
                                )
                            )

                            NavigationBarItem(
                                selected = !showFavorites && currentScreen == AppScreen.GROCERY,
                                onClick = {
                                    viewModel.showFavoritesOnly.value = false
                                    viewModel.navigateTo(AppScreen.GROCERY)
                                },
                                icon = {
                                    val uncompletedGroceryCount = groceryItems.count { !it.isChecked }
                                    if (uncompletedGroceryCount > 0) {
                                        BadgedBox(
                                            badge = {
                                                Badge(containerColor = MintGreenPrimary) {
                                                    Text("$uncompletedGroceryCount")
                                                }
                                            }
                                        ) {
                                            Icon(
                                                imageVector = if (currentScreen == AppScreen.GROCERY) Icons.Filled.ShoppingCart else Icons.Outlined.ShoppingCart,
                                                contentDescription = "Grocery Checklist"
                                            )
                                        }
                                    } else {
                                        Icon(
                                            imageVector = if (currentScreen == AppScreen.GROCERY) Icons.Filled.ShoppingCart else Icons.Outlined.ShoppingCart,
                                            contentDescription = "Grocery Checklist"
                                        )
                                    }
                                },
                                label = { Text("Grocery") },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MintGreenPrimary,
                                    indicatorColor = MintGreenPrimary.copy(alpha = 0.15f)
                                )
                            )

                            NavigationBarItem(
                                selected = !showFavorites && currentScreen == AppScreen.WATER,
                                onClick = {
                                    viewModel.showFavoritesOnly.value = false
                                    viewModel.navigateTo(AppScreen.WATER)
                                },
                                icon = {
                                    Icon(
                                        imageVector = if (currentScreen == AppScreen.WATER) Icons.Filled.WaterDrop else Icons.Outlined.WaterDrop,
                                        contentDescription = "Water Intake"
                                    )
                                },
                                label = { Text("Water") },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = com.example.ui.theme.HydrationBlue,
                                    indicatorColor = com.example.ui.theme.HydrationBlue.copy(alpha = 0.15f)
                                )
                            )

                            NavigationBarItem(
                                selected = !showFavorites && currentScreen == AppScreen.DASHBOARD,
                                onClick = {
                                    viewModel.showFavoritesOnly.value = false
                                    viewModel.navigateTo(AppScreen.DASHBOARD)
                                },
                                icon = {
                                    Icon(
                                        imageVector = if (currentScreen == AppScreen.DASHBOARD) Icons.Filled.Insights else Icons.Outlined.Insights,
                                        contentDescription = "Trends & Reminders"
                                    )
                                },
                                label = { Text("Trends") },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MintGreenPrimary,
                                    indicatorColor = MintGreenPrimary.copy(alpha = 0.15f)
                                )
                            )
                        }
                    },
                    snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        if (showFavorites) {
                            FavoritesScreen(
                                favoriteMeals = favoriteMeals,
                                onLogFavorite = { meal, targetType ->
                                    viewModel.logFavoriteMealDirectly(meal, targetType)
                                },
                                onRemoveFavorite = { id ->
                                    viewModel.toggleFavorite(id, true)
                                },
                                onNavigateBack = { viewModel.showFavoritesOnly.value = false }
                            )
                        } else {
                            AnimatedContent(
                                targetState = currentScreen,
                                transitionSpec = { fadeIn() togetherWith fadeOut() },
                                label = "screen_transition"
                            ) { screen ->
                                when (screen) {
                                    AppScreen.LOG -> TodayLogScreen(
                                        currentDate = currentDate,
                                        summary = summary,
                                        mealLogs = mealLogs,
                                        onStepDate = { viewModel.stepDate(it) },
                                        onSetToday = { viewModel.setToday() },
                                        onOpenAddMeal = { viewModel.openAddMeal(it) },
                                        onDeleteMeal = { viewModel.deleteMealLog(it) },
                                        onToggleFavorite = { id, fav -> viewModel.toggleFavorite(id, fav) },
                                        onOpenBioCalculator = { viewModel.isBioCalculatorOpen.value = true },
                                        onAddWater = { viewModel.addWater(it) },
                                        onResetWater = { viewModel.resetWaterToday() },
                                        onNavigateToFavorites = { viewModel.showFavoritesOnly.value = true }
                                    )

                                    AppScreen.PLANNER -> WeeklyPlannerScreen(
                                        weeklyPlans = weeklyPlans,
                                        onAddPlanClick = { day ->
                                            viewModel.selectedPlanDay.value = day
                                            viewModel.isAddPlanDialogOpen.value = true
                                        },
                                        onDeletePlan = { viewModel.deleteWeeklyPlan(it) },
                                        onGenerateGroceryList = { viewModel.generateGroceryList() },
                                        onNavigateToGrocery = { viewModel.navigateTo(AppScreen.GROCERY) }
                                    )

                                    AppScreen.GROCERY -> GroceryListScreen(
                                        groceryItems = groceryItems,
                                        onToggleItem = { id, chk -> viewModel.toggleGroceryItem(id, chk) },
                                        onDeleteItem = { viewModel.deleteGroceryItem(it) },
                                        onClearChecked = { viewModel.clearCheckedGroceries() },
                                        onClearAll = { viewModel.clearAllGroceries() },
                                        onOpenAddCustom = { viewModel.isAddGroceryDialogOpen.value = true },
                                        onGenerateFromPlan = { viewModel.generateGroceryList() }
                                    )

                                    AppScreen.WATER -> {
                                        Box(modifier = Modifier.padding(16.dp)) {
                                            WaterBottleTracker(
                                                waterMl = summary.waterMl,
                                                targetWaterMl = summary.targetWaterMl,
                                                onAddWater = { viewModel.addWater(it) },
                                                onResetWater = { viewModel.resetWaterToday() }
                                            )
                                        }
                                    }

                                    AppScreen.DASHBOARD -> DashboardTrendsScreen(
                                        profile = profile,
                                        recentLogs = recentLogs,
                                        onUpdateReminders = { enabled, b, l, d, h ->
                                            viewModel.updateReminders(enabled, b, l, d, h)
                                        },
                                        onOpenBioCalculator = { viewModel.isBioCalculatorOpen.value = true }
                                    )
                                }
                            }
                        }

                        // Dialogs
                        if (isAddMealOpen) {
                            AddMealDialog(
                                initialMealType = targetMealType,
                                onDismiss = { viewModel.isAddMealDialogOpen.value = false },
                                onAddMeal = { name, serving, cals, p, c, f, fib, type, fav ->
                                    viewModel.addMealLog(name, serving, cals, p, c, f, fib, type, fav)
                                }
                            )
                        }

                        if (isBioCalcOpen) {
                            BioscienceCalculatorDialog(
                                currentProfile = profile,
                                onDismiss = { viewModel.isBioCalculatorOpen.value = false },
                                onApplyTargets = { age, gender, w, h, pal, goal ->
                                    viewModel.applyBioscienceTargets(age, gender, w, h, pal, goal)
                                }
                            )
                        }

                        if (isAddPlanOpen) {
                            AddPlanDialog(
                                dayOfWeek = selectedPlanDay,
                                onDismiss = { viewModel.isAddPlanDialogOpen.value = false },
                                onAddPlan = { day, type, name, cals, p, c, f, ing ->
                                    viewModel.addWeeklyPlan(day, type, name, cals, p, c, f, ing)
                                }
                            )
                        }

                        if (isAddGroceryOpen) {
                            AddGroceryDialog(
                                onDismiss = { viewModel.isAddGroceryDialogOpen.value = false },
                                onAddItem = { name, cat, qty ->
                                    viewModel.addCustomGrocery(name, cat, qty)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
