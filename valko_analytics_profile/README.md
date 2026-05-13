# Участник 3: Аналитика, профиль, настройки

## Твоя зона ответственности

Основной файл:

```text
app/src/main/java/com/example/financecompose/view/FinanceApp.kt
```

Функции внутри файла:

```text
AnalyticsScreen
ProfileScreen
BarChart
DonutChart
Legend
CategoryProgress
AccountDialog
SecurityDialog
CurrencyDialog
HelpDialog
SwitchRow
ActionRow
AccountRow
```

## Что можно улучшать

- графики расходов;
- диаграмму категорий;
- выбор периода `Нед / Мес / Год`;
- выбор категории в аналитике;
- профиль пользователя;
- настройки темы, валюты, уведомлений и безопасности.

## Что говорить на защите

Я отвечал за аналитику и профиль. Аналитика не статическая: данные берутся из операций, категории и графики пересчитываются после добавления новой операции.  
Профиль содержит рабочие настройки: смена темы, смена валюты, уведомления, PIN-код и биометрия.

## Git-команды

```powershell
git checkout main
git pull
git checkout -b participant-3-analytics-profile

git add app/src/main/java/com/example/financecompose/view/FinanceApp.kt

git commit -m "Implement analytics profile and settings"
git push -u origin participant-3-analytics-profile
```

