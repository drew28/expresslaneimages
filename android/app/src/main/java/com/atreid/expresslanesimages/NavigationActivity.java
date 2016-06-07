package com.atreid.expresslanesimages;

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
import com.atreid.expresslanesimages.loaders.JSONLoader;

import org.json.JSONException;
import org.json.JSONObject;

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

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        try {
            mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

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

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        private JSONObject image_schema;
        private JSONObject entry_exit;

        public SectionsPagerAdapter(FragmentManager fm) throws JSONException {
            super(fm);
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
                    return DynamicSignsFragment.newInstance(image_schema.getJSONArray("images"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (position == 1) {
                return TripCostFragment.newInstance(entry_exit);
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
