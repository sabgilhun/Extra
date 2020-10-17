package com.sabgil.processor.common

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.WildcardTypeName

val typeIntent: TypeName = ClassName("android.content", "Intent")

val typeNullableAny: TypeName = ClassName("kotlin", "Any").copy(true)

val typePropertyMapper: TypeName = ClassName("com.sabgil.extra", "PropertyMapper")

val typeKProperty: TypeName = ClassName("kotlin.reflect", "KProperty")
    .parameterizedBy(WildcardTypeName.producerOf(typeNullableAny))