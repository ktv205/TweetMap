package com.example.krishnateja.tweetmap;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.JsonReader;
import android.util.Log;

import com.example.krishnateja.tweetmap.models.RequestPackage;
import com.example.krishnateja.tweetmap.models.TweetList;
import com.example.krishnateja.tweetmap.models.TweetModel;
import com.example.krishnateja.tweetmap.models.TweetmapPreferences;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by krishnateja on 11/10/2014.
 */
public class GetTweetsAsyncTask extends AsyncTask<RequestPackage, Integer, List<TweetModel>> {
    Context context;
    ProgressDialog progress;

    @Override
    protected void onPreExecute() {
        if(TweetmapPreferences.number==1) {
            if (progress != null) {
                progress.dismiss();
            }
            progress = new ProgressDialog(context);
            progress.setTitle("Loading");
            progress.setMessage("Wait while loading...");
            progress.show();

        }
    }

    public GetTweetsAsyncTask(Context context) {
        this.context = context;
    }

    @Override
    protected List doInBackground(RequestPackage... params) {
        return new RemoteConnection().getDataTweets(params[0]);
    }

    @Override
    protected void onPostExecute(List<TweetModel> tweetModels) {
        if(TweetmapPreferences.number==1) {
            if (progress != null) {
                if (progress.isShowing()) {
                    progress.dismiss();
                }
            }
        }
        TweetList tweetList = new TweetList(tweetModels);
        Intent intent = new Intent(context, TweetsActivity.class);
        intent.putExtra("PARCEL", tweetList);
        context.startActivity(intent);

    }


}
