package com.sabgil.processor

import com.google.auto.service.AutoService
import com.sabgil.annotation.Extra
import com.squareup.kotlinpoet.ClassName
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

        val outerMapperFileSpec = OuterMapperGenerator(fieldMap).generate()
        val innerMapperFileSpecs = fieldMap.entries.map {
            InnerMapperGenerator(
                it.key,
                it.value
            ).generate()
        }

        val file = createKotlinGeneratedDir()
        outerMapperFileSpec.writeTo(file)

        innerMapperFileSpecs.forEach {
            it.writeTo(file)
        }

        return true
    }

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

            map[className] = methodList
        }

        return map
    }

    private fun createKotlinGeneratedDir() = File(
        processingEnv.options[KAPT_GENERATED_PACKAGE], ""
    )
}