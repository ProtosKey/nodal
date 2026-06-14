package proxima.app.presentation.basic

interface Mapper<T, R> {
    fun mapTo(t: T): R
    fun mapFrom(r: R): T
}
