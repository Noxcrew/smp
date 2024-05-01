package com.noxcrew.smp

import com.noxcrew.smp.exception.ComputeException
import com.noxcrew.smp.exception.ResolveException
import com.noxcrew.smp.token.Operator
import com.noxcrew.smp.token.Token
import com.noxcrew.smp.token.Value
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll

/**
 * A parsed expression.
 *
 * @since 1.0
 */
public class Expression internal constructor(
    private val smp: Parser,
    /**
     * The tokens in this expression in RPN order.
     */
    internal val rpnSortedTokens: List<Token>,
) {
    /**
     * Resolves all variables in this expression, returning a new expression that can be
     * computed without exceptions.
     *
     * @return a copy of this expression with all variables resolved
     * @since 1.0
     */
    public suspend fun resolve(): Expression {
        val toResolve = rpnSortedTokens.filterIsInstance<Value.Variable>()

        // Check we have variables to resolve first.
        if (toResolve.isEmpty()) {
            return this
        }

        // Otherwise, we need to do some coroutine setup.
        val scope = smp.scopeFactory()

        // Create the deferred loads for all the variables.
        val deferredVariables =
            toResolve.map { variable ->
                scope.async(start = CoroutineStart.LAZY) {
                    try {
                        variable.name to smp.variableValueProvider.getValue(variable.name)
                    } catch (exception: Exception) {
                        throw ResolveException(variable, exception)
                    }
                }
            }

        // Then load them!
        val loadedVariables =
            try {
                deferredVariables.awaitAll()
            } catch (exception: CancellationException) {
                // If we can unwrap the root cause, do that.
                if (exception.cause is ResolveException) {
                    throw exception.cause as ResolveException
                } else {
                    throw ResolveException("An unknown error occurred whilst resolving!", exception)
                }
            }.toMap()

        // Now return a new expression!
        return Expression(
            smp = smp,
            rpnSortedTokens =
                rpnSortedTokens.map { token ->
                    if (token is Value.Variable) {
                        Value.Constant(loadedVariables.getValue(token.name))
                    } else {
                        token
                    }
                },
        )
    }

    /**
     * Computes the result of this expression without resolving variables.
     *
     * This method will throw a [ComputeException] if there are any unresolved variables
     * in this expression.
     *
     * @return the result of the expression
     * @since 1.0
     */
    public fun computeUnresolved(): Double {
        return if (rpnSortedTokens.any { token -> token is Value.Variable }) {
            throw ComputeException("expression contained unresolved variables")
        } else {
            internalCompute()
        }
    }

    /**
     * Computes the result of this expression, resolving variables beforehand.
     *
     * @return the result of the expression
     * @since 1.0
     */
    public suspend fun compute(): Double {
        return resolve().internalCompute()
    }

    /**
     * Performs an internal compute, assuming all variables are resolved.
     *
     * @return the result of computing this expression
     */
    private fun internalCompute(): Double {
        val numberStack = ArrayDeque<Double>()

        for (token in rpnSortedTokens) {
            when (token) {
                is Operator -> {
                    // Remove the last two numbers, then invoke the equation and push again.
                    val second = numberStack.removeLastOrNull()
                    val first = numberStack.removeLastOrNull()

                    if (second == null || first == null) {
                        throw ComputeException("input equation was not well formed")
                    }

                    numberStack.addLast(token.operation(first, second))
                }

                is Value.Constant -> numberStack.addLast(token.number)

                else -> error("Unknown value in internal RPN tokens: $token")
            }
        }

        // The result is the remaining number on the stack.
        return numberStack.singleOrNull() ?: throw ComputeException("input equation was not well formed")
    }
}
