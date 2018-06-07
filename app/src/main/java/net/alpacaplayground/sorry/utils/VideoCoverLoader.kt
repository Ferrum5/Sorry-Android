package net.alpacaplayground.sorry.utils

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.os.SystemClock
import android.util.Log
import android.util.LruCache
import android.widget.ImageView
import net.alpacaplayground.sorry.xRunOnUiThread
import java.lang.ref.Reference
import java.lang.ref.WeakReference
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.collections.HashMap

object VideoCoverLoader : LifecycleObserver {

    lateinit var context: Context

    private val cacheMap: LruCache<String, Bitmap> = LruCache(1024 * 1024 * 32)
    private val taskMap: MutableMap<String, Reference<ImageView>> = HashMap()
    private val executor: ExecutorService = Executors.newFixedThreadPool(3)
    private val mmrCache: LinkedList<MediaMetadataRetriever> = LinkedList()

    fun load(file: String?, image: ImageView) {
        if (file == null) {
            image.setImageBitmap(null)
        } else {
            if (!::context.isInitialized) {
                context = image.context.applicationContext
            }
            val bitmap = cacheMap[file]
            if (bitmap != null) {
                image.setImageBitmap(bitmap)
            } else {
                taskMap.put(file, WeakReference(image))
                var mmr = if (mmrCache.isNotEmpty()) {
                    mmrCache.pop()
                } else {
                    MediaMetadataRetriever()
                }
                executor.submit {
                    try {
                        val afd = context.assets.openFd(file)
                        mmr.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                        val bitmap = mmr.getFrameAtTime(0)
                        xRunOnUiThread {
                            if (bitmap != null) {
                                cacheMap.put(file, bitmap)
                            }
                            taskMap.remove(file)?.get()?.setImageBitmap(bitmap)
                        }
                    } catch (e: Exception) {
                        Log.i("LoadVideoCover", "task failed", e)
                    } finally {
                        mmr.release()
                        xRunOnUiThread { mmrCache.add(mmr) }
                    }
                }
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun shutdown() {
        executor.shutdown()
    }
}