package com.jabirdeveloper.tinderswipe

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.ImageDecoder
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.util.TypedValue
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayout
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
import com.google.firebase.storage.FirebaseStorage
import com.nipunru.nsfwdetector.NSFWDetector
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*
import kotlin.collections.HashMap

@Suppress("UNREACHABLE_CODE", "NAME_SHADOWING")
open class SettingActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var editname: CustomEdittext
    private lateinit var editcareer: CustomEdittext
    private lateinit var editstudy: CustomEdittext
    private lateinit var editmy: CustomEdittext
    private lateinit var image1: ImageView
    private lateinit var image2: ImageView
    private lateinit var image3: ImageView
    private lateinit var image4: ImageView
    private lateinit var image5: ImageView
    private lateinit var image6: ImageView
    private lateinit var add1: ImageView
    private lateinit var add2: ImageView
    private lateinit var add3: ImageView
    private lateinit var add4: ImageView
    private lateinit var add5: ImageView
    private lateinit var add6: ImageView
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mUserDatabase: DatabaseReference
    private lateinit var userId: String
    private lateinit var name: String
    private lateinit var profile: String
    private lateinit var Y: String
    private lateinit var resulturi: Uri
    private lateinit var religion: TextView
    private lateinit var language: TextView
    private lateinit var listItems: Array<String?>
    private lateinit var listItems2: Array<String?>
    private lateinit var listItems3: Array<String?>
    private lateinit var item2: String
    private lateinit var item: String
    private lateinit var toolbar: Toolbar
    private lateinit var params: GridLayout.LayoutParams
    private lateinit var flexLayout: FlexboxLayout
    private var selectedPosition = -1
    private lateinit var checkedItems: BooleanArray
    private lateinit var checkedItems2: BooleanArray
    private lateinit var checkedItems3: BooleanArray
    private lateinit var dialog: Dialog
    private var list: ArrayList<Int> = ArrayList()
    private var mUserItems: ArrayList<Int?> = ArrayList()
    private var mUserItems2: ArrayList<Int?> = ArrayList()
    private lateinit var map: MutableMap<*, *>
    var i = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadLocal()
        setContentView(R.layout.activity_setting)
        getUserinfo()
        editname = findViewById(R.id.name)
        editcareer = findViewById(R.id.career)
        editstudy = findViewById(R.id.study)
        editmy = findViewById(R.id.editText2)
        language = findViewById(R.id.language)
        religion = findViewById(R.id.religion)
        toolbar = findViewById(R.id.my_tools)
        setSupportActionBar(toolbar)
        supportActionBar!!.setTitle(R.string.Edit)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        val inflater = layoutInflater
        val view = inflater.inflate(R.layout.progress_dialog, null)
        dialog = Dialog(this)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(view)
        dialog.setCancelable(false)
        val width = (resources.displayMetrics.widthPixels * 0.80).toInt()
        dialog.window!!.setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT)
        add1 = findViewById(R.id.add1)
        add2 = findViewById(R.id.add2)
        add3 = findViewById(R.id.add3)
        add4 = findViewById(R.id.add4)
        add5 = findViewById(R.id.add5)
        add6 = findViewById(R.id.add6)
        image1 = findViewById(R.id.Image1)
        image2 = findViewById(R.id.Image2)
        image3 = findViewById(R.id.Image3)
        image4 = findViewById(R.id.Image4)
        image5 = findViewById(R.id.Image5)
        image6 = findViewById(R.id.Image6)
        val height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, image1.measuredWidth.toFloat(), resources.displayMetrics).toInt()
        Log.d("12", image1.width.toString())
        findViewById<View>(R.id.Image1).setOnClickListener(this)
        findViewById<View>(R.id.Image2).setOnClickListener(this)
        findViewById<View>(R.id.Image3).setOnClickListener(this)
        findViewById<View>(R.id.Image4).setOnClickListener(this)
        findViewById<View>(R.id.Image5).setOnClickListener(this)
        findViewById<View>(R.id.Image6).setOnClickListener(this)
        mAuth = FirebaseAuth.getInstance()
        userId = mAuth.currentUser!!.uid
        mUserDatabase = FirebaseDatabase.getInstance().reference.child("Users").child(userId)
        listItems = resources.getStringArray(R.array.shopping_item)
        checkedItems = BooleanArray(listItems.size)
        listItems2 = resources.getStringArray(R.array.pasa_item)
        checkedItems2 = BooleanArray(listItems2.size)
        listItems3 = resources.getStringArray(R.array.religion_item)
        checkedItems3 = BooleanArray(listItems3.size)
        flexLayout = findViewById(R.id.Grid)
        flexLayout.flexDirection = FlexDirection.ROW
        params = GridLayout.LayoutParams()
        language.setOnClickListener(View.OnClickListener { dialog(language, getString(R.string.language), listItems2, checkedItems2, mUserItems2) })
        religion.setOnClickListener(View.OnClickListener { dialog2(religion, getString(R.string.religion), listItems3) })
        editname.setOnEditTextImeBackListener(object : EditTextImeBackListener {
            override fun onImeBack(ctrl: CustomEdittext?, text: String?) {
                editname.clearFocus()
            }
        })
        editmy.setOnEditTextImeBackListener(object : EditTextImeBackListener {
            override fun onImeBack(ctrl: CustomEdittext?, text: String?) {
                editmy.clearFocus()
            }
        })
        editcareer.setOnEditTextImeBackListener(object : EditTextImeBackListener {
            override fun onImeBack(ctrl: CustomEdittext?, text: String?) {
                editcareer.clearFocus()
            }
        })
        editstudy.setOnEditTextImeBackListener(object : EditTextImeBackListener {
            override fun onImeBack(ctrl: CustomEdittext?, text: String?) {
                editstudy.clearFocus()
            }
        })
        flexLayout.setOnClickListener(View.OnClickListener {
            val mBuilder = AlertDialog.Builder(this@SettingActivity)
            mBuilder.setTitle(R.string.dialog_title)
            mBuilder.setMultiChoiceItems(listItems, checkedItems) { _, position, isChecked ->
                if (isChecked) {
                    mUserItems.add(position)
                } else {
                    mUserItems.remove(Integer.valueOf(position))
                }
            }
            mBuilder.setCancelable(true)
            mBuilder.setPositiveButton(R.string.ok) { _, _ ->
                flexLayout.removeAllViews()
                for (i in mUserItems.indices) {
                    addT(mUserItems[i]?.let { it1 -> listItems[it1] })
                }
                buttonAdd()
            }
            mBuilder.setNeutralButton(R.string.clear_all_label) { _, _ ->
                for (i in checkedItems.indices) {
                    checkedItems[i] = false
                    mUserItems.clear()
                    flexLayout.removeAllViews()
                }
                buttonAdd()
            }
            mBuilder.setNegativeButton(R.string.cancle) { _, _ -> }
            val mDialog = mBuilder.create()
            mDialog.window!!.setBackgroundDrawable(ContextCompat.getDrawable(this@SettingActivity, R.drawable.myrect2))
            mDialog.show()
        })
    }

    private fun dialog(textView: TextView?, TitleName: String?, order: Array<String?>?, checkedItem: BooleanArray?, mUserItem: ArrayList<Int?>?) {
        val mBuilder = AlertDialog.Builder(this@SettingActivity)
        mBuilder.setTitle(TitleName)
        mBuilder.setMultiChoiceItems(order, checkedItem) { _, position, isChecked ->
            if (isChecked) {
                mUserItem?.add(position)
            } else {
                mUserItem?.remove(Integer.valueOf(position))
            }
        }
        mBuilder.setCancelable(true)
        mBuilder.setPositiveButton(R.string.ok) { _, _ ->
            item = ""
            textView?.text = ""
            for (i in mUserItem!!.indices) {
                item += order!![mUserItem[i]!!]
                if (i != mUserItem.size - 1) {
                    item = "$item, "
                }
            }
            textView!!.text = item
        }
        mBuilder.setNegativeButton(R.string.cancle) { _, _ -> }
        val mDialog = mBuilder.create()
        mDialog.window!!.setBackgroundDrawable(ContextCompat.getDrawable(this@SettingActivity, R.drawable.myrect2))
        mDialog.show()
    }

    private fun dialog2(textView: TextView?, TitleName: String?, order: Array<String?>?) {
        val mBuilder = AlertDialog.Builder(this@SettingActivity)
        mBuilder.setTitle(TitleName)
        mBuilder.setSingleChoiceItems(order, selectedPosition) { _, which ->
            item2 = order!![which].toString()
            selectedPosition = which
        }
        mBuilder.setCancelable(true)
        mBuilder.setPositiveButton(R.string.ok) { _, _ -> textView!!.text = item2 }
        mBuilder.setNegativeButton(R.string.cancle) { _, _ -> }
        val mDialog = mBuilder.create()
        mDialog.window!!.setBackgroundDrawable(ContextCompat.getDrawable(this@SettingActivity, R.drawable.myrect2))
        mDialog.show()
    }

    private fun addT(string: String?) {
        flexLayout = findViewById(R.id.Grid)
        params = GridLayout.LayoutParams()
        val textView = TextView(this)
        textView.text = string
        textView.setTextColor(ContextCompat.getColor(applicationContext, R.color.c2))
        textView.setPadding(13, 10, 13, 10)
        textView.setBackgroundResource(R.drawable.tag)
        params.setMargins(10, 10, 10, 10)
        textView.layoutParams = params
        flexLayout.addView(textView)
    }

    private fun buttonAdd() {
        params.setMargins(10, 10, 10, 10)
        val textView = TextView(this@SettingActivity)
        textView.text = ""
        textView.setBackgroundResource(R.drawable.ic_add_circle_outline_black_24dp)
        textView.setTextColor(ContextCompat.getColor(applicationContext, R.color.c2))
        textView.setPadding(10, 10, 10, 10)
        params.setMargins(10, 10, 10, 10)
        textView.layoutParams = params
        flexLayout.addView(textView)
    }

    private fun DeleteImage(imageDelete: ImageView?, str: String?, add: ImageView?) {
        val mBuilder = AlertDialog.Builder(this@SettingActivity)
        mBuilder.setTitle(R.string.delete_image)
        mBuilder.setMessage(R.string.delete_image_confirm)
        mBuilder.setCancelable(true)
        mBuilder.setPositiveButton(R.string.ok) { _, _ ->
            val filepath = FirebaseStorage.getInstance().reference.child("profileImages").child(userId).child(str!!)
            filepath.delete().addOnSuccessListener {
                mUserDatabase.child("ProfileImage").child(str).removeValue()
                if (str == "profileImageUrl0") {
                    val MyUser = getSharedPreferences("MyUser", Context.MODE_PRIVATE).edit()
                    MyUser.putString("image", "")
                    MyUser.apply()
                }

                imageDelete?.setBackgroundResource(R.drawable.border3)
                imageDelete?.setImageResource(R.drawable.tran)
                add?.visibility = View.VISIBLE
            }
        }
        mBuilder.setNegativeButton(R.string.cancle) { _, _ -> }
        val mDialog = mBuilder.create()
        mDialog.window!!.setBackgroundDrawable(ContextCompat.getDrawable(this@SettingActivity, R.drawable.myrect2))
        mDialog.show()
    }

    private fun getUserinfo() {
        FirebaseDatabase.getInstance().reference.child("Users").child(Firebase.auth.uid!!).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.childrenCount > 0) {
                    map = dataSnapshot.value as MutableMap<*, *>
                    if (map.containsKey("myself")) {
                        editmy.setText(map.get("myself").toString())
                    }
                    if (map.containsKey("name")) {
                        name = map.get("name").toString()
                        editname.setText(name)
                    }
                    if (map.containsKey("career")) {
                        editcareer.setText(map.get("career").toString())
                    }
                    if (map.containsKey("study")) {
                        editstudy.setText(map.get("study").toString())
                    }
                    if (map.containsKey("language")) {
                        val size = dataSnapshot.child("language").childrenCount.toInt()
                        var str = ""
                        for (u in 0 until size) {
                            val position = dataSnapshot.child("language").child("language$u").value.toString().toInt()
                            str += listItems2[position]
                            checkedItems2[position] = true
                            mUserItems2.add(position)
                            if (u != size - 1) {
                                str = "$str, "
                            }
                        }
                        language.text = str
                    }
                    if (map.containsKey("religion")) {
                        selectedPosition = map.get("religion").toString().toInt()
                        religion.text = listItems3[selectedPosition]
                    }
                    if (map.containsKey("hobby")) {
                        val size = dataSnapshot.child("hobby").childrenCount.toInt()
                        val str = ""
                        for (u in 0 until size) {
                            val position = dataSnapshot.child("hobby").child("hobby$u").value.toString().toInt()
                            checkedItems[position] = true
                            mUserItems.add(position)
                            addT(listItems[position])
                        }
                        buttonAdd()
                    } else {
                        buttonAdd()
                    }
                    val map2 = dataSnapshot.child("ProfileImage").value as MutableMap<*, *>?
                    if (dataSnapshot.child("ProfileImage").exists()) {
                        if (map2!!.containsKey("profileImageUrl0")) {
                            profile = map2["profileImageUrl0"].toString()
                            add1.visibility = View.GONE
                            Glide.with(application).load(profile).placeholder(R.drawable.tran).into(image1)
                        } else {
                            list.add(1)
                            Toast.makeText(this@SettingActivity, R.string.primary_profile_alert, Toast.LENGTH_LONG).show()
                        }
                        if (map2.containsKey("profileImageUrl1")) {
                            profile = map2.get("profileImageUrl1").toString()
                            add2.visibility = View.GONE
                            Glide.with(application).load(profile).placeholder(R.drawable.tran).into(image2)
                        } else list.add(2)
                        if (map2.containsKey("profileImageUrl2")) {
                            profile = map2.get("profileImageUrl2").toString()
                            add3.visibility = View.GONE
                            Glide.with(application).load(profile).placeholder(R.drawable.tran).into(image3)
                        } else list.add(3)
                        if (map2.containsKey("profileImageUrl3")) {
                            profile = map2.get("profileImageUrl3").toString()
                            add4.visibility = View.GONE
                            Glide.with(application).load(profile).placeholder(R.drawable.tran).into(image4)
                        } else list.add(4)
                        if (map2.containsKey("profileImageUrl4")) {
                            profile = map2.get("profileImageUrl4").toString()
                            add5.visibility = View.GONE
                            Glide.with(application).load(profile).placeholder(R.drawable.tran).into(image5)
                        } else list.add(5)
                        if (map2.containsKey("profileImageUrl5")) {
                            profile = map2.get("profileImageUrl5").toString()
                            add6.visibility = View.GONE
                            Glide.with(application).load(profile).placeholder(R.drawable.tran).into(image6)
                        } else list.add(6)
                    } else {
                        Snackbar.make(image1, R.string.primary_profile_alert, Snackbar.LENGTH_LONG).show()
                    }
                    if (intent.hasExtra("setImage")) {
                        var h = 1
                        if (list.size > 0) {
                            h = list.minOrNull()!!
                        }
                        if (h == 1) {
                            setImage(1, image1, add1)
                        }
                        if (h == 2) {
                            setImage(2, image2, add2)
                        }
                        if (h == 3) {
                            setImage(3, image3, add3)
                        }
                        if (h == 4) {
                            setImage(4, image4, add4)
                        }
                        if (h == 5) {
                            setImage(5, image5, add5)
                        }
                        if (h == 6) {
                            setImage(6, image6, add6)
                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun saveUserinFormation() {
        val userInfo = hashMapOf<String, Any?>()
        val myUser = getSharedPreferences("MyUser", Context.MODE_PRIVATE).edit()
        if (editmy.text.toString().trim { it <= ' ' }.isNotEmpty())
            userInfo["myself"] = editmy.text.toString()
        else userInfo["myself"] = null

        if (editname.text.toString().trim { it <= ' ' }.isNotEmpty()) {
            myUser.putString("name", editname.text.toString())
            userInfo["name"] = editname.text.toString()
        } else userInfo["name"] = null

        if (editcareer.text.toString().trim { it <= ' ' }.isNotEmpty())
            userInfo["career"] = editcareer.text.toString()
        else userInfo["career"] = null

        if (editstudy.text.toString().trim { it <= ' ' }.isNotEmpty()) {
            userInfo["study"] = editstudy.text.toString()
        } else userInfo["study"] = null

        if (selectedPosition != -1) userInfo["religion"] = selectedPosition
        mUserDatabase.updateChildren(userInfo)

        val userInfo12 = hashMapOf<String, Any?>()
        for (u in mUserItems2.indices)
            userInfo12["language$u"] = mUserItems2[u]!!.toInt()
        mUserDatabase.child("language").setValue(userInfo12)

        val userInfo2: MutableMap<String, Any> = HashMap()
        for (u in mUserItems.indices) userInfo2["hobby$u"] = mUserItems[u]!!.toInt()
        mUserDatabase.child("hobby").setValue(userInfo2)
        myUser.apply()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                resulturi = result.uri
                when (i) {
                    1 -> image1.setBackgroundColor(Color.WHITE)
                    2 -> image2.setBackgroundColor(Color.WHITE)
                    3 -> image3.setBackgroundColor(Color.WHITE)
                    4 -> image4.setBackgroundColor(Color.WHITE)
                    5 -> image5.setBackgroundColor(Color.WHITE)
                    6 -> image6.setBackgroundColor(Color.WHITE)
                }
                dialog.show()
                save(resulturi)
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
            }
        }
        if (requestCode == 258) {
            Log.d("258", "1")
            val h = Collections.min(list)
            val handler = Handler()
            handler.postDelayed({
                if (h == 1) {
                    setImage(1, image1, add1)
                }
                if (h == 2) {
                    setImage(2, image2, add2)
                }
                if (h == 3) {
                    setImage(3, image3, add3)
                }
                if (h == 4) {
                    setImage(4, image4, add4)
                }
                if (h == 5) {
                    setImage(5, image5, add5)
                }
                if (h == 6) {
                    setImage(6, image6, add6)
                }
            }, 400)
        }
    }

    private fun inputImage() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(3, 4)
                .start(this)
    }

    private fun save(resulturi: Uri?) {

        Y = (i - 1).toString()
        var bitmap: Bitmap? = null
        val filepath = FirebaseStorage.getInstance().reference.child("profileImages").child(userId).child("profileImageUrl$Y")
        try {
            Log.d("123", Build.VERSION.SDK_INT.toString())
            bitmap = if (Build.VERSION.SDK_INT >= 29) {
                val source = ImageDecoder.createSource(this.contentResolver, resulturi!!)
                ImageDecoder.decodeBitmap(source)
            } else {
                MediaStore.Images.Media.getBitmap(application.contentResolver, resulturi)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val image = FirebaseVisionImage.fromBitmap(bitmap!!)
        val highAccuracyOpts = FirebaseVisionFaceDetectorOptions.Builder().build()
        val detector = FirebaseVision.getInstance().getVisionFaceDetector(highAccuracyOpts)
        val finalBitmap = bitmap
        detector.detectInImage(image)
                .addOnSuccessListener { faces ->
                    if (faces.isNotEmpty()) {
                        NSFWDetector.isNSFW(bitmap) { isNSFW, confidence, image ->
                            if (isNSFW) {
                                Toast.makeText(this, "โรคจิต", Toast.LENGTH_SHORT).show()
                                backgroundAdd()
                                dialog.dismiss()
                            } else {
                                val baos = ByteArrayOutputStream()
                                finalBitmap.compress(Bitmap.CompressFormat.JPEG, 40, baos)
                                val data = baos.toByteArray()

                                val uploadTask = filepath.putBytes(data)
                                uploadTask.addOnFailureListener { finish() }
                                uploadTask.addOnSuccessListener {
                                    val filepath = FirebaseStorage.getInstance().reference.child("profileImages").child(userId).child("profileImageUrl$Y")
                                    filepath.downloadUrl.addOnSuccessListener { uri ->
                                        val userInfo = hashMapOf<String, Any>()
                                        userInfo["profileImageUrl$Y"] = uri.toString()
                                        mUserDatabase.child("ProfileImage").updateChildren(userInfo)
                                        when (i) {
                                            1 -> {
                                                Glide.with(application).load(resulturi).into(image1)
                                                val MyUser = getSharedPreferences("MyUser", Context.MODE_PRIVATE).edit()
                                                MyUser.putString("image", resulturi.toString())
                                                MyUser.apply()
                                                add1.visibility = View.GONE
                                            }
                                            2 -> {
                                                Glide.with(application).load(resulturi).into(image2)
                                                add2.visibility = View.GONE
                                            }
                                            3 -> {
                                                Glide.with(application).load(resulturi).into(image3)
                                                add3.visibility = View.GONE
                                            }
                                            4 -> {
                                                Glide.with(application).load(resulturi).into(image4)
                                                add4.visibility = View.GONE
                                            }
                                            5 -> {
                                                Glide.with(application).load(resulturi).into(image5)
                                                add5.visibility = View.GONE
                                            }
                                            6 -> {
                                                Glide.with(application).load(resulturi).into(image6)
                                                add6.visibility = View.GONE
                                            }
                                        }
                                        dialog.dismiss()
                                        i++
                                    }.addOnFailureListener(OnFailureListener {
                                        Log.d("TAG", "สวยยยยย")
                                        val intent = Intent(this@SettingActivity, MainActivity::class.java)
                                        startActivityForResult(intent, 1)
                                        finish()
                                        return@OnFailureListener
                                    })
                                }
                            }
                        }

                    } else {
                        backgroundAdd()
                        Toast.makeText(this@SettingActivity, "ไม่ใช่หน้าอีดอก", Toast.LENGTH_LONG).show()
                    }
                }
                .addOnFailureListener { }

    }

    private fun backgroundAdd() {
        dialog.dismiss()
        var imageDelete: ImageView? = null
        var add: ImageView? = null
        when (i) {
            1 -> {
                imageDelete = image1
                add = add1
            }
            2 -> {
                imageDelete = image2
                add = add2
            }
            3 -> {
                imageDelete = image3
                add = add3
            }
            4 -> {
                imageDelete = image4
                add = add4
            }
            5 -> {
                imageDelete = image5
                add = add5
            }
            6 -> {
                imageDelete = image6
                add = add6
            }
        }
        imageDelete?.setBackgroundResource(R.drawable.border3)
        imageDelete?.setImageResource(R.drawable.tran)
        add?.visibility = View.VISIBLE
    }

    override fun onStop() {
        super.onStop()
        saveUserinFormation()
    }

    override fun onBackPressed() {
        if (this::map.isInitialized) {
            saveUserinFormation()
        }
        val drawable = image1.drawable
        val hasImage = drawable != null
        if (hasImage && drawable is BitmapDrawable) {
            setResult(123)
            super.onBackPressed()
        } else {
            val mBuilder = AlertDialog.Builder(this@SettingActivity)
            mBuilder.setTitle(R.string.profile_image)
            mBuilder.setMessage(R.string.primary_profile_alert)
            mBuilder.setCancelable(true)
            mBuilder.setPositiveButton(R.string.ok) { _, _ ->
                setResult(1233)
                super@SettingActivity.onBackPressed()
            }
            mBuilder.setNegativeButton(R.string.cancle) { _, _ -> }
            val mDialog = mBuilder.create()
            mDialog.window!!.setBackgroundDrawable(ContextCompat.getDrawable(this@SettingActivity, R.drawable.myrect2))
            mDialog.show()
        }
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
        Log.d("My", lang)
    }

    private fun loadLocal() {
        val preferences = getSharedPreferences("Settings", Context.MODE_PRIVATE)
        val langure = preferences.getString("My_Lang", "")
        Log.d("My2", langure)
        setLocal(langure)
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

    override fun onClick(v: View?) {
        if (v?.id == R.id.Image1) {
            setImage(1, image1, add1)
        }
        if (v?.id == R.id.Image2) {
            setImage(2, image2, add2)
        }
        if (v?.id == R.id.Image3) {
            setImage(3, image3, add3)
        }
        if (v?.id == R.id.Image4) {
            setImage(4, image4, add4)
        }
        if (v?.id == R.id.Image5) {
            setImage(5, image5, add5)
        }
        if (v?.id == R.id.Image6) {
            setImage(6, image6, add6)
        }
    }

    fun setImage(u: Int, image: ImageView?, add: ImageView?) {
        i = u
        val drawable = image?.drawable
        val hasImage = drawable != null
        if (hasImage && drawable is BitmapDrawable) {
            DeleteImage(image, "profileImageUrl" + (u - 1), add)
        } else inputImage()
    }
}