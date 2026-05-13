# Участник 1: Архитектура, Model, ViewModel

## Твоя зона ответственности

Основные файлы:

```text
app/src/main/java/com/example/financecompose/MainActivity.kt
app/src/main/java/com/example/financecompose/model/FinanceModels.kt
app/src/main/java/com/example/financecompose/viewmodel/FinanceViewModel.kt
```

## Что можно улучшать

- модели данных операций;
- состояние приложения `AppUiState`;
- логику добавления операций;
- расчеты доходов, расходов, аналитики;
- переключение темы, валюты и настроек;
- подготовку данных для экранов.

## Что говорить на защите

Я отвечал за MVVM-архитектуру. `MainActivity` только запускает Compose и получает `FinanceViewModel`.  
`Model` содержит структуры данных, `ViewModel` хранит состояние приложения и бизнес-логику, а `View` только отображает состояние и вызывает методы ViewModel.

## Git-команды

```powershell
git checkout main
git pull
git checkout -b participant-1-viewmodel

git add app/src/main/java/com/example/financecompose/MainActivity.kt
git add app/src/main/java/com/example/financecompose/model/FinanceModels.kt
git add app/src/main/java/com/example/financecompose/viewmodel/FinanceViewModel.kt

git commit -m "Implement MVVM model and viewmodel"
git push -u origin participant-1-viewmodel
```

