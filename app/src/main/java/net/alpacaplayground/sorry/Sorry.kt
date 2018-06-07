package net.alpacaplayground.sorry

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Looper
import android.os.Handler
import android.support.v4.app.Fragment as V4Fragment
import android.app.Fragment
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Environment
import android.support.annotation.ColorRes
import android.support.v4.app.FragmentActivity
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.View
import android.widget.Toast
import java.io.File
import kotlin.reflect.KClass


private val mainHandler by lazy { Handler(Looper.getMainLooper()) }

fun xRunOnUiThread(delay: Long = 0L, run: () -> Unit) {
    if (0L == delay && isOnMainThread()) {
        run()
    } else {
        mainHandler.postDelayed(run, delay)
    }
}

fun isOnMainThread() = Thread.currentThread() === Looper.getMainLooper().thread


fun Activity.alert(message: String) {
    AlertDialog.Builder(this)
            .setTitle("提示")
            .setMessage(message)
            .setPositiveButton("ok", { d, _ -> d.dismiss() })
            .show()
}

fun Fragment.alert(message: String) {
    activity?.alert(message)
}

fun V4Fragment.alert(message: String) {
    activity?.alert(message)
}

fun <T : Activity> Activity.startActivity(clazz: KClass<T>) {
    startActivity(Intent(this, clazz.java))
}

inline fun <T : Activity> Activity.startActivity(clazz: KClass<T>, intent: Intent.() -> Unit) {
    val intent = Intent(this, clazz.java)
    intent(intent)
    startActivity(intent)
}

inline fun <reified T : ViewModel> FragmentActivity.getViewModel(): T {
    return ViewModelProviders.of(this).get(T::class.java)
}


fun Context.toast(message: String) {
    val toast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
    toast.setGravity(Gravity.CENTER, 0, 0)
    toast.show()
}

fun Activity.setActionBarTitle(title: String, showBack: Boolean = true) {
    if ((this as? AppCompatActivity)?.supportActionBar?.apply {
                this@setActionBarTitle.title = title
                setDisplayHomeAsUpEnabled(showBack)
            } === null) {
        actionBar?.apply {
            this@setActionBarTitle.title = title
            setDisplayHomeAsUpEnabled(showBack)
        }
    }
}

fun getPictureDir(): File {
    return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
}

fun Context.color(@ColorRes id: Int): Int = ResourcesCompat.getColor(resources, id, null)
fun View.color(@ColorRes id: Int): Int = context.color(id)