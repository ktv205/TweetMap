package com.example.krishnateja.tweetmap;

import android.util.JsonReader;
import android.util.Log;

import com.example.krishnateja.tweetmap.models.KeyWordsModel;
import com.example.krishnateja.tweetmap.models.RequestPackage;
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
 * Created by krishnateja on 11/21/2014.
 */
public class RemoteConnection {
    private  URL url;
    private  HttpURLConnection con;
    private InputStream in;
    private JsonReader reader;
    private OutputStreamWriter writer;
    public  List<KeyWordsModel> getDataKeywords(RequestPackage requestPackage) {
        try {
            url=new URL(requestPackage.getURI());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            con=(HttpURLConnection)url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            con.setRequestMethod(requestPackage.getMethod());
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
        con.setDoOutput(true);
        con.setDoInput(true);
        try {
            writer=new OutputStreamWriter(con.getOutputStream());
            writer.write(requestPackage.getEncodedParams());
            writer.flush();
            in=con.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            reader=new JsonReader(new InputStreamReader(in,"UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
           return readerListKeywords(reader);
    }
           public List<KeyWordsModel> readerListKeywords(JsonReader reader){
               List<KeyWordsModel> keyWordsModelList=new ArrayList<KeyWordsModel>();
               try {
                   reader.beginArray();
                   while(reader.hasNext()){
                      keyWordsModelList.add(readerObjectKeywords(reader));
                   }
               } catch (IOException e) {
                   e.printStackTrace();
               }
               try {
                   reader.endArray();
               } catch (IOException e) {
                   e.printStackTrace();
               }
               return keyWordsModelList;
           }
         public KeyWordsModel readerObjectKeywords(JsonReader reader){
             String keyWord = null;
             int count = 0;
             try {
                 reader.beginObject();
             } catch (IOException e) {
                 e.printStackTrace();
             }
             try {
                 while (reader.hasNext()) {
                     String name = reader.nextName();
                     if (name.equals("keyword")) {
                         keyWord = reader.nextString();
                     } else if (name.equals("noOfTweets")) {
                         count = reader.nextInt();
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
             return new KeyWordsModel(keyWord, count);
         }
    public  List<TweetModel> getDataTweets(RequestPackage requestPackage) {
        try {
            url=new URL(requestPackage.getURI());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            con=(HttpURLConnection)url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            con.setRequestMethod(requestPackage.getMethod());
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
        con.setDoOutput(true);
        con.setDoInput(true);
        try {
            writer=new OutputStreamWriter(con.getOutputStream());
            writer.write(requestPackage.getEncodedParams());
            writer.flush();
            in=con.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            reader=new JsonReader(new InputStreamReader(in,"UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return readerListTweets(reader);
    }
    public List<TweetModel> readerListTweets(JsonReader reader){
        List<TweetModel> tweetModelList=new ArrayList<TweetModel>();
        try {
            reader.beginArray();
            while(reader.hasNext()){
                tweetModelList.add(readerObjectTweets(reader));
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
    public TweetModel readerObjectTweets(JsonReader reader) {
        String tweetId = null, name = null, location = null, tweet = null;
        double lat = 0, lng = 0;
        Log.d("brgin object", "reader");
        try {
            reader.beginObject();
            Log.d("jsonreaderARRAY","begin object");
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
