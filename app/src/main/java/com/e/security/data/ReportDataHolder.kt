package com.e.security.data

import org.bson.types.ObjectId

class ReportDataHolder {

  var id:ObjectId = ObjectId()
  var date:String=""
  var findingArr=Array<HashMap<ObjectId,FindingDataHolder>>(3) { HashMap() }

}