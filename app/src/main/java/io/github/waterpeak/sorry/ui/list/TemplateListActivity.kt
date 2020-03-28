package io.github.waterpeak.sorry.ui.list

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.waterpeak.sorry.*
import io.github.waterpeak.sorry.ui.edit.GifEditActivity


class TemplateListActivity : BaseActivity() {

    private val model by lazy{ViewModelProvider(this).get(TemplateListViewModel::class.java)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val adapter = TemplateListAdapter(this)
        val list = RecyclerView(this)
        setContentView(list)
        list.layoutManager = GridLayoutManager(this,3)
        list.adapter = adapter
        list.setOnItemClickListener{
            val item = adapter.list!!.get(it)
            startActivity<GifEditActivity> {
                it.putExtra("json", item.json)
                it.putExtra("file", item.file)
            }
        }
        model.parseList(applicationContext).observe(this, Observer { adapter.list = it })
    }
}