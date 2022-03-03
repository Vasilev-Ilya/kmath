/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.functions

import space.kscience.kmath.functions.AbstractPolynomialSpace.Companion.optimizedAddMultiplied
import space.kscience.kmath.functions.AbstractPolynomialSpace.Companion.optimizedMultiply
import space.kscience.kmath.operations.*
import kotlin.jvm.JvmName
import kotlin.math.max
import kotlin.math.min

/**
 * Polynomial coefficients model without fixation on specific context they are applied to.
 *
 * @param coefficients constant is the leftmost coefficient.
 */
public class Polynomial<T>(public val coefficients: List<T>) : AbstractPolynomial<T> {
    override fun toString(): String = "Polynomial$coefficients"

//    public companion object {
//        /**
//         * Default name of variables used in string representations.
//         *
//         * @see Polynomial.toString
//         */
//        public var defaultVariableName: String = "x"
//
//        /**
//         * Represents result of division with remainder.
//         */
//        public data class DividingResult<C>(
//            val quotient: Polynomial<C>,
//            val reminder: Polynomial<C>
//        )
//    }
}

/**
 * Represents internal [Polynomial] errors.
 */
internal class PolynomialError(message: String): Error(message)

// region Constructors and converters

/**
 * Returns a [Polynomial] instance with given [coefficients]. The collection of coefficients will be reversed if
 * [reverse] parameter is true.
 */
@Suppress("FunctionName")
public fun <T> Polynomial(coefficients: List<T>, reverse: Boolean = false): Polynomial<T> =
    Polynomial(with(coefficients) { if (reverse) reversed() else this })

/**
 * Returns a [Polynomial] instance with given [coefficients]. The collection of coefficients will be reversed if
 * [reverse] parameter is true.
 */
@Suppress("FunctionName")
public fun <T> Polynomial(vararg coefficients: T, reverse: Boolean = false): Polynomial<T> =
    Polynomial(with(coefficients) { if (reverse) reversed() else toList() })

public fun <T> T.asPolynomial() : Polynomial<T> = Polynomial(listOf(this))

// endregion

/**
 * Space of polynomials constructed over ring.
 *
 * @param C the type of constants. Polynomials have them as a coefficients in their terms.
 * @param A type of underlying ring of constants. It's [Ring] of [C].
 * @param ring underlying ring of constants of type [A].
 */
