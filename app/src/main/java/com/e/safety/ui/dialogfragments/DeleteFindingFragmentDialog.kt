package com.e.safety.ui.dialogfragments

import com.e.safety.R
import com.e.safety.ui.dialogfragments.basedialogfragments.BaseTextDialogFragment
import com.e.safety.ui.viewmodels.MainViewModel

class DeleteFindingFragmentDialog : BaseTextDialogFragment() {

    private val viewModel: MainViewModel by lazy(this::getViewModel)

    override fun setMessage(): String {
        return requireContext().getString(R.string.delete_finding)
    }

    override fun onAcceptBtnClick() {
        viewModel.deleteFinding()
        super.onAcceptBtnClick()
    }
}