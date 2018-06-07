package net.alpacaplayground.sorry.templatelist

import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import net.alpacaplayground.sorry.base.BaseActivity
import net.alpacaplayground.sorry.getViewModel
import net.alpacaplayground.sorry.startActivity
import net.alpacaplayground.sorry.template.TemplateActivity
import org.jetbrains.anko.recyclerview.v7.recyclerView

class TemplateListActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val adapter = TemplateListAdapter()
        adapter.onItemClickListener = { _, item ->
            startActivity(TemplateActivity::class) {
                putExtra("json", item.json)
                putExtra("file", item.file)
            }
        }
        val recyclerView = recyclerView {
            layoutManager = GridLayoutManager(this@TemplateListActivity, 3)
            this.adapter = adapter
        }
        setContentView(recyclerView)
        val model: TemplateListViewModel = getViewModel()
        model.parseList(applicationContext).observe(this, adapter)
    }
}