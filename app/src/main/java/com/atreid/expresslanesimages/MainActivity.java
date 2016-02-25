package com.atreid.expresslanesimages;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView imageView = (ImageView) findViewById(R.id.imageView1);
        Picasso.with(this)
                .load("https://www.expresslanes.com/generate-dms-sign.php?tmsName=DMS-LH-E-0.1-1&direction=S&road=495&type=entry")
                .resize(203*5, 210*5)
                .into(imageView);
        imageView = (ImageView) findViewById(R.id.imageView2);
        Picasso.with(this)
                .load("https://www.expresslanes.com/generate-dms-sign.php?tmsName=DMS-495-N-55.6&direction=N&road=495&type=entry")
                .resize(203*5, 210*5)
                .into(imageView);
        // https://www.expresslanes.com/generate-dms-sign.php?tmsName=DMS-LH-E-0.1-1&direction=S&road=495&type=entry
        // https://www.expresslanes.com/generate-dms-sign.php?tmsName=DMS-495-N-55.6&direction=N&road=495&type=entry
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
