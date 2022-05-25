package com.e.security.ui

import android.content.res.Resources
import android.net.Uri
import androidx.lifecycle.LiveData
import com.e.security.R
import com.e.security.data.FindingDataHolder
import com.e.security.data.ReportDataHolder
import com.e.security.data.StudyPlaceDataHolder
import com.e.security.data.StudyPlaceDetailsDataHolder
import com.e.security.data.definitions.HmScope
import com.e.security.data.localdatabase.operations.FindingsCrudRepo
import com.e.security.di.scopes.ActivityScope
import com.e.security.ui.recyclerviews.celldata.*
import com.e.security.ui.states.MainViewState
import com.e.security.ui.utils.MviMutableLiveData
import com.e.security.ui.utils.PrevAndCurrentState
import com.e.security.ui.utils.livedata.MviLiveData
import com.e.security.ui.utils.livedata.SingleLiveEvent
import com.e.security.ui.viewmodels.BaseViewModel
import com.e.security.ui.viewmodels.effects.Effects
import com.e.security.usecase.HozerMankalUseCase
import com.e.security.usecase.WriteToWordUseCase
import com.e.security.utils.printErrorIfDbg
import com.e.security.utils.printIfDbg
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import org.bson.types.ObjectId
import java.util.*
import javax.inject.Inject

