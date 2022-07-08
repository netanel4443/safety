package com.e.security.ui.recyclerviews.viewholders

import android.net.Uri
import android.view.View
import android.view.ViewGroup
import com.e.security.R
import com.e.security.databinding.ImageVhCellDesignBinding
import com.e.security.ui.recyclerviews.celldata.ImageViewVhCell
import com.e.security.ui.recyclerviews.clicklisteners.ImageVhItemClickListener
import com.e.security.ui.recyclerviews.helpers.CreateVh
import com.e.security.ui.recyclerviews.helpers.GenericItemClickListener
import com.squareup.picasso.Picasso

class CreateImageViewVh : CreateVh<ImageViewVhCell>() {
    private var binding: ImageVhCellDesignBinding? = null
    private var _itemClick: ImageVhItemClickListener? = null

    override fun getViewHolder(
        parent: ViewGroup,
        itemClick: GenericItemClickListener<ImageViewVhCell>?
    ): GenericViewHolder {
        return createVh(
            parent,
            R.layout.image_vh_cell_design,
            itemClick
        )
    }

    override fun onInitVh(view: View) {
        binding = ImageVhCellDesignBinding.bind(view)
        binding!!.imageView.setOnClickListener {
            _itemClick?.onItemClick(cachedItem!!)
        }
        binding!!.deleteImageBtn.setOnClickListener {
            _itemClick?.onDeleteImage(cachedItem!!)
        }
    }

    override fun bindData(item: ImageViewVhCell) {
        Picasso.get().load(Uri.parse(item.image)).into(binding!!.imageView)
    }

    override fun setClickListener(itemClickListener: GenericItemClickListener<ImageViewVhCell>?) {
        _itemClick = itemClickListener as ImageVhItemClickListener
    }
}