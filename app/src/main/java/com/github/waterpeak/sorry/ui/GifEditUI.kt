package com.github.waterpeak.sorry.ui

import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import org.jetbrains.anko.*
import org.jetbrains.anko.custom.ankoView
import org.json.JSONArray

class GifEditUI : AnkoComponent<GifEditActivity> {

    lateinit var imageTop: ImageView
    lateinit var progressLayout: ProgressLayout
    lateinit var progressText: TextView
    lateinit var scrollContent: LinearLayout
    lateinit var edits: Array<AssItem>
    lateinit var editFileName: EditText

    override fun createView(ui: AnkoContext<GifEditActivity>): View {
        return ui.run {
            verticalLayout {
                imageTop = imageView().lparams(width = MATCH_PARENT, height = dip(160))
                scrollView {
                    scrollContent = verticalLayout {
                        fileNameEdit()
                    }.lparams(width = MATCH_PARENT, height = WRAP_CONTENT) {
                        horizontalMargin = dip(5)
                    }
                }.lparams(MATCH_PARENT,0,1f)
                progressLayout = ankoView(::ProgressLayout,0) {
                    progressText = textView("生成") {
                        textSize = 18f
                        textColor = Color.WHITE
                        gravity = Gravity.CENTER
                    }.lparams(width = MATCH_PARENT, height = MATCH_PARENT)
                }.lparams(width = MATCH_PARENT, height = dip(50))
            }
        }
    }

    fun setAssEdits(array: JSONArray) {
        val length = array.length()
        with(scrollContent as _LinearLayout) {
            edits = Array(length) {
                val jsonItem = array.getJSONObject(it)
                val prompt = "第${it + 1}条字幕"
                val editText = editText {
                    hint = jsonItem.optString("hint", prompt)
                    gravity = Gravity.CENTER_VERTICAL
                    setSingleLine()
                    imeOptions = if (it != length - 1) EditorInfo.IME_ACTION_NEXT else EditorInfo.IME_ACTION_DONE
                    //fixme
                    setText(hint)
                }.lparams(width = MATCH_PARENT, height = dip(50)) {
                    horizontalMargin = dip(5)
                }
                AssItem(jsonItem.getString("start"),
                        jsonItem.getString("end"),
                        editText, prompt)
            }
        }
    }

    private fun _LinearLayout.fileNameEdit() = linearLayout {
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        textView("生成文件名：")
        editFileName = editText {
            gravity = Gravity.CENTER_VERTICAL
            hint = "请输入"
            setSingleLine()
            inputType = EditorInfo.TYPE_CLASS_TEXT
            imeOptions = EditorInfo.IME_ACTION_NEXT
        }.lparams(width = 0, height = MATCH_PARENT, weight = 1f)
        textView(".gif")
    }.lparams(width = MATCH_PARENT, height = dip(50))

}