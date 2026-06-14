package proxima.app.data.model

enum class FunctionType(val label: String) {
    LAGRANGE("Многочлен Лагранжа"),
    NEWTON_FORWARD("Ньютон (1-я формула)"),
    NEWTON_BACKWARD("Ньютон (2-я формула)"),
    GAUSS_FORWARD("Гаусс (1-я формула)"),
    GAUSS_BACKWARD("Гаусс (2-я формула)"),
    STIRLING("Стирлинг"),
    BESSEL("Бессель")
}
