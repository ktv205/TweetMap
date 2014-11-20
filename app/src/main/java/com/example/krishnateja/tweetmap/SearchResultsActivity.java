package com.example.krishnateja.tweetmap;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
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

import com.example.krishnateja.tweetmap.models.RequestPackage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class SearchResultsActivity extends Activity {
    private ListView list;
    private ArrayAdapter<String> arrayAdapter;
    private final static String DEBUG="SearchResultsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        list = (ListView) findViewById(R.id.list_view);
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
             Set<String> keywords=getSharedPreferences("KEYWORDLISTSHARED",MODE_PRIVATE)
                                  .getStringSet("KEYWORDSET",null);

            List results =new ArrayList();
            results=serachKeyWords(query,keywords);
            if (results!= null) {
                arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,results);
                list.setAdapter(arrayAdapter);
                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        TextView keywordClicked=(TextView)view.findViewById(android.R.id.text1);
                        makeRequest(keywordClicked.getText().toString());
                    }
                });
            }
        }
    }
    private void makeRequest(String s) {
        RequestPackage rp=new RequestPackage();
        rp.setURI("http://54.85.154.149/tweetmap/tweets_keyword.php");
        rp.setParam("keyword",s);
        rp.setMethod("POST");
        new GetTweetsAsyncTask(this).execute(rp);
    }

    public List serachKeyWords(String query, Set<String> keywords) {
        List result = new ArrayList();
       for(String s : keywords){
           if(s.contains(query)){
               result.add(s);
           }
       }
        return result;
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
