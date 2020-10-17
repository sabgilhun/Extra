package com.sabgil.processor.common

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.WildcardTypeName

val typeString: TypeName = ClassName("kotlin", "String")

val typeIntent: TypeName = ClassName("android.content", "Intent")

val typeNullableAny: TypeName = ClassName("kotlin", "Any").copy(true)

val typeClass: TypeName = ClassName("java.lang", "Class")

val typeClassParameterizedByActivity: TypeName =
    (typeClass as ClassName).parameterizedBy(WildcardTypeName.producerOf(typeNullableAny))

val typeExtraMapper: TypeName = ClassName("com.sabgil.extra", "ExtraMapper")
