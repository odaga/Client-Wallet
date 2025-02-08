package com.mcash.client.presentation.ui.kyc.faceProcessors

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class FaceCameraViewModel @Inject constructor(
) : ViewModel() {


    private val _numFacesDetected = MutableLiveData<Int>()
    val numFacesDetected: LiveData<Int>
        get() = _numFacesDetected

    private val _orientation = MutableLiveData<FaceOrientation>()
    val orientation: LiveData<FaceOrientation>
        get() = _orientation

    private val _faceCanvasAreaRatio = MutableLiveData<Float?>()
    val faceCanvasAreaRatio: MutableLiveData<Float?>
        get() = _faceCanvasAreaRatio


    private val _screenOrientationPortrait = MutableLiveData<Boolean>()
    val screenOrientationPortrait: MutableLiveData<Boolean>
        get() = _screenOrientationPortrait


    /**
     *  combined livedata to be used to observed for face camera checks
     * */
    private val _combinedFaceCameraCheckLiveData = MediatorLiveData<Triple<Int, Float, Boolean>>()
    val combinedFaceCameraCheckLiveData: LiveData<Triple<Int, Float, Boolean>>
        get() = _combinedFaceCameraCheckLiveData

    init {

        _combinedFaceCameraCheckLiveData.addSource(_numFacesDetected) { numFacesDetected ->
            val faceCanvasAreaRatio = _faceCanvasAreaRatio.value
            val screenOrientationPortrait = _screenOrientationPortrait.value
            if (faceCanvasAreaRatio != null && screenOrientationPortrait != null) {
                _combinedFaceCameraCheckLiveData.postValue(
                    Triple(
                        numFacesDetected,
                        faceCanvasAreaRatio,
                        screenOrientationPortrait
                    )
                )
            }
        }

        _combinedFaceCameraCheckLiveData.addSource(_faceCanvasAreaRatio) { faceCanvasAreaRatio ->
            val facesDetected = _numFacesDetected.value
            val screenOrientationPortrait = _screenOrientationPortrait.value
            if (facesDetected != null && screenOrientationPortrait != null) {
                _combinedFaceCameraCheckLiveData.postValue(
                    Triple(
                        facesDetected,
                        faceCanvasAreaRatio,
                        screenOrientationPortrait
                    )!! as Triple<Int, Float, Boolean>?
                )
            }
        }

        _combinedFaceCameraCheckLiveData.addSource(_screenOrientationPortrait) { screenOrientationPortrait ->
            val facesDetected = _numFacesDetected.value
            val faceCanvasAreaRatio = _faceCanvasAreaRatio.value
            if (facesDetected != null && faceCanvasAreaRatio != null) {
                _combinedFaceCameraCheckLiveData.postValue(
                    Triple(
                        facesDetected,
                        faceCanvasAreaRatio,
                        screenOrientationPortrait
                    )
                )
            }
        }


    }


    fun updateNumberOfDetectedFaces(numberOfFaces: Int) {
        _numFacesDetected.postValue(numberOfFaces)
    }

    fun updateFaceOrientation(eulerX: Float, eulerY: Float, eulerZ: Float) {
        if (_numFacesDetected.value == 1)
            _orientation.postValue(FaceOrientation(eulerX, eulerY, eulerZ))
        return
    }


    fun updateRatio(canvasArea: Float, boxArea: Float) {
        var ratio = (boxArea / canvasArea)
        runBlocking {
            if (_numFacesDetected.value == 1) {
                delay(200)
                _faceCanvasAreaRatio.postValue(ratio)
            } else {
                delay(200)
                _faceCanvasAreaRatio.postValue(2f)
            }
        }
    }

    fun updateScreenOrientation(config: Boolean) {
        _screenOrientationPortrait.postValue(config)
    }


    data class FaceOrientation(
        var eulerX: Float,
        var eulerY: Float,
        var eulerZ: Float
    )

}