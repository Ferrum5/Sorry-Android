package net.alpacaplayground.sorry.template

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.ViewManager
import android.widget.FrameLayout
import net.alpacaplayground.sorry.color
import org.jetbrains.anko._LinearLayout
import org.jetbrains.anko.custom.ankoView

fun ViewManager.progressFramelayout(init: ProgressFrameLayout.() -> Unit): ProgressFrameLayout {
    return ankoView({ ProgressFrameLayout(it) }, 0, init)
}

class ProgressFrameLayout(context: Context) : _LinearLayout(context) {

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