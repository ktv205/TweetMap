package com.example.krishnateja.tweetmap.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by krishnateja on 11/10/2014.
 */
public class TweetModel implements Parcelable {
  private String tweetId;
    private String name;
    private String location;
    private String tweet;
    private double lat;
    private double lng;



    public TweetModel(String tweetId, String name, String location, String tweet,double lat,double lng) {
        this.tweetId = tweetId;
        this.name = name;
        this.location = location;
        this.tweet = tweet;
        this.lat=lat;
        this.lng=lng;
    }
    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public String getTweetId() {
        return tweetId;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public String getTweet() {
        return tweet;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
         dest.writeString(tweetId);
        dest.writeString(name);
        dest.writeString(location);
        dest.writeString(tweet);
        dest.writeDouble(lat);
        dest.writeDouble(lng);
    }
    private TweetModel(Parcel in) {
        tweetId = in.readString();
        name=in.readString();
        location=in.readString();
        tweet=in.readString();
        lat=in.readDouble();
        lng=in.readDouble();

    }
    public static final Parcelable.Creator<TweetModel> CREATOR = new Parcelable.Creator<TweetModel>() {
        public TweetModel createFromParcel(Parcel p) {
            return new TweetModel(p);
        }

        public TweetModel[] newArray(int size) {
            return new TweetModel[size];
        }
    };
}
