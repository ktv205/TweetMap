package com.example.krishnateja.tweetmap;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.krishnateja.tweetmap.models.KeyWordsModel;
import com.example.krishnateja.tweetmap.models.RequestPackage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class SearchResultsActivity extends Activity {
    private ListView list;
    private ArrayAdapter<String> arrayAdapter;
    private final static String DEBUG = "SearchResultsActivity";
    private RequestPackage requestPackage;
    private List<KeyWordsModel> globalKeyWordsModelList = new ArrayList<KeyWordsModel>();
    private static int size;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        list = (ListView) findViewById(R.id.list_view_search);
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);

    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            setRequestParams(query);
            new SearchAsycTask().execute(requestPackage);
        }
    }

    public void setRequestParams(String s) {
        requestPackage = new RequestPackage();
        requestPackage.setParam("key", "search");
        requestPackage.setParam("keyword", s);
        requestPackage.setMethod("POST");
        requestPackage.setURI("http://54.173.51.136/tweetmap/tweets.php");
    }

    private void makeRequest(String s) {
        RequestPackage rp = new RequestPackage();
        rp.setURI("http://54.173.51.136/tweetmap/tweets_keyword.php");
        rp.setParam("keyword", s);
        rp.setParam("key", "tweets");
        rp.setMethod("POST");
        new GetTweetsAsyncTask(this).execute(rp);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    public class SearchAsycTask extends AsyncTask<RequestPackage, Integer, List<KeyWordsModel>> {
        @Override
        protected List<KeyWordsModel> doInBackground(RequestPackage... params) {
            return new RemoteConnection().getDataKeywords(params[0]);
        }

        @Override
        protected void onPostExecute(List<KeyWordsModel> keyWordsModels) {
            globalKeyWordsModelList = keyWordsModels;
            size = globalKeyWordsModelList.size();
            list.setAdapter(new KeywordsAdapter(SearchResultsActivity.this, size, globalKeyWordsModelList));
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    TextView keywordClicked=(TextView)view.findViewById(R.id.keyword_text);
                    makeRequest(keywordClicked.getText().toString());
                }
            });
        }
    }
}
