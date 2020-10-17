package com.sabgil.extraofsample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sabgil.annotation.Extra
import com.sabgil.extra.extra
import com.sabgil.extra.extraOf

class MainActivity3 : AppCompatActivity() {

    @Extra
    private val abc: Boolean by extra()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}