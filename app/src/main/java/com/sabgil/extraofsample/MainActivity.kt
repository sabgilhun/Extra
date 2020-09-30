package com.sabgil.extraofsample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sabgil.annotation.Extra
import com.sabgil.extraof.extraOf

class MainActivity : AppCompatActivity() {

    @Extra
    private val aqsASASAS: String by extraOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        println(aqsASASAS)
    }
}