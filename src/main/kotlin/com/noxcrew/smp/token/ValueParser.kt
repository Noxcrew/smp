package com.noxcrew.smp.token

import com.noxcrew.smp.exception.ParseException

/**
 * A parser for values.
 */
internal class ValueParser private constructor(
    private val name: String,
    private val inputString: String,
    private val startingIndex: Int,
    private val symbolFilter: (Char) -> Boolean,
    private val finisher: (String) -> Value,
) {
    internal companion object {
        /**
         * Returns a new value parser based on the starting symbol.
         *
         * @param inputString the input string, used for parse exceptions
         * @param currentIndex the starting index, used for offsetting exceptions
         * @param currentSymbol the starting symbol
         * @return the value parser to use, if any
         */
        internal fun create(
            inputString: String,
            currentIndex: Int,
            currentSymbol: Char,
        ): ValueParser? {
            return when {
                Value.Variable.isValidSymbol(currentSymbol) ->
                    ValueParser(
                        name = "variable",
                        inputString = inputString,
                        startingIndex = currentIndex,
                        symbolFilter = Value.Variable::isValidSymbol,
                        finisher = Value::Variable,
                    )
                Value.Constant.isValidSymbol(currentSymbol) ->
                    ValueParser(
                        name = "constant",
                        inputString = inputString,
                        startingIndex = currentIndex,
                        symbolFilter = Value.Constant::isValidSymbol,
                        finisher = { string -> Value.Constant(string.toDouble()) },
                    )
                else -> null
            }
        }
    }

    private val builder: StringBuilder = StringBuilder()

    /**
     * Accepts the given symbol.
     *
     * @param symbol the symbol
     */
    internal fun accept(symbol: Char) {
        if (symbolFilter(symbol)) {
            builder.append(symbol)
        } else {
            throw ParseException(
                input = inputString,
                reason = "invalid $name symbol '$symbol'",
                index = startingIndex + builder.length,
            )
        }
    }

    /**
     * Finishes this parse attempt, returning the value.
     *
     * @return the value
     */
    internal fun finish(): Value {
        try {
            return finisher(builder.toString())
        } catch (exception: Exception) {
            throw ParseException(
                input = inputString,
                reason = "couldn't finish $name token",
                index = startingIndex + builder.length,
                cause = exception,
            )
        }
    }
}
