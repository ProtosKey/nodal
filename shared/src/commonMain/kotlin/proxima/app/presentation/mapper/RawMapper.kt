package proxima.app.presentation.mapper

import proxima.app.data.model.RawPoint
import proxima.app.domain.model.Point
import proxima.app.presentation.model.PointEntry
import proxima.app.presentation.tools.StringParser

object RawMapper {
    fun toPoint(raw: RawPoint): Point = Point(
        StringParser.parseBigDecimal(raw.x),
        StringParser.parseBigDecimal(raw.y)
    )

    fun toEntry(raw: RawPoint): PointEntry {
        val isValid = runCatching {
            StringParser.parseBigDecimal(raw.x)
            StringParser.parseBigDecimal(raw.y)
        }.isSuccess
        return PointEntry(raw.x, raw.y, isValid)
    }

    fun toRaw(point: Point): RawPoint = RawPoint(
        StringParser.prepareToString(point.x),
        StringParser.prepareToString(point.y)
    )
}
