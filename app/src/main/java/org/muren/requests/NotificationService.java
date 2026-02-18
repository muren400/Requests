package org.muren.requests;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.IBinder;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

public class NotificationService extends Service {

    public static boolean isServiceRunning = false;

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
        stopForeground( STOP_FOREGROUND_REMOVE);
        stopSelf();
        isServiceRunning = false;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        requestObjects = MainActivity.readPreferences(this);

        startMyOwnForeground();

        // Create an IntentFilter instance.
        IntentFilter intentFilter = new IntentFilter();

        // Add network connectivity change action.
        for(int i=0; i<requestObjects.size(); i++){
            intentFilter.addAction(BroadcastReceiver.ACTION_STUB + i);
        }

        // Set broadcast receiver priority.
        intentFilter.setPriority(100);

        // Create a network change broadcast receiver.
        receiver = new BroadcastReceiver(requestObjects);

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
                    PendingIntent.getBroadcast(this, 0, toggleIntent, PendingIntent.FLAG_IMMUTABLE);
            intents.add(pendingIntent);

            index++;
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        notificationBuilder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher);

        RemoteViews view = new RemoteViews(getPackageName(), R.layout.notification);
        populateButtonRows(view, intents);

        Notification notification = notificationBuilder.setContent(view)
                .setStyle(new NotificationCompat.BigPictureStyle())
                .setCustomContentView(view)
                .setCustomBigContentView(view)
                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setColor(Color.BLACK)
                .build();

        startForeground(1, notification);
    }

    private void populateButtonRows(RemoteViews view, List<PendingIntent> intents) {
        RemoteViews viewRow = null;

        int index = 0;
        for(PendingIntent intent : intents){
            var indexInRow = index % 4;
            if(indexInRow % 4 == 0) {
                viewRow = new RemoteViews(getPackageName(), R.layout.notification_request_item_row);
                view.addView(R.id.notification, viewRow);
            }

            RequestObject requestObject = requestObjects.get(index);
            String strID = "notification_row_button_" + indexInRow;

            index++;

            int id = getResId(strID, R.id.class);
            if(id < 0) {
                continue;
            }

            viewRow.setOnClickPendingIntent(id, intent);
            viewRow.setTextViewText(id, requestObject.getName());
        }
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
