package com.example.krishnateja.tweetmap;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.krishnateja.tweetmap.models.TweetList;

/**
 * Created by krishnateja on 11/10/2014.
 */
public class TweetListFragment extends Fragment {
    ListView list;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tweet_list, container, false);
        list = (ListView) view.findViewById(R.id.listview_tweetlist);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        TweetList tweetList = bundle.getParcelable("PARCELTOLISTFRAGMENT");
        if (tweetList != null) {
            list.setAdapter(new TweetAdapter(getActivity(),tweetList.getTweetModelList().size(),tweetList.getTweetModelList()));
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                       Intent intent=new Intent();
//                    Toast.makeText(getActivity(),"clicked",Toast.LENGTH_SHORT).show();
//                    intent.setAction(Intent.ACTION_VIEW);
//                    intent.setData(Uri.parse("http://www.twitter.com"));
//                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
//                    startActivity(intent);
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

    public String[] getTweets(TweetList list) {
        String[] tweetListStrings = new String[list.getTweetModelList().size()];
        for (int i = 0; i < list.getTweetModelList().size(); i++) {
            tweetListStrings[i] = list.getTweetModelList().get(i).getTweet();
        }
        return tweetListStrings;
    }
}
