package com.atreid.expresslanesimages;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.atreid.expresslanesimages.loaders.JSONLoader;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private JSONArray images;
    private Date date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        try {
            JSONObject obj =  (new JSONLoader("image_schema.json", this)).loadJSON();
            images = obj.getJSONArray("images");
            loadImages();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void clearLayout (ViewGroup layout) {
        layout.removeAllViews();
    }

    private String getDateAndTime() {
        DateFormat df = DateFormat.getDateTimeInstance();
        this.date = new Date();
        return df.format(this.date);
    }

    public void loadImages() {
        LayoutParams layoutParams = new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.mainLinearLayout);
        View line;
        JSONObject image;
        String url, description;

        // clear layout
        clearLayout(linearLayout);

        // add last updated text
        TextView textView = new TextView(this);
        textView.setLayoutParams(layoutParams);
        textView.setTypeface(null, Typeface.ITALIC);
        textView.setText("Last updated: " + getDateAndTime());
        linearLayout.addView(textView);

        try {
            // add all images and text
            for (int i = 0; i < images.length(); i++) {
                // get first object from JSON
                image = images.getJSONObject(i);
                url = image.getString("url");
                description = image.getString("description");
                // add image and text
                loadTextAndImageIntoView(url, description);
                if (i < images.length() - 1) {
                    // add separator
                    line = new View(this);
                    line.setLayoutParams(new LinearLayout.LayoutParams(
                            LayoutParams.MATCH_PARENT,
                            5
                    ));
                    line.setBackgroundColor(Color.parseColor("#B3B3B3"));
                    linearLayout.addView(line);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void loadTextAndImageIntoView(String url, String description) {
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.mainLinearLayout);
        ImageView imageView = new ImageView(this);
        imageView.setLayoutParams(layoutParams);
        layoutParams = new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        TextView textView = new TextView(this);
        textView.setLayoutParams(layoutParams);
        textView.setText(description);
        Picasso.with(this)
                .load(url) // + "&nocache=" + this.date.getTime())
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .resize(203 * 5, 210 * 5)
                .into(imageView);
        linearLayout.addView(textView);
        linearLayout.addView(imageView);
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
        if (id == R.id.action_refresh) {
            loadImages();
        }
        if (id == R.id.action_clear) {
            LinearLayout linearLayout = (LinearLayout) findViewById(R.id.mainLinearLayout);
            clearLayout(linearLayout);
        }

        return super.onOptionsItemSelected(item);
    }
}
