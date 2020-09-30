package com.sabgil.extraofsample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sabgil.annotation.Extra
import com.sabgil.extraof.extraOf

class MainActivity2 : AppCompatActivity() {

    @Extra
    private val a: Int by extraOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}