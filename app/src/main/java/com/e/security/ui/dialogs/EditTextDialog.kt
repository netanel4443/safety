package com.e.security.ui.dialogs

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.e.security.databinding.EdittextLayoutBinding
import com.e.security.utils.printErrorIfDbg

class EditTextDialog() {
    val onClick:((String)->Unit)?=null
    fun create(context: Context){
        val alertDialog=AlertDialog.Builder(context)

        val inflater=LayoutInflater.from(context)
        val binding=EdittextLayoutBinding.inflate(inflater)

        binding.confirmButton.setOnClickListener {
            val text=binding.editText.text.toString()
            if (text.isNotBlank()){
                onClick?.invoke(text)
            }
            else{
                Toast.makeText(context,"לא הוכנס טקסט",Toast.LENGTH_SHORT).show()
            }
        }
        alertDialog.setView(binding.root)
        alertDialog.create()
    }
}