package com.noxcrew.smp

/**
 * Provider of values for variables.
 *
 * Exceptions thrown in this interface will be passed up to the parse call.
 * This interface is also called in parallel, so care should be made to ensure it is
 * thread-safe.
 *
 * @since 1.0
 */
public fun interface VariableValueProvider {
    public companion object {
        /**
         * A value provider that throws [UnsupportedOperationException] for all calls.
         *
         * @since 1.0
         */
        @Deprecated(
            message = "Moved to an object",
            replaceWith =
                ReplaceWith(
                    expression = "NoOpVariableValueProvider",
                    imports = ["com.noxcrew.smp.provider.NoOpVariableValueProvider"],
                ),
        )
        public val NONE: VariableValueProvider =
            VariableValueProvider {
                throw UnsupportedOperationException("Variables are not supported in this parser!")
            }
    }

    /**
     * Returns the value of the variable with the provided name.
     *
     * @param name the name of the variable
     * @return the value of the variable
     * @since 1.0
     */
    public suspend fun getValue(name: String): Double
}
