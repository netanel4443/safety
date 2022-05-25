package com.e.security.sensors

import android.app.Application
import android.content.ContentValues
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.e.security.R
import com.e.security.utils.printErrorIfDbg
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Inject
//todo check if application here is ok or it should be an activity
class CameraOperations @Inject constructor(private val application: Application) {

    private var lifeCycle: LifecycleOwner? = null

    fun bindLifeCycle(lifecycleOwner: LifecycleOwner) {
        lifeCycle = lifecycleOwner
    }

    companion object {
        private const val TAG = "CameraOperations"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    }

    private var cameraExecutor: ExecutorService? = null
    private var imageCapture: ImageCapture? = null

    init {
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

//    val REQUIRED_PERMISSIONS =
//        mutableListOf(
//            Manifest.permission.CAMERA
//        ).apply {
//            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
//                add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
//            }
//        }.toTypedArray()


//    fun requestPermissions(): Boolean {
//
//        cameraExecutor = Executors.newSingleThreadExecutor()
//
//        return allPermissionsGranted()
//    }


//    fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
//        ContextCompat.checkSelfPermission(
//            activity!!, it
//        ) == PackageManager.PERMISSION_GRANTED
//    }

    fun startCamera(view: PreviewView) {

        val cameraProviderFuture = ProcessCameraProvider.getInstance(application)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(view.surfaceProvider)
                }

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            imageCapture = ImageCapture.Builder()
                .build()


            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    lifeCycle!!, cameraSelector, imageCapture, preview
                )

            } catch (exc: Exception) {
                printErrorIfDbg(TAG, exc.message)
            }

        }, ContextCompat.getMainExecutor(application))

    }

    fun takePhoto(uri: (Uri?) -> Unit) {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Create time stamped name and MediaStore entry.
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(
                    MediaStore.Images.Media.RELATIVE_PATH,
                    "Pictures/${application.getString(R.string.app_name)}"
                )
            }
        }

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(
                application.contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
            .build()

        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(application),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    printErrorIfDbg(TAG, "Photo capture failed: ${exc.message}")
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val msg = "תמונה נשמרה: ${output.savedUri}"
                    Toast.makeText(application, msg, Toast.LENGTH_SHORT).show()
                    uri(output.savedUri)
                }
            }
        )
    }

    fun onDestroy() {
        cameraExecutor?.shutdown()
    }
}
