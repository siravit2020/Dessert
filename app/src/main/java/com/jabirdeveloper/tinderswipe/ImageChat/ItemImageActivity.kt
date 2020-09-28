package com.jabirdeveloper.tinderswipe.ImageChat

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.jabirdeveloper.tinderswipe.R
import java.util.*

class ItemImageActivity : AppCompatActivity() {
    var viewPager: ViewPager? = null
    private lateinit var mRecyclerview: RecyclerView
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var adapter: RecyclerView.Adapter<*>
    private var countImg = 0
    private var count = 0
    private var countReal: Int? = 0
    private lateinit var mLinearView: LinearLayout
    private lateinit var mLinearRe: LinearLayout
    private lateinit var screenAdapterImage: ScreenAdapterImage
    private lateinit var Nest: NestedScrollView
    private var chk1time = false
    private lateinit var allImageClick: Button
    private lateinit var mCountImg: TextView
    private lateinit var setDate: TextView
    private lateinit var nameSender: TextView
    private var findImage: DatabaseReference? = null
    private var mDatabaseName: DatabaseReference? = null
    private var currentUid: String? = null
    private var matchId: String? = null
    private var url: String? = null
    private var nameUser: String? = ""
    private var nameMatch: String? = null
    private var matchIdReal: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_image)
        matchId = intent.extras?.getString("matchId")
        matchIdReal = intent.extras?.getString("matchIdReal")
        val countSt = intent.extras!!.getString("ChkImage")
        val countSt2 = intent.extras!!.getString("ChkImage2")
        count = Integer.valueOf(countSt.toString())
        allImageClick = findViewById(R.id.button_all_image)
        countReal = Integer.valueOf(countSt2.toString())
        nameSender = findViewById(R.id.name_sender_image)
        currentUid = FirebaseAuth.getInstance().uid
        viewPager = findViewById(R.id.page_image_all)
        screenAdapterImage = ScreenAdapterImage(this@ItemImageActivity, getDataSetImage())
        mDatabaseName = FirebaseDatabase.getInstance().reference.child("Users")
        findImage = FirebaseDatabase.getInstance().reference.child("Chat").child(matchId.toString())
        mCountImg = findViewById(R.id.count_image)
        setDate = findViewById(R.id.image_date_front)
        Nest = findViewById(R.id.nest_scroll)
        mLinearView = findViewById(R.id.linear_viewpager)
        mLinearRe = findViewById(R.id.linear_recycler_image)
        mCountImg.text = ("$countReal/$count")
        mRecyclerview = findViewById(R.id.recyclerView_image)
        mRecyclerview.isNestedScrollingEnabled = false
        mRecyclerview.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(this)
        mRecyclerview.layoutManager = layoutManager
        adapter = ImageAllAdapter(getDataSetImage(), this)
        allImageClick.setOnClickListener(View.OnClickListener {
            allImageClick.visibility = View.GONE
            mLinearView.visibility = View.GONE
            mCountImg.visibility = View.GONE
            nameSender.visibility = View.GONE
            setDate.visibility = View.GONE
            mLinearRe.visibility = View.VISIBLE
            mRecyclerview.adapter = adapter
            Nest.post(Runnable { Nest.fullScroll(View.FOCUS_DOWN) })
        })
        getName()
        chk1time = true
        viewPager!!.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                if (position == 0 && chk1time) {
                    chk1time = if (resultImage!![position]!!.create) {
                        nameSender.text = nameUser
                        setDate.text = (resultImage[position]!!.date + " " + resultImage[position]!!.time)
                        false
                    } else {
                        nameSender.text = nameMatch
                        setDate.text = (resultImage[position]!!.date + " " + resultImage[position]!!.time)
                        false
                    }
                }
            }

            override fun onPageSelected(position: Int) {
                val dd = count.toString()
                mCountImg.text = ((position + 1).toString() + "/" + dd)
                setDate.hint = resultImage!![position]!!.date + " " + resultImage[position]!!.time
                if (resultImage.elementAt(position)!!.create) {
                    nameSender.text = nameUser
                } else {
                    nameSender.text = nameMatch
                }
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
    }

    private fun getName() {
        mDatabaseName!!.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.hasChild(currentUid.toString())) {
                    nameUser = dataSnapshot.child(currentUid.toString()).child("name").value.toString()
                }
                if (dataSnapshot.hasChild(matchIdReal.toString())) {
                    nameMatch = dataSnapshot.child(matchIdReal.toString()).child("name").value.toString()
                }
                findImage()
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun findImage() {
        findImage!!.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                if (dataSnapshot.child("image").value != null) {
                    url = dataSnapshot.child("image").value.toString()
                    val date = dataSnapshot.child("date").value.toString()
                    val time = dataSnapshot.child("time").value.toString()
                    val create = dataSnapshot.child("createByUser").value.toString()
                    var checkUser = false
                    if (create == currentUid) {
                        checkUser = true
                    }
                    val dd = ScreenObject(url, date, time, checkUser, matchId, matchIdReal)
                    resultImage!!.add(dd)
                    ++countImg
                }
                if (count == countImg) {
                    adapter.notifyDataSetChanged()
                    screenAdapterImage.notifyDataSetChanged()
                    viewPager?.adapter = screenAdapterImage
                    mRecyclerview.scrollToPosition(count / 3 - 1)
                    viewPager?.currentItem = countReal!! - 1
                    viewPager?.visibility = View.VISIBLE
                }
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private val resultImage: ArrayList<ScreenObject?>? = ArrayList()
    private fun getDataSetImage(): MutableList<ScreenObject?>? {
        return resultImage
    }
}