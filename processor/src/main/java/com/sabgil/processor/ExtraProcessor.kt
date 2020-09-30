package com.sabgil.processor

import com.google.auto.service.AutoService
import com.sabgil.annotation.Extra
import com.sabgil.processor.ExtraMethodType.Companion.mapTo
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import java.io.File
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

@AutoService(Processor::class)
class ExtraProcessor : AbstractProcessor() {

    override fun getSupportedAnnotationTypes(): Set<String> =
        object : HashSet<String>() {
            init {
                add(Extra::class.java.canonicalName)
            }
        }

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latestSupported()

    override fun process(set: Set<TypeElement>, roundEnvironment: RoundEnvironment): Boolean {
        val elements: Set<Element> = roundEnvironment.getElementsAnnotatedWith(Extra::class.java)

        val fieldMap = classifyByIntentOwner(elements) {
            processingEnv.messager.printMessage(
                Diagnostic.Kind.ERROR,
                WRONG_ANNOTATION_PLACE_ASSERT
            )
            return false
        }

        if (fieldMap.isEmpty()) {
            return false
        }

        val mapperObjectSpecBuilder = TypeSpec.objectBuilder(OUTER_MAPPER_NAME)
        val outerMapperFunSpec = FunSpec.builder(OUTER_MAPPER_METHOD_NAME)
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

        val activityMapperFileSpecList = mutableListOf<FileSpec>()
        fieldMap.keys.forEach { className ->
            outerMapperFunSpec.addStatement(
                "%L -> %L.%L(%L, %L)",
                className.canonicalName.addJavaClassKeyword(),
                className.canonicalName.addInnerMapperClassSuffix(),
                INNER_MAPPER_METHOD_NAME,
                OUTER_MAPPER_METHOD_PARAM_NAME__FIELD_NAME,
                OUTER_MAPPER_METHOD_PARAM_NAME__INTENT
            )
            val funSpec = FunSpec.builder("map")
                .addParameter(INNER_MAPPER_METHOD_PARAM_NAME__FIELD_NAME, String::class)
                .addParameter(
                    INNER_MAPPER_METHOD_PARAM_NAME__INTENT,
                    ClassName("android.content", "Intent")
                )
                .returns(Any::class.asTypeName().copy(true))
                .beginControlFlow("return when(%L)", INNER_MAPPER_METHOD_PARAM_NAME__FIELD_NAME)

            fieldMap[className]?.forEach {
                funSpec.addStatement(mappingType(it), it.fieldName, it.fieldName)
            }

            funSpec.addStatement("else -> throw com.sabgil.exception.NotFoundFiledNameException()")
                .endControlFlow()

            activityMapperFileSpecList.add(
                FileSpec.builder(
                    className.packageName,
                    className.simpleName.addInnerMapperClassSuffix()
                )
                    .addType(
                        TypeSpec.objectBuilder(className.simpleName.addInnerMapperClassSuffix())
                            .addFunction(funSpec.build())
                            .build()
                    )
                    .build()
            )
        }

        outerMapperFunSpec.addStatement("else -> throw com.sabgil.exception.NotFoundActivityClassException()")
            .endControlFlow()

        val fileSpec = FileSpec.builder("", "Mapper")
            .addType(
                mapperObjectSpecBuilder.addFunction(
                    outerMapperFunSpec.build()
                ).build()
            )
            .build()

        val kaptKotlinGeneratedDir = processingEnv.options[KAPT_GENERATED_PACKAGE]
        val file = File(kaptKotlinGeneratedDir, "")
        fileSpec.writeTo(file)

        activityMapperFileSpecList.forEach {
            it.writeTo(file)
        }
        return true
    }

    private fun mappingType(fieldData: FieldData) = fieldData.mapTo().methodFormat

    private inline fun classifyByIntentOwner(
        elements: Set<Element>,
        onFoundNotField: () -> Unit
    ): Map<ClassName, List<FieldData>> {
        val map = hashMapOf<ClassName, List<FieldData>>()

        elements.forEach { element ->
            if (!element.kind.isField) {
                onFoundNotField()
                return emptyMap()
            }

            val className = element.toClassName()
            val methodList = map[className]?.toMutableList() ?: mutableListOf()

            methodList.add(
                FieldData(element.simpleName.toString(), element.asType())
            )

            map.putIfAbsent(className, methodList)
        }

        return map
    }

    private fun String.addJavaClassKeyword() = "$this::class.java"
    private fun String.addInnerMapperClassSuffix() = "${this}_$INNER_MAPPER_NAME_SUFFIX"
}