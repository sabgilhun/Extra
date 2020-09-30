package com.sabgil.processor

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy

class OuterMapperGenerator(
    private val fieldMap: Map<ClassName, List<FieldData>>
) {
    fun generate(): FileSpec =
        FileSpec.builder(ROOT_PACKAGE, OUTER_MAPPER_NAME)
            .addType(objectBuild())
            .build()

    private fun objectBuild(): TypeSpec =
        TypeSpec.objectBuilder(OUTER_MAPPER_NAME)
            .addFunction(funBuild())
            .build()

    private fun funBuild(): FunSpec =
        FunSpec.builder(OUTER_MAPPER_METHOD_NAME)
            .addParameter(OUTER_MAPPER_METHOD_PARAM_NAME__FIELD_NAME, String::class)
            .addParameter(
                OUTER_MAPPER_METHOD_PARAM_NAME__INTENT,
                ClassName("android.content", "Intent")
            )
            .addParameter(
                OUTER_MAPPER_METHOD_PARAM_NAME__INTENT_OWNER_CLASS,
                ClassName("java.lang", "Class")
                    .parameterizedBy(
                        ClassName(
                            "android.app", "Activity"
                        )
                    )
            )
            .returns(Any::class.asTypeName().copy(true))
            .beginControlFlow("return when(%L)", OUTER_MAPPER_METHOD_PARAM_NAME__INTENT_OWNER_CLASS)
            .addWhenCasesStatement()
            .build()

    private fun FunSpec.Builder.addWhenCasesStatement(): FunSpec.Builder {
        fieldMap.keys.forEach {className ->
            addStatement(
                "%L -> %L.%L(%L, %L)",
                className.canonicalName.addJavaClassKeyword(),
                className.canonicalName.addInnerMapperClassSuffix(),
                INNER_MAPPER_METHOD_NAME,
                OUTER_MAPPER_METHOD_PARAM_NAME__FIELD_NAME,
                OUTER_MAPPER_METHOD_PARAM_NAME__INTENT
            )
        }

        return this
    }

    private fun String.addJavaClassKeyword() = "$this::class.java"
    private fun String.addInnerMapperClassSuffix() = "${this}_$INNER_MAPPER_NAME_SUFFIX"
}

private const val ROOT_PACKAGE = ""