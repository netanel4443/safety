package com.e.security.ui.fragments

import com.e.security.R
import com.e.security.ui.dialogfragments.basedialogfragments.BaseTextDialogFragment
import com.e.security.ui.states.MainViewState
import com.e.security.ui.viewmodels.MainViewModel

class DeleteStudyPlaceFragmentDialog : BaseTextDialogFragment<MainViewState>() {

    private val viewModel: MainViewModel by lazy(this::getViewModel)

    override fun setMessage(): String {
        return requireActivity().getString(R.string.delete_study_place)
    }

    override fun onAcceptBtnClick() {
        viewModel.deleteStudyPlace()
        super.onAcceptBtnClick()
    }
}