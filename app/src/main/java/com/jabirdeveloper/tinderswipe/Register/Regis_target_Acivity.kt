package com.jabirdeveloper.tinderswipe.Register

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.ImageDecoder
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
import com.google.firebase.storage.FirebaseStorage
import com.jabirdeveloper.tinderswipe.R
import com.jabirdeveloper.tinderswipe.SwitchpageActivity
import com.tapadoo.alerter.Alerter
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import java.io.ByteArrayOutputStream
import java.io.IOException

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "NAME_SHADOWING")
class Regis_target_Acivity : AppCompatActivity() {
    private var email: String? = null
    private var pass: String? = null
    private lateinit var name: String
    private lateinit var sex: String
    private var Age: Int = 18
    private lateinit var Type: String
    private var x: Double = 0.0
    private var y: Double = 0.0
    private lateinit var mAuth: FirebaseAuth
    private lateinit var imageView: ImageView
    private lateinit var UserId: String
    private lateinit var currentUserDb: DatabaseReference
    private var resultUri: Uri? = null
    private lateinit var dialog: Dialog
    private lateinit var toolbar: Toolbar
    private lateinit var add: ImageView
    private lateinit var skip: TextView
    private lateinit var b1: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_regis_target__acivity)
        mAuth = FirebaseAuth.getInstance()
        toolbar = findViewById(R.id.my_tools)
        skip = findViewById(R.id.skip)
        imageView = findViewById(R.id.ImageMain)
        add = findViewById(R.id.add1)
        b1 = findViewById(R.id.button6)
        mAuth = FirebaseAuth.getInstance()
        setSupportActionBar(toolbar)
        supportActionBar!!.setTitle(R.string.registered)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        x = intent.getDoubleExtra("X", x)
        y = intent.getDoubleExtra("Y", y)
        Toast.makeText(this, "$x , $y", Toast.LENGTH_SHORT).show()
        Type = intent.getStringExtra("Type")
        email = intent.getStringExtra("email")
        pass = intent.getStringExtra("password")
        name = intent.getStringExtra("Name")
        sex = intent.getStringExtra("Sex")
        Age = intent.getIntExtra("Age", Age)

        val inflater = layoutInflater
        val view = inflater.inflate(R.layout.progress_dialog, null)
        dialog = Dialog(this)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(view)
        val width = (resources.displayMetrics.widthPixels * 0.80).toInt()
        dialog.window!!.setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT)
        imageView.setOnClickListener(View.OnClickListener { InputImage() })
        b1.setOnClickListener(View.OnClickListener { createId(1) })
        skip.setOnClickListener(View.OnClickListener { createId(2) })
    }

    private fun createId(i: Int) {
        if (Type == "email") {
            mAuth.createUserWithEmailAndPassword(email!!, pass!!).addOnCompleteListener(this@Regis_target_Acivity) { task -> if (!task.isSuccessful) Snackbar.make(b1, R.string.try_again, Snackbar.LENGTH_LONG).show() else CreateData_to_Firebase(i) }
        } else {
            CreateData_to_Firebase(i)
        }
    }

    private fun InputImage() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(3, 4)
                .start(this)
    }

    private fun CreateData_to_Firebase(i: Int) {
        val editor = getSharedPreferences("notification_match", Context.MODE_PRIVATE).edit()
        editor.putString("noti", "1")
        editor.apply()
        UserId = mAuth.currentUser!!.uid
        currentUserDb = FirebaseDatabase.getInstance().reference.child("Users").child(UserId)
        val userInfo = hashMapOf(
                "name" to name,
                "sex" to sex,
                "Age" to Age,
                "Distance" to "Untitled",
                "OppositeUserSex" to "All",
                "OppositeUserAgeMin" to 18,
                "OppositeUserAgeMax" to 70,
                "MaxChat" to 20,
                "MaxLike" to 40,
                "MaxAdmob" to 10,
                "MaxStar" to 3,
                "Vip" to 0,
                "birth" to intent.getLongExtra("Birth", 0)
        )
        currentUserDb.updateChildren(userInfo as Map<String, Any>)
        val location = hashMapOf(
                "X" to x,
                "Y" to y
        )
        currentUserDb.child("Location").updateChildren(location as Map<String, Any>)
        if (i == 1) {
            if (resultUri != null) {
                dialog.show()
                var bitmap: Bitmap? = null
                val filepath = FirebaseStorage.getInstance().reference.child("profileImages").child(UserId).child("profileImageUrl0")
                try {
                    bitmap = if (Build.VERSION.SDK_INT >= 29) {
                        val source = ImageDecoder.createSource(this.contentResolver, resultUri!!)
                        ImageDecoder.decodeBitmap(source)
                    } else {
                        MediaStore.Images.Media.getBitmap(application.contentResolver, resultUri)
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                val baos = ByteArrayOutputStream()
                bitmap?.compress(Bitmap.CompressFormat.JPEG, 40, baos)
                val data = baos.toByteArray()
                val uploadTask = filepath.putBytes(data)
                uploadTask.addOnFailureListener { finish() }
                uploadTask.addOnSuccessListener {
                    val filepath = FirebaseStorage.getInstance().reference.child("profileImages").child(UserId).child("profileImageUrl0")
                    Log.d("TAG", "สำเร็จ")
                    filepath.downloadUrl.addOnSuccessListener { uri ->
                        dialog.dismiss()
                        val userInfo = hashMapOf(
                                "profileImageUrl0" to uri.toString()
                        )
                        currentUserDb.child("ProfileImage").updateChildren(userInfo as Map<String, Any>)
                        val intent = Intent(this@Regis_target_Acivity, SwitchpageActivity::class.java)
                        intent.putExtra("first", "0")
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }.addOnFailureListener(OnFailureListener {
                        Log.d("TAG", "สวยยยยย")
                        return@OnFailureListener
                    })
                }
            } else {
                Alerter.create(this@Regis_target_Acivity)
                        .setTitle(R.string.profile_image)
                        .setText(getString(R.string.choose_photo))
                        .setBackgroundColorRes(R.color.c2)
                        .show()
            }
        } else {
            dialog.dismiss()
            val intent = Intent(this@Regis_target_Acivity, SwitchpageActivity::class.java)
            intent.putExtra("first", "0")
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                resultUri = result.uri
                var image: FirebaseVisionImage? = null
                try {
                    image = FirebaseVisionImage.fromFilePath(this@Regis_target_Acivity, resultUri!!)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                dialog.show()
                val highAccuracyOpts = FirebaseVisionFaceDetectorOptions.Builder().build()
                val detector = FirebaseVision.getInstance().getVisionFaceDetector(highAccuracyOpts)
                val result2 = detector.detectInImage(image!!)
                        .addOnSuccessListener { faces ->
                            dialog.dismiss()
                            if (faces.isNotEmpty()) {
                                Glide.with(application).load(resultUri).placeholder(R.drawable.tran).into(imageView)
                                add.visibility = View.GONE
                            } else {
                                Snackbar.make(b1, "ไม่ใช่คน", Snackbar.LENGTH_LONG).show()
                            }
                        }
                        .addOnFailureListener {
                            // Task failed with an exception
                            // ...
                        }
            }
        }
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