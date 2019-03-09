package com.github.waterpeak.sorry.ui

import android.os.Bundle
import com.github.waterpeak.sorry.BaseActivity
import com.github.waterpeak.sorry.VideoCoverLoader
import com.github.waterpeak.sorry.entity.AssEntity
import com.github.waterpeak.sorry.lazyModel
import com.github.waterpeak.sorry.observeNonNull
import org.jetbrains.anko.alert
import org.jetbrains.anko.setContentView
import org.json.JSONObject
import org.json.JSONTokener


class GifEditActivity : BaseActivity() {

    private val model: GifEditViewModel by lazyModel()

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState((outState ?: Bundle()).apply {
            putString("json", intent.getStringExtra("json"))
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val json = try {
            JSONTokener(intent.getStringExtra("json")
                    ?: savedInstanceState?.getString("json")).nextValue() as JSONObject
        } catch (e: Exception) {
            alert("Json parse failed:${e.message}")
            return
        }
        title = json.getString("name")

        val ui = GifEditUI()
        ui.setContentView(this)

        val file = intent.getStringExtra("file")
        VideoCoverLoader.loadFromCache(file, ui.imageTop)

        val array = json.getJSONArray("ass")
        ui.setAssEdits(array)

        ui.progressLayout.setOnClickListener {
            with(it as ProgressLayout) {
                try {
                    isClickable = false
                    progress = 0
                    ui.progressText.text = "正在生成"
                    val outputName = ui.editFileName.text.toString()
                    if (outputName.isEmpty()) {
                        ui.editFileName.requestFocus()
                        throw AssNotAllFillException("请输入输出文件名")
                    }
                    model.createGif(applicationContext, file, outputName, ui.edits.map {
                        val input = it.editText.text.toString()
                        if (input.isEmpty()) {
                            it.editText.requestFocus()
                            throw AssNotAllFillException("请输入${it.prompt}")
                        }
                        AssEntity(it.startTime, it.endTime, input)
                    }).observeNonNull(this@GifEditActivity) {
                        if (it.finished) {
                            ui.progressText.text = "生成"
                            resetProgress()
                            isClickable = true
                            if (it.success) {
                                val gif = it.gif
                                if (gif != null) {
                                    ui.imageTop.setImageDrawable(gif)
                                }
                            } else {
                                alert(it.message ?: "出错了")
                            }
                        } else {
                            ui.progressText.text = "正在处理第${it.progress}帧，共${it.total}"
                            setProgressAndTotal(it)
                        }
                    }
                } catch (e: AssNotAllFillException) {
                    alert(e.message)
                    ui.progressText.text = "生成"
                    resetProgress()
                    isClickable = true
                }
            }
        }
    }


    private class AssNotAllFillException(override val message: String) : Exception(message)
}