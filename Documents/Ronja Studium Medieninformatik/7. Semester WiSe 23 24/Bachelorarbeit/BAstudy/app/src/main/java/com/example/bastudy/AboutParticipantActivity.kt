package com.example.bastudy

import android.content.Intent
import android.os.Build
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat

class AboutParticipantActivity: AppCompatActivity() {

    private lateinit var bt_back: Button
    private lateinit var text_subjectID: TextView
    private lateinit var text_age: TextView
    private lateinit var text_gender: TextView
    private lateinit var text_groupUUID: TextView
    private lateinit var text_androidversion: TextView
    private lateinit var text_studystart: TextView
    private lateinit var text_studyend: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_participant)

        bt_back = findViewById(R.id.bt_back_participant)
        bt_back.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        text_subjectID = findViewById(R.id.text_subjectID)
        text_subjectID.text = AppPreferences.getSubjectID(this)
        text_age = findViewById(R.id.text_age)
        text_age.text = AppPreferences.getSubjectAge(this).toString()
        text_gender = findViewById(R.id.text_gender)
        text_gender.text = AppPreferences.getSubjectGender(this)
        text_groupUUID = findViewById(R.id.text_groupuuid)
        text_groupUUID.text = AppPreferences.getStudyUUID(this)
        text_androidversion = findViewById(R.id.text_version)
        text_androidversion.text = getAndroidVersion()
        text_studystart = findViewById(R.id.text_start)
        text_studystart.text = AppPreferences.getStartTimeOfStudy(this)
        text_studyend = findViewById(R.id.text_end)
        val sdf = SimpleDateFormat("dd/MM/yyyy")
        text_studyend.text = sdf.format(AppPreferences.getEndDate(this))


    }

    fun getAndroidVersion(): String {
        val versionCode = Build.VERSION.SDK_INT
        val versionName = when (versionCode) {
            VERSION_CODES.BASE -> "1.0"
            VERSION_CODES.BASE_1_1 -> "1.1"
            VERSION_CODES.CUPCAKE -> "1.5"
            VERSION_CODES.DONUT -> "1.6"
            VERSION_CODES.ECLAIR -> "2.0"
            VERSION_CODES.ECLAIR_0_1 -> "2.0.1"
            VERSION_CODES.ECLAIR_MR1 -> "2.1"
            VERSION_CODES.FROYO -> "2.2"
            VERSION_CODES.GINGERBREAD -> "2.3"
            VERSION_CODES.GINGERBREAD_MR1 -> "2.3.3"
            VERSION_CODES.HONEYCOMB -> "3.0"
            VERSION_CODES.HONEYCOMB_MR1 -> "3.1"
            VERSION_CODES.HONEYCOMB_MR2 -> "3.2"
            VERSION_CODES.ICE_CREAM_SANDWICH -> "4.0"
            VERSION_CODES.ICE_CREAM_SANDWICH_MR1 -> "4.0.3"
            VERSION_CODES.JELLY_BEAN -> "4.1"
            VERSION_CODES.JELLY_BEAN_MR1 -> "4.2"
            VERSION_CODES.JELLY_BEAN_MR2 -> "4.3"
            VERSION_CODES.KITKAT -> "4.4"
            VERSION_CODES.KITKAT_WATCH -> "4.4W"
            VERSION_CODES.LOLLIPOP -> "5.0"
            VERSION_CODES.LOLLIPOP_MR1 -> "5.1"
            VERSION_CODES.M -> "6.0"
            VERSION_CODES.N -> "7.0"
            VERSION_CODES.N_MR1 -> "7.1.1"
            VERSION_CODES.O -> "8.0"
            VERSION_CODES.O_MR1 -> "8.1"
            VERSION_CODES.P -> "9.0"
            VERSION_CODES.Q -> "10.0"
            VERSION_CODES.R -> "11.0"
            VERSION_CODES.S -> "12.0"
            VERSION_CODES.TIRAMISU -> "13.0"
            VERSION_CODES.UPSIDE_DOWN_CAKE -> "14.0"
            else -> "Unknown"
        }

        return "Android $versionName (API $versionCode)"
    }
}