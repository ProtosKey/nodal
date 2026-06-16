package nodal.app.domain.basic

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import nodal.app.domain.model.Coordinates
import nodal.app.domain.model.Function

interface CanSolve {
    fun solve(points: Coordinates, count: Long, point: BigDecimal): Function
}
