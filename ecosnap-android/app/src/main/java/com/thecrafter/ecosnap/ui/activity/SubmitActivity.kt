package com.thecrafter.ecosnap.ui.activity

import android.content.Context
import android.content.Intent
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.SpannableStringBuilder
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.activity_submit.*
import com.jaredrummler.materialspinner.MaterialSpinner
import com.thecrafter.ecosnap.*
import com.thecrafter.ecosnap.data.Incident
import retrofit2.Call
import retrofit2.Response
import java.util.*


private const val INTENT_IMAGE = "INTENT_IMAGE"

fun Context.SubmitActivityIntent(): Intent {
    return Intent(this, SubmitActivity::class.java)
}

class SubmitActivity : AppCompatActivity() {

    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var mLocation: Location? = null

    private var resultReceiver: AdderssResultReceiver = AdderssResultReceiver(Handler())

    private fun startIntentService() {

        AdderssResultReceiver.onAddress = { address ->
            val str = when (address.isEmpty()) {
                true  -> "Pireos 100, Athens 118 54, Greece"
                false -> address
            }
            etxt_address.text = SpannableStringBuilder(str)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_submit)

        startIntentService()

        val base64 = MyApplication.imageBase64

        val spinner = findViewById<MaterialSpinner>(R.id.spinner)
        val spinnerItems = arrayOf("Trash", "Compost", "Water Damage", "Smoking", "Pollution")
        spinner.setItems(*spinnerItems)

        btn_submit.setOnClickListener {
            val citizen = "34710ea4-1f24-11ea-9712-0242ac110002"
            val incident = Incident(
                UUID.randomUUID().toString(),
                citizen,
                Date().time,
                base64,
                mLocation?.latitude?.toFloat() ?: 0f,
                mLocation?.longitude?.toFloat() ?: 0f,
                etxt_address.text.toString(),
                etxt_description.text.toString(),
                spinnerItems[spinner.selectedIndex],
                "pending"
            )
            ApiService.service.createIncident(citizen, incident).enqueue(object:
                retrofit2.Callback<String > {
                override fun onFailure(call: Call<String>, t: Throwable) {
                }

                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if (response.body() != null) {
                        Log.e("ApiService", response.body() ?: "")
                    }
                }
            })

            startActivity(DashboardActivityIntent())
            finish()
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onStart() {
        super.onStart()
        img_preview.setImageBitmap(rotateBitmap(base64ToBitmap(MyApplication.imageBase64), 90f))
        mFusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                mLocation = location
                val intent = Intent(this, FetchAddressIntentService::class.java).apply {
                    putExtra(RECEIVER, resultReceiver)
                    putExtra(LOCATION_DATA_EXTRA, location)
                }
                etxt_address.text = SpannableStringBuilder("${location?.longitude} ${location?.latitude}")
                startService(intent)
                FetchAddressIntentService.enqueueGetAddressWork(this, resultReceiver, location)
            }
    }
}
