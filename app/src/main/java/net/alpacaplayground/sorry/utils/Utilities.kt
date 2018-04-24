package net.alpacaplayground.sorry.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Looper
import net.alpacaplayground.sorry.SorryApplication
import android.os.Handler
import android.support.v4.app.Fragment as V4Fragment
import android.app.Fragment
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.os.Environment
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AppCompatActivity
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Toast
import java.io.File
import kotlin.reflect.KClass

val application: SorryApplication
    get() = SorryApplication.application

const val MATCH = ViewGroup.LayoutParams.MATCH_PARENT
const val WRAP = ViewGroup.LayoutParams.WRAP_CONTENT

private val mainHandler = Handler(Looper.getMainLooper())

fun runOnUiThread(runnable: () -> Unit) {
    mainHandler.post(runnable)
}

fun runOnUiThreadDelay(delay: Long, runnable: () -> Unit) {
    mainHandler.postDelayed(runnable, delay)
}

fun <T> T.toMainThread(block: T.() -> Unit){
    mainHandler.post{block()}
}

fun <T> T.callLiveData(liveData: MutableLiveData<T>){
    liveData.postValue(this)
}

@SuppressLint
fun getColor(colorId: Int): Int {
    return if (Build.VERSION.SDK_INT >= 23) {
        application.resources.getColor(colorId, application.theme)
    } else {
        application.resources.getColor(colorId)
    }
}

fun getDimensionPixelSize(dimensId: Int): Int {
    return application.resources.getDimensionPixelSize(dimensId)
}

fun dip2Px(dip: Int): Int {
    return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, dip.toFloat(), application.resources.displayMetrics)
            .toInt()
}

fun Activity.alert(message: String) {
    AlertDialog.Builder(this)
            .setTitle("提示")
            .setMessage(message)
            .setPositiveButton("ok", { d, _ -> d.dismiss() })
            .show()
}

fun Fragment.alert(message: String){
    activity?.alert(message)
}

fun V4Fragment.alert(message: String){
    activity?.alert(message)
}

fun <T : Activity> Activity.startActivity(clazz :KClass<T>){
    startActivity(Intent(this,clazz.java))
}

inline fun <T : Activity> Activity.startActivity(clazz :KClass<T>, handler: Intent.()->Unit){
    val intent = Intent(this,clazz.java)
    handler(intent)
    startActivity(intent)
}
@SuppressLint
fun Activity.showLoading(){

}

inline fun <reified T : ViewModel> FragmentActivity.getViewModel() : T{
    return ViewModelProviders.of(this).get(T::class.java)
}


fun toast(message: String){
    val toast = Toast.makeText(application,message,Toast.LENGTH_SHORT)
    toast.setGravity(Gravity.CENTER,0,0)
    toast.show()
}

fun AppCompatActivity.setActionBarTitle(title: String){
    val supportActionBar = supportActionBar
    if(supportActionBar!=null){
        supportActionBar.title = title
    }else{
        actionBar?.title = title
    }
}

fun getPictureDir(): File{
    return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
}