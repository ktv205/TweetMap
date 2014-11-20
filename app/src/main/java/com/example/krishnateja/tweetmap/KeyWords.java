package com.example.krishnateja.tweetmap;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.JsonReader;
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
    ListView keywordsListView;
    ArrayAdapter array;
    SharedPreferences keywordPreference;
    SharedPreferences.Editor keywordsEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_key_words);
        keywordsListView = (ListView) findViewById(R.id.keyword_listview);
        if (handleIntent(getIntent())) {
        } else {
            new MyAsyncTask().execute("");
        }
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

    public class MyAsyncTask extends AsyncTask<String, String, String[]> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String[] doInBackground(String... params) {
            URL url = null;
            try {
                url = new URL("http://54.85.154.149/tweetmap/keywords.php");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            HttpURLConnection con = null;
            try {
                con = (HttpURLConnection) url.openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
            InputStream in = null;
            try {
                in = con.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            StringBuilder builder = new StringBuilder();
            InputStreamReader is = new InputStreamReader(in);
            JsonReader reader = new JsonReader(is);
            List messages = new ArrayList<KeyWordsModel>();
            try {
                reader.beginArray();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                while (reader.hasNext()) {
                    messages.add(readMessage(reader));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                reader.endArray();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return StringList(messages);


        }

        public String[] StringList(List messages) {
            String[] keywords = new String[messages.size()];
            KeyWordsModel model = null;
            for (int i = 0; i < messages.size(); i++) {
                model = (KeyWordsModel) messages.get(i);
                keywords[i] = model.getKeyword();
            }
            return keywords;
        }

        public KeyWordsModel readMessage(JsonReader reader) throws IOException {
            String keyWord = null;
            int count = 0;
            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("keyword")) {
                    keyWord = reader.nextString();
                } else if (name.equals("noOfTweets")) {
                    count = reader.nextInt();
                } else {
                    reader.skipValue();
                }

            }
            reader.endObject();
            return new KeyWordsModel(keyWord, count);
        }

        @Override
        protected void onPostExecute(String[] s) {
            Set<String> mySet = new HashSet<String>(Arrays.asList(s));
            keywordPreference = getSharedPreferences("KEYWORDLISTSHARED", MODE_PRIVATE);
            keywordsEditor = keywordPreference.edit();
            keywordsEditor.putStringSet("KEYWORDSET", mySet);
            keywordsEditor.commit();
            ArrayAdapter array = new ArrayAdapter(KeyWords.this, android.R.layout.simple_list_item_1, s);
            keywordsListView.setAdapter(array);
            keywordsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    TextView keywordClicked = (TextView) view.findViewById(android.R.id.text1);
                    makeRequest(keywordClicked.getText().toString());
                }
            });
        }

    }

    private void makeRequest(String s) {
        RequestPackage rp = new RequestPackage();
        rp.setURI("http://54.85.154.149/tweetmap/tweets_keyword.php");
        rp.setParam("keyword", s);
        rp.setMethod("POST");
        new GetTweetsAsyncTask(this).execute(rp);
    }
}
