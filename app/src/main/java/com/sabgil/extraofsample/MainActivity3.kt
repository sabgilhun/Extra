package com.sabgil.extraofsample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sabgil.annotation.Extra

class MainActivity3 : AppCompatActivity() {

    @Extra
    private val a: Int = 0

    @Extra
    private val b: Int = 0

    @Extra
    private val c: Int = 0

    @Extra
    private val d: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}