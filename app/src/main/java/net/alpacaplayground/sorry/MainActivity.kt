package net.alpacaplayground.sorry

import android.Manifest
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.support.v4.content.ContextCompat
import android.view.WindowManager
import android.widget.ImageView
import net.alpacaplayground.sorry.base.BaseActivity
import net.alpacaplayground.sorry.templatelist.TemplateListActivity
import net.alpacaplayground.sorry.utils.*


class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val imageView = ImageView(this)
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        imageView.setImageResource(R.mipmap.background_main)
        setContentView(imageView, WindowManager.LayoutParams(MATCH, MATCH))
        //修改标题
        setActionBarTitle("Sorry, 有钱就是可以大晒")
        val startLoadTime = SystemClock.elapsedRealtime()
        if (Build.VERSION.SDK_INT >= 23) {
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
                requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE),1)
                return
            }
        }
        jump()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            alert("请允许读写外部存储权限，已储存生成的gif文件")
        }else{
            jump()
        }
    }

    private fun jump(){
        runOnUiThreadDelay(1000) {
            startActivity(TemplateListActivity::class)
            finish()
        }
    }
}