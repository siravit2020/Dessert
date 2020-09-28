package com.jabirdeveloper.tinderswipe.Functions

import android.annotation.SuppressLint
import android.app.Activity

import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.jabirdeveloper.tinderswipe.R
import com.tapadoo.alerter.Alerter
import java.text.SimpleDateFormat
import java.util.*

class ReportUser(private var context: Activity, private var matchId: String) {
    private var i = 0;
    private var usersDb = FirebaseDatabase.getInstance().reference.child("Users")
    private var currentUserId = FirebaseAuth.getInstance().uid!!
    fun reportDialog(): AlertDialog {

        val choice = context.resources.getStringArray(R.array.report_item)
        val checkedItem = BooleanArray(choice.size)
        val mBuilder = androidx.appcompat.app.AlertDialog.Builder(context)
        mBuilder.setTitle(R.string.dialog_reportUser)
        mBuilder.setMultiChoiceItems(R.array.report_item, checkedItem) { _, position, isChecked ->
            checkedItem[position] = isChecked
        }
        mBuilder.setCancelable(true)
        mBuilder.setPositiveButton(R.string.ok) { _, _ ->
            i = 0
            while (i < choice.size) {
                val checked = checkedItem[i]
                if (checked) {
                    update(i.toString())
                }
                i++
            }
        }
        mBuilder.setNegativeButton(R.string.cancle) { _, _ -> }
        val mDialog = mBuilder.create()
        mDialog.window!!.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.myrect2))

        return mDialog
    }

    private fun update(Child: String) {
        usersDb.addListenerForSingleValueEvent(object : ValueEventListener {
            @SuppressLint("SimpleDateFormat")
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                var dateBefore = true

                if (dataSnapshot.child(currentUserId).child("PutReportId").hasChild(matchId)) {
                    dateBefore = false
                } else {
                    val dateUser: String
                    val currentDate = SimpleDateFormat("dd/MM/yyyy")
                    val calendar = Calendar.getInstance()
                    dateUser = currentDate.format(calendar.time)
                    val ff = hashMapOf<String, Any>()
                    ff["date"] = dateUser
                    usersDb.child(currentUserId).child("PutReportId").child(matchId).updateChildren(ff)
                }
                Log.d("test_boolean", "$dateBefore , $matchId")
                if (dateBefore) {

                    Alerter.create(context)
                            .setTitle(context.getString(R.string.report_suc))
                            .setText(context.getString(R.string.report_suc2))
                            .setBackgroundColorInt(ContextCompat.getColor(context, R.color.c2))
                            .setIcon(ContextCompat.getDrawable(context, R.drawable.ic_check)!!)
                            .show()
                    if (!dataSnapshot.child(matchId).hasChild("Report")) {
                        val jj = hashMapOf<String, Any>()
                        jj[Child] = "1"
                        usersDb.child(matchId).child("Report").updateChildren(jj)
                    } else if (dataSnapshot.child(matchId).hasChild("Report")) {
                        if (dataSnapshot.child(matchId).child("Report").hasChild(Child)) {
                            val countRep = Integer.valueOf(dataSnapshot.child(matchId).child("Report").child(Child).value.toString()) + 1
                            val inputCount = countRep.toString()
                            val jj = hashMapOf<String, Any>()
                            jj[Child] = inputCount
                            usersDb.child(matchId).child("Report").updateChildren(jj)
                        } else {
                            val jj = hashMapOf<String, Any>()
                            jj[Child] = "1"
                            usersDb.child(matchId).child("Report").updateChildren(jj)
                        }
                    }
                } else {

                    Alerter.create(context)
                            .setTitle(context.getString(R.string.report_failed))
                            .setText(context.getString(R.string.report_fail))
                            .setBackgroundColorInt(ContextCompat.getColor(context, R.color.c1))
                            .setIcon(ContextCompat.getDrawable(context, R.drawable.ic_do_not_disturb_black_24dp)!!)
                            .show()
                    val builder = AlertDialog.Builder(context)
                    val view = LayoutInflater.from(context).inflate(R.layout.alert_dialog, null)
                    val title = view.findViewById<View?>(R.id.title_alert) as TextView
                    val li = view.findViewById<View?>(R.id.linear_alert) as LinearLayout
                    val icon = view.findViewById<View?>(R.id.icon_alert) as ImageView
                    val message = view.findViewById<View?>(R.id.message_alert) as TextView
                    val dis = view.findViewById<View?>(R.id.dis_alert) as TextView
                    val yes = view.findViewById<View?>(R.id.yes_alert) as TextView
                    yes.setText(R.string.report_close)
                    li.gravity = Gravity.CENTER
                    dis.visibility = View.GONE
                    title.setText(R.string.report_alert)
                    message.setText(R.string.report_reset)
                    icon.background = ContextCompat.getDrawable(context, R.drawable.ic_warning_black_24dp)
                    builder.setView(view)
                    val mDialog = builder.show()
                    yes.setOnClickListener { mDialog.dismiss() }

                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }
}