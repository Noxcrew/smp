package com.noxcrew.smp.exception

/**
 * A parse exception.
 *
 * @since 1.0.0
 */
public data class ParseException internal constructor(
    /**
     * The input string.
     *
     * @since 1.0.0
     */
    public val input: String,
    /**
     * The reason the parse failed.
     *
     * @since 1.0.0
     */
    public val reason: String,
    /**
     * The index of the error's cause, if known.
     *
     * @since 1.0.0
     */
    public val index: Int? = null,
    override val cause: Throwable? = null,
) : IllegalArgumentException() {
    override val message: String =
        if (index != null) {
            """
            An error occurred while parsing!
            
                $input
                ${ " ".repeat(index) }^
                
            The reason for the failure was: $reason.
            """.trimIndent()
        } else {
            "An error occurred while parsing: $reason!"
        }

    override fun toString(): String {
        return message
    }
}
