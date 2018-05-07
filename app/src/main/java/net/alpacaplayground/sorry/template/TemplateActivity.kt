package net.alpacaplayground.sorry.template

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.activity_template.*
import net.alpacaplayground.sorry.R
import net.alpacaplayground.sorry.base.BaseActivity
import net.alpacaplayground.sorry.entity.AssEntity
import net.alpacaplayground.sorry.utils.*
import org.json.JSONObject
import org.json.JSONTokener

class TemplateActivity : BaseActivity() {

    lateinit var edits: Array<AssItem>
    lateinit var file: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_template)
        val json = JSONTokener(intent.getStringExtra("json")).nextValue() as JSONObject
        setActionBarTitle(json.getString("name"))
        file = intent.getStringExtra("file")
        VideoCoverLoader.instance.load(file, imageTemplate)
        var array = json.getJSONArray("ass")
        edits = Array(array.length()) {
            val jsonItem = array.getJSONObject(it)
            val editText = EditText(this)
            val prompt = "第${it + 1}条字幕"
            editText.hint = jsonItem.optString("hint", null) ?: prompt
            editText.gravity = Gravity.CENTER_VERTICAL
            editText.setSingleLine()
            editText.imeOptions = EditorInfo.IME_ACTION_NEXT
            linearTemplate.addView(editText, LinearLayout.LayoutParams(MATCH, dip2Px(50)).apply {
                leftMargin = dip2Px(5)
                rightMargin = leftMargin
            })

            AssItem(jsonItem.getString("start"),
                    jsonItem.getString("end"),
                    editText, prompt)
        }

        var model: TemplateViewModel = getViewModel()
        model.liveGif.observe(this, Observer {
            Log.i("CreateGif", "set animation drawable for iamgeview")
            it?.apply {
                imageTemplate.setImageDrawable(this)
            }
        })
        model.liveProgress.observe(this, Observer {
            it?.apply {
                if (progress != total) {
                    textTemplateCreate.text = "正在处理第${progress}帧，共$total"
                    customerTemplateProgress.setProgressAndTotal(this)
                } else {
                    textTemplateCreate.text = "生成"
                    textTemplateCreate.isClickable = true
                    customerTemplateProgress.resetProgress()
                }
            }
        })
        model.liveMessage.observe(this, Observer {
            it?.apply {
                alert(it)
                textTemplateCreate.text = "生成"
                textTemplateCreate.isClickable = true
                customerTemplateProgress.resetProgress()
            }
        })
        textTemplateCreate.setOnClickListener {
            try {
                customerTemplateProgress.progress = 0
                customerTemplateProgress.invalidate()
                textTemplateCreate.text = "正在生成"
                textTemplateCreate.isClickable = false
                val outputName = editTemplateGifFileName.text.toString()
                if (outputName.isEmpty()) {
                    editTemplateGifFileName.requestFocus()
                    throw AssNotAllFillException("请输入输出文件名")
                }
                model.create(file, outputName, edits.map {
                    val input = it.editText.text.toString()
                    if (input.isEmpty()) {
                        it.editText.requestFocus()
                        throw AssNotAllFillException("请输入${it.prompt}")
                    }
                    AssEntity(it.startTime, it.endTime, input)
                })
            } catch (e: AssNotAllFillException) {
                alert(e.message)
                customerTemplateProgress.resetProgress()
                customerTemplateProgress.invalidate()
                textTemplateCreate.text = "生成"
                textTemplateCreate.isClickable = true
            }
        }
    }

    class AssItem(val startTime: String, val endTime: String, val editText: EditText, val prompt: String)

    class AssNotAllFillException(override val message: String) : Exception(message)
}