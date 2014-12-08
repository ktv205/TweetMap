package com.example.krishnateja.tweetmap;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.krishnateja.tweetmap.models.RequestPackage;
import com.example.krishnateja.tweetmap.models.TweetList;
import com.example.krishnateja.tweetmap.models.TweetmapPreferences;

/**
 * Created by krishnateja on 11/10/2014.
 */
public class TweetListFragment extends Fragment {
    ListView list;
    View footerView;
    View loadingFooterView;
    public static int loadingFlag=0;
    private static int number=0;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tweet_list, container, false);
        list = (ListView) view.findViewById(R.id.listview_tweetlist);
        footerView = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.footer_content, null, false);
        loadingFooterView=((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.loading_footer, null, false);
        list.addFooterView(loadingFooterView);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        TweetList tweetList = bundle.getParcelable("PARCELTOLISTFRAGMENT");
        if (tweetList != null) {
            loadingFlag=0;
            list.setAdapter(new TweetAdapter(getActivity(),tweetList.getTweetModelList().size(),tweetList.getTweetModelList()));
            number++;
            if(number>1){
                 list.setSelection(TweetmapPreferences.tweetModelListGlobal.size()-10);
            }
            list.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {

                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                                  if(list.getLastVisiblePosition()==totalItemCount-1){
                                      if(loadingFlag==0) {
                                          loadingFlag++;
                                          if (TweetmapPreferences.flagGlobal == 0) {
                                              Log.d("globalflag", "in tweetlistfragment->" + TweetmapPreferences.flagGlobal);
                                              reLoad();
                                          } else {
                                              Log.d("globalflag", "in tweetlistfragment->" + TweetmapPreferences.flagGlobal);
                                              list.removeFooterView(loadingFooterView);
                                              Toast.makeText(getActivity(), "tweets for this keyword are finished", Toast.LENGTH_SHORT).show();
                                          }
                                      }
                                  }
                }
            });
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                       Intent intent=new Intent();
//                    Toast.makeText(getActivity(),"clicked",Toast.LENGTH_SHORT).show();
//                    intent.setAction(Intent.ACTION_VIEW);
//                    intent.setData(Uri.parse("http://www.twitter.com"));
//                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
//                    startActivity(intent);
                    View id1 = view.findViewById(R.id.footer_1);
                    if (id1 != null) {
                        if (id1.getId() == R.id.footer_1) {
                            if(TweetmapPreferences.flagGlobal==0) {
                                Log.d("globalflag","in tweetlistfragment->"+TweetmapPreferences.flagGlobal);
                                reLoad();
                            }else{
                                Log.d("globalflag","in tweetlistfragment->"+TweetmapPreferences.flagGlobal);
                                list.removeFooterView(footerView);
                                Toast.makeText(getActivity(),"tweets for this keyword are finished",Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
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
    private void reLoad() {
        Log.d("here in reload", "do something");
        new GetTweetsAsyncTask(getActivity()).execute(setRequestPackage());
    }
    public RequestPackage setRequestPackage() {
        RequestPackage rp = new RequestPackage();
        rp.setURI("http://"+ TweetmapPreferences.ipAdd+"/tweetmap/tweets_keyword.php");
        rp.setParam("key", "tweets");
        rp.setParam("keyword", TweetmapPreferences.keyword);
        rp.setParam("number",String.valueOf(TweetmapPreferences.number));
        TweetmapPreferences.number++;
        rp.setMethod("POST");
        return rp;

    }
}
