package com.e.security.ui.dialogs

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.e.security.MainActivity
import com.e.security.R
import com.e.security.data.StudyPlaceDetailsDataHolder
import com.e.security.databinding.StudyPlaceInfoBinding
import com.e.security.ui.MainViewModel
import com.e.security.ui.fragments.BaseSharedVmFragment
import com.e.security.ui.utils.rxjava.throttleClick

class StudyPlaceInfoFscreen() : BaseSharedVmFragment() {

    private val viewModel: MainViewModel by lazy(this::getViewModel)

    private lateinit var binding: StudyPlaceInfoBinding
//    private var recyclerViewDialog: RecyclerViewDialog<TextViewVhCell>? = null


    companion object {
        const val TAG = "StudyPlaceInfoFscreen"
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity() as MainActivity).mainActivityComponent.inject(this)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = StudyPlaceInfoBinding.inflate(inflater, null, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.educationalInstitution.setOnClickListener {
            viewModel.showStringRecyclerViewDialog(resources.getStringArray(R.array.hozer_types),
            viewModel::changeEducationalInstitution)
        }

        binding.confirmButton.throttleClick() {
            val text = binding.placeNameEdittext.text.toString()
            if (text.isNotBlank() && binding.educationalInstitution.text.isNotEmpty()) {
                viewModel.createNewStudyPlace(
                    StudyPlaceDetailsDataHolder(
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
                        testerDetails = binding.testerDetailsEditText.text.toString(),
                        educationalInstitution = binding.educationalInstitution.text.toString()
                    )
                )
                requireActivity().supportFragmentManager.popBackStack()
                viewModel.popFragment()
            } else {
                Toast.makeText(context, "לא הוכנס טקסט", Toast.LENGTH_SHORT).show()
            }
        }.addDisposable()


        initStateObserver()
        initEffectObserver()

    }

    private fun initEffectObserver() {
//        viewModel.viewEffect.observe(viewLifecycleOwner) { effect ->
//            when (effect) {
//                is Effects.ShowStringRecyclerViewDialog -> showEducationalInstitutionsDialog(
//                    effect.items,
//                    effect.func
//                )
//
//            }
//        }
    }


    private fun initStateObserver() {
        viewModel.viewState.observe(viewLifecycleOwner, { state ->

            val currentState = state.currentState.studyPlaceInfoFragmentState
            val prevState = state.prevState.studyPlaceInfoFragmentState
            if (currentState != prevState) {
                if (currentState.reportDetails.educationalInstitution !=
                    prevState.reportDetails.educationalInstitution
                ) {
                    binding.educationalInstitution.text =
                        currentState.reportDetails.educationalInstitution
                }

            }

        }) { initialState ->
            val data = initialState.currentState.studyPlaceInfoFragmentState.reportDetails
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


//        recyclerViewDialog!!.onClick = {
//            viewModel.changeEducationalInstitution(it)
//        }
//
    }
