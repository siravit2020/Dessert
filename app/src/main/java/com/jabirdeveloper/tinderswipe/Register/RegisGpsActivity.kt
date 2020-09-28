package com.jabirdeveloper.tinderswipe.Register

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.jabirdeveloper.tinderswipe.ChooseLoginRegistrationActivity
import com.jabirdeveloper.tinderswipe.R

class RegisGpsActivity : AppCompatActivity(), LocationListener {
    private lateinit var mGPSDialog: Dialog
    private lateinit var mLocationManager: LocationManager
    private lateinit var imageView: ImageView
    private lateinit var anime1: ImageView
    private lateinit var anime2: ImageView
    private lateinit var handler: Handler
    private var chk = 0
    private lateinit var toolbar: Toolbar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_regis_gps)
        handler = Handler()
        imageView = findViewById(R.id.imageView3)
        anime1 = findViewById(R.id.anime1)
        anime2 = findViewById(R.id.anime2)
        runnable!!.run()
        toolbar = findViewById(R.id.my_tools)
        setSupportActionBar(toolbar)
        supportActionBar!!.setTitle(R.string.registered)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        mLocationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf<String?>(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.INTERNET
            ), 1)
        } else {
            var location =
                    mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            getLocation(location);
            //mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 0f, this)
        }
    }

    private fun getLocation(location: Location) {
        val intent = Intent(this@RegisGpsActivity, Regis_Sex_Activity::class.java)
        intent.putExtra("X", location.latitude)
        intent.putExtra("Y", location.longitude)
        intent.putExtra("Name", getIntent().getStringExtra("Name"))
        intent.putExtra("Type", getIntent().getStringExtra("Type"))
        intent.putExtra("email", getIntent().getStringExtra("email"))
        intent.putExtra("password", getIntent().getStringExtra("password"))
        startActivity(intent)
    }

    override fun onLocationChanged(location: Location) {
        /*val latitude = location.latitude
        val longitude = location.longitude
        // val value1 = latitude.toString()
        // val value2 = longitude.toString()
        if (chk == 0) {
            chk = -1
            val intent = Intent(this@RegisGpsActivity, Regis_Sex_Activity::class.java)
            intent.putExtra("X", latitude)
            intent.putExtra("Y", longitude)
            intent.putExtra("Name", getIntent().getStringExtra("Name"))
            intent.putExtra("Type", getIntent().getStringExtra("Type"))
            intent.putExtra("email", getIntent().getStringExtra("email"))
            intent.putExtra("password", getIntent().getStringExtra("password"))
            startActivity(intent)
        }*/
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
    override fun onProviderEnabled(provider: String?) {}
    override fun onProviderDisabled(provider: String?) {
        if (provider == LocationManager.GPS_PROVIDER) {
            showGPSDisabledDialog()
        }
    }

    private fun showGPSDisabledDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.GPS_Disabled)
        builder.setMessage(R.string.GPS_open)
        builder.setPositiveButton(R.string.open_gps) { dialog, which -> startActivityForResult(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0) }.setNegativeButton(R.string.report_close) { dialog, which -> }
        mGPSDialog = builder.create()
        mGPSDialog.show()
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0) {
            if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                showGPSDisabledDialog()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                recreate()
            } else {
                startActivity(Intent(this@RegisGpsActivity, ChooseLoginRegistrationActivity::class.java))
            }
        }
    }

    private val runnable: Runnable? = object : Runnable {
        override fun run() {
            anime1.animate().scaleX(4f).scaleY(4f).alpha(0f).setDuration(900).withEndAction {
                anime1.scaleX = 1f
                anime1.scaleY = 1f
                anime1.alpha = 1f
            }
            anime2.animate().scaleX(4f).scaleY(4f).alpha(0f).setDuration(1400).withEndAction {
                anime2.scaleX = 1f
                anime2.scaleY = 1f
                anime2.alpha = 1f
            }
            handler.postDelayed(this, 1500)
        }
    }
}