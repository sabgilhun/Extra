package com.sabgil.processor

import com.sabgil.processor.ExtraMethodType.Companion.mapTo
import com.squareup.kotlinpoet.*

class InnerMapperGenerator(
    private val className: ClassName,
    private val fieldDataList: List<FieldData>
) {
    fun generate(): FileSpec =
        FileSpec.builder(className.packageName, className.simpleName.addInnerMapperClassSuffix())
            .addType(objectBuild())
            .build()

    private fun objectBuild(): TypeSpec =
        TypeSpec.objectBuilder(className.simpleName.addInnerMapperClassSuffix())
            .addFunction(funBuild())
            .build()

    private fun funBuild(): FunSpec =
        FunSpec.builder(INNER_MAPPER_METHOD_NAME)
            .addParameter(INNER_MAPPER_METHOD_PARAM_NAME__FIELD_NAME, String::class)
            .addParameter(
                INNER_MAPPER_METHOD_PARAM_NAME__INTENT,
                ClassName("android.content", "Intent")
            )
            .returns(Any::class.asTypeName().copy(true))
            .beginControlFlow("return when(%L)", INNER_MAPPER_METHOD_PARAM_NAME__FIELD_NAME)
            .addWhenCasesStatement()
            .addStatement("else -> throw com.sabgil.exception.NotFoundFiledNameException()")
            .endControlFlow()
            .build()

    private fun FunSpec.Builder.addWhenCasesStatement(): FunSpec.Builder {
        println(fieldDataList.size)
        fieldDataList.forEach { fieldData ->
            addStatement(mappingType(fieldData), fieldData.fieldName, fieldData.fieldName)
        }

        return this
    }

    private fun mappingType(fieldData: FieldData) = fieldData.mapTo().methodFormat

    private fun String.addInnerMapperClassSuffix() = "${this}_$INNER_MAPPER_NAME_SUFFIX"
}