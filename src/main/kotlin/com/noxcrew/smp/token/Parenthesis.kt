package com.noxcrew.smp.token

/**
 * Parentheses.
 */
internal enum class Parenthesis(
    override val symbol: Char,
) : SymbolIdentified {
    /**
     * A left parenthesis.
     */
    LEFT('('),

    /**
     * A left parenthesis.
     */
    RIGHT(')'),
    ;

    override fun toString(): String {
        return symbol.toString()
    }
}
