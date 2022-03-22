/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.functions

import space.kscience.kmath.operations.Ring


public data class ListRationalFunction<C> internal constructor (
    public override val numerator: ListPolynomial<C>,
    public override val denominator: ListPolynomial<C>
) : RationalFunction<C, ListPolynomial<C>> {
    override fun toString(): String = "RationalFunction${numerator.coefficients}/${denominator.coefficients}"
}

// Waiting for context receivers :( TODO: Replace with context receivers when they will be available

//context(RationalFunctionSpace<C, A>)
//@Suppress("FunctionName")
//internal fun <C, A: Ring<C>> RationalFunction(numerator: Polynomial<C>, denominator: Polynomial<C>): RationalFunction<C> =
//    if (denominator.isZero()) throw ArithmeticException("/ by zero")
//    else RationalFunction<C>(numerator, denominator)
//context(RationalFunctionSpace<C, A>)
//@Suppress("FunctionName")
//public fun <C, A: Ring<C>> RationalFunction(numeratorCoefficients: List<C>, denominatorCoefficients: List<C>, reverse: Boolean = false): RationalFunction<C> =
//    RationalFunction<C>(
//        Polynomial( with(numeratorCoefficients) { if (reverse) reversed() else this } ),
//        Polynomial( with(denominatorCoefficients) { if (reverse) reversed() else this } ).also { if (it.isZero()) }
//    )
//context(RationalFunctionSpace<C, A>)
//@Suppress("FunctionName")
//public fun <C, A: Ring<C>> RationalFunction(numerator: Polynomial<C>): RationalFunction<C> =
//    RationalFunction(numerator, onePolynomial)
//context(RationalFunctionSpace<C, A>)
//@Suppress("FunctionName")
//public fun <C, A: Ring<C>> RationalFunction(numeratorCoefficients: List<C>, reverse: Boolean = false): RationalFunction<C> =
//    RationalFunction(
//        Polynomial( with(numeratorCoefficients) { if (reverse) reversed() else this } )
//    )

public class ListRationalFunctionSpace<C, A : Ring<C>> (
    public val ring: A,
) :
    RationalFunctionalSpaceOverPolynomialSpace<
            C,
            ListPolynomial<C>,
            ListRationalFunction<C>,
            ListPolynomialSpace<C, A>,
            >,
    PolynomialSpaceOfFractions<
            C,
            ListPolynomial<C>,
            ListRationalFunction<C>,
            >() {

    override val polynomialRing : ListPolynomialSpace<C, A> = ListPolynomialSpace(ring)
    override fun constructRationalFunction(numerator: ListPolynomial<C>, denominator: ListPolynomial<C>): ListRationalFunction<C> =
        ListRationalFunction(numerator, denominator)

    /**
     * Instance of zero rational function (zero of the rational functions ring).
     */
    public override val zero: ListRationalFunction<C> = ListRationalFunction(polynomialZero, polynomialOne)
    /**
     * Instance of unit polynomial (unit of the rational functions ring).
     */
    public override val one: ListRationalFunction<C> = ListRationalFunction(polynomialOne, polynomialOne)

    // TODO: Разобрать

//    operator fun invoke(arg: UnivariatePolynomial<T>): RationalFunction<T> =
//        RationalFunction(
//            numerator(arg),
//            denominator(arg)
//        )
//
//    operator fun invoke(arg: RationalFunction<T>): RationalFunction<T> {
//        val num = numerator invokeRFTakeNumerator arg
//        val den = denominator invokeRFTakeNumerator arg
//        val degreeDif = numeratorDegree - denominatorDegree
//        return if (degreeDif > 0)
//            RationalFunction(
//                num,
//                multiplyByPower(den, arg.denominator, degreeDif)
//            )
//        else
//            RationalFunction(
//                multiplyByPower(num, arg.denominator, -degreeDif),
//                den
//            )
//    }
//
//    override fun toString(): String = toString(UnivariatePolynomial.variableName)
//
//    fun toString(withVariableName: String = UnivariatePolynomial.variableName): String =
//        when(true) {
//            numerator.isZero() -> "0"
//            denominator.isOne() -> numerator.toString(withVariableName)
//            else -> "${numerator.toStringWithBrackets(withVariableName)}/${denominator.toStringWithBrackets(withVariableName)}"
//        }
//
//    fun toStringWithBrackets(withVariableName: String = UnivariatePolynomial.variableName): String =
//        when(true) {
//            numerator.isZero() -> "0"
//            denominator.isOne() -> numerator.toStringWithBrackets(withVariableName)
//            else -> "(${numerator.toStringWithBrackets(withVariableName)}/${denominator.toStringWithBrackets(withVariableName)})"
//        }
//
//    fun toReversedString(withVariableName: String = UnivariatePolynomial.variableName): String =
//        when(true) {
//            numerator.isZero() -> "0"
//            denominator.isOne() -> numerator.toReversedString(withVariableName)
//            else -> "${numerator.toReversedStringWithBrackets(withVariableName)}/${denominator.toReversedStringWithBrackets(withVariableName)}"
//        }
//
//    fun toReversedStringWithBrackets(withVariableName: String = UnivariatePolynomial.variableName): String =
//        when(true) {
//            numerator.isZero() -> "0"
//            denominator.isOne() -> numerator.toReversedStringWithBrackets(withVariableName)
//            else -> "(${numerator.toReversedStringWithBrackets(withVariableName)}/${denominator.toReversedStringWithBrackets(withVariableName)})"
//        }
//
//    fun removeZeros() =
//        RationalFunction(
//            numerator.removeZeros(),
//            denominator.removeZeros()
//        )
}