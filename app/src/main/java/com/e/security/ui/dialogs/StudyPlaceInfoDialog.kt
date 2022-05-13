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
import com.e.security.data.ReportDetailsDataHolder
import com.e.security.databinding.StudyPlaceInfoBinding
import com.e.security.ui.MainViewModel
import com.e.security.ui.fragments.VmDialogFragment
import com.e.security.ui.recyclerviews.celldata.TextViewVhCell
import com.e.security.ui.recyclerviews.viewholders.CreateTextViewVh
import com.e.security.ui.utils.rxjava.throttleClick
import com.e.security.ui.viewmodels.effects.Effects

class StudyPlaceInfoDialog() : VmDialogFragment() {

    private val viewModel: MainViewModel by lazy(this::getViewModel)

    private lateinit var binding: StudyPlaceInfoBinding
    private var recyclerViewDialog: RecyclerViewDialog<TextViewVhCell>? = null


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

        binding.educationalInstitution.setOnClickListener {
            viewModel.showEducationalInstitutionsDialog(resources.getStringArray(R.array.hozer_types))
        }

        binding.confirmButton.throttleClick(3000) {
            val text = binding.placeNameEdittext.text.toString()
            if (text.isNotBlank() && binding.educationalInstitution.text.isEmpty()) {
                viewModel.createNewStudyPlace(
                    ReportDetailsDataHolder(
                        placeName = text,
                        city = binding.cityEdittext.text.toString(),
                        address = binding.address.text.toString(),
                        ownership = binding.institutionSymbolEdittext.text.toString(),
                        institutionSymbol = binding.institutionSymbolEdittext.text.toString(),
                        studentsNumber = binding.studentsNumber.text.toString(),
                        yearOfFounding = binding.foundingYear.text.toString(),
                        studyPlacePhone = binding.studyPlacePhone.text.toString(),
                        managerDetails = binding.managerDetails.text.toString(),
                        inspectorDetails = binding.inspectorDetails.text.toString(),
                        studyPlaceParticipants = binding.studyPlaceParticipants.text.toString(),
                        authorityParticipants = binding.authorityParticipants.text.toString(),
                        testerDetails = binding.testerDetailsEditText.text.toString()
                    )
                )
                dismiss()
            } else {
                Toast.makeText(context, "לא הוכנס טקסט", Toast.LENGTH_SHORT).show()
            }
        }.addDisposable()


        initStateObserver()
        initEffectObserver()

    }

    private fun initEffectObserver() {
        viewModel.viewEffect.observe(viewLifecycleOwner) { effect ->
            when (effect) {
                is Effects.ShowEducationalInstitutionDialog -> showEducationalInstitutionsDialog(
                    effect.items
                )

            }
        }
    }


    private fun initStateObserver() {
        viewModel.viewState.observe(viewLifecycleOwner, { state ->

            val currentState = state.currentState.studyPlaceFragmentState
            val prevState = state.prevState.studyPlaceFragmentState
            if (currentState != prevState) {
                if (currentState.reportDetails.educationalInstitution !=
                    prevState.reportDetails.educationalInstitution
                ) {
                    binding.educationalInstitution.text =
                        currentState.reportDetails.educationalInstitution
                }

            }

        }) { initialState ->
            val data = initialState.currentState.studyPlaceFragmentState.reportDetails
            binding.placeNameEdittext.setText(data.placeName)
            binding.cityEdittext.setText(data.city)
            binding.address.setText(data.address)
            binding.institutionSymbolEdittext.setText(data.ownership)
            binding.institutionSymbolEdittext.setText(data.institutionSymbol)
            binding.studentsNumber.setText(data.studentsNumber)
            binding.foundingYear.setText(data.yearOfFounding)
            binding.studyPlacePhone.setText(data.studyPlacePhone)
            binding.managerDetails.setText(data.managerDetails)
            binding.inspectorDetails.setText(data.inspectorDetails)
            binding.studyPlaceParticipants.setText(data.studyPlaceParticipants)
            binding.authorityParticipants.setText(data.authorityParticipants)
            binding.testerDetailsEditText.setText(data.testerDetails)
            binding.educationalInstitution.text = data.educationalInstitution
        }
    }

    private fun showEducationalInstitutionsDialog(items: List<TextViewVhCell>) {
        recyclerViewDialog?.run { showDialog() } ?: createEducationalInstitutionsDialog(items)
    }

    private fun createEducationalInstitutionsDialog(items: List<TextViewVhCell>) {

        recyclerViewDialog = RecyclerViewDialog(
            requireActivity(),
            CreateTextViewVh::class.java,
            R.layout.textview_vh_cell_design
        )
        recyclerViewDialog!!.showDialog()
        recyclerViewDialog!!.onClick = {
            viewModel.changeEducationalInstitution(it.item)
        }

        recyclerViewDialog!!.addItems(items)
    }
}