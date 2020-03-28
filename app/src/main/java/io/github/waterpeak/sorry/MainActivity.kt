package io.github.waterpeak.sorry

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.os.Bundle
import android.widget.ImageView
import io.github.waterpeak.sorry.ui.list.TemplateListActivity
import pub.devrel.easypermissions.*


class MainActivity : BaseActivity(),EasyPermissions.PermissionCallbacks  {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ImageView(this).apply {
            scaleType = ImageView.ScaleType.CENTER_CROP
        })
        EasyPermissions.requestPermissions(
            PermissionRequest.Builder(this, 1, WRITE_EXTERNAL_STORAGE)
                .setRationale("外置存储权限用于存储生成的GIF文件")
                .setPositiveButtonText("允许")
                .setNegativeButtonText("拒绝")
                .build()
        )
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        finish()
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        jump()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    private fun jump() {
        runOnUiThread(1000) {
            startActivity<TemplateListActivity>()
            finish()
        }
    }
}