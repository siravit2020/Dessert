package com.jabirdeveloper.tinderswipe.Register

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

import com.google.firebase.auth.FirebaseAuth
import com.jabirdeveloper.tinderswipe.R
import com.jabirdeveloper.tinderswipe.Register.Regis_name_Activity
import com.tapadoo.alerter.Alerter

class Regis_name_Activity : AppCompatActivity() {
    private lateinit var b1: Button
    private lateinit var t1: EditText
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var toolbar: Toolbar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_regis_name_)
        b1 = findViewById(R.id.button_name)
        t1 = findViewById(R.id.editText5)
        toolbar = findViewById(R.id.my_tools)
        setSupportActionBar(toolbar)
        supportActionBar!!.setTitle(R.string.registered)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        b1.setOnClickListener(View.OnClickListener {
            if (!t1.text.toString().trim { it <= ' ' }.isEmpty()) {
                Alerter.hide()
                val intent = Intent(this@Regis_name_Activity, RegisGpsActivity::class.java)
                intent.putExtra("Name", t1.text.toString())
                intent.putExtra("Type", getIntent().getStringExtra("Type"))
                intent.putExtra("email", getIntent().getStringExtra("email"))
                intent.putExtra("password", getIntent().getStringExtra("password"))
                startActivity(intent)
            } else {
                Alerter.create(this@Regis_name_Activity)
                        .setTitle(R.string.Noti)
                        .setText(getString(R.string.enter_name))
                        .setBackgroundColorRes(R.color.c2)
                        .show()
            }
        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.signOut()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // todo: goto back activity from here
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}