package com.e.safety.ui.recyclerviews.clicklisteners

import com.e.safety.ui.recyclerviews.celldata.ImageViewVhCell
import com.e.safety.ui.recyclerviews.helpers.GenericItemClickListener

interface ImageVhItemClickListener:GenericItemClickListener<ImageViewVhCell> {

    fun onDeleteImage(item: ImageViewVhCell)
}