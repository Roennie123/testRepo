package com.example.bastudy

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.view.View
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class PermissionActivityM : AppCompatActivity() {

    private lateinit var gps_check_button: Button
    private lateinit var overlay_check_button: Button
    private lateinit var battery_check_button: Button
    private lateinit var accessibility_check_button: Button
    private lateinit var next_button: Button
    private lateinit var back_button: Button


    // request codes for permissions
    private val LOCATION_PERMISSION_REQUEST = 1002
    private val OVERLAY_PERMISSION_REQUEST_CODE = 1003

    private lateinit var powerManager: PowerManager

    /**
     * InitPermissionCheckActivity() shows Checkboxes with permissionrequest
     * the subject has to tick and allow to run the app.
     *
     * user has to tick all checkboxes and allow all permission
     * if done that, by pressing "next"-button
     * "StudyRegistrationActivity()" will be start,
     * else a dialog with instruction to tick checkboxes will be shown.
     */
    /**
     * permission needed for android version 6+ (M):
     * * ACCESS_FINE_LOCATION
     * * Settings.canOverDrawThings()
     * * PowerManager.isIgnoringBatteryOptimization()
     *
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permissions_m)

        // init UI
        gps_check_button = findViewById(R.id.bt_gps)
        overlay_check_button = findViewById(R.id.bt_overlay)
        battery_check_button = findViewById(R.id.bt_battery)
        accessibility_check_button = findViewById(R.id.bt_accessibility)
        next_button = findViewById(R.id.bt_next_permission)
        back_button = findViewById(R.id.bt_back_permission)

        // UI when not init process
        if(AppPreferences.openedBefore(this)){
            next_button.visibility = View.INVISIBLE
            back_button.visibility = View.VISIBLE
        }

        back_button.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        gps_check_button.setOnClickListener{
            checkLocationPermission()
            gps_check_button.visibility = View.INVISIBLE
            findViewById<View>(R.id.view_gps).alpha = 0.5f
        }

        overlay_check_button.setOnClickListener {
            checkOverlayPermission()
            overlay_check_button.visibility = View.INVISIBLE
            findViewById<View>(R.id.view_overlay).alpha = 0.5f
        }

        powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        battery_check_button.setOnClickListener {
            checkBatteryPermission()
            battery_check_button.visibility = View.INVISIBLE
            findViewById<View>(R.id.view_battery).alpha = 0.5f
        }

        accessibility_check_button.setOnClickListener {
            checkAccessibilityPermission()
            accessibility_check_button.visibility = View.INVISIBLE
            findViewById<View>(R.id.view_accessibility).alpha = 0.5f
        }


        next_button.setOnClickListener{
            if(checkPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)){
                gps_check_button.visibility = View.INVISIBLE
                findViewById<View>(R.id.view_gps).alpha = 0.5f
            } else{
                gps_check_button.visibility = View.VISIBLE
                findViewById<View>(R.id.view_gps).alpha = 1f
            }
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(this)){
                overlay_check_button.visibility = View.INVISIBLE
                findViewById<View>(R.id.view_overlay).alpha = 0.5f
            } else{
                overlay_check_button.visibility = View.VISIBLE
                findViewById<View>(R.id.view_overlay).alpha = 1f
            }
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && powerManager.isIgnoringBatteryOptimizations(packageName)){
                battery_check_button.visibility = View.INVISIBLE
                findViewById<View>(R.id.view_battery).alpha = 0.5f
            } else{
                battery_check_button.visibility = View.VISIBLE
                findViewById<View>(R.id.view_battery).alpha = 1f
            }

            if(checkPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) &&
                (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(this)) &&
                (powerManager.isIgnoringBatteryOptimizations(packageName))
            ){
                val intent = Intent(this, RegistrationActivity::class.java)
                startActivity(intent)
                finish()
            } else{
                val dialogBuilder = AlertDialog.Builder(this, R.style.CustomDialogStyle)
                val dialogView = layoutInflater.inflate(R.layout.dialog_permissions, null)
                dialogBuilder.setView(dialogView)
                val alertDialog = dialogBuilder.create()
                val buttonOK: Button = dialogView.findViewById(R.id.ok_button)
                buttonOK.setOnClickListener { alertDialog.dismiss() }
                alertDialog.show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == OVERLAY_PERMISSION_REQUEST_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(this)) {
                // Overlay permission granted
                // Handle your logic here
            } else {
                // Overlay permission not granted
                // Handle the situation where the user didn't grant permission
            }
        }
    }

    private fun checkPermission(permission: String) : Boolean{
        if(ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED){
            return false
        }
        return true
    }

    // permission check
    /**
     * permission check
     * if not granted calls requestPermissions()
     */
    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST)
        }
    }
    private fun checkOverlayPermission(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            intent.data = Uri.parse("package:$packageName")
            overlayPermissionLauncher.launch(intent)
        }
    }

    private fun checkBatteryPermission(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !powerManager.isIgnoringBatteryOptimizations(packageName)){
            val intent = Intent()
            intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
            intent.data = Uri.parse("package:$packageName")
            startActivity(intent)
        }
    }

    private fun checkAccessibilityPermission(){
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.BIND_ACCESSIBILITY_SERVICE) != PackageManager.PERMISSION_GRANTED){
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            this.startActivity(intent)
        }
    }

    private val overlayPermissionLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Check if the overlay permission is granted
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(this)) {
                // Overlay permission granted
                // Handle your logic here
            } else {
                // Overlay permission not granted
                // Handle the situation where the user didn't grant permission
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            LOCATION_PERMISSION_REQUEST -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Location permission granted
                    gps_check_button.visibility = View.INVISIBLE
                    findViewById<View>(R.id.view_gps).alpha = 0.5f
                } else {
                    // Location permission denied
                    // Handle this scenario (e.g., show a message or request again)
                }
            }

        }
    }
}