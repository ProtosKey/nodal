package nodal.app.presentation.mapper

import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import nodal.app.domain.math.toFloat
import nodal.app.domain.model.Point
import nodal.app.presentation.basic.Mapper
import nodal.app.presentation.model.PointData

object DataMapper : Mapper<Point, PointData> {
    override fun mapTo(t: Point): PointData {
        return PointData(
            t.x.toFloat(),
            t.y.toFloat()
        )
    }

    override fun mapFrom(r: PointData): Point {
        return Point(
            r.x.toBigDecimal(),
            r.y.toBigDecimal()
        )
    }
}
