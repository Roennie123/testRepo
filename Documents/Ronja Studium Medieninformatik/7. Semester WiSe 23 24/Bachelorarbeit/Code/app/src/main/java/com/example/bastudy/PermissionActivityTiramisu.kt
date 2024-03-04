package com.example.bastudy

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

private lateinit var bluetooth_check_button: Button
private lateinit var gps_check_button: Button
private lateinit var overlay_check_button: Button
private lateinit var battery_check_button: Button
private lateinit var notification_check_button: Button
private lateinit var accessibility_check_button: Button
private lateinit var next_button: Button
private lateinit var back_button: Button


// request codes for permissions
private val BLUETOOTH_PERMISSION_REQUEST = 1001
private val LOCATION_PERMISSION_REQUEST = 1002
private val OVERLAY_PERMISSION_REQUEST_CODE = 1003
private val BACKGROUND_LOCATION_PERMISSION_REQUEST = 1005
private val NOTIFICATION_PERMISSION_REQUEST = 1006

private lateinit var powerManager: PowerManager

/**
 * InitPermissionCheckActivity() shows Checkboxes with permissionrequest
 * the subject has to tick and allow to run the app.
 *
 * user has to tick all checkboxes and allow all permission
 * if done that, by pressing "next"-button
 * "StudyRegistrationActivity()" will be start,
 * else a dialog with instruction to tick checkboxes will be shown.
 *
 * permission needed for android version 13+ (Tiramisu)
 * * ACCESS_FINE_LOCATION
 * * ACCESS_COARSE_LOCATION
 * * Settings.canOverDrawThings()
 * * PowerManager.isIgnoringBatteryOptimization()
 * * ACCESS_BACKGROUND_LOCATION
 * * BLUETOOTH_SCAN
 * * BLUETOOTH_ADVERTISE
 * * BLUETOOTH_CONNECT
 * * POST_NOTIFICATIONS
 */

