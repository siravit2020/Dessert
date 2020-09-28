package com.jabirdeveloper.tinderswipe.Chat

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.MediaPlayer
import android.os.CountDownTimer
import android.os.Environment
import android.view.View
import android.view.View.OnLongClickListener
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.jabirdeveloper.tinderswipe.ImageChat.ItemImageActivity
import com.jabirdeveloper.tinderswipe.R
import com.ldoublem.loadingviewlib.LVCircularCD
import com.wang.avi.AVLoadingIndicatorView
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class ChatViewHolders(itemView: View, private val context: Context?) : RecyclerView.ViewHolder(itemView) {
    var mMessage: TextView
    var time_send: TextView
    var mMatchId: TextView
    var mMatchIdReal: TextView
    var audio_url: TextView
    var begin_audio: TextView
    var mContainer: LinearLayout
    var button_audio: Button
    var mchk: LinearLayout
    var mchk2: LinearLayout
    var mchk3: LinearLayout
    var mImage_opposite: ImageView
    var mImage_sent: ImageView
    var stop_Animate: ImageView? = null
    var progressBarAudio: ProgressBar

    //var mRecycler: RecyclerView?
    var mChk: TextView
    var mChk_2: TextView
    private val loading: AVLoadingIndicatorView? = null
    private val myClipboard: ClipboardManager?
    private var myClip: ClipData? = null
    private var mediaPlayer: MediaPlayer? = null
    var CD: LVCircularCD?
    private var length = 0
    private var total_length = 0
    private var countDownTimer: CountDownTimer? = null
    var card: CardView?
    private var check = true
    var alert_dialog: AlertDialog? = null

    init {
        card = itemView.findViewById(R.id.card)
        CD = itemView.findViewById(R.id.play_pause_animate)
        CD!!.setViewColor(Color.parseColor("#FFF064"))
        progressBarAudio = itemView.findViewById(R.id.progressBar_playAudio)
        button_audio = itemView.findViewById(R.id.play_audio)
        audio_url = itemView.findViewById(R.id.audio_url)
        mChk = itemView.findViewById(R.id.chk_image)
        mMatchId = itemView.findViewById(R.id.match_id)
        mImage_sent = itemView.findViewById(R.id.img_sent)
        //mRecycler = itemView.findViewById<View?>(R.id.recyclerView_2) as RecyclerView
        mchk2 = itemView.findViewById(R.id.lili)
        begin_audio = itemView.findViewById(R.id.begin_audio)
        mchk3 = itemView.findViewById(R.id.li)
        mchk = itemView.findViewById(R.id.lilili)
        time_send = itemView.findViewById(R.id.time_chat_user)
        mMessage = itemView.findViewById(R.id.chatmessage)
        mContainer = itemView.findViewById(R.id.container)
        mMatchIdReal = itemView.findViewById(R.id.match_id_real_image)
        mImage_opposite = itemView.findViewById(R.id.image_holder)
        mChk_2 = itemView.findViewById(R.id.chk_image_2)
        val item = context!!.resources.getStringArray(R.array.chat_item)
        val item_image = context.resources.getStringArray(R.array.chat_item_image)
        val builder = AlertDialog.Builder(context)
        myClipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        button_audio.setOnClickListener(View.OnClickListener {
            if (button_audio.background.constantState === ContextCompat.getDrawable(context!!, R.drawable.ic_play_circle_outline_black_24dp)!!.constantState) {
                CD!!.startAnim()
                if (check) {
                    val minute = Integer.valueOf(mMessage.text.toString().substring(0, 2))
                    val second = Integer.valueOf(mMessage.text.toString().substring(3, 5))
                    total_length = second + minute * 60
                    check = false
                }
                button_audio.visibility = View.GONE
                progressBarAudio.visibility = View.VISIBLE
                val minute = Integer.valueOf(mMessage.text.toString().substring(0, 2))
                val second = Integer.valueOf(mMessage.text.toString().substring(3, 5))
                val minute_sub = Integer.valueOf(begin_audio.text.toString().substring(0, 2))
                val second_sub = Integer.valueOf(begin_audio.text.toString().substring(3, 5))
                val counter_sub = second_sub + minute_sub * 60
                val counter = second + minute * 60 - counter_sub
                mediaPlayer = MediaPlayer()
                try {
                    mediaPlayer!!.setDataSource(audio_url.text.toString())
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                try {
                    mediaPlayer!!.prepare()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                if (length == 0) {
                    mediaPlayer!!.start()
                } else {
                    mediaPlayer!!.seekTo(length)
                    mediaPlayer!!.start()
                }
                if (mchk2.background.constantState === ContextCompat.getDrawable(context, R.drawable.chat_1)!!.constantState) {
                    mchk2.background = ContextCompat.getDrawable(context, R.drawable.chat_1_selected)
                } else {
                    mchk2.background = ContextCompat.getDrawable(context, R.drawable.chat_2_selected)
                }
                progressBarAudio.visibility = View.GONE
                button_audio.visibility = View.VISIBLE
                button_audio.background = ContextCompat.getDrawable(context, R.drawable.ic_pause_circle_outline_black_24dp)
                countDownTimer = object : CountDownTimer(((counter + 1) * 1000).toLong(), 1000) {
                    var total = counter
                    override fun onTick(millisUntilFinished: Long) {
                        val aaa = millisUntilFinished.toInt()
                        val all_second = total - aaa / 1000
                        if (all_second < 60) {
                            val second = String.format("%02d", all_second + second_sub)
                            begin_audio.text = ("00:$second")
                        } else {
                            val check_minute = all_second / 60
                            val check_second = all_second % 60
                            val second_s = String.format("%02d", check_second + minute_sub)
                            val minute = String.format("%02d", check_minute + second_sub)
                            begin_audio.text = ("$minute:$second_s")
                        }
                    }

                    override fun onFinish() {
                        length = 0
                        CD!!.stopAnim()
                        button_audio.background = ContextCompat.getDrawable(context, R.drawable.ic_play_circle_outline_black_24dp)
                        begin_audio.text = ("00:00")
                        if (mchk2.background.constantState === ContextCompat.getDrawable(context, R.drawable.chat_1_selected)!!.constantState) {
                            mchk2.background = ContextCompat.getDrawable(context, R.drawable.chat_1)
                        } else {
                            mchk2.background = ContextCompat.getDrawable(context, R.drawable.chat_2)
                        }
                    }
                }.start()
            } else {
                CD!!.stopAnim()
                countDownTimer!!.cancel()
                button_audio.background = ContextCompat.getDrawable(context, R.drawable.ic_play_circle_outline_black_24dp)
                mediaPlayer!!.stop()
                length = mediaPlayer!!.currentPosition
                if (mchk2.background.constantState === ContextCompat.getDrawable(context, R.drawable.chat_1_selected)!!.constantState) {
                    mchk2.background = ContextCompat.getDrawable(context, R.drawable.chat_1)
                } else {
                    mchk2.background = ContextCompat.getDrawable(context, R.drawable.chat_2)
                }
            }
        })
        mchk2.setOnLongClickListener(OnLongClickListener {
            if (mchk2.background.constantState === ContextCompat.getDrawable(context, R.drawable.chat_2)!!.constantState) {
                mchk2.background = ContextCompat.getDrawable(context, R.drawable.chat_2_selected)
                builder.setItems(item) { dialog, which ->
                    mchk2.background = ContextCompat.getDrawable(context, R.drawable.chat_2)
                    if (item[which] == "คัดลอก") {
                        val text: String
                        text = mMessage.text.toString()
                        myClip = ClipData.newPlainText("text", text)
                        myClipboard.setPrimaryClip(myClip!!)
                    }
                }
                builder.setOnDismissListener { mchk2.background = ContextCompat.getDrawable(context, R.drawable.chat_2) }
                alert_dialog = builder.create()
                alert_dialog!!.show()
                alert_dialog!!.window!!.setLayout(800, 400)
            } else {
                mchk2.background = ContextCompat.getDrawable(context, R.drawable.chat_1_selected)
                builder.setItems(item) { dialog, which ->
                    mchk2.background = ContextCompat.getDrawable(context, R.drawable.chat_1)
                    if (item[which] == "คัดลอก") {
                        val text: String = mMessage.text.toString()
                        myClip = ClipData.newPlainText("text", text)
                        myClipboard.setPrimaryClip(myClip!!)
                    }
                }
                builder.setOnDismissListener { mchk2.background = ContextCompat.getDrawable(context, R.drawable.chat_1) }
                alert_dialog = builder.create()
                alert_dialog!!.show()
                alert_dialog!!.window!!.setLayout(800, 390)
            }
            true
        })
        mImage_sent.setOnClickListener(View.OnClickListener {
            val intent = Intent(context, ItemImageActivity::class.java)
            intent.putExtra("matchIdReal", mMatchIdReal.text.toString())
            intent.putExtra("matchId", mMatchId.text.toString())
            intent.putExtra("ChkImage", mChk.text.toString())
            intent.putExtra("ChkImage2", mChk_2.text.toString())
            context.startActivity(intent)
        })
        mImage_sent.setOnLongClickListener(OnLongClickListener {
            if (mchk3.background.constantState === ContextCompat.getDrawable(context, R.drawable.chat_1_photo)!!.constantState) {
                mchk3.background = ContextCompat.getDrawable(context, R.drawable.chat_1_photo_selected)
                builder.setItems(item_image) { dialog, which ->
                    if (item_image[which] == "ดาวน์โหลดภาพ") {
                        ActivityCompat.requestPermissions((context as Activity?)!!, arrayOf<String?>(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
                        mchk3.background = ContextCompat.getDrawable(context, R.drawable.chat_1_photo)
                        val bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.maicar)
                        val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "test.png")
                        var out: FileOutputStream? = null
                        try {
                            out = FileOutputStream(file)
                            Toast.makeText(context, "chk", Toast.LENGTH_SHORT).show()
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                            out.flush()
                            out.close()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }
                builder.setOnDismissListener { mchk3.background = ContextCompat.getDrawable(context, R.drawable.chat_1_photo) }
                alert_dialog = builder.create()
                alert_dialog!!.show()
                alert_dialog!!.window!!.setLayout(800, 245)
            } else {
                mchk3.background = ContextCompat.getDrawable(context, R.drawable.chat_2_photo_selected)
                builder.setItems(item_image) { dialog, which -> mchk3.background = ContextCompat.getDrawable(context, R.drawable.chat_2_photo) }
                builder.setOnDismissListener { mchk3.background = ContextCompat.getDrawable(context, R.drawable.chat_2_photo) }
                alert_dialog = builder.create()
                alert_dialog!!.show()
                alert_dialog!!.window!!.setLayout(800, 245)
            }
            true
        })
    }
}