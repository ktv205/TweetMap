package com.example.krishnateja.tweetmap;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.krishnateja.tweetmap.models.KeyWordsModel;
import com.example.krishnateja.tweetmap.models.RequestPackage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class KeyWords extends Activity {
    private ListView keywordsListView;
    private int number = 1;
    private List<KeyWordsModel> globalKeyWordsModelList = new ArrayList<KeyWordsModel>();
    private static int size;
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!checkForInternet()) {
            LinearLayout linear = new LinearLayout(this);
            TextView text = new TextView(this);
            text.setText("InternetConnectionUnAvailable");
            linear.addView(text);
            setContentView(linear);
        } else {
            setContentView(R.layout.activity_key_words);
            View footerView = ((LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.footer_content, null, false);
            keywordsListView = (ListView) findViewById(R.id.keyword_listview);
            keywordsListView.addFooterView(footerView);
            new getKeywordsAsyncTask().execute(setRequestPackage());

        }
    }

    public boolean checkForInternet() {
        ConnectivityManager manager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo active = manager.getActiveNetworkInfo();
        if (active != null && active.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    public RequestPackage setRequestPackage() {
        RequestPackage requestPackage = new RequestPackage();
        requestPackage.setURI("http://testappnew1-1-1-env.elasticbeanstalk.com/twittMap/tweets.php");
        requestPackage.setMethod("POST");
        requestPackage.setParam("key", "keywords");
        requestPackage.setParam("number", String.valueOf(number));
        requestPackage.setFlag(0);
        Log.d("RequestPackage-KeyWordsActivity", requestPackage.getEncodedParams());
        number++;
        return requestPackage;

    }

    private boolean handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            ArrayAdapter arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new String[]{query});
            keywordsListView.setAdapter(arrayAdapter);
            keywordsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                }
            });
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void startActivity(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
        }
        super.startActivity(intent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_key_words, menu);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class getKeywordsAsyncTask extends AsyncTask<RequestPackage, String, List<KeyWordsModel>> {
        ProgressDialog progress;
        @Override
        protected void onPreExecute() {
            progress = new ProgressDialog(KeyWords.this);
            progress.setTitle("Loading");
            progress.setMessage("Wait while loading...");
            progress.show();
        }

        @Override
        protected List<KeyWordsModel> doInBackground(RequestPackage... params) {
            Log.d("doInBackGround-KeyWordsActivity", "here");
            return new RemoteConnection().getDataKeywords(params[0]);
        }

        @Override
        protected void onPostExecute(List<KeyWordsModel> s) {
                progress.dismiss();
            Log.d("onPostExecute-KeyWordsActivity", s.toString());
            for (int i = 0; i < s.size(); i++) {
                globalKeyWordsModelList.add(s.get(i));
            }
            size = globalKeyWordsModelList.size();
            keywordsListView.setAdapter(new KeywordsAdapter(KeyWords.this, size, globalKeyWordsModelList));
            if (number > 1) {
                keywordsListView.setSelection(size - 200);
            }
            keywordsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    View id1 = view.findViewById(R.id.footer_1);
                    if (id1 != null) {
                        if (id1.getId() == R.id.footer_1) {
                            reLoad();
                        }
                    }
                    if (id1 == null) {
                        TextView keywordClicked = (TextView) view.findViewById(R.id.keyword_text);
                        makeRequest(keywordClicked.getText().toString());
                    }
                    //TextView keywordClicked = (TextView) view.findViewById(android.R.id.text1);
                    //makeRequest(keywordClicked.getText().toString());
                }
            });
        }

        private void reLoad() {
            Log.d("here in reload", "do something");
            new getKeywordsAsyncTask().execute(setRequestPackage());
        }
    }

    private void makeRequest(String s) {
        RequestPackage rp = new RequestPackage();
        rp.setURI("http://54.173.51.136/tweetmap/tweets_keyword.php");
        rp.setParam("key", "tweets");
        rp.setParam("keyword", s);
        rp.setMethod("POST");
        new GetTweetsAsyncTask(this).execute(rp);
    }
}
