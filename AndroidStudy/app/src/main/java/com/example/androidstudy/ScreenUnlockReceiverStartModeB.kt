package com.example.androidstudy

import android.content.BroadcastReceiver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date

/**
 * TODO
 * ScreenUnlockReceiverA is a BroadcastReceiver which checks if phone is unlocked
 * * if unlocked checks socialInteractionStatus from SocialContactManager
 * * sends start, end and duration of phoneuse while social to firebase
 *
 * registered in MainActivity() by startBeaconScanService() -> start study part B (with intervention)
 */
class ScreenUnlockReceiverStartModeB : BroadcastReceiver(){

    // init firebase firestore for sending data to cloud
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private lateinit var intervention: Intervention

    /**
     * TAGs for Logging
     * shortcuts for Logging:
     * * 00 error/fail/exit
     * * 01 success/entry
     * * 02 temp
     */
    private val TAG_FIREBASE_RECEIVER = "ScreenUnlockReceiver_Firebase"
    private val TAG_ACTION_RECEIVER = "ScreenUnlockReceiver_UserAction"
    private val TAG_SOCIAL_STATUS = "ScreenUnlockReceiver_SocialStatus"

    private var entryTime: Long = 0
    private var exitTime: Long = 0
    private var startedTimer: Boolean = false
    private var showedFirstQuestionnaire: Boolean = false
    private var showedFinalQuestionaire: Boolean = false

    override fun onReceive(context: Context, intent: Intent?) {

        if(!showedFinalQuestionaire){
            if(Date().after(AppPreferences.getEndDate(context))){
                NotificationCreater.showNotification(
                    context,
                    "Study Questionnaire",
                    "A questionnaire is waiting for you. Please answer it as soon as possible.")
                showedFinalQuestionaire = true
            }
        }

        if(Date().before(AppPreferences.getEndDate(context)) || Date().equals(AppPreferences.getEndDate(context))){

            if((intent?.action == Intent.ACTION_USER_PRESENT)){
                Log.d(TAG_ACTION_RECEIVER, "01 unlocked phone")

                if(!showedFirstQuestionnaire){
                    if(Date().after(AppPreferences.getSwitchDate(context))){
                        // send notification with questionnaire
                        NotificationCreater.showNotification(
                            context,
                            "Study Questionnaire",
                            "A questionnaire is waiting for you. Please answer it as soon as possible.")
                        showedFirstQuestionnaire = true
                    }
                }

                if((SocialContactManager.socialInteraction.value == true) && (InterventionManager.createdOverlay.value == false)){
                    Log.d(ContentValues.TAG, "social Interaction")

                    // shows intervention for 7 days
                    // then switching to study mode A without interventions
                    if(Date().before(AppPreferences.getSwitchDate(context)) || Date().equals(AppPreferences.getSwitchDate(context))){
                        intervention = Intervention(context)
                        InterventionManager.setOverlayCreatingStatus(true)
                    }

                    entryTime = System.currentTimeMillis()
                    startedTimer = true

                }
            }

            if(intent?.action == Intent.ACTION_SCREEN_OFF){
                Log.d(TAG_ACTION_RECEIVER, "00 locked phone")

                if(startedTimer){
                    exitTime = System.currentTimeMillis()
                    var phubbDuration = hashMapOf<String, String>()
                    phubbDuration.put("subjectID", AppPreferences.getSubjectID(context))
                    if(InterventionManager.createdOverlay.value == true){
                        phubbDuration.put("showedIntervention", "true")
                    } else{
                        phubbDuration.put("showedIntervention", "false")
                    }
                    phubbDuration.put("date", SimpleDateFormat("dd/MM/yyyy").format(Date()))
                    phubbDuration.put("endOfPhoneUse", android.text.format.DateFormat.format("HH:mm:ss", exitTime).toString())
                    phubbDuration.put("startOfPhoneUse", android.text.format.DateFormat.format("HH:mm:ss", entryTime).toString())
                    phubbDuration.put("appsUsed", OpenedAppsManager.getList().distinct().toString())
                    db
                        .collection("phoneUse")
                        .document("${AppPreferences.getSubjectID(context)} ${System.currentTimeMillis()}").set(phubbDuration)
                        .addOnSuccessListener { documentReference -> Log.d(TAG_FIREBASE_RECEIVER, "01 success on sending phoneUse to firebase") }
                        .addOnFailureListener { e -> Log.w(TAG_FIREBASE_RECEIVER, "00 error on sending phoneUse to firebase", e) }

                    startedTimer = false
                    exitTime = 0
                    entryTime = 0
                }

                if(InterventionManager.createdOverlay.value == true){
                    if(InterventionManager.closedOverlay.value == false){
                        // close screen with overlay
                        intervention.hideOverlay()
                    }
                    // else: pressed "yes"-button and closed screen after using
                    // createdOverlay.value and closedOverlay.value are getting resets in hideOverlay()
                }
            }

        }

    }

}