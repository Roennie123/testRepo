package com.example.bastudy

import android.content.Context
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.Date

class AppPreferences {

    companion object{

        private const val PREFS_NAME = "AppPreferences"
        private const val APP_OPENED_BEFORE = "appOpenedBefore"
        private const val STUDY_STARTED = "studyStarted"
        private const val STUDY_START_TIME = "studyStartTime"

        private const val STUDY_UUID = "studyUUID"
        private const val SUBJECT_ID = "subjectID"
        private const val SUBJECT_AGE = "subjectAge"
        private const val SUBJECT_GENDER = "subjectGender"
        private const val STUDY_DURATION = "studyDuration"
        private const val SWITCH_MODE = "switchStudyMode"

        private const val STUDY_ENDDATE = "studyEndDate"
        private const val STUDY_SWITCHDATE = "studyModeSwitchDate"

        private const val STUDY_START_MODE = "studyStartModeOfSubject"

        private const val SHOWED_QUESTIONAIRE_W01 = "showedQuestionaireLinkA"
        private const val SHOWED_QUESTIONAIRE_W02 = "showedQuestionaireLinkB"

        private const val LINK_QUESTIONAIRE_W01 = "questionaireOfW01"
        private const val LINK_QUESTIONAIRE_W02 = "questionaireOfW02"

        private const val SHOW_QUESTIONNAIRE = "showQuestionnaire"
        private const val NEXT_QUESTIONNAIRE_TIME = "timeToShowNextQuestionnaire"
        private const val LAST_INTERACTION_ID = "lastInteractionID"

        fun setLastInteractionID(context: Context, interactionID: String){
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val editor = prefs.edit()
            editor.putString(LAST_INTERACTION_ID, interactionID)
            editor.apply()        }

        fun getLastInteractionID(context: Context): String?{
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            return prefs.getString(LAST_INTERACTION_ID, "")
        }

        fun getShowQuestionnaire(context: Context): Boolean{
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            return prefs.getBoolean(SHOW_QUESTIONNAIRE, false)
        }

        fun setShowQuestionnaire(context: Context, bool: Boolean){
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val editor = prefs.edit()
            editor.putBoolean(SHOW_QUESTIONNAIRE, bool)
            editor.apply()
        }

        fun getNextQuestionnaireTime(context: Context): Date{
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val timeInMillis = prefs.getLong(NEXT_QUESTIONNAIRE_TIME, 0)
            return Date(timeInMillis)
        }

        fun setNextQuestionnaireTime(context: Context, nextQuestionnaireTime: Date){
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val editor = prefs.edit()

            // Convert Date to Long and save it
            val timeInMillis = nextQuestionnaireTime.time
            editor.putLong(NEXT_QUESTIONNAIRE_TIME, timeInMillis)

            // Apply changes
            editor.apply()
        }

        fun setLinkForQuestionaireW01(context: Context, link: String){
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val editor = prefs.edit()
            editor.putString(LINK_QUESTIONAIRE_W01, link)
            editor.apply()
        }

        fun setLinkForQuestionaireW02(context: Context, link: String){
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val editor = prefs.edit()
            editor.putString(LINK_QUESTIONAIRE_W02, link)
            editor.apply()
        }

        fun getLinkForQuestionaireW01(context: Context): String?{
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            return prefs.getString(LINK_QUESTIONAIRE_W01, "")
        }

        fun getLinkForQuestionaireW02(context: Context): String?{
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            return prefs.getString(LINK_QUESTIONAIRE_W02, "")
        }

        fun showedQuestionaire_W01(context: Context) : Boolean{
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            return prefs.getBoolean(SHOWED_QUESTIONAIRE_W01, false)
        }

        fun markQuestionaireAsShown_W01(context: Context){
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val editor = prefs.edit()
            editor.putBoolean(SHOWED_QUESTIONAIRE_W01, true)
            editor.apply()
        }

        fun showedQuestionaire_W02(context: Context) : Boolean{
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            return prefs.getBoolean(SHOWED_QUESTIONAIRE_W02, false)
        }

        fun markQuestionaireAsShown_W02(context: Context){
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val editor = prefs.edit()
            editor.putBoolean(SHOWED_QUESTIONAIRE_W02, true)
            editor.apply()
        }
        fun setEndDate(context: Context, endDate: String){
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val editor = prefs.edit()
            editor.putString(STUDY_ENDDATE, endDate)
            editor.apply()
        }

        fun getEndDate(context: Context): Date {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val sdf = SimpleDateFormat("dd/MM/yyyy")
            val endDate: Date = sdf.parse(prefs.getString(STUDY_ENDDATE, "defValue"))
            return endDate
        }

        fun setSwitchDate(context: Context, switchDate: String){
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val editor = prefs.edit()
            editor.putString(STUDY_SWITCHDATE, switchDate)
            editor.apply()
        }

        fun getSwitchDate(context: Context): Date{
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val sdf = SimpleDateFormat("dd/MM/yyyy")
            val switchDate: Date = sdf.parse(prefs.getString(STUDY_SWITCHDATE, "defValue"))
            return switchDate
        }

        fun getStudyDuration(context: Context): Int{
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            return prefs.getInt(STUDY_DURATION, 0)!!
        }

        fun setStudyDuration(context: Context, durationInDays: Int){
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val editor = prefs.edit()
            editor.putInt(STUDY_DURATION, durationInDays)
            editor.apply()
        }

        fun getDaysTillStudyModeSwitch(context: Context): Int{
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            return prefs.getInt(SWITCH_MODE, 0)!!        }

        fun setDaysTillStudyModeSwitch(context: Context, days: Int){
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val editor = prefs.edit()
            editor.putInt(SWITCH_MODE, days)
            editor.apply()
        }

        fun setStudyStartMode(context: Context, studyMode: String){
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val editor = prefs.edit()
            editor.putString(STUDY_START_MODE, studyMode)
            editor.apply()
        }

        fun getStudyStartMode(context: Context): String{
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            return prefs.getString(STUDY_START_MODE, "standard")!!
        }

        fun setSubjectAge(context: Context, subjectAge: Int){
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val editor = prefs.edit()
            editor.putInt(SUBJECT_AGE, subjectAge)
            editor.apply()
        }

        fun getSubjectAge(context: Context): Int{
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            return prefs.getInt(SUBJECT_AGE, 0)!!
        }

        fun setSubjectGender(context: Context, subjectGender: String){
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val editor = prefs.edit()
            editor.putString(SUBJECT_GENDER, subjectGender)
            editor.apply()
        }

        fun getSubjectGender(context: Context): String{
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            return prefs.getString(SUBJECT_GENDER, "standard")!!
        }

        fun setSubjectID(context: Context, subjectID: String){
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val editor = prefs.edit()
            editor.putString(SUBJECT_ID, subjectID)
            editor.apply()
        }

        fun getSubjectID(context: Context): String{
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            return prefs.getString(SUBJECT_ID, "standard")!!
        }

        fun setStudyUUID(context: Context, customUUID: String){
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val editor = prefs.edit()
            editor.putString(STUDY_UUID, customUUID)
            editor.apply()
        }

        fun getStudyUUID(context: Context): String {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            return prefs.getString(STUDY_UUID, "standard")!!
        }

        fun openedBefore(context: Context) : Boolean{
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            return prefs.getBoolean(APP_OPENED_BEFORE, false)
        }

        fun markAppAsOpen(context: Context){
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val editor = prefs.edit()
            editor.putBoolean(APP_OPENED_BEFORE, true)
            editor.apply()
        }

        fun startedStudy(context: Context) : Boolean{
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            return prefs.getBoolean(STUDY_STARTED, false)
        }

        fun markStudyAsStarted(context: Context){
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val editor = prefs.edit()
            editor.putBoolean(STUDY_STARTED, true)
            editor.apply()
        }

        fun getStartTimeOfStudy(context: Context): String{
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            return prefs.getString(STUDY_START_TIME, "standard")!!
        }

        fun setStartTimeOfStudy(context: Context, formattedDate: String){
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val editor = prefs.edit()
            editor.putString(STUDY_START_TIME, formattedDate)
            editor.apply()
        }

    }

}