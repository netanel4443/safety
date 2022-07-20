package com.e.safety.ui.recyclerviews.clicklisteners

import com.e.safety.ui.recyclerviews.celldata.StudyPlaceDataVhCell
import com.e.safety.ui.recyclerviews.helpers.GenericItemClickListener

interface StudyPlaceVhItemClick:GenericItemClickListener<StudyPlaceDataVhCell> {

    fun onEditBtnClick(item: StudyPlaceDataVhCell)
    fun onLongClick(item: StudyPlaceDataVhCell):Boolean
}