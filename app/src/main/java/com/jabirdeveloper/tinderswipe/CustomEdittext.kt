package com.jabirdeveloper.tinderswipe

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import android.widget.EditText

@SuppressLint("AppCompatCustomView")
class CustomEdittext : EditText {
    private lateinit var mOnImeBack: EditTextImeBackListener

    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {}

    override fun onKeyPreIme(keyCode: Int, event: KeyEvent): Boolean {
        if (event.keyCode == KeyEvent.KEYCODE_BACK &&
                event.action == KeyEvent.ACTION_UP) {
            mOnImeBack.onImeBack(this, this.text.toString())
        }
        return super.dispatchKeyEvent(event)
    }

    fun setOnEditTextImeBackListener(listener: EditTextImeBackListener?) {
        mOnImeBack = listener!!
    }
}