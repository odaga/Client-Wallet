package com.mcash.client.presentation.ui.kyc.faceProcessors

import android.content.Context
import android.content.res.Configuration
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.camera.core.CameraSelector
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

open class GraphicOverlay(context: Context?, attrs: AttributeSet?) :
    View(context, attrs) {

    private val lock = Any()
    private val graphics: MutableList<Graphic> = ArrayList()
    var mScale: Float? = null
    var mOffsetX: Float? = null
    var mOffsetY: Float? = null
    var cameraSelector: Int = CameraSelector.LENS_FACING_FRONT

    // Matrix for transforming from image coordinates to overlay view coordinates.
    private val transformationMatrix = Matrix()

    private var imageWidth = 0
    private var imageHeight = 0

    // The factor of overlay View size to image size. Anything in the image coordinates need to be
    // scaled by this amount to fit with the area of overlay View.
    private var scaleFactor = 1.0f

    // The number of horizontal pixels needed to be cropped on each side to fit the image with the
    // area of overlay View after scaling.
    private var postScaleWidthOffset = 0f

    // The number of vertical pixels needed to be cropped on each side to fit the image with the
    // area of overlay View after scaling.
    private var postScaleHeightOffset = 0f
    private var isImageFlipped = false
    private var needUpdateTransformation = true

    abstract class Graphic(val overlay: GraphicOverlay) {

        abstract fun draw(canvas: Canvas?)

        fun calculateRect(height: Float, width: Float, boundingBoxT: Rect): RectF {

            // for land scape
            fun isLandScapeMode(): Boolean {
                return overlay.context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
            }

            fun whenLandScapeModeWidth(): Float {
                return when (isLandScapeMode()) {
                    true -> width
                    false -> height
                }
            }

            fun whenLandScapeModeHeight(): Float {
                return when (isLandScapeMode()) {
                    true -> height
                    false -> width
                }
            }

            val scaleX = overlay.width.toFloat() / whenLandScapeModeWidth()
            val scaleY = overlay.height.toFloat() / whenLandScapeModeHeight()
            val scale = scaleX.coerceAtLeast(scaleY)
            overlay.mScale = scale

            // Calculate offset (we need to center the overlay on the target)
            val offsetX = (overlay.width.toFloat() - ceil(whenLandScapeModeWidth() * scale)) / 1.0f
            val offsetY =
                (overlay.height.toFloat() - ceil(whenLandScapeModeHeight() * scale)) / 1.0f

            overlay.mOffsetX = offsetX
            overlay.mOffsetY = offsetY

            val mappedBox = RectF().apply {
                left = boundingBoxT.right * scale + offsetX
                top = boundingBoxT.top * scale + offsetY
                right = boundingBoxT.left * scale + offsetX
                bottom = boundingBoxT.bottom * scale + offsetY
            }

//             for front mode
            if (overlay.isFrontMode()) {
                val centerX = overlay.width.toFloat() / 2
                mappedBox.apply {
                    left = centerX + (centerX - left)
                    right = centerX - (right - centerX)
                }
            }
            return mappedBox
        }
    }

    fun isFrontMode() = cameraSelector == CameraSelector.LENS_FACING_FRONT

    fun toggleSelector() {
        cameraSelector =
            if (cameraSelector == CameraSelector.LENS_FACING_BACK) CameraSelector.LENS_FACING_FRONT
            else CameraSelector.LENS_FACING_BACK
    }

    fun clear() {
        synchronized(lock) { graphics.clear() }
        postInvalidate()
    }

    fun add(graphic: Graphic) {
        synchronized(lock) { graphics.add(graphic) }
    }

    fun remove(graphic: Graphic) {
        synchronized(lock) { graphics.remove(graphic) }
        postInvalidate()
    }


    /** Adjust the supplied value from the image scale to the view scale  */
    fun scale(imagePixel: Float): Float {
        return imagePixel * scaleFactor
    }

    /**
     *  Adjust the x coordinate from the image's coordinate system to the view coordinate system
     */
    fun translateX(x: Float): Float {
        return if (isImageFlipped)
            width - (scale(x) - postScaleWidthOffset)
        else
            scale(x) - postScaleWidthOffset
    }

    /**
     *  Adjust the y coordinate from the image's coordinate system to the view coordinate system
     */
    fun translateY(y: Float): Float {
        return scale(y) - postScaleHeightOffset
    }

    private fun getImageWidth(): Int {
        return imageWidth
    }

    private fun getImageHeight(): Int {
        return imageHeight
    }


    @Suppress("DIVISION_BY_ZERO")
    private fun updateTransformationIfNeeded() {
        if (!needUpdateTransformation || imageWidth <= 0 || imageHeight <= 0) {
            return
        }
        val viewAspectRatio = width.toFloat() / height
        val imageAspectRatio = imageWidth.toFloat().div(imageHeight)
        postScaleWidthOffset = 0f
        postScaleHeightOffset = 0f
        if (viewAspectRatio > imageAspectRatio) {
            // The image needs to be vertically cropped to be displayed in this view.
            scaleFactor = width.toFloat() / imageWidth
            postScaleHeightOffset = (width.toFloat() / imageAspectRatio - height) / 2
        } else {
            // The image needs to be horizontally cropped to be displayed in this view.
            scaleFactor = height.toFloat() / imageHeight
            postScaleWidthOffset = (height.toFloat() * imageAspectRatio - width) / 2
        }
        transformationMatrix.reset()
        transformationMatrix.setScale(scaleFactor, scaleFactor)
        transformationMatrix.postTranslate(-postScaleWidthOffset, -postScaleHeightOffset)
        if (isImageFlipped) {
            transformationMatrix.postScale(-1f, 1f, width / 2f, height / 2f)
        }
        needUpdateTransformation = false
    }


    /**
     *  Sets the source information of the image being processed bt detectors, including
     *  size and whether it is flipped, which informs how to transform image coordinates later
     */
    fun setImageSourceInfo(imageWidth: Int, imageHeight: Int, isFlipped: Boolean) {

        if ((imageWidth > 0) && imageHeight > 0) {
            synchronized(lock) {
                this.imageWidth = imageWidth
                this.imageHeight = imageHeight
                isImageFlipped = isFlipped
                needUpdateTransformation = true
            }
        } else {
            println(" Both image height and width must be positive values")
        }

        postInvalidate()
    }

    fun updatePaintColorByZValue(
        paint: Paint,
        canvas: Canvas,
        visualizeZ: Boolean,
        rescaleZForVisualization: Boolean,
        zInImagePixel: Float,
        zMin: Float,
        zMax: Float
    ) {
        if (!visualizeZ)
            return

        // When visualizeZ is true, sets up paint to different color based on z values.
        // Gets the range of z value.
        var zLowerBoundInScreenPixel: Float
        var zUpperBoundInScreenPixel: Float

        if (rescaleZForVisualization) {
            zLowerBoundInScreenPixel = min(-0.001f, scale(zMin))
            zUpperBoundInScreenPixel = max(0.001f, scale(zMax))
        } else {
            // By default, assume the range of z value pixel is [ -canvasWidth, canvasWidth ]
            var defaultRangeFactor = 1f
            zLowerBoundInScreenPixel = -defaultRangeFactor * canvas.width
            zUpperBoundInScreenPixel = defaultRangeFactor * canvas.width
        }

        var zInScreenPixel = scale(zInImagePixel)

        if (zInScreenPixel < 0) {
            // Sets up the paint to be red if the item is in front of the z origin.
            // Maps values within [zLowerBoundInScreenPixel, 0) to [255, 0) and use it to control the
            // color. The larger the value is, the more red it will be.
            var v = (zInScreenPixel / zLowerBoundInScreenPixel * 255).toInt()
            paint.setARGB(255, 255, 255 - v, 255 - v)
        } else {
            // Sets up the paint to be blue if the item is behind the z origin.
            // Maps values within [0, zUpperBoundInScreenPixel] to [0, 255] and use it to control the
            // color. The larger the value is, the more blue it will be.
            var v = (zInScreenPixel / zUpperBoundInScreenPixel * 255).toInt()
//            v = Ints.constrainToRange(v, 0, 255)
            v = v.coerceIn(0, 255)
            paint.setARGB(255, 255 - v, 255 - v, 255)
        }

    }


    /** Draws the overlay with it's associated graphic objects **/
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        synchronized(lock) {
            for (graphic in graphics) {
                graphic.draw(canvas)
            }
        }
    }


}