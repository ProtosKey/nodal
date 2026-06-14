package proxima.app.domain.basic

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import proxima.app.domain.model.Coordinates
import proxima.app.domain.model.Function

interface CanSolve {
    fun solve(points: Coordinates, count: Long, point: BigDecimal): Function
}
