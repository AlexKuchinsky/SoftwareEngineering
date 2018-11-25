package com.example.alex.app4u

import android.Manifest
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.telephony.TelephonyManager
import android.widget.TextView
import android.support.v4.app.ActivityCompat
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.Button
import android.widget.Toast
import android.support.design.widget.Snackbar;
import android.content.Intent
import android.net.Uri
import android.provider.Settings
//import org.ajoberstar.grgit.Grgit

class MainActivity : AppCompatActivity() {
    private val MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 10

    private lateinit var imeiTextView: TextView
    private lateinit var versionTextView: TextView
    private lateinit var showIMEIButton: Button
    private lateinit var mainActivityLayout: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getSupportActionBar()?.title = getString(R.string.action_bar_title)

        showIMEIButton = findViewById(R.id.showIMEIButton)
        imeiTextView = findViewById(R.id.IMEITextView)
        versionTextView = findViewById(R.id.versionTextView)
        mainActivityLayout = findViewById(R.id.mainActivityLayout)

        try {
            val pInfo = getPackageManager().getPackageInfo(packageName, 0)
            versionTextView.text = "${getString(R.string.app_version)} ${pInfo.versionName}"
        } catch (e: PackageManager.NameNotFoundException) {
            //Log.e("IMELOG", e.message)
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED){
            showIMEIButton.setOnClickListener({askForReadPhoneStatePermission()})
        }else{
            imeiTextView.text = getString(R.string.your_imei) + getIMEI()
            showIMEIButton.visibility = View.GONE
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_READ_PHONE_STATE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    showIMEIButton.visibility = View.GONE
                    imeiTextView.text = getString(R.string.your_imei) + getIMEI()
                } else {
                    if(ActivityCompat.shouldShowRequestPermissionRationale(this,
                                    Manifest.permission.READ_PHONE_STATE)){
                        Toast.makeText(this, "You have to give permission to see the IMEI",
                                Toast.LENGTH_LONG).show()
                    }else{
                        Snackbar.make(mainActivityLayout, "You have to give permission manually",
                                Snackbar.LENGTH_LONG)
                                .setAction("SETTINGS", {openSettingsForPhoneStatePermission()})
                                .show()
                    }
                }
                return
            }
            else -> {

            }
        }
    }

    private fun askForReadPhoneStatePermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.READ_PHONE_STATE)) {
                val message = "Permission is needed to use this functionality"
                Snackbar.make(mainActivityLayout, message, Snackbar.LENGTH_INDEFINITE)
                        .setAction("GRANT", View.OnClickListener {
                            ActivityCompat.requestPermissions(this,
                                    arrayOf(Manifest.permission.READ_PHONE_STATE),
                                    MY_PERMISSIONS_REQUEST_READ_PHONE_STATE)
                        })
                        .show()
            } else {
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.READ_PHONE_STATE),
                        MY_PERMISSIONS_REQUEST_READ_PHONE_STATE)
            }
        }
    }

    private fun openSettingsForPhoneStatePermission(){
        val appSettingsIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:$packageName"))
        startActivityForResult(appSettingsIntent, MY_PERMISSIONS_REQUEST_READ_PHONE_STATE)

        finish();
        startActivity(getIntent());
    }

    private fun getIMEI(): String {
        try{
            val tm = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            val IMEI = tm.getDeviceId()
            return IMEI
        } catch (e: SecurityException){
            //Log.e("IMEILOG", e.toString())
            throw e
        }
    }

}
