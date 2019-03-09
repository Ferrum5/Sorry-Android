package com.github.waterpeak.sorry.ui

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.os.Bundle
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import com.github.waterpeak.sorry.*
import org.jetbrains.anko.*


class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(frameLayout {
            imageView(R.mipmap.background_main){
                scaleType = ImageView.ScaleType.CENTER_CROP
            }.lparams(width = MATCH_PARENT,height = MATCH_PARENT)
        })
        if (!WRITE_EXTERNAL_STORAGE.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(WRITE_EXTERNAL_STORAGE), 1)
        } else {
            jump()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (WRITE_EXTERNAL_STORAGE.PERMISSION_GRANTED) {
            jump()
        } else {
            alert("请允许读写外部存储权限以储存生成的gif文件")
        }
    }

    private fun jump() {
        runOnUiThread(1000) {
            startActivity(TemplateListActivity::class)
            finish()
        }
    }
}