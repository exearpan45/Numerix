package com.example.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.Handyman
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.*
import com.example.viewmodel.CalculatorViewModel
import com.example.viewmodel.ConverterViewModel
import com.example.viewmodel.SettingsViewModel
import com.example.viewmodel.ToolsViewModel

sealed class Screen {
    object Splash : Screen()
    object Home : Screen()
    object Calculator : Screen()
    object Scientific : Screen()
    object Converter : Screen()
    object Percentage : Screen()
    object EverydayTools : Screen()
    object History : Screen()
    object Settings : Screen()
    object About : Screen()
    object Help : Screen()
    object Privacy : Screen()
    object Disclaimer : Screen()
    object Licenses : Screen()
}

enum class BottomNavTab(
    val route: Screen,
    val title: String,
    val iconFilled: ImageVector,
    val iconOutlined: ImageVector
) {
    HOME(Screen.Home, "Home", Icons.Filled.Home, Icons.Outlined.Home),
    CALCULATOR(Screen.Calculator, "Calculator", Icons.Filled.Calculate, Icons.Outlined.Calculate),
    TOOLS(Screen.EverydayTools, "Tools", Icons.Filled.Handyman, Icons.Outlined.Handyman),
    SETTINGS(Screen.Settings, "Settings", Icons.Filled.Settings, Icons.Outlined.Settings)
}

