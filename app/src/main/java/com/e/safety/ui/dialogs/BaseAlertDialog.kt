package com.e.safety.ui.dialogs

import android.app.AlertDialog
import android.content.Context
import androidx.viewbinding.ViewBinding
import com.e.safety.ui.dialogs.helpers.GenericDialogHelper

open class BaseAlertDialog(private var context: Context) {

    private var alert: AlertDialog? = null
    private var dialogHelper: GenericDialogHelper? = null

    fun create(binding: ViewBinding) {
        val alertDialogBuilder = AlertDialog.Builder(context)

        alertDialogBuilder.setView(binding.root)
        alert = alertDialogBuilder.create()
        alert!!.setCanceledOnTouchOutside(true)
            //for back press
        alert!!.setOnCancelListener {
            dismiss()
        }

    }

    open fun show() {
        alert!!.show()
        dialogHelper?.onShowDialog()
    }


    open fun dismiss() {
        alert!!.dismiss()
        dialogHelper?.onDismissDialog()
    }

    /**if doesn't called by base class , functions depend on  [dialogHelper]
    won't act as expected*/
    protected open fun setHelper(helper: GenericDialogHelper) {
        dialogHelper = helper
    }


    open fun dismissConfigurationChanges() {
        if (isShowing()){
            alert!!.dismiss()
        }
    }

    fun isShowing(): Boolean {
        return alert!!.isShowing
    }
}

