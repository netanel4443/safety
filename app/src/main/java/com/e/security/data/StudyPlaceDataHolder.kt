package com.e.security.data

import org.bson.types.ObjectId

class StudyPlaceDataHolder {

    var id: ObjectId = ObjectId()
    var generalReportDetails: GeneralReportDetailsDataHolder = GeneralReportDetailsDataHolder()
    var reportList: HashMap<ObjectId, FindingListDataHolder> = HashMap()

}