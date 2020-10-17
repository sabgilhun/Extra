package com.sabgil.extra

import android.content.Intent
import kotlin.reflect.KProperty

interface PropertyMapper {

    fun map(property: KProperty<*>, intent: Intent): Any?


    data class Key(val mapper: String) {
        companion object {
            fun of(clazz: Class<*>): Key {
                return Key(clazz.name + "_Mapper")
            }

            @Suppress("unused")
            fun withMapper(clazz: Class<out PropertyMapper>): Key {
                return Key(clazz.name)
            }
        }
    }
}