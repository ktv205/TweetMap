package com.example.krishnateja.tweetmap;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.krishnateja.tweetmap.models.KeyWordsModel;
import com.example.krishnateja.tweetmap.models.RequestPackage;
import com.example.krishnateja.tweetmap.models.TweetModel;
import com.example.krishnateja.tweetmap.models.TweetmapPreferences;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;


public class KeyWords extends Activity {
    private ListView keywordsListView;
    private int number = 0;
    private List<KeyWordsModel> globalKeyWordsModelList = new ArrayList<KeyWordsModel>();
    private static int size;
    private ProgressDialog progress;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private final static String TAG = "KeyWords";
    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    String SENDER_ID = "450808485242";
    GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();
    SharedPreferences prefs;
    String regid;
    Context context;

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
            gcmImp();
            setContentView(R.layout.activity_key_words);
            View footerView = ((LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.footer_content, null, false);
            View loadingFooterView= ((LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.loading_footer, null, false);
            keywordsListView = (ListView) findViewById(R.id.keyword_listview);
            keywordsListView.addFooterView(loadingFooterView);
            new getKeywordsAsyncTask().execute(setRequestPackage());

        }
    }
    public void gcmImp(){
        context=getApplicationContext();
        Log.d(TAG,"in gcmImp");
        if (checkPlayServices()) {
            Log.d(TAG, "playServices available in gcmImp");
            gcm = GoogleCloudMessaging.getInstance(this);
            regid = getRegistrationId(context);
            Log.d(TAG,"registration id if already registered->"+regid);
            if (regid.isEmpty()) {
                registerInBackground();
            }
        } else {
            Log.d(TAG, "playServices not available in onCreate");
        }
      // new sendRegistrationIdAsyncTask().execute();
    }
    public boolean checkPlayServices() {
        Log.d(TAG, "in checkPlayServices");
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        } else {
            return true;
        }
    }
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.d(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION,
                Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.d(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }
    private SharedPreferences getGCMPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences,
        // but
        // how you store the regID in your app is up to you.
        Log.d(TAG, "in getGCMPreferences(Context context)");
        return getSharedPreferences(KeyWords.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }
    private static int getAppVersion(Context context) {
        Log.d(TAG, "getAppVersion");
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            Log.d(TAG, "getAppVersion->" + packageInfo.versionCode);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }
    private void registerInBackground() {
        Log.d(TAG,"registerInBackground");
        new RegisterAsyncTask().execute();
    }
    class RegisterAsyncTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            Log.d(TAG,"doInBackground");
            String msg = "";
            try {
                if (gcm == null) {
                    gcm = GoogleCloudMessaging.getInstance(context);
                }
                regid = gcm.register(SENDER_ID);
                msg = "Device registered, registration ID=" + regid;
                Log.d(TAG,"msg->"+msg);

                // You should send the registration ID to your server over HTTP,
                // so it can use GCM/HTTP or CCS to send messages to your app.
                // The request to your server should be authenticated if your
                // app
                // is using accounts.
                sendRegistrationIdToBackend();

                // For this demo: we don't need to send it because the device
                // will send upstream messages to a server that echo back the
                // message using the 'from' address in the message.

                // Persist the regID - no need to register again.
                storeRegistrationId(context, regid);
            } catch (IOException ex) {
                msg = "Error :" + ex.getMessage();
                // If there is an error, don't just keep trying to register.
                // Require the user to click a button again, or perform
                // exponential back-off.
            }
            return msg;
        }

        @Override
        protected void onPostExecute(String msg) {
            Toast.makeText(KeyWords.this,msg,Toast.LENGTH_LONG).show();
        }

    }

    /**
     * Sends the registration ID to your server over HTTP, so it can use
     * GCM/HTTP or CCS to send messages to your app. Not needed for this demo
     * since the device sends upstream messages to a server that echoes back the
     * message using the 'from' address in the message.
     */
    private void sendRegistrationIdToBackend() {
        // Your implementation here.
    }

    /**
     * Stores the registration ID and app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context
     *            application's context.
     * @param regId
     *            registration ID
     */
    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }
    class sendRegistrationIdAsyncTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... params) {
            URL url=null;
            try {
                url=new URL("http://"+TweetmapPreferences.ipAdd+"/tweetmap/register_device.php"+"?id="+regid);
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            HttpURLConnection con=null;
            try {
                con=(HttpURLConnection) url.openConnection();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            con.setDoOutput(true);
            try {
                con.setRequestMethod("GET");
            } catch (ProtocolException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            String line=null;
            StringBuilder builder=new StringBuilder();
            InputStreamReader iReader=null;
            BufferedReader bReader=null;
            try {
                iReader=new InputStreamReader(con.getInputStream());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            bReader=new BufferedReader(iReader);
            try {
                while((line=bReader.readLine())!=null){
                    builder.append(line);
                }
                Log.d(TAG,"builder->"+builder.toString());
                return null;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return null;
            }
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
        requestPackage.setURI("http://"+ TweetmapPreferences.ipAdd+"/tweetmap/tweets.php");
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
        //ProgressDialog progress;
        @Override
        protected void onPreExecute() {
            //progress = new ProgressDialog(KeyWords.this);
            //progress.setTitle("Loading");
            //progress.setMessage("Wait while loading...");
            //progress.show();
        }

        @Override
        protected List<KeyWordsModel> doInBackground(RequestPackage... params) {
            Log.d("doInBackGround-KeyWordsActivity", "here");
            return new RemoteConnection().getDataKeywords(params[0]);
        }

        @Override
        protected void onPostExecute(List<KeyWordsModel> s) {
                //progress.dismiss();
            Log.d("onPostExecute-KeyWordsActivity", s.toString());
            for (int i = 0; i < s.size(); i++) {
                globalKeyWordsModelList.add(s.get(i));
            }
            size = globalKeyWordsModelList.size();
            keywordsListView.setAdapter(new KeywordsAdapter(KeyWords.this, size, globalKeyWordsModelList));
            if (number > 1) {
                keywordsListView.setSelection(size - 2000);
            }
            keywordsListView.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {

                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                       if(keywordsListView.getLastVisiblePosition()==totalItemCount-1){
                           reLoad();
                       }

                }
            });
            keywordsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    View id1 = view.findViewById(R.id.footer_1);
                    if (id1 != null) {
                        if (id1.getId() == R.id.footer_1) {

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
        rp.setURI("http://"+ TweetmapPreferences.ipAdd+"/tweetmap/tweets_keyword.php");
        rp.setParam("key", "tweets");
        TweetmapPreferences.number=0;
        TweetmapPreferences.flagGlobal=0;
        //TweetmapPreferences.tweetModelListGlobal=null;
        TweetmapPreferences.tweetModelListGlobal=new ArrayList<TweetModel>();
        TweetmapPreferences.keyword=s;
        rp.setParam("keyword", TweetmapPreferences.keyword);
        rp.setParam("number",String.valueOf(TweetmapPreferences.number));
        TweetmapPreferences.number++;
        rp.setMethod("POST");
        new GetTweetsAsyncTask(this).execute(rp);
    }
}
