package com.jabirdeveloper.tinderswipe

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager

class VipSlide(var context: Context, private var List: ArrayList<PagerModel?>) : PagerAdapter() {
    private var inflater: LayoutInflater? = (context as Activity?)!!.layoutInflater
    override fun getCount(): Int {
        return List.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = inflater!!.inflate(R.layout.vip_page1, container, false)
        val tv = view.findViewById<TextView>(R.id.text)
        val textView = view.findViewById<TextView>(R.id.text_second)
        val imageView = view.findViewById<ImageView>(R.id.image_vip)
        view.tag = position
        (container as ViewPager).addView(view)
        val model = List[position]!!
        tv.text = model.text
        textView.text = model.title
        imageView.setImageDrawable(ContextCompat.getDrawable(context, model.image))
        return view
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as View
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        (container as ViewPager).removeView(`object` as View)
    }

}