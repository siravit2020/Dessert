package com.jabirdeveloper.tinderswipe.Chat

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jabirdeveloper.tinderswipe.R

class ChatAdapter(private val chatList: MutableList<ChatObject?>?, private val context: Context?) : RecyclerView.Adapter<ChatViewHolders?>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolders {
        val layoutView = LayoutInflater.from(parent.context).inflate(R.layout.item_chat, null, false)
        val lp = RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        layoutView.layoutParams = lp
        return ChatViewHolders(layoutView, context)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ChatViewHolders, position: Int) {
        holder.mchk3.visibility = View.GONE
        holder.mchk2.visibility = View.GONE
        holder.mchk.visibility = View.GONE
        holder.mImage_opposite.visibility = View.GONE
        holder.mMatchIdReal.text = chatList!![position]!!.createByUser
        holder.mMatchId.text = chatList[position]!!.Match_id
        holder.mChk_2.text = "" + chatList[position]!!.chk
        holder.mChk.text = "" + chatList[chatList.size - 1]!!.chk
        holder.time_send.text = chatList[position]!!.time
        if (chatList[position]!!.currentUser!!) {
            if (chatList[position]!!.url !== "default") {
                holder.mImage_opposite.visibility = View.GONE
                holder.mchk3.visibility = View.VISIBLE
                holder.mchk.visibility = View.VISIBLE
                val params3 = holder.mchk.layoutParams as RelativeLayout.LayoutParams
                params3.addRule(RelativeLayout.END_OF, 0)
                params3.addRule(RelativeLayout.START_OF, holder.mchk3.id)
                params3.addRule(RelativeLayout.ALIGN_BOTTOM, holder.mchk3.id)
                val paramsimg = holder.mchk3.layoutParams as RelativeLayout.LayoutParams
                paramsimg.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE)
                holder.mchk3.background = ContextCompat.getDrawable(context!!, R.drawable.chat_1_photo)!!
                Glide.with(context).load(chatList[position]!!.url).into(holder.mImage_sent)
                holder.mchk2.visibility = View.GONE
            } else if (chatList[position]!!.audio_Url != "null") {
                holder.button_audio.visibility = View.VISIBLE
                holder.CD!!.visibility = View.VISIBLE
                holder.begin_audio.visibility = View.VISIBLE
                if (Integer.valueOf(chatList[position]!!.audio_length!!) < 60) {
                    holder.audio_url.text = chatList.get(position)!!.audio_Url
                    val second = String.format("%02d", Integer.valueOf(chatList[position]!!.audio_length!!))
                    holder.mMessage.text = "00:$second"
                } else {
                    val minute = String.format("%02d", Integer.valueOf(chatList[position]!!.audio_length!!) / 60)
                    val second = String.format("%02d", Integer.valueOf(chatList[position]!!.audio_length!!) % 60)
                    holder.mMessage.text = "$minute:$second"
                }
                holder.begin_audio.setTextColor(Color.parseColor("#FFFFFF"))
                holder.mMessage.setTextColor(Color.parseColor("#FFFFFF"))
                holder.mchk3.visibility = View.GONE
                holder.mchk.visibility = View.VISIBLE
                holder.mchk2.visibility = View.VISIBLE
                val params2 = holder.mchk2.layoutParams as RelativeLayout.LayoutParams
                params2.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE)
                val params3 = holder.mchk.layoutParams as RelativeLayout.LayoutParams
                params3.addRule(RelativeLayout.END_OF, 0)
                params3.addRule(RelativeLayout.START_OF, holder.mchk2.id)
                params3.addRule(RelativeLayout.ALIGN_BOTTOM, holder.mchk2.id)
                holder.mImage_opposite.visibility = View.GONE
                holder.mchk2.background = ContextCompat.getDrawable(context!!, R.drawable.chat_1)
            } else {
                /*if(chatList.get(position).getMessage().length() > 4){
                    if(!chatList.get(position).getMessage().equals("audio"+chatList.get(position).getUserIId())) {
                        holder.mMessage.setText(chatList.get(position).getMessage());
                    }else {
                        holder.button_audio.setVisibility(View.VISIBLE);
                        holder.CD.setVisibility(View.VISIBLE);
                        holder.begin_audio.setVisibility(View.VISIBLE);
                        if(Integer.valueOf(chatList.get(position).getAudio_length()) < 60) {
                            holder.audio_url.setText(chatList.get(position).getAudio_Url());
                            String second = String.format("%02d", Integer.valueOf(chatList.get(position).getAudio_length()));
                            holder.mMessage.setText("00:" +second);
                        }else{
                            String minute =String.format("%02d",Integer.valueOf(chatList.get(position).getAudio_length())/60);
                            String second =String.format("%02d",Integer.valueOf(chatList.get(position).getAudio_length())%60);
                            holder.mMessage.setText(minute+":" +second);
                        }
                    }
                }else{*/
                holder.mMessage.text = chatList[position]!!.message
                //}
                holder.button_audio.visibility = View.GONE
                holder.CD!!.visibility = View.GONE
                holder.begin_audio.visibility = View.GONE
                //holder.begin_audio.setTextColor(Color.parseColor("#FFFFFF"));
                holder.mMessage.setTextColor(Color.parseColor("#FFFFFF"))
                holder.mchk3.visibility = View.GONE
                holder.mchk.visibility = View.VISIBLE
                holder.mchk2.visibility = View.VISIBLE
                val params2 = holder.mchk2.layoutParams as RelativeLayout.LayoutParams
                params2.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE)
                val params3 = holder.mchk.layoutParams as RelativeLayout.LayoutParams
                params3.addRule(RelativeLayout.END_OF, 0)
                params3.addRule(RelativeLayout.START_OF, holder.mchk2.id)
                params3.addRule(RelativeLayout.ALIGN_BOTTOM, holder.mchk2.id)
                holder.mImage_opposite.visibility = View.GONE
                holder.mchk2.background = context?.let { ContextCompat.getDrawable(it, R.drawable.chat_1) }
            }
        } else {
            if (chatList[position]!!.url !== "default") {
                holder.mchk3.visibility = View.VISIBLE
                holder.mchk.visibility = View.VISIBLE
                val params = holder.mchk.layoutParams as RelativeLayout.LayoutParams
                params.addRule(RelativeLayout.START_OF, 0)
                params.addRule(RelativeLayout.END_OF, holder.mchk3.id)
                params.addRule(RelativeLayout.ALIGN_BOTTOM, holder.mchk3.id)
                val paramsimg = holder.mchk3.layoutParams as RelativeLayout.LayoutParams
                paramsimg.addRule(RelativeLayout.ALIGN_PARENT_END, 0)
                holder.mImage_opposite.visibility = View.VISIBLE
                Glide.with(context!!).load(chatList[position]!!.profileImageUrl).into(holder.mImage_opposite)
                Glide.with(context).load(chatList[position]!!.url).into(holder.mImage_sent)
                holder.mchk3.background = ContextCompat.getDrawable(context, R.drawable.chat_2_photo)
                holder.mchk2.visibility = View.GONE
            } else if (chatList[position]!!.audio_Url != "null") {
                holder.button_audio.visibility = View.VISIBLE
                holder.CD!!.visibility = View.VISIBLE
                holder.begin_audio.visibility = View.VISIBLE
                if (Integer.valueOf(chatList[position]!!.audio_length!!) < 60) {
                    holder.audio_url.text = chatList[position]!!.audio_Url
                    val second = String.format("%02d", Integer.valueOf(chatList[position]!!.audio_length!!))
                    holder.mMessage.text = "00:$second"
                } else {
                    val minute = String.format("%02d", Integer.valueOf(chatList[position]!!.audio_length!!) / 60)
                    val second = String.format("%02d", Integer.valueOf(chatList[position]!!.audio_length!!) % 60)
                    holder.mMessage.text = "$minute:$second"
                }
                holder.begin_audio.setTextColor(Color.parseColor("#292929"))
                holder.mMessage.setTextColor(Color.parseColor("#292929"))
                holder.mchk3.visibility = View.GONE
                holder.mchk.visibility = View.VISIBLE
                holder.mchk2.visibility = View.VISIBLE
                val params4 = holder.mchk2.layoutParams as RelativeLayout.LayoutParams
                params4.addRule(RelativeLayout.ALIGN_PARENT_END, 0)
                val params = holder.mchk.layoutParams as RelativeLayout.LayoutParams
                params.addRule(RelativeLayout.START_OF, 0)
                params.addRule(RelativeLayout.END_OF, holder.mchk2.id)
                params.addRule(RelativeLayout.ALIGN_BOTTOM, holder.mchk2.id)
                holder.mchk2.background = ContextCompat.getDrawable(context!!, R.drawable.chat_2)!!
                holder.mImage_opposite.visibility = View.VISIBLE
                Glide.with(context).load(chatList[position]!!.profileImageUrl).into(holder.mImage_opposite)
            } else {
                /*if(chatList.get(position).getMessage().length() > 4){
                    if(!chatList.get(position).getMessage().equals("audio"+chatList.get(position).getCreateByUser())) {
                        holder.mMessage.setText(chatList.get(position).getMessage());
                    }else {
                        holder.button_audio.setVisibility(View.VISIBLE);
                        holder.CD.setVisibility(View.VISIBLE);
                        holder.begin_audio.setVisibility(View.VISIBLE);
                        if(Integer.valueOf(chatList.get(position).getAudio_length()) < 60) {
                            holder.audio_url.setText(chatList.get(position).getAudio_Url());
                            String second = String.format("%02d", Integer.valueOf(chatList.get(position).getAudio_length()));
                            holder.mMessage.setText("00:" +second);
                        }else{
                            String minute =String.format("%02d",Integer.valueOf(chatList.get(position).getAudio_length())/60);
                            String second =String.format("%02d",Integer.valueOf(chatList.get(position).getAudio_length())%60);
                            holder.mMessage.setText(minute+":" +second);
                        }
                    }
                }else{
                    holder.mMessage.setText(chatList.get(position).getMessage());
                }*/
                //holder.begin_audio.setTextColor(Color.parseColor("#292929"));
                holder.mMessage.text = chatList[position]!!.message
                holder.button_audio.visibility = View.GONE
                holder.CD!!.visibility = View.GONE
                holder.begin_audio.visibility = View.GONE
                holder.mMessage.setTextColor(Color.parseColor("#292929"))
                holder.mchk3.visibility = View.GONE
                holder.mchk.visibility = View.VISIBLE
                holder.mchk2.visibility = View.VISIBLE
                val params4 = holder.mchk2.layoutParams as RelativeLayout.LayoutParams
                params4.addRule(RelativeLayout.ALIGN_PARENT_END, 0)
                val params = holder.mchk.layoutParams as RelativeLayout.LayoutParams
                params.addRule(RelativeLayout.START_OF, 0)
                params.addRule(RelativeLayout.END_OF, holder.mchk2.id)
                params.addRule(RelativeLayout.ALIGN_BOTTOM, holder.mchk2.id)
                holder.mchk2.background = ContextCompat.getDrawable(context!!, R.drawable.chat_2)
                holder.mImage_opposite.visibility = View.VISIBLE
                Glide.with(context).load(chatList[position]!!.profileImageUrl).into(holder.mImage_opposite)
            }
        }
    }

    override fun getItemCount(): Int {
        return chatList!!.size
    }

}