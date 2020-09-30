package com.sabgil.processor

import javax.lang.model.type.TypeKind
import javax.lang.model.type.TypeMirror

object TypeMapper {

}

enum class ExtraMethodType(
    val methodFormat: String
) {
    BOOLEAN("%S -> intent.getBooleanExtra(%S, false)"),
    BYTE("%S -> intent.getByteExtra(%S, Byte.MIN_VALUE)"),
    SHORT("%S -> intent.getShortExtra(%S, Short.MIN_VALUE)"),
    INT("%S -> intent.getIntExtra(%S, 0)"),
    LONG("%S -> intent.getLongExtra(%S, 0L)"),
    CHAR("%S -> intent.getCharExtra(%S, Char.MIN_VALUE)"),
    FLOAT("%S -> intent.getFloatExtra(%S, 0f)"),
    DOUBLE("%S -> intent.getDoubleExtra(%S, 0.0)"),
    STRING("%S -> intent.getStringExtra(%S)"),
    SERIALIZABLE("%S -> intent.getSerializableExtra(%S)");

    companion object {
        fun FieldData.mapTo(): ExtraMethodType {
            return if (type.kind.isPrimitive) {
                when (type.kind) {
                    TypeKind.BOOLEAN -> BOOLEAN
                    TypeKind.BYTE -> BYTE
                    TypeKind.SHORT -> SHORT
                    TypeKind.INT -> INT
                    TypeKind.LONG -> LONG
                    TypeKind.CHAR -> CHAR
                    TypeKind.FLOAT -> FLOAT
                    TypeKind.DOUBLE -> DOUBLE
                    else -> throw IllegalStateException("Unsuitable field type")
                }
            } else {
                if (isString(type)) {
                    STRING
                } else {
                    SERIALIZABLE
                }
            }
        }

        private fun isString(type: TypeMirror) = type.toString() == "java.lang.String"
    }
}