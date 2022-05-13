package com.e.security.ui.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import com.e.security.MainActivity
import com.e.security.R
import com.e.security.data.FindingDataHolder
import com.e.security.databinding.CreateFindingBinding
import com.e.security.ui.MainViewModel
import com.e.security.ui.spinners.GenericSpinner
import com.e.security.ui.utils.addFragment
import com.e.security.utils.printIfDbg
import com.squareup.picasso.Picasso

class CreateFindingFragment : BaseSharedVmFragment() {

    private val viewModel: MainViewModel by lazy(this::getViewModel)
    private lateinit var binding: CreateFindingBinding
    private var imagePath: String = ""
    private var findingToEdit = FindingDataHolder()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity() as MainActivity).mainActivityComponent.inject(this)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = CreateFindingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        initUi()
        initStateObserver()
    }

    private fun initStateObserver() {

        viewModel.viewState.observe(viewLifecycleOwner, { state ->
            val prev = state.prevState
            val curr = state.currentState

            if (prev.createFindingFragmentState.problemImage !=
                curr.createFindingFragmentState.problemImage) {
                imagePath = curr.createFindingFragmentState.problemImage.toString()
                Picasso.get().load(curr.createFindingFragmentState.problemImage)
                    .into(binding.problemImage)
            }
        }) {
            val currentState = it.currentState.createFindingFragmentState
            fillViewsWithData(currentState.finding)
        }
    }


    private fun fillViewsWithData(finding: FindingDataHolder) {
        findingToEdit = finding
        imagePath = finding.pic
        printIfDbg("","image pic ${finding.pic}")
        Picasso.get().load(Uri.parse(finding.pic)).into(binding.problemImage)

        val array=resources.getStringArray(R.array.priority_array)
        binding.priority.setSelection(array.indexOf(finding.priority))
        binding.sectionInAssessmentList.text = finding.sectionInAssessmentList
        binding.sectionInAssessmentList.text = finding.requirement
        binding.problemDescription.setText(finding.problem)
    }

    private fun initUi() {
        binding.problemImageCard.setOnClickListener {
            pickAnImage()
        }

        val spinnerAdapter = GenericSpinner()
        binding.priority.adapter = spinnerAdapter.create(requireActivity(), R.array.priority_array)


        //todo prevent multiple clicks and fix spinner text getter etc..
        binding.confirmButton.setOnClickListener {
            findingToEdit.priority = binding.priority.selectedItem.toString()
            findingToEdit.sectionInAssessmentList = binding.sectionInAssessmentList.text.toString()
            //   findingToEdit.section = binding.section.toString() //should be incremented somehow
            findingToEdit.problem = binding.problemDescription.text.toString()
            findingToEdit.requirement = binding.requirement.text.toString()
            findingToEdit.pic = imagePath

            viewModel.saveFinding(findingToEdit)
        }
    }

    fun pickAnImage() {
        requireActivity().addFragment(CameraFragment(),R.id.fragment_container,"CameraFragment")
    }

}