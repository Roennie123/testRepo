package com.example.androidstudy

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.google.firebase.firestore.FirebaseFirestore
import org.altbeacon.beacon.Beacon
import org.altbeacon.beacon.BeaconManager
import org.altbeacon.beacon.BeaconParser
import org.altbeacon.beacon.BeaconTransmitter
import org.altbeacon.beacon.Identifier
import org.altbeacon.beacon.MonitorNotifier
import org.altbeacon.beacon.RangeNotifier
import org.altbeacon.beacon.Region
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date


class MainActivity : AppCompatActivity() {

    // UI elements
    private lateinit var toolbar: Toolbar
    private lateinit var animationCircle_1: ImageView
    private lateinit var animationCircle_2: ImageView
    private lateinit var startStudy_button: Button
    private lateinit var showQuestionaireLink_button: Button

    private lateinit var testImg: ImageView

    // animation
    private var animationHandler: Handler = Handler()

    // altbeacon foreground service
    private lateinit var customUUID: String
    private lateinit var region: Region
    private lateinit var beaconManager: BeaconManager

    // for compensation of little bug in monitoring (takes the second "no beacons detected" for real)
    private var compensationHandler: Handler = Handler()

    private var compensationBool: Boolean = false

    //private var entryTime: Long = 0
    private lateinit var entryTime: String
    //private var exitTime: Long = 0
    private lateinit var exitTime: String
    private lateinit var entryDate: String
    private lateinit var exitDate: String

    // init firebase firestore for sending data to cloud
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()

    /**
     * TAGs for Logging
     * shortcuts for Logging:
     * * 00 error/fail/exit
     * * 01 success/entry
     * * 02 temp
     */
    private val TAG_MONITORING = "MainActivity_Monitoring"
    private val TAG_RANGING = "MainActivity_Ranging"
    private val TAG_FIREBASE_MAIN = "MainActivity_Firebase"
    private val TAG_BEACON_ADVERTISING = "MainActivity_BeaconAdvertising"
    private val TAG_BEACON_FOREGROUNGSERVICE = "MainActivity_BeaconForegroundService"


    var bluetoothReceiver: BluetoothReceiver = BluetoothReceiver()
    val filter = IntentFilter().apply {
        addAction(Intent.ACTION_USER_PRESENT)
        addAction(Intent.ACTION_SCREEN_OFF)
    }

    // required setup
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var bluetoothManager: BluetoothManager
    private lateinit var locationManager: LocationManager

    // TODO: prototype studytext etc.

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /**
         * when first init
         * * starts StudyInformationActivity()
         *      * starts InitPermissionCheckActivity()
         *          * starts StudyRegistrationActivity()
         *
         * * marks app as opened if subject data saved in StudyRegistrationActivity()
         * * button to start study -> start ScanService and BeaconAdvertService
         * * marks study as started if Services started
         */
        if(!AppPreferences.openedBefore(this)){
            // never opened before
            val intent = Intent(this, InitActivity::class.java)
            startActivity(intent)
        }

        // start study
        if(!AppPreferences.startedStudy(this)){
            startStudy_button = findViewById(R.id.bt_startStudyMain)
            startStudy_button.visibility = View.VISIBLE
            startStudy_button.setOnClickListener {
                startBeaconScanService()
                // set study duration (start date, switch mode date, end date)
                setUpStudyStart()
                startPulse()
                AppPreferences.markStudyAsStarted(this)
                startStudy_button.visibility = View.INVISIBLE
            }
        }

        showQuestionaireLink_button = findViewById(R.id.bt_questionaire)

