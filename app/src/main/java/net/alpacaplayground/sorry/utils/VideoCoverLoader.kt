package net.alpacaplayground.sorry.utils

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.util.Log
import android.util.LruCache
import android.widget.ImageView
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.collections.HashMap

class VideoCoverLoader private constructor() : LifecycleObserver {
    companion object {
        val instance by lazy {
            VideoCoverLoader()
        }
    }

    val cacheMap: LruCache<String, Bitmap> = LruCache(1024 * 1024 * 32)
    val taskMap: MutableMap<String, LoadTask> = HashMap()
    val imageUrlMap: MutableMap<ImageView, String> = HashMap()
    val executor: ExecutorService = Executors.newFixedThreadPool(3)
    val mmrCache: LinkedList<MediaMetadataRetriever> = LinkedList()

    fun load(file: String?, image: ImageView) {
        Log.i("LoadVideoCover",file?:"null file")
        //已图片为索引清除已有任务
        taskMap[imageUrlMap[image]]?.apply {
            if (file == this.file && image === this.target) {
                return@load
            }
            target = null
            imageUrlMap.remove(image)
        }
        if(file!=null) {
            val urlExistTask: LoadTask? = taskMap[file]
            //任务不为空
            if (urlExistTask != null) {
                urlExistTask.target = image
            } else {
                val bitmap = cacheMap[file]
                if (bitmap != null) {
                    image.setImageBitmap(bitmap)
                    return
                } else {
                    val newTask = LoadTask(image, file)
                    taskMap[file] = newTask
                    imageUrlMap[image] = file
                    executor.submit(newTask)
                }
            }
        }else{
            image.setImageDrawable(null)
        }

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun shutdown() {
        executor.shutdown()
    }

    class LoadTask(var target: ImageView? = null,
                   val file: String,
                   val mmr: MediaMetadataRetriever = if (instance.mmrCache.isNotEmpty()) {
                       instance.mmrCache.pop()
                   } else {
                       MediaMetadataRetriever()
                   }
    ) : Runnable {
        override fun run() {
            try {
                val afd = application.assets.openFd(file)
                mmr.setDataSource(afd.fileDescriptor, afd.startOffset,afd.length)
                mmr.getFrameAtTime(0)!!.toMainThread bitmap@ {
                    with(instance) {
                        cacheMap.put(file, this@bitmap)
                        taskMap.remove(file)
                    }

                    target?.apply {
                        instance.imageUrlMap.remove(this)
                        setImageBitmap(this@bitmap)
                    }
                }
            } catch (e: Exception) {
                Log.i("LoadVideoCover","task failed",e)
            }finally {
                mmr.release()
                instance.mmrCache.add(mmr)
            }

        }
    }
}