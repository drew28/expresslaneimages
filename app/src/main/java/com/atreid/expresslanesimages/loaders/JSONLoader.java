package com.atreid.expresslanesimages.loaders;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by atrei_000 on 3/4/2016.
 */
public class JSONLoader  {
    public JSONObject obj;
    private String jsonFile;
    private Context context;

    public JSONLoader(String jsonFile, Context context) {
        this.jsonFile = jsonFile;
        this.context = context;
    }

    public JSONObject loadJSON() throws JSONException {
        JSONObject obj = new JSONObject(loadJSONFromAsset());
        return obj;
    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = context.getAssets().open(jsonFile);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
