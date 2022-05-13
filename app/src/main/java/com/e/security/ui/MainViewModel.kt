package com.e.security.ui

import android.net.Uri
import androidx.lifecycle.LiveData
import com.e.security.data.FindingDataHolder
import com.e.security.data.ReportDataHolder
import com.e.security.data.ReportDetailsDataHolder
import com.e.security.data.StudyPlaceDataHolder
import com.e.security.data.localdatabase.operations.FindingsCrudRepo
import com.e.security.di.scopes.ActivityScope
import com.e.security.ui.recyclerviews.celldata.FindingVhCellData
import com.e.security.ui.recyclerviews.celldata.ReportVhCell
import com.e.security.ui.recyclerviews.celldata.StudyPlaceDataVhCell
import com.e.security.ui.recyclerviews.celldata.TextViewVhCell
import com.e.security.ui.states.MainViewState
import com.e.security.ui.utils.MviMutableLiveData
import com.e.security.ui.utils.PrevAndCurrentState
import com.e.security.ui.utils.livedata.MviLiveData
import com.e.security.ui.utils.livedata.SingleLiveEvent
import com.e.security.ui.viewmodels.BaseViewModel
import com.e.security.ui.viewmodels.effects.Effects
import com.e.security.usecase.HozerMankalUseCase
import com.e.security.utils.printErrorIfDbg
import com.e.security.utils.printIfDbg
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import org.bson.types.ObjectId
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

@ActivityScope
class MainViewModel @Inject constructor(
    private val crud: FindingsCrudRepo,
    private val hozerMankalUSeCase: HozerMankalUseCase
) : BaseViewModel() {

    private val TAG = this.javaClass.name
    private val _viewState = MviMutableLiveData(MainViewState())
    val viewState: MviLiveData<PrevAndCurrentState<MainViewState>> get() = _viewState

    private val _viewEffect = SingleLiveEvent<Effects>()
    val viewEffect: LiveData<Effects> get() = _viewEffect

    private var data=HashMap<ObjectId,StudyPlaceDataHolder>()
    private var chosenStudyPlaceId = ObjectId()
    private var chosenReportId = ObjectId()
    private var chosenFindingId: ObjectId = ObjectId()


    init {
        onViewModelCreated()
    }

    private fun onViewModelCreated(){
        hozerMankalUSeCase.sortHozerim()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({},{ printErrorIfDbg(TAG,it.message) })
    }

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
                value.reportDetails.placeName,
                value.reportDetails.city
            )
            tmpArray.add(obj)
        }

        _viewState.mviValue {
            it.copy(studyPlacesVhCells = tmpArray)
        }

    }

    fun createNewReport() {
        val date = getDate()
        val reportListId = ObjectId()
        crud.createNewReport(chosenStudyPlaceId, reportListId, date)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                val newReport=ReportDataHolder()
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

    fun getChosenHozerMankal(){

    }


    fun createNewStudyPlace(placeDetails: ReportDetailsDataHolder) {
        val id=ObjectId()
        crud.createNewStudyPlace(id,placeDetails)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                val placeDataHolder=StudyPlaceDataHolder()
                placeDataHolder.id=id
                placeDataHolder.reportDetails=placeDetails

                data[placeDataHolder.id]=placeDataHolder
                val copyArr = _viewState.currentState().copy().studyPlacesVhCells
                val studyPlaceVhCellData =
                    StudyPlaceDataVhCell(
                        id=placeDataHolder.id,
                        placeName = placeDetails.placeName,
                        city = placeDetails.city)
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
               return@forEach
           }
       }
       printIfDbg(TAG,"finding ${finding!!.priority}")

       updateChosenFindingUi(finding!!)
    }

    private fun updateChosenFindingUi(finding: FindingDataHolder){
        _viewState.mviValue {
            it.copy(
                createFindingFragmentState = it.createFindingFragmentState.copy(
                    finding = finding
                )
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
        updateChosenFindingUi(FindingDataHolder())
        _viewEffect.value=Effects.StartCreateFindingFragment
    }

    fun editAFinding(findingId:ObjectId) {
        //if we want to edit an existing finding , pass its data
        chosenFindingId=findingId // should happen before calling [getFindingToEdit()]
        getFindingToEdit()
        _viewEffect.value=Effects.StartCreateFindingFragment
    }

    fun saveFinding(finding:FindingDataHolder){

        val findingsArr=data[chosenStudyPlaceId]!!
            .reportList[chosenReportId]!!
            .findingArr
        var findings:HashMap<ObjectId,FindingDataHolder>?=null

        findingsArr.forEach {
                if (it.containsKey(finding.id)){
                    findings=it
                    return@forEach
                }
            }

        //if finding doesn't exists, get proper Hm for inserting the new one
      if (findings==null){
          findings=findingsArr[finding.priority.toInt()]
      }

        if (findings!!.containsKey(finding.id)){
            crud.updateFinding(finding)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    // check if priority has changed , if it has,
                    // remove the finding from  prev hm and insert to correct one
                    if (findings!![finding.id]!!.priority!=finding.priority){
                        findings!!.remove(finding.id)
                        findingsArr[finding.priority.toInt()][finding.id]=finding
                    }else{// else we need to update the finding in same hm
                        findings!![finding.id]= finding
                    }
                    val findingVhCellDataArrayList=ArrayList<FindingVhCellData>()
                    _viewState.currentState().copy().findingArrayList.forEach {
                        val tmpFinding:FindingVhCellData =
                            if (it.id != finding.id) { it }
                            else{
                            FindingVhCellData(
                                id = finding.id,
                                problem = finding.problem,
                                findingSection = finding.section
                            )
                        }
                        findingVhCellDataArrayList.add(tmpFinding)
                    }
                    updateFindingArrayList(findingVhCellDataArrayList)

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
                findings!![finding.id]=finding
                updateFindingArrayList(findingVhCellDataArrayList)
            },{ printIfDbg(TAG,it.message)})
        }
    }

    fun updateFindingArrayList(findingArrayList:ArrayList<FindingVhCellData>){
        _viewState.mviValue {
            it.copy(
                findingArrayList = findingArrayList
            )
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
                createFindingFragmentState = it.createFindingFragmentState.copy(
                    problemImage = uri
                )
            )
        }
    }

    fun showCalendarDialog(){
        _viewEffect.value=Effects.ShowCalendarDialog
    }

    fun showEducationalInstitutionsDialog(items:Array<String>) {
        val tvList= ArrayList<TextViewVhCell>()
        items.forEach {
            tvList.add(
                TextViewVhCell(item = it)
            )
        }
        _viewEffect.value=Effects.ShowEducationalInstitutionDialog(tvList)
    }

    fun changeEducationalInstitution(educationalInstitution: String) {
        _viewState.mviValue {
            it.copy(
                studyPlaceFragmentState=  it.studyPlaceFragmentState.copy(
                    reportDetails = it.studyPlaceFragmentState.reportDetails.copy(
                        educationalInstitution=educationalInstitution
                    )
                )
            )
        }
    }

    fun setReportDate() {
        TODO("Not yet implemented")
    }
}