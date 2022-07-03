package com.e.security.ui.dialogs

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import com.e.security.databinding.EditTextDialogBinding
import com.e.security.ui.dialogs.helpers.IeditTextDialogHelper

open class EditTextDialog(private var context: Context) {

    private var alert: AlertDialog? = null
    private var binding: EditTextDialogBinding? = null
    private var dialogHelper: IeditTextDialogHelper? = null

    init {
        create()
    }

    fun create() {
        val alertDialog = AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context)

        binding = EditTextDialogBinding.inflate(inflater)

        alertDialog.setView(binding!!.root)
        alert = alertDialog.create()
        alert!!.setCanceledOnTouchOutside(true)
        alert!!.setOnDismissListener {
            dismissDialog()
        }

        binding!!.cancelButton.setOnClickListener {
            dismissDialog()
        }

        binding!!.confirmButton.setOnClickListener {
            dialogHelper!!.onConfirm(binding!!.editText.text.toString())
        }
    }

    fun show() {
        alert!!.show()
        dialogHelper?.onShowDialog()
    }

    fun dismissDialog() {
        alert!!.dismiss()
        dialogHelper?.onDismissDialog()
    }

    fun setHelper(helper: IeditTextDialogHelper) {
        dialogHelper = helper
    }

    // for configuration changes
    fun setText(text: String) {
        binding!!.editText.setText(text)
    }

}

