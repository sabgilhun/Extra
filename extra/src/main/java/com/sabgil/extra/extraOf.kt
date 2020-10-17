package com.sabgil.extra

import android.app.Activity

fun <T : Activity, B> T.extraOf(): ExtraValueHolder<B> {
    return ExtraValueHolder { fieldName ->
        @Suppress("UNCHECKED_CAST")
        ExtraMapper.map(fieldName, intent, this::class.java) as B
    }
}

fun <T : Activity, B> T.extra(): ExtraValueHolder2<B> {
    return ExtraValueHolder2 { property ->
        val propertyMapper = MapperManager[PropertyMapper.Key(this::class.java)]

        @Suppress("UNCHECKED_CAST")
        propertyMapper.map(property, intent, this::class.java) as B
    }
}