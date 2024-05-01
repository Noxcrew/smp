package com.noxcrew.smp.exception

import com.noxcrew.smp.token.Value

/**
 * An exception encountered during the resolve phase.
 *
 * @since 1.0
 */
public data class ResolveException internal constructor(
    /**
     * The reason why the resolve failed.
     *
     * @since 1.0
     */
    public val reason: String,
    override val cause: Throwable?,
) : RuntimeException() {
    internal constructor(variable: Value.Variable, cause: Throwable) : this(
        reason = "An error occurred whilst resolving variable '$variable'!",
        cause = cause,
    )

    override val message: String = reason

    override fun toString(): String {
        return message
    }
}
