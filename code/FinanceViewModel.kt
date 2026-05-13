package com.example.financecompose.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.example.financecompose.model.AppTab
import com.example.financecompose.model.AppUiState
import com.example.financecompose.model.CategoryStat
import com.example.financecompose.model.Operation

class FinanceViewModel : ViewModel() {
    private val operations = mutableStateListOf(
        Operation("Супермаркет", "Сегодня 14:30", -2840, "Продукты", "▣"),
        Operation("Зарплата", "Вчера 10:00", 82400, "Доход", "◆", true),
        Operation("Такси", "Вчера 22:15", -350, "Транспорт", "◈"),
        Operation("Кофейня", "02 мая", -480, "Кафе", "●"),
        Operation("Фитнес-клуб", "30 апреля", -2500, "Здоровье", "★"),
    )

    var uiState = mutableStateOf(AppUiState(operations = operations))
        private set

    fun selectTab(tab: AppTab) {
        uiState.value = uiState.value.copy(selectedTab = tab)
    }

    fun addOperation(amount: Int, category: String, note: String, expense: Boolean) {
        if (amount <= 0) return
        operations.add(
            0,
            Operation(
                title = category,
                subtitle = note.ifBlank { "Только что" },
                amount = if (expense) -amount else amount,
                category = category,
                icon = iconFor(category),
                income = !expense,
            )
        )
        uiState.value = uiState.value.copy(operations = operations, selectedTab = AppTab.Operations)
    }

    fun setDarkTheme(enabled: Boolean) {
        uiState.value = uiState.value.copy(darkTheme = enabled)
    }

    fun setCurrency(symbol: String) {
        uiState.value = uiState.value.copy(currency = symbol)
    }

    fun setNotifications(enabled: Boolean) {
        uiState.value = uiState.value.copy(notifications = enabled)
    }

    fun setPin(enabled: Boolean) {
        uiState.value = uiState.value.copy(pinEnabled = enabled)
    }

    fun setBiometrics(enabled: Boolean) {
        uiState.value = uiState.value.copy(biometricsEnabled = enabled)
    }

    fun setAnalyticsPeriod(period: String) {
        uiState.value = uiState.value.copy(analyticsPeriod = period)
    }

    fun toggleAnalyticsCategory(category: String) {
        val current = uiState.value.selectedAnalyticsCategory
        uiState.value = uiState.value.copy(selectedAnalyticsCategory = if (current == category) null else category)
    }

    fun totalIncome(): Int = operations.filter { it.income }.sumOf { it.amount }

    fun totalExpense(): Int = operations.filter { !it.income }.sumOf { -it.amount }

    fun categoryStats(colors: List<Color>): List<CategoryStat> {
        val palette = colors.ifEmpty { listOf(Color(0xFF7B4DFF)) }
        return operations
            .filter { !it.income }
            .groupBy { it.category }
            .map { (category, items) -> category to items.sumOf { -it.amount } }
            .sortedByDescending { it.second }
            .mapIndexed { index, item -> CategoryStat(item.first, item.second, palette[index % palette.size]) }
    }

    fun filteredExpenses(): List<Operation> {
        val category = uiState.value.selectedAnalyticsCategory ?: return operations.filter { !it.income }
        return operations.filter { !it.income && it.category == category }
    }

    fun analyticsBars(): List<Int> {
        val values = operations.filter { !it.income }.map { -it.amount }
        val period = uiState.value.analyticsPeriod
        if (values.isEmpty()) return List(if (period == "Год") 12 else 7) { 0 }
        return when (period) {
            "Нед" -> List(7) { index -> values.getOrElse(index % values.size) { 0 } }
            "Год" -> List(12) { index ->
                val base = values.getOrElse(index % values.size) { 0 }
                (base * (index + 2) / 3).coerceAtLeast(base / 2)
            }
            else -> List(6) { index ->
                values.drop(index).filterIndexed { valueIndex, _ -> valueIndex % 2 == 0 }.sum()
                    .coerceAtLeast(values[index % values.size] / 2)
            }
        }
    }

    fun iconFor(category: String) = when (category) {
        "Продукты" -> "▣"
        "Транспорт" -> "◈"
        "Кафе" -> "●"
        "Здоровье" -> "★"
        "Развлечения" -> "◆"
        "Связь" -> "▥"
        else -> "•"
    }
}
