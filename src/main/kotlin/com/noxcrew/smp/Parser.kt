package com.noxcrew.smp

import com.noxcrew.smp.exception.ParseException
import com.noxcrew.smp.token.Operator
import com.noxcrew.smp.token.Parenthesis
import com.noxcrew.smp.token.SymbolIdentified
import com.noxcrew.smp.token.Token
import com.noxcrew.smp.token.Value
import com.noxcrew.smp.token.ValueParser
import com.noxcrew.smp.util.Either
import com.noxcrew.smp.util.Either.Companion.unwrap
import kotlinx.coroutines.CoroutineScope

/**
 * Calculator to handle logic for parsing expressions.
 */
internal data class Parser(
    /**
     * The variable value provider.
     */
    internal val variableValueProvider: VariableValueProvider,
    /**
     * The scope factory.
     */
    internal val scopeFactory: () -> CoroutineScope,
) : SMP {
    private companion object {
        private val SYMBOLS = (Operator.entries + Parenthesis.entries).associateBy(SymbolIdentified::symbol)
    }

    override fun parse(input: String): Expression {
        // Step 1: tokenize.
        val tokens = tokenize(input)

        // Step 2: shunt.
        val shunted = shunt(input, tokens)

        // Step 3: create the expression.
        return Expression(this, shunted)
    }

    /**
     * Tokenizes some input.
     *
     * @param input the input string
     * @return a list of tokens in the input
     */
    private fun tokenize(input: String): List<Token> =
        buildList {
            var valueParser: ValueParser? = null

            for ((index, symbol) in input.withIndex()) {
                // Ignore whitespace.
                if (symbol.isWhitespace()) continue

                // Try and find a symbol identified token.
                val symbolIdentified = SYMBOLS[symbol]
                if (symbolIdentified != null) {
                    valueParser?.finish()?.let(::add)
                    valueParser = null
                    add(symbolIdentified)
                    continue
                }

                // At this point, we are parsing a value. We may need to init the parser.
                if (valueParser == null) {
                    valueParser = ValueParser.create(input, index, symbol)

                    // If it's still null we encountered an unknown symbol.
                    if (valueParser == null) {
                        throw ParseException(input, "unknown symbol '$symbol'", index)
                    }
                }

                // Now we can accept the symbol.
                valueParser.accept(symbol)
            }

            // If there are still elements in the value parser, finish it.
            valueParser?.finish()?.let(::add)
        }

    /**
     * Performs the shunting yard algorithm on a list of tokens.
     *
     * @param input the input string
     * @param tokens the tokens
     * @return the tokens in RPN format
     */
    private fun shunt(
        input: String,
        tokens: List<Token>,
    ): List<Token> {
        // Set up the stacks.
        val output = mutableListOf<Token>()
        val operators = mutableListOf<Either<Operator, Parenthesis>>()

        /**
         * Iterates through the operator list in reverse order.
         *
         * @param handler a handler for the iteration, returning if iteration should continue
         */
        fun iterateOperatorsReversed(
            handler: (MutableListIterator<Either<Operator, Parenthesis>>, Either<Operator, Parenthesis>) -> Boolean,
        ) {
            if (operators.isNotEmpty()) {
                val iterator = operators.listIterator(operators.size)

                while (iterator.hasPrevious()) {
                    if (!handler(iterator, iterator.previous())) {
                        break
                    }
                }
            }
        }

        for (token in tokens) {
            @Suppress("REDUNDANT_ELSE_IN_WHEN") // Although we are exhaustive, the compiler doesn't agree.
            when (token) {
                // Values just get added directly to the output.
                is Value -> output.add(token)

                is Parenthesis -> {
                    when (token) {
                        // Left parentheses get put straight on the operator stack.
                        Parenthesis.LEFT -> operators.add(Either.Right(token))

                        Parenthesis.RIGHT -> {
                            var foundMatch = false

                            iterateOperatorsReversed { iterator, either ->
                                either.fold(
                                    ifLeft = { operator ->
                                        // Pop it into the output.
                                        output.add(operator)
                                        iterator.remove()
                                        true
                                    },
                                    ifRight = { parenthesis ->
                                        if (parenthesis == Parenthesis.LEFT) {
                                            // We discard this parenthesis and move on.
                                            iterator.remove()
                                            foundMatch = true
                                            false
                                        } else {
                                            true
                                        }
                                    },
                                )
                            }

                            // If we didn't find a match, throw an exception.
                            if (!foundMatch) {
                                throw ParseException(input, "mismatched parentheses")
                            }
                        }
                    }
                }

                is Operator -> {
                    // We need to pop from the operator stack anything with a greater or equal precedence.
                    iterateOperatorsReversed { iterator, either ->
                        either.fold(
                            ifLeft = { operator ->
                                if (operator.precedence <= token.precedence) {
                                    // Pop from operators into the output.
                                    output.add(operator)
                                    iterator.remove()
                                    true
                                } else {
                                    // We've reached a lower precedence operator and can break.
                                    false
                                }
                            },
                            // This will only be a left parenthesis and can be ignored.
                            ifRight = { false },
                        )
                    }

                    // Then we just push it to the operator stack.
                    operators.add(Either.Left(token))
                }

                else -> error("Unknown token type")
            }
        }

        // If the operator at the top of the stack is a left parenthesis, we have mismatched!
        if (operators.lastOrNull()?.unwrap() == Parenthesis.LEFT) {
            throw ParseException(input, "mismatched parentheses")
        }

        // We've reached the end of the main iteration, so we can just add everything to the output.
        iterateOperatorsReversed { _, either ->
            output.add(either.unwrap())
            true
        }

        // Now we can just produce the expression!
        return output.toList()
    }
}
