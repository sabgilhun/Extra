package com.sabgil.processor.mapper

import com.sabgil.processor.common.model.FieldData
import javax.lang.model.type.TypeKind

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
    CHAR_SEQUENCE("%S -> intent.getCharSequenceExtra(%S)"),
    BOOLEAN_ARRAY("%S -> intent.getBooleanArrayExtra(%S)"),
    BYTE_ARRAY("%S -> intent.getByteArrayExtra(%S)"),
    SHORT_ARRAY("%S -> intent.getShortArrayExtra(%S)"),
    INT_ARRAY("%S -> intent.getIntArrayExtra(%S)"),
    LONG_ARRAY("%S -> intent.getLongArrayExtra(%S)"),
    CHAR_ARRAY("%S -> intent.getCharArrayExtra(%S)"),
    FLOAT_ARRAY("%S -> intent.getFloatArrayExtra(%S)"),
    DOUBLE_ARRAY("%S -> intent.getDoubleArrayExtra(%S)"),
    INT_ARRAY_LIST("%S -> intent.getIntegerArrayListExtra(%S)"),
    STRING_ARRAY_LIST("%S -> intent.getStringArrayListExtra(%S)"),
    CHAR_SEQUENCE_ARRAY_LIST("%S -> intent.getCharSequenceArrayListExtra(%S)"),
    SERIALIZABLE("%S -> intent.getSerializableExtra(%S)");

    companion object {
        private const val booleanArray = "boolean[]"
        private const val byteArray = "byte[]"
        private const val shortArray = "short[]"
        private const val intArray = "int[]"
        private const val longArray = "long[]"
        private const val charArray = "char[]"
        private const val floatArray = "float[]"
        private const val doubleArray = "double[]"

        private const val string = "java.lang.String"
        private const val charSequence = "java.lang.CharSequence"

        private const val intArrayList = "java.util.ArrayList<java.lang.Integer>"
        private const val stringArrayList = "java.util.ArrayList<java.lang.String>"
        private const val charSequenceArrayList = "java.util.ArrayList<java.lang.CharSequence>"

        fun FieldData.mapTo(): ExtraMethodType =
            when {
                type.kind.isPrimitive -> {
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
                }
                type.kind == TypeKind.ARRAY -> {
                    when (type.toString()) {
                        booleanArray -> BOOLEAN_ARRAY
                        byteArray -> BYTE_ARRAY
                        shortArray -> SHORT_ARRAY
                        intArray -> INT_ARRAY
                        longArray -> LONG_ARRAY
                        charArray -> CHAR_ARRAY
                        floatArray -> FLOAT_ARRAY
                        doubleArray -> DOUBLE_ARRAY
                        else -> throw IllegalStateException("Unsuitable field type")
                    }
                }
                else -> {
                    when (type.toString()) {
                        string -> STRING
                        charSequence -> CHAR_SEQUENCE
                        intArrayList -> INT_ARRAY_LIST
                        stringArrayList -> STRING_ARRAY_LIST
                        charSequenceArrayList -> CHAR_SEQUENCE_ARRAY_LIST
                        else -> {
                            SERIALIZABLE
                        }
                    }
                }
            }
    }
}