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
import com.e.security.ui.recyclerviews.celldata.FindingVhCellData
import com.e.security.ui.recyclerviews.helpers.GenericItemClickListener
import com.e.security.ui.recyclerviews.viewholders.CreateFindingDetailsViewHolder
import com.e.security.ui.utils.addFragment
import com.e.security.ui.viewmodels.effects.Effects
import com.e.security.utils.differentItems

class FindingsDetailsFragment : BaseSharedVmFragment() {

    lateinit var applicationComponent: ApplicationComponent
    private val viewModel: MainViewModel by lazy(this::getViewModel)
    private lateinit var binding: FragmentRecyclerviewAddBtnBinding
    private lateinit var recyclerviewAdapter: GenericRecyclerviewAdapter<
            FindingVhCellData, CreateFindingDetailsViewHolder
            >
    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity() as MainActivity).mainActivityComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
         binding=FragmentRecyclerviewAddBtnBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
       initUi()
        initEffectObserver()
        initStateObserver()
        viewModel.getReportListFindings()
    }

    private fun initStateObserver() {
        viewModel.viewState.observe(viewLifecycleOwner) { state ->
            val prev=state.prevState
            val curr=state.currentState

            when {

                recyclerviewAdapter.hasNoItems() -> {
                    recyclerviewAdapter.addItems(curr.findingArrayList)
                }
                prev.findingArrayList.size < curr.findingArrayList.size -> {
                    val newItems = curr.findingArrayList.differentItems(prev.findingArrayList)
                    recyclerviewAdapter.addItems(newItems)
                }
                prev.findingArrayList.size > curr.findingArrayList.size -> {
                    val itemsToRemove =
                        curr.findingArrayList.differentItems(prev.findingArrayList)
                    recyclerviewAdapter.removeItems(itemsToRemove)
                }
            }
        }
    }

    private fun initEffectObserver() {
        viewModel.viewEffect.observe(viewLifecycleOwner) { effect ->
            when(effect){
                is Effects.StartCreateFindingFragment-> startCreateFindingFragment()
            }
        }
    }

    private fun startCreateFindingFragment() {
        val fragment=CreateFindingFragment()
        requireActivity().addFragment(fragment,R.id.fragment_container,"CreateFindingFragment")
    }


    private fun initUi() {
        initRecyclerView()
        binding.addBtn.setOnClickListener {
            viewModel.startCreateFindingFragment()
        }
    }

    private fun initRecyclerView() {
        val recyclerView = binding.recyclerview
        recyclerviewAdapter =
            GenericRecyclerviewAdapter(
                R.layout.finding_recyclerview_cell_design,
                CreateFindingDetailsViewHolder::class.java
            )
        recyclerviewAdapter.setItemClickListener(object:GenericItemClickListener<FindingVhCellData>{
            override fun onItemClick(item: FindingVhCellData) {
                viewModel.editAFinding(item.id)
            }
        })


        recyclerView.adapter = recyclerviewAdapter

        //todo is require context the right one here?
        recyclerView.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )
        recyclerView.setHasFixedSize(true)
    }

}