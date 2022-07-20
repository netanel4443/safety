package com.e.safety.ui.recyclerviews.generics

import com.e.fakerestapi.ui.recyclerviews.helpers.GenericVhItem
import com.e.safety.ui.recyclerviews.helpers.CreateVh
import com.e.safety.ui.recyclerviews.helpers.GenericItemClickListener

class VhItemSetters<T : GenericVhItem> {
    var clickListener: GenericItemClickListener<T>? = null
    var createVh: Class<out CreateVh<T>>? = null

}