package com.sabgil.extraofsample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sabgil.extra.extra

class MainActivity3 : AppCompatActivity() {

    private val abc: Boolean by extra()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}