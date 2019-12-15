package com.thecrafter.ecosnap

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

abstract class ViewHolder<Item>(itemView: View, val context: Context) : RecyclerView.ViewHolder(itemView) {
    abstract fun bind(item: Item)

}

fun inflate(parent: ViewGroup, resId: Int): View {
    return LayoutInflater.from(parent.context).inflate(resId, parent, false)
}

class Adapter<Item, VH : ViewHolder<Item>>(val vhFactory: (ViewGroup) -> VH) : RecyclerView.Adapter<VH>() {
    val items: MutableList<Item> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return vhFactory(parent)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(items[position])
    }

    fun addItems(vararg items: Item) {
        this.items.addAll(items.asList())
        notifyDataSetChanged()
    }

    fun removeItems(vararg items: Item) {
        items.forEach { i ->
            this.items.remove(i)
        }
        notifyDataSetChanged()
    }
}
