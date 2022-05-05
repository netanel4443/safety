package com.e.security.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.e.security.MainActivity
import com.e.security.R
import com.e.security.databinding.FragmentRecyclerviewAddBtnBinding
import com.e.security.di.ApplicationComponent
import com.e.security.ui.MainViewModel
import com.e.security.ui.recyclerviews.GenericRecyclerviewAdapter
import com.e.security.ui.recyclerviews.celldata.ReportVhCell
import com.e.security.ui.recyclerviews.helpers.GenericItemClickListener
import com.e.security.ui.recyclerviews.viewholders.CreateStudyPlaceReportsVh
import com.e.security.ui.utils.addFragment
import com.e.security.ui.viewmodels.effects.Effects
import com.e.security.utils.differentItems

class StudyPlaceReportsFragment : BaseSharedVmFragment() {

    private lateinit var binding: FragmentRecyclerviewAddBtnBinding
    private val viewModel: MainViewModel by lazy(this::getViewModel)
    private lateinit var recyclerviewAdapter:
            GenericRecyclerviewAdapter<ReportVhCell,
                    CreateStudyPlaceReportsVh>

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity() as MainActivity).mainActivityComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRecyclerviewAddBtnBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initUi()
        initEffectObserver()
        initStateObserver()
        viewModel.getReportListOfChosenStudyPlace()
    }

    private fun initStateObserver() {
        viewModel.viewState.observe(viewLifecycleOwner) { state->
            val prev=state.prevState
            val curr=state.currentState

            when {

                recyclerviewAdapter.hasNoItems() -> {
                    recyclerviewAdapter.addItems(curr.reportList)
                }
                prev.reportList.size < curr.reportList.size -> {
                    val newItems = curr.reportList.differentItems(prev.reportList)
                    recyclerviewAdapter.addItems(newItems)
                }
                prev.reportList.size > curr.reportList.size -> {
                    val itemsToRemove =
                        curr.reportList.differentItems(prev.reportList)
                    recyclerviewAdapter.removeItems(itemsToRemove)
                }
            }
        }
    }

    private fun initEffectObserver() {
        viewModel.viewEffect.observe(viewLifecycleOwner) { effect ->
            when (effect) {
                is Effects.StartFindingsDetailsFragment -> startFindingDetailsFragment()
            }
        }
    }

    private fun startFindingDetailsFragment() {
        val fragment = FindingsDetailsFragment()
         requireActivity().addFragment(fragment, binding.fragmentContainer.id, "FindingsDetailsFragment")
    }


    private fun initUi() {
        initRecyclerView()

        binding.addBtn.setOnClickListener {
            viewModel.createNewFindingList()
        }
    }

    private fun initRecyclerView() {
        val recyclerview = binding.recyclerview
        recyclerviewAdapter = GenericRecyclerviewAdapter(
            R.layout.report_vh_cell_design,
            CreateStudyPlaceReportsVh::class.java
        )
        recyclerviewAdapter.setItemClickListener(object : GenericItemClickListener<ReportVhCell> {
            override fun onItemClick(item: ReportVhCell) {
                viewModel.startFindingDetailsFragment(item.id)
            }
        })
        recyclerview.adapter = recyclerviewAdapter
        //todo is requirecontext here is the correct one?
        recyclerview.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )
        recyclerview.setHasFixedSize(true)
    }


}