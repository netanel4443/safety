package com.e.security.ui.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.e.security.MainActivity
import com.e.security.R
import com.e.security.data.FindingDataHolder
import com.e.security.databinding.CreateFindingBinding
import com.e.security.ui.MainViewModel
import com.e.security.ui.dialogs.FilterResultsDialog
import com.e.security.ui.dialogs.helpers.IFilterResultsDialogHelper
import com.e.security.ui.recyclerviews.celldata.HozerMankalVhCell
import com.e.security.ui.recyclerviews.viewholders.CreateHozerMankalVh
import com.e.security.ui.spinners.GenericSpinner
import com.e.security.ui.utils.addFragment
import com.e.security.ui.utils.rxjava.throttleClick
import com.e.security.ui.viewmodels.effects.Effects
import com.squareup.picasso.Picasso
import io.reactivex.rxjava3.core.Observable

class CreateFindingFragment : BaseSharedVmFragment() {

    private val viewModel: MainViewModel by lazy(this::getViewModel)
    private lateinit var binding: CreateFindingBinding
    private var imagePath: String = ""
    private var findingToEdit = FindingDataHolder()
    private var filterResultsDialog: FilterResultsDialog<HozerMankalVhCell>? = null

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
        initEffectObserver()
    }

    private fun initEffectObserver() {
        viewModel.viewEffect.observe(viewLifecycleOwner) { effect ->
            when (effect) {
                is Effects.ShowHozerMankalDialog -> showFilterResultDialog()
            }

        }
    }

    private fun initStateObserver() {

        viewModel.viewState.observe(viewLifecycleOwner, { state ->
            val prev = state.prevState.createFindingFragmentState
            val curr = state.currentState.createFindingFragmentState

            if (prev.problemImage != curr.problemImage) {
                imagePath = curr.problemImage.toString()
                Picasso.get().load(curr.problemImage)
                    .into(binding.problemImage)
            }
            if (prev.finding.sectionInAssessmentList != curr.finding.sectionInAssessmentList) {
                binding.requirement.text = curr.finding.requirement
                binding.sectionInAssessmentList.text = curr.finding.sectionInAssessmentList
                findingToEdit.testArea = curr.finding.testArea
            }
        }) {
            val currentState = it.currentState.createFindingFragmentState
            fillViewsWithData(currentState.finding)
        }
    }


    private fun fillViewsWithData(finding: FindingDataHolder) {
        findingToEdit = finding
        imagePath = finding.pic

        Picasso.get().load(Uri.parse(finding.pic)).into(binding.problemImage)

        val array = resources.getStringArray(R.array.priority_array)
        binding.priority.setSelection(array.indexOf(finding.priority))
        binding.sectionInAssessmentList.text = finding.sectionInAssessmentList
        binding.requirement.text = finding.requirement
        binding.problemDescription.setText(finding.problem)
    }

    private fun initUi() {
        binding.problemImageCard.setOnClickListener {
            pickAnImage()
        }

        binding.requirement.setOnClickListener {
            viewModel.showHozerMankalDialog()
        }


        val spinnerAdapter = GenericSpinner()
        binding.priority.adapter = spinnerAdapter.create(requireActivity(), R.array.priority_array)


        //todo check spinner text getter etc..
        binding.confirmButton.throttleClick() {
            findingToEdit.priority = binding.priority.selectedItem.toString()
            findingToEdit.sectionInAssessmentList = binding.sectionInAssessmentList.text.toString()
            findingToEdit.problem = binding.problemDescription.text.toString()
            findingToEdit.requirement = binding.requirement.text.toString()
            findingToEdit.pic = imagePath

            viewModel.saveFinding(findingToEdit)
            viewModel.popFragment()
        }.addDisposable()
    }

    private fun showFilterResultDialog() {
        filterResultsDialog?.run { showDialog() } ?: createFilterResultDialog()
    }

    private fun createFilterResultDialog() {
        filterResultsDialog = FilterResultsDialog(
            requireActivity(),
            CreateHozerMankalVh::class.java
        )
        filterResultsDialog?.setIHelper(object : IFilterResultsDialogHelper<HozerMankalVhCell> {
            override fun onItemClick(item: HozerMankalVhCell) {
                viewModel.changeRequirement(item)
            }

            override fun textChanges(charSequence: CharSequence): Observable<List<HozerMankalVhCell>> {
                return viewModel.filterHozerMankal(charSequence.toString())
            }
        })

        filterResultsDialog?.showDialog()
    }

    private fun pickAnImage() {
        val fragment=CameraFragment()

        requireActivity().addFragment(fragment, R.id.fragment_container, "CameraFragment")
    }

}