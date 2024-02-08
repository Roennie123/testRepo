package com.example.androidstudy

import androidx.lifecycle.MutableLiveData

/**
 * SocialContactManager holds the LiveData about the social interaction status
 * only changed by altbeacon rangingObserver
 * * beacon is nearby ( < x meters ) = true
 * * beacon is nearby ( > x meters ) = false -> to far
 * * no custom beacon = false
 *
 * * custom beacon := beacon with individual study intern uuid
 */
object SocialContactManager {

    val socialInteraction = MutableLiveData<Boolean>()

    fun updateSocialInteractionStatus(value: Boolean){
        socialInteraction.value = value
    }

}