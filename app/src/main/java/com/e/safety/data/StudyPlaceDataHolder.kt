package com.e.safety.data

import org.bson.types.ObjectId

class StudyPlaceDataHolder {

    var id: ObjectId = ObjectId()
    var reportDetails: StudyPlaceDetailsDataHolder = StudyPlaceDetailsDataHolder()
    var reportList: HashMap<ObjectId, ReportDataHolder> = HashMap()

}