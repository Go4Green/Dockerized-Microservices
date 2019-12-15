package com.thecrafter.ecosnap

import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.os.ResultReceiver
import android.text.TextUtils
import androidx.core.app.JobIntentService
import org.jetbrains.annotations.NotNull
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

val PACKAGE_NAME = "com.thecrafter.ecosnap"
val RESULT_DATA_KEY = "$PACKAGE_NAME.RESULT_DATA_KEY"
val SUCCESS_RESULT = 0
val FAILURE_RESULT = 1
val RECEIVER = "$PACKAGE_NAME.RECEIVER"
val LOCATION_DATA_EXTRA = "$PACKAGE_NAME.LOCATION_DATA_EXTRA"

@Suppress("PropertyName")
class FetchAddressIntentService : JobIntentService() {

    /**
     * Convenience method for enqueuing work in to this service.
     */
    companion object {
        /** Unique job ID for this service.  */
        private val JOB_ID = 1000
        fun enqueueGetAddressWork(
            context: Context?,
            receiver: ResultReceiver?,
            location: Location?
        ) {
            val intent = Intent(context, FetchAddressIntentService::class.java)
            intent.putExtra(LOCATION_DATA_EXTRA, location)
            intent.putExtra(RECEIVER, receiver)
            enqueueWork(context!!, FetchAddressIntentService::class.java, JOB_ID, intent)
        }
    }

    override fun onHandleWork(@NotNull intent: Intent) {

        val mockLocation = Location(LocationManager.GPS_PROVIDER).apply {
            latitude = 37.978085
            longitude = 23.71424
        }

        val location: Location = intent.getParcelableExtra(LOCATION_DATA_EXTRA) ?: mockLocation
        val receiver: ResultReceiver?= intent.getParcelableExtra(RECEIVER)
        if (location == null) {
            //Timber.w("Intent has null location")
            return
        }
        if (receiver == null) {
            //Timber.w("Intent has null receiver")
            return
        }
        val (first, second) = getAddress(location)
        deliverResultToReceiver(receiver, first, second)
    }

    private fun getAddress(location: Location): Pair<Int, String> {
        val geocoder = Geocoder(this, Locale.getDefault())
        var errorMessage = ""
        var addresses: List<Address>? = null
        try {
            addresses = geocoder.getFromLocation(
                location.getLatitude(),
                location.getLongitude(),  // In this sample, get just a single address.
                1
            )
        } catch (ioException: IOException) { // Catch network or other I/O problems.
            errorMessage = "Service not available"
            //Timber.e(ioException)
        } catch (illegalArgumentException: IllegalArgumentException) { // Catch invalid latitude or longitude values.
            errorMessage = "Invalid LatLng"
//            Timber.e(
//                illegalArgumentException, errorMessage + ". " +
//                        "Latitude = " + location.getLatitude() +
//                        ", Longitude = " +
//                        location.getLongitude()
//            )
        }
        // Handle case where no address was found.
        return if (addresses == null || addresses.size == 0) {
            if (errorMessage.isEmpty()) {
                errorMessage = "No address found"
                //Timber.e(errorMessage)
            }
            Pair(FAILURE_RESULT, errorMessage)
        } else {
            val address: Address = addresses[0]
            val addressFragments: ArrayList<String?> = ArrayList()
            // Fetch the address lines using getAddressLine,
            // join them, and send them to the thread.
            for (i in 0..address.getMaxAddressLineIndex()) {
                addressFragments.add(address.getAddressLine(i))
            }
            //Timber.d("Address Found!")
            Pair(
                SUCCESS_RESULT,
                TextUtils.join(
                    System.getProperty("line.separator")!!,
                    addressFragments
                )
            )
        }
    }

    private fun deliverResultToReceiver(
        receiver: ResultReceiver,
        resultCode: Int,
        message: String
    ) {
        val bundle = Bundle()
        bundle.putString(RESULT_DATA_KEY, message)
        receiver.send(resultCode, bundle)
    }
}