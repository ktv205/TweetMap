package com.example.krishnateja.tweetmap;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.krishnateja.tweetmap.models.TweetList;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by krishnateja on 11/6/2014.
 */
public class MapViewFragment extends Fragment {
    MapView mapView;
    GoogleMap myMap;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_map_view,container,false);
        mapView=(MapView)view.findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        myMap=mapView.getMap();
        myMap.setMyLocationEnabled(true);
        Bundle bundle=new Bundle();
        bundle=getArguments();
        if(bundle.getParcelable("PARCELTOMAPFRAGMENT")!=null){
            setMarkersAndTweets(bundle);
        }
    }
    public void setMarkersAndTweets(Bundle bundle){
        TweetList mapTweetList=(TweetList)bundle.getParcelable("PARCELTOMAPFRAGMENT");
        Toast.makeText(getActivity(), mapTweetList.getTweetModelList().size()+"<-",Toast.LENGTH_LONG).show();
        for(int i=0;i<mapTweetList.getTweetModelList().size();i++){
            myMap.addMarker(new MarkerOptions()
                    .position(new LatLng(mapTweetList.getTweetModelList().get(i).getLat()
                            ,mapTweetList.getTweetModelList().get(i).getLng() ))
                    .title(mapTweetList.getTweetModelList().get(i).getName()))
                    .setSnippet(mapTweetList.getTweetModelList().get(i).getTweet());
            myMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    Toast.makeText(getActivity(),marker.getTitle(),Toast.LENGTH_SHORT).show();
                    Intent intent = null;
                    try {
                        // get the Twitter app if possible
                        getActivity().getPackageManager().getPackageInfo("com.twitter.android", 0);
                        intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://"));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    } catch (Exception e) {
                        // no Twitter app, revert to browser
                        intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com"));
                    }
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
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
}
