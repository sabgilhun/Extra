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
import javax.lang.model.element.ElementKind
import javax.lang.model.element.PackageElement
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

        val activityMap = mutableMapOf<ClassName, MutableList<FieldData>>()

        elements.forEach {
            if (it.kind != ElementKind.FIELD) {
                processingEnv.messager.printMessage(
                    Diagnostic.Kind.ERROR,
                    "Extra annotation unable to place to not filed"
                )
                return false
            }

            var e: Element = it.enclosingElement
            val simpleName = (e as TypeElement).simpleName.toString()
            while (e !is PackageElement) {
                e = e.enclosingElement
            }
            val packageName = e.qualifiedName.toString()
            val classNName = ClassName(packageName, simpleName)
            (it.enclosingElement as TypeElement).simpleName
            val methodList = activityMap[classNName] ?: mutableListOf()

            methodList.add(FieldData(it.simpleName.toString(), it.asType()))
            activityMap.putIfAbsent(classNName, methodList)
        }

        if (activityMap.isEmpty()) {
            return true
        }

        val mapperObjectSpecBuilder = TypeSpec.objectBuilder("Mapper")
        val outerMapperFunSpec = FunSpec.builder("map")
            .addParameter("filedName", String::class)
            .addParameter("intent", ClassName("android.content", "Intent"))
            .addParameter(
                "activityClass",
                ClassName("java.lang", "Class")
                    .parameterizedBy(
                        ClassName(
                            "android.app", "Activity"
                        )
                    )
            )
            .returns(Any::class.asTypeName().copy(true))
            .beginControlFlow("return when(activityClass)")

        val activityMapperFileSpecList = mutableListOf<FileSpec>()
        activityMap.keys.forEach { className ->
            outerMapperFunSpec.addStatement(
                "%L::class.java -> %L_Mapper.map(filedName, intent)",
                className.canonicalName,
                className.canonicalName
            )
            val funSpec = FunSpec.builder("map")
                .addParameter("filedName", String::class)
                .addParameter("intent", ClassName("android.content", "Intent"))
                .returns(Any::class.asTypeName().copy(true))
                .beginControlFlow("return when(filedName)")

            activityMap[className]?.forEach {
                funSpec.addStatement(mappingType(it), it.fieldName, it.fieldName)
            }

            funSpec.addStatement("else -> throw com.sabgil.exception.NotFoundFiledNameException()")
                .endControlFlow()

            activityMapperFileSpecList.add(
                FileSpec.builder(className.packageName, className.simpleName + "_Mapper")
                    .addType(
                        TypeSpec.objectBuilder(className.simpleName + "_Mapper")
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

        val kaptKotlinGeneratedDir = processingEnv.options["kapt.kotlin.generated"]
        val file = File(kaptKotlinGeneratedDir, "")
        fileSpec.writeTo(file)

        activityMapperFileSpecList.forEach {
            it.writeTo(file)
        }
        return true
    }

    private fun mappingType(fieldData: FieldData) = fieldData.mapTo().methodFormat
}