package com.example.krishnateja.tweetmap;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.JsonReader;

import com.example.krishnateja.tweetmap.models.RequestPackage;
import com.example.krishnateja.tweetmap.models.TweetList;
import com.example.krishnateja.tweetmap.models.TweetModel;

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

    public GetTweetsAsyncTask(Context context) {
        this.context = context;
    }

    @Override
    protected List doInBackground(RequestPackage... params) {

        return getTweetsForKeyword(params[0]);


    }

    private List getTweetsForKeyword(RequestPackage param) {
        URL url = null;
        try {
            url = new URL(param.getURI());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            conn.setRequestMethod(param.getMethod());
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
        if (param.getMethod().equals("POST")) {
            conn.setDoOutput(true);
            OutputStreamWriter writer = null;
            try {
                writer = new OutputStreamWriter(conn.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                writer.write(param.getEncodedParams());
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        InputStream input = null;
        try {
            input = conn.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        JsonReader reader = null;
        try {
            reader = new JsonReader(new InputStreamReader(input, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return jsonReaderArray(reader);


    }

    @Override
    protected void onPostExecute(List<TweetModel> tweetModels) {
        super.onPostExecute(tweetModels);
        TweetList tweetList = new TweetList(tweetModels);
        Intent intent = new Intent(context, TweetsActivity.class);
        intent.putExtra("PARCEL", tweetList);
        context.startActivity(intent);
    }

    public List jsonReaderArray(JsonReader reader) {
        List<TweetModel> tweetModelList = new ArrayList<TweetModel>();
        try {
            reader.beginArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            while (reader.hasNext()) {
                tweetModelList.add(JsonReaderObject(reader));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            reader.endArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tweetModelList;
    }

    public TweetModel JsonReaderObject(JsonReader reader) {
        String tweetId = null, name = null, location = null, tweet = null;
        double lat = 0, lng = 0;
        try {
            reader.beginObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            while (reader.hasNext()) {
                String field = reader.nextName();
                if (field.equals("tweetId")) {
                    tweetId = reader.nextString();
                } else if (field.equals("name")) {
                    name = reader.nextString();
                } else if (field.equals("location")) {
                    location = reader.nextString();
                } else if (field.equals("tweet")) {
                    tweet = reader.nextString();
                } else if (field.equals("lat")) {
                    lat = reader.nextDouble();
                } else if (field.equals("lng")) {
                    lng = reader.nextDouble();
                } else {
                    reader.skipValue();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            reader.endObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new TweetModel(tweetId, name, location, tweet, lat, lng);
    }
}
