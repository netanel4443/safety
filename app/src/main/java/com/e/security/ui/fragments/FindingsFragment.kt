package com.e.security.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.e.security.ui.activities.mainactivity.MainActivity
import com.e.security.R
import com.e.security.databinding.FindingFragmentBinding
import com.e.security.ui.dialogfragments.DeleteFindingFragmentDialog
import com.e.security.ui.viewmodels.MainViewModel
import com.e.security.ui.dialogs.EditTextDialog
import com.e.security.ui.dialogs.helpers.IeditTextDialogHelper
import com.e.security.ui.recyclerviews.celldata.FindingVhCell
import com.e.security.ui.recyclerviews.generics.FindingVhItemClick
import com.e.security.ui.recyclerviews.generics.GenericRecyclerviewAdapter2
import com.e.security.ui.recyclerviews.generics.VhItemSetters
import com.e.security.ui.recyclerviews.viewholders.CreateFindingDetailsViewHolder
import com.e.security.ui.utils.addFragment
import com.e.security.ui.viewmodels.effects.Effects
import org.bson.types.ObjectId

class FindingsFragment : BaseSharedVmFragment() {

    private val viewModel: MainViewModel by lazy(this::getViewModel)
    private lateinit var binding: FindingFragmentBinding
    private lateinit var recyclerviewAdapter: GenericRecyclerviewAdapter2<FindingVhCell>
    private var editTextDialog: EditTextDialog? = null
    private val deleteFindingFragmentDialogTag = "DeleteFindingFragmentDialogTag"

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
        restoreState(savedInstanceState)
        viewModel.getReportListFindings()
    }

    private fun initStateObserver() {
        viewModel.viewState.observe(viewLifecycleOwner, { state ->

            val prev = state.prevState.findingFragmentState
            val curr = state.currentState.findingFragmentState

//            if (prev.findingVhCellArrayList != curr.findingVhCellArrayList || recyclerviewAdapter.hasNoItems()) {
                recyclerviewAdapter.submitList(curr.findingVhCellArrayList)
//            }

            if (prev.conclusion != curr.conclusion) {
                binding.conclusion.text = curr.conclusion
            }

        }) { state ->
            val curr = state.currentState.findingFragmentState

            binding.conclusion.text = curr.conclusion

            if (curr.reportConclusionDialogVisibility) {
                showReportConclusionDialog(curr.conclusion)
            }
        }
    }

    private fun initEffectObserver() {
        viewModel.viewEffect.observe(viewLifecycleOwner) { effect ->
            when (effect) {
                is Effects.StartCreateFindingFragment -> startCreateFindingFragment()
                is Effects.ShowReportConclusionDialog -> showReportConclusionDialog(effect.conclusion)
                is Effects.ShowDeleteFindingDialog -> showDeleteFindingDialog()
                else->{}
            }
        }
    }

    private fun showDeleteFindingDialog() {
        val fragment = DeleteFindingFragmentDialog()
       fragment.show(childFragmentManager,deleteFindingFragmentDialogTag)
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        //call only if dialog is showed
        editTextDialog?.let {
            if (it.isShowing()){
                outState.putString("editTextDialogUserText", it.getText())
            }
        }
    }

    private fun restoreState(bundle: Bundle?) {
        bundle?.run {
            getString("editTextDialogUserText")?.let {
                editTextDialog!!.setText(it)
            }
        }
    }

    private fun startCreateFindingFragment() {
        val fragment = CreateFindingFragment()
        requireActivity().addFragment(fragment, R.id.fragment_container, "CreateFindingFragment")
    }


    private fun showReportConclusionDialog(conclusion: String) {
        if (editTextDialog == null) {
            createReportConclusionDialog()
        }
        editTextDialog!!.show()
        editTextDialog!!.setText(conclusion)
    }

    private fun createReportConclusionDialog() {
        editTextDialog = EditTextDialog(requireActivity())
        editTextDialog!!.setHelper(object : IeditTextDialogHelper {
            override fun onDismissDialog() {
                viewModel.isReportConclusionDialogVisible(false)
            }

            override fun onShowDialog() {
                viewModel.isReportConclusionDialogVisible(true)
            }

            override fun onConfirm(text: String) {
                viewModel.saveReportConclusion(text)
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        editTextDialog?.dismissConfigurationChanges()
    }

}

