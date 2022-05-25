package com.e.security.ui.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.e.security.MainActivity
import com.e.security.R
import com.e.security.databinding.RecyclerviewAddBtnScreenBinding
import com.e.security.ui.MainViewModel
import com.e.security.ui.dialogs.CalendarDialog
import com.e.security.ui.recyclerviews.GenericRecyclerviewAdapter
import com.e.security.ui.recyclerviews.celldata.ReportVhCell
import com.e.security.ui.recyclerviews.clicklisteners.ReportVhItemClick
import com.e.security.ui.recyclerviews.viewholders.CreateStudyPlaceReportsVh
import com.e.security.ui.utils.addFragment
import com.e.security.ui.viewmodels.effects.Effects
import com.e.security.utils.differentItems

class ReportsFragment : BaseSharedVmFragment() {

    private lateinit var binding: RecyclerviewAddBtnScreenBinding
    private val viewModel: MainViewModel by lazy(this::getViewModel)
    private lateinit var recyclerviewAdapter:
            GenericRecyclerviewAdapter<ReportVhCell,
                    CreateStudyPlaceReportsVh>

    private var calendarDialog: CalendarDialog? = null
    private var launcher = registerForActivityResult()

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
        viewModel.getReportListOfChosenStudyPlace()
    }

    private fun initUi() {
        initRecyclerView()

        binding.addBtn.setOnClickListener {
            viewModel.createNewReport()
        }
    }

    private fun initStateObserver() {
        viewModel.viewState.observe(viewLifecycleOwner) { state ->
            val prev = state.prevState
            val curr = state.currentState
            println("  ${prev.reportVhCellArrayList} ${curr.reportVhCellArrayList}")
            when {
                recyclerviewAdapter.hasNoItems() -> {
                    recyclerviewAdapter.addItems(curr.reportVhCellArrayList)
                }
                prev.reportVhCellArrayList.size < curr.reportVhCellArrayList.size -> {
                    val newItems =
                        curr.reportVhCellArrayList.differentItems(prev.reportVhCellArrayList)
                    recyclerviewAdapter.addItems(newItems)
                }
                prev.reportVhCellArrayList.size > curr.reportVhCellArrayList.size -> {
                    val itemsToRemove =
                        curr.reportVhCellArrayList.differentItems(prev.reportVhCellArrayList)
                    recyclerviewAdapter.removeItems(itemsToRemove)
                }

                // if we arrived to this point , one item has been updated
                // so we need to reflect it on the ui

                prev.reportVhCellArrayList != curr.reportVhCellArrayList -> {
                    val itemsToRemove =
                        curr.reportVhCellArrayList.differentItems(prev.reportVhCellArrayList)
                    val itemsToAdd =
                        prev.reportVhCellArrayList.differentItems(curr.reportVhCellArrayList)
                    recyclerviewAdapter.changeItem(Pair(itemsToRemove[0], itemsToAdd[0]))
                }
            }
        }
    }

    private fun initEffectObserver() {
        viewModel.viewEffect.observe(viewLifecycleOwner) { effect ->
            when (effect) {
                is Effects.StartFindingsDetailsFragment -> startFindingDetailsFragment()
                is Effects.ShowCalendarDialog -> showCalendarDialog()
                is Effects.Toast -> Toast.makeText(
                    requireActivity(),
                    effect.message,
                    Toast.LENGTH_SHORT
                ).show()
                is Effects.StartActivityForResult -> saveFileIntent(effect.intent)
//                is Effects.ShowDeleteDialogReportScreen ->Toast.makeText(requireActivity(),effect.message,Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveFileIntent(intent: Intent) {
        launcher.launch(intent)
    }

    private fun registerForActivityResult(): ActivityResultLauncher<Intent> {
        return registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {

                result.data?.data?.let {
                    println(it)
                    viewModel.saveFile(it)
                }
            }
        }
    }

    private fun startFindingDetailsFragment() {
        val fragment = FindingsDetailsFragment()
        requireActivity().addFragment(
            fragment,
            binding.fragmentContainer.id,
            "FindingsDetailsFragment"
        )
    }


    private fun initRecyclerView() {
        val recyclerview = binding.recyclerview
        recyclerviewAdapter = GenericRecyclerviewAdapter(
            CreateStudyPlaceReportsVh::class.java
        )
        recyclerviewAdapter.setItemClickListener(object : ReportVhItemClick {
            override fun onItemClick(item: ReportVhCell) {
                viewModel.startFindingDetailsFragment(item.id)
            }

            override fun onEditBtnClick(item: ReportVhCell) {
                viewModel.setChosenReportId(item.id)
                viewModel.showStringRecyclerViewDialog(
                    resources.getStringArray(R.array.esd),
                    viewModel::editExportDeleteMenuSelection
                )
            }

            override fun onLongClick(item: ReportVhCell): Boolean {
                viewModel.setChosenReportId(item.id)
                viewModel.showDeleteReportDialog(viewModel::deleteReport)
                return true
            }
        })
        recyclerview.adapter = recyclerviewAdapter

        recyclerview.layoutManager = LinearLayoutManager(
            requireActivity(),
            LinearLayoutManager.VERTICAL,
            false
        )
        recyclerview.setHasFixedSize(true)
    }

    private fun showCalendarDialog() {
        calendarDialog?.run { showDialog() } ?: createCalendarDialog()

    }

    private fun createCalendarDialog() {
        calendarDialog = CalendarDialog(requireActivity())
        calendarDialog!!.onClick = { date ->
            viewModel.setReportDate(date)
        }
        calendarDialog!!.showDialog()
    }
}