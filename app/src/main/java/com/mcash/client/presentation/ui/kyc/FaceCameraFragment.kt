package com.mcash.client.presentation.ui.kyc

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.Size
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import com.google.common.util.concurrent.ListenableFuture
import com.mcash.client.R
import com.mcash.client.core.base.BaseFragment
import com.mcash.client.databinding.FragmentFaceCameraBinding
import com.mcash.client.presentation.ui.kyc.IdScanFragment.Companion
import com.mcash.client.presentation.ui.kyc.faceProcessors.FaceCameraViewModel
import com.mcash.client.presentation.ui.kyc.faceProcessors.FaceMeshDetectionProcessor
import dagger.hilt.android.AndroidEntryPoint
import id.zelory.compressor.Compressor
import kotlinx.coroutines.runBlocking
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@AndroidEntryPoint
class FaceCameraFragment :
    BaseFragment<FragmentFaceCameraBinding>(FragmentFaceCameraBinding::inflate) {

    private val formViewModel: FormViewModel by viewModels()
    private val viewModel: FaceCameraViewModel by viewModels()
    private var cameraControl: CameraControl? = null
    private var imageCapture: ImageCapture? = null
    private var imageAnalysis: ImageAnalysis? = null
    private lateinit var cameraExecutors: ExecutorService
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>

    private lateinit var facePhotoFile: File

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (arguments != null) {
            formLabel = arguments?.getString("FORM_GROUP_LABEL").toString()
        }

        with(binding) {
            cameraExecutors = Executors.newSingleThreadExecutor()

            // Request camera Permissions
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                requestPermissions()
            }

            cameraShutter.setOnClickListener {
                takeFacePhoto()
            }

            shutterButton.setOnClickListener {
                takeFacePhoto()
            }
            cameraFlashButton.setOnClickListener {
                if (flashOn) {
                    flashOn = false
                    cameraFlashButton.setImageResource(R.drawable.ic_flash_off)
                    cameraControl?.enableTorch(false)
                } else {
                    flashOn = true
                    cameraFlashButton.setImageResource(R.drawable.ic_flash_on)
                    cameraControl?.enableTorch(true)
                }
            }

            /**
             * Delete the face image file if it exists
             * */
            buttonDeletePhoto.setOnClickListener {
                shutterButton.visibility = View.GONE
                resultLayout.visibility = View.GONE
                cameraLayout.visibility = View.VISIBLE

                if (facePhotoFile.exists()) {
                    facePhotoFile.delete()
                } else {
                    Log.e(TAG, "onViewCreated: facePhotoFile does not exist")
                }
            }

            buttonSavePhoto.setOnClickListener {
                runBlocking {
                    with(formViewModel) {
                        if (facePhotoFile.exists()) {
                            saveKycFormData(facePhotoFile.path, formLabel, FORM_TYPE)
                        }
                        navigateUp()
                    }
                }
            }


            //   Listen face detection events
            with(viewModel) {
                numFacesDetected.observe(viewLifecycleOwner) {
                    faceCameraMessage.text = "Faces detected: $it"

                    if (it in 1..1) {
                        captureBtn.visibility = View.VISIBLE
                        numberOfDetectedFacesText.text = "Number of Faces Detected: $it / 1"
                        numberOfDetectedFacesCheck.visibility = View.VISIBLE
                        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_check)
                        numberOfDetectedFacesCheck.setImageBitmap(bitmap)

                    } else {
                        captureBtn.visibility = View.GONE
                        numberOfDetectedFacesText.text = "Number of Faces Detected: $it / 1"
                        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_check)
                        numberOfDetectedFacesCheck.setImageBitmap(bitmap)

                        subjectDistanceText.text = "Distance from Camera: "
                        val bitmap1 = BitmapFactory.decodeResource(resources, R.drawable.ic_check)
                        cameraDistanceCheck.setImageBitmap(bitmap1)
                    }
                }

                /**
                 *  Listen to changes in the check requirements from the combined view model
                 **/
                combinedFaceCameraCheckLiveData.observe(viewLifecycleOwner) { triple ->

                    val numFacesDetected = triple.first
                    val faceToCanvasRatio = triple.second
                    val screeOrientationPortrait = triple.third


                    areaRatioText.text = "Area Ratio: $faceToCanvasRatio"
                    if (faceToCanvasRatio < 0.1) {
                        areaRatioText.text =
                            "Area Ratio: $faceToCanvasRatio \n Optimal distance: No (Too far)"
                        subjectDistanceText.text = "Distance from Camera: ( Too Far )"
                        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_check)
                        cameraDistanceCheck.setImageBitmap(bitmap)
                    } else if (faceToCanvasRatio in 0.1..0.4) {
                        areaRatioText.text =
                            "Area Ratio: $faceToCanvasRatio \n Optimal distance: Yes"
                        subjectDistanceText.text = "Distance from Camera: ( Optimal Distance )"
                        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_check)
                        cameraDistanceCheck.setImageBitmap(bitmap)
                    } else if (faceToCanvasRatio == 2f) {
                        subjectDistanceText.text = "Distance from Camera: (No face detected)"
                        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_check)
                        cameraDistanceCheck.setImageBitmap(bitmap)
                    } else {
                        areaRatioText.text =
                            "Area Ratio: $faceToCanvasRatio \n Optimal distance: No (Too close)"
                        subjectDistanceText.text = "Distance from Camera: ( Too Close )"
                        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_check)
                        cameraDistanceCheck.setImageBitmap(bitmap)
                    }

                    if (screeOrientationPortrait) {
                        phoneOrientationText.text = "Phone Orientation: Portrait"
                        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_check)
                        screenOrientationCheck.setImageBitmap(bitmap)
                    } else {
                        phoneOrientationText.text = "Phone Orientation: Landscape"
                        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_check)
                        screenOrientationCheck.setImageBitmap(bitmap)
                    }

                    if (numFacesDetected == 1 && faceToCanvasRatio in 0.1..0.4 && screeOrientationPortrait) {
                        //Show or enable camera shutter button
                        shutterButton.visibility = View.VISIBLE
                    } else {
                        // Hide or disable camera shutter button
                        shutterButton.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun takeFacePhoto() {
        val file = File(requireContext().filesDir, "face-photo.png")
        val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()

        imageCapture?.takePicture(outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    Log.d(TAG, "onImageSaved: Face photo taken successfully")
                    runBlocking {
                        val result = Compressor.compress(requireContext(), file)
                        facePhotoFile = file
                        showResult(result)
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: $exception")
                }
            })
    }

    private fun showResult(file: File) {
        with(binding) {
            cameraLayout.visibility = View.GONE
            val uri = Uri.fromFile(file)
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)

            facePhotoPreview.setImageBitmap(bitmap)
            resultLayout.visibility = View.VISIBLE
        }
    }

    private fun startCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            bindPreview(cameraProvider)
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun bindPreview(cameraProvider: ProcessCameraProvider?) {
        var preview: Preview = Preview.Builder()
            .build()
        var cameraSelector: CameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()

        val outputDirectory =
            File(ContextCompat.getNoBackupFilesDir(requireContext()), "com.mcash.kyc.App")
        outputDirectory.mkdirs()

        // Instantiate camera capture use case
        imageCapture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
            .build()


        // Set up image analysis instance
        imageAnalysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .also {
                it.setAnalyzer(
                    cameraExecutors, selectAnalyzer()
                )
            }

        try {
            preview.surfaceProvider = binding.viewFinder.surfaceProvider
            val camera = cameraProvider?.bindToLifecycle(
                this as LifecycleOwner,
                cameraSelector,
                preview,
                imageCapture,
                imageAnalysis
            )
            cameraControl = camera!!.cameraControl
        } catch (e: Exception) {
            Log.e(TAG, "Use case binding failed", e)
        }
    }


    private fun selectAnalyzer(): ImageAnalysis.Analyzer {
        return FaceMeshDetectionProcessor(binding.graphicOverlayFinder)
    }

    private val activityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        )
        { permissions ->
            // Handle Permission granted/rejected
            var permissionGranted = true
            permissions.entries.forEach {
                if (it.key in REQUIRED_PERMISSIONS && !it.value)
                    permissionGranted = false
            }
            if (permissionGranted) {
                startCamera()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Permission request denied",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


    private fun requestPermissions() {
        activityResultLauncher.launch(REQUIRED_PERMISSIONS)
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            requireContext(), it
        ) == PackageManager.PERMISSION_GRANTED
    }


    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            startCamera()
        } else {
            Toast.makeText(requireContext(), "Permission denied by user", Toast.LENGTH_SHORT).show()
        }
    }


    companion object {
        private const val TAG = "FaceCameraFragment"
        private const val FORM_TYPE = "File"
        private var formLabel = ""
        private var flashOn = false
        private const val REQUEST_CODE_PERMISSIONS = 3002
        private val REQUIRED_PERMISSIONS = mutableListOf(
            Manifest.permission.CAMERA
        )
            .apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }
}