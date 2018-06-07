package net.alpacaplayground.sorry.base

import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager

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