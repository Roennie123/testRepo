package com.example.bastudy

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class RegistrationActivity : AppCompatActivity() {

    private lateinit var nextButton: Button
    private lateinit var femaleRadioButton: RadioButton
    private lateinit var maleRadioButton: RadioButton
    private lateinit var diversRadioButton: RadioButton
    private lateinit var subjectIdInput: EditText
    private lateinit var subjectAgeInput: EditText
    private lateinit var genderRadioGroup: RadioGroup

    // init firebase firestore for sending data to cloud
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private var subjectAttributes = hashMapOf<String, String>()
    private val TAG_FIREBASE_REGISTER = "RegistrationActivity_Firebase"
    private val KEY_ID = "subjectID"
    private val KEY_AGE = "age"
    private val KEY_GENDER = "gender"
    private val KEY_GROUP = "groupUUID"
    private val KEY_VERSION = "androidVersion"

    /**
     * StudyRegistrationActivity() prompts user for
     * * study ID (anonymous participation)
     * * age
     * * gender (f/m/d)
     *
     * user has to answer prompts,
     * then these will be send to server or something else (?)
     * if done that, by pressing "startstudy"-button
     * Activity will be closed and it goes back to "MainActivity()",
     * else a dialog with instruction to answer prompts will be shown.
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        femaleRadioButton = findViewById(R.id.rb_female)
        maleRadioButton = findViewById(R.id.rb_male)
        diversRadioButton = findViewById(R.id.rb_divers)
        subjectAgeInput = findViewById(R.id.edit_age)
        subjectIdInput = findViewById(R.id.edit_uuid)
        nextButton = findViewById(R.id.bt_next_init)
        genderRadioGroup = findViewById(R.id.group_gender)

        nextButton.setOnClickListener {
            var subjectID = subjectIdInput.getText().toString()
            var subjectAge = subjectAgeInput.getText().toString()
            var subjectGender: Gender =
                (if(femaleRadioButton.isChecked){
                Gender.FEMALE
                } else if(maleRadioButton.isChecked){
                Gender.MALE
                } else{
                Gender.DIVERS
                })
            var groupUUID: String = AppPreferences.getStudyUUID(this)

            if(!subjectID.equals("") && !subjectAge.equals("") && (genderRadioGroup.getCheckedRadioButtonId() != -1)){

                subjectAttributes.put(KEY_ID, subjectID)
                subjectAttributes.put(KEY_GROUP, groupUUID)
                subjectAttributes.put(KEY_AGE, subjectAge)
                subjectAttributes.put(KEY_GENDER, subjectGender.toString())
                subjectAttributes.put(KEY_VERSION, getAndroidVersion())

                db.collection("subjectAttributes").document(subjectID).set(subjectAttributes)
                    .addOnSuccessListener { documentReference -> Log.d(TAG_FIREBASE_REGISTER, "01 success on sending subjectAttributes to firebase") }
                    .addOnFailureListener { e -> Log.w(TAG_FIREBASE_REGISTER, "00 error on sending subjectAttributes to firebase", e) }

                // start study
                AppPreferences.markAppAsOpen(this)
                // save subjectID in AppPreferences to use as collection name
                AppPreferences.setSubjectID(this, subjectID)
                AppPreferences.setSubjectAge(this, subjectAge.toInt())
                AppPreferences.setSubjectGender(this, subjectGender.toString())
                finish()

            } else{
                val dialogBuilder = AlertDialog.Builder(this, R.style.CustomDialogStyle)
                val dialogView = layoutInflater.inflate(R.layout.dialog_registration, null)
                dialogBuilder.setView(dialogView)
                val alertDialog = dialogBuilder.create()
                val buttonOK: Button = dialogView.findViewById(R.id.ok_button)
                buttonOK.setOnClickListener { alertDialog.dismiss() }
                alertDialog.show()
            }
        }
    }

    fun getAndroidVersion(): String {
        val versionCode = Build.VERSION.SDK_INT
        val versionName = when (versionCode) {
            Build.VERSION_CODES.BASE -> "1.0"
            Build.VERSION_CODES.BASE_1_1 -> "1.1"
            Build.VERSION_CODES.CUPCAKE -> "1.5"
            Build.VERSION_CODES.DONUT -> "1.6"
            Build.VERSION_CODES.ECLAIR -> "2.0"
            Build.VERSION_CODES.ECLAIR_0_1 -> "2.0.1"
            Build.VERSION_CODES.ECLAIR_MR1 -> "2.1"
            Build.VERSION_CODES.FROYO -> "2.2"
            Build.VERSION_CODES.GINGERBREAD -> "2.3"
            Build.VERSION_CODES.GINGERBREAD_MR1 -> "2.3.3"
            Build.VERSION_CODES.HONEYCOMB -> "3.0"
            Build.VERSION_CODES.HONEYCOMB_MR1 -> "3.1"
            Build.VERSION_CODES.HONEYCOMB_MR2 -> "3.2"
            Build.VERSION_CODES.ICE_CREAM_SANDWICH -> "4.0"
            Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1 -> "4.0.3"
            Build.VERSION_CODES.JELLY_BEAN -> "4.1"
            Build.VERSION_CODES.JELLY_BEAN_MR1 -> "4.2"
            Build.VERSION_CODES.JELLY_BEAN_MR2 -> "4.3"
            Build.VERSION_CODES.KITKAT -> "4.4"
            Build.VERSION_CODES.KITKAT_WATCH -> "4.4W"
            Build.VERSION_CODES.LOLLIPOP -> "5.0"
            Build.VERSION_CODES.LOLLIPOP_MR1 -> "5.1"
            Build.VERSION_CODES.M -> "6.0"
            Build.VERSION_CODES.N -> "7.0"
            Build.VERSION_CODES.N_MR1 -> "7.1.1"
            Build.VERSION_CODES.O -> "8.0"
            Build.VERSION_CODES.O_MR1 -> "8.1"
            Build.VERSION_CODES.P -> "9.0"
            Build.VERSION_CODES.Q -> "10.0"
            Build.VERSION_CODES.R -> "11.0"
            Build.VERSION_CODES.S -> "12.0"
            Build.VERSION_CODES.TIRAMISU -> "13.0"
            Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> "14.0"
            else -> "Unknown"
        }

        return "Android $versionName (API $versionCode)"
    }
}