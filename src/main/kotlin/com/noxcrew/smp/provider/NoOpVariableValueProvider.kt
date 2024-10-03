package com.noxcrew.smp.provider

import com.noxcrew.smp.VariableValueProvider

/**
 * A value provider that throws [UnsupportedOperationException] for all calls.
 *
 * @since 1.1
 */
public data object NoOpVariableValueProvider : VariableValueProvider {
    override suspend fun getValue(name: String): Double {
        throw UnsupportedOperationException("Variables are not supported in this parser!")
    }
}
