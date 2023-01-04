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
import com.e.safety.ui.recyclerviews.celldata.ImageViewVhCell
import com.e.safety.ui.recyclerviews.celldata.StudyPlaceDataVhCell
import com.e.safety.ui.recyclerviews.clicklisteners.StudyPlaceVhItemClick
import com.e.safety.ui.recyclerviews.generics.GenericRecyclerviewAdapter2
import com.e.safety.ui.recyclerviews.generics.VhItemSetters
import com.e.safety.ui.recyclerviews.viewholders.CreateImageViewVh
import com.e.safety.ui.recyclerviews.viewholders.CreateStudyPlacesVh
import com.e.safety.ui.utils.addFragment
import com.e.safety.ui.viewmodels.MainViewModel
import com.e.safety.ui.viewmodels.effects.Effects
import org.bson.types.ObjectId


class MainActivity : BaseActivity() {

    private val viewModel: MainViewModel by lazy(this::getViewModel)
    private val deleteStudyPlaceFragmentDialogTag = "DeleteStudyPlaceFragmentDialogTag"

    private lateinit var recyclerviewAdapter:
            GenericRecyclerviewAdapter2<StudyPlaceDataVhCell>

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
        recyclerviewAdapter = GenericRecyclerviewAdapter2()

        val setter = VhItemSetters<StudyPlaceDataVhCell>(
            layoutId =  R.layout.study_place_vh_cell_design,
        )
        setter.createVh = CreateStudyPlacesVh::class.java

        setter.clickListener = object : StudyPlaceVhItemClick {
            override fun onItemClick(item: StudyPlaceDataVhCell) {
                viewModel.startReportsFragment(item.id as ObjectId)
            }

            override fun onEditBtnClick(item: StudyPlaceDataVhCell) {
                viewModel.getStudyPlaceDetailsForEdit(item.id as ObjectId)
            }

            override fun onLongClick(item: StudyPlaceDataVhCell): Boolean {
                viewModel.showDeleteStudyPlaceDialog(item.id as ObjectId)
                return true
            }
        }
        recyclerviewAdapter.setVhItemSetter(setter)
        recyclerview.adapter = recyclerviewAdapter
        recyclerview.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false
        )
        recyclerview.setHasFixedSize(true)
    }
}

