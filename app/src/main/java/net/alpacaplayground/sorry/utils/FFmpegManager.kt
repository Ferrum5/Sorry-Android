//package net.alpacaplayground.sorry.utils
//
//import android.util.Log
//import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler
//import com.github.hiteshsondhi88.libffmpeg.FFmpeg
//import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler
//import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException
//import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException
//
//class FFmpegManager private constructor() {
//    companion object {
//        val instance by lazy { FFmpegManager() }
//    }
//
//    lateinit var ffmpeg: FFmpeg
//
//    var busy = false
//
//    fun init(callback: (String?) -> Unit) {
//        if (! ::ffmpeg.isInitialized) {
//            ffmpeg = FFmpeg.getInstance(application)
//        }
//        try {
//            ffmpeg.loadBinary(object : LoadBinaryResponseHandler() {
//
//                override fun onFailure() {
//                    callback("FFmpeg加载失败")
//                }
//
//                override fun onSuccess() {
//                    callback(null)
//                }
//            })
//        }catch (e: FFmpegNotSupportedException){
//            callback("不支持FFmpeg")
//        }
//    }
//
//    fun execute(cmd: Array<String>, callback: (String?) -> Unit){
//        if(busy){
//            callback("ffmpeg正在转码，请稍后再试")
//            return
//        }
//        busy = true
//        try{
//            ffmpeg.execute(cmd, object: ExecuteBinaryResponseHandler(){
//                override fun onFinish() {
//                    busy = false
//                }
//                override fun onSuccess(message: String?) {
//                    Log.i("FFmpeg","success:$message")
//                    callback(null)
//                }
//
//                override fun onFailure(message: String?) {
//                    Log.i("FFmpeg","failed:$message")
//                    callback(message?:"未知错误")
//                }
//            })
//        }catch (e: FFmpegCommandAlreadyRunningException){
//            busy = false
//            callback("FFmpeg正在执行其他操作，请稍后再试")
//        }
//    }
//
//}