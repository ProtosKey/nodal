package nodal.app.domain.basic

interface CanVisit {
    fun <R> acceptVisitor(visitor: FunctionVisitor<R>): R
}
