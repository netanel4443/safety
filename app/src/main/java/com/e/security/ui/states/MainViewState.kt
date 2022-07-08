package com.e.security.ui.states

import android.net.Uri
import com.e.fakerestapi.ui.recyclerviews.helpers.GenericVhItem
import com.e.security.data.FindingDataHolder
import com.e.security.data.StudyPlaceDetailsDataHolder
import com.e.security.ui.recyclerviews.celldata.*

data class MainViewState(
    var findingFragmentState: FindingFragmentState = FindingFragmentState(),
//    var reportFragmentState: ReportFragmentState = ReportFragmentState(),
    var studyPlacesVhCellArrayList: ArrayList<StudyPlaceDataVhCell> = ArrayList(),
    var reportVhCellArrayList: ArrayList<ReportVhCell> = ArrayList(),
    var createFindingFragmentState: CreateFindingFragmentState = CreateFindingFragmentState(),
    var studyPlaceInfoFragmentState: StudyPlaceInfoFragmentState = StudyPlaceInfoFragmentState(),
    val educationalInstitutionsRvItems: List<TextViewVhCell> = ArrayList(),
    val reportFragmentMenuRvDialogRvItems: List<TextViewVhCell> = ArrayList()
) {
    data class CreateFindingFragmentState(
        var finding: FindingDataHolder = FindingDataHolder(),
        var problemImage: ArrayList<ImageViewVhCell> = ArrayList(),
        var chosenHozerMankalRecyclerItems: List<GenericVhItem> = ArrayList(),
        val imageOptionsRvItems: List<TextViewVhCell> = ArrayList(),
    )

    data class StudyPlaceInfoFragmentState(
        val reportDetails: StudyPlaceDetailsDataHolder = StudyPlaceDetailsDataHolder(),
    )

    data class FindingFragmentState(
        var findingVhCellArrayList: ArrayList<FindingVhCell> = ArrayList(),
        var reportConclusionDialogVisibility: Boolean = false,
        var conclusion: String = ""
    )

    data class ReportFragmentState(
        var reportVhCellArrayList: ArrayList<ReportVhCell> = ArrayList(),
    )


}