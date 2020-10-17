package com.sabgil.processor

import com.google.auto.service.AutoService
import com.sabgil.annotation.Extra
import com.sabgil.exception.IllegalAnnotationPlaceException
import com.sabgil.exception.NotFoundFairElementException
import com.sabgil.processor.common.model.FieldData
import com.sabgil.processor.common.toClassName
import com.sabgil.processor.mapper.MapperGenerator
import com.squareup.kotlinpoet.ClassName
import java.io.File
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement

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

        val fieldMap = classifyByIntentOwner(elements)

        if (fieldMap.isEmpty()) {
            return false
        }

        val file = createKotlinGeneratedDir()
        fieldMap.entries.map {
            MapperGenerator(
                it.key,
                it.value
            ).generate()
        }.forEach { it.writeTo(file) }

        return true
    }

    private fun classifyByIntentOwner(
        elements: Set<Element>
    ): Map<ClassName, List<FieldData>> {
        val map = hashMapOf<ClassName, List<FieldData>>()

        elements.forEach { element ->
            if (element.kind != ElementKind.METHOD) {
                throw IllegalAnnotationPlaceException()
            }

            val className = element.toClassName()
            val methodList = map[className]?.toMutableList() ?: mutableListOf()

            val getterElement = findGetterElement(element)

            methodList.add(
                FieldData(
                    parseToPropertyName(getterElement),
                    (getterElement as ExecutableElement).returnType
                )
            )

            map[className] = methodList
        }

        return map
    }

    private fun findGetterElement(
        element: Element
    ): Element {
        val suffixRemovedName = element.simpleName.toString()
            .removeSuffix("\$annotations")
        val parent = element.enclosingElement

        return parent.enclosedElements.find {
            it.simpleName.toString() == suffixRemovedName
        } ?: throw NotFoundFairElementException()
    }

    private fun parseToPropertyName(element: Element): String {
        val prefixRemovedName = element.simpleName.toString()
            .removePrefix("get")

        val sb = StringBuilder(prefixRemovedName)

        sb.setCharAt(
            0,
            Character.toLowerCase(prefixRemovedName[0])
        )

        return sb.toString()
    }

    private fun createKotlinGeneratedDir() = File(
        processingEnv.options[com.sabgil.processor.common.KAPT_GENERATED_PACKAGE], ""
    )
}