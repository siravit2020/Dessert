package com.jabirdeveloper.tinderswipe.Listcard

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.View.OnLongClickListener
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.jabirdeveloper.tinderswipe.Functions.DateTime
import com.jabirdeveloper.tinderswipe.Functions.ReportUser
import com.jabirdeveloper.tinderswipe.ProfileUserOppositeActivity2
import com.jabirdeveloper.tinderswipe.R
import java.util.*

class ListCardViewHolders(itemView: View, private val context: Context) : RecyclerView.ViewHolder(itemView) {
    var mMatchId: TextView = itemView.findViewById(R.id.id)
    var mMatchName: TextView = itemView.findViewById(R.id.Matches_name)
    var mDistance: TextView = itemView.findViewById(R.id.distance_text)
    var mStatus: TextView = itemView.findViewById(R.id.status_time)
    var tag: TextView = itemView.findViewById(R.id.tagkm)
    var percent:TextView = itemView.findViewById(R.id.Latest_chat)
    var myself: TextView = itemView.findViewById(R.id.myself)
    var mMatchImage: ImageView = itemView.findViewById(R.id.Match_Image)
    var onOffList: ImageView = itemView.findViewById(R.id.on_off_matches)
    private var mLinear: LinearLayout = itemView.findViewById(R.id.dd_22)
    var viewOnline: LinearLayout = itemView.findViewById(R.id.view_online)
    private var seeDB: DatabaseReference? = null
    private val userID: String = FirebaseAuth.getInstance().uid!!
    private var i = 0
    var progressBar: ProgressBar? = itemView.findViewById(R.id.progress_image)
    init {
        mLinear.setOnLongClickListener(OnLongClickListener {
            mLinear.background = ContextCompat.getDrawable(context, R.drawable.background_click_tran)
            showDialog()
            true
        })
        mLinear.setOnClickListener(View.OnClickListener {
            seeDB = FirebaseDatabase.getInstance().reference.child("Users").child(mMatchId.text.toString()).child("see_profile").child(userID)
            val d = DateTime
            val newDate: MutableMap<String?, Any?> = HashMap()
            newDate["date"] = d.date()
            newDate["time"] = d.time()
            seeDB!!.updateChildren(newDate)
            val intent = Intent(context, ProfileUserOppositeActivity2::class.java)
            //val activityOptions: ActivityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(context as Activity,mMatchImage, ViewCompat.getTransitionName(mMatchImage).toString())
            intent.putExtra("User_opposite", mMatchId.text.toString())
            intent.putExtra("form_list", mMatchId.text.toString())
            context.startActivity(intent,/*activityOptions.toBundle()*/)
        })
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    fun showDialog() {
        val dd = PopupMenu(context, tag)
        dd.menuInflater.inflate(R.menu.popup_listmenu, dd.menu)
        dd.gravity = Gravity.START
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            dd.setForceShowIcon(true)
        }
        dd.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.menu_unmatch2) {
                val mDialog = ReportUser(context as Activity, mMatchId.text.toString()).reportDialog()
                mDialog.show()
            }
            mLinear.background = ContextCompat.getDrawable(context, R.drawable.background_click)
            true
        }
        dd.setOnDismissListener { mLinear.background = ContextCompat.getDrawable(context, R.drawable.background_click) }
        dd.show()
    }

}