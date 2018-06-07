package net.alpacaplayground.sorry.template

import android.graphics.drawable.Drawable

data class GifProgress(var progress: Int = 0,
                       var total: Int = 0,
                       var success: Boolean = false,
                       var finished: Boolean = false,
                       var message: String? = null,
                       var gif: Drawable? = null)