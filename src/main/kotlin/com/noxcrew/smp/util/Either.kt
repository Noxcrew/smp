package com.noxcrew.smp.util

/**
 * An object with one of two types.
 */
internal sealed class Either<out A, out B> {
    internal companion object {
        /**
         * Unwraps into a common supertype.
         */
        internal fun <A, B, C> Either<A, B>.unwrap(): C where A : C, B : C = fold(ifLeft = { it }, ifRight = { it })
    }

    /**
     * The left-hand side of an Either type.
     */
    internal data class Left<out A>(internal val value: A) : Either<A, Nothing>() {
        override fun toString(): String = "Either.Left($value)"
    }

    /**
     * The right-hand side of an Either type.
     */
    internal data class Right<out B>(internal val value: B) : Either<Nothing, B>() {
        override fun toString(): String = "Either.Right($value)"
    }

    /**
     * Applies `ifLeft` if this is a [Left] or `ifRight` if this is a [Right].
     */
    internal inline fun <C> fold(
        ifLeft: (A) -> C,
        ifRight: (B) -> C,
    ): C =
        when (this) {
            is Left -> ifLeft(value)
            is Right -> ifRight(value)
        }
}
