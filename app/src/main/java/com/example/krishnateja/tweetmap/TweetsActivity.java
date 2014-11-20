package com.example.krishnateja.tweetmap;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.JsonReader;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.krishnateja.tweetmap.models.TweetList;
import com.example.krishnateja.tweetmap.models.TweetModel;


public class TweetsActivity extends FragmentActivity implements DrawerLayout.DrawerListener {
    private DrawerLayout myDrawerLayout;
    private ListView listView;
    private int flag = 0;
    private final int ITEMS = 2;
    TweetList tweetList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (getIntent() != null) {
            getTweetsFromIntent(getIntent());
        }
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeAsUpIndicator(R.drawable.ic_drawer);
        myDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        listView = (ListView) findViewById(R.id.left_drawer);
        myDrawerLayout.setDrawerListener(this);
        MyViewPagerAdapter pagerAdaper = new MyViewPagerAdapter(getSupportFragmentManager());
        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setAdapter(pagerAdaper);
    }

    public void getTweetsFromIntent(Intent intent) {
        tweetList = (TweetList) intent.getParcelableExtra("PARCEL");
        if (tweetList == null) {
            Toast.makeText(this, "null", Toast.LENGTH_SHORT).show();
        } else {
        }
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Log.d("1111", "11111");
            return true;
        }

        if (id == R.id.action_search) {
            Log.d("dasdsa", "asdsad");
        }
        if (id == android.R.id.home) {
            if (myDrawerLayout.isDrawerOpen(listView)) {
                flag = 1;
                setActionBarToggle(flag);
            } else {
                flag = 0;
                setActionBarToggle(flag);

            }
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDrawerSlide(View view, float v) {

    }

    @Override
    public void onDrawerOpened(View view) {
        flag = 1;
        setActionBarToggle(flag);
    }

    @Override
    public void onDrawerClosed(View view) {
        flag = 0;
        setActionBarToggle(flag);
    }

    @Override
    public void startActivity(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
        }
        super.startActivity(intent);
    }

    @Override
    public void onDrawerStateChanged(int i) {

    }

    public void setActionBarToggle(int flag) {
        if (flag == 1) {
            getActionBar().setHomeAsUpIndicator(R.drawable.drawer_shadow);
            myDrawerLayout.closeDrawer(listView);
        } else {
            getActionBar().setHomeAsUpIndicator(R.drawable.ic_drawer);
            myDrawerLayout.openDrawer(listView);
        }
    }

    public class MyViewPagerAdapter extends FragmentStatePagerAdapter {

        public MyViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            if (i == 0) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("PARCELTOLISTFRAGMENT", tweetList);
                TweetListFragment tweetListFragment = new TweetListFragment();
                tweetListFragment.setArguments(bundle);
                return tweetListFragment;

            } else {
                Bundle bundle = new Bundle();
                bundle.putParcelable("PARCELTOMAPFRAGMENT", tweetList);
                MapViewFragment mapViewFragment = new MapViewFragment();
                mapViewFragment.setArguments(bundle);
                return mapViewFragment;
            }
        }

        @Override
        public int getCount() {
            return ITEMS;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) {
                return "tweet list";
            } else {
                return "tweet map";
            }
        }
    }


}

