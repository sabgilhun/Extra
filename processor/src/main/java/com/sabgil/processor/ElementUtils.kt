package com.sabgil.processor

import com.squareup.kotlinpoet.ClassName
import javax.lang.model.element.Element
import javax.lang.model.element.PackageElement
import javax.lang.model.element.TypeElement

fun Element.packageElement(): PackageElement {
    var _element = this

    while (_element !is PackageElement) {
        _element = _element.enclosingElement
    }

    return _element
}


fun Element.packageName(): String = packageElement().qualifiedName.toString()

fun Element.outerClassElement(): TypeElement {
    var _element = this

    while (_element !is TypeElement) {
        _element = _element.enclosingElement
    }

    return _element
}

fun Element.outerClassSimpleName(): String = outerClassElement().simpleName.toString()

fun Element.toClassName(): ClassName = ClassName(packageName(), outerClassSimpleName())