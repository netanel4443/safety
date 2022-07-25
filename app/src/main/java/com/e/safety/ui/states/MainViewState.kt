package com.e.safety.ui.states

import com.e.fakerestapi.ui.recyclerviews.helpers.GenericVhItem
import com.e.safety.data.FindingDataHolder
import com.e.safety.data.StudyPlaceDetailsDataHolder
import com.e.safety.ui.recyclerviews.celldata.*

data class MainViewState(
    var findingFragmentState: FindingFragmentState = FindingFragmentState(),
    var reportFragmentState: ReportFragmentState = ReportFragmentState(),
    var studyPlacesVhCellArrayList: ArrayList<StudyPlaceDataVhCell> = ArrayList(),
    val createFindingFragmentState: CreateFindingFragmentState = CreateFindingFragmentState(),
    var studyPlaceInfoFragmentState: StudyPlaceInfoFragmentState = StudyPlaceInfoFragmentState(),
    val educationalInstitutionsRvItems: List<TextViewVhCell> = ArrayList(),
    val reportFragmentMenuRvDialogRvItems: List<TextViewVhCell> = ArrayList()
) {
    data class CreateFindingFragmentState(
        val finding: FindingDataHolder = FindingDataHolder(),
        val problemImage: ArrayList<ImageViewVhCell> = ArrayList(),
        val chosenHozerMankalRecyclerItems: List<GenericVhItem> = ArrayList(),
        val imageOptionsRvItems: List<TextViewVhCell> = ArrayList(),
    )

    data class StudyPlaceInfoFragmentState(
        val reportDetails: StudyPlaceDetailsDataHolder = StudyPlaceDetailsDataHolder(),
    )

    data class FindingFragmentState(
        val findingVhCellArrayList: ArrayList<FindingVhCell> = ArrayList(),
        val reportConclusionDialogVisibility: Boolean = false,
        val conclusion: String = ""
    )

    data class ReportFragmentState(
        val reportVhCellArrayList: ArrayList<ReportVhCell> = ArrayList(),
        val isLoading:Boolean = false
    )


}