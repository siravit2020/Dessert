package com.jabirdeveloper.tinderswipe.Functions

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.util.Log
import java.io.IOException
import java.util.*

class City(private var language: String,private var context:Context,private var x:Double,private var y:Double) {

    private var ff: Geocoder? = null
    fun getCity(): String? {
        ff = if (language == "th") {
            Geocoder(context)
        } else {
            Geocoder(context, Locale.UK)
        }
        var addresses: MutableList<Address?>? = null
        try {
            addresses = ff!!.getFromLocation(x, y, 1)
            return addresses[0]!!.adminArea

        } catch (e: IOException) {
            e.printStackTrace()
        }
        return "null"
    }



}