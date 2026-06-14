package proxima.app.domain.model

import proxima.app.domain.exception.InitException

class Coordinates(points: MutableList<Point>) : Iterable<Point> {
    companion object {
        const val MAX_SIZE = 30
        const val MIN_SIZE = 3
    }

    var points = points
        set(value) {
            if (value.size > MAX_SIZE)
                throw InitException("Превышен лимит в $MAX_SIZE точек")
            if (value.size < MIN_SIZE)
                throw InitException("Количество точек меньше минимума в $MIN_SIZE точек")
            field = value
        }

    init {
        if (points.size > MAX_SIZE) {
            throw InitException("Превышен лимит в $MAX_SIZE точек")
        }
    }

    operator fun set(index: Int, point: Point) {
        points[index] = point
    }

    operator fun get(index: Int): Point = points[index]

    override fun iterator() = points.iterator()

    val size: Int get() = points.size
}
