package nodal.app.data.model

data class Settings(
    val mathPrecision: Setting = Setting(
        current = "Стандартная",
        options = mapOf(
            "Низкая" to "16",
            "Стандартная" to "32",
            "Высокая" to "64",
            "Научная" to "128"
        ),
        label = "Точность вычислений",
        description = "Количество значащих разрядов вычислений"
    ),
    val graphResolution: Setting = Setting(
        current = "Оптимальное",
        options = mapOf(
            "Производительное" to "50",
            "Оптимальное" to "250",
            "Высокое" to "500",
            "Максимальное" to "1000"
        ),
        label = "Разрешение графика",
        description = "Количество точек для отрисовки кривой"
    ),
    val displayPrecision: Setting = Setting(
        current = "5 знаков",
        options = mapOf(
            "2 знака" to "2",
            "3 знака" to "3",
            "4 знака" to "4",
            "5 знаков" to "5",
            "8 знаков" to "8",
            "10 знаков" to "10"
        ),
        label = "Точность вывода",
        description = "Знаков после запятой в ячейках таблицы разностей"
    ),
    val stepX: Setting = Setting(
        current = "0.1",
        options = mapOf(
            "0.1" to "0.1",
            "0.25" to "0.25",
            "0.5" to "0.5",
            "1" to "1"
        ),
        label = "Шаг для x",
        description = "Расстояние h между соседними точками при добавлении"
    )
)

data class Setting(
    val current: String,
    val options: Map<String, String>,
    val label: String,
    val description: String
) {
    val value: String get() = options[current] ?: options.values.first()
}
