package com.example.erzae.requests;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class BroadcastReceiver extends android.content.BroadcastReceiver {

    public static final String ACTION_STUB = "requests.action.";

    private RequestClient client;
    private List<RequestObject> requestObjects;

    public BroadcastReceiver(){
        client = null;
        requestObjects = null;
    }

    public BroadcastReceiver(RequestClient client, List<RequestObject> requestObjects){
        super();

        this.client = client;
        this.requestObjects = requestObjects;
    }

    @Override
    public void onReceive(final Context context, Intent intent) {

        if(client == null){
            client = new RequestClient();
        }

        if(requestObjects == null || requestObjects.isEmpty()){
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            requestObjects = new ArrayList<>();
            MainActivity.readPreferences(sharedPref, requestObjects);
            if(requestObjects == null || requestObjects.isEmpty()){
                Toast.makeText(context, "no requests selected", Toast.LENGTH_LONG).show();
                return;
            }
        }

        String strIndex = intent.getAction();
        strIndex = strIndex.substring(ACTION_STUB.length());

        try {
            int index = Integer.parseInt(strIndex);
            client.makeRequest(context, requestObjects.get(index).getUrls(), null, null, null);
        } catch (NumberFormatException e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
