package com.example.krishnateja.tweetmap.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by krishnateja on 11/10/2014.
 */
public class KeyWordsModel implements Parcelable {
    private String keyword;
    private int count;
    public KeyWordsModel(String keyword,int count){
        this.keyword=keyword;
        this.count=count;
    }
    public String getKeyword(){
        return keyword;
    }
    public int getCount(){
        return count;
    }

    @Override
    public int describeContents() {
        return 0;
    }
    public static final Parcelable.Creator<KeyWordsModel> CREATOR = new Parcelable.Creator<KeyWordsModel>() {
        public KeyWordsModel createFromParcel(Parcel p) {
            return new KeyWordsModel(p);
        }

        public KeyWordsModel[] newArray(int size) {
            return new KeyWordsModel[size];
        }
    };
    private KeyWordsModel(Parcel p){
     keyword=p.readString();
     count=p.readInt();
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(keyword);
        dest.writeInt(count);

    }
}

