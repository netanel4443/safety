package com.e.security.ui.recyclerviews.clicklisteners

import com.e.security.ui.recyclerviews.celldata.StudyPlaceDataVhCell
import com.e.security.ui.recyclerviews.helpers.GenericItemClickListener

interface StudyPlaceVhItemClick:GenericItemClickListener<StudyPlaceDataVhCell> {

    fun onEditBtnClick(item: StudyPlaceDataVhCell)
    fun onLongClick(item: StudyPlaceDataVhCell):Boolean
}