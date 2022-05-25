package com.e.security.ui.states

import android.net.Uri
import com.e.security.data.FindingDataHolder
import com.e.security.data.StudyPlaceDetailsDataHolder
import com.e.security.ui.recyclerviews.celldata.FindingVhCell
import com.e.security.ui.recyclerviews.celldata.ReportVhCell
import com.e.security.ui.recyclerviews.celldata.StudyPlaceDataVhCell

data class MainViewState(
    var findingVhCellArrayList:ArrayList<FindingVhCell> = ArrayList(),
    var studyPlacesVhCellArrayList:ArrayList<StudyPlaceDataVhCell> = ArrayList(),
    var reportVhCellArrayList:ArrayList<ReportVhCell> = ArrayList(),
    var createFindingFragmentState: CreateFindingFragmentState = CreateFindingFragmentState(),
    var studyPlaceInfoFragmentState:StudyPlaceInfoFragmentState = StudyPlaceInfoFragmentState()
 ){
    data class CreateFindingFragmentState(
        var finding:FindingDataHolder = FindingDataHolder(),
        var problemImage: Uri?= null
    )
    data class StudyPlaceInfoFragmentState(
        val reportDetails :StudyPlaceDetailsDataHolder = StudyPlaceDetailsDataHolder(),
        val educationalInstitutions:List<String> = ArrayList()
    )
//    data class ReportFragmentState(
//        var reportList:ArrayList<ReportVhCell> = ArrayList(),
//
//    )


}