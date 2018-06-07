package net.alpacaplayground.sorry.templatelist

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.content.Context
import net.alpacaplayground.sorry.entity.TemplateItemEntity
import org.json.JSONObject
import java.io.InputStreamReader

class TemplateListViewModel : ViewModel() {

    fun parseList(context: Context): LiveData<List<TemplateItemEntity>> {
        val liveData = MutableLiveData<List<TemplateItemEntity>>()
        Thread {
            with(context.assets) {
                val templateds = list("templates")
                        .filter { it.endsWith(".json") }
                        .map { it.substring(0, it.length - 5) }
                        .toList()
                        .map { string ->
                            InputStreamReader(open("templates/$string.json"))
                                    .use {
                                        val json = it.readText()
                                        val name = (org.json.JSONTokener(json).nextValue() as JSONObject).getString("name")
                                        val file = "templates/$string.mp4"
                                        TemplateItemEntity(name, file, json)
                                    }
                        }
                liveData.postValue(templateds)

            }
        }.start()
        return liveData
    }
}