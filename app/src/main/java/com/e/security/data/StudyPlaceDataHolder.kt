package com.e.security.data

import org.bson.types.ObjectId

class StudyPlaceDataHolder {

    var id: ObjectId = ObjectId()
    var reportDetails: ReportDetailsDataHolder = ReportDetailsDataHolder()
    var reportList: HashMap<ObjectId, ReportDataHolder> = HashMap()

}