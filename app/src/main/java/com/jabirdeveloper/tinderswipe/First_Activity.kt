package com.jabirdeveloper.tinderswipe

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.database.*
import com.google.firebase.functions.HttpsCallableResult
import com.google.firebase.functions.ktx.functions
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.ktx.Firebase
import com.hanks.htextview.base.AnimationListener
import com.hanks.htextview.base.HTextView
import com.hanks.htextview.line.LineTextView
import com.jaredrummler.android.widget.AnimatedSvgView
import java.util.*

class First_Activity : AppCompatActivity() {
    private var firebaseAuthStateListener: AuthStateListener? = null
    private var mAuth: FirebaseAuth? = null
    private var usersDb: DatabaseReference? = null
    private var first = true
    private val plus: SwitchpageActivity? = SwitchpageActivity()
    private var hTextView: LineTextView? = null
    private var mContext: Context? = null
    private var functions = Firebase.functions
    private var mLocationManager: LocationManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first_)
        MobileAds.initialize(this) {}
        mContext = applicationContext
        mAuth = FirebaseAuth.getInstance()
        firebaseAuthStateListener = AuthStateListener {
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                val svgView: AnimatedSvgView = findViewById(R.id.animated_svg_view)
                svgView.start()
                usersDb = FirebaseDatabase.getInstance().reference.child("Users")
                usersDb!!.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.child(mAuth!!.currentUser!!.uid).child("sex").exists()) {
                            checkReport()
                            pushToken()
                            //getUnreadFunction()
                        } else {
                            mAuth!!.signOut()
                            val intent = Intent(this@First_Activity, ChooseLoginRegistrationActivity::class.java)
                            startActivity(intent)
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {}
                })
            } else {
                val intent = Intent(this@First_Activity, ChooseLoginRegistrationActivity::class.java)
                startActivity(intent)
                finish()
                return@AuthStateListener
            }
        }
        mLocationManager = this@First_Activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!mLocationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showGPSDisabledDialog()
        } else if (ActivityCompat.checkSelfPermission(this@First_Activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this@First_Activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this@First_Activity, arrayOf<String?>(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.INTERNET
            ), 1)
        } else mAuth!!.addAuthStateListener(firebaseAuthStateListener!!)
        hTextView = findViewById(R.id.textview)
        hTextView!!.setAnimationListener(SimpleAnimationListener(this@First_Activity))
        hTextView!!.animateText("Welcome to my world")
    }



    private fun pushToken() {
        FirebaseInstanceId.getInstance().instanceId
                .addOnCompleteListener(OnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        return@OnCompleteListener
                    }
                    val token = task.result?.token
                    FirebaseDatabase.getInstance().reference.child("Users").child(mAuth!!.currentUser!!.uid).child("token").setValue(token)
                })
    }

    fun getUnreadFunction(): Task<HttpsCallableResult> {
        val data = hashMapOf(
                "uid" to "test"
        )
        return functions
                .getHttpsCallable("getUnreadChat")
                .call(data)
                .addOnSuccessListener { task ->
                    val data = task.data as Map<*, *>
                    Log.d("testGetUnreadFunction", data.toString())
                }
                .addOnFailureListener {
                    Log.d("testGetUnreadFunction", "error")
                }
    }
    private fun showGPSDisabledDialog() {
        val builder = AlertDialog.Builder(this@First_Activity)
        builder.setTitle(R.string.GPS_Disabled)
        builder.setMessage(R.string.GPS_open)
        builder.setPositiveButton(R.string.open_gps) { _, _ -> startActivityForResult(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0) }.setNegativeButton(R.string.report_close) { dialog, which ->
            val intent = Intent(this@First_Activity, ShowGpsOpen::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            finish()
            startActivity(intent)
        }
        val mGPSDialog: Dialog = builder.create()
        mGPSDialog.window!!.setBackgroundDrawable(ContextCompat.getDrawable(this@First_Activity, R.drawable.myrect2))
        mGPSDialog.show()
    }
    private var countNumberChat: Int? = 0
    private var chk = 0
    private var chktotalhavechat = 0
    private var chkcountchat = 0
    private var chk_node = 0
    private var start = 0
    /*private fun getUserMarchId() {
        val matchDb = FirebaseDatabase.getInstance().reference.child("Users").child(mAuth!!.currentUser!!.uid).child("connection").child("matches")
        matchDb.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                ++chk_node
                ++start
                val chatID = dataSnapshot.child("ChatId").value.toString()
                val matchIDStored = getSharedPreferences(mAuth!!.currentUser!!.uid + "Match_first", Context.MODE_PRIVATE)
                val editorMatch = matchIDStored.edit()
                editorMatch.putInt(dataSnapshot.key, start)
                editorMatch.apply()
                Log.d("count_unread",dataSnapshot.key);
                getMatchUnread(dataSnapshot.key, chatID)
            }
            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                --chk_node
                val matchIDStored = getSharedPreferences("Match_First", Context.MODE_PRIVATE)
                val editorMatch = matchIDStored.edit()
                editorMatch.putInt(mAuth!!.currentUser!!.uid, --start)
                editorMatch.apply()
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }*/

    /*private var bkk = true
    private fun getMatchUnread(MatchId: String?, ChatId: String?) {
        val chatDB = FirebaseDatabase.getInstance().reference.child("Chat")
        val dd1 = chatDB.child(ChatId.toString()).orderByKey().limitToLast(1)
        dd1.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                ++chk
                if (dataSnapshot.exists()) {
                    for (dd in dataSnapshot.children) {
                        if (dataSnapshot.child(dd.key.toString()).child("createByUser").value.toString() == MatchId) {
                            if (!first) {
                                if (chk > sumReported) {
                                    if (dataSnapshot.child(dd.key.toString()).child("read").value.toString() != "Read") {
                                        val myUnread = getSharedPreferences("TotalMessage", Context.MODE_PRIVATE)
                                        val dd2 = myUnread.getInt("total", 0)
                                        countNumberChat = dd2 + 1
                                        Log.d("count_unread", "chat : $countNumberChat text : ${dataSnapshot.child(dd.key.toString()).child("text").value.toString()}")
                                        val myUnread2 = getSharedPreferences("TotalMessage", Context.MODE_PRIVATE)
                                        val editorRead = myUnread2.edit()
                                        editorRead.putInt("total", countNumberChat!!)
                                        editorRead.apply()
                                        plus?.setCurrentIndex(countNumberChat!!)
                                    } else {
                                        val mySharedPreferences = getSharedPreferences("SentRead", Context.MODE_PRIVATE)
                                        val read = mySharedPreferences.getInt("Read", 0)
                                        val myUnread = getSharedPreferences("TotalMessage", Context.MODE_PRIVATE)
                                        val dd2 = myUnread.getInt("total", 0)
                                        countNumberChat = dd2 - read
                                        if (countNumberChat!! < 0) {
                                            countNumberChat = 0
                                        }
                                        val myUnread2 = getSharedPreferences("TotalMessage", Context.MODE_PRIVATE)
                                        val editorRead = myUnread2.edit()
                                        editorRead.putInt("total", countNumberChat!!)
                                        editorRead.apply()
                                        plus!!.setCurrentIndex(countNumberChat!!)
                                    }
                                }
                            } else {
                                bkk = false
                                ++chktotalhavechat;
                                getLastCheck(ChatId)
                            }
                        } else if (chk == chk_node) {
                            if (bkk) {
                                checkReport()
                            }
                        }
                    }
                } else {
                    if (chk == chk_node) {
                        if (bkk) {
                            checkReport()
                        }
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }*/
    /*private fun getLastCheck(ChatId: String?) {
        val chatDB = FirebaseDatabase.getInstance().reference.child("Chat").child(ChatId.toString())
        val dd = chatDB.orderByChild("read").equalTo("Unread")
        dd.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (first) {
                    ++chkcountchat;
                    val count = dataSnapshot.childrenCount.toInt()
                    countNumberChat = count + countNumberChat!!
                    Log.d("count_unread", "$count total $countNumberChat")
                    if (chkcountchat == chktotalhavechat) {
                        checkReport()
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }*/
    var nameCaution: MutableList<String?>? = ArrayList()
    var valueCaution: MutableList<Int?>? = ArrayList()
    private var sumReported = 0
    private fun checkReport() {
        val reportDb = FirebaseDatabase.getInstance().reference.child("Users").child(mAuth!!.currentUser!!.uid).child("Report")
        reportDb.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                    sumReported = chk_node
                    val myUnread = getSharedPreferences("TotalMessage", Context.MODE_PRIVATE)
                    val editorRead = myUnread.edit()
                    editorRead.putInt("total", countNumberChat!!.toInt())
                    editorRead.apply()
                    val intent = Intent(this@First_Activity, SwitchpageActivity::class.java)
                    var sumReport: Int? = 0
                    if (dataSnapshot.exists()) {
                        for (dd in dataSnapshot.children) {
                            sumReport = Integer.valueOf(dataSnapshot.child(dd.key.toString()).value.toString())
                            nameCaution?.add(dd.key)
                            valueCaution?.add(sumReport)
                        }
                        if (sumReport != 0) {
                            intent.putExtra("warning", nameCaution as ArrayList<String?>?)
                            intent.putExtra("warning_value", valueCaution as ArrayList<Int?>?)
                        }
                        intent.putExtra("first", countNumberChat.toString())
                        startActivity(intent)
                        finish()
                    } else {
                        intent.putExtra("first", countNumberChat.toString())
                        startActivity(intent)
                        finish()
                    }
                }
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    /*private fun getDatabaseUser(Uid: String?) {
        val InfoDB = FirebaseDatabase.getInstance().reference.child("Users").child(Uid.toString()).child("name")
        InfoDB.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.value != null) {
                    NameMatch?.add(dataSnapshot.value.toString())
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }*/

    /*private val IDNotification: MutableList<String?>? = ArrayList()
    private val IndexNotification: MutableList<Int?>? = ArrayList()
    private var id_plus = 0
    private fun Notification_chat(lastChat: String?, time: String?, ID: String?) {
        var icon: Bitmap? = null
        var Name: String? = "null"
        for (i in UidMatch_Image!!.indices) {
            if (ID == UidMatch?.get(i)) {
                icon = UidMatch_Image?.get(i)
                Name = NameMatch?.get(i)
                break
            }
        }
        val intent = Intent(this@First_Activity, ChatActivity::class.java)
        val b = Bundle()
        val random = Random()
        var TwoItems = false
        var id = 0
        if (IDNotification!!.size == 0) {
            id = ++id_plus
            IDNotification?.add(ID)
            IndexNotification?.add(id)
        } else {
            for (i in IDNotification.indices) {
                if (IDNotification?.get(i) == ID) {
                    TwoItems = true
                    id = IndexNotification?.get(i)!!
                }
            }
            if (!TwoItems) {
                id = ++id_plus
                Toast.makeText(this@First_Activity, "id :$id", Toast.LENGTH_SHORT).show()
                IDNotification.add(ID)
                IndexNotification!!.add(id)
            }
        }
        b.putString("time_chk", time)
        b.putString("matchId", ID)
        b.putString("nameMatch", Name)
        b.putString("unread", "-1")
        intent.putExtras(b)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        val pendingIntent = PendingIntent.getActivity(this@First_Activity, 1, intent, PendingIntent.FLAG_ONE_SHOT)
        val notification = NotificationCompat.Builder(this@First_Activity, App.Companion.CHANNEL_ID!!)
                .setSmallIcon(R.drawable.ic_love)
                .setContentTitle(Name)
                .setGroup("Chat")
                .setContentText(lastChat)
                .setLargeIcon(icon)
                .setColor(0xFFCC00)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()
        val Sum = NotificationCompat.Builder(this@First_Activity, App.Companion.CHANNEL_ID!!)
                .setSmallIcon(R.drawable.ic_love)
                .setStyle(NotificationCompat.InboxStyle().setBigContentTitle(getString(R.string.New_message)).setSummaryText(getString(R.string.You_have_new_message)))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setGroup("Chat")
                .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_CHILDREN)
                .setAutoCancel(true)
                .setGroupSummary(true)
                .build()
        notificationManager = NotificationManagerCompat.from(this@First_Activity)
        if (id == 2) {
            notificationManager!!.notify(id + random.nextInt(9999 - 1000) + 1000, Sum)
        }
        notificationManager!!.notify(id, notification)
    }*/


    override fun onStop() {
        super.onStop()
        mAuth!!.removeAuthStateListener(firebaseAuthStateListener!!)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0) {
            recreate()
            if (mLocationManager == null) {
                mLocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 1) {
            if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                recreate()
            } else {
                val intent = Intent(this@First_Activity, ShowGpsOpen::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                intent.putExtra("2", "2")
                finish()
                startActivity(intent)
            }
        }
    }


    override fun onStart() {
        super.onStart()
        ///////////////////////////////// เอา mAuth.start ออก
    }

    private inner class SimpleAnimationListener(private val context: Context?) : AnimationListener {
        override fun onAnimationEnd(hTextView: HTextView?) {
            hTextView!!.animateText("Welcome to my world")
        }

    }
}