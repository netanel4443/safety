package com.e.safety.ui.dialogfragments

import com.e.safety.ui.dialogfragments.basedialogfragments.BaseEditTextDialogFragment
import com.e.safety.ui.viewmodels.MainViewModel

class ConclusionFragmentDialog : BaseEditTextDialogFragment() {

    private val viewModel: MainViewModel by lazy(this::getViewModel)

    override fun observeForChanges() {
        viewModel.viewState.observe(viewLifecycleOwner) {
            setData(it.findingFragmentState.conclusion)
        }
    }

    override fun onAcceptBtnClick() {
        viewModel.saveReportConclusion(binding!!.editText.text.toString())
        super.onAcceptBtnClick()
    }
}