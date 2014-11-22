package com.example.krishnateja.tweetmap;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.krishnateja.tweetmap.models.KeyWordsModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by krishnateja on 11/21/2014.
 */
public class KeywordsAdapter extends BaseAdapter {
    Context mContext;
    int size;
    List<KeyWordsModel> list = new ArrayList<KeyWordsModel>();

    public KeywordsAdapter(Context mContext, int size, List<KeyWordsModel> list) {
        this.mContext = mContext;
        this.size = size;
        this.list = list;
    }

    @Override
    public int getCount() {
        Log.d("getCount-KeywordsAdapter", size + "");
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
        TextView keyword, tweetCount;
        public MyHolder(View v) {
            keyword = (TextView) v.findViewById(R.id.keyword_text);
            tweetCount = (TextView) v.findViewById(R.id.tweets_count_text);
        }

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        MyHolder holder;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.keyword_contents, parent, false);
            holder = new MyHolder(view);
            view.setTag(holder);
        } else {
            holder = (MyHolder) view.getTag();
        }
        holder.keyword.setText(list.get(position).getKeyword());
        holder.tweetCount.setText(String.valueOf(list.get(position).getCount()));
        return view;
    }
}
