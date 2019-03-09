package com.github.waterpeak.sorry.ui

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.waterpeak.sorry.entity.TemplateItemEntity
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