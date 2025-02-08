package com.mcash.client.presentation.ui.kyc.faceProcessors

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import com.google.mlkit.vision.face.Face

class FaceDetectionGraphic(
    overlay: GraphicOverlay,
    private val face: Face,
    private val imageRect: Rect
) :
    GraphicOverlay.Graphic(overlay) {

    private val boxPaint: Paint
    private val facePositionPaint: Paint


    init {
        val selectedColor = Color.WHITE

        boxPaint = Paint()
        boxPaint.color = selectedColor
        boxPaint.style = Paint.Style.STROKE
        boxPaint.strokeWidth = BOX_STROKE_WIDTH

        facePositionPaint = Paint()
        facePositionPaint.color = selectedColor
        facePositionPaint.style = Paint.Style.STROKE
        facePositionPaint.strokeWidth = BOX_STROKE_WIDTH
    }

    companion object {
        private const val USE_CASE_CONTOUR_ONLY = 999
        private const val FACE_POSITION_RADIUS = 8.0f
        private const val BOX_STROKE_WIDTH = 5.0f
    }

    override fun draw(canvas: Canvas?) {

        val rect = calculateRect(
            imageRect.height().toFloat(),
            imageRect.width().toFloat(),
            face.boundingBox
        )
        canvas?.drawRect(rect, boxPaint)
    }


}