class PermissionActivityTiramisu: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permissions_tiramisu)

        // init UI
        bluetooth_check_button = findViewById(R.id.bt_bluetooth)
        gps_check_button = findViewById(R.id.bt_gps)
        overlay_check_button = findViewById(R.id.bt_overlay)
        battery_check_button = findViewById(R.id.bt_battery)
        notification_check_button = findViewById(R.id.bt_notificatons)
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

        bluetooth_check_button.setOnClickListener{
            checkBluetoothPermission()
            bluetooth_check_button.visibility = View.INVISIBLE
            findViewById<View>(R.id.view_bluetooth).alpha = 0.5f
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

        notification_check_button.setOnClickListener {
            checkNotificationPermission()
            notification_check_button.visibility = View.INVISIBLE
            findViewById<View>(R.id.view_notifications).alpha = 0.5f
        }

        accessibility_check_button.setOnClickListener {
            checkAccessibilityPermission()
            accessibility_check_button.visibility = View.INVISIBLE
            findViewById<View>(R.id.view_accessibility).alpha = 0.5f
        }


        next_button.setOnClickListener{
            if(checkPermissions(arrayOf(android.Manifest.permission.BLUETOOTH_SCAN, android.Manifest.permission.BLUETOOTH_ADVERTISE, android.Manifest.permission.BLUETOOTH_CONNECT))){
                bluetooth_check_button.visibility = View.INVISIBLE
                findViewById<View>(R.id.view_bluetooth).alpha = 0.5f
            } else{
                bluetooth_check_button.visibility = View.VISIBLE
                findViewById<View>(R.id.view_bluetooth).alpha = 1f
            }
            if(checkPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_BACKGROUND_LOCATION))){
                gps_check_button.visibility = View.INVISIBLE
                findViewById<View>(R.id.view_gps).alpha = 0.5f
            } else{
                gps_check_button.visibility = View.VISIBLE
                findViewById<View>(R.id.view_gps).alpha = 1f
            }
            if(Settings.canDrawOverlays(this)){
                overlay_check_button.visibility = View.INVISIBLE
                findViewById<View>(R.id.view_overlay).alpha = 0.5f
            } else{
                overlay_check_button.visibility = View.VISIBLE
                findViewById<View>(R.id.view_overlay).alpha = 1f
            }
            if(powerManager.isIgnoringBatteryOptimizations(packageName)){
                battery_check_button.visibility = View.INVISIBLE
                findViewById<View>(R.id.view_battery).alpha = 0.5f
            } else{
                battery_check_button.visibility = View.VISIBLE
                findViewById<View>(R.id.view_battery).alpha = 1f
            }
            if(checkPermission(android.Manifest.permission.POST_NOTIFICATIONS)){
                notification_check_button.visibility = View.INVISIBLE
                findViewById<View>(R.id.view_notifications).alpha = 0.5f
            } else{
                notification_check_button.visibility = View.VISIBLE
                findViewById<View>(R.id.view_notifications).alpha = 1f
            }

            if(checkPermissions(arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                    android.Manifest.permission.BLUETOOTH_SCAN,
                    android.Manifest.permission.BLUETOOTH_CONNECT,
                    android.Manifest.permission.BLUETOOTH_ADVERTISE,
                    android.Manifest.permission.POST_NOTIFICATIONS)) &&
                Settings.canDrawOverlays(this) &&
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
            if (Settings.canDrawOverlays(this)) {
                // Overlay permission granted
                // Handle your logic here
            } else {
                // Overlay permission not granted
                // Handle the situation where the user didn't grant permission
            }
        }
    }


    private fun checkPermissions(permissions: Array<String>) : Boolean{
        for(permission in permissions){
            if(ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED){
                return false
            }
        }
        return true
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
    private fun checkBluetoothPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.BLUETOOTH_SCAN, android.Manifest.permission.BLUETOOTH_ADVERTISE, android.Manifest.permission.BLUETOOTH_CONNECT), BLUETOOTH_PERMISSION_REQUEST)
        } else if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.BLUETOOTH_SCAN, android.Manifest.permission.BLUETOOTH_ADVERTISE, android.Manifest.permission.BLUETOOTH_CONNECT), BLUETOOTH_PERMISSION_REQUEST)
        } else if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_ADVERTISE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.BLUETOOTH_SCAN, android.Manifest.permission.BLUETOOTH_ADVERTISE, android.Manifest.permission.BLUETOOTH_CONNECT), BLUETOOTH_PERMISSION_REQUEST)
        }
    }

    private fun checkLocationPermission() {
        if((ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) ||
            (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)){
            locationPermissionRequest.launch(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION))
        }
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED){
            requestBackgroundPermission()
        }
    }

    private fun checkOverlayPermission(){
        if(!Settings.canDrawOverlays(this)) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            intent.data = Uri.parse("package:$packageName")
            overlayPermissionLauncher.launch(intent)
        }
    }

    private fun checkBatteryPermission(){
        if(!powerManager.isIgnoringBatteryOptimizations(packageName)){
            val intent = Intent()
            intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
            intent.data = Uri.parse("package:$packageName")
            startActivity(intent)
        }
    }

    private fun checkNotificationPermission(){
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), NOTIFICATION_PERMISSION_REQUEST)
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

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(android.Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                // Precise location access granted.
            }
            permissions.getOrDefault(android.Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                // Only approximate location access granted.
            } else -> {
            // No location access granted.
        }
        }
    }

    fun requestBackgroundPermission() {
        val dialogBuilder = AlertDialog.Builder(this, R.style.CustomDialogStyle)
        val dialogView = layoutInflater.inflate(R.layout.dialog_backgroundlocation, null)
        dialogBuilder.setView(dialogView)
        val alertDialog = dialogBuilder.create()
        val buttonOK: Button = dialogView.findViewById(R.id.ok_button)
        buttonOK.setOnClickListener {
            requestPermissions(arrayOf(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION), BACKGROUND_LOCATION_PERMISSION_REQUEST)
            alertDialog.dismiss() }
        alertDialog.show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            BLUETOOTH_PERMISSION_REQUEST -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Bluetooth permission granted
                    bluetooth_check_button.visibility = View.INVISIBLE
                    findViewById<View>(R.id.view_bluetooth).alpha = 0.5f
                } else {
                    // Bluetooth permission denied
                    // Handle this scenario (e.g., show a message or request again)
                }
            }
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
            BACKGROUND_LOCATION_PERMISSION_REQUEST ->{
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Location permission granted
                } else {
                    // Location permission denied
                    // Handle this scenario (e.g., show a message or request again)
                }
            }

        }
    }
}

