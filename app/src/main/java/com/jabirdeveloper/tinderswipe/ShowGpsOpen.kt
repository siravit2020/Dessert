package com.jabirdeveloper.tinderswipe

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class ShowGpsOpen : AppCompatActivity() {
    private lateinit var open: Button
    private lateinit var mLocationManager: LocationManager
    private lateinit var textView: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_gps_open)
        open = findViewById(R.id.open)
        textView = findViewById(R.id.text_gps)
        if (intent.hasExtra("2")) {
            textView.text = "คุณต้องอนุญาตให้ระบบเข้าถึงตำแหน่งที่ตั้งเพื่อใช้งานแอปพลิเคชัน กรุณาลองใหม่อีกครั้ง"
        }
        mLocationManager = this@ShowGpsOpen.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        open.setOnClickListener(View.OnClickListener {
            mLocationManager = this@ShowGpsOpen.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if (!intent.hasExtra("2")) {
                if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) showGPSDiabledDialog() else {
                    val intent = Intent(this@ShowGpsOpen, First_Activity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    finish()
                    startActivity(intent)
                }
            } else if (intent.hasExtra("2")) {
                if (ActivityCompat.checkSelfPermission(this@ShowGpsOpen, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(this@ShowGpsOpen, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    showGPSDiabledDialog()
                } else {
                    val intent = Intent(this@ShowGpsOpen, First_Activity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    finish()
                    startActivity(intent)
                }
            }
        })
    }

    fun showGPSDiabledDialog() {
        if (!intent.hasExtra("2")) {
            val builder = AlertDialog.Builder(this@ShowGpsOpen)
            builder.setTitle(R.string.GPS_Disabled)
            builder.setMessage(R.string.GPS_open)
            builder.setPositiveButton(R.string.open_gps) { dialog, which -> startActivityForResult(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0) }.setNegativeButton(R.string.report_close) { dialog, which -> }
            val mGPSDialog: Dialog = builder.create()
            mGPSDialog.window!!.setBackgroundDrawable(ContextCompat.getDrawable(this@ShowGpsOpen, R.drawable.myrect2))
            mGPSDialog.show()
        } else {
            ActivityCompat.requestPermissions(this@ShowGpsOpen, arrayOf<String?>(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.INTERNET
            ), 1)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0) {
            val intent = Intent(this@ShowGpsOpen, First_Activity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            finish()
            startActivity(intent)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val intent = Intent(this@ShowGpsOpen, First_Activity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                finish()
                startActivity(intent)
            }
        }
    }
}