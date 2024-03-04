package com.example.bastudy

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class AboutActivity : AppCompatActivity() {

    private lateinit var bt_next: Button
    private lateinit var cb_permission: CheckBox
    private lateinit var cb_registration: CheckBox
    private lateinit var cb_startStudy: CheckBox
    private lateinit var cb_restart: CheckBox
    private lateinit var cb_serviceKill: CheckBox
    private lateinit var cb_studyStopp: CheckBox
    private lateinit var cb_extra: CheckBox
    private lateinit var bt_back: Button
    private lateinit var switch_language: Switch

    private lateinit var intent: Intent

    /**
     * StudyinformationActivity() shows infotext about the study
     * * incl.everything the subject should know
     * * maybe incl.video about study (?)
     *
     * user has to tick the checkbox for reading the information
     * if done that, by pressing "next"-button
     * "InitPermissionCheckActivity()" will be start,
     * else a dialog with instruction to tick checkbox will be shown.
     */

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        bt_next = findViewById(R.id.bt_next_about)
        cb_permission = findViewById(R.id.checkbox_aboutPermissions)
        cb_registration = findViewById(R.id.checkbox_aboutRegistration)
        cb_startStudy = findViewById(R.id.checkbox_aboutStudystart)
        cb_restart = findViewById(R.id.checkbox_aboutRestart)
        cb_serviceKill = findViewById(R.id.checkbox_aboutServicekill)
        cb_studyStopp = findViewById(R.id.checkbox_aboutStudystopp)
        cb_extra = findViewById(R.id.checkbox_extra)

        bt_back = findViewById(R.id.bt_back_about)
        bt_back.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        // UI when not init process
        if(AppPreferences.openedBefore(this)){
            bt_next.visibility = View.INVISIBLE
            bt_back.visibility = View.VISIBLE
            cb_permission.visibility = View.INVISIBLE
            cb_registration.visibility = View.INVISIBLE
            cb_startStudy.visibility = View.INVISIBLE
            cb_restart.visibility = View.INVISIBLE
            cb_serviceKill.visibility = View.INVISIBLE
            cb_studyStopp.visibility = View.INVISIBLE
            cb_extra.visibility = View.INVISIBLE
        }

        bt_next.setOnClickListener {
            if(cb_permission.isChecked && cb_registration.isChecked && cb_extra.isChecked &&
                cb_startStudy.isChecked && cb_restart.isChecked &&
                cb_serviceKill.isChecked && cb_studyStopp.isChecked){
                finish()
                if(Build.VERSION.SDK_INT < Build.VERSION_CODES.Q){
                    // android version 6 - 9 (M - P)
                    intent = Intent(this, PermissionActivityM::class.java)
                } else if(Build.VERSION.SDK_INT == Build.VERSION_CODES.Q){
                    // android version 10 (Q)
                    intent = Intent(this, PermissionActivityQ::class.java)
                } else if(Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU){
                    // android version 11 - 12 (R - S)
                    // TODO: check for android 11
                    intent = Intent(this, PermissionActivityS::class.java)
                } else{
                    // 13+ (Tiramisu)
                    // TODO: check for android 14
                    intent = Intent(this, PermissionActivityTiramisu::class.java)
                }
                startActivity(intent)
            } else{
                val dialogBuilder = AlertDialog.Builder(this, R.style.CustomDialogStyle)
                val dialogView = layoutInflater.inflate(R.layout.dialog_about, null)
                dialogBuilder.setView(dialogView)
                val alertDialog = dialogBuilder.create()
                val buttonOK: Button = dialogView.findViewById(R.id.ok_button)
                buttonOK.setOnClickListener { alertDialog.dismiss() }
                alertDialog.show()
            }
        }

        switch_language = findViewById(R.id.switch_language)
        switch_language.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                // Switch to german
                cb_extra.setText(R.string.checkbox_DEabout)
                cb_permission.setText(R.string.checkbox_DEabout)
                cb_registration.setText(R.string.checkbox_DEabout)
                cb_restart.setText(R.string.checkbox_DEabout)
                cb_startStudy.setText(R.string.checkbox_DEabout)
                cb_studyStopp.setText(R.string.checkbox_DEabout)
                cb_serviceKill.setText(R.string.checkbox_DEabout)

                findViewById<TextView>(R.id.subtitle_installationInstruction).setText(R.string.subtitle_DEinstallation)
                findViewById<TextView>(R.id.text_aboutPermissions).setText(R.string.text_DEpermissions)
                findViewById<TextView>(R.id.text_aboutRegistration).setText(R.string.text_DEregistration)
                findViewById<TextView>(R.id.text_aboutStudystart).setText(R.string.text_DEstudyStart)
                findViewById<TextView>(R.id.subtitle_aboutRestart).setText(R.string.subtitle_DErestart)
                findViewById<TextView>(R.id.text_aboutRestart).setText(R.string.text_DErestart)
                findViewById<TextView>(R.id.subtitle_aboutServicekill).setText(R.string.subtitle_DEservicekill)
                findViewById<TextView>(R.id.text_aboutServicekill).setText(R.string.text_DEservicekill)
                findViewById<TextView>(R.id.subtitle_aboutStudystopp).setText(R.string.subtitle_DEstudyStopp)
                findViewById<TextView>(R.id.text_aboutStudystopp).setText(R.string.text_DEstudyStopp)
                findViewById<TextView>(R.id.subtitle_extra).setText(R.string.subtitle_DEextra)
                findViewById<TextView>(R.id.text_extra).setText(R.string.text_DEextra)
                findViewById<TextView>(R.id.subtitle_aboutThanks).setText(R.string.subtitle_DEthankyou)

                findViewById<TextView>(R.id.text_german).setTextColor(R.color.greenblue)
                findViewById<TextView>(R.id.switch_language).setTextColor(R.color.black)
            } else {
                // Switch to english
                cb_extra.setText(R.string.checkbox_about)
                cb_permission.setText(R.string.checkbox_about)
                cb_registration.setText(R.string.checkbox_about)
                cb_restart.setText(R.string.checkbox_about)
                cb_startStudy.setText(R.string.checkbox_about)
                cb_studyStopp.setText(R.string.checkbox_about)
                cb_serviceKill.setText(R.string.checkbox_about)

                findViewById<TextView>(R.id.subtitle_installationInstruction).setText(R.string.subtitle_installation)
                findViewById<TextView>(R.id.text_aboutPermissions).setText(R.string.text_permissions)
                findViewById<TextView>(R.id.text_aboutRegistration).setText(R.string.text_registration)
                findViewById<TextView>(R.id.text_aboutStudystart).setText(R.string.text_studyStart)
                findViewById<TextView>(R.id.subtitle_aboutRestart).setText(R.string.subtitle_restart)
                findViewById<TextView>(R.id.text_aboutRestart).setText(R.string.text_restart)
                findViewById<TextView>(R.id.subtitle_aboutServicekill).setText(R.string.subtitle_servicekill)
                findViewById<TextView>(R.id.text_aboutServicekill).setText(R.string.text_servicekill)
                findViewById<TextView>(R.id.subtitle_aboutStudystopp).setText(R.string.subtitle_studyStopp)
                findViewById<TextView>(R.id.text_aboutStudystopp).setText(R.string.text_studyStopp)
                findViewById<TextView>(R.id.subtitle_extra).setText(R.string.subtitle_extra)
                findViewById<TextView>(R.id.text_extra).setText(R.string.text_extra)
                findViewById<TextView>(R.id.subtitle_aboutThanks).setText(R.string.subtitle_thankyou)

                findViewById<TextView>(R.id.text_german).setTextColor(R.color.black)
                findViewById<TextView>(R.id.switch_language).setTextColor(R.color.greenblue)
            }
        }

    }

}