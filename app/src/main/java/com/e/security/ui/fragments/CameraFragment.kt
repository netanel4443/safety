package com.e.security.ui.fragments

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.e.security.MainActivity
import com.e.security.R
import com.e.security.databinding.ActivityCameraBinding
import com.e.security.sensors.CameraOperations
import com.e.security.ui.MainViewModel
import javax.inject.Inject

class CameraFragment : BaseSharedVmFragment() {
    //    private lateinit var cameraOperations: CameraOperations

    @Inject
    lateinit var cameraOperations: CameraOperations
    private lateinit var binding: ActivityCameraBinding
    private val viewModel: MainViewModel by lazy(this::getViewModel)
    private var launcher: ActivityResultLauncher<Array<String>> = registerForActivityResult()
    private val REQUIRED_PERMISSIONS =
        mutableListOf(
            Manifest.permission.CAMERA
        ).apply {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }.toTypedArray()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity() as MainActivity).mainActivityComponent.inject(this)
   }

    fun registerForActivityResult(): ActivityResultLauncher<Array<String>> {
        return registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->

            val granted = allPermissionsGranted()
            if (granted) {
                cameraOperations.startCamera(binding.viewFinder)
            } else {
                viewModel.toast(R.string.permission_not_granted)
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ActivityCameraBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        cameraOperations= CameraOperations(requireActivity() as MainActivity,viewLifecycleOwner)
        cameraOperations.bindLifeCycle(viewLifecycleOwner)
        val bool = requestPermissions()
        if (bool) {
            cameraOperations.startCamera(binding.viewFinder)
        } else {
            launcher.launch(REQUIRED_PERMISSIONS)
        }

        binding.takePhotoBtn.setOnClickListener {
            cameraOperations.takePhoto() {
                viewModel.addProblemImage(it)
                viewModel.popFragment()
            }
        }
    }

    private fun requestPermissions(): Boolean {
        return allPermissionsGranted()
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            requireActivity(), it
        ) == PackageManager.PERMISSION_GRANTED
    }


    override fun onDestroy() {
        super.onDestroy()
        //todo check if it should be here or another place
        cameraOperations.onDestroy()
    }
}