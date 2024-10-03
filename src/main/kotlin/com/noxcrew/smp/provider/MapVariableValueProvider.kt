package com.noxcrew.smp.provider

import com.noxcrew.smp.VariableValueProvider

/**
 * A variable value provider that is backed by a map.
 *
 * If a value is not present in the map for a given key, an exception will be thrown
 * as this implementation uses [getValue]. If you wish to avoid this, consider creating
 * the backing map using [withDefault].
 *
 * @property backingMap The backing map
 * @since 1.1
 */
public data class MapVariableValueProvider(
    public val backingMap: Map<String, Double>,
) : VariableValueProvider {
    override suspend fun getValue(name: String): Double {
        return backingMap.getValue(name)
    }
}
