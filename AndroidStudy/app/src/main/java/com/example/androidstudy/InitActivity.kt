package com.example.androidstudy

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class InitActivity: AppCompatActivity() {

    private lateinit var nextButton: Button
    private lateinit var groupUUIDInput: EditText
    private lateinit var studyDurationInput: EditText
    private lateinit var switchModeInput: EditText
    private lateinit var studyModeRadioGroup: RadioGroup
    private lateinit var studyModeA_RadioButton: RadioButton
    private lateinit var studyModeB_RadioButton: RadioButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_init)

        nextButton = findViewById(R.id.bt_next_init)
        groupUUIDInput = findViewById(R.id.edit_uuid)
        studyDurationInput = findViewById(R.id.edit_studyDuration)
        switchModeInput = findViewById(R.id.edit_studyMode)
        studyModeRadioGroup = findViewById(R.id.group_studymode)
        studyModeA_RadioButton = findViewById(R.id.rb_A)
        studyModeB_RadioButton = findViewById(R.id.rb_B)

        nextButton.setOnClickListener {
            val groupUUID = groupUUIDInput.text.toString()
            val studyDuration = studyDurationInput.text.toString().toInt()
            val switchStudyMode = switchModeInput.text.toString().toInt()
            val studyMode: String =
                (if(studyModeA_RadioButton.isChecked){
                    "A"
                } else if(studyModeB_RadioButton.isChecked){
                    "B"
                } else{
                    ""
                }).toString()

            if((studyDuration != null) && (switchStudyMode != null) && (groupUUID.length == 4) && (studyModeRadioGroup.getCheckedRadioButtonId() != -1)){
                AppPreferences.setStudyDuration(this, studyDuration)
                AppPreferences.setDaysTillStudyModeSwitch(this, switchStudyMode)
                AppPreferences.setStudyStartMode(this, studyMode)
                if(studyMode.equals("A")){
                    // W01: A, W02: B
                    AppPreferences.setLinkForQuestionaireW01(this, InternAttributes().questionaireLink_StudyModeA)
                    AppPreferences.setLinkForQuestionaireW02(this, InternAttributes().questionaireLink_StudyModeB)
                } else{
                    // W01: B, W02: A
                    AppPreferences.setLinkForQuestionaireW01(this, InternAttributes().questionaireLink_StudyModeB)
                    AppPreferences.setLinkForQuestionaireW02(this, InternAttributes().questionaireLink_StudyModeA)
                }

                // initialization of uuid
                createCustomUUID(groupUUID)

                finish()
                val intent = Intent(this, AboutActivity::class.java)
                startActivity(intent)
            } else{
                val dialogBuilder = AlertDialog.Builder(this, R.style.CustomDialogStyle)
                val dialogView = layoutInflater.inflate(R.layout.dialog_init, null)
                dialogBuilder.setView(dialogView)
                val alertDialog = dialogBuilder.create()
                val buttonOK: Button = dialogView.findViewById(R.id.ok_button)
                buttonOK.setOnClickListener { alertDialog.dismiss() }
                alertDialog.show()
            }

        }

    }

    private fun createCustomUUID(inputSnippet: String){
        val finalGroupUUID: String = InternAttributes().tempUUID + inputSnippet
        AppPreferences.setStudyUUID(this, finalGroupUUID)
        Log.d(ContentValues.TAG, finalGroupUUID.toString())
    }

}