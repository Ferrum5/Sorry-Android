package com.github.waterpeak.sorry.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import com.github.waterpeak.sorry.color
import org.jetbrains.anko.*

class ProgressLayout(context: Context) : _LinearLayout(context) {

    private val paint = Paint()

    var progress: Int = 100
        set(value) {
            field = value
            invalidate()
        }

    var total: Int = 100

    private val colorEnable = color(android.R.color.holo_blue_dark)
    private val colorDisable = color(android.R.color.darker_gray)

    init {
        setWillNotDraw(false)
    }

    fun setProgressAndTotal(p: GifProgress) {
        this.total = p.total
        this.progress = p.progress
    }

    fun resetProgress() {
        progress = total
    }

    override fun onDraw(canvas: Canvas) {
        when (progress) {
            0 -> canvas.drawColor(colorDisable)
            total -> canvas.drawColor(colorEnable)
            else -> {
                paint.strokeWidth = height.toFloat()
                val y = paint.strokeWidth / 2
                //分界点
                val x = (progress.toFloat()) / total * width
                //进行进度
                paint.color = colorEnable
                canvas.drawLine(0f, y, x, y, paint)
                //灰色进度
                paint.color = colorDisable
                canvas.drawLine(x, y, width.toFloat(), y, paint)
            }
        }
    }
}