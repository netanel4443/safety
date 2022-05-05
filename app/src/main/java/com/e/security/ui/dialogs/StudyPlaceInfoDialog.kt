package com.e.security.ui.dialogs

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import com.e.security.MainActivity
import com.e.security.R
import com.e.security.data.GeneralReportDetailsDataHolder
import com.e.security.data.PlaceGeneralDetails
import com.e.security.databinding.StudyPlaceInfoBinding
import com.e.security.ui.MainViewModel
import com.e.security.ui.fragments.VmDialogFragment
import java.util.*

class StudyPlaceInfoDialog() : VmDialogFragment() {

    private val viewModel: MainViewModel by lazy(this::getViewModel)

    private lateinit var binding: StudyPlaceInfoBinding

    companion object {
        const val TAG = "StudyPlaceInfoDialog"
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity() as MainActivity).mainActivityComponent.inject(this)
    }

    //todo check about this window
    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = StudyPlaceInfoBinding.inflate(inflater, null, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = StudyPlaceInfoBinding.bind(view)

        binding.date.text= getDate()

        binding.confirmButton.setOnClickListener {
            val text = binding.placeNameEdittext.text.toString()
            if (text.isNotBlank()) {
                viewModel.createNewStudyPlace(GeneralReportDetailsDataHolder(placeName = text))
                dismiss()
            } else {
                Toast.makeText(context, "לא הוכנס טקסט", Toast.LENGTH_SHORT).show()
            }
        }

    }
    //todo move it from here
    private fun getDate():String {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        return StringBuilder().append(resources.getString(R.string.date))
            .append(day).append("/")
            .append(month).append("/")
            .append(year)
            .toString()
    }


}