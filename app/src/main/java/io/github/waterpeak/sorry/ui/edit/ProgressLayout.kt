package io.github.waterpeak.sorry.ui.edit

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.core.content.ContextCompat.getColor
import io.github.waterpeak.sorry.ui.edit.GifProgress

class ProgressLayout : LinearLayout {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    @TargetApi(21)
    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    init {
        setWillNotDraw(false)
    }

    private val paint = Paint()

    var progress: Int = 100
        set(value) {
            field = value
            invalidate()
        }

    var total: Int = 100

    private val colorEnable = getColor(context, android.R.color.holo_blue_dark)
    private val colorDisable = getColor(context, android.R.color.darker_gray)



    fun setProgressAndTotal(p: GifProgress) {
        this.total = p.total
        this.progress = p.progress
    }

    fun reset() {
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