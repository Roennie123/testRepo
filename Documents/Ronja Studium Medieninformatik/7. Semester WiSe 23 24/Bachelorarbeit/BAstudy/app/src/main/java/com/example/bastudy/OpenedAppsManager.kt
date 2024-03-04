package com.example.bastudy

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.MutableLiveData

object OpenedAppsManager {

    val openedAppsWhileSocialInteraction = MutableLiveData<List<String>>()
    private val TAG_ACCESSIBILITY = "AccessibilityService_OpenedApp"


    fun addApp(app: String){
        val currentList = openedAppsWhileSocialInteraction.value ?: emptyList()
        val updatedList = currentList + app
        openedAppsWhileSocialInteraction.value = updatedList
        Log.d(TAG_ACCESSIBILITY, "added $app")
    }

    fun getList(): List<String>{
        val currentList = openedAppsWhileSocialInteraction.value ?: emptyList()
        return currentList
    }

    fun clearList(){
        openedAppsWhileSocialInteraction.value = null
    }
}