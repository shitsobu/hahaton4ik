package com.example.financecompose.view

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financecompose.model.AppTab
import com.example.financecompose.model.CategoryStat
import com.example.financecompose.model.Operation
import com.example.financecompose.model.ProfileDialog
import com.example.financecompose.viewmodel.FinanceViewModel
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.abs

private data class AppColors(
    val bg: Color,
    val card: Color,
    val card2: Color,
    val text: Color,
    val muted: Color,
    val purple: Color = Color(0xFF7B4DFF),
    val cyan: Color = Color(0xFF24D7C2),
    val green: Color = Color(0xFF25D98F),
    val red: Color = Color(0xFFFF5B7B),
)

private val DarkColors = AppColors(Color(0xFF11101B), Color(0xFF201F35), Color(0xFF282642), Color(0xFFF8F7FF), Color(0xFF9290A8))
private val LightColors = AppColors(Color(0xFFF5F3FA), Color.White, Color(0xFFECE8F7), Color(0xFF171323), Color(0xFF706A82))
private val LocalColors = compositionLocalOf { DarkColors }
private val LocalCurrency = compositionLocalOf { "₽" }

@Composable
fun FinanceApp(viewModel: FinanceViewModel) {
    val state = viewModel.uiState.value
    CompositionLocalProvider(
        LocalColors provides if (state.darkTheme) DarkColors else LightColors,
        LocalCurrency provides state.currency,
    ) {
        val colors = LocalColors.current
        MaterialTheme {
            Surface(color = colors.bg, modifier = Modifier.fillMaxSize()) {
                Box(Modifier.fillMaxSize()) {
                    when (state.selectedTab) {
                        AppTab.Home -> DashboardScreen(state.operations) { viewModel.selectTab(AppTab.Operations) }
                        AppTab.Operations -> OperationsScreen(state.operations)
                        AppTab.Add -> AddScreen(
                            iconFor = viewModel::iconFor,
                            onSave = viewModel::addOperation,
                        )
                        AppTab.Analytics -> AnalyticsScreen(viewModel)
                        AppTab.Profile -> ProfileScreen(viewModel)
                    }
                    BottomBar(state.selectedTab, viewModel::selectTab)
                }
            }
        }
    }
}

@Composable
private fun ScreenScaffold(title: String, content: @Composable ColumnScope.() -> Unit) {
    val colors = LocalColors.current
    Column(
        Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding().padding(horizontal = 20.dp).padding(bottom = 90.dp)
    ) {
        Spacer(Modifier.height(14.dp))
        Text(title, color = colors.text, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))
        content()
    }
}

@Composable
private fun ScreenLazyScaffold(title: String, content: LazyListScope.() -> Unit) {
    val colors = LocalColors.current
    LazyColumn(
        Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding().padding(horizontal = 20.dp).padding(bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Spacer(Modifier.height(14.dp))
            Text(title, color = colors.text, fontSize = 32.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
        }
        content()
        item { Spacer(Modifier.height(18.dp)) }
    }
}

@Composable
private fun DashboardScreen(operations: List<Operation>, onAll: () -> Unit) {
    val colors = LocalColors.current
    ScreenLazyScaffold("Мои финансы") {
        item { Text("Привет, Дима ✦", color = colors.muted, fontSize = 16.sp) }
        item { BalanceCard(operations) }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SmallMetric("Накопления", money(15200), Modifier.weight(1f))
                SmallMetric("Инвестиции", money(48000), Modifier.weight(1f))
            }
        }
        item {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("Последние операции", color = colors.text, fontWeight = FontWeight.Bold, fontSize = 21.sp, modifier = Modifier.weight(1f))
                Text("Все →", color = colors.purple, fontWeight = FontWeight.Bold, fontSize = 20.sp, modifier = Modifier.clickable { onAll() })
            }
        }
        items(operations.take(4)) { OperationRow(it) }
    }
}

