package com.e.safety.ui.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.recyclerview.widget.LinearLayoutManager
import com.e.safety.R
import com.e.safety.databinding.RecyclerviewAddBtnScreenBinding
import com.e.safety.ui.activities.mainactivity.MainActivity
import com.e.safety.ui.activityresults.SaveFileResultContract
import com.e.safety.ui.dialogfragments.ReportFragmentMenuRvDialog
import com.e.safety.ui.dialogs.CalendarDialog
import com.e.safety.ui.fragments.reportsfragment.DeleteReportFragmentDialog
import com.e.safety.ui.recyclerviews.celldata.ReportVhCell
import com.e.safety.ui.recyclerviews.clicklisteners.ReportVhItemClick
import com.e.safety.ui.recyclerviews.generics.GenericRecyclerviewAdapter2
import com.e.safety.ui.recyclerviews.generics.VhItemSetters
import com.e.safety.ui.recyclerviews.viewholders.CreateStudyPlaceReportsVh
import com.e.safety.ui.utils.addFragment
import com.e.safety.ui.viewmodels.MainViewModel
import com.e.safety.ui.viewmodels.effects.Effects
import org.bson.types.ObjectId

class ReportsFragment : BaseSharedVmFragment() {

    private lateinit var binding: RecyclerviewAddBtnScreenBinding
    private val viewModel: MainViewModel by lazy(this::getViewModel)

    private lateinit var recyclerviewAdapter:
            GenericRecyclerviewAdapter2<ReportVhCell>

    private var calendarDialog: CalendarDialog? = null

    private lateinit var wordLauncher: ActivityResultLauncher<String?>
    private lateinit var pdfLauncher: ActivityResultLauncher<String?>
    private val reportFragmentMenuRvDialogTag = "ReportFragmentMenuRvDialog"
    private val deleteReportFragmentDialogTag = "DeleteReportFragmentDialogTag"

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

        viewModel.viewState.observeMviLiveData(viewLifecycleOwner) {prevState, currentState ->
            val prev = prevState?.reportFragmentState
            val curr = currentState.reportFragmentState

            if (prev == null || prev.reportVhCellArrayList
                != curr.reportVhCellArrayList)
            recyclerviewAdapter.submitList(curr.reportVhCellArrayList)

            if (prev == null || prev.isLoading != curr.isLoading){

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
                is Effects.StartActivityForResultWord -> saveWordFile(effect.type)
                is Effects.StartActivityForResultPdf -> savePdfFile(effect.type)
                is Effects.ShowReportFragmentRecyclerViewMenu -> showEducationalInstitutionsDialog()
                is Effects.ShowDeleteReportDialog -> showDeleteReportDialog()
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

    private fun initRecyclerView(){
        val recyclerview = binding.recyclerview
        recyclerviewAdapter = GenericRecyclerviewAdapter2()

        val setters = VhItemSetters<ReportVhCell>(
            layoutId = R.layout.report_vh_cell_design
        )
        setters.createVh = CreateStudyPlaceReportsVh::class.java

        setters.clickListener = object : ReportVhItemClick {
            override fun onItemClick(item: ReportVhCell) {
                viewModel.startFindingDetailsFragment(item.id as ObjectId)
            }

            override fun onEditBtnClick(item: ReportVhCell) {
                viewModel.setChosenReportId(item.id as ObjectId)
                viewModel.showReportFragmentRecyclerViewMenu(resources.getStringArray(R.array.esd))
            }

            override fun onLongClick(item: ReportVhCell): Boolean {
                viewModel.setChosenReportId(item.id as ObjectId)
                viewModel.showDeleteReportDialog()
                return true
            }
        }
        recyclerviewAdapter.setVhItemSetter(setters)
        recyclerview.adapter = recyclerviewAdapter

        recyclerview.layoutManager = LinearLayoutManager(
            requireActivity(),
            LinearLayoutManager.VERTICAL,
            false
        )
        recyclerview.setHasFixedSize(true)

    }


//    private fun initRecyclerView() {
//        val recyclerview = binding.recyclerview
//        recyclerviewAdapter = GenericRecyclerviewAdapter(
//            CreateStudyPlaceReportsVh::class.java
//        )
//        recyclerviewAdapter.setItemClickListener(object : ReportVhItemClick {
//            override fun onItemClick(item: ReportVhCell) {
//                viewModel.startFindingDetailsFragment(item.id)
//            }
//
//            override fun onEditBtnClick(item: ReportVhCell) {
//                viewModel.setChosenReportId(item.id)
//                viewModel.showReportFragmentRecyclerViewMenu(resources.getStringArray(R.array.esd))
//            }
//
//            override fun onLongClick(item: ReportVhCell): Boolean {
//                viewModel.setChosenReportId(item.id)
//                viewModel.showDeleteReportDialog()
//                return true
//            }
//        })
//        recyclerview.adapter = recyclerviewAdapter
//
//        recyclerview.layoutManager = LinearLayoutManager(
//            requireActivity(),
//            LinearLayoutManager.VERTICAL,
//            false
//        )
//        recyclerview.setHasFixedSize(true)
//    }

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

    private fun showDeleteReportDialog() {
        val fragment = DeleteReportFragmentDialog()
        fragment.show(childFragmentManager, deleteReportFragmentDialogTag)
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
