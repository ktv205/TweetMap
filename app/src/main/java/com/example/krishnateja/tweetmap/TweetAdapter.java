package com.example.krishnateja.tweetmap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.krishnateja.tweetmap.models.TweetModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by krishnateja on 11/21/2014.
 */
public class TweetAdapter extends BaseAdapter {
    Context mContext;
    int size;
    List<TweetModel> list = new ArrayList<TweetModel>();

    public TweetAdapter(Context mContext, int size, List<TweetModel> list) {
        this.mContext = mContext;
        this.size = size;
        this.list = list;
    }
    @Override
    public int getCount() {
        return size;
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    public class MyHolder {
        TextView tweet, name,id;
        public MyHolder(View v) {
            tweet = (TextView) v.findViewById(R.id.actual_tweet);
            name = (TextView) v.findViewById(R.id.person_name);
            id=(TextView)v.findViewById(R.id.tweet_id);
        }

    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        MyHolder holder;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.tweet_contents, parent, false);
            holder = new MyHolder(view);
            view.setTag(holder);
        } else {
            holder = (MyHolder) view.getTag();
        }
        holder.tweet.setText(list.get(position).getTweet());
        holder.id.setText(String.valueOf(list.get(position).getTweetId()));
        holder.name.setText(list.get(position).getName());
        return view;
    }
}
