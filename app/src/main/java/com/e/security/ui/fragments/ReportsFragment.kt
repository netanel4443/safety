package com.e.security.ui.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.recyclerview.widget.LinearLayoutManager
import com.e.security.ui.activities.MainActivity
import com.e.security.R
import com.e.security.databinding.RecyclerviewAddBtnScreenBinding
import com.e.security.ui.viewmodels.MainViewModel
import com.e.security.ui.activityresults.SaveFileResultContract
import com.e.security.ui.dialogfragments.ReportFragmentMenuRvDialog
import com.e.security.ui.dialogs.CalendarDialog
import com.e.security.ui.recyclerviews.celldata.ReportVhCell
import com.e.security.ui.recyclerviews.clicklisteners.ReportVhItemClick
import com.e.security.ui.recyclerviews.generics.GenericRecyclerviewAdapter
import com.e.security.ui.recyclerviews.viewholders.CreateStudyPlaceReportsVh
import com.e.security.ui.utils.addFragment
import com.e.security.ui.viewmodels.effects.Effects

class ReportsFragment : BaseSharedVmFragment() {

    private lateinit var binding: RecyclerviewAddBtnScreenBinding
    private val viewModel: MainViewModel by lazy(this::getViewModel)
    private lateinit var recyclerviewAdapter:
            GenericRecyclerviewAdapter<ReportVhCell,
                    CreateStudyPlaceReportsVh>

    private var calendarDialog: CalendarDialog? = null

    private lateinit var wordLauncher: ActivityResultLauncher<String?>
    private lateinit var pdfLauncher: ActivityResultLauncher<String?>
    private val reportFragmentMenuRvDialogTag = "ReportFragmentMenuRvDialog"

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity() as MainActivity).mainActivityComponent.inject(this)
        wordLauncher = registerForActivityResult(viewModel::saveWordFile)
        pdfLauncher = registerForActivityResult(viewModel::savePdfFile)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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
            recyclerviewAdapter.submitList(curr.reportVhCellArrayList)
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
                is Effects.StartActivityForResultWord -> saveWordFile(effect.type)
                is Effects.StartActivityForResultPdf -> savePdfFile(effect.type)
                is Effects.ShowReportFragmentRecyclerViewMenu -> showEducationalInstitutionsDialog()
//                is Effects.ShowDeleteDialogReportScreen ->Toast.makeText(requireActivity(),effect.message,Toast.LENGTH_SHORT).show()
                else -> {}
            }
        }
    }

    private fun saveWordFile(type: String) {
        wordLauncher.launch(type)
    }

    private fun savePdfFile(type: String) {
        pdfLauncher.launch(type)
    }

    private fun registerForActivityResult(func: (Uri) -> Unit): ActivityResultLauncher<String?> {
        return registerForActivityResult(SaveFileResultContract()) { result ->

            result?.let {
                it.data?.let { uri ->
                    func.invoke(uri)
                }
            }
        }
    }


    private fun startFindingDetailsFragment() {
        val fragment = FindingsFragment()
        requireActivity().addFragment(
            fragment,
            binding.fragmentContainer.id,
            "FindingsFragment"
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
                viewModel.showReportFragmentRecyclerViewMenu(resources.getStringArray(R.array.esd))
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
        if (calendarDialog == null) {
            createCalendarDialog()
        }
        calendarDialog!!.show()
    }

    private fun createCalendarDialog() {
        calendarDialog = CalendarDialog(requireActivity())
        calendarDialog!!.onClick = { date ->
            viewModel.setReportDate(date)
        }
    }

    private fun showEducationalInstitutionsDialog() {
        val reportFragmentMenuRvDialog = ReportFragmentMenuRvDialog()
        reportFragmentMenuRvDialog.show(childFragmentManager, reportFragmentMenuRvDialogTag)
    }

    override fun onDestroy() {
        super.onDestroy()
        calendarDialog?.let {
            if (it.isShowing()) {
                it.dismiss()
            }
        }
    }
}
