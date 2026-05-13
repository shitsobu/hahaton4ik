# Участник 2: Главный экран, операции, добавление операции

## Твоя зона ответственности

Основной файл:

```text
app/src/main/java/com/example/financecompose/view/FinanceApp.kt
```

Функции внутри файла:

```text
FinanceApp
DashboardScreen
OperationsScreen
AddScreen
BalanceCard
OperationRow
CategoryGrid
DarkField
FieldLabel
BottomBar
```

## Что можно улучшать

- главный экран с балансом;
- список последних операций;
- экран всех операций;
- форму добавления расхода/дохода;
- навигацию по вкладкам;
- внешний вид карточек операций.

## Что говорить на защите

Я отвечал за основные пользовательские сценарии: просмотр баланса, просмотр операций и добавление новой операции.  
UI написан на Jetpack Compose, без XML layout. View не хранит бизнес-логику, а вызывает методы `FinanceViewModel`.

## Git-команды

```powershell
git checkout main
git pull
git checkout -b participant-2-main-operations

git add app/src/main/java/com/example/financecompose/view/FinanceApp.kt

git commit -m "Implement dashboard operations and add flow"
git push -u origin participant-2-main-operations
```

