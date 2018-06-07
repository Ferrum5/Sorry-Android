package net.alpacaplayground.sorry

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.ImageView
import net.alpacaplayground.sorry.base.BaseActivity
import net.alpacaplayground.sorry.templatelist.TemplateListActivity
import org.jetbrains.anko.frameLayout
import org.jetbrains.anko.imageView


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
            alert("请允许读写外部存储权限以储存生成的gif文件")
        } else {
            jump()
        }
    }

    private fun jump() {
        xRunOnUiThread(1000) {
            startActivity(TemplateListActivity::class)
            finish()
        }
    }
}