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
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.lang.reflect.Field;
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
        String NOTIFICATION_CHANNEL_ID = "com.example.erzae.request";
        String channelName = "NotificationService";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_HIGH);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
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

        RemoteViews view = new RemoteViews(getPackageName(), R.layout.notification);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        notificationBuilder.setVisibility(Notification.VISIBILITY_PUBLIC)
                .setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher);

        index = 0;
        for(PendingIntent intent : intents){
            RequestObject requestObject = requestObjects.get(index);
            String strID = "notification_button_" + index++;
            int id = getResId(strID, R.id.class);
            if(id < 0)
                continue;
            view.setOnClickPendingIntent(id, intent);
            view.setTextViewText(id, requestObject.getName());
        }

        Notification notification = notificationBuilder.setContent(view)
                .setStyle(new NotificationCompat.BigPictureStyle())
                .setCustomContentView(view)
                .setCustomBigContentView(view)
                .setPriority(Notification.PRIORITY_MAX)
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .setColor(Color.BLACK)
                .build();

        startForeground(1, notification);
    }

    private int getResId(String resName, Class<?> c) {

        try {
            Field idField = c.getDeclaredField(resName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}
