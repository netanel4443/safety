package com.e.security.ui.dialogs

import android.content.Context
import android.view.LayoutInflater
import com.e.security.databinding.CalendarDialogBinding

class CalendarDialog(
    private var context: Context
) : BaseAlertDialog(context) {
    var onClick: ((String) -> Unit)? = null

    init {
        create()
    }

    private fun create() {
        var d = 0
        var m = 0
        var y = 0
        val inflater = LayoutInflater.from(context)
        val binding = CalendarDialogBinding.inflate(inflater)
        super.create(binding)

        binding.calendar.setOnDateChangeListener { _, year, month, day ->
            d = day
            m = month
            y = year
        }

        binding.confirmButton.setOnClickListener {
            dismiss()
            onClick?.invoke(
                StringBuilder()
                    .append(d).append("/")
                    .append(m).append("/")
                    .append(y)
                    .toString()
            )
        }
        binding.cancelButton.setOnClickListener {
            dismiss()
        }
    }

}

