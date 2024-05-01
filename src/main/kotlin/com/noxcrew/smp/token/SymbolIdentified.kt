package com.noxcrew.smp.token

/**
 * A token that is identified with a symbol.
 */
internal sealed interface SymbolIdentified : Token {
    /**
     * The associated symbol.
     */
    val symbol: Char
}
