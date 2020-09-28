package com.jabirdeveloper.tinderswipe

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class BandUser : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_band_user)
        (findViewById<Button>(R.id.button_back)).setOnClickListener {

            startActivity(Intent(applicationContext, ChooseLoginRegistrationActivity::class.java))
            finish()
        }
    }
}