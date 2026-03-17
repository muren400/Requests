package org.muren.requests
import android.content.Context
import com.getpebble.android.kit.PebbleKit
import com.getpebble.android.kit.PebbleKit.PebbleDataReceiver
import com.getpebble.android.kit.util.PebbleDictionary
import java.util.UUID

const val CONFIG: Int = 100
const val REQUEST: Int = 101

const val APP_UUID = "7c89d69e-59d0-4078-a45c-faba17cf5741"

class PebbleCompanion(val requestObjects: List<RequestObject>, val context: Context) {
    val appUuid: UUID = UUID.fromString(APP_UUID)

    var debounce = true;

    var dataReceiver: PebbleDataReceiver = object : PebbleDataReceiver(appUuid) {
        override fun receiveData(context: Context, transactionId: Int, dict: PebbleDictionary?) {
            onReceiveData(context, transactionId, dict)
        }
    };

    init {
        PebbleKit.registerReceivedDataHandler(context, dataReceiver);
    }

    fun onReceiveData(context: Context, transactionId: Int, dict: PebbleDictionary?) {
        try {
            if(dict!!.getInteger(CONFIG) != null) {
                sendConfig()
            } else if(dict!!.getInteger(REQUEST) != null) {
                sendRequest(dict.getInteger(REQUEST).toInt());
            }
        } finally {
            PebbleKit.sendAckToPebble(context, transactionId)
        }
    }

    fun sendConfig() {
        debounce = true;

        val dict = PebbleDictionary()
        var index = 0;
        for (requestObject in requestObjects) {
            dict.addString(index++, requestObject.name);

            if(dict.size() > 4){
                PebbleKit.sendDataToPebble(context, appUuid, dict);
                dict.removeAll { true }
            }
        }

        if(dict.size() > 0){
            PebbleKit.sendDataToPebble(context, appUuid, dict);
            dict.removeAll { true }
        }

        debounce = false;
    }

    fun sendRequest(index: Int) {
        if(debounce) {
            debounce = false;
            return;
        }

        RequestClient().makeRequest(context, requestObjects[index].urls, null, null, null)
    }
}