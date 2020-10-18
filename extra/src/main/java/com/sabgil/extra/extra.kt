package com.sabgil.extra

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment

inline fun <reified T : Activity> Context.intentFor(
    vararg extras: Pair<String, Any?>,
    apply: Intent.() -> Unit = {}
) = Intent(this, T::class.java).apply {
    putExtras(bundleOf(*extras))
    apply(this)
}

fun <T : Activity, B> T.extra(key: String? = null) =
    ExtraValueHolder { property ->
        @Suppress("UNCHECKED_CAST")
        intent.extras?.get(key ?: property.name) as B
    }

fun <T : Fragment, B> T.extra(key: String? = null) =
    ExtraValueHolder { property ->
        @Suppress("UNCHECKED_CAST")
        arguments?.get(key ?: property.name) as B
    }