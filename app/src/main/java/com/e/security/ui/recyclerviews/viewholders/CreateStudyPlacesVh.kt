package com.e.security.ui.recyclerviews.viewholders

import android.view.View
import com.e.security.databinding.StudyPlaceVhCellDesignBinding
import com.e.security.ui.recyclerviews.celldata.StudyPlaceDataVhCell
import com.e.security.ui.recyclerviews.helpers.CreateVh
import com.e.security.ui.recyclerviews.helpers.GenericItemClickListener

class CreateStudyPlacesVh : CreateVh<StudyPlaceDataVhCell>
{
    override fun createViewHolder(
        view: View,
        itemClick: GenericItemClickListener<StudyPlaceDataVhCell>?
    ): GenericViewHolder<StudyPlaceDataVhCell> {
        val vh=StudyPlaceViewHolder(view)
        vh.setItemClickListener(itemClick)
        return vh
    }

    private inner class StudyPlaceViewHolder(view:View):GenericViewHolder<StudyPlaceDataVhCell>(view){
        val binding:StudyPlaceVhCellDesignBinding = StudyPlaceVhCellDesignBinding.bind(view)

        init {
            binding.parent.setOnClickListener {
                itemClick!!.onItemClick(cachedItem!!)
            }
        }

        override fun bind(item: StudyPlaceDataVhCell) {
            super.bind(item)
            binding.placeName.text=item.placeName
            binding.city.text=item.city
        }
    }
}