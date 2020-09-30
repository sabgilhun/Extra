package com.sabgil.extraofsample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sabgil.annotation.Extra
import com.sabgil.extraof.extraOf

class MainActivity : AppCompatActivity() {

    @Extra
    private val a: Int by extraOf()

    @Extra
    private val b: String = ""

    @Extra
    private val c: Test? = null

    @Extra
    private val d: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        println(a)
    }

    class Test
}