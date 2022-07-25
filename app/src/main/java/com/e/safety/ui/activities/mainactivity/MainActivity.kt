package com.e.safety.ui.activities.mainactivity

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.e.safety.R
import com.e.safety.application.BaseApplication
import com.e.safety.databinding.RecyclerviewAddBtnScreenBinding
import com.e.safety.di.components.MainActivityComponent
import com.e.safety.ui.activities.BaseActivity
import com.e.safety.ui.fragments.DeleteStudyPlaceFragmentDialog
import com.e.safety.ui.fragments.ReportsFragment
import com.e.safety.ui.fragments.StudyPlaceInfoFragment
import com.e.safety.ui.recyclerviews.celldata.StudyPlaceDataVhCell
import com.e.safety.ui.recyclerviews.clicklisteners.StudyPlaceVhItemClick
import com.e.safety.ui.recyclerviews.generics.GenericRecyclerviewAdapter
import com.e.safety.ui.recyclerviews.viewholders.CreateStudyPlacesVh
import com.e.safety.ui.utils.addFragment
import com.e.safety.ui.viewmodels.MainViewModel
import com.e.safety.ui.viewmodels.effects.Effects


class MainActivity : BaseActivity() {

    private val viewModel: MainViewModel by lazy(this::getViewModel)
    private val deleteStudyPlaceFragmentDialogTag = "DeleteStudyPlaceFragmentDialogTag"
    private lateinit var recyclerviewAdapter:
            GenericRecyclerviewAdapter<StudyPlaceDataVhCell, CreateStudyPlacesVh>
    private var TAG = javaClass.name
    private lateinit var binding: RecyclerviewAddBtnScreenBinding
    lateinit var mainActivityComponent: MainActivityComponent

    override fun onCreate(savedInstanceState: Bundle?) {

        mainActivityComponent =
            (application as BaseApplication).appComponent.mainActivityComponent().create()
        mainActivityComponent.inject(this)

        super.onCreate(savedInstanceState)
        binding = RecyclerviewAddBtnScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        initUi()
        initStateObserver()
        initEffectObserver()
        viewModel.getStudyPlacesAndTheirFindings()

        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            PackageManager.PERMISSION_GRANTED
        )
    }

    private fun initEffectObserver() {
        viewModel.viewEffect.observe(this) { effect ->

            when (effect) {
                is Effects.StartReportsFragment -> startReportsFragment()
                is Effects.ShowStudyPlaceInfoDialogFragment -> startStudyPlaceInfoFragment()
                is Effects.ShowDeleteStudyPlaceDialog -> showDeleteDialog()
                is Effects.PopBackStack -> popFragment()
                else -> {}
            }
        }
    }

    private fun popFragment() {
        supportFragmentManager.popBackStack()
    }

    private fun startReportsFragment() {
        val fragment = ReportsFragment()
        addFragment(fragment, R.id.fragment_container, "ReportsFragment")
    }

    private fun initStateObserver() {
//        viewModel.viewState.observeMviLiveData(this, { state ->
////            val prev = state.prevState
////            val curr = state.currentState
//            recyclerviewAdapter.submitList(state.studyPlacesVhCellArrayList)
//
//        }) { prevState, currentState ->
//            if (prevState != null &&
//                prevState.studyPlacesVhCellArrayList != currentState.studyPlacesVhCellArrayList) {
//                recyclerviewAdapter.submitList(currentState.studyPlacesVhCellArrayList)
//            }
//        }
        viewModel.viewState.observeMviLiveData(this){ prevState, currentState->
            if (prevState == null ||
                prevState.studyPlacesVhCellArrayList != currentState.studyPlacesVhCellArrayList) {
                recyclerviewAdapter.submitList(currentState.studyPlacesVhCellArrayList)
            }
        }
    }

    private fun initUi() {
        initRecyclerview()

        binding.addBtn.setOnClickListener {
            viewModel.showEmptyStudyPlaceInfoDialogFragment()
        }
    }

    private fun startStudyPlaceInfoFragment() {
        addFragment(
            StudyPlaceInfoFragment(),
            R.id.fragment_container,
            StudyPlaceInfoFragment.TAG
        )
    }

    private fun showDeleteDialog() {
        val fragment = DeleteStudyPlaceFragmentDialog()
        fragment.show(supportFragmentManager, deleteStudyPlaceFragmentDialogTag)
    }

    private fun initRecyclerview() {
        val recyclerview = binding.recyclerview
        recyclerviewAdapter = GenericRecyclerviewAdapter(CreateStudyPlacesVh::class.java)
        recyclerviewAdapter.setItemClickListener(object : StudyPlaceVhItemClick {
            override fun onItemClick(item: StudyPlaceDataVhCell) {
                viewModel.startReportsFragment(item.id)
            }

            override fun onEditBtnClick(item: StudyPlaceDataVhCell) {
                viewModel.getStudyPlaceDetailsForEdit(item.id)
            }

            override fun onLongClick(item: StudyPlaceDataVhCell): Boolean {
                viewModel.showDeleteStudyPlaceDialog(item.id)
                return true
            }
        })
        recyclerview.adapter = recyclerviewAdapter
        recyclerview.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false
        )
        recyclerview.setHasFixedSize(true)
        binding.recyclerview.setOnClickListener {
        }
    }

}

