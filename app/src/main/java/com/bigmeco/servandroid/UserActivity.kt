package com.bigmeco.servandroid

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.PointF
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.View
import com.dlazaro66.qrcodereaderview.QRCodeReaderView
import android.widget.TextView
import android.support.v4.app.ActivityCompat


class UserActivity : AppCompatActivity() , QRCodeReaderView.OnQRCodeReadListener {


    private val resultTextView: TextView? = null
    private var qrCodeReaderView: QRCodeReaderView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)


        val permissionStatus = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)

        if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.CAMERA), 0)
        }


        qrCodeReaderView = findViewById<View>(R.id.qrdecoderview) as QRCodeReaderView?
        qrCodeReaderView!!.setOnQRCodeReadListener(this)

        // Use this function to enable/disable decoding
        qrCodeReaderView!!.setQRDecodingEnabled(true)

        // Use this function to change the autofocus interval (default is 5 secs)
        qrCodeReaderView!!.setAutofocusInterval(2000L)

        // Use this function to enable/disable Torch
        qrCodeReaderView!!.setTorchEnabled(true)

        // Use this function to set front camera preview
        qrCodeReaderView!!.setFrontCamera()

        // Use this function to set back camera preview
        qrCodeReaderView!!.setBackCamera()
    }


    override fun onQRCodeRead(text: String, points: Array<PointF>) {
        startActivity(Intent(this, HostActivity::class.java))
    }

    override fun onResume() {
        super.onResume()
        qrCodeReaderView!!.startCamera()
    }

    override fun onPause() {
        super.onPause()
        qrCodeReaderView!!.stopCamera()
    }
}
