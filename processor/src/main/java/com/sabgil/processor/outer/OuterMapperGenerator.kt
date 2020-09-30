package com.sabgil.processor.outer

import com.sabgil.processor.common.*
import com.sabgil.processor.common.Outer.METHOD_NAME
import com.sabgil.processor.common.Outer.NAME
import com.sabgil.processor.common.Outer.PARAM_NAME__FIELD_NAME
import com.sabgil.processor.common.Outer.PARAM_NAME__INTENT
import com.sabgil.processor.common.Outer.PARAM_NAME__INTENT_OWNER_CLASS
import com.sabgil.processor.common.model.FieldData
import com.squareup.kotlinpoet.*

class OuterMapperGenerator(
    private val fieldMap: Map<ClassName, List<FieldData>>
) {
    private val notFoundActivityClassFormat =
        "else -> throw com.sabgil.exception.NotFoundActivityClassException()"

    fun generate(): FileSpec =
        FileSpec.builder(ROOT_PACKAGE, NAME)
            .addType(classBuild())
            .build()

    private fun classBuild(): TypeSpec =
        TypeSpec.classBuilder(NAME)
            .addSuperinterface(typeExtraMapper)
            .addFunction(funBuild())
            .build()

    private fun funBuild(): FunSpec =
        FunSpec.builder(METHOD_NAME)
            .addModifiers(KModifier.OVERRIDE)
            .addParameter(PARAM_NAME__FIELD_NAME, typeString)
            .addParameter(PARAM_NAME__INTENT, typeIntent)
            .addParameter(PARAM_NAME__INTENT_OWNER_CLASS, typeClassParameterizedByActivity)
            .returns(typeNullableAny)
            .beginControlFlow("return when(%L)", PARAM_NAME__INTENT_OWNER_CLASS)
            .addWhenCasesStatement()
            .addStatement(notFoundActivityClassFormat)
            .endControlFlow()
            .build()

    private fun FunSpec.Builder.addWhenCasesStatement(): FunSpec.Builder {
        fieldMap.keys.forEach { className ->
            addStatement(
                "%L -> %L.%L(%L, %L)",
                className.canonicalName.addJavaClassKeyword(),
                className.canonicalName.addInnerMapperClassSuffix(),
                Inner.METHOD_NAME,
                PARAM_NAME__FIELD_NAME,
                PARAM_NAME__INTENT
            )
        }

        return this
    }

    private fun String.addJavaClassKeyword() = "$this::class.java"
    private fun String.addInnerMapperClassSuffix() = "${this}_${Inner.NAME_SUFFIX}"
}

private const val ROOT_PACKAGE = ""