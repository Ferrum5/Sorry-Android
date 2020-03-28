package io.github.waterpeak.sorry

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import java.io.File

inline fun <reified T : Activity> Activity.startActivity() {
    startActivity(Intent(this, T::class.java))
}

fun Context.dip(dp: Float): Int{
    return (dp * resources.displayMetrics.density).toInt()
}
fun View.dip(dp: Float): Int{
    return context.dip(dp)
}
inline fun <reified T : Activity> Activity.startActivity(intent: (intent: Intent) -> Unit) {
    startActivity(Intent(this, T::class.java).apply(intent))
}

fun getExternalStoragePublicDirectoryPicture(context: Context): File {
    return context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
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

abstract class BaseActivity : AppCompatActivity(), ActivityCompat.OnRequestPermissionsResultCallback {

    override fun onSupportNavigateUp(): Boolean {
        return onNavigateUp()
    }

    override fun onNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}

fun RecyclerView.setOnItemClickListener(onItemClickListener: (index: Int)->Unit){
    val gestureDetector = GestureDetector(context, object: GestureDetector.SimpleOnGestureListener(){
        override fun onSingleTapUp(e: MotionEvent?): Boolean {
            if(e==null){
                return false
            }
            val childView = findChildViewUnder(e.x, e.y);
            if(childView != null){
                onItemClickListener(getChildLayoutPosition(childView));
                return true;
            }
            return false
        }
    })
    addOnItemTouchListener(object :RecyclerView.OnItemTouchListener{
        override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {

        }

        override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
           return gestureDetector.onTouchEvent(e)
        }

        override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {

        }

    })
}