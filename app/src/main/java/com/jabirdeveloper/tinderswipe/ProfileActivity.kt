package com.jabirdeveloper.tinderswipe

import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.View
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.jabirdeveloper.tinderswipe.ProfileActivity
import java.io.IOException
import java.util.*

class ProfileActivity : AppCompatActivity() {
    private lateinit var findImage: DatabaseReference
    private lateinit var mUserDatabase: DatabaseReference
    private lateinit var mLinear: LinearLayout
    private lateinit var l1: LinearLayout
    private lateinit var l2: LinearLayout
    private lateinit var l3: LinearLayout
    private lateinit var l4: LinearLayout
    private lateinit var l5: LinearLayout
    private lateinit var l6: LinearLayout
    private var Url0: String? = null
    private var Url1: String? = null
    private var Url2: String? = null
    private var Url3: String? = null
    private var Url4: String? = null
    private var Url5: String? = null
    private lateinit var userId: String
    private lateinit var listItems: Array<String?>
    private lateinit var listItems2: Array<String?>
    private lateinit var listItems3: Array<String?>
    private lateinit var name: TextView
    private lateinit var age: TextView
    private lateinit var gender: TextView
    private lateinit var study: TextView
    private lateinit var career: TextView
    private lateinit var religion: TextView
    private lateinit var myself: TextView
    private lateinit var language: TextView
    private lateinit var mcity: TextView
    private lateinit var mAuth: FirebaseAuth
    private lateinit var flexboxLayout: FlexboxLayout
    private lateinit var params: GridLayout.LayoutParams
    private var i = 0
    private var chk = 11
    private lateinit var viewPager: WrapContentHeightViewPager
    private lateinit var adapter: ScreenAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        mAuth = FirebaseAuth.getInstance()
        userId = mAuth.currentUser!!.uid
        mUserDatabase = FirebaseDatabase.getInstance().reference.child("Users").child(userId)
        flexboxLayout = findViewById(R.id.Grid_profile)
        flexboxLayout.flexDirection = FlexDirection.ROW
        params = GridLayout.LayoutParams()
        name = findViewById(R.id.name_profile)
        age = findViewById(R.id.age_profile)
        gender = findViewById(R.id.gender_profile)
        study = findViewById(R.id.study_profile)
        career = findViewById(R.id.career_profile)
        religion = findViewById(R.id.religion_profile)
        myself = findViewById(R.id.myself_profile)
        language = findViewById(R.id.language_profile)
        viewPager = findViewById(R.id.slide_main)
        mcity = findViewById(R.id.city_profile)
        mLinear = findViewById<View?>(R.id.main) as LinearLayout
        listItems = resources.getStringArray(R.array.shopping_item)
        listItems2 = resources.getStringArray(R.array.pasa_item)
        listItems3 = resources.getStringArray(R.array.religion_item)
        l1 = findViewById(R.id.linearLayout33)
        l2 = findViewById(R.id.linearLayout34)
        l3 = findViewById(R.id.linearLayout35)
        l4 = findViewById(R.id.linearLayout36)
        l5 = findViewById(R.id.linearLayout37)
        l6 = findViewById(R.id.linearLayout38)
        l1.visibility = View.GONE
        l2.visibility = View.GONE
        l3.visibility = View.GONE
        l4.visibility = View.GONE
        l5.visibility = View.GONE
        l6.visibility = View.GONE
        val fab = findViewById<FloatingActionButton>(R.id.floatingActionButton)
        fab.setOnClickListener {
            val intent = Intent(this@ProfileActivity, SettingActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivityForResult(intent, 123)
        }
        viewPager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                if (chk == 11) {
                    if (i > 1) {
                        val dd = findViewById<View?>(position) as LinearLayout
                        dd.background = ContextCompat.getDrawable(this@ProfileActivity, R.drawable.image_selected)
                    }
                }
                chk = 0
            }

            override fun onPageSelected(position: Int) {
                val total = adapter.count
                for (jk in total - 1 downTo 0) {
                    if (jk == position) {
                        val dd = findViewById<View?>(position) as LinearLayout
                        dd.background = ContextCompat.getDrawable(this@ProfileActivity, R.drawable.image_selected)
                    } else {
                        val dd = findViewById<View?>(jk) as LinearLayout
                        dd.background = ContextCompat.getDrawable(this@ProfileActivity, R.drawable.image_notselector)
                    }
                }
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
        findIamge()
        getUserinfo()
    }

