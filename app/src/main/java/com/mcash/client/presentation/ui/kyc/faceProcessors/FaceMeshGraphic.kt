package com.mcash.client.presentation.ui.kyc.faceProcessors

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import com.google.mlkit.vision.common.PointF3D
import com.google.mlkit.vision.facemesh.FaceMesh
import com.google.mlkit.vision.facemesh.FaceMeshPoint

/**
 * Graphic instance for rendering face position and mesh info within the associated graphic overlay
 * view.
 */
class FaceMeshGraphic(
    overlay: GraphicOverlay,
    private val faceMesh: FaceMesh,
//    private val viewModel: FaceCameraViewModel
) :
    GraphicOverlay.Graphic(overlay) {

    private val positionPaint: Paint
    private val boxPaint: Paint
    private val useCase: Int
    private var zMin: Float
    private var zMax: Float

    @FaceMesh.ContourType
    private val DISPLAY_CONTOURS =
        intArrayOf(
            FaceMesh.FACE_OVAL,
            FaceMesh.LEFT_EYEBROW_TOP,
            FaceMesh.LEFT_EYEBROW_BOTTOM,
            FaceMesh.RIGHT_EYEBROW_TOP,
            FaceMesh.RIGHT_EYEBROW_BOTTOM,
            FaceMesh.LEFT_EYE,
            FaceMesh.RIGHT_EYE,
            FaceMesh.UPPER_LIP_TOP,
            FaceMesh.UPPER_LIP_BOTTOM,
            FaceMesh.LOWER_LIP_TOP,
            FaceMesh.LOWER_LIP_BOTTOM,
            FaceMesh.NOSE_BRIDGE
        )


    override fun draw(canvas: Canvas?) {
        // Draws the bounding box
        val rect = RectF(faceMesh.boundingBox)

//        viewModel.updateRatio(
//            (canvas!!.width * canvas.height).toFloat(),
//            (rect.width() * rect.height())
//        )


        /** We are skipping flipping of the points in case the image is not  flipped **/
        rect.left = rect.left.coerceAtMost(rect.right)
        rect.right = rect.left.coerceAtLeast(rect.right)
        canvas!!.drawRect(rect, boxPaint)

        // Draw face mesh
        val points =
            if (useCase == USE_CASE_CONTOUR_ONLY) getContourPoints(faceMesh) else faceMesh.allPoints
        val triangles = faceMesh.allTriangles
        zMin = Float.MAX_VALUE
        zMax = Float.MIN_VALUE

        for (point in points) {
            zMin = Math.min(zMin, point.position.z)
            zMax = Math.max(zMax, point.position.z)
        }

        //  Draw the face mesh points
//        for (point in points) {
//            canvas.drawCircle(
//                point.position.x,
//                point.position.y,
//                FACE_POSITION_RADIUS,
//                positionPaint
//            )
//        }

        //Draw face mesh triangles
//        for (triangle in triangles) {
//            val point1 = triangle.allPoints[0].position
//            val point2 = triangle.allPoints[1].position
//            val point3 = triangle.allPoints[2].position
//
//            drawLine(canvas, point1, point2)
//            drawLine(canvas, point1, point3)
//            drawLine(canvas, point2, point3)
//
//        }
    }

    private fun drawLine(canvas: Canvas, point1: PointF3D, point2: PointF3D) {
        canvas.drawLine(
            overlay.translateX(point1.x),
            overlay.translateY(point1.y),
            overlay.translateX(point2.x),
            overlay.translateY(point2.y),
            positionPaint
        )
    }


    private fun getContourPoints(faceMesh: FaceMesh): List<FaceMeshPoint> {
        val contourPoints: MutableList<FaceMeshPoint> = ArrayList()
        for (type in DISPLAY_CONTOURS) {
            contourPoints.addAll(faceMesh.getPoints(type))
        }
        return contourPoints
    }

    companion object {
        private const val USE_CASE_CONTOUR_ONLY = 999
        private const val FACE_POSITION_RADIUS = 8.0f
        private const val BOX_STROKE_WIDTH = 5.0f
    }

    init {
        val selectedColor = Color.WHITE
        positionPaint = Paint()
        positionPaint.color = selectedColor

        boxPaint = Paint()
        boxPaint.color = selectedColor
        boxPaint.style = Paint.Style.STROKE
        boxPaint.strokeWidth = BOX_STROKE_WIDTH

        useCase = USE_CASE_CONTOUR_ONLY
        zMin = java.lang.Float.MAX_VALUE
        zMax = java.lang.Float.MAX_VALUE

    }
}
