package com.noxcrew.smp

import com.noxcrew.smp.SMP.Companion.create
import com.noxcrew.smp.provider.NoOpVariableValueProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

/**
 * The main entrypoint to the SMP library.
 *
 * This can be accessed using:
 * * The companion object, which is a shared SMP instance created with the default
 * settings.
 * * The `create` method, which can be used to create a custom instance.
 *
 * There are three steps to this library:
 * 1. **Parse**: turns the input string into an intermediate representation (IR).
 * 2. **Resolve** (optional): turns variables in the expression into doubles using the
 * [VariableValueProvider] associated with this SMP instance.
 * 3. **Compute**: calculates and returns the result of the expression.
 *
 * The resolve step can be skipped entirely by using [SMP.computeUnresolved] or
 * [Expression.computeUnresolved].
 *
 * @see create
 * @see SMP.Companion
 * @since 1.0
 */
public interface SMP {
    /**
     * A shared SMP instance created with the default settings.
     *
     * @since 1.0
     */
    public companion object : SMP {
        private val DEFAULT_INSTANCE by lazy { create() }

        /**
         * Creates a new SMP instance.
         *
         * @param variableValueProvider The variable value provider to use. This
         * defaults to a provider that throws exceptions when any variable is entered.
         * This allows for a "default" state to not allow for any variables to be set.
         * @param scopeFactory A factory that creates coroutine scopes for use when
         * resolving expressions.
         * @since 1.0
         */
        public fun create(
            variableValueProvider: VariableValueProvider = NoOpVariableValueProvider,
            scopeFactory: () -> CoroutineScope = { CoroutineScope(Job() + Dispatchers.Default) },
        ): SMP {
            return Parser(variableValueProvider, scopeFactory)
        }

        override fun parse(input: String): Expression {
            return DEFAULT_INSTANCE.parse(input)
        }
    }

    /**
     * Parses the input into an expression.
     *
     * @param input the input string
     * @return the expression
     * @since 1.0
     */
    public fun parse(input: String): Expression

    /**
     * Shorthand method to parse the input and perform an unresolved compute on it.
     *
     * @param input the input string
     * @return the result of the expression
     * @see Expression.computeUnresolved
     * @since 1.0
     */
    public fun computeUnresolved(input: String): Double = parse(input).computeUnresolved()

    /**
     * Shorthand method to parse the input and compute it.
     *
     * @param input the input string
     * @return the result of the expression
     * @see Expression.compute
     * @since 1.0
     */
    public suspend fun compute(input: String): Double = parse(input).compute()

    /**
     * Shorthand method to parse the input and compute it using only cached variables.
     *
     * @param fallback value to use for all non-cached variables
     * @return the result of the expression
     * @see Expression.computeCacheOnly
     * @since 1.1
     */
    public fun computeCacheOnly(
        input: String,
        fallback: Double = 0.0,
    ): Double = parse(input).computeCacheOnly(fallback)
}
