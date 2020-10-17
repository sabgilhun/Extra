package com.sabgil.extra

import android.content.Intent
import kotlin.reflect.KProperty

interface PropertyMapper {

    fun map(property: KProperty<*>, intent: Intent, propertyOwnerClass: Class<*>): Any?


    data class Key(val clazz: Class<*>) {
        val implementedMapperName : String = clazz.simpleName + "_Mapper"
    }
}