package com.thecrafter.ecosnap

import android.os.Bundle
import android.os.Handler
import android.support.v4.os.ResultReceiver


class AdderssResultReceiver(handler: Handler?) : ResultReceiver(handler) {
    companion object {
        var onAddress: (String) -> Unit = {}
    }

    override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {
        if (resultData == null) {
            return
        }

        // Display the address string
        // or an error message sent from the intent service.
        var addressOutput =
            resultData.getString(RESULT_DATA_KEY)
        if (addressOutput == null) {
            addressOutput = ""
        }
        onAddress(addressOutput)
    }
}