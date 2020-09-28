package com.jabirdeveloper.tinderswipe.Register

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.jabirdeveloper.tinderswipe.R
import com.tapadoo.alerter.Alerter

class Regis_Sex_Activity : AppCompatActivity() {
    private lateinit var b3: Button
    private var sex: String? = null
    private lateinit var imageView: ImageView
    private lateinit var imageView2: ImageView
    private lateinit var toolbar: Toolbar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_regis__sex_)
        imageView = findViewById(R.id.imageView2)
        imageView2 = findViewById(R.id.imageView22)
        toolbar = findViewById(R.id.my_tools)
        setSupportActionBar(toolbar)
        supportActionBar!!.setTitle(R.string.registered)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        b3 = findViewById(R.id.button_name)
        imageView.setOnClickListener(View.OnClickListener {
            imageView.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.male2))
            imageView2.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.female1))
            sex = "Male"
        })
        imageView2.setOnClickListener(View.OnClickListener {
            imageView.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.male1))
            imageView2.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.female2))
            sex = "Female"
        })
        b3.setOnClickListener(View.OnClickListener {
            if (sex != null) {
                Alerter.hide()
                val intent = Intent(this@Regis_Sex_Activity, Regis_ageActivity::class.java)
                intent.putExtra("Sex", sex)
                intent.putExtra("X", getIntent().getDoubleExtra("X", 0.0))
                intent.putExtra("Y", getIntent().getDoubleExtra("Y", 0.0))
                intent.putExtra("Type", getIntent().getStringExtra("Type"))
                intent.putExtra("Name", getIntent().getStringExtra("Name"))
                intent.putExtra("email", getIntent().getStringExtra("email"))
                intent.putExtra("password", getIntent().getStringExtra("password"))
                startActivity(intent)
                return@OnClickListener
            } else {
                Alerter.create(this@Regis_Sex_Activity)
                        .setTitle(R.string.Noti)
                        .setText(getString(R.string.choose_gender))
                        .setBackgroundColorRes(R.color.c2)
                        .show()
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}