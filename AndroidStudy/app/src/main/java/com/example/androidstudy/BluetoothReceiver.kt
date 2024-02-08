package com.example.androidstudy

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.util.Log
import org.altbeacon.beacon.Beacon
import org.altbeacon.beacon.BeaconParser
import org.altbeacon.beacon.BeaconTransmitter


/**
 * BluetoothReceiver is a BroadcastReceiver which checks if bluetooth was turned on
 * * if turns on then starts advertising altbeacon
 * * necessary because if bluetooth turns off beacon transmission quit and never starts again
 *
 * registered in MainActivity() by startBeaconScanService() -> start study
 */
class BluetoothReceiver: BroadcastReceiver() {

    private val TAG = "BluetoothReceiver"

    private lateinit var customUUID: String

    override fun onReceive(context: Context, intent: Intent?) {

        if(intent?.action == BluetoothAdapter.ACTION_STATE_CHANGED){
            var state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)

            when (state) {
                BluetoothAdapter.STATE_OFF -> {
                    Log.d(TAG, "00 disables bluetooth")
                }
                BluetoothAdapter.STATE_ON ->  {
                    Log.d(TAG, "01 enables bluetooth")
                    startBeaconTransmission(context)
                }
            }
        }
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
        Log.d(ContentValues.TAG, "01 beacon advertising successful. customUUID = $beacon")
    }

}