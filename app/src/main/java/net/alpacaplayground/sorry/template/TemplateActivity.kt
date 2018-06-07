package net.alpacaplayground.sorry.template

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.LinearLayout
import net.alpacaplayground.sorry.R
import net.alpacaplayground.sorry.alert
import net.alpacaplayground.sorry.base.BaseActivity
import net.alpacaplayground.sorry.entity.AssEntity
import net.alpacaplayground.sorry.getViewModel
import net.alpacaplayground.sorry.setActionBarTitle
import net.alpacaplayground.sorry.utils.*
import org.jetbrains.anko.setContentView
import org.jetbrains.anko.verticalLayout
import org.json.JSONObject
import org.json.JSONTokener


class TemplateActivity : BaseActivity() {

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
            alert("出错了")
            return@onCreate
        }
        setActionBarTitle(json.getString("name"))

        val ui = TemplateUi()
        ui.setContentView(this)

        val file = intent.getStringExtra("file")
        VideoCoverLoader.load(file, ui.imageTop)

        val array = json.getJSONArray("ass")
        ui.setAssEdits(array)

        val model: TemplateViewModel = getViewModel()

        ui.progressLayout.setOnClickListener {
            with(it as ProgressFrameLayout) {
                try {
                    isClickable = false
                    progress = 0
                    ui.progressText.text = "正在生成"
                    val outputName = ui.editFileName.text.toString()
                    if (outputName.isEmpty()) {
                        ui.editFileName.requestFocus()
                        throw AssNotAllFillException("请输入输出文件名")
                    }
                    model.create(it.context.applicationContext, file, outputName, ui.edits.map {
                        val input = it.editText.text.toString()
                        if (input.isEmpty()) {
                            it.editText.requestFocus()
                            throw AssNotAllFillException("请输入${it.prompt}")
                        }
                        AssEntity(it.startTime, it.endTime, input)
                    }).observe(this@TemplateActivity, Observer {
                        if (it != null) {
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
                    })
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