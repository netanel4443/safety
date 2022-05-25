package com.e.security.ui.recyclerviews.viewholders

import android.view.View
import android.view.ViewGroup
import com.e.security.R
import com.e.security.databinding.StudyPlaceVhCellDesignBinding
import com.e.security.ui.recyclerviews.celldata.StudyPlaceDataVhCell
import com.e.security.ui.recyclerviews.clicklisteners.StudyPlaceVhItemClick
import com.e.security.ui.recyclerviews.helpers.CreateVh
import com.e.security.ui.recyclerviews.helpers.GenericItemClickListener

class CreateStudyPlacesVh : CreateVh<StudyPlaceDataVhCell>() {
    private var binding: StudyPlaceVhCellDesignBinding? = null
    private var itmClick: StudyPlaceVhItemClick? = null
    override fun getViewHolder(
        parent: ViewGroup,
        itemClick: GenericItemClickListener<StudyPlaceDataVhCell>?
    ): GenericViewHolder {
        return createVh(
            parent,
            R.layout.study_place_vh_cell_design,
            itemClick
        )
    }

    override fun onInitVh(view: View) {
        binding = StudyPlaceVhCellDesignBinding.bind(view)

        binding!!.parent.setOnClickListener {
            itmClick!!.onItemClick(cachedItem!!)
        }
        binding!!.editBtn.setOnClickListener {
            itmClick!!.onEditBtnClick(cachedItem!!)
        }
        binding!!.parent.setOnLongClickListener {
            itmClick!!.onLongClick(cachedItem!!)
        }
    }

    override fun bindData(item: StudyPlaceDataVhCell) {
        binding!!.placeName.text = item.placeName
        binding!!.city.text = item.city
    }

    override fun setClickListener(itemClickListener: GenericItemClickListener<StudyPlaceDataVhCell>?) {
        itmClick = itemClickListener as StudyPlaceVhItemClick
    }
}
