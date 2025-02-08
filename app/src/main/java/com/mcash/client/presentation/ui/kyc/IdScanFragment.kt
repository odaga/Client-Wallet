
package com.mcash.client.presentation.ui.kyc

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.util.Size
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
import com.mcash.client.core.models.IdCardInfo
import com.mcash.client.databinding.FragmentIdScanBinding
import com.mcash.client.presentation.ui.fuelSave.FuelSaveFragmentDirections
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@AndroidEntryPoint
class IdScanFragment : BaseFragment<FragmentIdScanBinding>(FragmentIdScanBinding::inflate) {

    private lateinit var cameraExecutors: ExecutorService
    private var cameraControl: CameraControl? = null
    private var imageAnalysis: ImageAnalysis? = null
    private var barcodeScanner: BarcodeScanner? = null

    private val formViewModel: FormViewModel by viewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cameraExecutors = Executors.newSingleThreadExecutor()
        with(binding) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                requestPermissions()
            }
            closeCameraButton.setOnClickListener {
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

            requireActivity().onBackPressedDispatcher.addCallback(
                requireActivity(),
                object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        if (findNavController().currentDestination?.id == R.id.idScanFragment)
                            navigateUp()
//                            navigate(IdScanFragmentDirections.actionIdScanFragmentToFuel())
//                            navigate(FuelSaveFragmentDirections.actionFuelSaveFragmentToFragmentHome())
                    }
                })

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cameraExecutors.shutdown()
    }


    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        val barcodeOptions = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_PDF417)
            .build()
        barcodeScanner = BarcodeScanning.getClient(barcodeOptions)
        val barcodeScanner: BarcodeScanner = BarcodeScanning.getClient()

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            // set Preview use case
            val preview = Preview.Builder()
                .build()
                .also {
                    it.surfaceProvider = binding.previewView.surfaceProvider
                }
            // set image Analysis use case
            imageAnalysis = ImageAnalysis.Builder()
                .setTargetResolution(Size(1920, 1080))
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setTargetRotation(binding.previewView.display.rotation)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutors) { imageProxy ->
                        Log.d(TAG, "startCamera: Barcode processing proxy, $imageProxy")
                        processImageProxy(barcodeScanner, imageProxy)
                    }
                }

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()
                // Bind use cases to camera
                val camera = cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageAnalysis
                )
                cameraControl = camera.cameraControl
            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
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
                barcodes.forEach {
                    println(it.rawValue)
                    it.rawValue.let { value ->
                        println(value)
                        val cardInfoEntity = extractIdData(value)
                        println(cardInfoEntity)
                        backToRegister(cardInfoEntity)
                    }
                }
            }.addOnFailureListener {
                Log.e(TAG, buildString {
                    append("processImageProxy: ")
                    append(it.message)
                })
            }
            .addOnCompleteListener {
                imageProxy.close()
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

    private fun extractIdData(rawValue: String?): IdCardInfo {
        return try {
            val strArray = rawValue!!.split(";")
            val lastname = strArray[0].decodeString()
            val firstname = strArray[1].decodeString()
            val middlename = strArray[2].decodeString()
            val dob = strArray[3].parseCardDate("/")
            val dateIssue = strArray[4].parseCardDate("/")
            val expiry = strArray[5].parseCardDate("/")
            val ninNum = strArray[6]
            val cardNum = strArray[7]
            val gender = ninNum[1]

            IdCardInfo(
                firstName = firstname,
                lastName = lastname,
                middleName = middlename,
                dateOfBirth = dob,
                expiryDate = expiry,
                issueDate = dateIssue,
                nin = ninNum,
                cardNumber = cardNum,
                gender = gender.toString(),
                nationality = "UGA"
            )
        } catch (e: Exception) {
            Log.e(TAG, "extractIdData: ${e.message}")
            IdCardInfo()
        }
    }

    private fun String.decodeString(): String {
        val s = Base64.decode(this, Base64.DEFAULT)
        return String(s)
    }

    private fun String.parseCardDate(separator: String): String {
        val day = this.substring(0..1)
        val mon = this.substring(2..3)
        val yr = this.substring(4..7)

        return "\"$day$separator$mon$separator$yr\""
    }

//    private fun backToRegister(idCardInfo: IdCardInfo) {
////        println("===== card info:  \n $idCardInfo")
////        try {
////            if (findNavController().currentDestination?.id == R.id.idScanFragment) {
////                runBlocking {
////                    with(formViewModel) {
////                        saveCardInfo(idCardInfo)
////                        navigateUp()
////                    }
////                }
////            }
////        } catch (t: Throwable) {
////            throw t
////        }
////
////    }

    private fun backToRegister(cardInfoEntity: IdCardInfo) {
        println("===== card info:  \n $cardInfoEntity")
        try {
            if (findNavController().currentDestination?.id == R.id.idScanFragment) {
                val bundle = Bundle()
                bundle.putString("ID_CARD_DRIVER_NIN_VALUE", cardInfoEntity.nin)
                navigateWithBundle(R.id.action_idScanFragment_to_driverOnboardingFragment, bundle)
            }
        } catch (t: Throwable) {
            throw t
        }
    }

    companion object {
        private const val TAG = "IdScanFragment"
        private var flashOn = false
        private var searchActive = false
        private val REQUIRED_PERMISSIONS =
            mutableListOf(
                Manifest.permission.CAMERA
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }
}