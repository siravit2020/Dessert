package com.jabirdeveloper.tinderswipe

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.formats.NativeAdOptions
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdCallback
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.jabirdeveloper.tinderswipe.LoginActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var mLogin: Button
    private lateinit var mEmail: EditText
    private lateinit var mPassword: EditText
    private lateinit var mAuth: FirebaseAuth
    private lateinit var firebaseAuthStateListener: AuthStateListener
    lateinit var mInterstitialAd: InterstitialAd
    private lateinit var rewardedAd: RewardedAd
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        /* mInterstitialAd = InterstitialAd(this)
         mInterstitialAd.adUnitId = "ca-app-pub-3940256099942544/1033173712"
         mInterstitialAd.loadAd(AdRequest.Builder().build())
         mInterstitialAd.show()*/
        rewardedAd = RewardedAd(this,
                "ca-app-pub-3940256099942544/5224354917")
        val adLoadCallback = object : RewardedAdLoadCallback() {
            override fun onRewardedAdLoaded() {
                Toast.makeText(this@LoginActivity, "สวย", Toast.LENGTH_SHORT).show()
            }

            override fun onRewardedAdFailedToLoad(errorCode: Int) {
                Toast.makeText(this@LoginActivity, errorCode.toString(), Toast.LENGTH_SHORT).show()
            }
        }
        val adLoader = AdLoader.Builder(this, "ca-app-pub-3940256099942544/2247696110")
                .forUnifiedNativeAd { ad: UnifiedNativeAd ->
                    // Show the ad.
                }
                .withAdListener(object : AdListener() {
                    override fun onAdFailedToLoad(errorCode: Int) {
                        // Handle the failure by logging, altering the UI, and so on.
                    }
                })
                .withNativeAdOptions(NativeAdOptions.Builder()
                        // Methods in the NativeAdOptions.Builder class can be
                        // used here to specify individual options settings.
                        .build())
                .build()
        rewardedAd.loadAd(AdRequest.Builder().build(), adLoadCallback)
        mAuth = FirebaseAuth.getInstance()
        mLogin = findViewById(R.id.BLogin)
        mEmail = findViewById(R.id.Edit_L_email)
        mPassword = findViewById(R.id.Edit_L_password)
        firebaseAuthStateListener = AuthStateListener {
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                val intent = Intent(this@LoginActivity, First_Activity::class.java)
                startActivity(intent)
                finish()
                return@AuthStateListener
            }
        }
        mLogin.setOnClickListener(View.OnClickListener {
            /* if (mInterstitialAd.isLoaded) {
                 mInterstitialAd.show()
             } else {
                 Log.d("TAG", "The interstitial wasn't loaded yet.")
             }*/
            if (rewardedAd.isLoaded) {
                val activityContext: Activity = this@LoginActivity
                val adCallback = object : RewardedAdCallback() {
                    override fun onRewardedAdOpened() {
                        rewardedAd = createAndLoadRewardedAd()
                    }

                    override fun onRewardedAdClosed() {

                    }

                    override fun onUserEarnedReward(@NonNull reward: RewardItem) {

                    }

                    override fun onRewardedAdFailedToShow(errorCode: Int) {
                        // Ad failed to display.
                    }
                }
                rewardedAd.show(activityContext, adCallback)
            } else {
                Log.d("TAG", "The rewarded ad wasn't loaded yet.")
            }
            val email = mEmail.getText().toString()
            val password = mPassword.getText().toString()
            if (email.trim { it <= ' ' } != "" && password.trim { it <= ' ' } != "") {
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this@LoginActivity) { task ->
                    if (!task.isSuccessful) {
                        Toast.makeText(this@LoginActivity, "สวย", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    fun createAndLoadRewardedAd(): RewardedAd {
        val rewardedAd = RewardedAd(this, "ca-app-pub-3940256099942544/5224354917")
        val adLoadCallback = object : RewardedAdLoadCallback() {
            override fun onRewardedAdLoaded() {
                // Ad successfully loaded.
            }

            override fun onRewardedAdFailedToLoad(errorCode: Int) {
                // Ad failed to load.
            }
        }
        rewardedAd.loadAd(AdRequest.Builder().build(), adLoadCallback)
        return rewardedAd
    }

    override fun onStart() {
        super.onStart()
        mAuth.addAuthStateListener(firebaseAuthStateListener)
    }

    override fun onStop() {
        super.onStop()
        mAuth.removeAuthStateListener(firebaseAuthStateListener)
    }
}