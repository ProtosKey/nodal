package nodal.app.presentation.mapper

import nodal.app.domain.model.Point
import nodal.app.presentation.basic.Mapper
import nodal.app.presentation.model.PointEntry
import nodal.app.presentation.tools.StringParser

object EntryMapper : Mapper<Point, PointEntry> {
    override fun mapTo(t: Point): PointEntry {
        return PointEntry(
            StringParser.prepareToString(t.x),
            StringParser.prepareToString(t.y)
        )
    }

    override fun mapFrom(r: PointEntry): Point {
        return Point(
            StringParser.parseBigDecimal(r.x),
            StringParser.parseBigDecimal(r.y)
        )
    }
}
