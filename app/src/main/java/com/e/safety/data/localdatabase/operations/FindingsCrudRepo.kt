package com.e.safety.data.localdatabase.operations

import android.app.Application
import com.e.safety.R
import com.e.safety.data.FindingDataHolder
import com.e.safety.data.ReportDataHolder
import com.e.safety.data.StudyPlaceDataHolder
import com.e.safety.data.StudyPlaceDetailsDataHolder
import com.e.safety.data.localdatabase.configuration.FindingsDetailsSchema
import com.e.safety.data.localdatabase.utils.*
import com.e.safety.data.objects.FindingRlmObj
import com.e.safety.data.objects.GeneralReportDetailsRlmObj
import com.e.safety.data.objects.ReportRlmObj
import com.e.safety.data.objects.StudyPlaceRlmObj
import com.e.safety.di.scopes.ApplicationScope
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import org.bson.types.ObjectId
import javax.inject.Inject

@ApplicationScope
class FindingsCrudRepo @Inject constructor(
    private val app: Application
) {

    private val realm = FindingsDetailsSchema().getRealmInstance()
    private var cachedStudyPlaceData = HashMap<ObjectId, StudyPlaceDataHolder>()
    private var chosenStudyPlaceId = ObjectId()
    private var chosenReportId = ObjectId()
    private var chosenFindingId: ObjectId = ObjectId()

    fun setChosenFindingId(findingId: ObjectId) {
        chosenFindingId = findingId
    }

    fun getReportLists(placeId: ObjectId): Single<HashMap<ObjectId, ReportDataHolder>> {
        chosenStudyPlaceId = placeId
        return Single.just(cachedStudyPlaceData[chosenStudyPlaceId]!!.reportList)
    }

    fun getChosenReport(reportId: ObjectId): Single<ReportDataHolder> {
        chosenReportId = reportId
        return getReportLists(chosenStudyPlaceId).map { it[reportId]!! }
    }

    fun getReportFindings(): Single<Array<HashMap<ObjectId, FindingDataHolder>>> {
        return getChosenReport(chosenReportId).map { it.findingArr }
    }

    fun getFinding(findingId: ObjectId): Single<FindingDataHolder?> {
        return getReportFindings().map { findingArr ->
            var finding: FindingDataHolder? = null
            findingArr.forEach { findingHmap ->
                if (findingHmap.containsKey(findingId)) {
                    finding = findingHmap[findingId]!!
                    return@forEach
                }
            }
            finding
        }
    }


    //todo check if id is already exists? maybe not needed because objectid
    fun saveFinding(listId: ObjectId, finding: FindingDataHolder): Completable {
        return Completable.create { emitter ->
            try {
                realm.executeTransactionAsync { realm ->
                    val reportRlmObj =
                        realm.where(ReportRlmObj::class.java).equalTo("_id", listId)
                            .findFirst()
                    val newFinding = realm.createObject(FindingRlmObj::class.java, finding.id)
                    newFinding.picPaths = finding.problemImages.toRealmList()
                    newFinding.problem = finding.problem
                    newFinding.problemLocation = finding.problemLocation
                    newFinding.requirement = finding.requirement
                    newFinding.testArea = finding.testArea
                    newFinding.sectionInAssessmentList = finding.sectionInAssessmentList
                    newFinding.priority = finding.priority

                    reportRlmObj!!.findingList.add(newFinding)
                    realm.insertOrUpdate(reportRlmObj)

                    emitter.onComplete()
                }
            } catch (e: Exception) {
                emitter.onError(e)
            }

        }
    }

    fun setConclusion(id: ObjectId, conclusion: String): Single<String> {
        return realm.rxSingleTransactionAsync { realm ->
            val reportRlmObj = realm.findObjectById(ReportRlmObj::class.java, id)
            reportRlmObj!!.conclusion = conclusion
            realm.insertOrUpdate(reportRlmObj)
            app.getString(R.string.saved_successfully)
        }
    }

    fun createNewStudyPlace(
        id: ObjectId,
        placeDetails: StudyPlaceDetailsDataHolder
    ): Single<String> {
        return Single.create { emitter ->
            try {
                realm.executeTransactionAsync { realm ->

                    val obj = realm.createObject(StudyPlaceRlmObj::class.java, id)

                    val generalReportDetailsRlmObj = realm.createObject(
                        GeneralReportDetailsRlmObj::class.java
                    )

                    updateGeneralReportDetails(generalReportDetailsRlmObj, placeDetails)

                    obj.generalReportDetailsRlmObj = generalReportDetailsRlmObj
                    realm.insertOrUpdate(obj)
                    emitter.onSuccess("success")
                }

            } catch (e: Exception) {
                emitter.onError(e)
            }
        }
    }

    fun updateStudyPlace(
        id: ObjectId,
        details: StudyPlaceDetailsDataHolder
    ): Single<String> {
        return Single.create { emitter ->
            try {
                realm.executeTransactionAsync { realm ->
                    val obj = realm.where(StudyPlaceRlmObj::class.java)
                        .equalTo("_id", id)
                        .findFirst()

                    updateGeneralReportDetails(obj!!.generalReportDetailsRlmObj!!, details)
                    realm.insertOrUpdate(obj)
                    emitter.onSuccess("success")
                }
            } catch (e: Exception) {
                emitter.onError(e)
            }
        }
    }

    private fun updateGeneralReportDetails(
        generalReportDetailsRlmObj: GeneralReportDetailsRlmObj,
        placeDetails: StudyPlaceDetailsDataHolder
    ) {
        generalReportDetailsRlmObj.city = placeDetails.city
        generalReportDetailsRlmObj.institutionSymbol = placeDetails.institutionSymbol
        generalReportDetailsRlmObj.placeName = placeDetails.placeName
        generalReportDetailsRlmObj.testerDetails = placeDetails.testerDetails
        generalReportDetailsRlmObj.address = placeDetails.address
        generalReportDetailsRlmObj.studyPlaceParticipants = placeDetails.studyPlaceParticipants
        generalReportDetailsRlmObj.authorityParticipants = placeDetails.authorityParticipants
        generalReportDetailsRlmObj.inspectorDetails = placeDetails.inspectorDetails
        generalReportDetailsRlmObj.managerDetails = placeDetails.managerDetails
        generalReportDetailsRlmObj.ownership = placeDetails.ownership
        generalReportDetailsRlmObj.studentsNumber = placeDetails.studentsNumber
        generalReportDetailsRlmObj.yearOfFounding = placeDetails.yearOfFounding
        generalReportDetailsRlmObj.studyPlacePhone = placeDetails.studyPlacePhone
        generalReportDetailsRlmObj.educationalInstitution = placeDetails.educationalInstitution
    }

    fun createNewReport(placeId: ObjectId, id: ObjectId, date: String): Completable {
        return Completable.create { emitter ->
            try {
                realm.executeTransactionAsync { realm ->
                    val place =
                        realm.where(StudyPlaceRlmObj::class.java).equalTo("_id", placeId)
                            .findFirst()
                    val newReport = realm.createObject(ReportRlmObj::class.java, id)
                    newReport.date = date
                    place!!.reportList.add(newReport)
                    realm.insertOrUpdate(place)
                    emitter.onComplete()
                }

            } catch (e: Exception) {
                emitter.onError(e)
            }
        }
    }

    fun updateFinding(finding: FindingDataHolder): Completable {
        return Completable.create { emitter ->
            try {
                realm.executeTransactionAsync { realm ->
                    val rlmObj = realm.where(FindingRlmObj::class.java)
                        .equalTo("_id", finding.id)
                        .findFirst()
                    rlmObj!!.picPaths = finding.problemImages.toRealmList()
                    rlmObj.problem = finding.problem
                    rlmObj.problemLocation = finding.problemLocation
                    rlmObj.requirement = finding.requirement
                    rlmObj.testArea = finding.testArea
                    rlmObj.sectionInAssessmentList = finding.sectionInAssessmentList
                    rlmObj.priority = finding.priority

                    emitter.onComplete()
                }
            } catch (e: Exception) {
                emitter.onError(e)
            }
        }
    }

    fun updateReportDate(reportId: ObjectId, date: String): Single<Int> {
        return Single.create { emitter ->
            try {
                realm.executeTransactionAsync { realm ->
                    val obj = realm.where(ReportRlmObj::class.java)
                        .equalTo("_id", reportId)
                        .findFirst()
                    obj!!.date = date
                    realm.insertOrUpdate(obj)
                    emitter.onSuccess(R.string.saved_successfully)
                }
            } catch (e: Exception) {
                emitter.onError(e)
            }
        }
    }

    fun getAllStudyPlacesData(): Single<HashMap<ObjectId, StudyPlaceDataHolder>> {

        return realm.getLists(StudyPlaceRlmObj::class.java) { realmResults ->

            val hashMap = HashMap<ObjectId, StudyPlaceDataHolder>()

            realmResults.forEach { studyPlaceRlmObj ->

                val studyPlaceDataHolder = StudyPlaceDataHolder()
                val generalReportDetailsRlmObj = studyPlaceRlmObj.generalReportDetailsRlmObj
                studyPlaceDataHolder.id = studyPlaceRlmObj._id

                studyPlaceDataHolder.reportDetails = StudyPlaceDetailsDataHolder(
                    city = generalReportDetailsRlmObj!!.city,
                    placeName = generalReportDetailsRlmObj.placeName,
                    institutionSymbol = generalReportDetailsRlmObj.institutionSymbol,
                    testerDetails = generalReportDetailsRlmObj.testerDetails,
                    ownership = generalReportDetailsRlmObj.ownership,
                    studentsNumber = generalReportDetailsRlmObj.studentsNumber,
                    address = generalReportDetailsRlmObj.address,
                    yearOfFounding = generalReportDetailsRlmObj.yearOfFounding,
                    studyPlacePhone = generalReportDetailsRlmObj.studyPlacePhone,
                    managerDetails = generalReportDetailsRlmObj.managerDetails,
                    inspectorDetails = generalReportDetailsRlmObj.inspectorDetails,
                    studyPlaceParticipants = generalReportDetailsRlmObj.studyPlaceParticipants,
                    authorityParticipants = generalReportDetailsRlmObj.authorityParticipants,
                    educationalInstitution = generalReportDetailsRlmObj.educationalInstitution,
                )

                val reportArrayList = HashMap<ObjectId, ReportDataHolder>()

                studyPlaceRlmObj.reportList.forEach { findingListRlmObj ->

                    val findingListDataHolder = ReportDataHolder()
                    findingListDataHolder.id = findingListRlmObj._id
                    findingListDataHolder.date = findingListRlmObj.date
                    findingListDataHolder.conclusion = findingListRlmObj.conclusion

                    findingListRlmObj.findingList.forEach { findingRealmObj ->
                        val findingsDataObj = FindingDataHolder(
                            id = findingRealmObj._id,
                            testArea = findingRealmObj.testArea,
                            priority = findingRealmObj.priority,
                            sectionInAssessmentList = findingRealmObj.sectionInAssessmentList,
                            problemLocation = findingRealmObj.problemLocation,
                            requirement = findingRealmObj.requirement,
                            problem = findingRealmObj.problem,
                            problemImages = findingRealmObj.picPaths.toArrayList()
                        )
                        //sort according to priorities
                        findingListDataHolder.findingArr[findingsDataObj.priority.toInt()][findingsDataObj.id] =
                            findingsDataObj
                    }
                    reportArrayList[findingListDataHolder.id] = findingListDataHolder
                    studyPlaceDataHolder.reportList = reportArrayList
                }
                hashMap[studyPlaceDataHolder.id] = studyPlaceDataHolder
            }
            hashMap
        }
    }

    fun deleteFinding(id: ObjectId): Single<String> {
        return realm.rxSingleTransactionAsync { realm ->
            val obj = realm.findObjectById(FindingRlmObj::class.java, id)
            obj!!.deleteFromRealm()

            app.getString(R.string.deleted_successfully)
        }
    }

    fun deleteReport(id: ObjectId): Single<Int> {
        return realm.rxSingleTransactionAsync { realm ->
            val obj = realm.findObjectById(ReportRlmObj::class.java, id)
            for (i in obj!!.findingList.size - 1 downTo 0) {
                obj.findingList.deleteFromRealm(i)
            }
            obj.deleteFromRealm()
            R.string.deleted_successfully
        }
    }

    fun deleteStudyPlace(id: ObjectId): Single<Int> {
        return realm.rxSingleTransactionAsync { realm ->
            val studyPlaceRlmObj = realm.findObjectById(StudyPlaceRlmObj::class.java, id)!!

            val reportList = studyPlaceRlmObj.reportList

            for (i in reportList.size - 1 downTo 0) {
                val findingList = reportList.elementAt(i).findingList
                for (j in findingList.size - 1 downTo 0) {
                    findingList.deleteFromRealm(j)
                }
                reportList.deleteFromRealm(i)
            }
            studyPlaceRlmObj.deleteFromRealm()

            R.string.deleted_successfully
        }
    }
}