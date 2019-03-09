package com.github.waterpeak.sorry.ui

import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.waterpeak.sorry.entity.TemplateItemEntity
import com.github.waterpeak.sorry.VideoCoverLoader
import org.jetbrains.anko.*

class TemplateListAdapter(private val loader: VideoCoverLoader) : RecyclerView.Adapter<TemplateListViewHolder>(){

    var list: List<TemplateItemEntity>? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TemplateListViewHolder {
        return TemplateListViewHolder(parent.context.verticalLayout())
    }

    override fun getItemCount(): Int {
        return list?.size ?: 0
    }

    override fun onBindViewHolder(holder: TemplateListViewHolder, position: Int) {
        list?.get(position)?.also {
            holder.textName.text = it.name
            loader.load(it.file, holder.imageCover)
        }
    }
}

class TemplateListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val imageCover: ImageView
    val textName: TextView

    init {
        with(itemView as _LinearLayout) {
            gravity = Gravity.CENTER_HORIZONTAL
            imageCover = imageView().lparams(width = MATCH_PARENT, height = dip(100))
            textName = textView {
                textSize = 18f
                textColor = Color.BLACK
            }
            lparams(width = MATCH_PARENT) {
                horizontalMargin = dip(5)
            }
            view {
                backgroundColor = Color.DKGRAY
            }.lparams(width = MATCH_PARENT, height = dip(1))
        }
    }
}