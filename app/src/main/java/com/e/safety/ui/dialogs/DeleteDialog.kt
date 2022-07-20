package com.e.safety.ui.dialogs

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import com.e.safety.databinding.TextviewDeleteDialogBinding

class DeleteDialog(
    private var context: Context
):BaseAlertDialog(context) {
    private var alert: AlertDialog? = null
    private var binding: TextviewDeleteDialogBinding? = null
    var onClick: (() -> Unit)? = null

    fun showDialog(message: String) {
      if (alert==null) { create() }
          addMessage(message)
          alert!!.show()
    }

    private fun addMessage(message: String) {
        binding!!.message.text = message
    }

    private fun create() {
        val alertDialog = AlertDialog.Builder(context)

        val inflater = LayoutInflater.from(context)
        binding = TextviewDeleteDialogBinding.inflate(inflater)
        alertDialog.setView(binding!!.root)
        alert = alertDialog.create()
        alert!!.setCanceledOnTouchOutside(true)

        binding!!.confirmButton.setOnClickListener {
            alert!!.dismiss()
            onClick?.invoke()
        }

        binding!!.cancelButton.setOnClickListener {
            alert!!.dismiss()
        }
    }

}

