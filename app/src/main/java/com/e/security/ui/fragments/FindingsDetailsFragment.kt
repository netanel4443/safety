package com.e.security.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.e.security.ui.recyclerviews.generics.GenericRecyclerviewAdapter2
import com.e.security.MainActivity
import com.e.security.R
import com.e.security.databinding.RecyclerviewAddBtnScreenBinding
import com.e.security.ui.MainViewModel
import com.e.security.ui.recyclerviews.celldata.FindingVhCell
import com.e.security.ui.recyclerviews.generics.FindingVhItemClick
import com.e.security.ui.recyclerviews.generics.VhItemSetters
import com.e.security.ui.recyclerviews.viewholders.CreateFindingDetailsViewHolder
import com.e.security.ui.utils.addFragment
import com.e.security.ui.viewmodels.effects.Effects
import org.bson.types.ObjectId

class FindingsDetailsFragment : BaseSharedVmFragment() {

    private val viewModel: MainViewModel by lazy(this::getViewModel)
    private lateinit var binding: RecyclerviewAddBtnScreenBinding
    private lateinit var recyclerviewAdapter: GenericRecyclerviewAdapter2<FindingVhCell>

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
        recyclerviewAdapter = GenericRecyclerviewAdapter2()
        //                CreateFindingDetailsViewHolder::class.java
        val setter = VhItemSetters<FindingVhCell>()
        setter.createVh = CreateFindingDetailsViewHolder::class.java

        setter.clickListener = object : FindingVhItemClick {
            override fun onItemClick(item: FindingVhCell) {
                viewModel.editAFinding(item.id as ObjectId)
            }

            override fun onLongClick(item: FindingVhCell): Boolean {
                viewModel.showDeleteFindingDialog(item.id as ObjectId, viewModel::deleteFinding)
                return true
            }
        }

        recyclerviewAdapter.setVhItemSetter(setter)


        recyclerView.adapter = recyclerviewAdapter

        recyclerView.layoutManager = LinearLayoutManager(
            requireActivity(),
            LinearLayoutManager.VERTICAL,
            false
        )
        recyclerView.setHasFixedSize(true)

    }

}