package com.e.security.ui

import android.net.Uri
import androidx.lifecycle.LiveData
import com.e.security.data.FindingDataHolder
import com.e.security.data.FindingListDataHolder
import com.e.security.data.GeneralReportDetailsDataHolder
import com.e.security.data.StudyPlaceDataHolder
import com.e.security.data.localdatabase.operations.FindingsCrudRepo
import com.e.security.di.scopes.ActivityScope
import com.e.security.ui.recyclerviews.celldata.FindingVhCellData
import com.e.security.ui.recyclerviews.celldata.ReportVhCell
import com.e.security.ui.recyclerviews.celldata.StudyPlaceDataVhCell
import com.e.security.ui.states.MainViewState
import com.e.security.ui.utils.MviMutableLiveData
import com.e.security.ui.utils.PrevAndCurrentState
import com.e.security.ui.utils.livedata.SingleLiveEvent
import com.e.security.ui.viewmodels.BaseViewModel
import com.e.security.ui.viewmodels.effects.Effects
import com.e.security.utils.printErrorIfDbg
import com.e.security.utils.printIfDbg
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import org.bson.types.ObjectId
import java.util.*
import javax.inject.Inject

@ActivityScope
class MainViewModel @Inject constructor(
    private val crud: FindingsCrudRepo
) : BaseViewModel() {

    private val TAG = this.javaClass.name
    private val _viewState = MviMutableLiveData(MainViewState())
    val viewState: LiveData<PrevAndCurrentState<MainViewState>> get() = _viewState

    private val _viewEffect = SingleLiveEvent<Effects>()
    val viewEffect: LiveData<Effects> get() = _viewEffect

    private var data=HashMap<ObjectId,StudyPlaceDataHolder>()

    private var chosenStudyPlaceId = ObjectId()
    private var chosenReportId = ObjectId()
    private var chosenFindingId: ObjectId = ObjectId()

    fun getStudyPlacesAndTheirFindings() {
        if (data.isNotEmpty()) return

        crud.getAllFindings()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                createStudyPlaceDataVhCell(it)
                data = it
            }, { printErrorIfDbg(TAG, it.message) })
    }

    fun getReportListOfChosenStudyPlace() {
        //todo check if we can prevent from always do that

                val tmpArray = ArrayList<ReportVhCell>()
                data[chosenStudyPlaceId]!!.reportList.forEach { reportList->
                    val value=reportList.value
                    val reportVhCell = ReportVhCell(
                        id = value.id,
                        date = value.date
                    )
                    tmpArray.add(reportVhCell)
                }

                _viewState.mviValue {
                    it.copy(
                        reportList = tmpArray
                    )
                }
    }

    fun getReportListFindings() {
        val tmpArray = ArrayList<FindingVhCellData>()
        data[chosenStudyPlaceId]!!.reportList[chosenReportId]!!
            .findingArr.forEach { findingPriorityList ->
                findingPriorityList.forEach { finding ->
                    val value = finding.value
                    val reportVhCell = FindingVhCellData(
                        id = value.id,
                        problem = value.problem,
                        findingSection = value.section
                    )
                    tmpArray.add(reportVhCell)
                }
            }
        _viewState.mviValue {
            it.copy(
                findingArrayList = tmpArray
            )
        }
    }



    private fun createStudyPlaceDataVhCell(data: HashMap<ObjectId,StudyPlaceDataHolder>) {

        val tmpArray = ArrayList<StudyPlaceDataVhCell>()
        tmpArray.addAll(_viewState.currentState().copy().studyPlacesVhCells)
        data.forEach { _, value->
            val obj = StudyPlaceDataVhCell(
                value.id,
                value.generalReportDetails.placeName,
                value.generalReportDetails.city
            )
            tmpArray.add(obj)
        }

        _viewState.mviValue {
            it.copy(studyPlacesVhCells = tmpArray)
        }

    }

    fun createNewFindingList() {
        val date = getDate()
        val reportListId = ObjectId()
        crud.createNewFindingList(chosenStudyPlaceId, reportListId, date)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                val newReport=FindingListDataHolder()
                newReport.id=reportListId
                newReport.date=date

                data[chosenStudyPlaceId]!!.reportList[reportListId]= newReport
                val tmpArray = ArrayList<ReportVhCell>()
                tmpArray.addAll(_viewState.currentState().copy().reportList)
                tmpArray.add(
                    ReportVhCell(reportListId, date)
                )
                _viewState.mviValue {
                    it.copy(reportList = tmpArray)
                }

            }, { printIfDbg(TAG, it.message) })
    }



    fun createNewStudyPlace(placeGeneralDetails: GeneralReportDetailsDataHolder) {
        val id=ObjectId()
        crud.createNewStudyPlace(id,placeGeneralDetails)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                val placeDataHolder=StudyPlaceDataHolder()
                placeDataHolder.id=id
                placeDataHolder.generalReportDetails=placeGeneralDetails

                data[placeDataHolder.id]=placeDataHolder
                val copyArr = _viewState.currentState().copy().studyPlacesVhCells
                val studyPlaceVhCellData =
                    StudyPlaceDataVhCell(
                        id=placeDataHolder.id,
                        placeName = placeGeneralDetails.placeName,
                        city = placeGeneralDetails.city)
                copyArr.add(studyPlaceVhCellData)
                _viewState.mviValue {

                    it.copy(
                        studyPlacesVhCells = copyArr
                    )
                }
            }, { printErrorIfDbg(TAG, it.message) })
    }

   private fun getFindingToEdit() {
        val findingArr = data[chosenStudyPlaceId]!!
            .reportList[chosenReportId]!!
            .findingArr

       var finding:FindingDataHolder?=null

       findingArr.forEach {
           if (it.containsKey(chosenFindingId)){
               finding=it[chosenFindingId]!!
               return
           }
       }

        _viewState.mviValue {
            it.copy(
                finding = finding!!
            )
        }
    }


    fun startReportsFragment(placeId: ObjectId) {
        chosenStudyPlaceId = placeId
        _viewEffect.value = Effects.StartReportsFragment
    }

    fun startFindingDetailsFragment(reportId: ObjectId) {
        chosenReportId = reportId

        _viewEffect.value = Effects.StartFindingsDetailsFragment
    }

    fun startCreateFindingFragment() {
        //if we want to create a new finding , pass empty data
        _viewState.mviValue {
          it.copy(finding = FindingDataHolder())
        }
        _viewEffect.value=Effects.StartCreateFindingFragment
    }

    fun editAFinding(findingId:ObjectId) {
        //if we want to edit an existing finding , pass its data
        chosenFindingId=findingId // should happen before calling [getFindingToEdit()]
        getFindingToEdit()
        _viewEffect.value=Effects.StartCreateFindingFragment
    }

    fun saveFinding(finding:FindingDataHolder){

        val findings=data[chosenStudyPlaceId]!!
            .reportList[chosenReportId]!!
            .findingArr[finding.priority.toInt()]


        if (findings.containsKey(finding.id)){
            crud.updateFinding(finding)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    //update the finding
                    findings[chosenFindingId]=finding
                },{ printIfDbg(TAG,it.message)})
        }
        else{
        crud.saveFinding(chosenReportId,finding)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                val findingVhCellDataArrayList=ArrayList<FindingVhCellData>()
                findingVhCellDataArrayList.addAll(_viewState.currentState().copy().findingArrayList)
                findingVhCellDataArrayList.add(
                    FindingVhCellData(
                        id = finding.id,
                        problem = finding.problem,
                        findingSection = finding.section
                    )
                )
                _viewState.mviValue {
                    it.copy(
                        findingArrayList = findingVhCellDataArrayList
                    )
                }
               findings[finding.id]=finding

            },{ printIfDbg(TAG,it.message)})
        }
    }


    private fun getDate(): String {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        return StringBuilder()
            .append(day).append("/")
            .append(month).append("/")
            .append(year)
            .toString()
    }

    fun setProblemImage(uri: Uri?) {
        _viewState.mviValue {
            it.copy(
                problemImage = uri
            )
        }
    }
}