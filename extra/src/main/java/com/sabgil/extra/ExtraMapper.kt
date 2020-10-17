package com.sabgil.extra

import android.content.Intent

interface ExtraMapper {

    fun map(fieldName: String, intent: Intent, intentOwnerClass: Class<*>): Any?

    companion object {
        private var extraMapper: ExtraMapper? = null

        @Synchronized
        fun map(fieldName: String, intent: Intent, intentOwnerClass: Class<*>): Any? {
            val _extraMapper =
                extraMapper ?: Class.forName("ExtraMapperImpl").newInstance() as ExtraMapper
            extraMapper = _extraMapper
            return _extraMapper.map(fieldName, intent, intentOwnerClass)
        }
    }
}