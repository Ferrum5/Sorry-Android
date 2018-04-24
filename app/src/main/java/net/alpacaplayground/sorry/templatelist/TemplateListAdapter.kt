package net.alpacaplayground.sorry.templatelist

import android.arch.lifecycle.Observer
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import net.alpacaplayground.sorry.R
import net.alpacaplayground.sorry.entity.TemplateItemEntity
import net.alpacaplayground.sorry.utils.MATCH
import net.alpacaplayground.sorry.utils.VideoCoverLoader
import net.alpacaplayground.sorry.utils.WRAP
import net.alpacaplayground.sorry.utils.dip2Px

class TemplateListAdapter : RecyclerView.Adapter<TemplateListAdapter.ViewHolder>(),Observer<List<TemplateItemEntity>> {
    override fun onChanged(t: List<TemplateItemEntity>?) {
        mList = t
    }

    var mList: List<TemplateItemEntity>? = null
    set(value) {
        field = value
        notifyDataSetChanged()
    }

    var onItemClickListener: OnItemClickListener? = null

    interface OnItemClickListener{
        fun onItemClick(position: Int, item: TemplateItemEntity?)
    }

    operator fun get(index: Int): TemplateItemEntity?{
       return mList?.get(index)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_template_list,parent,false)
        return ViewHolder(itemView).apply { itemView.setOnClickListener(this) }
    }

    override fun getItemCount(): Int {
        return mList?.size ?: 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val name = this[position]?.name
        val file = this[position]?.file
        holder.textName.text = name?:"null"
        VideoCoverLoader.instance.load(file,holder.imageCover)
    }


    inner class ViewHolder(itemView: View,
                           val imageCover: ImageView = itemView.findViewById(R.id.imageItemTemplateCover),
                           val textName: TextView = itemView.findViewById(R.id.textItemTemplateName)):
            RecyclerView.ViewHolder(itemView),
            View.OnClickListener {
        init {
            val params = RecyclerView.LayoutParams(MATCH,WRAP)
            params.leftMargin = dip2Px(5)
            params.rightMargin = params.leftMargin
            itemView.layoutParams = params
        }
        override fun onClick(v: View?) {
            onItemClickListener?.onItemClick(adapterPosition,mList?.get(adapterPosition))
        }

    }
}