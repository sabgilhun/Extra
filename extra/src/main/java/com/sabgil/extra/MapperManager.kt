package com.sabgil.extra

object MapperManager {

    private val mappers: Map<PropertyMapper.Key, PropertyMapper> = mutableMapOf()

    operator fun get(key: PropertyMapper.Key): PropertyMapper {
        val mapper = mappers[key]

        if (mapper == null) {
            try {
                Class.forName(key.implementedMapperName)
            } catch (e: ClassNotFoundException) {
                // TODO: print error message
            }
        }

        return mapper ?: requireNotNull(mappers[key]) {
            "${key.clazz.simpleName} not found. check if there is @Extra annotation in ${key.clazz.simpleName}"
        }
    }
}