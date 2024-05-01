package com.noxcrew.smp.token

/**
 * A value.
 */
internal sealed interface Value : Token {
    /**
     * A constant value.
     */
    data class Constant(
        /**
         * The number.
         */
        internal val number: Double,
    ) : Value {
        internal companion object {
            /**
             * Checks if this symbol is valid for a constant.
             *
             * @param symbol the symbol
             * @return if this symbol is valid
             */
            internal fun isValidSymbol(symbol: Char): Boolean = symbol.isDigit() || symbol == '.' || symbol == ','
        }

        override fun toString(): String {
            return number.toString()
        }
    }

    /**
     * A variable that needs to be resolved.
     */
    data class Variable(
        /**
         * The name of the variable.
         */
        internal val name: String,
    ) : Value {
        internal companion object {
            /**
             * Checks if this symbol is valid for a variable.
             *
             * @param symbol the symbol
             * @return if this symbol is valid
             */
            internal fun isValidSymbol(symbol: Char): Boolean = symbol.isLetter() || symbol == '_'
        }

        override fun toString(): String {
            return name
        }
    }
}
