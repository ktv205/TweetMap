package com.example.krishnateja.tweetmap;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.krishnateja.tweetmap.R;
import com.example.krishnateja.tweetmap.models.TweetModel;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by krishnateja on 12/7/2014.
 */
public class TweetMap extends Activity  {
    BroadcastReceiver mReceiver;
    MapView mapView;
    GoogleMap myMap;
    private String tweet,name;
    private double lat,lng;
    private static final String TAG="TweetMap";
    IntentFilter intentFilter;
    ArrayList<TweetModel> tweetModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate");
        setContentView(R.layout.activity_tweetmap);
        mapView=(MapView)findViewById(R.id.tweetmap_map_view);
        mapView.onCreate(savedInstanceState);
        intentFilter= new IntentFilter("com.google.android.c2dm.intent.RECEIVE");
        intentFilter.addCategory("com.example.krishnateja.tweetmap");


    }
    public void setMarkersAndTweets(double lat,double lng,String tweet, final String name){
            myMap.addMarker(new MarkerOptions()
                    .position(new LatLng(lat,lng))
                    .title(name)
                    .snippet(tweet));
            myMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    Toast.makeText(TweetMap.this,marker.getTitle(),Toast.LENGTH_SHORT).show();
                    Intent intent = null;
                    try {
                        // get the Twitter app if possible
                        TweetMap.this.getPackageManager().getPackageInfo("com.twitter.android"+name, 0);
                        intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://"));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    } catch (Exception e) {
                        // no Twitter app, revert to browser
                        intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/"+name));
                    }
                    startActivity(intent);
                }
            });
        }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        myMap=mapView.getMap();
        myMap.setMyLocationEnabled(true);
        mReceiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("received","received");
                String message=intent.getExtras().getString("default");
                Log.d("message","message->"+message);
                JSONObject obj=null;
                try {
                    obj=new JSONObject(message);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    lat=Double.parseDouble(obj.getString("latitude"));
                    lng=Double.parseDouble(obj.getString("longitude"));
                    tweet=obj.getString("tweet");
                    name=obj.getString("userScreenName");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setMarkersAndTweets(lat,lng,tweet,name);
            }
        };

        this.registerReceiver(mReceiver,intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        //this.unregisterReceiver(mReceiver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.to_keywords){
            startActivity(new Intent(this,KeyWords.class));
        }
        return true;
    }

    //    @Override
//    public void onReceiveResult(int resultCode, Bundle resultData) {
//        Log.d(TAG,"here");
//    }
}
