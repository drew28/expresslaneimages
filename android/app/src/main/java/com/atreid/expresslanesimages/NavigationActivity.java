package com.atreid.expresslanesimages;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.atreid.expresslanesimages.fragments.DynamicSignsFragment;
import com.atreid.expresslanesimages.fragments.HistoricRatesFragment;
import com.atreid.expresslanesimages.fragments.TripCostFragment;
import com.atreid.expresslanesimages.loaders.HOVDirectionRetriever;
import com.atreid.expresslanesimages.loaders.JSONLoader;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class NavigationActivity extends AppCompatActivity
        implements DynamicSignsFragment.OnFragmentInteractionListener,
        HistoricRatesFragment.OnFragmentInteractionListener,
        TripCostFragment.OnFragmentInteractionListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    private AdView mAdView;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        new GETHOVDirectionFirst(this).execute();

        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(this, "ca-app-pub-7550332846806881~6420624117");

        // Gets the ad view defined in layout/ad_fragment.xml with ad unit ID set in
        // values/strings.xml.
        mAdView = (AdView) findViewById(R.id.ad_view);

        // Create an ad request. Check your logcat output for the hashed device ID to
        // get test ads on a physical device. e.g.
        // "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("A5E34E659E0D815706392732A74C2A01")
                .build();

        // Start loading the ad in the background.
        mAdView.loadAd(adRequest);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_navigation, menu);
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

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public class GETHOVDirectionFirst extends HOVDirectionRetriever {

        private Activity activity;

        public GETHOVDirectionFirst(Activity activity) {
            this.activity = activity;
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            //        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
            // Create the adapter that will return a fragment for each of the three
            // primary sections of the activity.
            try {
                mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), direction);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // Set up the ViewPager with the sections adapter.
            mViewPager = (ViewPager) findViewById(R.id.container);
            mViewPager.setAdapter(mSectionsPagerAdapter);

            TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
            tabLayout.setupWithViewPager(mViewPager);
        }

    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        private JSONObject image_schema;
        private JSONObject entry_exit;
        private String direction;

        public SectionsPagerAdapter(FragmentManager fm, String direction) throws JSONException {
            super(fm);
            this.direction = direction;
            Context context = getBaseContext();
            Resources resources = context.getResources();
            image_schema = (new JSONLoader(resources.getString(R.string.json_images), context)).loadJSON();
            entry_exit = (new JSONLoader(resources.getString(R.string.json_entry_exit), context)).loadJSON();
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            if (position == 0) {
                try {
                    return DynamicSignsFragment.newInstance(image_schema.getJSONArray("images"), direction);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (position == 1) {
                return TripCostFragment.newInstance(entry_exit, direction);
            }
            return HistoricRatesFragment.newInstance(entry_exit);

        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Dynamic Signs";
                case 1:
                    return "Trip Cost Estimator";
                case 2:
                    return "Historic Rates";
            }
            return null;
        }
    }
}
