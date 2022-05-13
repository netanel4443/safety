package com.e.security.ui.states

import android.net.Uri
import com.e.security.data.FindingDataHolder
import com.e.security.data.ReportDetailsDataHolder
import com.e.security.ui.recyclerviews.celldata.FindingVhCellData
import com.e.security.ui.recyclerviews.celldata.ReportVhCell
import com.e.security.ui.recyclerviews.celldata.StudyPlaceDataVhCell

data class MainViewState(
    var findingArrayList:ArrayList<FindingVhCellData> = ArrayList(),
    var studyPlacesVhCells:ArrayList<StudyPlaceDataVhCell> = ArrayList(),
    var reportList:ArrayList<ReportVhCell> = ArrayList(),
    var createFindingFragmentState: CreateFindingFragmentState = CreateFindingFragmentState(),
    var studyPlaceFragmentState:StudyPlaceInfoFragmentState = StudyPlaceInfoFragmentState()


 ){
    data class CreateFindingFragmentState(
        var finding:FindingDataHolder = FindingDataHolder(),
        var problemImage: Uri?= null
    )
    data class StudyPlaceInfoFragmentState(
        val reportDetails :ReportDetailsDataHolder = ReportDetailsDataHolder(),
        val educationalInstitutions:List<String> = ArrayList()
    )


}