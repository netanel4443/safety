package com.e.safety.ui.viewmodels

import android.content.res.Resources
import android.net.Uri
import androidx.lifecycle.LiveData
import com.e.fakerestapi.ui.recyclerviews.helpers.GenericVhItem
import com.e.safety.R
import com.e.safety.data.FindingDataHolder
import com.e.safety.data.ReportDataHolder
import com.e.safety.data.StudyPlaceDataHolder
import com.e.safety.data.StudyPlaceDetailsDataHolder
import com.e.safety.data.definitions.HmScope
import com.e.safety.data.firebase.UserAccess
import com.e.safety.data.localdatabase.operations.FindingsCrudRepo
import com.e.safety.di.scopes.ActivityScope
import com.e.safety.ui.recyclerviews.celldata.*
import com.e.safety.ui.states.MainViewState
import com.e.safety.ui.utils.livedata.MviMutableLiveData2
import com.e.safety.ui.utils.livedata.SingleLiveEvent
import com.e.safety.ui.viewmodels.effects.Effects
import com.e.safety.usecase.HozerMankalUseCase
import com.e.safety.usecase.SaveFileUseCase
import com.e.safety.usecase.WriteToWordUseCase
import com.e.safety.utils.printErrorIfDbg
import com.e.safety.utils.printIfDbg
import com.e.safety.utils.subscribeBlock
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import org.bson.types.ObjectId
import java.util.*
import javax.inject.Inject

