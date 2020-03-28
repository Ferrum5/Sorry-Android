package io.github.waterpeak.sorry.ui.edit

import android.graphics.drawable.Drawable

data class GifProgress(var progress: Int = 0,
                       var total: Int = 0,
                       var success: Boolean,
                       var finished: Boolean ,
                       var message: String?,
                       var gif: Drawable? ){
    constructor(message: String?):this(0,0,false,true,message,null)
    constructor(progress: Int, total: Int):this(progress, total,
            false,false,null,null)

    constructor(message: String, gif: Drawable):this(0,0,true,true,message,gif)
}