@Composable
private fun OperationsScreen(operations: List<Operation>) {
    val colors = LocalColors.current
    var filter by rememberSaveable { mutableStateOf("Май") }
    ScreenLazyScaffold("Операции") {
        item { Segments(listOf("Апр", "Май", "Июн"), filter) { filter = it } }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SmallMetric("Доходы", "+ ${money(operations.filter { it.income }.sumOf { it.amount })}", Modifier.weight(1f), colors.green)
                SmallMetric("Расходы", "- ${money(operations.filter { !it.income }.sumOf { -it.amount })}", Modifier.weight(1f), colors.red)
            }
        }
        item { Text("Сегодня", color = colors.muted, fontSize = 16.sp) }
        items(operations.filter { !it.income }.take(4)) { OperationRow(it) }
        item { Text("Вчера", color = colors.muted, fontSize = 16.sp) }
        items(operations.filter { it.income }) { OperationRow(it) }
    }
}

@Composable
private fun AddScreen(iconFor: (String) -> String, onSave: (Int, String, String, Boolean) -> Unit) {
    val colors = LocalColors.current
    var expense by rememberSaveable { mutableStateOf(true) }
    var amount by rememberSaveable { mutableStateOf("2500") }
    var category by rememberSaveable { mutableStateOf("Продукты") }
    var note by rememberSaveable { mutableStateOf("") }

    ScreenScaffold("Новая операция") {
        Segments(listOf("Расход", "Доход"), if (expense) "Расход" else "Доход") { expense = it == "Расход" }
        Spacer(Modifier.height(18.dp))
        FieldLabel("Сумма")
        DarkField(amount, { amount = it.filter(Char::isDigit).take(7) }, prefix = "${LocalCurrency.current} ", keyboardType = KeyboardType.Number)
        Spacer(Modifier.height(18.dp))
        FieldLabel("Категория")
        CategoryGrid(category, iconFor) { category = it }
        Spacer(Modifier.height(18.dp))
        FieldLabel("Примечание")
        DarkField(note, { note = it }, placeholder = "Добавить заметку...")
        Spacer(Modifier.weight(1f))
        Button(
            onClick = { onSave(amount.toIntOrNull() ?: 0, category, note, expense) },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            modifier = Modifier.fillMaxWidth().height(56.dp).background(Brush.horizontalGradient(listOf(colors.purple, colors.cyan)), RoundedCornerShape(24.dp))
        ) {
            Text("Сохранить операцию", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun AnalyticsScreen(viewModel: FinanceViewModel) {
    val state = viewModel.uiState.value
    val colors = LocalColors.current
    val stats = viewModel.categoryStats(listOf(colors.purple, colors.cyan, colors.green, Color(0xFFFFD86B), colors.red))
    val totalExpense = viewModel.totalExpense()
    val activeCategory = state.selectedAnalyticsCategory ?: stats.firstOrNull()?.name
    val bars = viewModel.analyticsBars()
    ScreenLazyScaffold("Аналитика") {
        item { Segments(listOf("Нед", "Мес", "Год"), state.analyticsPeriod, viewModel::setAnalyticsPeriod) }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SmallMetric("Доходы", "+ ${money(viewModel.totalIncome())}", Modifier.weight(1f), colors.green)
                SmallMetric("Расходы", "- ${money(totalExpense)}", Modifier.weight(1f), colors.red)
            }
        }
        item {
            Panel {
                Text("Расходы: ${state.analyticsPeriod}", color = colors.muted, fontSize = 12.sp)
                Spacer(Modifier.height(12.dp))
                BarChart(bars)
                Spacer(Modifier.height(8.dp))
                Text("Пик: ${compactMoney(bars.maxOrNull() ?: 0)}", color = colors.text, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
        item {
            Panel {
                Text("По категориям", color = colors.muted, fontSize = 12.sp)
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    DonutChart(stats, totalExpense)
                    Spacer(Modifier.width(18.dp))
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        stats.take(3).forEach { stat ->
                            val percent = if (totalExpense == 0) 0 else stat.value * 100 / totalExpense
                            Legend(stat.name, "$percent%", stat.color, activeCategory == stat.name) { viewModel.toggleAnalyticsCategory(stat.name) }
                        }
                    }
                }
            }
        }
        item { Text(if (activeCategory == null) "Топ категории" else "Операции: $activeCategory", color = colors.text, fontWeight = FontWeight.Bold, fontSize = 18.sp) }
        items(stats) { stat ->
            CategoryProgress(stat.name, stat.value, stats.maxOfOrNull { it.value } ?: 1, activeCategory == stat.name) {
                viewModel.toggleAnalyticsCategory(stat.name)
            }
        }
        items(viewModel.filteredExpenses().filter { activeCategory == null || it.category == activeCategory }) { OperationRow(it) }
    }
}

@Composable
private fun ProfileScreen(viewModel: FinanceViewModel) {
    val state = viewModel.uiState.value
    val colors = LocalColors.current
    var dialog by rememberSaveable { mutableStateOf<ProfileDialog?>(null) }

    ScreenLazyScaffold("Профиль") {
        item {
            Panel {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(72.dp).clip(CircleShape).background(colors.purple), contentAlignment = Alignment.Center) {
                        Text("Д", color = Color.White, fontSize = 36.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.width(18.dp))
                    Column {
                        Text("Дима Волков", color = colors.text, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        Text("dvolkov@gmail.com", color = colors.muted, fontSize = 16.sp)
                        Text("★ Premium", color = Color(0xFFFFD86B), fontSize = 15.sp)
                    }
                }
            }
        }
        item { SectionTitle("Мои счета") }
        item { AccountRow("Основная карта", "Сбербанк", money(124350)) { dialog = ProfileDialog.MainCard } }
        item { AccountRow("Накопительный", "Тинькофф", money(86000)) { dialog = ProfileDialog.Savings } }
        item { AccountRow("Инвестиции", "ВТБ", money(38000)) { dialog = ProfileDialog.Investments } }
        item { SectionTitle("Настройки") }
        item { SwitchRow("Уведомления", state.notifications, viewModel::setNotifications) }
        item { ActionRow("Безопасность") { dialog = ProfileDialog.Security } }
        item { SwitchRow("Темная тема", state.darkTheme, viewModel::setDarkTheme) }
        item { ActionRow("Валюта: ${state.currency}") { dialog = ProfileDialog.Currency } }
        item { ActionRow("Помощь") { dialog = ProfileDialog.Help } }
    }

    when (dialog) {
        ProfileDialog.MainCard -> AccountDialog("Основная карта", "Сбербанк", money(124350), "Последняя операция: супермаркет, ${money(2840)}") { dialog = null }
        ProfileDialog.Savings -> AccountDialog("Накопительный", "Тинькофф", money(86000), "Цель: отпуск. Прогресс: 57%") { dialog = null }
        ProfileDialog.Investments -> AccountDialog("Инвестиции", "ВТБ", money(38000), "Доходность за месяц: +4.8%") { dialog = null }
        ProfileDialog.Security -> SecurityDialog(state.pinEnabled, state.biometricsEnabled, viewModel::setPin, viewModel::setBiometrics) { dialog = null }
        ProfileDialog.Currency -> CurrencyDialog(state.currency, viewModel::setCurrency) { dialog = null }
        ProfileDialog.Help -> HelpDialog { dialog = null }
        null -> Unit
    }
}

@Composable
private fun AccountDialog(title: String, subtitle: String, amount: String, detail: String, onClose: () -> Unit) {
    AppDialog(title, onClose) {
        Text(subtitle, color = LocalColors.current.muted, fontSize = 15.sp)
        Spacer(Modifier.height(10.dp))
        Text(amount, color = LocalColors.current.text, fontSize = 26.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        Text(detail, color = LocalColors.current.muted, fontSize = 15.sp)
    }
}

@Composable
private fun SecurityDialog(pin: Boolean, bio: Boolean, onPin: (Boolean) -> Unit, onBio: (Boolean) -> Unit, onClose: () -> Unit) {
    AppDialog("Безопасность", onClose) {
        SwitchRow("PIN-код", pin, onPin)
        Spacer(Modifier.height(8.dp))
        SwitchRow("Вход по биометрии", bio, onBio)
    }
}

@Composable
private fun CurrencyDialog(selected: String, onCurrency: (String) -> Unit, onClose: () -> Unit) {
    AppDialog("Валюта", onClose) {
        listOf("₽" to "Российский рубль", "$" to "Доллар США", "€" to "Евро").forEach { (symbol, title) ->
            Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).clickable { onCurrency(symbol) }.padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = selected == symbol, onClick = { onCurrency(symbol) }, colors = RadioButtonDefaults.colors(selectedColor = LocalColors.current.purple))
                Text("$symbol  $title", color = LocalColors.current.text, fontSize = 16.sp)
            }
        }
    }
}

@Composable
private fun HelpDialog(onClose: () -> Unit) {
    AppDialog("Помощь", onClose) {
        Text("MVP финансового трекера:", color = LocalColors.current.text, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text("• учет операций\n• категории\n• аналитика\n• настройки профиля\n• смена темы и валюты", color = LocalColors.current.muted, fontSize = 15.sp)
    }
}

@Composable
private fun AppDialog(title: String, onClose: () -> Unit, content: @Composable ColumnScope.() -> Unit) {
    val colors = LocalColors.current
    AlertDialog(
        onDismissRequest = onClose,
        confirmButton = { TextButton(onClick = onClose) { Text("Готово", color = colors.purple) } },
        containerColor = colors.card,
        title = { Text(title, color = colors.text, fontWeight = FontWeight.Bold) },
        text = { Column(content = content) },
    )
}

@Composable
private fun BalanceCard(operations: List<Operation>) {
    val colors = LocalColors.current
    val balance = 248350 + operations.sumOf { it.amount }
    Box(
        Modifier.fillMaxWidth().heightIn(min = 168.dp).background(Brush.linearGradient(listOf(colors.purple, Color(0xFF5A8BFF), colors.cyan)), RoundedCornerShape(22.dp)).padding(20.dp)
    ) {
        Column(Modifier.fillMaxWidth(.78f)) {
            Text("Общий баланс", color = Color.White.copy(alpha = .72f), fontSize = 16.sp)
            Text(compactMoney(balance), color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold, maxLines = 1)
            Spacer(Modifier.height(18.dp))
            Text("+8.4% за этот месяц", color = Color.White.copy(.85f), fontSize = 15.sp)
            Text("Доходы ${money(82400)}  Расходы ${money(34150)}", color = Color.White.copy(.8f), fontSize = 15.sp, maxLines = 1)
        }
        Canvas(Modifier.align(Alignment.BottomEnd).size(116.dp, 86.dp)) {
            listOf(.32f, .52f, .42f, .72f, .62f, .85f, .96f).forEachIndexed { i, value ->
                val x = i * size.width / 7 + 8f
                drawLine(Color.White.copy(alpha = .42f), Offset(x, size.height), Offset(x, size.height * (1 - value)), 8f, StrokeCap.Round)
            }
        }
    }
}

@Composable
private fun BoxScope.BottomBar(selected: AppTab, onSelect: (AppTab) -> Unit) {
    val colors = LocalColors.current
    Row(Modifier.align(Alignment.BottomCenter).fillMaxWidth().navigationBarsPadding().height(74.dp).background(colors.card).padding(horizontal = 18.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        AppTab.entries.forEach { tab ->
            val active = tab == selected
            val isAdd = tab == AppTab.Add
            Column(
                Modifier.size(if (isAdd) 56.dp else 62.dp).clip(if (isAdd) CircleShape else RoundedCornerShape(12.dp)).background(if (isAdd) colors.purple else Color.Transparent).clickable { onSelect(tab) },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(tab.icon, color = if (isAdd) Color.White else if (active) colors.text else colors.muted, fontSize = if (isAdd) 28.sp else 22.sp, fontWeight = FontWeight.Bold)
                if (!isAdd) Text(tab.label, color = if (active) colors.purple else colors.muted, fontSize = 11.sp, maxLines = 1)
            }
        }
    }
}

@Composable
private fun SmallMetric(label: String, value: String, modifier: Modifier = Modifier, color: Color = LocalColors.current.text) {
    val colors = LocalColors.current
    Panel(modifier.heightIn(min = 92.dp)) {
        Text(label, color = colors.muted, fontSize = 16.sp, maxLines = 1)
        Spacer(Modifier.height(8.dp))
        Text(value, color = color, fontWeight = FontWeight.Bold, fontSize = 24.sp, maxLines = 1)
    }
}

@Composable
private fun OperationRow(operation: Operation) {
    val colors = LocalColors.current
    Panel(Modifier.heightIn(min = 88.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(56.dp).clip(RoundedCornerShape(18.dp)).background(colors.card2), contentAlignment = Alignment.Center) {
                Text(operation.icon, color = colors.text, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(operation.title, color = colors.text, fontSize = 20.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(operation.subtitle, color = colors.muted, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Spacer(Modifier.width(8.dp))
            Text((if (operation.income) "+ " else "- ") + compactMoney(abs(operation.amount)), color = if (operation.income) colors.green else colors.red, fontWeight = FontWeight.Bold, fontSize = 16.sp, maxLines = 1)
        }
    }
}

@Composable
private fun Segments(values: List<String>, selected: String, onSelect: (String) -> Unit) {
    val colors = LocalColors.current
    Row(Modifier.fillMaxWidth().height(54.dp).background(colors.card, RoundedCornerShape(28.dp)).padding(5.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        values.forEach {
            Box(Modifier.weight(1f).clip(RoundedCornerShape(24.dp)).background(if (it == selected) Brush.horizontalGradient(listOf(colors.red, colors.purple)) else Brush.linearGradient(listOf(colors.card, colors.card))).clickable { onSelect(it) }, contentAlignment = Alignment.Center) {
                Text(it, color = if (it == selected) Color.White else colors.muted, fontWeight = FontWeight.Bold, fontSize = 17.sp)
            }
        }
    }
}

@Composable
private fun CategoryGrid(selected: String, iconFor: (String) -> String, onSelect: (String) -> Unit) {
    val colors = LocalColors.current
    listOf("Продукты", "Транспорт", "Кафе", "Здоровье", "Развлечения", "Связь").chunked(3).forEach { row ->
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            row.forEach {
                Box(Modifier.weight(1f).height(66.dp).clip(RoundedCornerShape(14.dp)).background(if (it == selected) colors.purple else colors.card).clickable { onSelect(it) }, contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(iconFor(it), color = if (it == selected) Color.White else colors.text)
                        Text(it, color = if (it == selected) Color.White else colors.text, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
            }
        }
        Spacer(Modifier.height(10.dp))
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(text, color = LocalColors.current.muted, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    Spacer(Modifier.height(8.dp))
}

@Composable
private fun DarkField(value: String, onValue: (String) -> Unit, prefix: String = "", placeholder: String = "", keyboardType: KeyboardType = KeyboardType.Text) {
    val colors = LocalColors.current
    TextField(
        value = value,
        onValueChange = onValue,
        prefix = { if (prefix.isNotBlank()) Text(prefix, color = colors.muted, fontWeight = FontWeight.Bold) },
        placeholder = { Text(placeholder, color = colors.muted) },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = true,
        colors = TextFieldDefaults.colors(focusedTextColor = colors.text, unfocusedTextColor = colors.text, focusedContainerColor = colors.card, unfocusedContainerColor = colors.card, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent, cursorColor = colors.purple),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth().height(58.dp)
    )
}

@Composable
private fun Panel(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier.fillMaxWidth().background(LocalColors.current.card, RoundedCornerShape(20.dp)).padding(18.dp), content = content)
}

@Composable
private fun SectionTitle(text: String) {
    Spacer(Modifier.height(14.dp))
    Text(text, color = LocalColors.current.text, fontWeight = FontWeight.Bold, fontSize = 24.sp)
}

@Composable
private fun ActionRow(title: String, onClick: () -> Unit) {
    val colors = LocalColors.current
    Panel(Modifier.heightIn(min = 72.dp).clickable { onClick() }) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("•", color = colors.purple, fontSize = 24.sp)
            Spacer(Modifier.width(14.dp))
            Text(title, color = colors.text, fontSize = 18.sp, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text("›", color = colors.muted, fontSize = 24.sp)
        }
    }
}

@Composable
private fun SwitchRow(title: String, checked: Boolean, onChecked: (Boolean) -> Unit) {
    val colors = LocalColors.current
    Panel(Modifier.heightIn(min = 72.dp).clickable { onChecked(!checked) }) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("•", color = colors.purple, fontSize = 24.sp)
            Spacer(Modifier.width(14.dp))
            Text(title, color = colors.text, fontSize = 18.sp, modifier = Modifier.weight(1f))
            Switch(checked = checked, onCheckedChange = onChecked)
        }
    }
}

@Composable
private fun BarChart(values: List<Int>) {
    val colors = LocalColors.current
    Canvas(Modifier.fillMaxWidth().height(120.dp)) {
        val width = size.width / values.size
        val maxValue = (values.maxOrNull() ?: 1).coerceAtLeast(1)
        values.forEachIndexed { i, v ->
            val h = (size.height * v / maxValue).coerceAtLeast(8f)
            drawRoundRect(if (v == maxValue) colors.purple else colors.card2, Offset(i * width + width * .22f, size.height - h), Size(width * .48f, h), androidx.compose.ui.geometry.CornerRadius(10f, 10f))
        }
    }
}

@Composable
private fun DonutChart(stats: List<CategoryStat>, total: Int) {
    val colors = LocalColors.current
    val currency = LocalCurrency.current
    Canvas(Modifier.size(96.dp)) {
        var start = -90f
        if (total == 0 || stats.isEmpty()) {
            drawArc(colors.card2, start, 360f, false, style = Stroke(18f, cap = StrokeCap.Round))
        } else {
            stats.forEach {
                val sweep = it.value * 360f / total
                drawArc(it.color, start, sweep, false, style = Stroke(18f, cap = StrokeCap.Round))
                start += sweep
            }
        }
        drawCircle(colors.card, radius = 28f)
        drawContext.canvas.nativeCanvas.drawText("$currency${total / 1000}K", size.width / 2f - 28f, size.height / 2f + 8f, android.graphics.Paint().apply {
            color = android.graphics.Color.WHITE
            textSize = 24f
            isFakeBoldText = true
        })
    }
}

@Composable
private fun Legend(name: String, percent: String, color: Color, selected: Boolean, onClick: () -> Unit) {
    val colors = LocalColors.current
    Row(Modifier.clip(RoundedCornerShape(10.dp)).background(if (selected) colors.card2 else Color.Transparent).clickable { onClick() }.padding(horizontal = 6.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(8.dp).clip(CircleShape).background(color))
        Spacer(Modifier.width(8.dp))
        Text(name, color = colors.text, modifier = Modifier.weight(1f), fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(percent, color = colors.muted, fontSize = 12.sp)
    }
}

@Composable
private fun CategoryProgress(name: String, value: Int, maxValue: Int, selected: Boolean, onClick: () -> Unit) {
    val colors = LocalColors.current
    Panel(Modifier.clickable { onClick() }) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("•", color = colors.text, modifier = Modifier.width(28.dp))
            Column(Modifier.weight(1f)) {
                Text(name, color = colors.text, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(Modifier.height(7.dp))
                Box(Modifier.fillMaxWidth().height(4.dp).background(colors.card2, RoundedCornerShape(2.dp))) {
                    Box(Modifier.fillMaxWidth((value.toFloat() / maxValue.coerceAtLeast(1)).coerceIn(0f, 1f)).height(4.dp).background(if (selected) colors.cyan else colors.purple, RoundedCornerShape(2.dp)))
                }
            }
            Spacer(Modifier.width(12.dp))
            Text(compactMoney(value), color = if (selected) colors.cyan else colors.purple, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
    }
}

@Composable
private fun AccountRow(title: String, subtitle: String, amount: String, onClick: () -> Unit) {
    val colors = LocalColors.current
    Panel(Modifier.heightIn(min = 82.dp).clickable { onClick() }) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("▰", color = Color(0xFFFFD86B), modifier = Modifier.width(34.dp), fontSize = 20.sp)
            Column(Modifier.weight(1f)) {
                Text(title, color = colors.text, fontSize = 18.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(subtitle, color = colors.muted, fontSize = 12.sp, maxLines = 1)
            }
            Text(amount, color = colors.text, fontSize = 18.sp, fontWeight = FontWeight.Bold, maxLines = 1)
        }
    }
}

@Composable
private fun money(value: Int): String {
    val format = NumberFormat.getNumberInstance(Locale("ru", "RU"))
    return "${LocalCurrency.current} ${format.format(value)}"
}

@Composable
private fun compactMoney(value: Int): String {
    val sign = if (value < 0) "-" else ""
    val amount = abs(value)
    return if (amount >= 10_000_000) "$sign${LocalCurrency.current} ${amount / 1_000_000} млн" else sign + money(amount)
}
