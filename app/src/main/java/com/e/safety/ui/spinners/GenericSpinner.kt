package com.e.safety.ui.spinners

import android.content.Context
import android.widget.ArrayAdapter

class GenericSpinner {

    fun create(context: Context, array: Int): ArrayAdapter<CharSequence> {
        return ArrayAdapter.createFromResource(
            context,
            array,
            android.R.layout.simple_spinner_item
        ).let { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            adapter
        }
    }


}