package com.e.safety.ui.fragments.reportsfragment

import com.e.safety.R
import com.e.safety.ui.dialogfragments.basedialogfragments.BaseTextDialogFragment
import com.e.safety.ui.viewmodels.MainViewModel

class DeleteReportFragmentDialog : BaseTextDialogFragment() {

    private val viewModel: MainViewModel by lazy(this::getViewModel)

    override fun setMessage(): String {
        return requireContext().getString(R.string.delete_report)
    }

    override fun onAcceptBtnClick() {
        viewModel.deleteReport()
        super.onAcceptBtnClick()
    }
}