@Suppress("INAPPLICABLE_JVM_NAME") // KT-31420
public open class PolynomialSpace<C, A : Ring<C>>(
    public val ring: A,
) : AbstractPolynomialSpace<C, Polynomial<C>>{
    // region Constant-integer relation
    @JvmName("constantIntPlus")
    public override operator fun C.plus(other: Int): C = ring { optimizedAddMultiplied(this@plus, one, other) }
    @JvmName("constantIntMinus")
    public override operator fun C.minus(other: Int): C = ring { optimizedAddMultiplied(this@minus, one, -other) }
    @JvmName("constantIntTimes")
    public override operator fun C.times(other: Int): C = ring { optimizedMultiply(this@times, other) }
    // endregion

    // region Integer-constant relation
    @JvmName("intConstantPlus")
    public override operator fun Int.plus(other: C): C = ring { optimizedAddMultiplied(other, one, this@plus) }
    @JvmName("intConstantMinus")
    public override operator fun Int.minus(other: C): C = ring { optimizedAddMultiplied(-other, one, this@minus) }
    @JvmName("intConstantTimes")
    public override operator fun Int.times(other: C): C = ring { optimizedMultiply(other, this@times) }
    // endregion

    // region Polynomial-integer relation
    public override operator fun Polynomial<C>.plus(other: Int): Polynomial<C> =
        if (other == 0) this
        else
            Polynomial(
                coefficients
                    .toMutableList()
                    .apply {
                        if (isEmpty()) this[0] = ring.zero + other
                        else this[0] = this[0]!! + other
                    }
            )
    public override operator fun Polynomial<C>.minus(other: Int): Polynomial<C> =
        if (other == 0) this
        else
            Polynomial(
                coefficients
                    .toMutableList()
                    .apply {
                        if (isEmpty()) this[0] = ring.zero - other
                        else this[0] = this[0]!! - other
                    }
            )
    public override operator fun Polynomial<C>.times(other: Int): Polynomial<C> =
        if (other == 0) zero
        else Polynomial(
            coefficients
                .subList(0, degree + 1)
                .map { it * other }
        )
    // endregion

    // region Integer-polynomial relation
    public override operator fun Int.plus(other: Polynomial<C>): Polynomial<C> =
        if (this == 0) other
        else
            Polynomial(
                other.coefficients
                    .toMutableList()
                    .apply {
                        if (isEmpty()) this[0] = ring.zero + this@plus
                        else this[0] = this[0]!! + this@plus
                    }
            )
    public override operator fun Int.minus(other: Polynomial<C>): Polynomial<C> =
        if (this == 0) other
        else
            Polynomial(
                other.coefficients
                    .toMutableList()
                    .apply {
                        if (isEmpty()) this[0] = ring.zero - this@minus
                        else this[0] = this[0]!! - this@minus
                    }
            )
    public override operator fun Int.times(other: Polynomial<C>): Polynomial<C> =
        if (this == 0) zero
        else Polynomial(
            other.coefficients
                .subList(0, other.degree + 1)
                .map { it * this }
        )
    // endregion

    // region Constant-constant relation
    @JvmName("constantUnaryMinus")
    public override operator fun C.unaryMinus(): C = ring { -this@unaryMinus }
    @JvmName("constantPlus")
    public override operator fun C.plus(other: C): C = ring { this@plus + other }
    @JvmName("constantMinus")
    public override operator fun C.minus(other: C): C = ring { this@minus - other }
    @JvmName("constantTimes")
    public override operator fun C.times(other: C): C = ring { this@times * other }
    @JvmName("constantIsZero")
    public override fun C.isZero(): Boolean = ring { this == zero }
    @JvmName("constantIsNotZero")
    public override fun C.isNotZero(): Boolean = ring { this != zero }
    @JvmName("constantIsOne")
    public override fun C.isOne(): Boolean = ring { this == one }
    @JvmName("constantIsNotOne")
    public override fun C.isNotOne(): Boolean = ring { this != one }
    @JvmName("constantIsMinusOne")
    public override fun C.isMinusOne(): Boolean = ring { this == -one }
    @JvmName("constantIsNotMinusOne")
    public override fun C.isNotMinusOne(): Boolean = ring { this != -one }
    // endregion

    // region Constant-polynomial relation
    public override operator fun C.plus(other: Polynomial<C>): Polynomial<C> =
        with(other.coefficients) {
            if (isEmpty()) Polynomial(listOf(this@plus))
            else Polynomial(
                toMutableList()
                    .apply {
                        this[0] += this@plus
                    }
            )
        }
//        if (degree == -1) UnivariatePolynomial(other) else UnivariatePolynomial(
//            listOf(coefficients[0] + other) + coefficients.subList(1, degree + 1)
//        )
    public override operator fun C.minus(other: Polynomial<C>): Polynomial<C> =
        with(other.coefficients) {
            if (isEmpty()) Polynomial(listOf(-this@minus))
            else Polynomial(
                toMutableList()
                    .apply {
                        this[0] -= this@minus
                    }
            )
        }
//        if (degree == -1) UnivariatePolynomial(other) else UnivariatePolynomial(
//            listOf(coefficients[0] + other) + coefficients.subList(1, degree + 1)
//        )
    public override operator fun C.times(other: Polynomial<C>): Polynomial<C> =
        Polynomial(
            other.coefficients
//                .subList(0, other.degree + 1)
                .map { it * this }
        )
    // endregion

    // region Polynomial-constant relation
    public override operator fun Polynomial<C>.plus(other: C): Polynomial<C> =
        if (other.isZero()) this
        else with(coefficients) {
            if (isEmpty()) Polynomial(listOf(other))
            else Polynomial(
                toMutableList()
                    .apply {
                        this[0] += other
                    }
            )
        }
//        if (degree == -1) UnivariatePolynomial(other) else UnivariatePolynomial(
//            listOf(coefficients[0] + other) + coefficients.subList(1, degree + 1)
//        )
    public override operator fun Polynomial<C>.minus(other: C): Polynomial<C> =
        with(coefficients) {
            if (isEmpty()) Polynomial(listOf(other))
            else Polynomial(
                toMutableList()
                    .apply {
                        this[0] -= other
                    }
            )
        }
//        if (degree == -1) UnivariatePolynomial(-other) else UnivariatePolynomial(
//            listOf(coefficients[0] - other) + coefficients.subList(1, degree + 1)
//        )
    public override operator fun Polynomial<C>.times(other: C): Polynomial<C> =
        Polynomial(
            coefficients
//                .subList(0, degree + 1)
                .map { it * other }
        )
    // endregion

    // region Polynomial-polynomial relation
    public override operator fun Polynomial<C>.unaryMinus(): Polynomial<C> =
        Polynomial(coefficients.map { -it })
    public override operator fun Polynomial<C>.plus(other: Polynomial<C>): Polynomial<C> =
        Polynomial(
            (0..max(degree, other.degree))
                .map {
                    when {
                        it > degree -> other.coefficients[it]
                        it > other.degree -> coefficients[it]
                        else -> coefficients[it] + other.coefficients[it]
                    }
                }
                .ifEmpty { listOf(ring.zero) }
        )
    public override operator fun Polynomial<C>.minus(other: Polynomial<C>): Polynomial<C> =
        Polynomial(
            (0..max(degree, other.degree))
                .map {
                    when {
                        it > degree -> -other.coefficients[it]
                        it > other.degree -> coefficients[it]
                        else -> coefficients[it] - other.coefficients[it]
                    }
                }
                .ifEmpty { listOf(ring.zero) }
        )
    public override operator fun Polynomial<C>.times(other: Polynomial<C>): Polynomial<C> {
        val thisDegree = degree
        val otherDegree = other.degree
        return when {
            thisDegree == -1 -> this
            otherDegree == -1 -> other
            else ->
                Polynomial(
                    (0..(thisDegree + otherDegree))
                        .map { d ->
                            (max(0, d - otherDegree)..(min(thisDegree, d)))
                                .map { coefficients[it] * other.coefficients[d - it] }
                                .reduce { acc, rational -> acc + rational }
                        }
                )
        }
    }

    public override fun Polynomial<C>.isZero(): Boolean = coefficients.all { it.isZero() }
    public override fun Polynomial<C>.isNotZero(): Boolean = coefficients.any { it.isNotZero() }
    public override fun Polynomial<C>.isOne(): Boolean =
        with(coefficients) { isNotEmpty() && asSequence().withIndex().any { (index, c) -> if (index == 0) c.isOne() else c.isZero() } } // TODO: It's better to write new methods like `anyIndexed`. But what's better way to do it?
    public override fun Polynomial<C>.isNotOne(): Boolean = !isOne()
    public override fun Polynomial<C>.isMinusOne(): Boolean =
        with(coefficients) { isNotEmpty() && asSequence().withIndex().any { (index, c) -> if (index == 0) c.isMinusOne() else c.isZero() } } // TODO: It's better to write new methods like `anyIndexed`. But what's better way to do it?
    public override fun Polynomial<C>.isNotMinusOne(): Boolean = !isMinusOne()

    override val zero: Polynomial<C> = Polynomial(emptyList())
    override val one: Polynomial<C> = Polynomial(listOf(ring.one))

    @Suppress("EXTENSION_SHADOWED_BY_MEMBER", "CovariantEquals")
    public override fun Polynomial<C>.equals(other: Polynomial<C>): Boolean =
        when {
            this === other -> true
            else -> {
                if (this.degree == other.degree)
                    (0..degree).all { coefficients[it] == other.coefficients[it] }
                else false
            }
        }
    // endregion

    // Not sure is it necessary...
    // region Polynomial properties
    public override val Polynomial<C>.degree: Int get() = coefficients.indexOfLast { it != ring.zero }

    public override fun Polynomial<C>.asConstantOrNull(): C? =
        with(coefficients) {
            when {
                isEmpty() -> ring.zero
                degree > 0 -> null
                else -> first()
            }
        }
    public override fun Polynomial<C>.asConstant(): C = asConstantOrNull() ?: error("Can not represent non-constant polynomial as a constant")

    @Suppress("NOTHING_TO_INLINE")
    public inline fun Polynomial<C>.substitute(argument: C): C = this.substitute(ring, argument)
    @Suppress("NOTHING_TO_INLINE")
    public inline fun Polynomial<C>.substitute(argument: Polynomial<C>): Polynomial<C> = this.substitute(ring, argument)

    @Suppress("NOTHING_TO_INLINE")
    public inline fun Polynomial<C>.asFunction(): (C) -> C = { this.substitute(ring, it) }
    @Suppress("NOTHING_TO_INLINE")
    public inline fun Polynomial<C>.asFunctionOnConstants(): (C) -> C = { this.substitute(ring, it) }
    @Suppress("NOTHING_TO_INLINE")
    public inline fun Polynomial<C>.asFunctionOnPolynomials(): (Polynomial<C>) -> Polynomial<C> = { this.substitute(ring, it) }

    /**
     * Evaluates the polynomial for the given value [arg].
     */
    @Suppress("NOTHING_TO_INLINE")
    public inline operator fun Polynomial<C>.invoke(argument: C): C = this.substitute(ring, argument)
    @Suppress("NOTHING_TO_INLINE")
    public inline operator fun Polynomial<C>.invoke(argument: Polynomial<C>): Polynomial<C> = this.substitute(ring, argument)
    // endregion

}

/**
 * Space of polynomials constructed over ring.
 *
 * @param C the type of constants. Polynomials have them as a coefficients in their terms.
 * @param A type of underlying ring of constants. It's [Ring] of [C].
 * @param ring underlying ring of constants of type [A].
 */
public class ScalablePolynomialSpace<C, A>(
    ring: A,
) : PolynomialSpace<C, A>(ring), ScaleOperations<Polynomial<C>> where A : Ring<C>, A : ScaleOperations<C> {

    override fun scale(a: Polynomial<C>, value: Double): Polynomial<C> =
        ring { Polynomial(List(a.coefficients.size) { index -> a.coefficients[index] * value }) }

}
