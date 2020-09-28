package com.jabirdeveloper.tinderswipe

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import hearsilent.discreteslider.DiscreteSlider
import hearsilent.discreteslider.DiscreteSlider.OnValueChangedListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.HashMap

@Suppress("UNCHECKED_CAST", "NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class Setting2Activity : AppCompatActivity() {
    private lateinit var radioGroup: RadioGroup
    private lateinit var toolbar: Toolbar
    private lateinit var textView: TextView
    private lateinit var button: Button
    private lateinit var delete: LinearLayout
    private lateinit var mAuth: FirebaseAuth
    private lateinit var Getdistance: DatabaseReference
    private lateinit var text_seek2: TextView
    private lateinit var text_seekAge: TextView
    private lateinit var change: TextView
    private lateinit var seekBar_2: DiscreteSlider
    private lateinit var distance_user: String
    private lateinit var currentUid: String
    private lateinit var age_min: String
    private lateinit var age_max: String
    private lateinit var gender: String
    private var maxV = 0
    private var minV = 0
    private lateinit var logout: TextView
    private lateinit var mSlider: DiscreteSlider
    private var selectedPosition = -1
    private var check_item = -1
    private lateinit var noti_1: Switch
    private lateinit var online: Switch
    private lateinit var on_off_card: Switch
    private lateinit var on_off_list: Switch
    private var noti_match: String? = null
    private var on_off: String? = null
    private val order: Array<String?>? = arrayOf("ภาษาไทย", "English")
    private lateinit var map: HashMap<String, Any>
    private var valCh = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CoroutineScope(Job()).launch(Dispatchers.IO) {
            getdisAge()
        }
        setContentView(R.layout.activity_setting2)
        change = findViewById(R.id.change_Language)
        loadLocal()
        mAuth = FirebaseAuth.getInstance()
        radioGroup = findViewById(R.id.radioGroup)
        text_seek2 = findViewById(R.id.text_seek2)
        seekBar_2 = findViewById(R.id.seekBar_2)
        toolbar = findViewById(R.id.my_tools)
        delete = findViewById(R.id.delete)
        logout = findViewById(R.id.bl)
        mSlider = findViewById(R.id.discreteSlider)
        noti_1 = findViewById(R.id.switch1)
        online = findViewById(R.id.switch2)
        on_off_card = findViewById(R.id.on_off_card)
        on_off_list = findViewById(R.id.on_off_list)
        setSupportActionBar(toolbar)
        supportActionBar!!.setTitle(R.string.Setting)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        currentUid = mAuth.uid.toString()
        text_seekAge = findViewById(R.id.seek_age_text)
        change.setOnClickListener(View.OnClickListener {
            val mBuilder = AlertDialog.Builder(this@Setting2Activity)
            mBuilder.setTitle(R.string.language)
            mBuilder.setSingleChoiceItems(order, check_item) { _, which -> //item2 = order[which];
                selectedPosition = which
            }
            mBuilder.setCancelable(true)
            mBuilder.setPositiveButton(R.string.ok) { _, _ ->
                if (selectedPosition == 0) setLocal("th") else setLocal("en")
                save()
                finish()
                overridePendingTransition(0, 0)
                startActivity(Intent(this@Setting2Activity, SwitchpageActivity::class.java))
                overridePendingTransition(0, 0)

            }
            mBuilder.setNegativeButton(R.string.cancle) { dialog, _ -> dialog.dismiss() }
            val mDialog = mBuilder.create()
            mDialog.window!!.setBackgroundDrawable(ContextCompat.getDrawable(this@Setting2Activity, R.drawable.myrect2))
            mDialog.show()
        })
        mSlider.setValueChangedImmediately(true) // Default is false
        mSlider.setOnValueChangedListener(object : OnValueChangedListener() {
            override fun onValueChanged(progress: Int, fromUser: Boolean) {
                super.onValueChanged(progress, fromUser)
            }

            @SuppressLint("SetTextI18n")
            override fun onValueChanged(minProgress: Int, maxProgress: Int, fromUser: Boolean) {
                minV = minProgress + 18
                maxV = maxProgress + 18

                text_seekAge.text = "$minV - $maxV"
                if (maxV == 70) text_seekAge.text = "$minV - $maxV+" else text_seekAge.text = "$minV - $maxV"
            }
        })
        radioGroup.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radioButton -> gender = "Male"
                R.id.radioButton2 -> gender = "Female"
                R.id.radioButton3 -> gender = "All"
            }

        })
        seekBar_2.setValueChangedImmediately(true)
        seekBar_2.setOnValueChangedListener(object : OnValueChangedListener() {
            @SuppressLint("SetTextI18n")
            override fun onValueChanged(progress: Int, fromUser: Boolean) {
                super.onValueChanged(progress, fromUser)
                val value = progress.toString()

                if (progress == 190) text_seek2.text = "$value km+" else text_seek2.text = "$value km"
            }

            override fun onValueChanged(minProgress: Int, maxProgress: Int, fromUser: Boolean) {}
        })
        logout.setOnClickListener(View.OnClickListener {
            val userDb = Firebase.database.reference.child("Users").child(FirebaseAuth.getInstance().uid.toString())
            val status_up2 = HashMap<String?, Any?>()
            status_up2["date"] = ServerValue.TIMESTAMP
            status_up2["status"] = 0
            userDb.updateChildren(status_up2)
            mAuth.signOut()
            val intent = Intent(applicationContext, ChooseLoginRegistrationActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
            return@OnClickListener
        })
        delete.setOnClickListener(View.OnClickListener {
            val mBuilder = AlertDialog.Builder(this@Setting2Activity)
            mBuilder.setTitle(R.string.Close_account)
            mBuilder.setMessage(R.string.Close_account_confirm)
            mBuilder.setCancelable(true)
            mBuilder.setPositiveButton(R.string.ok) { _, _ -> delete() }
            mBuilder.setNegativeButton(R.string.cancle) { _, _ -> }
            val mDialog = mBuilder.create()
            mDialog.window!!.setBackgroundDrawable(ContextCompat.getDrawable(this@Setting2Activity, R.drawable.myrect2))
            mDialog.show()
        })
        on_off_card.setOnClickListener(View.OnClickListener {
            if (!on_off_card.isChecked) {
                val mBuilder = AlertDialog.Builder(this@Setting2Activity)
                mBuilder.setTitle(R.string.Vision_closed)
                mBuilder.setMessage(R.string.Vision_closed_match)
                mBuilder.setCancelable(true)
                mBuilder.setOnCancelListener { on_off_card.isChecked = true }
                mBuilder.setPositiveButton(R.string.ok) { _, _ -> on_off_card.isChecked = false; }
                mBuilder.setNegativeButton("ยกเลิก") { _, _ -> on_off_card.isChecked = true; }
                val mDialog = mBuilder.create()
                mDialog.window!!.setBackgroundDrawable(ContextCompat.getDrawable(this@Setting2Activity, R.drawable.myrect2))
                mDialog.show()
            }
        })
        on_off_list.setOnClickListener(View.OnClickListener {
            if (!on_off_list.isChecked) {
                val mBuilder = AlertDialog.Builder(this@Setting2Activity)
                mBuilder.setTitle(R.string.Vision_closed)
                mBuilder.setMessage(R.string.Vision_closed_nearby)
                mBuilder.setCancelable(true)
                mBuilder.setOnCancelListener { on_off_list.isChecked = true }
                mBuilder.setPositiveButton(R.string.ok) { _, _ -> on_off_list.isChecked = false; }
                mBuilder.setNegativeButton(R.string.cancle) { _, _ -> on_off_list.isChecked = true; }
                val mDialog = mBuilder.create()
                mDialog.window!!.setBackgroundDrawable(ContextCompat.getDrawable(this@Setting2Activity, R.drawable.myrect2))
                mDialog.show()
            }
        })
    }

    private fun delete() {

        val filepath = FirebaseStorage.getInstance().reference.child("profileImages").child(currentUid)
        filepath.delete()
        val userDb = FirebaseDatabase.getInstance().reference.child("Users").child(currentUid)
        userDb.removeValue()
        val userAllDb = FirebaseDatabase.getInstance().reference.child("Users")
        userAllDb.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                if (dataSnapshot.child("connection").child("yep").hasChild(currentUid)) {
                    FirebaseDatabase.getInstance().reference
                            .child("Users")
                            .child(dataSnapshot.key.toString())
                            .child("connection")
                            .child("yep")
                            .child(currentUid)
                            .removeValue()
                }
                if (dataSnapshot.child("connection").child("matches").hasChild(currentUid)) {
                    FirebaseDatabase.getInstance().reference
                            .child("Users")
                            .child(dataSnapshot.key.toString())
                            .child("connection")
                            .child("matches")
                            .child(currentUid)
                            .removeValue()
                }
                if (dataSnapshot.child("connection").child("chatna").hasChild(currentUid)) {
                    FirebaseDatabase.getInstance().reference
                            .child("Users")
                            .child(dataSnapshot.key.toString())
                            .child("connection")
                            .child("chatna")
                            .child(currentUid)
                            .removeValue()
                    Toast.makeText(applicationContext, currentUid, Toast.LENGTH_SHORT).show()
                }
                if (dataSnapshot.child("connection").child("nope").hasChild(currentUid)) {
                    FirebaseDatabase.getInstance().reference
                            .child("Users")
                            .child(dataSnapshot.key.toString())
                            .child("connection")
                            .child("nope")
                            .child(currentUid)
                            .removeValue()
                }
                if (dataSnapshot.child("see_profile").hasChild(currentUid)) {
                    FirebaseDatabase.getInstance().reference
                            .child("Users")
                            .child(dataSnapshot.key.toString())
                            .child("see_profile")
                            .child(currentUid)
                            .removeValue()
                }
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onCancelled(databaseError: DatabaseError) {}
        }).let {
            val intent = Intent(this@Setting2Activity, ChooseLoginRegistrationActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            mAuth.signOut()
        }


    }


    private fun getdisAge() {

        FirebaseDatabase.getInstance().reference.child("Users").child(FirebaseAuth.getInstance().uid!!).addListenerForSingleValueEvent(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                map = HashMap<String, Any>()
                if (dataSnapshot.hasChild("Distance")) {
                    when {
                        dataSnapshot.child("Distance").value.toString() == "true" -> {
                            seekBar_2.progress = 1
                        }
                        dataSnapshot.child("Distance").value.toString() == "Untitled" -> {
                            seekBar_2.progress = 190
                        }
                        else -> {
                            distance_user = dataSnapshot.child("Distance").value.toString()
                            val distance1 = distance_user.toInt()
                            seekBar_2.progress = distance1 - 1
                            text_seek2.text = "$distance1 km"
                        }
                    }
                    map["Distance"] = seekBar_2.progress
                }
                if (dataSnapshot.hasChild("OppositeUserAgeMin")) {
                    age_min = dataSnapshot.child("OppositeUserAgeMin").value.toString()
                    age_max = dataSnapshot.child("OppositeUserAgeMax").value.toString()
                    minV = age_min.toInt()
                    maxV = age_max.toInt()
                    map["OppositeUserAgeMin"] = minV
                    map["OppositeUserAgeMax"] = maxV
                    mSlider.minProgress = minV - 18
                    mSlider.maxProgress = maxV - 18
                    if (maxV == 70) text_seekAge.text = "$minV - $maxV+" else text_seekAge.text = "$age_min - $age_max"
                }
                if (dataSnapshot.hasChild("OppositeUserSex")) {
                    when (dataSnapshot.child("OppositeUserSex").value.toString()) {
                        "Male" -> {
                            radioGroup.check(R.id.radioButton);map["gender"] = "Male"
                        }
                        "Female" -> {
                            radioGroup.check(R.id.radioButton2);map["gender"] = "Female"
                        }
                        "All" -> {
                            radioGroup.check(R.id.radioButton3);map["gender"] = "All"
                        }
                    }

                }
                if (!dataSnapshot.hasChild("off_status")) {
                    map["on_off"] = true
                    on_off = "1"
                    online.isChecked = true
                } else {
                    map["on_off"] = false
                    on_off = null
                    online.isChecked = false
                }
                if (!dataSnapshot.hasChild("off_card")) {
                    map["off_card"] = true
                    on_off_card.isChecked = true
                }
                if (!dataSnapshot.hasChild("off_list")) {
                    map["off_list"] = true
                    on_off_list.isChecked = true
                }
                val preferences2 = getSharedPreferences("notification_match", Context.MODE_PRIVATE)
                noti_match = preferences2.getString("noti", "1")
                noti_1.isChecked = noti_match == "1"
                map["noti"] = noti_1.isChecked
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun setLocal(lang: String?) {
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val configuration = Configuration()
        resources.configuration.setLocale(locale)
        baseContext.resources.updateConfiguration(configuration, baseContext.resources.displayMetrics)
        val editor = getSharedPreferences("Settings", Context.MODE_PRIVATE).edit()
        editor.putString("My_Lang", lang)
        editor.apply()
    }

    private fun loadLocal() {
        val preferences = getSharedPreferences("Settings", Context.MODE_PRIVATE)
        val langure = preferences.getString("My_Lang", "")
        if (langure == "th") {
            check_item = 0
            change.text = order!![check_item]
        } else {
            check_item = 1
            change.text = order!![check_item]
        }
        setLocal(langure)
    }

    /*override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.correct,menu)
        return true
    }*/

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun save() {
        var value = ""
        val progress = seekBar_2.progress + 1
        if (progress in 0..189) {
            value = progress.toString()
        } else if (progress == 190) {
            value = "Untitled"
        }
        noti_match = if (noti_1.isChecked) {
            "1"
        } else "0"
        on_off = if (!online.isChecked) {
            "1"
        } else null
        val card: String? = if (!on_off_card.isChecked) {
            "1"
        } else null

        val list: String? = if (!on_off_list.isChecked) {
            "1"
        } else null
        val currentUserDb = FirebaseDatabase.getInstance().reference.child("Users").child(currentUid)
        val myUser = getSharedPreferences("MyUser", Context.MODE_PRIVATE).edit()
        val userInfo = hashMapOf(
                "Distance" to value,
                "off_status" to on_off,
                "OppositeUserSex" to gender,
                "OppositeUserAgeMin" to minV,
                "OppositeUserAgeMax" to maxV,
                "off_card" to card,
                "off_list" to list
        )
        myUser.putInt("OppositeUserAgeMin", minV)
        myUser.putInt("OppositeUserAgeMax", maxV)
        myUser.putString("OppositeUserSex", gender)
        myUser.putString("Distance", value)
        currentUserDb.updateChildren(userInfo as Map<String, Any>)
        val editor = getSharedPreferences("notification_match", Context.MODE_PRIVATE).edit()
        editor.putString("noti", noti_match)
        editor.apply()
        Handler().postDelayed({
            finish()
            overridePendingTransition(0, 0)
            startActivity(Intent(this@Setting2Activity, SwitchpageActivity::class.java))
            overridePendingTransition(0, 0)
        }, 100)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val user = FirebaseAuth.getInstance().currentUser
        if (map["Distance"] != seekBar_2.progress) valCh = true
        if (map["noti"] != noti_1.isChecked) valCh = true
        if (map["on_off"] != online.isChecked) valCh = true
        if (map["off_card"] != on_off_card.isChecked) valCh = true
        if (map["off_list"] != on_off_list.isChecked) valCh = true
        if (minV != map["OppositeUserAgeMin"]) valCh = true
        if (maxV != map["OppositeUserAgeMax"]) valCh = true
        if (gender != map["gender"]) valCh = true
        if (user != null && valCh) {
            save()
        }


    }
}