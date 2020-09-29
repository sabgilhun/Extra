package com.sabgil.processor

import com.google.auto.service.AutoService
import com.sabgil.annotation.Extra
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement

@AutoService(Processor::class)
class ExtraProcessor : AbstractProcessor() {

    override fun init(processingEnvironment: ProcessingEnvironment) {
        super.init(processingEnvironment)
    }

    override fun getSupportedAnnotationTypes(): Set<String> =
        object : HashSet<String>() {
            init {
                add(Extra::class.java.canonicalName)
            }
        }

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latestSupported()

    override fun process(set: Set<TypeElement>, roundEnvironment: RoundEnvironment): Boolean {
        TODO("Not yet implemented")
    }
}