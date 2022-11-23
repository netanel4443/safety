package com.e.safety.ui.recyclerviews.generics

import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.e.fakerestapi.ui.recyclerviews.helpers.GenericVhItem
import com.e.safety.ui.recyclerviews.helpers.CreateVh


open class GenericRecyclerviewAdapter2<T : GenericVhItem> :
    RecyclerView.Adapter<CreateVh<T>.GenericViewHolder>() {

    private var vhItemSetter: VhItemSetters<T>? = null

    protected val diffUtil = object : DiffUtil.ItemCallback<T>() {
        override fun areItemsTheSame(
            oldItem: T,
            newItem: T
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: T,
            newItem: T
        ): Boolean {
            return newItem == oldItem
        }
    }

    protected val listDiffer: AsyncListDiffer<T> = AsyncListDiffer(this, diffUtil)



    fun submitList(list: List<T>) {
        listDiffer.submitList(list)
    }

    fun hasNoItems(): Boolean {
        return listDiffer.currentList.isEmpty()
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CreateVh<T>.GenericViewHolder {

        getVhItemSetter(viewType)

        val createVh = vhItemSetter!!.createVh!!.newInstance()
        return createVh.createVh(
            parent,
            vhItemSetter!!.layoutId,
            vhItemSetter!!.clickListener
        )
    }


    override fun onBindViewHolder(
        holder: CreateVh<T>.GenericViewHolder,
        position: Int
    ) {
        val k = listDiffer.currentList[position]!!
        holder.bind(k)
    }

    override fun getItemCount(): Int {
        return listDiffer.currentList.size
    }


     fun setVhItemSetter(setter: VhItemSetters<T>) {
        vhItemSetter = setter
    }

    /** this function fits to 1 view holder , that`s why we return 0.
    0 is the first index of [vhMap].
    If we need multiple view holders, we will override this function in a
    subclass of [GenericRecyclerviewAdapter2] and use proper logic
     */
    override fun getItemViewType(position: Int): Int {
        return 0
    }

    /***
     * this function depeneds on [getItemViewType] function, for recyclerview
     * with multiple  [RecyclerView.ViewHolder]s.
     * [getItemViewType] need to check which viewHolder fits.
     * inside [onCreateViewHolder] we call [getVhItemSetter]
     * that calls to [setVhItemSetter] to set the desired viewHolder
     */
    protected open fun getVhItemSetter(viewType: Int) {}


}