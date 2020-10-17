package com.sabgil.processor.mapper

import com.sabgil.processor.common.Mapper.MAPPER_MANAGER_PACKAGE_NAME
import com.sabgil.processor.common.Mapper.METHOD_NAME
import com.sabgil.processor.common.Mapper.NAME_SUFFIX
import com.sabgil.processor.common.Mapper.PARAM_NAME__FIELD_NAME
import com.sabgil.processor.common.Mapper.PARAM_NAME__INTENT
import com.sabgil.processor.common.Mapper.PROPERTY_MANAGER_KEY_PACKAGE_NAME
import com.sabgil.processor.common.model.FieldData
import com.sabgil.processor.common.typeIntent
import com.sabgil.processor.common.typeKProperty
import com.sabgil.processor.common.typeNullableAny
import com.sabgil.processor.common.typePropertyMapper
import com.sabgil.processor.mapper.ExtraMethodType.Companion.mapTo
import com.squareup.kotlinpoet.*

class MapperGenerator(
    className: ClassName,
    private val fieldDataList: List<FieldData>
) {
    private val mapperClassName = className.simpleName.addInnerMapperClassSuffix()
    private val packageName = className.packageName

    private val notFoundFiledNameFormat =
        "else -> throw com.sabgil.exception.NotFoundFiledNameException()"

    fun generate(): FileSpec =
        FileSpec.builder(packageName, mapperClassName)
            .addType(classBuild())
            .build()

    private fun classBuild(): TypeSpec =
        TypeSpec.classBuilder(mapperClassName)
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
                    "val mappers = %M",
                    MemberName(MAPPER_MANAGER_PACKAGE_NAME, "mappers")
                )
                addStatement(
                    "val key = %M(%L::class.java)",
                    MemberName(PROPERTY_MANAGER_KEY_PACKAGE_NAME, "withMapper"),
                    mapperClassName
                )
                addStatement("mappers[key] = %L()", mapperClassName)
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