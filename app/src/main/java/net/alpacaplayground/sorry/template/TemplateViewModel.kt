package net.alpacaplayground.sorry.template

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.Log
import com.madhavanmalolan.ffmpegandroidlibrary.Controller
import net.alpacaplayground.sorry.entity.AssEntity
import net.alpacaplayground.sorry.utils.*
import pl.droidsonroids.gif.GifDrawable
import java.io.*
import android.graphics.Color
import android.graphics.Paint
import android.media.MediaMetadataRetriever
import android.os.Build
import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import net.alpacaplayground.sorry.getPictureDir


class TemplateViewModel : ViewModel() {

    fun create(application: Context, mp4: String, outputName: String, inputAss: List<AssEntity>): LiveData<GifProgress> {
        val live = MutableLiveData<GifProgress>()
        val myDir = File(getPictureDir(), "sorrygif")
        myDir.mkdirs()
        val gifFile = File(myDir, "$outputName.gif")
        if (gifFile.exists()) {
            live.value = GifProgress(success = false, finished = true, message = "文件已存在，请充新命名")
        } else {
            Thread {
                val tempPicDir = File(application.cacheDir, "tmp")
                try {
                    tempPicDir.deleteRecursively()
                    tempPicDir.mkdirs()
                    //拷贝模板视频到缓存目录
                    val videoFile = File(tempPicDir, "template.mp4")
                    videoFile.delete()
                    val videoInputStream = application.assets.open(mp4)
                    val videoOutputStream = FileOutputStream(videoFile)
                    videoInputStream.copyTo(videoOutputStream)
                    videoInputStream.close()
                    videoOutputStream.close()
                    //获取视频时长
                    val mmr = MediaMetadataRetriever()
                    mmr.setDataSource(videoFile.absolutePath)
                    val videoDuration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION).toLong()
                    mmr.release()
                    //将视频文件转成gif
                    val tempGif = File(tempPicDir, "temp.gif")
                    Controller.getInstance().run(arrayOf(
                            "ffmpeg",
                            "-i", videoFile.absolutePath,
                            "-f", "gif",
                            tempGif.absolutePath
                    ))

                    //逐帧加字幕
                    val decoder = GifDrawable(tempGif)
                    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
                    paint.color = Color.WHITE
                    paint.isDither = true // 获取跟清晰的图像采样
                    paint.isFilterBitmap = true// 过滤一些
                    val frames = decoder.numberOfFrames
                    val frameDuration = videoDuration / frames
                    val progress = GifProgress(0, frames, false, false)
                    val encoder = AnimatedGifEncoder()
                    encoder.setRepeat(0)
                    encoder.setDelay(frameDuration.toInt())
                    val bos = ByteArrayOutputStream()
                    encoder.start(bos)
                    var assIndex = 0
                    val firstAss = inputAss[assIndex]
                    var startTime: Long = string2Long(firstAss.start)
                    var endTime: Long = string2Long(firstAss.end)
                    var assText: String = firstAss.ass
                    for (frame in 0..frames) {
                        progress.progress = frame
                        live.postValue(progress)
                        val bitmap = decoder.seekToFrameAndGet(frame)
                        if (assIndex == -1) {
                            encoder.addFrame(bitmap)
                            continue
                        }
                        val myTime = frame * frameDuration
                        if (myTime > endTime) {
                            assIndex++
                            if (assIndex >= inputAss.size) {
                                assIndex = -1
                            } else {
                                with(inputAss[assIndex]) {
                                    startTime = string2Long(start)
                                    endTime = string2Long(end)
                                    assText = ass
                                }
                            }
                        }
                        if (myTime < startTime || assIndex == -1) {
                            encoder.addFrame(bitmap)
                        } else {
                            encoder.addFrame(drawTextToBitmap(bitmap, assText, paint))
                        }
                    }
                    encoder.finish()

                    decoder.recycle()

                    val fos = FileOutputStream(gifFile)
                    fos.write(bos.toByteArray())
                    fos.close()
                    bos.close()
                    progress.progress = frames
                    live.postValue(progress)
                    val gif = GifDrawable(gifFile).apply { loopCount = Character.MAX_VALUE.toInt() }

                    //通知系统扫描媒体库
                    notifySystemMedia(application, gifFile)
                    live.postValue(GifProgress(0, 0, true, true, "生成成功，文件路径为${gifFile.path}", gif))
                } catch (e: Exception) {
                    Log.i("GifCreate", "发生异常", e)
                    live.postValue(GifProgress(0, 0, false, true, e.message))
                } finally {
                    tempPicDir.deleteRecursively()
                }
            }.start()
        }
        return live
    }

    private fun drawTextToBitmap(bitmap: Bitmap, text: String,
                                 paint: Paint): Bitmap {
        //字号设置为图片高度的1/n
        paint.textSize = bitmap.height / 10.toFloat()
        val canvas = Canvas(bitmap)
        val textWidth = paint.measureText(text)
        val bch = bitmap.width / 2
        val tl = bch - (textWidth / 2)
        val bb = bitmap.height - 5
        canvas.drawText(text, tl, bb.toFloat(), paint)
        //canvas.drawText(text, tl, bb.toFloat(), strokePaint)
        return bitmap
    }

    private fun string2Long(time: String): Long {
        //json时间格式0:00:00.00
        val hour = time.substring(0, 1).toLong()
        val minute = time.substring(2, 4).toLong()
        val second = time.substring(5, 7).toLong()
        val uSecond = time.substring(8).toInt()
        return hour * 3600 * 1000 + minute * 60 * 1000 + second * 1000 + uSecond * 10
    }

    lateinit var connection: MediaScannerConnection

    private fun notifySystemMedia(application: Context, gifFile: File) {
        if (Build.VERSION.SDK_INT <= 19) {
            application.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(gifFile)))
        } else {
            connection = MediaScannerConnection(application, object : MediaScannerConnection.MediaScannerConnectionClient {
                override fun onMediaScannerConnected() {
                    connection.scanFile(gifFile.absolutePath, "image/gif")
                }

                override fun onScanCompleted(path: String?, uri: Uri?) {
                    connection.disconnect()
                }

            })
            connection.connect()
        }
    }


}