    private fun findIamge() {
        findImage = FirebaseDatabase.getInstance().reference.child("Users").child(userId).child("ProfileImage")
        findImage.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Url0 = "null"
                Url1 = "null"
                Url2 = "null"
                Url3 = "null"
                Url4 = "null"
                Url5 = "null"
                if (dataSnapshot.hasChild("profileImageUrl0")) {
                    Url0 = dataSnapshot.child("profileImageUrl0").value.toString()
                    ++i
                }
                if (dataSnapshot.hasChild("profileImageUrl1")) {
                    Url1 = dataSnapshot.child("profileImageUrl1").value.toString()
                    ++i
                }
                if (dataSnapshot.hasChild("profileImageUrl2")) {
                    Url2 = dataSnapshot.child("profileImageUrl2").value.toString()
                    ++i
                }
                if (dataSnapshot.hasChild("profileImageUrl3")) {
                    Url3 = dataSnapshot.child("profileImageUrl3").value.toString()
                    ++i
                }
                if (dataSnapshot.hasChild("profileImageUrl4")) {
                    Url4 = dataSnapshot.child("profileImageUrl4").value.toString()
                    ++i
                }
                if (dataSnapshot.hasChild("profileImageUrl5")) {
                    Url5 = dataSnapshot.child("profileImageUrl5").value.toString()
                    ++i
                }
                if (i > 1) {
                    for (j in 0 until i) {
                        val layoutParams = LinearLayout.LayoutParams(50, 12, 0.5f)
                        layoutParams.setMargins(5, 0, 5, 0)
                        val layout = LinearLayout(this@ProfileActivity)
                        layout.background = ContextCompat.getDrawable(this@ProfileActivity, R.drawable.image_notselector)
                        layout.layoutParams = LinearLayout.LayoutParams(50, 12)
                        layout.id = j
                        mLinear.addView(layout, layoutParams)
                    }
                }
                adapter = ScreenAdapter(this@ProfileActivity, i, Url0, Url1, Url2, Url3, Url4, Url5, 0)
                viewPager.adapter = adapter
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun getUserinfo() {
        mUserDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.childrenCount > 0) {
                    val map = dataSnapshot.value as MutableMap<*, *>
                    if (map["myself"] != null && map["myself"] != "") {
                        l3.visibility = View.VISIBLE
                        myself.text = map["myself"].toString()
                    }
                    if (map["sex"] != null) {
                        if (map["sex"].toString() == "Male") gender.setText(R.string.Male_gender) else gender.setText(R.string.Female_gender)
                    }
                    if (map["name"] != null) {
                        name.text = map["name"].toString()
                    }
                    if (map["Age"] != null) {
                        age.text = map["Age"].toString()
                    }
                    if (map["career"] != null && map["career"] != "") {
                        l1.visibility = View.VISIBLE
                        career.text = map["career"].toString()
                    }
                    if (map["study"] != null && map["study"] != "") {
                        l2.visibility = View.VISIBLE
                        study.text = map["study"].toString()
                    }
                    if (map["language"] != null && map["language"] != "") {
                        l4.visibility = View.VISIBLE
                        val size = dataSnapshot.child("language").childrenCount.toInt()
                        var str = ""
                        for (u in 0 until size) {
                            val position = dataSnapshot.child("language").child("language$u").value.toString().toInt()
                            str += listItems2[position]
                            if (u != size - 1) {
                                str = "$str, "
                            }
                        }
                        language.text = str
                    }
                    if (map["religion"] != null && map["religion"] != "") {
                        l5.visibility = View.VISIBLE
                        religion.text = listItems3[map["religion"].toString().toInt()]
                    }
                    if (map["hobby"] != null && map["hobby"] != "") {
                        l6.visibility = View.VISIBLE
                        val size = dataSnapshot.child("hobby").childrenCount.toInt()
                        val str = ""
                        for (u in 0 until size) {
                            val position = dataSnapshot.child("hobby").child("hobby$u").value.toString().toInt()
                            addT(listItems[position])
                        }
                    }
                    if (dataSnapshot.hasChild("Location")) {
                        val lat = dataSnapshot.child("Location").child("X").value.toString()
                        val lon = dataSnapshot.child("Location").child("Y").value.toString()
                        val lat_double = java.lang.Double.valueOf(lat)
                        val lon_double = java.lang.Double.valueOf(lon)
                        val preferences = getSharedPreferences("Settings", Context.MODE_PRIVATE)
                        val langure = preferences.getString("My_Lang", "")
                        val ff: Geocoder
                        ff = if (langure == "th") {
                            Geocoder(this@ProfileActivity)
                        } else {
                            Geocoder(this@ProfileActivity, Locale.UK)
                        }
                        var addresses: MutableList<Address?>? = null
                        try {
                            addresses = ff.getFromLocation(lat_double, lon_double, 1)
                            val city = addresses[0]!!.adminArea
                            mcity.text = city
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun addT(string: String?) {
        flexboxLayout = findViewById(R.id.Grid_profile)
        params = GridLayout.LayoutParams()
        val textView = TextView(this)
        textView.text = string
        textView.setTextColor(ContextCompat.getColor(applicationContext, R.color.c2))
        textView.setPadding(13, 10, 13, 10)
        textView.setBackgroundResource(R.drawable.tag)
        params.setMargins(10, 10, 10, 10)
        textView.layoutParams = params
        flexboxLayout.addView(textView)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 123) {
            if (resultCode == 123) {
                finish()
                overridePendingTransition(0, 0)
                startActivity(intent)
                overridePendingTransition(0, 0)
            } else if (resultCode == 1233) {
                onBackPressed()
            }
        }
    }

}