package com.sabgil.extra

import android.app.Activity

fun <T : Activity, B> T.extra() =
    ExtraValueHolder { property ->
        val propertyMapper = MapperManager[PropertyMapper.Key.of(this::class.java)]

        @Suppress("UNCHECKED_CAST")
        propertyMapper.map(property, intent) as B
    }