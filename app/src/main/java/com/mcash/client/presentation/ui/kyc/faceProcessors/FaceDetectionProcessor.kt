package com.mcash.client.presentation.ui.kyc.faceProcessors

import android.graphics.Rect
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import java.io.IOException


class FaceDetectionProcessor(
    private val view: GraphicOverlay,
    private val viewModel: FaceCameraViewModel,
) : BaseImageAnalyser<List<Face>>() {
    override val graphicOverlay: GraphicOverlay
        get() = view

    private val faceDetectorOptions = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
        .enableTracking()
        .build()

    private val faceDetector = FaceDetection.getClient(faceDetectorOptions)


    override fun detectInImage(image: InputImage): Task<List<Face>> {
        return faceDetector.process(image)
    }

    override fun stop() {
        try {
            faceDetector.close()
        } catch (e: IOException) {
            Log.e(TAG, "stop: Exception thrown while closing face detector: ${e.message}")
        }
    }

    override fun onSuccess(results: List<Face>, graphicOverlay: GraphicOverlay, rect: Rect) {
        graphicOverlay.clear()
        viewModel.updateNumberOfDetectedFaces(results.size)

        results.forEach() { face ->
            println(face)
            val eulerY = face.headEulerAngleY
            val eulerX = face.headEulerAngleX
            val eulerZ = face.headEulerAngleZ
            viewModel.updateFaceOrientation(eulerX, eulerY, eulerZ)
            val faceGraphic = FaceDetectionGraphic(graphicOverlay, face, rect)
            graphicOverlay.add(faceGraphic)
        }
    }

    override fun onFailure(e: Exception) {
        Log.e(TAG, "onFailure: Face detection failed: ${e.message}")
    }

    companion object {
        private const val TAG = "FaceDetector"
    }
}