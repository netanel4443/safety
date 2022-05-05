package com.e.security.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.e.security.MainActivity
import com.e.security.databinding.ActivityCameraBinding
import com.e.security.sensors.CameraOperations
import com.e.security.ui.MainViewModel

class CameraFragment:BaseSharedVmFragment() {
    private lateinit var cameraOperations: CameraOperations
    private lateinit var binding: ActivityCameraBinding
    private val viewModel: MainViewModel by lazy(this::getViewModel)
    private  var launcher: ActivityResultLauncher<Array<String>> =registerForActivityResult()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity() as MainActivity).mainActivityComponent.inject(this)
        cameraOperations= CameraOperations(requireActivity() as MainActivity)

    }

  fun registerForActivityResult():ActivityResultLauncher<Array<String>> {
   return   registerForActivityResult(
          ActivityResultContracts.RequestMultiplePermissions()
      ) { permissions ->

          val granted = permissions.entries.all {
              it.value == true
          }
          if (granted) {
              cameraOperations.isGranted=true
          } else {
              Toast.makeText(
                  requireActivity(),
                  "Permissions not granted by the user.",
                  Toast.LENGTH_SHORT
              ).show()

          }
      }
  }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= ActivityCameraBinding.inflate(inflater)
          return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        cameraOperations.requestPermissions(binding.viewFinder)

        binding.takePhotoBtn.setOnClickListener {
            cameraOperations.takePhoto(){
                viewModel.setProblemImage(it)

            }
        }
    }
}