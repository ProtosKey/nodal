package proxima.app.presentation.mapper

import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import proxima.app.domain.math.toFloat
import proxima.app.domain.model.Point
import proxima.app.presentation.basic.Mapper
import proxima.app.presentation.model.PointData

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
