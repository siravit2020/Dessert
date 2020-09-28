package com.jabirdeveloper.tinderswipe.QAStore

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.jabirdeveloper.tinderswipe.R

class QAPagerAdapter(val context: Context, val choice: ArrayList<QAObject>, val dialog: Dialog, val viewpager: ViewPager2) : RecyclerView.Adapter<QAPagerAdapter.Holder?>() {
    private val hashMapQA: HashMap<String, Map<*, *>> = HashMap()
    val userId = FirebaseAuth.getInstance().currentUser!!.uid

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val radioGroupChoice: RadioGroup = itemView.findViewById(R.id.radioGroup_QA)
        val radioGroupChoiceWeight: RadioGroup = itemView.findViewById(R.id.radioGroup_QAWeight)
        val choice1: RadioButton = itemView.findViewById(R.id.radioButton_QA1)
        val choice2: RadioButton = itemView.findViewById(R.id.radioButton_QA2)
        val questions: TextView = itemView.findViewById(R.id.message_QA)
        val valPage: TextView = itemView.findViewById(R.id.page_QA)
        val confirmButton: TextView = itemView.findViewById(R.id.QA_confirm)
        val dismissButton: TextView = itemView.findViewById(R.id.QA_dismiss)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val inflater = (context as Activity).layoutInflater
        return Holder(inflater!!.inflate(R.layout.question_dialog, parent, false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.valPage.hint = "${position + 1} / $itemCount"
        holder.questions.text = choice[position].questions
        holder.choice1.text = choice[position].choice[0]
        holder.choice2.text = choice[position].choice[1]
        when (position) {
            0 -> {
                holder.confirmButton.text = context.getString(R.string.next_QA)
                holder.dismissButton.text = context.getString(R.string.dismiss_label)
                holder.dismissButton.setOnClickListener {
                    dialog.dismiss()
                }
            }
            itemCount - 1 -> {
                holder.confirmButton.text = context.getString(R.string.ok_QA)
                holder.dismissButton.text = context.getString(R.string.previous_QA)
                holder.dismissButton.setOnClickListener {
                    viewpager.setCurrentItem(--viewpager.currentItem, false)
                }
            }
            else -> {
                holder.confirmButton.text = context.getString(R.string.next_QA)
                holder.dismissButton.text = context.getString(R.string.previous_QA)
                holder.dismissButton.setOnClickListener {
                    viewpager.setCurrentItem(--viewpager.currentItem, false)
                }
            }
        }
        holder.confirmButton.setOnClickListener {
            var answerWeight: Int = 0
            var answerQA: Int = 0
            val chk1 = holder.radioGroupChoice.checkedRadioButtonId
            val chk2 = holder.radioGroupChoiceWeight.checkedRadioButtonId
            if (chk1 == -1 || chk2 == -1) {
                Toast.makeText(context, "กรุณาเลือกคำตอบและตอบให้ครบถ้วน", Toast.LENGTH_SHORT).show()
            } else {
                when (chk1) {
                    R.id.radioButton_QA1 -> answerQA = 1
                    R.id.radioButton_QA2 -> answerQA = 0
                }
                when (chk2) {
                    R.id.radioButton_QAWeight1 -> answerWeight = 1
                    R.id.radioButton_QAWeight2 -> answerWeight = 10
                    R.id.radioButton_QAWeight3 -> answerWeight = 100
                    R.id.radioButton_QAWeight4 -> answerWeight = 150
                    R.id.radioButton_QAWeight5 -> answerWeight = 250
                }
                val inputMap = mapOf("question" to answerQA, "weight" to answerWeight)
                hashMapQA.put("question${position + 1}", inputMap as Map<*, *>)
                Log.d("Check_IsCheck", hashMapQA.toString())
                viewpager.setCurrentItem(++viewpager.currentItem, false)
                if (position == itemCount - 1) {
                    FirebaseDatabase.getInstance().reference.child("Users").child(userId).child("Questions").setValue(hashMapQA)
                    dialog.dismiss()
                }
            }

        }

    }

    override fun getItemCount(): Int {
        return choice.size
    }
}