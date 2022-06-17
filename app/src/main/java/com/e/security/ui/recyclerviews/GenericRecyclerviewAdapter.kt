package com.e.security.ui.recyclerviews

import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.e.security.ui.recyclerviews.helpers.CreateVh
import com.e.security.ui.recyclerviews.helpers.GenericItemClickListener

open class GenericRecyclerviewAdapter<T, CVH : CreateVh<T>>(
    private val clazz: Class<CVH>
) : RecyclerView.Adapter<CreateVh<T>.GenericViewHolder>() {
    protected val items = ArrayList<T>()
    protected var itemClick: GenericItemClickListener<T>? = null

    protected val diffUtil= object:DiffUtil.ItemCallback<T>(){
        override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
           return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
         return newItem == oldItem
        }
    }

    protected val listDiffer:AsyncListDiffer<T> = AsyncListDiffer(this,diffUtil)

    fun submitList(list:List<T>){
        listDiffer.submitList(list)
    }

    fun addItems(items: List<T>) {
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    fun removeItems(list: List<T>) {
        list.forEach(::removeItem)
    }

    fun removeItem(item: T) {
        items.forEachIndexed { index, t ->
            if (t == item) {
                items.removeAt(index)
                notifyItemRemoved(index)
                return
            }
        }
    }
    fun removeAllItems(){
        items.clear()
    }

    fun changeItems(newItems: List<Pair<T, T>>) {
        newItems.forEach(::changeItem)
    }

    fun changeItem(newItem: Pair<T,T>){
        val oldItem =newItem.first
        val new = newItem.second
        items.forEachIndexed { index, item ->
            if (oldItem==item){
                items[index] = new
                notifyItemChanged(index)
                return
            }
        }
    }


    fun hasNoItems(): Boolean {
        return items.isEmpty()
    }

    fun setItemClickListener(itemClickListener: GenericItemClickListener<T>) {
        itemClick = itemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CreateVh<T>.GenericViewHolder {
        val instance = clazz.newInstance()
        return instance.getViewHolder(parent, itemClick)
    }

    override fun onBindViewHolder(holder: CreateVh<T>.GenericViewHolder, position: Int) {
        holder.bind(listDiffer.currentList.get(position))
    }

    override fun getItemCount(): Int {
      return  listDiffer.currentList.size
    }
}