@Composable
fun NumerixNavHost(
    calculatorViewModel: CalculatorViewModel = viewModel(),
    converterViewModel: ConverterViewModel = viewModel(),
    toolsViewModel: ToolsViewModel = viewModel(),
    settingsViewModel: SettingsViewModel = viewModel()
) {
    var screenStack by remember { mutableStateOf(listOf<Screen>(Screen.Splash)) }
    val currentScreen = screenStack.lastOrNull() ?: Screen.Home

    fun navigateTo(screen: Screen) {
        if (currentScreen != screen) {
            screenStack = screenStack + screen
        }
    }

    fun navigateBack() {
        if (screenStack.size > 1) {
            screenStack = screenStack.dropLast(1)
        }
    }

    fun navigateTab(screen: Screen) {
        screenStack = listOf(screen)
    }

    if (currentScreen != Screen.Splash && currentScreen != Screen.Home) {
        BackHandler {
            navigateBack()
        }
    }

    val calcUiState by calculatorViewModel.uiState.collectAsStateWithLifecycle()
    val appSettings by calculatorViewModel.appSettings.collectAsStateWithLifecycle()
    val historyList by calculatorViewModel.historyList.collectAsStateWithLifecycle()
    val recentHistory by calculatorViewModel.recentHistory.collectAsStateWithLifecycle()

    val converterState by converterViewModel.uiState.collectAsStateWithLifecycle()
    val percentageState by toolsViewModel.percentageState.collectAsStateWithLifecycle()
    val gstState by toolsViewModel.gstState.collectAsStateWithLifecycle()
    val tipState by toolsViewModel.tipState.collectAsStateWithLifecycle()

    val isBottomBarVisible = currentScreen in listOf(
        Screen.Home,
        Screen.Calculator,
        Screen.EverydayTools,
        Screen.Settings
    )

    Scaffold(
        bottomBar = {
            if (isBottomBarVisible) {
                NavigationBar(
                    modifier = Modifier.testTag("main_bottom_nav_bar"),
                    windowInsets = WindowInsets.navigationBars
                ) {
                    BottomNavTab.values().forEach { tab ->
                        val selected = when (tab) {
                            BottomNavTab.HOME -> currentScreen == Screen.Home
                            BottomNavTab.CALCULATOR -> currentScreen == Screen.Calculator
                            BottomNavTab.TOOLS -> currentScreen == Screen.EverydayTools || currentScreen == Screen.Converter || currentScreen == Screen.Percentage
                            BottomNavTab.SETTINGS -> currentScreen == Screen.Settings
                        }
                        NavigationBarItem(
                            selected = selected,
                            onClick = { navigateTab(tab.route) },
                            icon = {
                                Icon(
                                    imageVector = if (selected) tab.iconFilled else tab.iconOutlined,
                                    contentDescription = tab.title
                                )
                            },
                            label = { Text(tab.title) },
                            modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
                        )
                    }
                }
            }
        }
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
        ) {
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "screen_transition"
            ) { target ->
                when (target) {
                    is Screen.Splash -> {
                        SplashScreen(
                            onSplashFinished = {
                                screenStack = listOf(Screen.Home)
                            }
                        )
                    }

                    is Screen.Home -> {
                        HomeScreen(
                            onNavigateToCalculator = { navigateTo(Screen.Calculator) },
                            onNavigateToScientific = { navigateTo(Screen.Scientific) },
                            onNavigateToConverter = { navigateTo(Screen.Converter) },
                            onNavigateToPercentage = { navigateTo(Screen.Percentage) },
                            onNavigateToEverydayTools = { navigateTo(Screen.EverydayTools) },
                            onNavigateToHistory = { navigateTo(Screen.History) },
                            onNavigateToSettings = { navigateTo(Screen.Settings) },
                            recentHistory = recentHistory,
                            onReuseHistory = { item ->
                                calculatorViewModel.reuseHistory(item.expression, item.result)
                                navigateTo(Screen.Calculator)
                            }
                        )
                    }

                    is Screen.Calculator -> {
                        CalculatorScreen(
                            uiState = calcUiState,
                            onDigit = calculatorViewModel::onDigit,
                            onOperator = calculatorViewModel::onOperator,
                            onDecimal = calculatorViewModel::onDecimal,
                            onClear = calculatorViewModel::onClear,
                            onDelete = calculatorViewModel::onDelete,
                            onPercentage = calculatorViewModel::onPercentage,
                            onNegate = calculatorViewModel::onNegate,
                            onEquals = { calculatorViewModel.onEquals("standard") },
                            onFunction = calculatorViewModel::onFunction,
                            onParenthesis = calculatorViewModel::onParenthesis,
                            onToggleAngleMode = calculatorViewModel::toggleAngleMode,
                            onMemoryClear = calculatorViewModel::memoryClear,
                            onMemoryRecall = calculatorViewModel::memoryRecall,
                            onMemoryAdd = calculatorViewModel::memoryAdd,
                            onMemorySubtract = calculatorViewModel::memorySubtract,
                            onOpenHistory = { navigateTo(Screen.History) },
                            onOpenScientific = { navigateTo(Screen.Scientific) },
                            hapticEnabled = appSettings.hapticFeedback,
                            soundEnabled = appSettings.buttonSound
                        )
                    }

                    is Screen.Scientific -> {
                        ScientificScreen(
                            uiState = calcUiState,
                            onDigit = calculatorViewModel::onDigit,
                            onOperator = calculatorViewModel::onOperator,
                            onDecimal = calculatorViewModel::onDecimal,
                            onClear = calculatorViewModel::onClear,
                            onDelete = calculatorViewModel::onDelete,
                            onPercentage = calculatorViewModel::onPercentage,
                            onNegate = calculatorViewModel::onNegate,
                            onEquals = { calculatorViewModel.onEquals("scientific") },
                            onFunction = calculatorViewModel::onFunction,
                            onParenthesis = calculatorViewModel::onParenthesis,
                            onToggleAngleMode = calculatorViewModel::toggleAngleMode,
                            onMemoryClear = calculatorViewModel::memoryClear,
                            onMemoryRecall = calculatorViewModel::memoryRecall,
                            onMemoryAdd = calculatorViewModel::memoryAdd,
                            onMemorySubtract = calculatorViewModel::memorySubtract,
                            onOpenHistory = { navigateTo(Screen.History) },
                            onBack = { navigateBack() },
                            hapticEnabled = appSettings.hapticFeedback,
                            soundEnabled = appSettings.buttonSound
                        )
                    }

                    is Screen.Converter -> {
                        ConverterScreen(
                            uiState = converterState,
                            unitsList = converterViewModel.getUnitsForCategory(converterState.category),
                            onSelectCategory = converterViewModel::setCategory,
                            onSelectFromUnit = converterViewModel::setFromUnit,
                            onSelectToUnit = converterViewModel::setToUnit,
                            onInputValueChange = converterViewModel::setInputValue,
                            onSwapUnits = converterViewModel::swapUnits,
                            onBack = { navigateBack() }
                        )
                    }

                    is Screen.Percentage -> {
                        PercentageScreen(
                            uiState = percentageState,
                            onUpdatePercentOf = toolsViewModel::updatePercentOf,
                            onUpdatePercentChange = toolsViewModel::updatePercentChange,
                            onUpdateDiscount = toolsViewModel::updateDiscount,
                            onBack = { navigateBack() }
                        )
                    }

                    is Screen.EverydayTools -> {
                        EverydayToolsScreen(
                            tipState = tipState,
                            gstState = gstState,
                            onUpdateTipBill = toolsViewModel::updateTipBill,
                            onUpdateTipPercent = toolsViewModel::updateTipPercent,
                            onUpdatePeopleCount = toolsViewModel::updatePeopleCount,
                            onSetGstMode = toolsViewModel::setGstMode,
                            onUpdateGstAmount = toolsViewModel::updateGstAmount,
                            onUpdateGstRate = toolsViewModel::updateGstRate,
                            onBack = { navigateBack() }
                        )
                    }

                    is Screen.History -> {
                        HistoryScreen(
                            historyList = historyList,
                            onReuseItem = { item ->
                                calculatorViewModel.reuseHistory(item.expression, item.result)
                                navigateTo(Screen.Calculator)
                            },
                            onDeleteItem = calculatorViewModel::deleteHistoryItem,
                            onClearAll = calculatorViewModel::clearAllHistory,
                            onBack = { navigateBack() }
                        )
                    }

                    is Screen.Settings -> {
                        SettingsScreen(
                            settings = appSettings,
                            onSetThemeMode = settingsViewModel::setThemeMode,
                            onSetAngleMode = settingsViewModel::setAngleMode,
                            onSetHaptic = settingsViewModel::setHapticFeedback,
                            onSetSound = settingsViewModel::setButtonSound,
                            onSetThousandsSeparator = settingsViewModel::setThousandsSeparator,
                            onClearHistory = settingsViewModel::clearAllHistory,
                            onNavigateToAbout = { navigateTo(Screen.About) },
                            onNavigateToHelp = { navigateTo(Screen.Help) },
                            onNavigateToPrivacy = { navigateTo(Screen.Privacy) },
                            onNavigateToDisclaimer = { navigateTo(Screen.Disclaimer) },
                            onNavigateToLicenses = { navigateTo(Screen.Licenses) },
                            onBack = { navigateBack() }
                        )
                    }

                    is Screen.About -> {
                        AboutScreen(onBack = { navigateBack() })
                    }

                    is Screen.Help -> {
                        HelpScreen(onBack = { navigateBack() })
                    }

                    is Screen.Privacy -> {
                        PrivacyPolicyScreen(onBack = { navigateBack() })
                    }

                    is Screen.Disclaimer -> {
                        DisclaimerScreen(onBack = { navigateBack() })
                    }

                    is Screen.Licenses -> {
                        LicensesScreen(onBack = { navigateBack() })
                    }
                }
            }
        }
    }
}
