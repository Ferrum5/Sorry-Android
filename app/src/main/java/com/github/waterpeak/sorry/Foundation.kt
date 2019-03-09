package com.github.waterpeak.sorry

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.view.ViewManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.*
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import kotlin.reflect.KClass
import org.jetbrains.anko.custom.ankoView

fun <T : Activity> Activity.startActivity(clazz: KClass<T>) {
    startActivity(Intent(this, clazz.java))
}

inline fun <T : Activity> Activity.startActivity(clazz: KClass<T>, intent: (intent: Intent) -> Unit) {
    startActivity(Intent(this, clazz.java).apply(intent))
}

fun Context.toast(message: String) {
    val toast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
    toast.setGravity(Gravity.CENTER, 0, 0)
    toast.show()
}
fun getExternalStoragePublicDirectoryPicture(): File {
    return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
}
private val mainHandler by lazy { Handler(Looper.getMainLooper()) }

fun runOnUiThread(delay: Long = 0L, run: () -> Unit) {
    if (0L == delay && isMainThread()) {
        run()
    } else {
        mainHandler.postDelayed(run, delay)
    }
}

fun isMainThread() = Thread.currentThread() === Looper.getMainLooper().thread

inline fun ViewManager.recyclerView(init: RecyclerView.()->Unit): RecyclerView{
    return ankoView(::RecyclerView,0, init)
}

inline fun Context.recyclerView(init: RecyclerView.()->Unit): RecyclerView{
    return ankoView(::RecyclerView,0, init)
}

inline fun <reified T: ViewModel> FragmentActivity.lazyModel(): Lazy<T>{
    return lazy { ViewModelProviders.of(this).get(T::class.java) }
}

inline fun <T> LiveData<T>.observe(owner: LifecycleOwner, crossinline observer: (T?)->Unit){
    observe(owner, Observer { observer(it)})
}

inline fun <T> LiveData<T>.observeNonNull(owner: LifecycleOwner, crossinline observer: (T)->Unit){
    observe(owner, Observer { if(it!=null){observer(it)} })
}

fun Context.color(colorId: Int): Int{
    return ResourcesCompat.getColor(resources,colorId,theme)
}

fun View.color(colorId: Int): Int{
    return context.color(colorId)
}

abstract class BaseActivity : AppCompatActivity(), ActivityCompat.OnRequestPermissionsResultCallback {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    val String.PERMISSION_GRANTED: Boolean
        get() = ActivityCompat.checkSelfPermission(this@BaseActivity, this@PERMISSION_GRANTED) == PackageManager.PERMISSION_GRANTED

    override fun onSupportNavigateUp(): Boolean {
        return onNavigateUp()
    }

    override fun onNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}