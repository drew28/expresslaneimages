package com.atreid.expresslanesimages;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.atreid.expresslanesimages.loaders.JSONLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;


public class MainActivity extends Activity {

    private ListView listView1;
    private JSONArray images;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ExpressLanesStatusRetriever expressLanesStatus = new ExpressLanesStatusRetriever();
        TextView statusTextView = (TextView)findViewById(R.id.expressLanesStatus);
        statusTextView.setText("The 95 Express Lanes are currently: " + expressLanesStatus.toString());

        try {
            JSONObject obj =  (new JSONLoader(getString(R.string.json_images), this)).loadJSON();
            JSONObject eli;
            String url;
            String description;
            images = obj.getJSONArray("images");
            ExpressLaneImage[] expressLaneImageData = new ExpressLaneImage[images.length()];
            for (int i = 0; i < images.length(); i++) {
                // get first object from JSON
                eli = images.getJSONObject(i);
                url = eli.getString("url");
                description = eli.getString("description");
                expressLaneImageData[i] = new ExpressLaneImage(url, description);
            }
            ExpressLaneImageAdapter adapter = new ExpressLaneImageAdapter(this,
                    R.layout.listview_item_row, expressLaneImageData);
            listView1 = (ListView) findViewById(R.id.listView1);

            View header = (View) getLayoutInflater().inflate(R.layout.listview_header_row, null);
            listView1.addHeaderView(header);

            listView1.setAdapter(adapter);
            listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //get layout of row
//                    LinearLayout linearLayout = ((LinearLayout)view);
//                    TextView textView = (TextView)linearLayout.findViewById(R.id.txtTitle);
//                    String item = textView.getText().toString();

                    //get row data
                    if (position > 0) {
                        String item = position + ". " + parent.getItemAtPosition(position);
                        Toast.makeText(getBaseContext(), item, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

