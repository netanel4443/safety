package com.e.safety.ui.recyclerviews.generics

import com.e.fakerestapi.ui.recyclerviews.helpers.GenericVhItem
import com.e.safety.ui.recyclerviews.helpers.CreateVh
import com.e.safety.ui.recyclerviews.helpers.GenericItemClickListener

data class VhItemSetters<T : GenericVhItem> (
    //if itemClick is not necessary , it will be null to prevent exceptions
    var clickListener: GenericItemClickListener<T>? = null,
    var createVh: Class<out CreateVh<T>>? = null,
    var layoutId:Int
)