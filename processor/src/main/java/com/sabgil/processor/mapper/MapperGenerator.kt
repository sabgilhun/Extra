package com.sabgil.processor.mapper

import com.sabgil.processor.common.Inner.METHOD_NAME
import com.sabgil.processor.common.Inner.NAME_SUFFIX
import com.sabgil.processor.common.Inner.PARAM_NAME__FIELD_NAME
import com.sabgil.processor.common.Inner.PARAM_NAME__INTENT
import com.sabgil.processor.common.model.FieldData
import com.sabgil.processor.common.typeIntent
import com.sabgil.processor.common.typeKProperty
import com.sabgil.processor.common.typeNullableAny
import com.sabgil.processor.common.typePropertyMapper
import com.sabgil.processor.inner.ExtraMethodType.Companion.mapTo
import com.squareup.kotlinpoet.*

class MapperGenerator(
    private val className: ClassName,
    private val fieldDataList: List<FieldData>
) {

    private val notFoundFiledNameFormat =
        "else -> throw com.sabgil.exception.NotFoundFiledNameException()"

    fun generate(): FileSpec =
        FileSpec.builder(className.packageName, className.simpleName.addInnerMapperClassSuffix())
            .addType(classBuild())
            .build()

    private fun classBuild(): TypeSpec =
        TypeSpec.classBuilder(className.simpleName.addInnerMapperClassSuffix())
            .addSuperinterface(typePropertyMapper)
            .addFunction(funBuild())
            .addType(companionObjectBuild())
            .build()

    private fun companionObjectBuild(): TypeSpec =
        TypeSpec.companionObjectBuilder()
            .addInitializerBlock(initializerBlockBuild())
            .build()

    private fun initializerBlockBuild(): CodeBlock =
        CodeBlock.builder()
            .apply {
                addStatement(
                    "com.sabgil.extra.MapperManager.mappers.put(com.sabgil.extra.PropertyMapper.Key.withMapper(%L::class.java), %L())",
                    className.simpleName.addInnerMapperClassSuffix(),
                    className.simpleName.addInnerMapperClassSuffix()
                )
            }
            .build()

    private fun funBuild(): FunSpec =
        FunSpec.builder(METHOD_NAME)
            .addModifiers(KModifier.OVERRIDE)
            .addParameter(PARAM_NAME__FIELD_NAME, typeKProperty)
            .addParameter(PARAM_NAME__INTENT, typeIntent)
            .returns(typeNullableAny)
            .beginControlFlow("return when(%L)", "$PARAM_NAME__FIELD_NAME.name")
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