@ActivityScope
class MainViewModel @Inject constructor(
    private val crud: FindingsCrudRepo,
    private val hozerMankalUSeCase: HozerMankalUseCase,
    private val writeToWordUseCase: WriteToWordUseCase,
    private val resources: Resources
) : BaseViewModel() {

    private val TAG = this.javaClass.name
    private val _viewState = MviMutableLiveData(MainViewState())
    val viewState: MviLiveData<PrevAndCurrentState<MainViewState>> get() = _viewState

    private val _viewEffect = SingleLiveEvent<Effects>()
    val viewEffect: LiveData<Effects> get() = _viewEffect

    private var data = HashMap<ObjectId, StudyPlaceDataHolder>()
    private var chosenStudyPlaceId = ObjectId()
    private var chosenReportId = ObjectId()
    private var chosenFindingId: ObjectId = ObjectId()
    private var chosenHmScope: HmScope = HmScope()

    //edit state
    private var studyPlaceInfoFragmentEditable: Boolean = false


    init {
        onViewModelCreated()
    }

    private fun onViewModelCreated() {
        hozerMankalUSeCase.sortHozerim()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({}, { printErrorIfDbg(TAG, it.message) }).addDisposable()
    }

    fun getStudyPlacesAndTheirFindings() {
        if (data.isNotEmpty()) return

        crud.getAllFindings()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                createStudyPlaceDataVhCell(it)
                data = it
            }, { printErrorIfDbg(TAG, it.message) }).addDisposable()
    }

    fun getReportListOfChosenStudyPlace() {
        //todo check if we can prevent from always do that

        val tmpArray = ArrayList<ReportVhCell>()
        data[chosenStudyPlaceId]!!.reportList.forEach { reportList ->
            val value = reportList.value
            val reportVhCell = ReportVhCell(
                id = value.id,
                date = value.date
            )
            tmpArray.add(reportVhCell)
        }
        updateReportVhCellArrayList(tmpArray)
    }

    fun getReportListFindings() {
        val tmpArray = ArrayList<FindingVhCell>()
        data[chosenStudyPlaceId]!!.reportList[chosenReportId]!!
            .findingArr.forEach { findingPriorityList ->
                findingPriorityList.forEach { finding ->
                    val value = finding.value
                    val reportVhCell = FindingVhCell(
                        id = value.id,
                        problem = value.problem,
                        findingSection = value.testArea
                    )
                    tmpArray.add(reportVhCell)
                }
            }

        updateFindingVhCellArrayList(tmpArray)
    }

    private fun createStudyPlaceDataVhCell(data: HashMap<ObjectId, StudyPlaceDataHolder>) {

        val tmpArray = ArrayList<StudyPlaceDataVhCell>()
        tmpArray.addAll(_viewState.currentState().copy().studyPlacesVhCellArrayList)
        data.forEach { _, value ->
            val obj = StudyPlaceDataVhCell(
                value.id,
                value.reportDetails.placeName,
                value.reportDetails.city
            )
            tmpArray.add(obj)
        }

        _viewState.mviValue {
            it.copy(studyPlacesVhCellArrayList = tmpArray)
        }

    }

    fun createNewReport() {
        val date = getDate()
        val reportListId = ObjectId()
        crud.createNewReport(chosenStudyPlaceId, reportListId, date)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                val newReport = ReportDataHolder()
                newReport.id = reportListId
                newReport.date = date

                data[chosenStudyPlaceId]!!.reportList[reportListId] = newReport
                val tmpArray = ArrayList<ReportVhCell>()
                tmpArray.addAll(_viewState.currentState().copy().reportVhCellArrayList)
                tmpArray.add(
                    ReportVhCell(reportListId, date)
                )

                updateReportVhCellArrayList(tmpArray)
            }, { printIfDbg(TAG, it.message) }).addDisposable()
    }

    fun getChosenHozerMankal() {

    }

    fun getStudyPlaceDetailsForEdit(id: ObjectId) {
        studyPlaceInfoFragmentEditable = true
        chosenStudyPlaceId = id
        val details = data[id]!!.reportDetails
        setPlaceDetailsUiValue(details)
        showStudyPlaceInfoDialogFragment()
    }

    fun showEmptyStudyPlaceInfoDialogFragment() {
        //reset its data we we want to create a new StudyPlace
        setPlaceDetailsUiValue(StudyPlaceDetailsDataHolder())
        // screen is not in edit state
        studyPlaceInfoFragmentEditable = false
        //show the dialog
        showStudyPlaceInfoDialogFragment()
    }

    private fun showStudyPlaceInfoDialogFragment() {
        _viewEffect.value = Effects.ShowStudyPlaceInfoDialogFragment
    }

    fun changeEducationalInstitution(educationalInstitution: TextViewVhCell) {
        val details = _viewState.currentState().studyPlaceInfoFragmentState.reportDetails.copy(
            educationalInstitution = educationalInstitution.item
        )
        setPlaceDetailsUiValue(details)
    }

    fun editExportDeleteMenuSelection(textViewVhCell: TextViewVhCell) {
        when (textViewVhCell.item) {
            resources.getString(R.string.edit) -> showCalendarDialog()
            resources.getString(R.string.export) -> exportReport()
            resources.getString(R.string.delete) -> showDeleteReportDialog(::deleteReport)

        }
    }

    private fun exportReport() {
        val studyPlace = data[chosenStudyPlaceId]
        val studyPlaceDetailsDataHolder = data[chosenStudyPlaceId]!!.reportDetails
        val chosenReport = studyPlace!!.reportList[chosenReportId]!!
        writeToWordUseCase.write(chosenReport, studyPlaceDetailsDataHolder)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({
                _viewEffect.value=Effects.StartActivityForResult(it)
//                _viewEffect.value=Effects.Toast(it)
            },::printErrorIfDbg)
    }

    fun setChosenReportId(id: ObjectId) {
        chosenReportId = id
    }

    fun setChosenFindingId(id: ObjectId) {
        chosenFindingId = id
    }

    fun setChosenStudyplaceId(id: ObjectId) {
        chosenStudyPlaceId = id
    }

    private fun setPlaceDetailsUiValue(details: StudyPlaceDetailsDataHolder) {
        _viewState.mviValue {
            it.copy(
                studyPlaceInfoFragmentState = it.studyPlaceInfoFragmentState.copy(
                    reportDetails = details
                )
            )
        }
    }


    fun createNewStudyPlace(details: StudyPlaceDetailsDataHolder) {

        if (studyPlaceInfoFragmentEditable) {
            updateStudyPlaceDetails(details)
            return
        } else {
            val id = ObjectId()
            crud.createNewStudyPlace(id, details)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    val placeDataHolder = StudyPlaceDataHolder()
                    placeDataHolder.id = id
                    placeDataHolder.reportDetails = details

                    data[placeDataHolder.id] = placeDataHolder
                    val copyArr =
                        ArrayList(_viewState.currentState().copy().studyPlacesVhCellArrayList)
                    val studyPlaceVhCellData =
                        StudyPlaceDataVhCell(
                            id = placeDataHolder.id,
                            placeName = details.placeName,
                            city = details.city
                        )

                    copyArr.add(studyPlaceVhCellData)

                    updateStudyPlacesVhCellsState(copyArr)

                }, { printErrorIfDbg(TAG, it.message) }).addDisposable()
        }
    }

    private fun updateStudyPlaceDetails(details: StudyPlaceDetailsDataHolder) {
        val id = chosenStudyPlaceId
        crud.updateStudyPlace(id, details)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                //update study place details
                data[chosenStudyPlaceId]!!.reportDetails = details

                val copyArr = ArrayList(_viewState.currentState().studyPlacesVhCellArrayList)

                copyArr.forEachIndexed { index, studyPlaceDataVhCell ->
                    if (studyPlaceDataVhCell.id == chosenStudyPlaceId) {
                        copyArr[index] = studyPlaceDataVhCell.copy(
                            placeName = details.placeName,
                            city = details.city
                        )
                    }
                }

                updateStudyPlacesVhCellsState(copyArr)

            }, { printErrorIfDbg(TAG, it.message) }).addDisposable()
    }

    private fun updateStudyPlacesVhCellsState(list: ArrayList<StudyPlaceDataVhCell>) {
        _viewState.mviValue {
            it.copy(
                studyPlacesVhCellArrayList = list
            )
        }
    }

    private fun getFindingToEdit() {
        val findingArr = data[chosenStudyPlaceId]!!
            .reportList[chosenReportId]!!
            .findingArr

        var finding: FindingDataHolder? = null

        findingArr.forEach {
            if (it.containsKey(chosenFindingId)) {
                finding = it[chosenFindingId]!!
                return@forEach
            }
        }
        updateChosenFindingUi(finding!!)
    }


    private fun updateChosenFindingUi(finding: FindingDataHolder) {
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
        _viewEffect.value = Effects.StartCreateFindingFragment
    }

    fun editAFinding(findingId: ObjectId) {
        //if we want to edit an existing finding , pass its data
        chosenFindingId = findingId // should happen before calling [getFindingToEdit()]
        getFindingToEdit()
        _viewEffect.value = Effects.StartCreateFindingFragment
    }

    fun saveFinding(finding: FindingDataHolder) {

        val findingsArr = data[chosenStudyPlaceId]!!
            .reportList[chosenReportId]!!
            .findingArr
        var findings: HashMap<ObjectId, FindingDataHolder>? = null

        findingsArr.forEach {
            if (it.containsKey(finding.id)) {
                findings = it
                return@forEach
            }
        }

        //if finding doesn't exists, get proper Hm for inserting the new one
        if (findings == null) {
            findings = findingsArr[finding.priority.toInt()]
        }

        if (findings!!.containsKey(finding.id)) {
            crud.updateFinding(finding)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    // check if priority has changed , if it has,
                    // remove the finding from  prev hm and insert to correct one
                    if (findings!![finding.id]!!.priority != finding.priority) {
                        findings!!.remove(finding.id)
                        findingsArr[finding.priority.toInt()][finding.id] = finding
                    } else {// else we need to update the finding in same hm
                        findings!![finding.id] = finding
                    }
                    val findingVhCellArrayList = ArrayList<FindingVhCell>()
                    _viewState.currentState().copy().findingVhCellArrayList.forEach {
                        val tmpFinding: FindingVhCell =
                            if (it.id != finding.id) {
                                it
                            } else {
                                FindingVhCell(
                                    id = finding.id,
                                    problem = finding.problem,
                                    findingSection = finding.testArea
                                )
                            }
                        findingVhCellArrayList.add(tmpFinding)
                    }
                    updateFindingVhCellArrayList(findingVhCellArrayList)

                }, { printIfDbg(TAG, it.message) }).addDisposable()
        } else {
            crud.saveFinding(chosenReportId, finding)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    val findingVhCellArrayList = ArrayList<FindingVhCell>()
                    findingVhCellArrayList.addAll(
                        _viewState.currentState().copy().findingVhCellArrayList
                    )
                    findingVhCellArrayList.add(
                        FindingVhCell(
                            id = finding.id,
                            problem = finding.problem,
                            findingSection = finding.testArea
                        )
                    )
                    findings!![finding.id] = finding
                    updateFindingVhCellArrayList(findingVhCellArrayList)
                }, { printIfDbg(TAG, it.message) }).addDisposable()
        }
    }

    private fun updateFindingVhCellArrayList(findingArrayList: ArrayList<FindingVhCell>) {
        _viewState.mviValue {
            it.copy(
                findingVhCellArrayList = findingArrayList
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

    fun showCalendarDialog() {
        _viewEffect.value = Effects.ShowCalendarDialog
    }

    fun showStringRecyclerViewDialog(items: Array<String>, func: (TextViewVhCell) -> Unit) {
        val tvList = ArrayList<TextViewVhCell>()
        items.forEach {
            tvList.add(
                TextViewVhCell(item = it)
            )
        }
        _viewEffect.value = Effects.ShowStringRecyclerViewDialog(tvList, func)
    }


    fun setReportDate(date: String) {
        crud.updateReportDate(chosenReportId, date)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                toast(it)
                data[chosenStudyPlaceId]!!
                    .reportList[chosenReportId]!!
                    .date = date
                updateReportDateUi(date)
            }, ::printErrorIfDbg)
            .addDisposable()
    }

    private fun updateReportDateUi(date: String) {

        val newArr: ArrayList<ReportVhCell> =
            ArrayList(_viewState.currentState().copy().reportVhCellArrayList)

        newArr.forEachIndexed { index, reportVhCell ->
            if (reportVhCell.id == chosenReportId) {
                newArr[index] = reportVhCell.copy(date = date)
            }
        }
        updateReportVhCellArrayList(newArr)
    }

    private fun showDeleteDialog(message: Int, func: () -> Unit) {
        _viewEffect.value = Effects.ShowDeleteDialog(message, func)
    }

    fun showDeleteStudyPlaceDialog(id: ObjectId, func: () -> Unit) {
        chosenStudyPlaceId = id
        showDeleteDialog(R.string.delete_study_place, func)
    }

    fun showDeleteReportDialog(func: () -> Unit) {
        showDeleteDialog(R.string.delete_report, func)
    }

    fun showDeleteFindingDialog(id: ObjectId, func: () -> Unit) {
        chosenFindingId = id
        showDeleteDialog(R.string.delete_finding, func)
    }


    fun deleteStudyPlace() {
        crud.deleteStudyPlace(chosenStudyPlaceId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                //todo does chosenStudyPlaceId need a reset?
                data.remove(chosenStudyPlaceId)

                val newArr = ArrayList(_viewState.currentState().studyPlacesVhCellArrayList)

                newArr.forEach { studyPlaceVhCell ->
                    if (studyPlaceVhCell.id == chosenStudyPlaceId) {
                        newArr.remove(studyPlaceVhCell)
                        return@forEach
                    }
                }
                updateStudyPlacesVhCellsState(newArr)
                toast(it)
            }, ::printErrorIfDbg)
            .addDisposable()
    }

    fun deleteFinding() {
        crud.deleteFinding(chosenFindingId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                data[chosenStudyPlaceId]!!
                    .reportList[chosenReportId]!!
                    .findingArr.forEach { finding ->
                        if (finding.containsKey(chosenFindingId)) {
                            finding.remove(chosenFindingId)
                            return@forEach
                        }
                        val newArr: ArrayList<FindingVhCell> =
                            ArrayList(_viewState.currentState().findingVhCellArrayList)
                        newArr.forEach {
                            if (it.id == chosenFindingId) {
                                newArr.remove(it)
                            }
                        }
                        updateFindingVhCellArrayList(newArr)
                        toast(R.string.deleted_successfully)
                    }
            }, ::printErrorIfDbg)
            .addDisposable()
    }

    fun deleteReport() {
        crud.deleteReport(chosenReportId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                //todo maybe we need to reset chosenReportId?
                data[chosenStudyPlaceId]!!.reportList.remove(chosenReportId)
                val newArr = ArrayList(_viewState.currentState().reportVhCellArrayList)
                newArr.forEach { reportVhCell ->
                    if (reportVhCell.id == chosenReportId) {
                        newArr.remove(reportVhCell)
                        return@forEach
                    }
                }
                updateReportVhCellArrayList(newArr)
                toast(it)
            }, ::printErrorIfDbg)
            .addDisposable()
    }

    private fun updateReportVhCellArrayList(newArray: ArrayList<ReportVhCell>) {
        _viewState.mviValue {
            it.copy(
                reportVhCellArrayList = newArray
            )
        }
    }

     fun toast(stringRes: Int) {
        _viewEffect.value = Effects.Toast(resources.getString(stringRes))
    }

    fun filterHozerMankal(string: String): Observable<List<HozerMankalVhCell>> {
        return hozerMankalUSeCase.filterHozerMankal(string)
            .map {
                HozerMankalVhCell(
                    requirement = it.definition,
                    sectionInAssessmentList = it.section,
                    testArea = it.testArea
                )
            }
            .toList().toObservable()

    }

    fun showHozerMankalDialog() {
        _viewEffect.value = Effects.ShowHozerMankalDialog
    }

    fun changeRequirement(vhCell: HozerMankalVhCell) {
        val finding = _viewState.currentState().createFindingFragmentState.finding.copy(
            requirement = vhCell.requirement,
            sectionInAssessmentList = vhCell.sectionInAssessmentList,
            testArea = vhCell.testArea
        )
        updateChosenFindingUi(finding)
    }

    fun saveFile(uri: Uri) {
        val studyPlace = data[chosenStudyPlaceId]
        val studyPlaceDetailsDataHolder = data[chosenStudyPlaceId]!!.reportDetails
        val chosenReport = studyPlace!!.reportList[chosenReportId]!!

            writeToWordUseCase.saveFile(uri,chosenReport, studyPlaceDetailsDataHolder)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {

                }
                .subscribe({
                    _viewEffect.value=Effects.Toast(it)

                },::printErrorIfDbg)
    }

    fun popFragment() {
        _viewEffect.value=Effects.PopBackStack
    }

}