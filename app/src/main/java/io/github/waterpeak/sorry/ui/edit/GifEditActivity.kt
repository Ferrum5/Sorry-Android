package io.github.waterpeak.sorry.ui.edit

import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import io.github.waterpeak.sorry.BaseActivity
import io.github.waterpeak.sorry.R
import io.github.waterpeak.sorry.dip
import io.github.waterpeak.sorry.entity.AssEntity
import kotlinx.android.synthetic.main.activity_gif_edit.*
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener


class GifEditActivity : BaseActivity() {

    private val model by lazy{ViewModelProvider(this).get(GifEditViewModel::class.java)}
    lateinit var edits: Array<AssItem>

    private fun alert(message:String){
        AlertDialog.Builder(this)
            .setTitle("Warn")
            .setMessage(message)
            .show()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState((outState).apply {
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
        setContentView(R.layout.activity_gif_edit)

        val file = intent.getStringExtra("file")
        if(file==null){
            alert("未选择文件")
            return
        }
        Glide.with(this as FragmentActivity).load("file:///android_asset/$file").into(topImage)

        val array = json.getJSONArray("ass")
        setAssEdits(array)

        progress.setOnClickListener {
            try{
                progress.isClickable = false
                progress.progress = 0
                progressText.text = "正在生成"
                val outputName = fileName.text.toString()
                if (outputName.isEmpty()) {
                    fileName.requestFocus()
                    throw Exception("请输入输出文件名")
                }
                model.createGif(applicationContext, file, outputName, edits.map {
                    val input = it.editText.text.toString()
                    if (input.isEmpty()) {
                        it.editText.requestFocus()
                        throw Exception("请输入${it.prompt}")
                    }
                    AssEntity(
                        it.startTime,
                        it.endTime,
                        input
                    )
                }).observe(this, Observer {
                    if (it.finished) {
                        progressText.text = "生成"
                        progress.reset()
                        progress.isClickable = true
                        if (it.success) {
                            val gif = it.gif
                            if (gif != null) {
                                topImage.setImageDrawable(gif)
                            }
                            alert(it.message?:"Finished")
                        } else {
                            alert(it.message ?: "Error")
                        }
                    } else {
                        progressText.text = "In progress, ${it.progress}/${it.total}"
                        progress.setProgressAndTotal(it)
                    }
                })
            }catch (e: Exception){
                alert(e.message?:"Error occur")
                progressText.text = "生成"
                progress.reset()
                progress.isClickable = true
            }
        }
    }

    fun setAssEdits(array: JSONArray) {
        val length = array.length()
        edits = Array(length) {
            val jsonItem = array.getJSONObject(it)
            val prompt = "第${it + 1}条字幕"
            val editText = EditText(this).apply {
                hint = jsonItem.optString("hint", prompt)
                gravity = Gravity.CENTER_VERTICAL
                setSingleLine()
                imeOptions = if (it != length - 1) EditorInfo.IME_ACTION_NEXT else EditorInfo.IME_ACTION_DONE
            }
            val layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, dip(50f))
            layoutParams.leftMargin = dip(5f)
            layoutParams.rightMargin = layoutParams.leftMargin
            scrollColumn.addView(editText,layoutParams )
            AssItem(
                jsonItem.getString("start"),
                jsonItem.getString("end"),
                editText, prompt
            )
        }
    }


}