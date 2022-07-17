package com.e.security.ui.fragments.reportsfragment

import com.e.security.R
import com.e.security.ui.dialogfragments.basedialogfragments.BaseTextDialogFragment
import com.e.security.ui.states.MainViewState
import com.e.security.ui.viewmodels.MainViewModel

class DeleteReportFragmentDialog : BaseTextDialogFragment<MainViewState>() {

    private val viewModel: MainViewModel by lazy(this::getViewModel)

    override fun setMessage(): String {
        return requireContext().getString(R.string.delete_report)
    }

    override fun onAcceptBtnClick() {
        viewModel.deleteReport()
        super.onAcceptBtnClick()
    }
}