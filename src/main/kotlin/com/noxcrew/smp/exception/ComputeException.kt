package com.noxcrew.smp.exception

/**
 * An exception during the computation of an expression.
 *
 * @since 1.0.0
 */
public data class ComputeException internal constructor(
    /**
     * The reason the compute attempt failed.
     *
     * @since 1.0.0
     */
    public val reason: String,
    override val cause: Throwable? = null,
) : RuntimeException(cause) {
    override val message: String = "An error occurred whilst computing an expression: $reason!"

    override fun toString(): String {
        return message
    }
}
