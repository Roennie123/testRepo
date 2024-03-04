package com.example.bastudy

import android.content.Context
import android.graphics.PixelFormat
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView

class Intervention(context: Context?) {

    private var windowManager: WindowManager? = null
    private var overlayView: View? = null

    private var handler: Handler

    init {
        val layoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        layoutParams.gravity = Gravity.CENTER

        overlayView = LayoutInflater.from(context).inflate(R.layout.intervention_overlay, null)

        windowManager = context?.getSystemService(Context.WINDOW_SERVICE) as WindowManager


        val closeButton = overlayView?.findViewById<Button>(R.id.bt_close_overlay)
        closeButton?.setOnClickListener {
            hideOverlay()
        }

        closeButton?.isClickable = false

        handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            closeButton?.isClickable = true
            closeButton?.alpha = 1f
        }, 2000)

        windowManager?.addView(overlayView, layoutParams)
    }

    fun hideOverlay() {
        InterventionManager.setOverlayCreatingStatus(false)
        InterventionManager.setOverlayClosingStatus(false)
        windowManager?.removeView(overlayView)
    }

}