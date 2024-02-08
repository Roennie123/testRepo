package com.example.androidstudy

import androidx.lifecycle.MutableLiveData

/**
 * InterventionManager holds values for Interventioncreating and -closing
 * * to avoid crash by remove non-existing overlays
 */
object InterventionManager {

    val closedOverlay = MutableLiveData<Boolean>().apply { value = false }
    val createdOverlay = MutableLiveData<Boolean>().apply { value = false }

    fun setOverlayClosingStatus(value: Boolean){
        closedOverlay.value = value
    }

    fun setOverlayCreatingStatus(value: Boolean){
        createdOverlay.value = value
    }
}