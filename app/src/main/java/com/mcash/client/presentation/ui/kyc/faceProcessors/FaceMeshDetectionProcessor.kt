package com.mcash.client.presentation.ui.kyc.faceProcessors

import android.graphics.Rect
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.google.mlkit.vision.facemesh.FaceMesh
import com.google.mlkit.vision.facemesh.FaceMeshDetection
import com.google.mlkit.vision.facemesh.FaceMeshDetectorOptions
import java.io.IOException

class FaceMeshDetectionProcessor(
    private val view: GraphicOverlay,
//    private val viewModel: FaceCameraViewModel
) :
    BaseImageAnalyser<List<FaceMesh>>() {
    override val graphicOverlay: GraphicOverlay
        get() = view

    private val faceMeshOptions = FaceMeshDetectorOptions.Builder()
        .setUseCase(FaceMeshDetectorOptions.FACE_MESH)
        .build()

    private val detector = FaceMeshDetection.getClient(faceMeshOptions)

    private val faceDetectorOptions = FaceDetectorOptions.Builder()
        .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
        .build()

    private val faceDetector = FaceDetection.getClient(faceDetectorOptions)


    override fun detectInImage(image: InputImage): Task<List<FaceMesh>> {
        faceDetector.process(image)
        return detector.process(image)
    }

    override fun stop() {
        try {
            detector.close()
        } catch (e: IOException) {
            Log.e(TAG, "Exception thrown closing face mesh detector: ${e.message}")
        }
    }

    override fun onSuccess(results: List<FaceMesh>, graphicOverlay: GraphicOverlay, rect: Rect) {
        graphicOverlay.clear()


//        with(viewModel) {
//            // Update the number of faces detected
//            updateNumberOfDetectedFaces(results.size)
//        }
        for (faceMesh in results) {
            // pass the face mesh objects to the face mesh graphic for rendering.
            results.forEach {

                // Update the detected face bounding box area
//                viewModel.updateFaceBoundingBoxArea((rect.width() * rect.height()).toFloat())
//                val faceGraphic = FaceMeshGraphic(graphicOverlay, it, viewModel)
                val faceGraphic = FaceMeshGraphic(graphicOverlay, it)
                graphicOverlay.add(faceGraphic)
            }
            graphicOverlay.postInvalidate()
        }
    }

    override fun onFailure(e: Exception) {
        Log.e(TAG, "Face mesh detection failed: ${e.message}")
    }

    companion object {
        private const val TAG = "FaceMeshDetector"
    }
}