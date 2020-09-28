package com.jabirdeveloper.tinderswipe.Matches

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.jabirdeveloper.tinderswipe.Chat.ChatActivity
import com.jabirdeveloper.tinderswipe.R

class HiAdapter(private val Hilist: MutableList<HiObject?>?, private val context: Context) : RecyclerView.Adapter<HiAdapter.Holder?>() {

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.image_Hi)
        private val name: TextView = itemView.findViewById(R.id.name_Hi)
        fun set(position: Int) {
            Glide.with(context).load(Hilist!![position]!!.profileImageUrl).placeholder(R.drawable.tran).into(imageView)
            name.text = Hilist[position]!!.name
            imageView.setOnClickListener(View.OnClickListener {
                val intent = Intent(context, ChatActivity::class.java)
                intent.putExtra("User_opposite", Hilist[position]!!.userId)
                intent.putExtra("Hi", "open")
                intent.putExtra("time_chk", "")
                intent.putExtra("matchId", Hilist[position]!!.userId)
                intent.putExtra("nameMatch", Hilist[position]!!.name)
                intent.putExtra("first_chat", "")
                intent.putExtra("unread", "0")
                intent.putExtra("chat_na", "1")
                context.startActivity(intent)
            })
            imageView.setOnLongClickListener(OnLongClickListener {
                val mBuilder = AlertDialog.Builder(context, R.style.AlertDialog2)
                mBuilder.setTitle("ลบรูปภาพ")
                mBuilder.setMessage("คุณต้องการลบ " + Hilist[position]!!.name + " ออกจากแชทใช่หรือไม่")
                mBuilder.setCancelable(true)
                mBuilder.setPositiveButton(R.string.ok) { _, _ ->
                    val userID: String = FirebaseAuth.getInstance().uid!!
                    val datadelete = FirebaseDatabase.getInstance().reference.child("Users")
                    val datachat = FirebaseDatabase.getInstance().reference
                    datachat.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val chatId: String = dataSnapshot.child("Users").child(userID).child("connection").child("chatna").child(Hilist.elementAt(position)!!.userId!!).value.toString()
                            if (dataSnapshot.child("Chat").hasChild(chatId)) {
                                datachat.child("Chat").child(chatId).removeValue()
                            }
                            datadelete.child(userID).child("connection").child("chatna").child(Hilist!!.elementAt(position)!!.userId!!).removeValue()
                            notifyItemRemoved(position)
                        }

                        override fun onCancelled(databaseError: DatabaseError) {}
                    })
                }
                mBuilder.setNegativeButton(R.string.cancle) { _, _ -> }
                val mDialog = mBuilder.create()
                mDialog.show()
                false
            })
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.hi_item, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.set(position)
    }

    override fun getItemCount(): Int {
        return Hilist!!.size
    }

}