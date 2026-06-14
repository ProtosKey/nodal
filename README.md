# Nodal

**Nodal** — Kotlin Multiplatform Compose приложение для полиномиальной интерполяции табличных функций или набора точек. Реализует семь классических методов и визуализирует полученные многочлены в реальном времени.

## Возможности

* **7 методов интерполяции:** Лагранж, Ньютон (вперёд / назад) с конечными разностями, Гаусс (вперёд / назад), Стирлинг, Бессель.
* **Классическая трактовка:** каждая формула считается оптимальной в своей области таблицы. Если точка интерполяции выходит за «штатный» диапазон метода — отображается плашка-предупреждение, но значение всё равно вычисляется.
* **Высокая точность:** все расчёты на `BigDecimal` (`kotlin-bignum`) с настраиваемым числом знаков.
* **Интерактивный график:** одновременная отрисовка кривых всех методов, переключение видимости отдельных кривых, цвет на метод.
* **Multiplatform:** общий код Compose Multiplatform под Android и iOS.

## Технологический стек

* **UI:** Compose Multiplatform (Material 3, `materialKolor` для динамической палитры).
* **Платформы:** Android `androidApp/`, iOS `iosApp/`, общий код в `shared`.
* **Навигация:** Voyager.
* **DI:** Koin.
* **Архитектура:** MVVM + UDF, общий `MainStore` для глобального состояния.
* **Математика:** `kotlin-bignum` (`BigDecimal`).

## Структура

```
shared/src/commonMain/kotlin/proxima/app/
├── data/            # MainStore — общее состояние
├── di/              # Koin-модули
├── domain/
│   ├── basic/       # Интерфейсы CanSolve, CanVisit, FunctionVisitor
│   ├── math/        # DifferenceEngine, DecimalUtils
│   ├── model/       # Function (Lagrange/NewtonFinite/Gauss/Stirling/Bessel), Coordinates
│   ├── solver/      # 7 *Solver объектов — выбор узлов и сборка Function
│   └── utils/
├── presentation/    # ViewModel + State для каждого экрана
├── theme/           # MethodColor — цвет на метод, LocalAppDimens
└── view/feature/
    ├── input/       # Ввод узлов таблицы
    ├── graph/       # Совмещённый график всех методов
    ├── result/      # Карточки методов: формула, значение, плашка-предупреждение
    └── settings/    # Точность, тема и т.п.
```

## Архитектура расчётов

Каждый `*Solver` (`shared/.../domain/solver/`) принимает `Coordinates`, число знаков `count` и точку интерполяции `point`, и возвращает объект `Function`:

* `LagrangeSolver` — никакой подготовки, кладёт все узлы в `Function.Lagrange`.
* `NewtonFiniteForward/BackwardSolver` — требуют равноотстоящих узлов (`DifferenceEngine.verifyEquidistant`), считают таблицу конечных разностей, передают `x.first()` / `x.last()` как опорный узел.
* `GaussForward/BackwardSolver` — таблица разностей + опорный узел в середине таблицы (`m = (size−1)/2` для forward, `m = size/2` для backward).
* `StirlingSolver` / `BesselSolver` — таблица разностей + опорный узел, **ближайший** к точке интерполяции `point` (а не середина таблицы). Это сознательный выбор: у краёв таблицы Стирлинг/Бессель могут вырождаться до многочлена меньшей степени — это ожидаемое поведение классической трактовки.

`Function.calculate(value, count)` всегда возвращает значение. Для проверки «штатного» диапазона есть `Function.warningAt(value, count): String?` — текст выводится в UI как плашка `tertiaryContainer`.

## Сборка и запуск

Требования: JDK 17, Android SDK, Xcode (для iOS-таргета).

### Android

```bash
./gradlew :androidApp:installDebug
```

### iOS

Открыть `iosApp/iosApp.xcodeproj` в Xcode и запустить. Сборка общего фреймворка — `./gradlew :shared:linkDebugFrameworkIosSimulatorArm64`.

### Тесты

```bash
./gradlew :shared:allTests
```

## Пример

Введите таблицу:

| x   | y      |
|-----|--------|
| 0.5  | 1.5320 |
| 0.55 | 2.5356 |
| 0.60 | 3.5406 |
| 0.65 | 4.5462 |
| 0.70 | 5.5504 |

Точка интерполяции: `x = 0.62`.

Каждый из семи методов построит свой интерполяционный многочлен 4-й степени; в карточке результата будет:
* значение в точке,
* плашка-предупреждение, если точка вышла за штатный диапазон метода.

На графике все семь кривых отрисовываются одновременно своими цветами (`theme/MethodColor.kt`); видимость каждой можно переключить в карточке результата.

