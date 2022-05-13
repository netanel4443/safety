package com.e.security.ui.recyclerviews.clicklisteners

import com.e.security.ui.recyclerviews.helpers.GenericItemClickListener
import org.apache.poi.ss.formula.functions.T

interface ReportVhItemClick<T>:GenericItemClickListener<T> {

    fun onEditBtnClick()
}