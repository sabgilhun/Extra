package com.sabgil.extraof

import kotlin.reflect.KProperty

class ExtraValueHolder<V>(
    private var initializer: (String) -> V
) {
    @Volatile
    private var _value: Any? = UninitializedValue
    private val lock = this

    operator fun getValue(thisRef: Any?, property: KProperty<*>): V {
        val v1 = _value
        if (v1 !== UninitializedValue) {
            @Suppress("UNCHECKED_CAST")
            return v1 as V
        }

        return synchronized(lock) {
            val v2 = _value
            if (v2 !== UninitializedValue) {
                @Suppress("UNCHECKED_CAST") (v2 as V)
            } else {
                @Suppress("UNCHECKED_CAST")
                val typedValue = initializer(property.name)
                _value = typedValue
                typedValue
            }
        }
    }
}

private object UninitializedValue