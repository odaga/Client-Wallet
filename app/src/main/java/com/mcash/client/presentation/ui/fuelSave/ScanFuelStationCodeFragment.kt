package com.mcash.client.presentation.ui.fuelSave

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.mcash.client.R
import com.mcash.client.core.base.BaseFragment
import com.mcash.client.databinding.FragmentScanFuelSationCodeBinding
import com.mcash.client.presentation.ui.fuelSave.FuelSaveFragment.Companion.BRANCH_DETAILS_LABEL
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@AndroidEntryPoint
class ScanFuelStationCodeFragment :
    BaseFragment<FragmentScanFuelSationCodeBinding>(FragmentScanFuelSationCodeBinding::inflate) {

    private val fuelsSaveViewModel: FuelSaveViewModel by viewModels()

    private lateinit var cameraExecutors: ExecutorService
    private var cameraSelector: CameraSelector? = null
    private var cameraControl: CameraControl? = null
    private var imageAnalysis: ImageAnalysis? = null
    private var barcodeScanner: BarcodeScanner? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cameraExecutors = Executors.newSingleThreadExecutor()

        requireActivity().onBackPressedDispatcher.addCallback(
            requireActivity(),
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (findNavController().currentDestination?.id == R.id.scanFuelStationCodeFragment)
                        navigateUp()
                }
            })

        with(binding) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                requestPermissions()
            }
            closeCameraButton.setOnClickListener {
                if (findNavController().currentDestination?.id == R.id.scanFuelStationCodeFragment)
                    navigateUp()
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

            buttonTryAgain.setOnClickListener {
                searchActive = false
                searchView.visibility = View.GONE

            }

            with(fuelsSaveViewModel) {
                branchDetailState.observe(viewLifecycleOwner) {
                    when (it) {
                        is FuelSaveViewModel.BranchDetailsState.Initial -> Unit
                        is FuelSaveViewModel.BranchDetailsState.Loading -> {
                            searchActive = true
                            searchView.visibility = View.VISIBLE
                            progressCircular.visibility = View.VISIBLE
                            buttonTryAgain.visibility = View.GONE
                        }

                        is FuelSaveViewModel.BranchDetailsState.Error -> {
                            barcodeDataField.text = it.message
                            progressCircular.visibility = View.GONE
                            buttonTryAgain.visibility = View.VISIBLE

                        }

                        is FuelSaveViewModel.BranchDetailsState.Success -> {
                            searchActive = false

                            searchView.visibility = View.GONE
                            barcodeDataField.text = it.data.toString()
                            if (findNavController().currentDestination?.id == R.id.scanFuelStationCodeFragment) {
                                val bundle = Bundle()
                                println("payload: ${it.data}")
                                bundle.putString(BRANCH_CODE_NAME, it.data.code)
                                navigateWithBundle(
                                    R.id.action_scanFuelStationCodeFragment_to_fuelStationDetailsFragment,
                                    bundle
                                )
                            }

                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cameraExecutors.shutdown()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        val barcodeOptions = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()
        barcodeScanner = BarcodeScanning.getClient(barcodeOptions)
        val barcodeScanner: BarcodeScanner = BarcodeScanning.getClient()

        cameraProviderFuture.addListener(
            {
                val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder()
                    .build()
                    .also {
                        it.surfaceProvider = binding.previewView.surfaceProvider

                    }
                imageAnalysis = ImageAnalysis.Builder()
                    .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .setTargetRotation(binding.previewView.display.rotation)
                    .build()
                    .also {
                        it.setAnalyzer(cameraExecutors) { imageProxy ->
                            processImageProxy(barcodeScanner, imageProxy)
                        }
                        // Select back camera as a default
                        cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                    }
                try {
                    cameraProvider.unbindAll()
                    val camera = cameraProvider.bindToLifecycle(
                        this, cameraSelector!!, preview, imageAnalysis
                    )
                    cameraControl = camera.cameraControl
                } catch (exc: Exception) {
                    Log.e(TAG, "Use case binding failed", exc)
                }
            }, ContextCompat.getMainExecutor(requireContext())
        )
    }


    @SuppressLint("UnsafeOptInUsageError")
    private fun processImageProxy(
        barcodeScanner: BarcodeScanner,
        imageProxy: ImageProxy
    ) {
        val inputImage =
            InputImage.fromMediaImage(imageProxy.image!!, imageProxy.imageInfo.rotationDegrees)

        barcodeScanner.process(inputImage)
            .addOnSuccessListener { barcodes ->


                if (!searchActive) {
                    barcodes.forEach { barcode ->
                        when (barcode.valueType) {
                            Barcode.TYPE_TEXT -> {
                                fuelsSaveViewModel.getBranchDetails(barcode.rawValue.toString())
                                binding.barcodeDataField.text = barcode.rawValue.toString().trim()
                            }

                            else -> {
                                Log.e(TAG, "processImageProxy: Unsupported Barcode Type")
                            }
                        }

                    }
                } else println("==== search is active: $searchActive")


            }
            .addOnFailureListener {
                Log.e(TAG, "processImageProxy: ${it.message}")
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    }

    private fun previewFuelBranchCode(barcodeData: String) {
        with(binding) {
            barcodeDataField.text = barcodeData.trim()

        }
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
            if (!permissionGranted) {
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


    companion object {
        private const val TAG = "ScanFuelStationCodeFragment"
        const val BRANCH_CODE_NAME = "BRANCH_CODE"
        private var flashOn = false
        private var searchActive = false
        private val REQUIRED_PERMISSIONS =
            mutableListOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }


}