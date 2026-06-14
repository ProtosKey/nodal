package proxima.app.presentation.state

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import proxima.app.data.model.FunctionResult
import proxima.app.data.model.FunctionType
import proxima.app.presentation.tools.FunctionParams

data class ResultState(
    val params: FunctionParams = FunctionParams(5),
    val results: Map<FunctionType, FunctionResult> = emptyMap(),
    val isLoading: Boolean = false,
    val count: Long = 32L,
    val interpolateAt: String = "",
    val diffTable: List<List<BigDecimal>> = emptyList(),
    val xNodes: List<BigDecimal> = emptyList(),
    val displayPrecision: Int = 5,
    val hiddenMethods: Set<FunctionType> = emptySet(),
)
