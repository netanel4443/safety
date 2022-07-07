package com.e.security.ui.dialogs

import android.content.Context
import android.view.LayoutInflater
import com.e.security.databinding.EditTextDialogBinding
import com.e.security.ui.dialogs.helpers.IeditTextDialogHelper

open class EditTextDialog(private var context: Context) : BaseAlertDialog(context) {

    private var binding: EditTextDialogBinding? = null
    private var iHelper: IeditTextDialogHelper? = null

    init {
        create()
    }

    fun create() {
        val inflater = LayoutInflater.from(context)
        binding = EditTextDialogBinding.inflate(inflater)
        super.create(binding!!)

        binding!!.cancelButton.setOnClickListener { dismiss() }

        binding!!.confirmButton.setOnClickListener {
            iHelper!!.onConfirm(binding!!.editText.text.toString())
            dismiss()
        }
    }

    fun setHelper(helper: IeditTextDialogHelper) {
        super.setHelper(helper) // has to call super
        iHelper = helper
    }

    fun setText(text: String) {
        binding!!.editText.setText(text)
    }

    fun getText(): String {
        return binding!!.editText.text.toString()
    }
}

