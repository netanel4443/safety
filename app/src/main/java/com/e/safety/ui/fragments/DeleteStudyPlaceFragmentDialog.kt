package com.e.safety.ui.fragments

import com.e.safety.R
import com.e.safety.ui.dialogfragments.basedialogfragments.BaseTextDialogFragment
import com.e.safety.ui.viewmodels.MainViewModel


class DeleteStudyPlaceFragmentDialog : BaseTextDialogFragment() {

    private val viewModel: MainViewModel by lazy(this::getViewModel)

    override fun setMessage(): String {
        return requireActivity().getString(R.string.delete_study_place)
    }

    override fun onAcceptBtnClick() {
        viewModel.deleteStudyPlace()
        super.onAcceptBtnClick()
    }
}