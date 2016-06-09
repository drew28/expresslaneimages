package com.atreid.expresslanesimages.loaders;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import com.atreid.expresslanesimages.ExpressLanesStatusRetriever;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by andrewreid on 6/7/16.
 */
public class HOVDirectionRetriever extends AsyncTask<String, Void, String> {

    public String direction;
    public static final String NORTH = "north";
    public static final String SOUTH = "south";
    public static final String CLOSED = "closed";

    @Override
    protected String doInBackground(String... url) {
        // params comes from the execute() call: params[0] is the url.
        try {
            HttpClient httpClient = new HttpClient();
            HttpResponse httpResponse = httpClient.makeHttpRequest(
                    "https://www.expresslanes.com/on-the-road",
                    "GET",
                    "text/html",
                    null,
                    null
            );
            return httpResponse.getResponseBody();

        } catch (IOException e) {
            return "Unable to retrieve web page. URL may be invalid.";
        }
    }

    // onPostExecute displays the results of the AsyncTask.
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        String[] lines = result.split("\n");
        String line = "";
        for (int i = 0; i < lines.length; i++) {
            line = lines[i];
            if (line.contains("direction95")) {
                // 		direction95:     'S',
                lines = line.split(":");
                line = lines[1].replaceAll("[',]","").trim();
                break;
            }
        }
        if ("S".equals(line)) {
            direction = SOUTH;
        } else if ("N".equals(line)) {
            direction = NORTH;
        } else {
            direction = CLOSED;
        }
    }

}