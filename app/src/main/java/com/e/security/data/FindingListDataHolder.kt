package com.e.security.data

import org.bson.types.ObjectId

class FindingListDataHolder {

  var id:ObjectId = ObjectId()
  var date:String=""
  var findingArr=Array<HashMap<ObjectId,FindingDataHolder>>(3,{ HashMap()})

}