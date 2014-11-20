package com.example.krishnateja.tweetmap.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by krishnateja on 11/10/2014.
 */
public class TweetList implements Parcelable {
    List<TweetModel> tweetModelList=new ArrayList();
    @Override
    public int describeContents() {
        return 0;
    }
    public TweetList(List tweetModelList){
        this.tweetModelList=tweetModelList;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(tweetModelList);
    }
    public static final Parcelable.Creator<TweetList> CREATOR=new Parcelable.Creator<TweetList>(){

        @Override
        public TweetList createFromParcel(Parcel source) {
            return new TweetList(source);
        }

        @Override
        public TweetList[] newArray(int size) {
            return new TweetList[size];
        }
    };
    public TweetList(Parcel p){
        tweetModelList=p.readArrayList(TweetModel.class.getClassLoader());
    }

    public List<TweetModel> getTweetModelList() {
        return tweetModelList;
    }
}
