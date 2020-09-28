package com.jabirdeveloper.tinderswipe.Chat

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.jabirdeveloper.tinderswipe.*
import com.jabirdeveloper.tinderswipe.Functions.DateTime
import com.jabirdeveloper.tinderswipe.Functions.LoadingDialog
import com.jabirdeveloper.tinderswipe.Functions.ReportUser
import com.jabirdeveloper.tinderswipe.R
import com.tapadoo.alerter.Alerter
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class ChatActivity : AppCompatActivity() {
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mChatAdapter: RecyclerView.Adapter<*>
    private lateinit var mChatLayoutManager: RecyclerView.LayoutManager
    private lateinit var linearLayoutOvalSend: LinearLayout
    private lateinit var menu: LinearLayout
    private lateinit var linearRecord: LinearLayout
    private lateinit var toolbar: Toolbar
    private lateinit var imgSend: ImageView
    private lateinit var mSendImage: ImageView
    private lateinit var mCameraOpen: ImageView
    private lateinit var menuBar: ImageView
    private lateinit var profile: ImageView
    private lateinit var back: ImageView
    private lateinit var mRecord: ImageView
    private lateinit var mRecordReal: ImageView
    private lateinit var mNameChat: TextView
    private lateinit var mRecordStatus: TextView
    private lateinit var mSendButton: Button
    private lateinit var openMenu: Button
    private var chk = 0
    private var chk2 = 0
    private var time_count = 0
    private var currentUserId: String? = null
    private var matchId: String? = null
    private var chatId: String? = null
    private var UrlImage: String? = null
    private var name_chat: String? = null
    private var time_chk: String? = null
    private var fileName: String? = null
    private var file_uri: Uri? = null
    private var uri_camera: Uri? = null
    private var mSendEditText: CustomEdittext? = null
    private var cHeck_back = 0
    private var pro: ProgressBar? = null
    private var proAudio: ProgressBar? = null
    private var recorder: MediaRecorder? = null
    private var active = true
    private var T: Timer? = null
    private var i = 0
    private var dialog: Dialog? = null
    var mDatabaseUser: DatabaseReference? = null
    var mDatabaseChat: DatabaseReference? = null
    var mDatabaseImage: DatabaseReference? = null
    var userDatabase: DatabaseReference? = null
    var usersDb: DatabaseReference? = null
    private val MY_PERMISSIONS_REQUEST_READ_MEDIA = 0

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        val permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf<String?>(Manifest.permission.WRITE_EXTERNAL_STORAGE), MY_PERMISSIONS_REQUEST_READ_MEDIA)
        }

        mRecord = findViewById(R.id.record_audio)
        profile = findViewById(R.id.pre_Image_porfile)
        back = findViewById(R.id.imageView5)
        imgSend = findViewById(R.id.img_send)
        mCameraOpen = findViewById(R.id.camera_open)
        proAudio = findViewById(R.id.progressBar_audio)
        pro = findViewById(R.id.progressBar_Chat)
        matchId = intent.extras!!.getString("matchId")
        var unreadCount = intent!!.extras!!.getString("unread")
        if (unreadCount == "-1") {
            val myUnread = getSharedPreferences("NotificationMessage", Context.MODE_PRIVATE)
            val dd2 = myUnread.getInt(matchId, 0)
            //Toast.makeText(ChatActivity.this, "MatchId " + matchId + " , "+(dd2), Toast.LENGTH_SHORT).show();
            val removeNotification = getSharedPreferences("NotificationActive", Context.MODE_PRIVATE)
            val editorRead = removeNotification.edit()
            editorRead.putString("ID", matchId)
            editorRead.apply()
            unreadCount = dd2.toString()
        }
        dialog = LoadingDialog(this).dialog()

        val mySharedPreferences = getSharedPreferences("SentRead", Context.MODE_PRIVATE)
        val editor = mySharedPreferences.edit()
        editor.putInt("Read", Integer.valueOf(unreadCount!!.toInt()))
        editor.apply()
        name_chat = intent!!.extras!!.getString("nameMatch")
        time_chk = intent!!.extras!!.getString("time_chk")
        mRecordReal = findViewById(R.id.record_real)
        openMenu = findViewById(R.id.menu_button)
        mSendEditText = findViewById(R.id.message)
        menu = findViewById(R.id.menu_app)
        mRecordStatus = findViewById(R.id.record_status)
        linearRecord = findViewById(R.id.Linear_record)
        menuBar = findViewById(R.id.menubar)
        linearLayoutOvalSend = findViewById(R.id.oval_send)
        mNameChat = findViewById(R.id.name_chat)
        mSendImage = findViewById(R.id.send_image)
        currentUserId = FirebaseAuth.getInstance().currentUser!!.uid
        usersDb = FirebaseDatabase.getInstance().reference.child("Users")
        mDatabaseUser = if (intent.hasExtra("Hi")) {
            FirebaseDatabase.getInstance().reference.child("Users").child(currentUserId.toString()).child("connection").child("chatna").child(matchId.toString())
        } else {
            FirebaseDatabase.getInstance().reference.child("Users").child(currentUserId.toString()).child("connection").child("matches").child(matchId.toString()).child("ChatId")
        }
        mDatabaseImage = FirebaseDatabase.getInstance().reference.child("Users").child(matchId.toString()).child("ProfileImage").child("profileImageUrl0")
        mDatabaseChat = FirebaseDatabase.getInstance().reference.child("Chat")
        mRecord.setOnClickListener(View.OnClickListener {
            if (ActivityCompat.checkSelfPermission(this@ChatActivity, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this@ChatActivity, arrayOf<String?>(
                        Manifest.permission.RECORD_AUDIO), 72)
            } else {
                if (linearRecord.visibility == View.GONE) {
                    proAudio!!.visibility = View.GONE
                    linearRecord.visibility = View.VISIBLE
                    mRecordReal.visibility = View.VISIBLE
                    mRecordStatus.text = "Press to Record"
                } else {
                    linearRecord.visibility = View.GONE
                }
                /*if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        startRecording();
                        Toast.makeText(ChatActivity.this,"Start",Toast.LENGTH_SHORT).show();
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        stopRecording();
                        Toast.makeText(ChatActivity.this,"Stop",Toast.LENGTH_SHORT).show();
                    }*/
            }
        })
        mRecordReal.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                startRecording()
                mRecordStatus.text = ("00:00")
                T = Timer()
                T!!.scheduleAtFixedRate(object : TimerTask() {

                    override fun run() {
                        runOnUiThread {
                            ++time_count
                            val minute = time_count / 60
                            val second = time_count % 60
                            mRecordStatus.text = (String.format("%02d", minute) + ":" + String.format("%02d", second))
                        }
                    }
                }, 1000, 1000)
            } else if (event.action == MotionEvent.ACTION_UP) {
                T!!.cancel()
                stopRecording()
                mRecordReal.visibility = (View.GONE)
                proAudio!!.visibility = (View.VISIBLE)
                mRecordStatus.text = ("Uploading.....")
            }
            true
        }
        back.setOnClickListener(View.OnClickListener { onBackPressed() })
        menuBar.setOnClickListener(View.OnClickListener { v ->
            val dd = PopupMenu(this@ChatActivity, v)
            dd.menuInflater.inflate(R.menu.menu_bar, dd.menu)
            dd.gravity = Gravity.END
            dd.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_unmatch -> {
                        Alerter.create(this@ChatActivity)
                                .setTitle(getString(R.string.cancel_match2))
                                .setText(getString(R.string.cancel_match_confirm))
                                .setIconColorFilter(Color.parseColor("#FFFFFF"))
                                .setBackgroundColorInt(Color.parseColor("#FF5050"))
                                .setIcon(ContextCompat.getDrawable(this@ChatActivity, R.drawable.ic_warning_black_24dp)!!)
                                .addButton(getString(R.string.cancle), R.style.AlertButton, View.OnClickListener { Alerter.hide() })
                                .addButton(getString(R.string.ok), R.style.AlertButton, View.OnClickListener {
                                    Alerter.hide()
                                    deletechild()
                                })
                                .show()
                    }
                    R.id.menu_delete -> {
                        val mDialog = ReportUser(this@ChatActivity, matchId!!).reportDialog()
                        mDialog.show()
                    }
                    R.id.delete_chat -> {
                        Alerter.create(this@ChatActivity)
                                .setTitle(getString(R.string.delete_message_all))
                                .setText(getString(R.string.delete_message_all_confirm))
                                .setIconColorFilter(Color.parseColor("#FFFFFF"))
                                .setBackgroundColorInt(Color.parseColor("#FF5050"))
                                .setIcon(ContextCompat.getDrawable(this@ChatActivity, R.drawable.ic_warning_black_24dp)!!)
                                .addButton(getString(R.string.cancle), R.style.AlertButton, View.OnClickListener { Alerter.hide() })
                                .addButton(getString(R.string.ok), R.style.AlertButton, View.OnClickListener {
                                    Alerter.hide()
                                    val getStart = FirebaseDatabase.getInstance().reference.child("Users").child(currentUserId.toString()).child("connection").child("matches").child(matchId.toString()).child("Start")
                                    getStart.setValue(FetchId!![FetchId!!.size - 1])
                                    val prefs1 = getSharedPreferences(chatId, Context.MODE_PRIVATE)
                                    val allPrefs = prefs1.all
                                    val set = allPrefs.keys
                                    for (s in set) {
                                        Log.d("Id1", s)
                                        getSharedPreferences(s, Context.MODE_PRIVATE).edit().clear().apply()
                                    }
                                    getSharedPreferences(chatId, Context.MODE_PRIVATE).edit().clear().apply()
                                    FetchId.clear()
                                    start = "null"
                                    sizePre = 0
                                    resultChat!!.clear()
                                    mChatAdapter.notifyDataSetChanged()
                                    val removeNotification = getSharedPreferences("DeleteChatActive", Context.MODE_PRIVATE)
                                    val editorRead = removeNotification.edit()
                                    editorRead.putString("ID", matchId)
                                    editorRead.apply()
                                })
                                .show()
                    }
                    else -> {
                        Toast.makeText(this@ChatActivity, "" + item, Toast.LENGTH_SHORT).show()
                    }
                }
                true
            }
            dd.setOnDismissListener { }
            dd.show()
        })
        profile.setOnClickListener {
            val intent = Intent(applicationContext, ProfileUserOppositeActivity2::class.java)
            intent.putExtra("madoo", "1")
            intent.putExtra("User_opposite", matchId)
            startActivity(intent)
        }
        openMenu.setOnClickListener {
            if (menu.visibility == View.GONE) {
                openMenu.visibility = View.GONE
                menu.visibility = View.VISIBLE
            }
        }
        getImageProfile()
        mNameChat.text = name_chat
        mRecyclerView = findViewById<View?>(R.id.recyclerView_2) as RecyclerView
        val mChatLayoutManager = LinearLayoutManager(this@ChatActivity)
        mRecyclerView.layoutManager = mChatLayoutManager
        mChatAdapter = ChatAdapter(getDataSetChat(), this@ChatActivity)
        mSendButton = findViewById(R.id.send)
        mSendImage.setOnClickListener(View.OnClickListener {
            val intent = Intent()
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.type = "image/*"
            intent.action = Intent.ACTION_PICK
            startActivityForResult(Intent.createChooser(intent, "เลือกรูปภาพ"), 23)
        })
        mSendEditText!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().isNotEmpty()) {
                    mSendButton.background = ContextCompat.getDrawable(this@ChatActivity, R.drawable.chat_after)
                    mSendButton.rotation = 0f
                } else {
                    mSendButton.background = ContextCompat.getDrawable(this@ChatActivity, R.drawable.chat_before)
                    mSendButton.rotation = 0f
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        mSendEditText!!.setOnEditTextImeBackListener(object : EditTextImeBackListener {
            override fun onImeBack(ctrl: CustomEdittext?, text: String?) {
                menu.visibility = View.VISIBLE
                mSendEditText!!.clearFocus()
                openMenu.visibility = View.GONE
            }
        })
        mSendEditText!!.setOnFocusChangeListener { view, b ->
            if (b) {
                linearRecord.visibility = View.GONE
                menu.visibility = View.GONE
                openMenu.visibility = View.VISIBLE
            } else {
                menu.visibility = View.VISIBLE
                openMenu.visibility = View.GONE
            }
        }
        mSendButton.setOnClickListener(View.OnClickListener { sendMessage() })
        linearLayoutOvalSend.setOnClickListener(View.OnClickListener { sendMessage() })
        mCameraOpen.setOnClickListener(View.OnClickListener {
            if (ActivityCompat.checkSelfPermission(this@ChatActivity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this@ChatActivity, arrayOf<String?>(
                        Manifest.permission.CAMERA), 2)
            } else {
                val values = ContentValues()
                uri_camera = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri_camera)
                startActivityForResult(intent, 33)
            }
        })
    }

    private fun getImageProfile() {
        mDatabaseImage!!.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    UrlImage = dataSnapshot.value.toString()
                    Glide.with(applicationContext).load(UrlImage).apply(RequestOptions().override(100, 100)).into(profile)
                } else {
                    if (intent.getStringExtra("gender") == "Female") Glide.with(applicationContext).load(R.drawable.ic_woman).apply(RequestOptions().override(100, 100)).into(profile) else Glide.with(applicationContext).load(R.drawable.ic_man).apply(RequestOptions().override(100, 100)).into(profile)
                }
                getChatId()
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun sendMessage() {
        val sendMessageText = mSendEditText!!.text.toString()
        if (!sendMessageText.isEmpty()) {
            val d = DateTime
            val newMessageDb = mDatabaseChat!!.push()
            val newMessage = hashMapOf(
                    "createByUser" to currentUserId,
                    "text" to sendMessageText,
                    "time" to d.time(),
                    "date" to d.date(),
                    "read" to "Unread")
            newMessageDb.setValue(newMessage)
        }
        mSendEditText!!.text = (null)
    }

    private fun getChatId() {
        mDatabaseUser!!.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    chatId = dataSnapshot.value.toString()
                    mDatabaseChat = mDatabaseChat!!.child(chatId.toString())
                    userDatabase = FirebaseDatabase.getInstance().reference.child("Chat").child(chatId.toString())
                    fetch_sharedPreference()
                    //getCount()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun Chat_check_read() {
        val dd = mDatabaseChat!!.orderByChild("read").equalTo("Unread")
        dd.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (loop in dataSnapshot.children) {
                    read_already(loop.key)
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun read_already(key: String?) {
        mDatabaseChat!!.child(key.toString()).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.child("createByUser").value.toString() == matchId) {
                    mDatabaseChat!!.child(key.toString()).child("read").setValue("Read")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private var count_node_d = 0
    private fun getCount() {
        val dd = FirebaseDatabase.getInstance().reference.child("Chat")
        dd.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (!dataSnapshot.hasChild(chatId.toString())) {
                    pro!!.visibility = View.INVISIBLE
                }
                fetch_sharedPreference()
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private var c = 0
    private fun getcount(): Int {
        mDatabaseChat!!.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                c = dataSnapshot.childrenCount.toInt()
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
        return c
    }

    private var firstConnect = true
    private var start: String? = "null"
    private fun getFirstNode() {
        val getStart = FirebaseDatabase.getInstance().reference.child("Users").child(currentUserId.toString()).child("connection").child("matches").child(matchId.toString()).child("Start")
        getStart.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (firstConnect) {
                    firstConnect = false
                    if (dataSnapshot.exists()) {
                        start = dataSnapshot.value.toString()
                        getChatMessages()
                    } else {
                        getChatMessages()
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private var sizePre = 0
    private val FetchId: MutableList<String?>? = ArrayList()
    private fun fetch_sharedPreference() {
        val prefs = getSharedPreferences(chatId, Context.MODE_PRIVATE)
        val allPrefs = prefs.all
        val set = allPrefs.keys
        for (s in set) {
            FetchId!!.add(s)
        }
        for (s in set) {
            Log.d("Id2", "" + prefs.getInt(s, 0))
            FetchId!![prefs.getInt(s, 0) - 1] = s
        }
        sizePre = FetchId!!.size
        setMessage()
    }

    private fun setMessage() {
        for (i in (FetchId!!.indices)) {
            c++
            var message: String
            var createdByUser: String
            var time: String
            var urlSend = "default"
            var audio: String
            var read: String
            var audioLength: String
            val myInNode = getSharedPreferences(FetchId.elementAt(i), Context.MODE_PRIVATE)
            message = myInNode.getString("text", "null")!!
            read = myInNode.getString("read", "null")!!
            createdByUser = myInNode.getString("createByUser", "null")!!
            time = myInNode.getString("time", "null")!!
            val check = myInNode.getString("image", "null")
            Log.d("text_chat", message)
            if (check != "default" && check != "null") {
                ++chk2
                urlSend = myInNode.getString("image", "null")!!
            }
            audio = myInNode.getString("audio", "null")!!
            audioLength = myInNode.getString("audio_length", "null")!!
            var currentUserBoolean = false
            if (createdByUser == currentUserId) {
                currentUserBoolean = true
            } else if (c == FetchId.size) {
                if (createdByUser != currentUserId)
                    Chat_check_read()
            }
            val newMessage = ChatObject(message, currentUserBoolean, UrlImage, time, chatId, urlSend, chk2, matchId, audio, audioLength, currentUserId)
            resultChat!!.add(newMessage)
            ++chk
                if (FetchId.size == chk) {
                    mChatAdapter.notifyDataSetChanged()
                    mRecyclerView.adapter = mChatAdapter
                    mRecyclerView.scrollToPosition(resultChat.size - 1)
                    pro!!.visibility = View.INVISIBLE
                    count_node_d = FetchId.size
                }

        }
        getFirstNode()
    }

    private fun getChatMessages() {
        var chatDatabase: Query? = mDatabaseChat
        if (FetchId!!.size > 0) {
            Toast.makeText(this@ChatActivity, "Size > 1 :" + FetchId.elementAt(FetchId.size - 1), Toast.LENGTH_SHORT).show()
            chatDatabase = mDatabaseChat!!.orderByKey().startAt(FetchId.elementAt(FetchId.size - 1))
        } else if (start != "null" && FetchId.size == 0) {
            Toast.makeText(this@ChatActivity, "Size == 0 :$start", Toast.LENGTH_SHORT).show()
            chatDatabase = mDatabaseChat!!.orderByKey().startAt(start)
        }
        chatDatabase!!.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                if (dataSnapshot.exists()) {
                    if (FetchId.size > 0) {
                        //Toast.makeText(this@ChatActivity,dataSnapshot.key + " , "+FetchId.elementAt(FetchId.size - 1),Toast.LENGTH_LONG).show()
                        if (dataSnapshot.key != FetchId.elementAt(FetchId.size - 1)) {
                            c++
                            var message: String? = null
                            var createdByUser: String? = null
                            var time: String? = null
                            var url_send = "default"
                            var audio = "null"
                            var audio_length = "null"
                            var read = "null"
                            val MyNode = getSharedPreferences(chatId, Context.MODE_PRIVATE)
                            val S1 = MyNode.getInt(dataSnapshot.key, 0)
                            Log.d("unsave", dataSnapshot.key)
                            //Toast.makeText(this@ChatActivity,read,Toast.LENGTH_LONG).show()
                            if (dataSnapshot.child("read").value != null) {
                                read = dataSnapshot.child("read").value.toString()
                            }
                            if (dataSnapshot.child("text").value != null) {
                                message = dataSnapshot.child("text").value.toString()
                            }
                            if (dataSnapshot.child("createByUser").value != null) {
                                createdByUser = dataSnapshot.child("createByUser").value.toString()
                            }
                            if (dataSnapshot.child("time").value != null) {
                                time = dataSnapshot.child("time").value.toString()
                            }
                            if (dataSnapshot.child("image").value != null) {
                                url_send = dataSnapshot.child("image").value.toString()
                                ++chk2
                            }
                            if (dataSnapshot.child("audio").value != null) {
                                audio = dataSnapshot.child("audio").value.toString()
                                audio_length = dataSnapshot.child("audio_length").value.toString()
                            }
                            val ChatMessageStored = getSharedPreferences(chatId, Context.MODE_PRIVATE)
                            val editorRead = ChatMessageStored.edit()
                            editorRead.putInt(dataSnapshot.key, ++sizePre)
                            //Toast.makeText(this@ChatActivity,"UnSave"+(sizePre),Toast.LENGTH_SHORT).show();
                            FetchId.add(dataSnapshot.key)
                            editorRead.apply()
                            val NodeChatMessageStored = getSharedPreferences(dataSnapshot.key, Context.MODE_PRIVATE)
                            val NodeEditorRead = NodeChatMessageStored.edit()
                            NodeEditorRead.putString("text", message)
                            NodeEditorRead.putString("time", time)
                            NodeEditorRead.putString("createByUser", createdByUser)
                            NodeEditorRead.putString("image", url_send)
                            NodeEditorRead.putString("audio", audio)
                            NodeEditorRead.putString("audio_length", audio_length)
                            NodeEditorRead.putString("read", read)
                            NodeEditorRead.apply()
                            if (createdByUser != null && time != null) {
                                var currentUserBoolean = false
                                if (createdByUser == currentUserId) {
                                    currentUserBoolean = true
                                } else {
                                    if (active) {
                                        if (dataSnapshot.child("read").value.toString() == "Unread") {
                                            Toast.makeText(this@ChatActivity, dataSnapshot.child("read").value.toString(), Toast.LENGTH_LONG).show()
                                            Chat_check_read()
                                        }
                                    }
                                }
                                val newMessage = ChatObject(message, currentUserBoolean, UrlImage, time, chatId, url_send, chk2, matchId, audio, audio_length, currentUserId)
                                resultChat!!.add(newMessage)
                                mChatAdapter.notifyDataSetChanged()
                                ++chk
                                if (FetchId.size == 1) {
                                    mChatAdapter.notifyDataSetChanged()
                                    mRecyclerView.adapter = mChatAdapter
                                    mRecyclerView.scrollToPosition(resultChat!!.size - 1)
                                    pro!!.visibility = View.INVISIBLE
                                } else if (count_node_d < chk) {
                                    mRecyclerView.smoothScrollToPosition(mRecyclerView.adapter!!.itemCount - 1)
                                }
                            }
                        }
                    } else if (dataSnapshot.key != start) {
                        Toast.makeText(this@ChatActivity, "First", Toast.LENGTH_LONG).show()

                        c++
                        var message: String? = null
                        var createdByUser: String? = null
                        var time: String? = null
                        var urlSend = "default"
                        var audio = "null"
                        var audioLength = "null"
                        var read = "null"
                        Log.d("unsave", dataSnapshot.key.toString())
                        //Toast.makeText(this@ChatActivity,read,Toast.LENGTH_LONG).show()
                        if (dataSnapshot.child("read").value != null) {
                            read = dataSnapshot.child("read").value.toString()
                        }
                        if (dataSnapshot.child("text").value != null) {
                            message = dataSnapshot.child("text").value.toString()
                        }
                        if (dataSnapshot.child("createByUser").value != null) {
                            createdByUser = dataSnapshot.child("createByUser").value.toString()
                        }
                        if (dataSnapshot.child("time").value != null) {
                            time = dataSnapshot.child("time").value.toString()
                        }
                        if (dataSnapshot.child("image").value != null) {
                            urlSend = dataSnapshot.child("image").value.toString()
                            ++chk2
                        }
                        if (dataSnapshot.child("audio").value != null) {
                            audio = dataSnapshot.child("audio").value.toString()
                            audioLength = dataSnapshot.child("audio_length").value.toString()
                        }
                        val chatMessageStored = getSharedPreferences(chatId, Context.MODE_PRIVATE)
                        val editorRead = chatMessageStored.edit()
                        editorRead.putInt(dataSnapshot.key, ++sizePre)
                        //Toast.makeText(ChatActivity.this,"UnSave"+(sizePre),Toast.LENGTH_SHORT).show();
                        FetchId.add(dataSnapshot.key)
                        editorRead.apply()
                        val nodeChatMessageStored = getSharedPreferences(dataSnapshot.key, Context.MODE_PRIVATE)
                        val nodeEditorRead = nodeChatMessageStored.edit()
                        nodeEditorRead.putString("text", message)
                        nodeEditorRead.putString("time", time)
                        nodeEditorRead.putString("createByUser", createdByUser)
                        nodeEditorRead.putString("image", urlSend)
                        nodeEditorRead.putString("audio", audio)
                        nodeEditorRead.putString("audio_length", audioLength)
                        nodeEditorRead.putString("read", read)
                        nodeEditorRead.apply()
                        if (createdByUser != null && time != null) {
                            var currentUserBoolean = false
                            if (createdByUser == currentUserId) {
                                currentUserBoolean = true
                            } else {
                                if (active) {
                                    if (dataSnapshot.child("read").value.toString() == "Unread") {
                                        Chat_check_read()
                                    }
                                }
                            }
                            val newMessage = ChatObject(message, currentUserBoolean, UrlImage, time, chatId, urlSend, chk2, matchId, audio, audioLength, currentUserId)
                            resultChat!!.add(newMessage)
                            mChatAdapter.notifyDataSetChanged()
                            ++chk
                            if (FetchId.size == 1) {
                                mRecyclerView.adapter = mChatAdapter
                                mRecyclerView.scrollToPosition(resultChat.size - 1)
                                pro!!.visibility = View.INVISIBLE
                            } else if (count_node_d < chk) {
                                mRecyclerView.smoothScrollToPosition(mRecyclerView.adapter!!.getItemCount() - 1)
                            }
                        }
                    }
                }
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 33 && resultCode == Activity.RESULT_OK) {
            dialog!!.show()
            val name = System.currentTimeMillis().toString()
            val filepath = FirebaseStorage.getInstance().reference.child("SendImage").child(currentUserId.toString()).child(matchId.toString()).child("image$name")
            var bitmap: Bitmap? = null
            try {
                bitmap = MediaStore.Images.Media.getBitmap(application.contentResolver, uri_camera)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            val baos = ByteArrayOutputStream()
            bitmap!!.compress(Bitmap.CompressFormat.JPEG, 20, baos)
            val dataurl = baos.toByteArray()
            val uploadTask = filepath.putBytes(dataurl)
            uploadTask.addOnFailureListener {
                Toast.makeText(this@ChatActivity, "Fail Upload", Toast.LENGTH_LONG).show()
                finish()
            }
            uploadTask.addOnSuccessListener {
                val filepath = FirebaseStorage.getInstance().reference.child("SendImage").child(currentUserId.toString()).child(matchId.toString()).child("image$name")
                filepath.downloadUrl.addOnSuccessListener { uri ->
                    val newMessageDb = mDatabaseChat!!.push()
                    val d = DateTime
                    val newMessage = hashMapOf(
                            "createByUser" to currentUserId,
                            "time" to d.time(),
                            "date" to d.date(),
                            "text" to "photo$currentUserId",
                            "read" to "Unread",
                            "image" to uri.toString())
                    newMessageDb.setValue(newMessage)
                    dialog!!.dismiss()
                }.addOnFailureListener { }
            }
        }
        if (requestCode == 23 && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            dialog!!.show()
            val name = System.currentTimeMillis().toString()
            file_uri = data.data
            val filepath = FirebaseStorage.getInstance().reference.child("SendImage").child(currentUserId.toString()).child(matchId.toString()).child("image$name")
            var bitmap: Bitmap? = null
            try {
                bitmap = MediaStore.Images.Media.getBitmap(application.contentResolver, file_uri)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            val baos = ByteArrayOutputStream()
            bitmap!!.compress(Bitmap.CompressFormat.JPEG, 20, baos)
            val dataurl = baos.toByteArray()
            val uploadTask = filepath.putBytes(dataurl)
            uploadTask.addOnFailureListener {
                Toast.makeText(this@ChatActivity, "Fail Upload", Toast.LENGTH_LONG).show()
                finish()
            }
            uploadTask.addOnSuccessListener {
                val filepath = FirebaseStorage.getInstance().reference.child("SendImage").child(currentUserId.toString()).child(matchId.toString()).child("image$name")
                filepath.downloadUrl.addOnSuccessListener { uri ->
                    val newMessageDb = mDatabaseChat!!.push()
                    val d = DateTime
                    val newMessage = hashMapOf(
                            "createByUser" to currentUserId,
                            "time" to d.time(),
                            "date" to d.date(),
                            "text" to "photo$currentUserId",
                            "read" to "Unread",
                            "image" to uri.toString())
                    newMessageDb.setValue(newMessage)
                    dialog!!.dismiss()
                }.addOnFailureListener { }
            }
        }
    }

    private fun startRecording() {
        fileName = externalCacheDir!!.absolutePath
        fileName += "/recorded_audio.3gp"
        recorder = MediaRecorder()
        recorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
        recorder!!.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        recorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
        recorder!!.setOutputFile(fileName)
        try {
            recorder!!.prepare()
        } catch (e: IOException) {
            Toast.makeText(this@ChatActivity, "Fail to recorded", Toast.LENGTH_SHORT).show()
        }
        recorder!!.start()
    }

    private fun stopRecording() {
        recorder!!.stop()
        recorder!!.release()
        recorder = null
        UpLoadAudio()
    }

    private fun UpLoadAudio() {
        val name = System.currentTimeMillis().toString()
        val ss2 = FirebaseStorage.getInstance().reference.child("Audio").child(currentUserId.toString()).child(matchId.toString()).child("audio$name.3gp")
        val uri = Uri.fromFile(File(fileName))
        ss2.putFile(uri).addOnSuccessListener {
            ss2.downloadUrl.addOnSuccessListener { uri ->
                Toast.makeText(this@ChatActivity, "Success", Toast.LENGTH_SHORT).show()
                linearRecord.visibility = View.GONE
                val downloadUrl = uri
                val newMessageDb = mDatabaseChat!!.push()
                val calendar = Calendar.getInstance()
                val currentTime = SimpleDateFormat("HH:mm", Locale.UK)
                val time_user = currentTime.format(calendar.time)
                val currentDate = SimpleDateFormat("dd/MM/yyyy")
                val date_user = currentDate.format(calendar.time)
                val newMessage = hashMapOf(
                        "createByUser" to currentUserId,
                        "time" to time_user,
                        "date" to date_user,
                        "audio_length" to time_count.toString(),
                        "audio" to downloadUrl.toString(),
                        "text" to "audio$currentUserId",
                        "read" to "Unread")
                newMessageDb.setValue(newMessage)
                time_count = 0
            }
        }
    }

    override fun onBackPressed() {
        mSendEditText!!.clearFocus()
        if (linearRecord.visibility == View.VISIBLE) {
            linearRecord.visibility = View.GONE
        } else {
            super.onBackPressed()
            if (intent.hasExtra("chat_na")) {
                Log.d("gghj", getcount().toString())
                if (c > 1) {
                    usersDb!!.child(matchId.toString()).child("connection").child("yep").child(currentUserId.toString()).setValue(true)
                    usersDb!!.child(currentUserId.toString()).child("connection").child("yep").child(matchId.toString()).setValue(true)
                    usersDb!!.child(currentUserId.toString()).child("connection").child("chatna").child(matchId.toString()).setValue(null)
                }
            }
            if (cHeck_back == 0) {
                finish()
                return
            }
            val intent = Intent(this@ChatActivity, SwitchpageActivity::class.java)
            intent.putExtra("first", 1)
            startActivity(intent)
            finish()
        }
    }

    override fun onPause() {
        super.onPause()
        active = false

    }

    private fun deletechild() {
        val datadelete = FirebaseDatabase.getInstance().reference.child("Users")
        val datachat = FirebaseDatabase.getInstance().reference
        datachat.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var ChatId = dataSnapshot.child("Users").child(currentUserId.toString()).child("connection").child("matches").child(matchId.toString()).child("ChatId").value.toString()
                if (dataSnapshot.child("Chat").hasChild(ChatId.toString())) {
                    datachat.child("Chat").child(ChatId.toString()).removeValue()
                }
                datadelete.child(currentUserId.toString()).child("connection").child("matches").child(matchId.toString()).removeValue()
                datadelete.child(currentUserId.toString()).child("connection").child("yep").child(matchId.toString()).removeValue()
                datadelete.child(matchId.toString()).child("connection").child("matches").child(currentUserId.toString()).removeValue()
                datadelete.child(matchId.toString()).child("connection").child("yep").child(currentUserId.toString()).removeValue()
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }


    private fun UpdateDate() {
        val date_user: String
        val currentDate = SimpleDateFormat("dd/MM/yyyy")
        val calendar = Calendar.getInstance()
        date_user = currentDate.format(calendar.time)
        val ff = hashMapOf(
                "date" to date_user)
        usersDb!!.child(currentUserId.toString()).child("PutReportId").child(matchId.toString()).updateChildren(ff as Map<String, Any>)
    }

    private val resultChat: ArrayList<ChatObject?>? = ArrayList()
    private fun getDataSetChat(): MutableList<ChatObject?>? {
        return resultChat
    }
}