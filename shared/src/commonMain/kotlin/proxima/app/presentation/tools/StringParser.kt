package proxima.app.presentation.tools

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import proxima.app.presentation.exception.ParserException
import kotlin.math.E as KOTLIN_E
import kotlin.math.PI as KOTLIN_PI
import kotlin.math.min

object StringParser {
    private val ZEROS = "0*$".toRegex()
    private val EXTRA = "\\.$".toRegex()

    private val PI = BigDecimal.fromDouble(KOTLIN_PI)
    private val E = BigDecimal.fromDouble(KOTLIN_E)

    fun prepareToString(number: BigDecimal, sign: Int = -1): String {
        val result = prepareNumber(number.toPlainString())
        return if (sign < 0)
            result
        else
            shortResult(result, sign)
    }

    fun shortResult(result: String, sign: Int): String {
        return result.split(".").mapIndexedNotNull { index, string ->
            if (index != 0)
                if (sign == 0)
                    null
                else
                    string.substring(0, min(string.length, sign))
            else
                string
        }.joinToString(".")
    }

    fun prepareNumber(value: String): String {
        var result = value.replace(",", ".").trim()
        if (result.isEmpty()) return ""

        result = when {
            result.startsWith(".") -> "0$result"
            result.startsWith("-.") -> result.replace("-.", "-0.")
            else -> result
        }

        return removeZeros(
            if (result.startsWith("-")) {
                "-" + result.substring(1).replaceFirst("^0+(?=\\d)".toRegex(), "")
            } else {
                result.replaceFirst("^0+(?=\\d)".toRegex(), "")
            }
        )
    }

    fun parseBigDecimal(value: String): BigDecimal {
        parseConstant(value)?.let { return it }
        try {
            return prepareNumber(value).toBigDecimal()
        } catch (_: NumberFormatException) {
            if (value.isEmpty()) {
                throw ParserException("Значение не может быть пустым")
            } else {
                throw ParserException("Значение <$value> должно быть числом")
            }
        }
    }

    private fun parseConstant(value: String): BigDecimal? {
        val trimmed = value.trim().lowercase().replace(" ", "")
        val (sign, body) = when {
            trimmed.startsWith("-") -> -1 to trimmed.substring(1)
            trimmed.startsWith("+") -> 1 to trimmed.substring(1)
            else -> 1 to trimmed
        }
        val base = when (body) {
            "pi", "π" -> PI
            "e" -> E
            else -> return null
        }
        return if (sign < 0) -base else base
    }

    fun removeZeros(value: String): String {
        if (value.isEmpty() || !value.contains(".")) return value
        return value
            .replace(ZEROS, "")
            .replace(EXTRA, "")
    }
}
