package com.example.bastudy

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.text.TextUtils
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
 * * only works in duration of study -> after end of study no detecting of unlocks/locks
 *
 * registered in MainActivity() by startBeaconScanService() -> start study part A (without intervention)
 */
class ScreenUnlockReceiverStartModeA: BroadcastReceiver() {

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
    private val TAG_NOTIFICATION_STATUS = "ScreenUnlockReceiver_NotificationStatus"

    private var entryTime: Long = 0
    private var exitTime: Long = 0
    private var startedTimer: Boolean = false
    private var showedFinalQuestionaire: Boolean = false
    private var condition = "A"

    val surveylink: String = "https://surveys.informatik.uni-ulm.de/index.php/143871?lang=en"
    val finalSurveylink: String = "https://surveys.informatik.uni-ulm.de/index.php/116453?lang=en"

    override fun onReceive(context: Context, intent: Intent?) {

        if(AppPreferences.getShowQuestionnaire(context)){
            Log.d(TAG_NOTIFICATION_STATUS, "02 planned to show notification at ${AppPreferences.getNextQuestionnaireTime(context)}")
            if(Date().after(AppPreferences.getNextQuestionnaireTime(context))){
                NotificationCreater.showNotification(
                    context,
                    "Study Questionnaire",
                    "A questionnaire is waiting for you. Please answer it as soon as possible.",
                    "$surveylink" +
                            "&subjectID=${AppPreferences.getSubjectID(context)}" +
                            "&condition=$condition" +
                            "&interactionID=${AppPreferences.getLastInteractionID(context)}")
                AppPreferences.setShowQuestionnaire(context, false)
                Log.d(TAG_NOTIFICATION_STATUS, "01 success on sending notification for questionnaire")
            }
        }

        if(!showedFinalQuestionaire){
            if(Date().after(AppPreferences.getEndDate(context))){
                NotificationCreater.showNotification(
                    context,
                    "Study Questionnaire",
                    "A questionnaire is waiting for you. Please answer it as soon as possible.", finalSurveylink)
                showedFinalQuestionaire = true
            }
        }

        if(Date().before(AppPreferences.getEndDate(context)) || Date().equals(AppPreferences.getEndDate(context))){

            if((intent?.action == Intent.ACTION_USER_PRESENT)){
                Log.d(TAG_ACTION_RECEIVER, "01 unlocked phone")

                if((SocialContactManager.socialInteraction.value == true) && (InterventionManager.createdOverlay.value == false)){
                    Log.d(TAG_SOCIAL_STATUS, "true")

                    // shows intervention after 7 days
                    // switching to study mode B
                    if(Date().after(AppPreferences.getSwitchDate(context))){
                        intervention = Intervention(context)
                        InterventionManager.setOverlayCreatingStatus(true)
                        condition = "B"
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
                    OpenedAppsManager.clearList()
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