package com.example.bastudy

import android.accessibilityservice.AccessibilityService
import android.content.ContentValues.TAG
import android.util.Log
import android.view.accessibility.AccessibilityEvent

/**
 * AccessibilityService() checks if and which app will be open while social-interaction-status is true
 * service started in MainActivity() -> startBeaconScanService()
 * needs to request user to enable accessibility of the system for this app
 *
 * if an app was opened, service adds them to a MutableLiveData-List in object OpenedAppManager()
 * list will be send to firebase if screen was locked in ScreenUnlockReiver...()
 *
 * only saves "neccessary" apps -> filters launcher, systemui, settings, etc. as unneccessary
 */
class MyAccessibilityService : AccessibilityService() {

    private val TAG_ACCESSIBILITY = "AccessibilityService_OpenedApp"
    override fun onInterrupt() {
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // checks only data while being social
        if(SocialContactManager.socialInteraction.value == true){
            if (event?.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
                // This event is triggered when the window state changes, meaning an app was opened or closed
                val packageName = event.packageName?.toString()

                // only getting app-open-data if app is neccassary
                if(isPackageAppOrSystem(packageName.toString())){
                    OpenedAppsManager.addApp(packageName.toString())
                    Log.d(TAG_ACCESSIBILITY,"${packageName}")
                }
            }
        }
    }

    fun isPackageAppOrSystem(packageName: String): Boolean{
        when {
                packageName.contains("launcher") -> return false
                packageName.contains("systemui") ->  return false
                packageName.contains("cocktailbarservice") -> return false
                packageName.contains("settings") -> return false
                packageName.contains("bastudy") -> return false
        }
        return true
    }

}
