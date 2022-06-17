package com.e.security.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.e.security.MainActivity
import com.e.security.R
import com.e.security.databinding.RecyclerviewAddBtnScreenBinding
import com.e.security.di.ApplicationComponent
import com.e.security.ui.MainViewModel
import com.e.security.ui.recyclerviews.GenericRecyclerviewAdapter
import com.e.security.ui.recyclerviews.celldata.FindingVhCell
import com.e.security.ui.recyclerviews.clicklisteners.FindingVhItemClick
import com.e.security.ui.recyclerviews.viewholders.CreateFindingDetailsViewHolder
import com.e.security.ui.utils.addFragment
import com.e.security.ui.viewmodels.effects.Effects
import com.e.security.utils.differentItems

class FindingsDetailsFragment : BaseSharedVmFragment() {

    private val viewModel: MainViewModel by lazy(this::getViewModel)
    private lateinit var binding: RecyclerviewAddBtnScreenBinding
    private lateinit var recyclerviewAdapter: GenericRecyclerviewAdapter<
            FindingVhCell, CreateFindingDetailsViewHolder
            >

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity() as MainActivity).mainActivityComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = RecyclerviewAddBtnScreenBinding.inflate(inflater, container, false)
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
            val prev = state.prevState
            val curr = state.currentState
                recyclerviewAdapter.submitList(curr.findingVhCellArrayList)
//            when {
//
//                recyclerviewAdapter.hasNoItems() -> {
//                    recyclerviewAdapter.addItems(curr.findingVhCellArrayList)
//                }
//                prev.findingVhCellArrayList.size < curr.findingVhCellArrayList.size -> {
//                    val newItems =
//                        curr.findingVhCellArrayList.differentItems(prev.findingVhCellArrayList)
//                    recyclerviewAdapter.addItems(newItems)
//                }
//                prev.findingVhCellArrayList.size > curr.findingVhCellArrayList.size -> {
//                    val itemsToRemove =
//                        curr.findingVhCellArrayList.differentItems(prev.findingVhCellArrayList)
//                    recyclerviewAdapter.removeItems(itemsToRemove)
//                }
//                // if we arrived to this point , one item has been updated
//                // so we need to reflect it on the ui
//                prev.findingVhCellArrayList != curr.findingVhCellArrayList -> {
//                    val itemsToRemove =
//                        curr.findingVhCellArrayList.differentItems(prev.findingVhCellArrayList)
//                    val itemsToAdd =
//                        prev.findingVhCellArrayList.differentItems(curr.findingVhCellArrayList)
//                    recyclerviewAdapter.removeItems(itemsToRemove)
//                    recyclerviewAdapter.addItems(itemsToAdd)
//                }
//            }
        }
    }

    private fun initEffectObserver() {
        viewModel.viewEffect.observe(viewLifecycleOwner) { effect ->
            when (effect) {
                is Effects.StartCreateFindingFragment -> startCreateFindingFragment()
            }
        }
    }

    private fun startCreateFindingFragment() {
        val fragment = CreateFindingFragment()
        requireActivity().addFragment(fragment, R.id.fragment_container, "CreateFindingFragment")
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
                CreateFindingDetailsViewHolder::class.java
            )
        recyclerviewAdapter.setItemClickListener(object : FindingVhItemClick {
            override fun onItemClick(item: FindingVhCell) {
                viewModel.editAFinding(item.id)
            }

            override fun onLongClick(item: FindingVhCell): Boolean {
                viewModel.showDeleteFindingDialog(item.id, viewModel::deleteFinding)
                return true
            }
        })


        recyclerView.adapter = recyclerviewAdapter

        recyclerView.layoutManager = LinearLayoutManager(
            requireActivity(),
            LinearLayoutManager.VERTICAL,
            false
        )
        recyclerView.setHasFixedSize(true)
    }

}