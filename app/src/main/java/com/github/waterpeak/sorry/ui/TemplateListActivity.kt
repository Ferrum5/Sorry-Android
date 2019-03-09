package com.github.waterpeak.sorry.ui

import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.github.waterpeak.sorry.*


class TemplateListActivity : BaseActivity() {

    private val videoCoverLoader by lazy { VideoCoverLoader(applicationContext) }
    private val model: TemplateListViewModel by lazyModel()
    private val mAdapter by lazy { TemplateListAdapter(videoCoverLoader) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val recyclerView = recyclerView {
            layoutManager = GridLayoutManager(this@TemplateListActivity, 3)
            this.adapter = mAdapter
            addOnItemTouchListener(RecyclerViewOnItemClickListener(this){
                val item = mAdapter.list!!.get(it)
                startActivity(GifEditActivity::class) {
                    it.putExtra("json", item.json)
                    it.putExtra("file", item.file)
                }
            })
        }
        setContentView(recyclerView)

        model.parseList(applicationContext).observe(this) {
            mAdapter.list = it
        }
    }
}