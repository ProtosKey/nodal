package proxima.app.data.model

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import proxima.app.domain.model.Function

sealed class FunctionResult {
    data class Success(
        val function: Function,
        val value: BigDecimal,
        val warning: String? = null,
    ) : FunctionResult()

    data class Error(val message: String) : FunctionResult()
}
