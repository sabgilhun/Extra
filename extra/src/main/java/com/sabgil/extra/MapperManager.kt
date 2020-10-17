package com.sabgil.extra

object MapperManager {

    @Suppress("MemberVisibilityCanBePrivate")
    val mappers: MutableMap<PropertyMapper.Key, PropertyMapper> = mutableMapOf()

    operator fun get(key: PropertyMapper.Key): PropertyMapper {
        val mapper = mappers[key]

        if (mapper == null) {
            try {
                Class.forName(key.mapper)
            } catch (e: ClassNotFoundException) {
                // TODO: print error message
            }
        }

        // TODO: trim error message
        return mapper ?: requireNotNull(mappers[key]) {
            "${key.mapper} not found. check if there is @Extra annotation in ${key.mapper}"
        }
    }
}