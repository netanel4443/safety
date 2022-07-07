package com.e.security.ui.viewmodels.effects

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
    object ShowEducationalInstitutionsDialog : Effects()
    object ShowPhotoUploadDialog : Effects()
    object ShowReportFragmentRecyclerViewMenu : Effects()

    data class ShowReportConclusionDialog(val conclusion: String) : Effects()
    data class StartActivityForResultWord(val type: String) : Effects()
    data class StartActivityForResultPdf(val type: String) : Effects()
    data class Toast(val message: String) : Effects()
    data class ShowDeleteDialog(val message: Int, val func: () -> Unit) : Effects()


}