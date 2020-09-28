package com.jabirdeveloper.tinderswipe

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.jabirdeveloper.tinderswipe.Functions.LoadingDialog
import com.jabirdeveloper.tinderswipe.Register.PhoneActivity
import com.jabirdeveloper.tinderswipe.Register.Regis_name_Activity
import com.jabirdeveloper.tinderswipe.Register.RegistrationActivity
import java.util.*

class ChooseLoginRegistrationActivity : AppCompatActivity() {
    private lateinit var mLogin: Button
    private lateinit var mRegister: Button
    private lateinit var mCallbackManager: CallbackManager
    private lateinit var firebaseAuthStateListener: AuthStateListener
    private lateinit var mAuth: FirebaseAuth
    private lateinit var googleSignInClientg: GoogleSignInClient
    private val RC_SIGN_IN = 0
    private lateinit var thai: TextView
    private lateinit var eng: TextView
    private lateinit var face: ImageView
    private lateinit var google: ImageView
    private lateinit var mPhone: ImageView
    private lateinit var dialog: Dialog
    private lateinit var dialog2: Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState)
        loadLocal()
        setContentView(R.layout.activity_choose_login_registration)
        findViewById<TextView>(R.id.clickToTest).setOnClickListener { findViewById<LinearLayout>(R.id.testlogin).visibility = View.VISIBLE; findViewById<TextView>(R.id.clickToTest).visibility = View.GONE }
        mAuth = FirebaseAuth.getInstance()
        thai = findViewById(R.id.thai_lang)
        eng = findViewById(R.id.eng_lang)
        mLogin = findViewById(R.id.button3)
        mRegister = findViewById(R.id.button4)
        mPhone = findViewById(R.id.button7)
        face = findViewById(R.id.face)
        google = findViewById(R.id.google)
        dialog = LoadingDialog(this).dialog()
        val preferences = getSharedPreferences("Settings", Context.MODE_PRIVATE)
        val language = preferences.getString("My_Lang", "")
        if (language == "th") {
            thai.setTextColor(ContextCompat.getColor(applicationContext, R.color.c4))
            eng.setTextColor(ContextCompat.getColor(applicationContext, R.color.c4tran))
        } else {
            thai.setTextColor(ContextCompat.getColor(applicationContext, R.color.c4tran))
            eng.setTextColor(ContextCompat.getColor(applicationContext, R.color.c4))
        }
        firebaseAuthStateListener = AuthStateListener {
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                val userDb = FirebaseDatabase.getInstance().reference
                userDb.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        when {
                            dataSnapshot.child("BlackList").hasChild(user.uid) -> {
                                dialog.dismiss()
                                mAuth.signOut()
                                val intent = Intent(this@ChooseLoginRegistrationActivity, BandUser::class.java)
                                startActivity(intent)
                            }
                            dataSnapshot.child("Users").child(user.uid).hasChild("sex") -> {
                                dialog.dismiss()
                                val intent = Intent(this@ChooseLoginRegistrationActivity, SwitchpageActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                intent.putExtra("first", "0")
                                startActivity(intent)
                                finish()
                                return
                            }
                            else -> {

                                dialog.dismiss()
                                val intent = Intent(this@ChooseLoginRegistrationActivity, Regis_name_Activity::class.java)
                                intent.putExtra("Type", "face")
                                startActivity(intent)
                                return

                            }
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {}
                })
            }
        }
        mCallbackManager = CallbackManager.Factory.create()
        face.setOnClickListener(View.OnClickListener {
            LoginManager.getInstance().logInWithReadPermissions(this@ChooseLoginRegistrationActivity, Arrays.asList("email"))
            LoginManager.getInstance().registerCallback(mCallbackManager, object : FacebookCallback<LoginResult?> {
                override fun onSuccess(loginResult: LoginResult?) {
                    handleFacebookToken(loginResult?.accessToken)
                }

                override fun onCancel() {}
                override fun onError(exception: FacebookException?) {
                    Snackbar.make(face, exception.toString(), Snackbar.LENGTH_SHORT).show();
                }
            })
        })
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        googleSignInClientg = GoogleSignIn.getClient(this, gso)
        google.setOnClickListener { signIn() }
        mLogin.setOnClickListener {
            val intent = Intent(this@ChooseLoginRegistrationActivity, LoginActivity::class.java)
            startActivity(intent)

        }
        mRegister.setOnClickListener {
            val intent = Intent(this@ChooseLoginRegistrationActivity, RegistrationActivity::class.java)
            startActivity(intent)
        }
        mPhone.setOnClickListener {
            val intent = Intent(this@ChooseLoginRegistrationActivity, PhoneActivity::class.java)
            startActivity(intent)
        }
        thai.setOnClickListener(View.OnClickListener {
            setLocal("th")
            finish()
            overridePendingTransition(0, 0)
            startActivity(intent)
            overridePendingTransition(0, 0)
        })
        eng.setOnClickListener(View.OnClickListener {
            setLocal("en")
            finish()
            overridePendingTransition(0, 0)
            startActivity(intent)
            overridePendingTransition(0, 0)
        })
    }

    private fun signIn() {
        val signInIntent = googleSignInClientg.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun handleFacebookToken(token: AccessToken?) {
        dialog.show()
        val credential = FacebookAuthProvider.getCredential(token!!.token)
        mAuth.signInWithCredential(credential).addOnCompleteListener(this) { task ->
            if (!task.isSuccessful) {
                Snackbar.make(face, "Please try again later", Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String?) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    dialog.show()
                    if (!task.isSuccessful) {
                        Snackbar.make(google, "Please try again later", Snackbar.LENGTH_SHORT).show();
                    }
                }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                Log.d("TAG", "firebaseAuthWithGoogle:" + account?.id)
                firebaseAuthWithGoogle(account?.idToken)
            } catch (e: ApiException) {
                Snackbar.make(google, "Google sign in failed", Snackbar.LENGTH_SHORT).show();
                Log.d("TAG", "Google sign in failed", e)
            }
        }
        if (requestCode == 1150) {
            mAuth.removeAuthStateListener(firebaseAuthStateListener)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onStart() {
        super.onStart()
        mAuth.addAuthStateListener(firebaseAuthStateListener)
    }

    override fun onStop() {
        super.onStop()
        mAuth.removeAuthStateListener(firebaseAuthStateListener)
    }

    fun setLocal(lang: String?) {
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

    fun loadLocal() {

        val preferences = getSharedPreferences("Settings", Context.MODE_PRIVATE)
        val langure = preferences.getString("My_Lang", "")
        Log.d("My2", langure)
        setLocal(langure)

    }
}