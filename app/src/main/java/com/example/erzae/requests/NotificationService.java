package com.example.erzae.requests;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class NotificationService extends Service {

    public static boolean isServiceRunning = false;

    private RequestClient client;
    private BroadcastReceiver receiver;
    private List<RequestObject> requestObjects;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    void stopMyService() {
        stopForeground(true);
        stopSelf();
        isServiceRunning = false;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        requestObjects = new ArrayList<>();
        MainActivity.readPreferences(sharedPref, requestObjects);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
            startForeground(1, new Notification());

        client = new RequestClient();

        // Create an IntentFilter instance.
        IntentFilter intentFilter = new IntentFilter();

        // Add network connectivity change action.
        for(int i=0; i<MainActivity.MAX_REQUESTS; i++){
            intentFilter.addAction(BroadcastReceiver.ACTION_STUB + i);
        }

        // Set broadcast receiver priority.
        intentFilter.setPriority(100);

        // Create a network change broadcast receiver.
        receiver = new BroadcastReceiver(client, requestObjects);

        // Register the broadcast receiver with the intent filter object.
        registerReceiver(receiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Toast.makeText(this, "test", Toast.LENGTH_LONG).show();

        // Unregister screenOnOffReceiver when destroy.
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void startMyOwnForeground(){
        String NOTIFICATION_CHANNEL_ID = "com.example.erzae.sockets";
        String channelName = "NotificationService";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        List<PendingIntent> intents = new LinkedList<>();
        int index = 0;
        for(RequestObject requestObject : requestObjects){
            Intent toggleIntent = new Intent(this, BroadcastReceiver.class);
            toggleIntent.setAction(BroadcastReceiver.ACTION_STUB + index);
            PendingIntent pendingIntent =
                    PendingIntent.getBroadcast(this, 0, toggleIntent, 0);
            intents.add(pendingIntent);

            index++;
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.icon)
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE);

        index = 0;
        for(PendingIntent intent : intents){
            notificationBuilder.addAction(R.mipmap.ic_launcher, requestObjects.get(index++).getName(), intent);
        }

        Notification notification = notificationBuilder.setContentIntent(intents.get(0)).build();
        startForeground(1, notification);
    }
}
