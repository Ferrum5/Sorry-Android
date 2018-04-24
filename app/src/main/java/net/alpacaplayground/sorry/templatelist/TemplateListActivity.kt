package net.alpacaplayground.sorry.templatelist

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.WindowManager
import net.alpacaplayground.sorry.base.BaseActivity
import net.alpacaplayground.sorry.entity.TemplateItemEntity
import net.alpacaplayground.sorry.template.TemplateActivity
import net.alpacaplayground.sorry.utils.*
import org.json.JSONObject
import org.json.JSONTokener
import java.io.*

class TemplateListActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //修改标题
        setActionBarTitle("选择模板")
        val adapter = TemplateListAdapter()
        adapter.onItemClickListener = object: TemplateListAdapter.OnItemClickListener{
            override fun onItemClick(position: Int, item: TemplateItemEntity?) {
                item?.apply {
                    startActivity(TemplateActivity::class){
                        putExtra("json",(this@apply).json)
                        putExtra("file",(this@apply).file)
                    }
                }
            }

        }
        val recyclerView = RecyclerView(this)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        recyclerView.adapter = adapter
        setContentView(recyclerView, WindowManager.LayoutParams(MATCH, MATCH))

        val model: TemplateListViewModel = getViewModel()
        model.liveTemplateList.observe(this,adapter)
        model.parseList()
    }
}