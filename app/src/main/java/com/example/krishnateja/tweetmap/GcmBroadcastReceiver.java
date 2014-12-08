package com.example.krishnateja.tweetmap;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

/**
 * Created by krishnateja on 12/7/2014.
 */
public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {
    public final static  String TAG="GcmBroadcastReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("received", "received");
        String message=intent.getExtras().getString("message");
        Log.d("message","message->"+message);
        ComponentName comp=new ComponentName(context.getPackageName(), GcmIntentService.class.getName());
        startWakefulService(context, intent.setComponent(comp));
        setResultCode(Activity.RESULT_OK);

    }

}

