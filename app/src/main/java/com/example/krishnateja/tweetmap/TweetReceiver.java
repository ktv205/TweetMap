package com.example.krishnateja.tweetmap;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

/**
 * Created by krishnateja on 12/8/2014.
 */
public class TweetReceiver extends ResultReceiver {

    public TweetReceiver(Handler handler) {
        super(handler);
        // TODO Auto-generated constructor stub
    }

    private Receiver mReceiver;

    public void setReceiver(Receiver receiver) {
        mReceiver = receiver;
    }

    public interface Receiver {
        public void onReceiveResult(int resultCode, Bundle resultData);
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if (mReceiver != null) {
            mReceiver.onReceiveResult(resultCode, resultData);
        }
    }

}

