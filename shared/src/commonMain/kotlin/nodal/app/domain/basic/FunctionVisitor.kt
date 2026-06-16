package nodal.app.domain.basic

import com.ionspin.kotlin.bignum.decimal.BigDecimal

interface FunctionVisitor<R> {
    fun visitLagrange(xNodes: List<BigDecimal>, yNodes: List<BigDecimal>): R
    fun visitNewtonFinite(x0: BigDecimal, h: BigDecimal, difTable: List<List<BigDecimal>>, forward: Boolean): R
    fun visitGauss(xMid: BigDecimal, h: BigDecimal, difTable: List<List<BigDecimal>>, midIndex: Int, forward: Boolean): R
    fun visitStirling(xMid: BigDecimal, h: BigDecimal, difTable: List<List<BigDecimal>>, midIndex: Int): R
    fun visitBessel(x0: BigDecimal, h: BigDecimal, difTable: List<List<BigDecimal>>, midIndex: Int): R
}
