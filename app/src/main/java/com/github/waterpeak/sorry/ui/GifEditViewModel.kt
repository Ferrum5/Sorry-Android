package com.github.waterpeak.sorry.ui

import android.content.Context
import android.util.Log
import com.github.waterpeak.sorry.entity.AssEntity
import pl.droidsonroids.gif.GifDrawable
import java.io.*
import android.os.Build
import android.content.Intent
import android.graphics.*
import android.media.*
import android.net.Uri
import android.util.AndroidRuntimeException
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.waterpeak.sorry.AnimatedGifEncoder
import com.github.waterpeak.sorry.getExternalStoragePublicDirectoryPicture
import kotlin.math.min

class GifEditViewModel : ViewModel() {

    companion object {
        const val TAG = "GifEditViewModel"
        const val VERBOSE = true
        const val MAX_FRAMES = 10
    }

    fun createGif(context: Context,
                  mp4: String,
                  outputName: String,
                  inputAss: List<AssEntity>): LiveData<GifProgress> {
        return create3(context, mp4, outputName, inputAss)
    }

    @Deprecated("Old ffmepg implementation")
    private fun create(context: Context,
               mp4: String,
               outputName: String,
               inputAss: List<AssEntity>): LiveData<GifProgress> {
        val live = MutableLiveData<GifProgress>()
        val myDir = File(getExternalStoragePublicDirectoryPicture(), "sorrygif")
        myDir.mkdirs()
        val gifFile = File(myDir, "$outputName.gif")
        if (gifFile.exists()) {
            live.value = GifProgress("File ${gifFile.name} exists, use another one.")
            return live
        }

        Thread {
            val tempPicDir = File(context.cacheDir, "tmp")
            try {
                tempPicDir.deleteRecursively()
                tempPicDir.mkdirs()
                //拷贝模板视频到缓存目录
                val videoFile = File(tempPicDir, "template.mp4")
                videoFile.delete()
                val videoInputStream = context.assets.open(mp4)
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
                //run ffmpeg
//                Controller.getInstance().run(arrayOf(
//                        "ffmpeg",
//                        "-i", videoFile.absolutePath,
//                        "-f", "gif",
//                        tempGif.absolutePath
//                ))

                //逐帧加字幕
                val decoder = GifDrawable(tempGif)
                val paint = Paint(Paint.ANTI_ALIAS_FLAG)
                paint.color = Color.WHITE
                paint.isDither = true // 获取跟清晰的图像采样
                paint.isFilterBitmap = true// 过滤一些
                val frames = decoder.numberOfFrames
                val frameDuration = videoDuration / frames
                val progress = GifProgress(null)
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
                notifySystemMedia(context, gifFile)
                live.postValue(GifProgress(0, 0, true, true, "Success.File path:${gifFile.path}", gif))
            } catch (e: Exception) {
                Log.i("GifCreate", "发生异常", e)
                live.postValue(GifProgress(e.message))
            } finally {
                tempPicDir.deleteRecursively()
            }
        }.start()

        return live
    }

