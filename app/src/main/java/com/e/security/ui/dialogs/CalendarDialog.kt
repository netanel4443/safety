package com.e.security.ui.dialogs

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import com.e.security.databinding.CalendarDialogBinding

class CalendarDialog(
    private var context: Context
) {
    private var alert: AlertDialog? = null
    var onClick: ((String) -> Unit)? = null

    fun showDialog() {
        if (alert == null) {
            create()
        }
        alert?.show()
    }

    private fun create() {
        val alertDialog = AlertDialog.Builder(context)
        var d = 0
        var m = 0
        var y = 0
        val inflater = LayoutInflater.from(context)
        val binding = CalendarDialogBinding.inflate(inflater)
        alertDialog.setView(binding.root)
        alert = alertDialog.create()
        alert!!.setCanceledOnTouchOutside(true)

        binding.calendar.setOnDateChangeListener { calendar, year, month, day ->
            d = day
            m = month
            y = year
        }

        binding.confirmButton.setOnClickListener {
            alert!!.dismiss()
            onClick?.invoke(
                StringBuilder()
                    .append(d).append("/")
                    .append(m).append("/")
                    .append(y)
                    .toString()
            )
        }
        binding.cancelButton.setOnClickListener {
            alert!!.dismiss()
        }
    }

}

