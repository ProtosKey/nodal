package nodal.app.presentation.tools

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import nodal.app.domain.basic.FunctionVisitor

class FunctionParams(private val count: Int) : FunctionVisitor<String?> {
    private fun fmt(n: BigDecimal) = StringParser.prepareToString(n, count)

    override fun visitLagrange(xNodes: List<BigDecimal>, yNodes: List<BigDecimal>): String? = null

    override fun visitNewtonFinite(
        x0: BigDecimal,
        h: BigDecimal,
        difTable: List<List<BigDecimal>>,
        forward: Boolean
    ): String = if (forward)
        "x₀ = ${fmt(x0)},  h = ${fmt(h)}  ·  начало таблицы"
    else
        "xₙ = ${fmt(x0)},  h = ${fmt(h)}  ·  конец таблицы"

    override fun visitGauss(
        xMid: BigDecimal,
        h: BigDecimal,
        difTable: List<List<BigDecimal>>,
        midIndex: Int,
        forward: Boolean
    ): String = "x₀ = ${fmt(xMid)},  h = ${fmt(h)}  ·  середина таблицы"

    override fun visitStirling(
        xMid: BigDecimal,
        h: BigDecimal,
        difTable: List<List<BigDecimal>>,
        midIndex: Int
    ): String = "x₀ = ${fmt(xMid)},  h = ${fmt(h)}  ·  середина таблицы"

    override fun visitBessel(
        x0: BigDecimal,
        h: BigDecimal,
        difTable: List<List<BigDecimal>>,
        midIndex: Int
    ): String = "x₀ = ${fmt(x0)},  h = ${fmt(h)}  ·  около середины"
}
