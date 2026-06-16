package nodal.app.domain.utils

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import nodal.app.domain.math.DecimalUtils
import nodal.app.domain.model.Point
import kotlin.random.Random

object PointFactory {
    fun random(): Point = Point(randomValue(), randomValue())

    fun randomValue(): BigDecimal =
        Random.nextInt(1100).toBigDecimal()
            .divide("100".toBigDecimal(), DecimalUtils.getMode(2L))
}
