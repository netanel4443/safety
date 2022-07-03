package com.e.security.ui.viewmodels.effects

import com.e.security.ui.recyclerviews.celldata.TextViewVhCell

sealed class Effects {
    object StartReportsFragment : Effects()
    object StartFindingsDetailsFragment : Effects()
    object StartCreateFindingFragment : Effects()
    object ShowCalendarDialog : Effects()
    object ShowStudyPlaceInfoDialogFragment : Effects()
    object ShowHozerMankalDialog : Effects()
    object PopBackStack : Effects()
    object TakePhoto : Effects()
    object SelectPhoto : Effects()
    object ShowReportConclusionDialog : Effects()

    data class StartActivityForResultWord(val type: String) : Effects()
    data class StartActivityForResultPdf(val type: String) : Effects()
    data class Toast(val message: String) : Effects()
    data class ShowDeleteDialog(val message: Int, val func: () -> Unit) : Effects()
    data class ShowEducationalInstitutionsDialog(val items: List<TextViewVhCell>) : Effects()
    data class ShowReportFragmentRecyclerViewMenu(val items: List<TextViewVhCell>) : Effects()
    data class ShowPhotoUploadDialog(val items: List<TextViewVhCell>) : Effects()


}