@ActivityScope
class MainViewModel @Inject constructor(
    private val crud: FindingsCrudRepo,
    private val hozerMankalUSeCase: HozerMankalUseCase,
    private val writeToWordUseCase: WriteToWordUseCase,
    private val saveFileUseCase: SaveFileUseCase,
    private val resources: Resources,
    private val userAccess: UserAccess
) : BaseViewModel<MainViewState>() {

    private val TAG = this.javaClass.name

    override val _viewState: MviMutableLiveData2<MainViewState> =
        MviMutableLiveData2(MainViewState())

    private val _viewEffect = SingleLiveEvent<Effects>()
    val viewEffect: LiveData<Effects> get() = _viewEffect

    private var data = HashMap<ObjectId, StudyPlaceDataHolder>()
    private var chosenStudyPlaceId = ObjectId()
    private var chosenReportId = ObjectId()
    private var chosenFindingId: ObjectId = ObjectId()

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

        crud.getAllStudyPlacesData()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                createStudyPlaceDataVhCell(it)
                data = it
                printIfDbg(TAG, "items $it")
            }, { printErrorIfDbg(TAG, it.message) }).addDisposable()
    }

    fun getReportListOfChosenStudyPlace() {
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
        val report = data[chosenStudyPlaceId]!!.reportList[chosenReportId]!!
        report.findingArr.forEach { findingPriorityList ->
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
        updateUpdateReportConclusionState(report.conclusion)
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
                tmpArray.addAll(
                    _viewState.currentState().copy().reportFragmentState.reportVhCellArrayList
                )
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
            resources.getString(R.string.export_word) -> exportWord()
            resources.getString(R.string.export_pdf) -> exportPdf()
            resources.getString(R.string.delete) -> showDeleteReportDialog()
        }
    }


    fun setChosenReportId(id: ObjectId) {
        chosenReportId = id
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
        fitProblemImagesToVhCell(finding!!)
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
        val findingDataHolder = FindingDataHolder()
        //reset [chosenFindingId]
        chosenFindingId = findingDataHolder.id
        updateChosenFindingUi(findingDataHolder)
        updateProblemImageList(ArrayList()) // reset images
        _viewEffect.value = Effects.StartCreateFindingFragment
    }

    fun editAFinding(findingId: ObjectId) {
        //if we want to edit an existing finding , pass its data
        chosenFindingId = findingId // should happen before calling [getFindingToEdit()]
        getFindingToEdit()
        _viewEffect.value = Effects.StartCreateFindingFragment
    }

    fun saveFinding(
        finding: FindingDataHolder,
        problemImages: ArrayList<ImageViewVhCell>,
    ) {

        val findingsArr = data[chosenStudyPlaceId]!!
            .reportList[chosenReportId]!!
            .findingArr

        //todo maybe copy is not necessary in this case because its an int and not an object
        val oldPriority =
            _viewState.currentState().createFindingFragmentState.finding.copy().priority.toInt()

        //findings will hold a reference to the appropriate hashmap according to the priority.
        // First , checks if the object is already exists
        val findings = if (findingsArr[oldPriority].containsKey(chosenFindingId)) {
            findingsArr[oldPriority]
        } else {
            findingsArr[finding.priority.toInt()]
        }

        val images = ArrayList<String>()
        problemImages.forEach {
            images.add(it.image)
        }

        //problem images are empty , this adds the problem images to the object
        finding.problemImages = images

        if (findings.containsKey(chosenFindingId)) {
            /** @param chosenFindingId's value is being set at another places in the code
             * 1) when a new finding is being created at [startCreateFindingFragment]
             * 2) when a finding is being edited at the function of [FindingsFragment] class
             * which is called [initRecyclerView]  */
            finding.id = chosenFindingId
            crud.updateFinding(finding)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    // check if priority has changed , if it has,
                    // remove the finding from  prev hm and insert to correct one
                    printIfDbg(TAG,"${findings[chosenFindingId]!!.priority} , ${finding.priority}")
                    if (findings[chosenFindingId]!!.priority != finding.priority) {
                        findings.remove(chosenFindingId)
                        findingsArr[finding.priority.toInt()][chosenFindingId] = finding
                    } else {// else we need to update the finding in same hm
                        findings[chosenFindingId] = finding
                    }
                    val findingVhCellArrayList = ArrayList<FindingVhCell>()
                    _viewState.currentState().findingFragmentState.copy().findingVhCellArrayList.forEach {
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
                        _viewState.currentState().copy().findingFragmentState.findingVhCellArrayList
                    )
                    findingVhCellArrayList.add(
                        FindingVhCell(
                            id = finding.id,
                            problem = finding.problem,
                            findingSection = finding.testArea
                        )
                    )
                    findings[finding.id] = finding
                    updateFindingVhCellArrayList(findingVhCellArrayList)
                }, { printIfDbg(TAG, it.message) }).addDisposable()
        }
    }

    private fun updateFindingVhCellArrayList(findingArrayList: ArrayList<FindingVhCell>) {
        _viewState.mviValue {
            it.copy(
                findingFragmentState = it.findingFragmentState.copy(
                    findingVhCellArrayList = findingArrayList
                )
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


    fun showCalendarDialog() {
        _viewEffect.value = Effects.ShowCalendarDialog
    }

    fun showStringRecyclerViewDialog(items: Array<String>) {
        _viewEffect.value = Effects.ShowEducationalInstitutionsDialog
        updateEducationalInstitutionsRvItems(items)
    }

    private fun updateEducationalInstitutionsRvItems(items: Array<String>) {
        val tvList = createTextViewVhCell(items)
        _viewState.mviValue {
            it.copy(
                educationalInstitutionsRvItems = tvList
            )
        }
    }

    fun showReportFragmentRecyclerViewMenu(items: Array<String>) {
        _viewEffect.value = Effects.ShowReportFragmentRecyclerViewMenu
        val tvList = createTextViewVhCell(items)
        updateReportFragmentRecyclerViewMenuItems(tvList)
    }

    private fun updateReportFragmentRecyclerViewMenuItems(items: ArrayList<TextViewVhCell>) {
        _viewState.mviValue {
            it.copy(
                reportFragmentMenuRvDialogRvItems = items
            )
        }
    }


    private fun createTextViewVhCell(items: Array<String>): ArrayList<TextViewVhCell> {
        val tvList = ArrayList<TextViewVhCell>()
        items.forEach {
            tvList.add(
                TextViewVhCell(item = it)
            )
        }
        return tvList
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
            ArrayList(_viewState.currentState().copy().reportFragmentState.reportVhCellArrayList)

        newArr.forEachIndexed { index, reportVhCell ->
            if (reportVhCell.id == chosenReportId) {
                newArr[index] = reportVhCell.copy(date = date)
            }
        }
        updateReportVhCellArrayList(newArr)
    }

    fun showDeleteStudyPlaceDialog(id: ObjectId) {
        chosenStudyPlaceId = id
        _viewEffect.value = Effects.ShowDeleteStudyPlaceDialog
    }

    fun showDeleteReportDialog() {
        _viewEffect.value = Effects.ShowDeleteReportDialog
    }

    fun showDeleteFindingDialog(id: ObjectId) {
        chosenFindingId = id
        _viewEffect.value = Effects.ShowDeleteFindingDialog
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
                            ArrayList(_viewState.currentState().findingFragmentState.findingVhCellArrayList)
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
                val newArr =
                    ArrayList(_viewState.currentState().reportFragmentState.reportVhCellArrayList)
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
                reportFragmentState = it.reportFragmentState.copy(
                    reportVhCellArrayList = newArray
                )
            )
        }
    }

    fun toast(stringRes: Int) {
        _viewEffect.value = Effects.Toast(resources.getString(stringRes))
    }

    fun filterHozerMankal(string: String): Observable<ArrayList<GenericVhItem>> {
        return hozerMankalUSeCase.filterHozerMankal(string)
            .map(::mapHozerMankalToState)
            .toObservable()
    }

    private fun mapHozerMankalToState(hm: HashMap<String, ArrayList<HmScope>>): ArrayList<GenericVhItem> {
        val arrList = ArrayList<GenericVhItem>()
        hm.entries.forEach { entry ->

            val textViewVhCell = TextViewVhCell(entry.key, entry.key)
            arrList.add(textViewVhCell)

            entry.value.forEach { hmScope ->
                arrList.add(
                    HozerMankalVhCell(
                        ObjectId(),
                        requirement = hmScope.definition,
                        sectionInAssessmentList = hmScope.section,
                        testArea = hmScope.testArea
                    )
                )
            }
        }
        return arrList
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

    fun saveWordFile(uri: Uri) {
        val studyPlace = data[chosenStudyPlaceId]
        val studyPlaceDetailsDataHolder = data[chosenStudyPlaceId]!!.reportDetails
        val chosenReport = studyPlace!!.reportList[chosenReportId]!!
        val single = writeToWordUseCase
            .saveWordFile(uri, chosenReport, studyPlaceDetailsDataHolder)
        saveFile(single)
    }

    fun savePdfFile(uri: Uri) {
        val single = writeToWordUseCase.savePdfFile(uri)
        saveFile(single)
    }

    private fun saveFile(single: Single<String>) {
        single.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _viewEffect.value = Effects.Toast(it)
            }, ::printErrorIfDbg)
            .addDisposable()
    }


    private fun exportPdf() {
        showReportLoadingBar(true)
        checkExportPermission()
            .flatMap { exportFile(writeToWordUseCase::exportPdf) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBlock({
                _viewEffect.value = Effects.StartActivityForResultPdf(it)
                showReportLoadingBar(false)
            }, {
                showReportLoadingBar(false)
            })
            .addDisposable()
    }

    private fun showReportLoadingBar(visibility: Boolean) {
        val tmpArr = ArrayList(
            _viewState.currentState().reportFragmentState.reportVhCellArrayList.clone()
                    as ArrayList<ReportVhCell>
        )
        tmpArr.forEachIndexed { index, reportVhCell ->
            if (reportVhCell.id == chosenReportId) {
                val newItem = reportVhCell.copy(isLoading = visibility)
                tmpArr[index] = newItem
                return@forEachIndexed
            }
        }
        updateReportVhCellArrayList(tmpArr)
    }

    private fun exportWord() {
        showReportLoadingBar(true)
        checkExportPermission()
            .flatMap { writeToWordUseCase.getWordFileType() }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBlock({
                _viewEffect.value = Effects.StartActivityForResultWord(it)
                showReportLoadingBar(false)
            }, { showReportLoadingBar(false) } )
            .addDisposable()
    }

    private fun exportFile(
        func: (
            reportDataHolder: ReportDataHolder,
            studyPlaceDetailsDataHolder: StudyPlaceDetailsDataHolder
        ) -> Single<String>,
    ): Single<String> {
        val studyPlace = data[chosenStudyPlaceId]
        val studyPlaceDetailsDataHolder = data[chosenStudyPlaceId]!!.reportDetails
        val chosenReport = studyPlace!!.reportList[chosenReportId]!!

        return func.invoke(chosenReport, studyPlaceDetailsDataHolder)
    }


    fun popFragment() {
        _viewEffect.value = Effects.PopBackStack
    }

    fun getAppropriateHozerItems() {
        hozerMankalUSeCase.selectedHozerMankal(
            data[chosenStudyPlaceId]!!.reportDetails.educationalInstitution
        ).map(::mapHozerMankalToState)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBlock { updateChosenHozerMankalRecyclerItems(it) }
            .addDisposable()
    }

    fun updateChosenHozerMankalRecyclerItems(items: List<GenericVhItem>) {
        val newState = _viewState.value!!.createFindingFragmentState.copy(
            chosenHozerMankalRecyclerItems = items
        )
        updateCreateFindingFragmentState(newState)
    }

    private fun updateCreateFindingFragmentState(state: MainViewState.CreateFindingFragmentState) {
        _viewState.mviValue {
            it.copy(
                createFindingFragmentState = state
            )
        }
    }


    fun showPhotoUploadDialog(items: Array<String>) {
        _viewEffect.value = Effects.ShowPhotoUploadDialog
        updateImageOptionsItems(items)
    }

    private fun updateImageOptionsItems(items: Array<String>) {
        val tvhc = createTextViewVhCell(items)
        _viewState.mviValue {
            it.copy(
                createFindingFragmentState = it.createFindingFragmentState.copy(
                    imageOptionsRvItems = tvhc
                )
            )
        }
    }

    fun uploadPhotoUserSelection(item: TextViewVhCell) {
        when (item.item) {
            resources.getString(R.string.take_photo) -> takePhoto()
            resources.getString(R.string.select_photo) -> selectPhoto()
        }
    }

    private fun takePhoto() {
        _viewEffect.value = Effects.TakePhoto
    }

    private fun selectPhoto() {
        _viewEffect.value = Effects.SelectPhoto
    }

    fun showReportConclusionDialog() {
        _viewEffect.value =
            Effects.ShowReportConclusionDialog(_viewState.currentState().findingFragmentState.conclusion)
    }

    fun isReportConclusionDialogVisible(visibility: Boolean) {
        _viewState.mviValue {
            it.copy(
                findingFragmentState = it.findingFragmentState.copy(
                    reportConclusionDialogVisibility = visibility
                )
            )
        }
    }

    fun saveReportConclusion(conclusion: String) {
        crud.setConclusion(chosenReportId, conclusion)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBlock { message ->
                data[chosenStudyPlaceId]!!.reportList[chosenReportId]!!.conclusion = conclusion
                updateUpdateReportConclusionState(conclusion)
                _viewEffect.value = Effects.Toast(message)
            }
    }

    fun updateUpdateReportConclusionState(conclusion: String) {
        _viewState.mviValue {
            it.copy(
                findingFragmentState = it.findingFragmentState.copy(
                    conclusion = conclusion
                )
            )
        }
    }

    fun deleteImage(item: ImageViewVhCell) {
        val imageArrayList =
            ArrayList(_viewState.currentState().createFindingFragmentState.problemImage)
        val newArrayList = imageArrayList.filter {
            it.id != item.id
        }
        updateProblemImageList(newArrayList as ArrayList<ImageViewVhCell>)
    }

    fun addProblemImage(uri: Uri?) {
        uri?.let {
            val imageArrayList =
                ArrayList(_viewState.currentState().createFindingFragmentState.problemImage)
            val uriAsString = uri.toString()
            imageArrayList.add(ImageViewVhCell(uriAsString, uriAsString))
            updateProblemImageList(imageArrayList)
        }
    }

    private fun updateProblemImageList(arrayList: ArrayList<ImageViewVhCell>) {
        _viewState.mviValue {
            it.copy(
                createFindingFragmentState = it.createFindingFragmentState.copy(
                    problemImage = arrayList
                )
            )
        }
    }

    private fun fitProblemImagesToVhCell(finding: FindingDataHolder) {
        val list = finding.problemImages.map {
            printIfDbg(TAG, it)
            ImageViewVhCell(it, it)
        } as ArrayList
        updateProblemImageList(list)
    }

    /**
     * Call this function to save the image,
     * in order to allow future access to an image that isn't
     * in app's directory. In this situation the image has temporary permission
     * so we want to prevent that.
     * */
    fun saveImage(uri: Uri) {
        saveFileUseCase.saveImageToPicturesDir(resources.getString(R.string.app_name), uri)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBlock {
                addProblemImage(it)
            }
    }

    fun checkExportPermission(): Single<Boolean> {
        return userAccess.checkIfAllowedToExportReport().toSingle()

    }
}