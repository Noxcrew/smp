package com.noxcrew.smp.token

import kotlin.math.pow

/**
 * An operator.
 */
internal enum class Operator(
    override val symbol: Char,
    /**
     * The operation associated with this operator.
     */
    internal val operation: Operation,
    /**
     * The precedence of this operator.
     */
    internal val precedence: Int,
) : SymbolIdentified {
    /**
     * Plus.
     */
    PLUS('+', Double::plus, 2),

    /**
     * Minus.
     */
    MINUS('-', Double::minus, 2),

    /**
     * Times.
     */
    TIMES('*', Double::times, 1),

    /**
     * Divide.
     */
    DIVIDE('/', Double::div, 1),

    /**
     * Power.
     */
    POWER('^', Double::pow, 0),
    ;

    override fun toString(): String {
        return symbol.toString()
    }
}
