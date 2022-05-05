package com.e.security.data.objects

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import org.bson.types.ObjectId

open class FindingListRlmObj :RealmObject() {

  @PrimaryKey
  var _id:ObjectId=ObjectId()
  var findingList: RealmList<FindingRlmObj> = RealmList()
  var date:String=""

}