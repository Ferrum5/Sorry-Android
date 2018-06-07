package net.alpacaplayground.sorry.utils

import android.support.v7.widget.RecyclerView
import android.view.View

abstract class SimpleAdapter : RecyclerView.Adapter<SimpleHolder>() {
    open fun onItemClick(view: View, holder: SimpleHolder) {}
}

open class SimpleHolder(itemView: View?) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

    private var adapter: SimpleAdapter? = null

    fun bindAdapter(adapter: SimpleAdapter): SimpleHolder {
        this.adapter = adapter
        return this
    }

    override fun onClick(v: View) {
        adapter?.onItemClick(v, this)
    }
}