package org.muren.requests

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import org.json.JSONArray
import org.json.JSONObject
import androidx.core.content.edit

class RequestsConfig {
    fun readConfig(context: Context): List<RequestObject>? {
        val requestObjects = ArrayList<RequestObject>();

        val preferences = getConfig(context);
        val configAsString = preferences.getString("config", null) ?: return requestObjects;

        val jsonConfig = JSONObject(configAsString);
        val jsonRequests = jsonConfig.getJSONArray("requests");
        for(i in 0..<jsonRequests.length()) {
            val jsonRequest = jsonRequests.getJSONObject(i);
            val requestObject = RequestObject(jsonRequest.getString("name"));

            val jsonUrls = jsonRequest.getJSONArray("urls");
            for(j in 0..<jsonUrls.length()) {
                requestObject.addUrl(jsonUrls.getString(j));
            }

            requestObjects.add(requestObject);
        }

        return requestObjects;
    }

    fun writeConfig(requestObjects: List<RequestObject>, context: Context) {
        try {
            val jsonConfig = JSONObject();
            val jsonRequests = JSONArray();
            jsonConfig.put("requests", jsonRequests);

            for(requestObject in requestObjects) {
                if(isEmpty(requestObject)) {
                    continue;
                }

                val jsonRequest = JSONObject();
                jsonRequest.put("name", requestObject.name);

                val jsonUrls = JSONArray(requestObject.urls);
                jsonRequest.put("urls", jsonUrls);

                jsonRequests.put(jsonRequest);
            }

            val sharedPref = getConfig(context);
            sharedPref.edit {
                putString("config", jsonConfig.toString())
            };
        } catch (error: Exception) {
            Toast.makeText(context, error.message, Toast.LENGTH_LONG).show();
        }
    }

    private fun isEmpty(requestObject: RequestObject): Boolean {
        return requestObject.name == null || requestObject.name.isEmpty() || requestObject.urls == null || requestObject.urls.isEmpty();
    }

    private fun getConfig(context: Context): SharedPreferences {
        return context.getSharedPreferences(context.packageName + "_preferences", Context.MODE_PRIVATE);
    }
}