    private fun create3(context: Context,
                mp4: String,
                outputName: String,
                inputAss: List<AssEntity>): LiveData<GifProgress> {
        val live = MutableLiveData<GifProgress>()
        val myDir = File(getExternalStoragePublicDirectoryPicture(), "sorrygif")
        myDir.mkdirs()
        val gifFile = File(myDir, "$outputName.gif")
        if (gifFile.exists()) {
            live.value = GifProgress("File ${gifFile.name} exists, use another one.")
            return live
        }



        Thread {
            var fileOutputStream: FileOutputStream? = null
            var byteArrayOutputStream: ByteArrayOutputStream? = null
            var extractor: MediaExtractor? = null
            var decoder: MediaCodec? = null
            var outputSurface: CodecOutputSurface? = null
            try {
                val fileDescriptor = context.assets.openFd(mp4)

                extractor = MediaExtractor()
                extractor.setDataSource(fileDescriptor.fileDescriptor,
                        fileDescriptor.startOffset,
                        fileDescriptor.length)


                var format: MediaFormat? = null
                var trackIndex = 0
                for (i in 0 until extractor.trackCount) {
                    val format2 = extractor.getTrackFormat(i)
                    val mime = format2.getString(MediaFormat.KEY_MIME)
                    if (mime.startsWith("video/")) {
                        trackIndex = i
                        format = format2
                    }
                }


                if (format == null) {
                    live.postValue(GifProgress("Unsupport video minetype"))
                    return@Thread
                }

                extractor.selectTrack(trackIndex)

                val videoWidth = format.getInteger(MediaFormat.KEY_WIDTH)
                val videoHeight = format.getInteger(MediaFormat.KEY_HEIGHT)
                val videoDuration = format.getLong(MediaFormat.KEY_DURATION)
                val videoFrameRate = format.getInteger(MediaFormat.KEY_FRAME_RATE)
                val frameDuration = 1000 / videoFrameRate
                val totalFrames = (videoDuration / frameDuration / 1000).toInt()

                val gifWidth = min(videoWidth, 640)
                val gifHeight = if (gifWidth == videoWidth) {
                    videoHeight
                } else {
                    gifWidth * videoHeight / videoWidth
                }

                val mime = format.getString(MediaFormat.KEY_MIME);
                decoder = MediaCodec.createDecoderByType(mime);
                outputSurface = CodecOutputSurface(gifWidth, gifHeight)
                decoder.configure(format, outputSurface.surface, null, 0)
                decoder.start()

                //逐帧加字幕
                val paint = Paint(Paint.ANTI_ALIAS_FLAG)
                paint.color = Color.WHITE
                paint.isDither = true // 获取更清晰的图像采样
                paint.isFilterBitmap = true// 过滤一些

                val progress = GifProgress(0, totalFrames)
                val encoder = AnimatedGifEncoder()
                encoder.setRepeat(0)
                encoder.setDelay(frameDuration)
                byteArrayOutputStream = ByteArrayOutputStream()
                encoder.start(byteArrayOutputStream)


                var assIndex = 0
                val firstAss = inputAss[assIndex]
                var startTime: Long = string2Long(firstAss.start)
                var endTime: Long = string2Long(firstAss.end)
                var assText: String = firstAss.ass
                var frameIndex = 0

                val TIMEOUT_USEC = 3000L
                val decoderInputBuffers = decoder.inputBuffers
                val info = MediaCodec.BufferInfo()
                var inputChunk = 0
                var decodeCount = 0
                var frameSaveTime = 0L

                var outputDone = false
                var inputDone = false
                while (!outputDone) {
                    if (VERBOSE) Log.d(TAG, "loop");
                    // Feed more data to the decoder.
                    if (!inputDone) {
                        val inputBufIndex = decoder.dequeueInputBuffer(TIMEOUT_USEC)
                        if (inputBufIndex >= 0) {
                            val inputBuf = decoderInputBuffers[inputBufIndex]
                            // Read the sample data into the ByteBuffer.  This neither respects nor
                            // updates inputBuf's position, limit, etc.
                            val chunkSize = extractor.readSampleData(inputBuf, 0)
                            if (chunkSize < 0) {
                                // End of stream -- send empty frame with EOS flag set.
                                decoder.queueInputBuffer(inputBufIndex, 0, 0, 0L,
                                        MediaCodec.BUFFER_FLAG_END_OF_STREAM)
                                inputDone = true
                                if (VERBOSE) Log.d(TAG, "sent input EOS");
                            } else {
                                if (extractor.getSampleTrackIndex() != trackIndex) {
                                    if (VERBOSE) Log.w(TAG, "WEIRD: got sample from track " +
                                            extractor.getSampleTrackIndex() + ", expected " + trackIndex);
                                }
                                val presentationTimeUs = extractor.sampleTime;
                                decoder.queueInputBuffer(inputBufIndex, 0, chunkSize,
                                        presentationTimeUs, 0 /*flags*/);
                                if (VERBOSE) {
                                    Log.d(TAG, "submitted frame " + inputChunk + " to dec, size=" +
                                            chunkSize);
                                }
                                inputChunk++;
                                extractor.advance();
                            }
                        } else {
                            if (VERBOSE) Log.d(TAG, "input buffer not available")
                        }
                    }

                    if (!outputDone) {
                        val decoderStatus = decoder.dequeueOutputBuffer(info, TIMEOUT_USEC)
                        if (decoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {
                            // no output available yet
                            if (VERBOSE) Log.d(TAG, "no output from decoder available")
                        } else if (decoderStatus == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                            // not important for us, since we're using Surface
                            if (VERBOSE) Log.d(TAG, "decoder output buffers changed")
                        } else if (decoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                            val newFormat = decoder.outputFormat
                            if (VERBOSE) Log.d(TAG, "decoder output format changed: " + newFormat);
                        } else if (decoderStatus < 0) {
                            //fail("unexpected result from decoder.dequeueOutputBuffer: " + decoderStatus);
                        } else { // decoderStatus >= 0
                            if (VERBOSE) Log.d(TAG, "surface decoder given buffer " + decoderStatus +
                                    " (size=" + info.size + ")");
                            if ((info.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                                if (VERBOSE) Log.d(TAG, "output EOS");
                                outputDone = true;
                            }

                            val doRender = info.size != 0

                            // As soon as we call releaseOutputBuffer, the buffer will be forwarded
                            // to SurfaceTexture to convert to a texture.  The API doesn't guarantee
                            // that the texture will be available before the call returns, so we
                            // need to wait for the onFrameAvailable callback to fire.
                            decoder.releaseOutputBuffer(decoderStatus, doRender);
                            if (doRender) {
                                if (VERBOSE) Log.d(TAG, "awaiting decode of frame " + decodeCount);
                                outputSurface.awaitNewImage()
                                outputSurface.drawImage(true)

                                //if (decodeCount < MAX_FRAMES) {

                                val startWhen = System.nanoTime()

                                val bitmap = outputSurface.frameBitmap
                                progress.progress = frameIndex
                                live.postValue(progress)

                                if (assIndex == -1) {
                                    encoder.addFrame(bitmap)
                                    frameIndex++
                                    continue
                                }
                                val frameTime = frameIndex * frameDuration
                                if (frameTime > endTime) {
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
                                if (frameTime < startTime || assIndex == -1) {
                                    encoder.addFrame(bitmap)
                                } else {
                                    encoder.addFrame(drawTextToBitmap(bitmap.copy(Bitmap.Config.RGB_565, true), assText, paint))
                                }
                                bitmap.recycle()
                                frameIndex++

                                frameSaveTime += System.nanoTime() - startWhen
                                //}
                                decodeCount++;
                            }
                        }
                    }
                }

                val numSaved = if (MAX_FRAMES < decodeCount) MAX_FRAMES else decodeCount

                encoder.finish()


                fileOutputStream = FileOutputStream(gifFile)
                byteArrayOutputStream.writeTo(fileOutputStream)
                fileOutputStream.flush()
                progress.progress = totalFrames
                live.postValue(progress)
                val gif = GifDrawable(gifFile).apply { loopCount = 0 }

                //通知系统扫描媒体库
                notifySystemMedia(context, gifFile)
                live.postValue(GifProgress("Success.File path:${gifFile.path}", gif))
            } catch (e: Exception) {
                throw AndroidRuntimeException(e)
                Log.i("GifCreate", "发生异常", e)
                live.postValue(GifProgress(e.message))
            } finally {
                fileOutputStream?.close()
                byteArrayOutputStream?.close()
                extractor?.release()
                decoder?.release()
            }
        }.start()

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