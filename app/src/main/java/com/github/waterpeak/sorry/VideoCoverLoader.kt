package com.github.waterpeak.sorry

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.util.Log
import android.util.LruCache
import android.widget.ImageView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import org.jetbrains.anko.imageBitmap
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class VideoCoverLoader(private val context: Context) {

    companion object {
        private val cacheMap: LruCache<String, Bitmap> = LruCache(1024 * 1024 * 32)
        fun loadFromCache(file: String, image: ImageView){
            image.imageBitmap = cacheMap[file]
        }
    }


    private var hasShutdown =false

    private val taskMap: MutableMap<String, ImageView> = mutableMapOf()
    private val executor: ExecutorService = Executors.newCachedThreadPool()

    private fun removeImage(file: String?, image:ImageView){
        val it = taskMap.iterator()
        while(it.hasNext()){
            val entry = it.next()
            if(entry.key == file || entry.value === image){
                it.remove()
            }
        }
    }

    fun load(file: String?, image: ImageView) {
        if(hasShutdown){
            image.imageBitmap = null
            return
        }

        if (file == null) {
            removeImage(file, image)
            image.setImageBitmap(null)
            return
        }
        val bitmap = cacheMap[file]
        if (bitmap != null) {
            removeImage(file, image)
            image.setImageBitmap(bitmap)
            return
        }

        val taskImage = taskMap[file]
        if (taskImage != null) {
            if (taskImage !== image) {
                taskMap[file] = image
            }
            return
        } else {
            removeImage(null, image)
        }
        taskMap[file] = image
        executor.submit {
            var retriever: MediaMetadataRetriever? = null
            try {
                retriever = MediaMetadataRetriever()
                val fileDescriptor = context.assets.openFd(file)
                retriever.setDataSource(fileDescriptor.fileDescriptor,
                        fileDescriptor.startOffset,
                        fileDescriptor.length)
                val retrieveBitmap = retriever.frameAtTime
                runOnUiThread {
                    if (retrieveBitmap != null) {
                        cacheMap.put(file, retrieveBitmap)
                    }
                    taskMap.remove(file)?.imageBitmap = retrieveBitmap
                }
            } catch (e: Exception) {
                Log.i("LoadVideoCover", "task failed", e)
            } finally {
                retriever?.release()
                runOnUiThread {
                    val taskImage = taskMap[file]
                    if (taskImage === image) {
                        taskMap.remove(file)
                    }
                }
            }
        }
    }

    fun shutdown() {
        hasShutdown = true
        executor.shutdown()
        taskMap.clear()
    }
}