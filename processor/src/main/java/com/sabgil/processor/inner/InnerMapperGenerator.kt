package com.sabgil.processor.inner

import com.sabgil.processor.inner.ExtraMethodType.Companion.mapTo
import com.sabgil.processor.common.model.FieldData
import com.sabgil.processor.common.Inner.METHOD_NAME
import com.sabgil.processor.common.Inner.NAME_SUFFIX
import com.sabgil.processor.common.Inner.PARAM_NAME__FIELD_NAME
import com.sabgil.processor.common.Inner.PARAM_NAME__INTENT
import com.sabgil.processor.common.typeIntent
import com.sabgil.processor.common.typeNullableAny
import com.sabgil.processor.common.typeString
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeSpec

class InnerMapperGenerator(
    private val className: ClassName,
    private val fieldDataList: List<FieldData>
) {

    private val notFoundFiledNameFormat =
        "else -> throw com.sabgil.exception.NotFoundFiledNameException()"

    fun generate(): FileSpec =
        FileSpec.builder(className.packageName, className.simpleName.addInnerMapperClassSuffix())
            .addType(objectBuild())
            .build()

    private fun objectBuild(): TypeSpec =
        TypeSpec.objectBuilder(className.simpleName.addInnerMapperClassSuffix())
            .addFunction(funBuild())
            .build()

    private fun funBuild(): FunSpec =
        FunSpec.builder(METHOD_NAME)
            .addParameter(PARAM_NAME__FIELD_NAME, typeString)
            .addParameter(PARAM_NAME__INTENT, typeIntent)
            .returns(typeNullableAny)
            .beginControlFlow("return when(%L)", PARAM_NAME__FIELD_NAME)
            .addWhenCasesStatement()
            .addStatement(notFoundFiledNameFormat)
            .endControlFlow()
            .build()

    private fun FunSpec.Builder.addWhenCasesStatement(): FunSpec.Builder {
        fieldDataList.forEach { fieldData ->
            addStatement(mappingType(fieldData), fieldData.fieldName, fieldData.fieldName)
        }
        return this
    }

    private fun mappingType(fieldData: FieldData) = fieldData.mapTo().methodFormat

    private fun String.addInnerMapperClassSuffix() = "${this}_$NAME_SUFFIX"
}