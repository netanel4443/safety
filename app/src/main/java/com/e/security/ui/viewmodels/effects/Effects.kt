package com.e.security.ui.viewmodels.effects

import com.e.security.ui.recyclerviews.celldata.TextViewVhCell

sealed class Effects {
        object StartReportsFragment:Effects()
        object StartFindingsDetailsFragment:Effects()
        object StartCreateFindingFragment:Effects()
        object ShowCalendarDialog : Effects()

    data class ShowEducationalInstitutionDialog(val items:List<TextViewVhCell>):Effects()
}