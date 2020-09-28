package com.jabirdeveloper.tinderswipe

import android.app.PendingIntent
import android.content.Intent
import android.graphics.*
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.jabirdeveloper.tinderswipe.Chat.ChatActivity
import java.io.IOException
import java.net.URL
import java.util.*


class MyFirebaseMessagingService : FirebaseMessagingService() {
    private var message1: String? = null
    private var notificationManager: NotificationManagerCompat? = null
    private var datatype: String? = null
    private var idPlus2: Int = 0

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        Log.d("NotificationMessage", p0)
    }

    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)
        message1 = p0.data.toString()
        datatype = p0.data["data_type"]

        //message2 = p0.data.get("message")
        //message3 = p0.data.get("name_user")
        Log.d("NotificationMessage", datatype.toString())
        Log.d("NotificationMessage", message1.toString())
        //Log.d("NotificationMessage",message2)
        // Log.d("NotificationMessage",message3)
        //Notification_match(p0.data.get("name_user"))
        when (datatype) {
            "direct_message" -> notificationChat(p0.data["message"], p0.data["time"], p0.data["createBy"], p0.data["name_user"], p0.data["url"])
            else -> {
                notificationMatch(p0.data["name_user"])
            }
        }
        //Notification_chat(p0.data.get("message"),p0.data.get("time"),p0.data.get("createBy"),p0.data.get("name_user"),p0.data.get("url"))


    }

    override fun onDeletedMessages() {
    }

    private fun notificationMatch(name: String?) {
        //String name = NameMatch.get(NameMatch.size()-1);
        val intent = Intent(this, SwitchpageActivity::class.java)
        intent.putExtra("accept", "1")
        val random = Random()
        val id = ++idPlus2
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        val pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_ONE_SHOT)
        val notification = NotificationCompat.Builder(this, App.Companion.CHANNEL_ID!!)
                .setSmallIcon(R.drawable.ic_love)
                .setContentTitle(getString(R.string.You_have_a_new_match))
                .setColor(0xFFCC00)
                .setContentText(getString(R.string.you_and) + " " + name + " " + getString(R.string.Matched2))
                .setGroup("Matches")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()
        val sum = NotificationCompat.Builder(this, App.Companion.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_love)
                .setStyle(NotificationCompat.InboxStyle().setBigContentTitle(getString(R.string.Matched)).setSummaryText(getString(R.string.new_matching)))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setGroup("Matches")
                .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_CHILDREN)
                .setAutoCancel(true)
                .setGroupSummary(true)
                .build()
        notificationManager = NotificationManagerCompat.from(this)
        if (id == 2) {
            notificationManager!!.notify(id + random.nextInt(9999 - 1000) + 1000, sum)
        }
        notificationManager!!.notify(id + random.nextInt(9999 - 1000) + 1000, notification)
    }

    private fun getCropBitmap(bitmap: Bitmap): Bitmap? {
        val output = Bitmap.createBitmap(bitmap.width,
                bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val color = -0xbdbdbe
        val paint = Paint()
        val rect = Rect(0, 0, bitmap.width, bitmap.height)
        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        paint.color = color
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle((bitmap.width / 2).toFloat(), (bitmap.height / 2).toFloat(),
                (bitmap.width / 2).toFloat(), paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, rect, rect, paint)
        //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
        //return _bmp;
        return output
    }

    private var idPlus: Int = 0
    private val bitMapTest: MutableList<Bitmap?>? = ArrayList()
    private val idNotification: MutableList<String?>? = ArrayList()
    private val indexNotification: MutableList<Int?>? = ArrayList()
    private fun notificationChat(lastChat: String?, time: String?, ID: String?, Names: String?, Url: String?) {
        var icon: Bitmap? = null
        val name: String
        val intent = Intent(this, ChatActivity::class.java)
        val b = Bundle()
        val random = Random()
        var twoItems = false
        var id = 0
        if (idNotification!!.size == 0) {
            id = ++idPlus
            idNotification.add(ID)
            indexNotification?.add(id)
            try {
                val url = URL(Url)
                val image = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                icon = getCropBitmap(image)
            } catch (e: IOException) {
            e.printStackTrace()
            }
            bitMapTest!!.add(icon)
        } else {
            for (i in idNotification.indices) {
                if (idNotification.elementAt(i) == ID) {
                    twoItems = true
                    id = indexNotification?.get(i)!!
                    icon = bitMapTest!!.elementAt(i)
                }
            }
            if (!twoItems) {
                id = ++idPlus
                Toast.makeText(this, "id :$id", Toast.LENGTH_SHORT).show()
                idNotification.add(ID)
                indexNotification!!.add(id)
                try {
                    val url = URL(Url)
                    val image = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                    icon = getCropBitmap(image)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                bitMapTest!!.add(icon)
            }
        }
        name = Names.toString()
        b.putString("time_chk", time)
        b.putString("matchId", ID)
        b.putString("nameMatch", name)
        b.putString("unread", "-1")
        intent.putExtras(b)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        val pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_ONE_SHOT)
        val notification = NotificationCompat.Builder(this, App.Companion.CHANNEL_ID!!)
                .setSmallIcon(R.drawable.ic_love)
                .setContentTitle(name)
                .setGroup("Chat")
                .setContentText(lastChat)
                .setLargeIcon(icon)
                .setColor(0xFFCC00)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()
        val sum = NotificationCompat.Builder(this, App.Companion.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_love)
                .setStyle(NotificationCompat.InboxStyle().setBigContentTitle(getString(R.string.New_message)).setSummaryText(getString(R.string.You_have_new_message)))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setGroup("Chat")
                .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_CHILDREN)
                .setAutoCancel(true)
                .setGroupSummary(true)
                .build()
        notificationManager = NotificationManagerCompat.from(this)
        if (id == 2) {
            notificationManager!!.notify(id + random.nextInt(9999 - 1000) + 1000, sum)
        }
        notificationManager!!.notify(id, notification)
    }

}