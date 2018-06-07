package net.alpacaplayground.sorry.templatelist

import android.arch.lifecycle.Observer
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import net.alpacaplayground.sorry.R
import net.alpacaplayground.sorry.entity.TemplateItemEntity
import net.alpacaplayground.sorry.utils.SimpleAdapter
import net.alpacaplayground.sorry.utils.SimpleHolder
import net.alpacaplayground.sorry.utils.VideoCoverLoader
import org.jetbrains.anko.*

class TemplateListAdapter : SimpleAdapter(), Observer<List<TemplateItemEntity>> {

    override fun onChanged(t: List<TemplateItemEntity>?) {
        mList = t
    }

    var mList: List<TemplateItemEntity>? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var onItemClickListener: ((Int, TemplateItemEntity) -> Unit)? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleHolder {
        return ViewHolder(parent.context.verticalLayout()).bindAdapter(this)
    }

    override fun getItemCount(): Int {
        return mList?.size ?: 0
    }

    override fun onBindViewHolder(holder: SimpleHolder, position: Int) {
        mList?.get(position)?.apply {
            with(holder as ViewHolder) {
                holder.textName.text = name ?: "null"
                VideoCoverLoader.load(file, holder.imageCover)
            }
        }
    }

    override fun onItemClick(view: View, holder: SimpleHolder) {
        onItemClickListener?.apply {
            val pos = holder.adapterPosition
            val item = mList?.get(pos)
            if (item != null) {
                invoke(pos, item)
            }
        }
    }
}

private class ViewHolder(itemView: View) : SimpleHolder(itemView) {
    lateinit var imageCover: ImageView
    lateinit var textName: TextView

    init {
        with(itemView as _LinearLayout) {
            gravity = Gravity.CENTER_HORIZONTAL
            setOnClickListener(this@ViewHolder)
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