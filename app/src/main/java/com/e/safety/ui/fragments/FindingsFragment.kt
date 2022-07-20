package com.e.safety.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.e.safety.R
import com.e.safety.databinding.FindingFragmentBinding
import com.e.safety.ui.activities.mainactivity.MainActivity
import com.e.safety.ui.dialogfragments.DeleteFindingFragmentDialog
import com.e.safety.ui.dialogfragments.ConclusionFragmentDialog
import com.e.safety.ui.recyclerviews.celldata.FindingVhCell
import com.e.safety.ui.recyclerviews.generics.FindingVhItemClick
import com.e.safety.ui.recyclerviews.generics.GenericRecyclerviewAdapter2
import com.e.safety.ui.recyclerviews.generics.VhItemSetters
import com.e.safety.ui.recyclerviews.viewholders.CreateFindingDetailsViewHolder
import com.e.safety.ui.utils.addFragment
import com.e.safety.ui.viewmodels.MainViewModel
import com.e.safety.ui.viewmodels.effects.Effects
import org.bson.types.ObjectId

class FindingsFragment : BaseSharedVmFragment() {

    private val viewModel: MainViewModel by lazy(this::getViewModel)
    private lateinit var binding: FindingFragmentBinding
    private lateinit var recyclerviewAdapter: GenericRecyclerviewAdapter2<FindingVhCell>

    private val deleteFindingFragmentDialogTag = "DeleteFindingFragmentDialogTag"
    private val conclusionFragmentDialogTag = "ConclusionFragmentDialogTag"

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity() as MainActivity).mainActivityComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FindingFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initUi()
        initEffectObserver()
        initStateObserver()
        viewModel.getReportListFindings()
    }

    private fun initStateObserver() {
        viewModel.viewState.observeMviLiveData(viewLifecycleOwner) { prevState, currentState ->
            val prev = prevState?.findingFragmentState
            val curr = currentState.findingFragmentState

            if (prev == null || prev.findingVhCellArrayList != curr.findingVhCellArrayList) {
                recyclerviewAdapter.submitList(curr.findingVhCellArrayList)
            }

            if (prev == null || prev.conclusion != curr.conclusion) {
                binding.conclusion.text = curr.conclusion
            }
        }
    }

    private fun initEffectObserver() {
        viewModel.viewEffect.observe(viewLifecycleOwner) { effect ->
            when (effect) {
                is Effects.StartCreateFindingFragment -> startCreateFindingFragment()
                is Effects.ShowReportConclusionDialog -> showReportConclusionDialog()
                is Effects.ShowDeleteFindingDialog -> showDeleteFindingDialog()
                else -> {}
            }
        }
    }

    private fun showDeleteFindingDialog() {
        val fragment = DeleteFindingFragmentDialog()
        fragment.show(childFragmentManager, deleteFindingFragmentDialogTag)
    }

    private fun initUi() {
        initRecyclerView()

        binding.addBtn.setOnClickListener {
            viewModel.startCreateFindingFragment()
        }

        binding.conclusionCard.setOnClickListener {
            viewModel.showReportConclusionDialog()
        }
    }

    private fun initRecyclerView() {
        val recyclerView = binding.recyclerview
        recyclerviewAdapter = GenericRecyclerviewAdapter2()

        val setter = VhItemSetters<FindingVhCell>()
        setter.createVh = CreateFindingDetailsViewHolder::class.java

        setter.clickListener = object : FindingVhItemClick {
            override fun onItemClick(item: FindingVhCell) {
                viewModel.editAFinding(item.id as ObjectId)
            }

            override fun onLongClick(item: FindingVhCell): Boolean {
                viewModel.showDeleteFindingDialog(item.id as ObjectId)
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

    private fun startCreateFindingFragment() {
        val fragment = CreateFindingFragment()
//        val fragment = CreateFindingScreen()
        requireActivity().addFragment(fragment, R.id.fragment_container, "CreateFindingFragment")
    }

    private fun showReportConclusionDialog() {
        val fragment = ConclusionFragmentDialog()
        fragment.show(childFragmentManager, conclusionFragmentDialogTag)
    }

}

