package com.e.security.ui.viewmodels.effects

import android.content.Intent
import com.e.security.ui.recyclerviews.celldata.TextViewVhCell

sealed class Effects {
    object StartReportsFragment : Effects()
    object StartFindingsDetailsFragment : Effects()
    object StartCreateFindingFragment : Effects()
    object ShowCalendarDialog : Effects()
    object ShowStudyPlaceInfoDialogFragment : Effects()
    object ShowHozerMankalDialog : Effects()
    object  PopBackStack : Effects()

    data class StartActivityForResult(val intent: Intent) : Effects()
    data class ShowDeleteDialog(val message: Int, val func: () -> Unit) : Effects()
    data class Toast(val message: String) : Effects()
    data class ShowStringRecyclerViewDialog(
        val items: List<TextViewVhCell>,
        val func: (TextViewVhCell) -> Unit
    ) : Effects()


}