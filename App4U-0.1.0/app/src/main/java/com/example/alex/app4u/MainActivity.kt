package com.example.alex.app4u

import android.Manifest
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.TextView
import android.Manifest.permission
import android.Manifest.permission.READ_PHONE_STATE
import android.app.PendingIntent.getActivity
import android.support.v4.app.ActivityCompat
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.Button
import android.widget.Toast
import java.util.*
import kotlin.math.log
import android.R.attr.versionName
import android.content.pm.PackageInfo


class MainActivity : AppCompatActivity() {
    private val MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 10

    private lateinit var IMEITextView: TextView
    private lateinit var VersionTextView: TextView
    private lateinit var showIMEIButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getSupportActionBar()?.title = "Kuchinsky and partners ®"

        showIMEIButton = findViewById(R.id.showIMEIButton)
        IMEITextView = findViewById(R.id.IMEITextView)
        VersionTextView = findViewById(R.id.versionTextView)





        try {
            val pInfo = getPackageManager().getPackageInfo(packageName, 0)
            VersionTextView.text = "Version ${pInfo.versionName}"
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e("IMELOG", e.message)
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED){

            showIMEIButton.setOnClickListener({
                askForReadPhoneStatePermission()
            })
        }else{
            Log.d("IMEILOG", "Read phone state permission has already been granted")
            IMEITextView.text = "Your IMEI: ${getIMEI()}"
            showIMEIButton.visibility = View.GONE
        }

    }
    private fun askForReadPhoneStatePermission(){
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.READ_PHONE_STATE)) {
                Log.d("IMEILOG", "Showing explanation to the user why we need this permission")
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.READ_PHONE_STATE),
                        MY_PERMISSIONS_REQUEST_READ_PHONE_STATE)
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.READ_PHONE_STATE),
                        MY_PERMISSIONS_REQUEST_READ_PHONE_STATE)
                Log.d("IMEILOG", "Asked for a read phone state permission")
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {

        }
    }
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_READ_PHONE_STATE -> {
                Log.d("IMEILOG", "Handling read phone state permission")
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Log.d("IMEILOG", "Read phone state permission was granted")
                    showIMEIButton.visibility = View.GONE
                    IMEITextView.text = "Your IMEI: ${getIMEI()}"
                } else {
                    Log.d("IMEILOG", "Read phone state permission wasn't granted")
                    Toast.makeText(this, "You have to give permission to see the IMEI",
                            Toast.LENGTH_LONG).show();
                }
                return
            }

        // Add other 'when' lines to check for other
        // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }

    private fun getIMEI(): String {
        try{
            val tm = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            val IMEI = tm.getDeviceId()
            return IMEI
        } catch (e: SecurityException){
            Log.e("IMEILOG", e.toString())
            throw e
        }
    }

}
