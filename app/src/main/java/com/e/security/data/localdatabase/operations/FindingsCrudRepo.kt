package com.e.security.data.localdatabase.operations

import com.e.security.data.*
import com.e.security.data.localdatabase.configuration.FindingsDetailsSchema
import com.e.security.data.localdatabase.utils.getLists
import com.e.security.data.objects.FindingListRlmObj
import com.e.security.data.objects.FindingRlmObj
import com.e.security.data.objects.GeneralReportDetailsRlmObj
import com.e.security.data.objects.StudyPlaceRlmObj
import com.e.security.di.scopes.ApplicationScope
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import org.bson.types.ObjectId
import javax.inject.Inject

@ApplicationScope
class FindingsCrudRepo @Inject constructor() {

    private val realm = FindingsDetailsSchema().getRealmInstance()

    //todo check if id is already exists? maybe not needed because objectid
    fun saveFinding(listId: ObjectId, finding: FindingDataHolder): Completable {
        return Completable.create { emitter ->
            try {
                realm.executeTransactionAsync { realm ->
                    val findingListRlmObj =
                        realm.where(FindingListRlmObj::class.java).equalTo("_id", listId)
                            .findFirst()
                    val newFinding = realm.createObject(FindingRlmObj::class.java, finding.id)
                    newFinding.picPath = finding.pic
                    newFinding.problem = finding.problem
                    newFinding.requirement = finding.requirement
                    newFinding.section = finding.section
                    newFinding.sectionInAssessmentList = finding.sectionInAssessmentList
//                    newFinding.testArea = finding.testArea

                    findingListRlmObj!!.findingList.add(newFinding)
                    realm.insertOrUpdate(findingListRlmObj)

                    emitter.onComplete()
                }
            } catch (e: Exception) {
                emitter.onError(e)
            }

        }
    }

    fun createNewStudyPlace(id:ObjectId,placeGeneralDetails: GeneralReportDetailsDataHolder): Single<String> {
        return Single.create { emitter ->
            try {
                realm.executeTransactionAsync { realm ->
                    val obj = realm.createObject(
                        StudyPlaceRlmObj::class.java,
                        id
                    )
                    val generalReportDetailsRlmObj = realm.createObject(
                        GeneralReportDetailsRlmObj::class.java
                    )

                    generalReportDetailsRlmObj.city = placeGeneralDetails.city
                    generalReportDetailsRlmObj.date = placeGeneralDetails.date
                    generalReportDetailsRlmObj.institutionSymbol =
                        placeGeneralDetails.institutionSymbol
                    generalReportDetailsRlmObj.placeName = placeGeneralDetails.placeName
                    generalReportDetailsRlmObj.testerDetails = placeGeneralDetails.testerDetails
                    obj.generalReportDetailsRlmObj = generalReportDetailsRlmObj
                    realm.insertOrUpdate(obj)
                    emitter.onSuccess("success")
                }

            } catch (e: Exception) {
                emitter.onError(e)
            }
        }
    }

    fun createNewFindingList(placeId: ObjectId, id: ObjectId, date: String): Completable {
        return Completable.create { emitter ->
            try {
                realm.executeTransactionAsync { realm ->
                    val place =
                        realm.where(StudyPlaceRlmObj::class.java).equalTo("_id", placeId)
                            .findFirst()
                    val newReport = realm.createObject(FindingListRlmObj::class.java, id)
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

    fun updateFinding(finding: FindingDataHolder):Completable {
        return Completable.create{ emitter->
            try {
                realm.executeTransactionAsync{ realm->
                   val rlmObj= realm.where(FindingRlmObj::class.java)
                       .equalTo("_id",finding.id)
                       .findFirst()
                    rlmObj!!.picPath = finding.pic
                    rlmObj.problem = finding.problem
                    rlmObj.requirement = finding.requirement
                    rlmObj.section = finding.section
                    rlmObj.sectionInAssessmentList = finding.sectionInAssessmentList

                    emitter.onComplete()
                }
            }
            catch (e:Exception){
                emitter.onError(e)
            }
        }

    }

    fun getAllFindings(): Single<HashMap<ObjectId,StudyPlaceDataHolder>> {

        return realm.getLists(StudyPlaceRlmObj::class.java) { realmResults ->
            val hashMap =HashMap<ObjectId,StudyPlaceDataHolder>()
            realmResults.forEach { studyPlaceRlmObj ->
                val studyPlaceDataHolder = StudyPlaceDataHolder()
                val generalReportDetailsRlmObj = studyPlaceRlmObj.generalReportDetailsRlmObj
                studyPlaceDataHolder.id = studyPlaceRlmObj._id

                studyPlaceDataHolder.generalReportDetails = GeneralReportDetailsDataHolder(
                    city = generalReportDetailsRlmObj!!.city,
                    placeName = generalReportDetailsRlmObj.placeName,
                    institutionSymbol = generalReportDetailsRlmObj.institutionSymbol,
                    date = generalReportDetailsRlmObj.date,
                    testerDetails = generalReportDetailsRlmObj.testerDetails,
                )

                val reportArrayList = HashMap<ObjectId, FindingListDataHolder>()

                studyPlaceRlmObj.reportList.forEach { findingListRlmObj ->

                    val findingListDataHolder = FindingListDataHolder()
                    findingListDataHolder.id = findingListRlmObj._id
                    findingListDataHolder.date = findingListRlmObj.date

                    findingListRlmObj.findingList.forEach { findingRealmObj ->
                        val findingsDataObj = FindingDataHolder(
                            id = findingRealmObj._id,
                            section = findingRealmObj.section,
//                            testArea = findingRealmObj.testArea,
                            sectionInAssessmentList = findingRealmObj.sectionInAssessmentList,
                            requirement = findingRealmObj.requirement,
                            problem = findingRealmObj.problem,
                            pic = findingRealmObj.picPath
                        )
                            //sort according to priorities
                        findingListDataHolder.
                        findingArr[findingsDataObj.priority.toInt()][findingsDataObj.id]=findingsDataObj
                    }
                    reportArrayList[findingListDataHolder.id]=findingListDataHolder
                    studyPlaceDataHolder.reportList = reportArrayList
                }
                hashMap[studyPlaceDataHolder.id]=studyPlaceDataHolder
            }
            hashMap
        }
    }

    fun deleteFinding() {

    }
}