        // show questionaire link
        if(AppPreferences.startedStudy(this)){
            // survey for W02
            if(Date().after(AppPreferences.getEndDate(this))){
                if(!AppPreferences.showedQuestionaire_W02(this)){
                    showQuestionaireLink_button.visibility = View.VISIBLE
                    showQuestionaireLink_button.setOnClickListener {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(AppPreferences.getLinkForQuestionaireW02(this)))
                        startActivity(intent)
                        finish()
                        AppPreferences.markQuestionaireAsShown_W02(this)
                        showQuestionaireLink_button.visibility = View.INVISIBLE
                    }
                }
                // survey for W01
            } else if(Date().after(AppPreferences.getSwitchDate(this))){
                if(!AppPreferences.showedQuestionaire_W01(this)){
                    showQuestionaireLink_button.visibility = View.VISIBLE
                    showQuestionaireLink_button.setOnClickListener {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(AppPreferences.getLinkForQuestionaireW01(this)))
                        startActivity(intent)
                        finish()
                        AppPreferences.markQuestionaireAsShown_W01(this)
                        showQuestionaireLink_button.visibility = View.INVISIBLE
                    }
                }
            }
        }

        // init UI
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        animationCircle_1 = findViewById(R.id.img_animationcircle_1)
        animationCircle_2 = findViewById(R.id.img_animationcircle_2)

        testImg = findViewById(R.id.img_icon_on)

    }


    /**
     * if study starts start animation for 10 seconds
     */
    private fun startPulse(){
        runnable.run()
        animationHandler.postDelayed({
            stopPulse()
        }, 10_000)
    }

    private fun stopPulse(){
        animationHandler.removeCallbacks(runnable)
    }

    private var runnable = object : Runnable{
        override fun run() {
            animationCircle_1.animate().scaleX(4f).scaleY(4f).alpha(0f).setDuration(1200)
                .withEndAction{
                    animationCircle_1.scaleX = 1f
                    animationCircle_1.scaleY = 1f
                    animationCircle_1.alpha = 1f
                }
            animationCircle_2.animate().scaleX(4f).scaleY(4f).alpha(0f).setDuration(1000)
                .withEndAction{
                    animationCircle_2.scaleX = 1f
                    animationCircle_2.scaleY = 1f
                    animationCircle_2.alpha = 1f
                }
            animationHandler.postDelayed(this, 1500)
        }

    }

    /**
     * navigation bar
     * * restart scanservice
     * * TODO: informationtext about study
     * * permissions to check
     * * about study participant
     * * questionaires
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_burger -> {
                showPopupMenu(findViewById(R.id.menu_burger))
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun showPopupMenu(view: View) {
        val popup = PopupMenu(ContextThemeWrapper(this, R.style.PopupMenuStyle), view)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.burger_menu, popup.menu)

        popup.setOnMenuItemClickListener { menuItem ->
            if(menuItem.itemId == R.id.bt_about){
                finish()
                intent = Intent(this, AboutActivity::class.java)
                startActivity(intent)

            } else if(menuItem.itemId == R.id.bt_participant){
                finish()
                intent = Intent(this, AboutParticipantActivity::class.java)
                startActivity(intent)

            } else if(menuItem.itemId == R.id.bt_permissions){
                finish()
                if(Build.VERSION.SDK_INT < Build.VERSION_CODES.Q){
                    // 6+ (M)
                    intent = Intent(this, PermissionActivityM::class.java)
                } else if(Build.VERSION.SDK_INT < Build.VERSION_CODES.S){
                    // 10+ (Q)
                    intent = Intent(this, PermissionActivityQ::class.java)
                } else if(Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU){
                    // 12 (S)
                    intent = Intent(this, PermissionActivityS::class.java)
                } else{
                    // 13+ (Tiramisu)
                    intent = Intent(this, PermissionActivityTiramisu::class.java)
                }
                startActivity(intent)

            } else if(menuItem.itemId == R.id.bt_restart){
                // accessibility service permission gets killed if app was killed
                if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.BIND_ACCESSIBILITY_SERVICE) != PackageManager.PERMISSION_GRANTED){
                    val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                    this.startActivity(intent) }
                startBeaconScanService()
                startPulse()

            } else if(menuItem.itemId == R.id.bt_linkToQuestionaire){
                if(Date().after(AppPreferences.getEndDate(this))){
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(AppPreferences.getLinkForQuestionaireW02(this)))
                    startActivity(intent)
                    finish()
                } else if(Date().after(AppPreferences.getSwitchDate(this))){
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(AppPreferences.getLinkForQuestionaireW01(this)))
                    startActivity(intent)
                    finish()
                } else{
                    val dialogBuilder = AlertDialog.Builder(this, R.style.CustomDialogStyle)
                    val dialogView = layoutInflater.inflate(R.layout.dialog_questionaire, null)
                    dialogBuilder.setView(dialogView)
                    val alertDialog = dialogBuilder.create()
                    val buttonOK: Button = dialogView.findViewById(R.id.ok_button)
                    buttonOK.setOnClickListener { alertDialog.dismiss() }
                    alertDialog.show()
                }
            }
            true
        }

        popup.show()
    }

    /**
     * transmit custom altbeacon
     * * other devices with app are scanning for this specific beacon
     */
    private fun startBeaconTransmission(context: Context){
        customUUID = AppPreferences.getStudyUUID(context)
        val beacon = Beacon.Builder()
            .setId1(customUUID)
            .setId2("")
            .setId3("")
            .setManufacturer(0x0118)
            .setTxPower(-59)
            .setDataFields(listOf(0L))
            .build()
        val beaconParser = BeaconParser()
            .setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25")
        val beaconTransmitter = BeaconTransmitter(context, beaconParser)
        beaconTransmitter.startAdvertising(beacon)
        Log.d(TAG_BEACON_ADVERTISING, "01 beacon advertising successful")
        Log.d(TAG_BEACON_ADVERTISING, "customUUID: $beacon")
    }

    private fun startBeaconScanService(){

        customUUID = AppPreferences.getStudyUUID(this)
        region = Region("myBeaconRegion", Identifier.parse(customUUID), null, null)

        /**
         * registers receivers:
         * * screenUnlockReceiver -> inits interventions if lock/unlock phone while social interaction
         * * bluetoothReceiver -> starts altbeacon advertising if bluetooth enables
         */
        if(AppPreferences.getStudyStartMode(this).equals("A")){
            // start study mode A
            var screenUnlockReceiverStartModeA = ScreenUnlockReceiverStartModeA()
            registerReceiver(screenUnlockReceiverStartModeA, filter)
        } else{
            // start study mode B
            var screenUnlockReceiverStartModeB = ScreenUnlockReceiverStartModeB()
            registerReceiver(screenUnlockReceiverStartModeB, filter)
        }
        registerReceiver(bluetoothReceiver, IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED))

        /**
         * asks user to enable location and bluetooth
         */
        enableBluetooth()
        enableGPS()

        beaconManager = BeaconManager.getInstanceForApplication(this)

        /**
         * beaconManager.setBackground... defines how scanning for altbeacon if services goes to background
         * e.g. doze-mode or locked screen
         * BackgroundScanPeriod := duration of scanperiod
         * BackgroundBetweenScanPeriod := duration between scanperiods
         *
         * same for beaconManager.setForeground...
         *
         * scans every 30 seconds for 2 second for specific altbeacon
         */
        beaconManager.setBackgroundScanPeriod(1100)
        beaconManager.setBackgroundBetweenScanPeriod(10000)

        beaconManager.setForegroundScanPeriod(1100)
        beaconManager.setForegroundBetweenScanPeriod(10000)


        setUpForegroundService()

        beaconManager.startMonitoring(region)
        beaconManager.startRangingBeacons(region)

        val regionViewModel = beaconManager.getRegionViewModel(region)
        //regionViewModel.rangedBeacons.observeForever(rangingObserver)
        regionViewModel.regionState.observeForever(monitoringObserver)

        /**
         * accessibility service to check which app will be used while being social
         */
        val serviceIntent = Intent(this, MyAccessibilityService::class.java)
        this.startService(serviceIntent)

    }

    /**
     * setup beaconscan-foregroundservice
     * https://altbeacon.github.io/android-beacon-library/foreground-service.html
     */
    private fun setUpForegroundService(){
        val builder = Notification.Builder(this, "MainActivity")
        builder.setSmallIcon(R.drawable.ic_main_reduced)
        builder.setContentTitle("Study")
        builder.setContentText("Thank you for participating this study. Please do not disable Bluetooth, internet or GPS. :)")
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT + PendingIntent.FLAG_IMMUTABLE)
        builder.setContentIntent(pendingIntent);
        val channel =  NotificationChannel("beacon-ref-notification-id", "My Notification Name", NotificationManager.IMPORTANCE_DEFAULT)
        channel.setDescription("My Notification Channel Description")
        val notificationManager =  getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel);
        builder.setChannelId(channel.getId());
        Log.d(TAG_BEACON_FOREGROUNGSERVICE, "Calling enableForegroundServiceScanning")
        BeaconManager.getInstanceForApplication(this).enableForegroundServiceScanning(builder.build(), 456);
        Log.d(TAG_BEACON_FOREGROUNGSERVICE, "Back from  enableForegroundServiceScanning")
    }

    /**
     * beacons := all beacons in region (customUUID)
     * set up a Live Data observer so this Activity can get beacon data from the Application class
     * observer will be called each time a new list of beacons is ranged (typically ~1 second in the foreground)
     * handles with recognized beacons (customUUID from region)
     */
    private var isRangingStarted: Boolean = false
    private fun startRanging() {
        if (!isRangingStarted) {
            Log.d(TAG_RANGING, "started ranging")
            beaconManager.addRangeNotifier(object : RangeNotifier{
                override fun didRangeBeaconsInRegion(beacons: MutableCollection<Beacon>?, region: Region?) {
                    if (beacons != null) {
                        for(beacon: Beacon in beacons){
                            Log.d(TAG_RANGING, "02 detect beacon ${beacon.distance} metres nearby")
                            if(beacon.distance < 4){
                                SocialContactManager.updateSocialInteractionStatus(true)
                                Log.d(TAG_RANGING, "01 social interaction (distance < 4 metres)")
                            } else{
                                SocialContactManager.updateSocialInteractionStatus(false)
                                Log.d(TAG_RANGING, "00 no social interaction (distance > 4 metres)")

                            }
                        }
                    }
                }
            })
            isRangingStarted = true
        }
    }

    private fun stopRanging() {
        beaconManager.removeAllRangeNotifiers()
        Log.d(TAG_RANGING, "stopped ranging")
        isRangingStarted = false
    }

    /**
     * monitoring checks if device has entered or left the beacon region
     */
    val monitoringObserver = Observer<Int> { state ->
        if(Date().before(AppPreferences.getEndDate(this)) || Date().equals(AppPreferences.getEndDate(this))){
            if (state == MonitorNotifier.INSIDE) {
                Log.d(TAG_MONITORING, "01 inside")
                compensationBool = true
                //SocialContactManager.updateSocialInteractionStatus(true)

                startRanging()

                // start timer for social interaction's duration
                //entryTime = System.currentTimeMillis()
                entryTime = null.toString()
                entryDate = null.toString()
                entryTime = SimpleDateFormat("HH:mm:ss").format(Date()).toString()
                entryDate = SimpleDateFormat("dd/MM/yyyy").format(Date()).toString()
            }
            else if(state == MonitorNotifier.OUTSIDE){
                Log.d(TAG_MONITORING, "02 outside/still inside")
                compensationBool = false
                // waiting 30 seconds until really detecting out of beaconrange
                // because of compenstion from failures
                compensateScanFails()
            }
        }
    }

    private fun compensateScanFails(){
        compensationHandler.postDelayed(
            { if(!compensationBool){
                Log.d(TAG_MONITORING, "00 outside")
                stopRanging()
                SocialContactManager.updateSocialInteractionStatus(false)
                //exitTime = System.currentTimeMillis()
                exitTime = null.toString()
                exitDate = null.toString()
                exitTime = SimpleDateFormat("HH:mm:ss").format(Date()).toString()
                exitDate = SimpleDateFormat("dd/MM/yyyy").format(Date()).toString()
                var socialInteraction = hashMapOf<String, String>()
                socialInteraction.put("subjectID", AppPreferences.getSubjectID(this))
                socialInteraction.put("dateStartOfInteraction", entryDate)
                socialInteraction.put("startOfInteraction", entryTime)
                socialInteraction.put("dateEndOfInteraction", entryDate)
                socialInteraction.put("endOfInteraction", exitTime)
                //socialInteraction.put("date", SimpleDateFormat("dd/mm/yyyy").format(Date()))
                //socialInteraction.put("endOfInteraction", DateFormat.format("HH:mm:ss", exitTime).toString())
                //socialInteraction.put("startOfInteraction", DateFormat.format("HH:mm:ss", entryTime).toString())
                db
                    .collection("socialInteraction")
                    .document("${AppPreferences.getSubjectID(this)} ${System.currentTimeMillis()}").set(socialInteraction)
                    .addOnSuccessListener { documentReference -> Log.d(TAG_FIREBASE_MAIN, "01 success on sending socialInteraction to firebase") }
                    .addOnFailureListener { e -> Log.w(TAG_FIREBASE_MAIN, "00 error on sending socialInteraction to firebase", e) }

                //entryTime = 0
                //exitTime = 0
                //entryTime = ""
                //exitTime = ""
                //entryDate = ""
                //exitDate = ""

            } },
            30000)
    }

    /**
     * init BluetoothAdapter and check if enable
     * if not, ask user to enable bluetooth
     * not necessary to init BluetoothLeScanner/BluetoothAdapter for scan routine -> altbeacon library takes over
     * required for BLE-Scan (beacon)
     */
    @SuppressLint("MissingPermission")
    private fun enableBluetooth(){
        bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter

        while (!bluetoothAdapter.isEnabled){
            // check if bluetooth is disabled, and enable
            if(!bluetoothAdapter.isEnabled) {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                enableBtIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(enableBtIntent)
            }
        }
    }

    /**
     * checks if location/gps is enable
     * if not, ask user to enable location/gps
     * required for BLE-Scan (beacon)
     */
    private fun enableGPS(){
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        while(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            // check if GPS is disabled, and enable
            if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                val enableGpsIntent = Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                enableGpsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(enableGpsIntent)
            }
        }
    }

    /**
     * setting studystarttime in sharedPreferences
     * switching study-mode will base on this
     */
    private fun setUpStudyStart(){
        val dateFormat = SimpleDateFormat("dd/MM/yyyy")

        // start date of study
        val startDate = Date()
        val formattedDate = dateFormat.format(startDate)
        AppPreferences.setStartTimeOfStudy(this, formattedDate.toString())

        // end date of study
        val c: Calendar = Calendar.getInstance()
        c.setTime(startDate)
        c.add(Calendar.DATE, AppPreferences.getStudyDuration(this))
        val endDate: Date = c.time
        val formattedEndDate = dateFormat.format(endDate)
        AppPreferences.setEndDate(this, formattedEndDate)

        // date when switch to study mode B
        val d: Calendar = Calendar.getInstance()
        d.setTime(startDate)
        d.add(Calendar.DATE, AppPreferences.getDaysTillStudyModeSwitch(this))
        val switchDate: Date = d.time
        val formattedSwitchDate = dateFormat.format(switchDate)
        AppPreferences.setSwitchDate(this, formattedSwitchDate)

        var studyStart = hashMapOf<String, String>()
        studyStart.put("subjectID", AppPreferences.getSubjectID(this))
        studyStart.put("studyStart", formattedDate)
        studyStart.put("studyEnd", formattedEndDate)
        studyStart.put("switchedStudyMode", formattedSwitchDate)
        db
            .collection("studyDurations")
            .document("${AppPreferences.getSubjectID(this)} ${System.currentTimeMillis()}").set(studyStart)
            .addOnSuccessListener { documentReference -> Log.d(TAG_FIREBASE_MAIN, "01 success on sending studyDuration to firebase") }
            .addOnFailureListener { e -> Log.w(TAG_FIREBASE_MAIN, "00 error on sending studyDuration to firebase", e) }

    }

}