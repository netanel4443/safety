package com.e.safety.ui.fragments

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.e.safety.R
import com.e.safety.data.FindingDataHolder
import com.e.safety.databinding.CreateFindingBinding
import com.e.safety.ui.activities.mainactivity.MainActivity
import com.e.safety.ui.dialogfragments.FilterResultsDialogFragment
import com.e.safety.ui.dialogfragments.ImageOptionsDialog
import com.e.safety.ui.recyclerviews.celldata.ImageViewVhCell
import com.e.safety.ui.recyclerviews.clicklisteners.ImageVhItemClickListener
import com.e.safety.ui.recyclerviews.generics.GenericRecyclerviewAdapter2
import com.e.safety.ui.recyclerviews.generics.VhItemSetters
import com.e.safety.ui.recyclerviews.viewholders.CreateImageViewVh
import com.e.safety.ui.spinners.GenericSpinner
import com.e.safety.ui.utils.addFragment
import com.e.safety.ui.utils.rxjava.throttleClick
import com.e.safety.ui.viewmodels.MainViewModel
import com.e.safety.ui.viewmodels.effects.Effects

class CreateFindingFragment : BaseSharedVmFragment() {

    private val viewModel: MainViewModel by lazy(this::getViewModel)
    private lateinit var binding: CreateFindingBinding
    private var problemImages: ArrayList<ImageViewVhCell> = ArrayList()
    private var findingToEdit = FindingDataHolder()
    private lateinit var photoLauncher: ActivityResultLauncher<Array<String>>
    private val filterResultsDialogFragmentTag = "FilterResultsDialogFragment"
    private val imageOptionsDialogDialogTag = "ImageOptionsDialog"
    private var imageRecyclerAdapter: GenericRecyclerviewAdapter2<ImageViewVhCell>? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity() as MainActivity).mainActivityComponent.inject(this)
        photoLauncher = initPhotoLauncher()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = CreateFindingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initUi()
        initStateObserver()
        initEffectObserver()
    }

    private fun initUi() {

        binding.requirement.setOnClickListener {
            viewModel.getAppropriateHozerItems()
            viewModel.showHozerMankalDialog()
        }
        binding.addImageBtn.throttleClick {
            viewModel.showPhotoUploadDialog(resources.getStringArray(R.array.photo_operations))
        }.addDisposable()

        val spinnerAdapter = GenericSpinner()
        binding.priority.adapter = spinnerAdapter.create(requireActivity(), R.array.priority_array)

        binding.confirmButton.throttleClick {
            findingToEdit.priority = binding.priority.selectedItem.toString()
            findingToEdit.sectionInAssessmentList = binding.sectionInAssessmentList.text.toString()
            findingToEdit.problem = binding.problemDescription.text.toString()
            findingToEdit.problemLocation = binding.locationDescription.text.toString()
            findingToEdit.requirement = binding.requirement.text.toString()

            viewModel.saveFinding(findingToEdit, problemImages)
            viewModel.popFragment()
        }.addDisposable()

        initImagesRecyclerView()
    }


    private fun initEffectObserver() {
        viewModel.viewEffect.observe(viewLifecycleOwner) { effect ->
            when (effect) {
                is Effects.ShowHozerMankalDialog -> showFilterResultDialog()
                is Effects.ShowPhotoUploadDialog -> showImageOptionsDialog()
                is Effects.TakePhoto -> takePhoto()
                is Effects.SelectPhoto -> pickImage()
                else -> {}
            }
        }
    }

    fun initStateObserver() {
        viewModel.viewState.observeMviLiveData(viewLifecycleOwner) { prevState, currentState ->
            val prev = prevState?.createFindingFragmentState
            val curr = currentState.createFindingFragmentState

            if (prev == null ||
                prev.problemImage != curr.problemImage
            ) {
                problemImages = curr.problemImage
                imageRecyclerAdapter!!.submitList(problemImages)
            }
            if (prev == null ||
                prev.finding.sectionInAssessmentList != curr.finding.sectionInAssessmentList
            ) {
                binding.requirement.text = curr.finding.requirement
                binding.sectionInAssessmentList.text = curr.finding.sectionInAssessmentList
                findingToEdit.testArea = curr.finding.testArea // update data for future pass to vm
            }
            if (prev == null || prev.finding.problem != curr.finding.problem) {
                binding.problemDescription.setText(curr.finding.problem)
            }
            if (prev == null || prev.finding.problemLocation != curr.finding.problemLocation) {
                binding.locationDescription.setText(curr.finding.problemLocation)
            }
            if (prev == null || prev.finding.priority != curr.finding.priority){
                val array = resources.getStringArray(R.array.priority_array)
                binding.priority.setSelection(array.indexOf(curr.finding.priority))
            }
        }
    }

    private fun showFilterResultDialog() {
        val filterResultsDialogFragment = FilterResultsDialogFragment()
        filterResultsDialogFragment.show(childFragmentManager, filterResultsDialogFragmentTag)
    }

    private fun initImagesRecyclerView() {
        val recyclerView = binding.imageRecyclerView
        imageRecyclerAdapter = GenericRecyclerviewAdapter2()

        val setter = VhItemSetters<ImageViewVhCell>(
            layoutId =  R.layout.image_vh_cell_design,
        )
        setter.createVh = CreateImageViewVh::class.java

        setter.clickListener = object : ImageVhItemClickListener {
            override fun onItemClick(item: ImageViewVhCell) {
                viewModel.showPhotoUploadDialog(resources.getStringArray(R.array.photo_operations))
            }

            override fun onDeleteImage(item: ImageViewVhCell) {
                viewModel.deleteImage(item)
            }
        }

        imageRecyclerAdapter!!.setVhItemSetter(setter)

        recyclerView.adapter = imageRecyclerAdapter

        recyclerView.layoutManager = LinearLayoutManager(
            requireActivity(),
            LinearLayoutManager.HORIZONTAL,
            false
        )
        recyclerView.setHasFixedSize(true)
    }

    private fun showImageOptionsDialog() {
        val imageOptionsDialogD = ImageOptionsDialog()
        imageOptionsDialogD.show(childFragmentManager, imageOptionsDialogDialogTag)
    }


    private fun takePhoto() {
        val fragment = CameraFragment()
        requireActivity().addFragment(fragment, R.id.fragment_container, "CameraFragment")
    }

    private fun initPhotoLauncher(): ActivityResultLauncher<Array<String>> {
        return registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            uri?.let {
                if (ContextCompat.checkSelfPermission(
                        requireActivity(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    viewModel.saveImage(uri)
                }
              //  viewModel.addProblemImage(uri)
            }
        }
    }

    private fun pickImage() {
        photoLauncher.launch(arrayOf("image/*"))
    }
}