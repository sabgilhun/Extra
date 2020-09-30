package com.sabgil.processor

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName

val typeString: TypeName = ClassName("kotlin", "String")

val typeIntent: TypeName = ClassName("android.content", "Intent")

val typeNullableAny: TypeName = ClassName("kotlin", "Any").copy(true)

val typeActivity: TypeName = ClassName("android.app", "Activity")

val typeClassParameterizedByActivity: TypeName =
    ClassName("java.lang", "Class").parameterizedBy(typeActivity)
