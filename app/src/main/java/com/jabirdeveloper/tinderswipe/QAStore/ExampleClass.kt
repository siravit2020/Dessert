package com.jabirdeveloper.tinderswipe.QAStore

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.functions.FirebaseFunctions
import com.jabirdeveloper.tinderswipe.R


class ExampleClass : AppCompatDialogFragment() {
    var listener: ExampleClassListener? = null
    var radio1: RadioButton? = null
    var radio2: RadioButton? = null
    var questionText: TextView? = null
    var confirmText: TextView? = null
    var dismissText: TextView? = null
    var radioGroup1: RadioGroup? = null
    var radioGroupWeight: RadioGroup? = null
    var question: String = ""
    var Choice: ArrayList<QAObject> = ArrayList()
    private lateinit var functions: FirebaseFunctions

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = Dialog(activity!!)
        var lay = getActivity()!!.getLayoutInflater()
        val view: View = lay.inflate(R.layout.viewpager_questions, null)
        var viewpager: ViewPager2 = view.findViewById(R.id.pagerTest)
        val adapter: QAPagerAdapter = QAPagerAdapter(activity!!, Choice!!, builder, viewpager)
        viewpager.adapter = adapter
        viewpager.isUserInputEnabled = false
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE)
        builder.setContentView(view)
        builder.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        builder.show()
        return builder
    }

    fun setData(Choice: ArrayList<QAObject>) {
        this.Choice = Choice;
    }

    override fun onAttach(context: Context) {
        try {
            listener = context as ExampleClassListener;
        } catch (e: Exception) {
            Log.d("error", e.toString())
        }
        super.onAttach(context)
    }

    interface ExampleClassListener {
        fun applyTexts(choice: Int)
    }
}