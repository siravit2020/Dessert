package com.jabirdeveloper.tinderswipe.Register

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.hbb20.CountryCodePicker
import com.jabirdeveloper.tinderswipe.R

class PhoneActivity : AppCompatActivity() {
    private lateinit var b1: Button
    private lateinit var e1: EditText
    private lateinit var ccp: CountryCodePicker
    private lateinit var toolbar: Toolbar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone)
        toolbar = findViewById(R.id.my_tools)
        setSupportActionBar(toolbar)
        supportActionBar!!.setTitle(R.string.signin_phone)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        e1 = findViewById(R.id.edit_phone_input)
        b1 = findViewById(R.id.button10)
        ccp = findViewById(R.id.ccp)
        b1.setOnClickListener(View.OnClickListener {
            val phone = e1.text.toString().trim { it <= ' ' }
            if (phone.isEmpty() || phone.length != 10) {
                e1.error = getString(R.string.enter_number)
                e1.requestFocus()
                return@OnClickListener
            } else {
                val intent = Intent(this@PhoneActivity, VerifyActivity::class.java)
                intent.putExtra("Phone", "+" + ccp.selectedCountryCode + e1.text.toString().trim { it <= ' ' })
                startActivity(intent)
            }
        })
    }
}