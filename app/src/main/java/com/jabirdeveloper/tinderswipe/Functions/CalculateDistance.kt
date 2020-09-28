package com.jabirdeveloper.tinderswipe.Functions

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

object CalculateDistance {
    fun calculate(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val lonlon = Math.toRadians(lon2 - lon1)
        val latlat = Math.toRadians(lat2 - lat1)
        val lat1r = Math.toRadians(lat1)
        val lat2r = Math.toRadians(lat2)
        val R = 6371.0
        val a = sin(latlat / 2) * sin(latlat / 2) + cos(lat1r) * cos(lat2r) * sin(lonlon / 2) * sin(lonlon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return R * c
    }
}