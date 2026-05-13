package com.example.financecompose.model

import androidx.compose.ui.graphics.Color

data class Operation(
    val title: String,
    val subtitle: String,
    val amount: Int,
    val category: String,
    val icon: String,
    val income: Boolean = false,
)

data class CategoryStat(
    val name: String,
    val value: Int,
    val color: Color,
)

data class AppUiState(
    val operations: List<Operation> = emptyList(),
    val selectedTab: AppTab = AppTab.Home,
    val darkTheme: Boolean = true,
    val currency: String = "₽",
    val notifications: Boolean = true,
    val pinEnabled: Boolean = true,
    val biometricsEnabled: Boolean = false,
    val analyticsPeriod: String = "Мес",
    val selectedAnalyticsCategory: String? = null,
)

enum class AppTab(val label: String, val icon: String) {
    Home("Главная", "⌂"),
    Operations("Операции", "↕"),
    Add("", "+"),
    Analytics("Аналитика", "◉"),
    Profile("Профиль", "☰")
}

enum class ProfileDialog {
    MainCard, Savings, Investments, Security, Currency, Help
}
