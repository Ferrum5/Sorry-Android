package net.alpacaplayground.sorry.template

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.widget.FrameLayout
import net.alpacaplayground.sorry.utils.getColor

class ProgressFrameLayout(context: Context,
                          attrs: AttributeSet?,
                          defStyleAttr: Int) : FrameLayout(context, attrs, defStyleAttr) {
    constructor(context: Context,
                attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context) : this(context, null)

    val paint = Paint()
    var progress: Int = 100
    var total: Int = 100
    private val colorEnable = getColor(android.R.color.holo_blue_dark)
    private val colorDisable = getColor(android.R.color.darker_gray)

    init {
        setWillNotDraw(false)
    }

    fun setProgressAndTotal(p: GifProgress) {
        this.progress = p.progress
        this.total = p.total
        invalidate()
    }

    fun resetProgress(){
        progress = total
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        when (progress) {
            0 -> canvas.drawColor(colorDisable)
            total -> canvas.drawColor(colorEnable)
            else -> {
                paint.strokeWidth =height.toFloat()
                val y =paint.strokeWidth / 2
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