package com.example.krishnateja.tweetmap;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.os.SystemClock;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

/**
 * Created by krishnateja on 12/7/2014.
 */
public class GcmIntentService  extends IntentService {
    private final static String TAG="GcmIntentService";
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    private ResultReceiver receiver;
    Notification.Builder builder;
    public GcmIntentService() {
        super("name");
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "in onHandleIntent");
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);
        Log.d(TAG,"MessageType->"+messageType);
        if(!extras.isEmpty()){
            Log.d(TAG,"extras not empty");
            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                sendNotification("Send error: " + extras.toString());
            }else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_DELETED.equals(messageType)) {
                sendNotification("Deleted messages on server: " +
                        extras.toString());
                // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                // This loop represents the service doing some work.
                for (int i=0; i<5; i++) {
                    Log.i(TAG, "Working... " + (i+1)
                            + "/5 @ " + SystemClock.elapsedRealtime());
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                    }
                }
                Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());
                // Post notification of received message.
                sendNotification("Received: " + extras.getString("default"));
                Log.i(TAG, "Received: " + extras.toString());
            }
            GcmBroadcastReceiver.completeWakefulIntent(intent);
        }


    }
    @SuppressLint("NewApi")
    private void sendNotification(String msg){
        Log.d(TAG,"in sending notification");
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, KeyWords.class), 0);

        builder=new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("Testing GCM service")
                .setStyle(new Notification.BigTextStyle().bigText(msg))
                .setContentText(msg);
        builder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID,builder.build());
        Bundle b = new Bundle();
        b.putString("tweetjson",msg);
        if (receiver != null) {
            receiver.send(1, b);
        }


    }

}

