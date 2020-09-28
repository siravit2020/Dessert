package com.jabirdeveloper.tinderswipe.Functions

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.jabirdeveloper.tinderswipe.R

class LoadingDialog(private var context: Context) {
    fun dialog(): Dialog {
        val dialog = Dialog(context)
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.progress_dialog, null)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(view)
        dialog.setCancelable(false)
        val width = (context.resources.displayMetrics.widthPixels * 0.80).toInt()
        dialog.window?.setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT)
        return dialog
    }
}