package io.github.waterpeak.sorry.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import io.github.waterpeak.sorry.R
import io.github.waterpeak.sorry.entity.TemplateItemEntity
import kotlinx.android.synthetic.main.template_list_item.view.*

class TemplateListAdapter(private val fa: FragmentActivity) : RecyclerView.Adapter<ViewHolder>(){

    var list: List<TemplateItemEntity>? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return object: ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.template_list_item,parent,false)){}
    }

    override fun getItemCount(): Int {
        return list?.size ?: 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        list?.get(position)?.also {
            holder.itemView.title.text = it.name
            Glide.with(fa).load("file:///android_asset/${it.file}").into(holder.itemView.cover)
        }
    }
}