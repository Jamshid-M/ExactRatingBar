package uz.jamshid.library

import android.graphics.PointF

class Line(val startX: Float, val startY: Float, val endX: Float, val endY: Float) {

    fun startPoint() = PointF(startX, startY)
    fun endPoint() = PointF(endX, endY)
}