package com.example.ticketgeneratorproject.additionalClasses

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class DashedLineView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint()

    init {
        paint.color = Color.BLACK
        paint.strokeWidth = 10f
        paint.pathEffect = android.graphics.DashPathEffect(floatArrayOf(35f, 25f), 40f)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerY = height / 2f
        canvas.drawLine(0f, centerY, width.toFloat(), centerY, paint)
    }
}
