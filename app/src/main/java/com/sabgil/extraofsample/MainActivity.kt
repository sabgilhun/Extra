package com.sabgil.extraofsample

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sabgil.annotation.Extra
import com.sabgil.extra.extra
import com.sabgil.extra.extraOf

class MainActivity : AppCompatActivity() {

    @Extra
    private val aqsASASAS: Int by extra()

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, MainActivity::class.java).apply { putExtra("aqsASASAS", 259) })
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        println(aqsASASAS)
    }
}