package com.atreid.expresslanesimages.fragments;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.atreid.expresslanesimages.ExpressLanesStatusRetriever;
import com.atreid.expresslanesimages.R;
import com.atreid.expresslanesimages.loaders.GetHOVDirection;
import com.atreid.expresslanesimages.loaders.HttpClient;
import com.atreid.expresslanesimages.loaders.HttpResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HistoricRatesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HistoricRatesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TripCostFragment extends FormBaseFragment {

    private OnFragmentInteractionListener mListener;
    public TripCostFragment() {
        // Required empty public constructor
    }

    private static TextView response;
    private static TextView rate495;
    private static TextView rate95;
    private static Button submitButton;
    private static ExpressLanesStatusRetriever expressLanesStatusRetriever;
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param entry_exit Set of north and southbound entries and exits
     * @return A new instance of fragment HistoricRatesFragment.
     */
    public static TripCostFragment newInstance(JSONObject entry_exit) {
        TripCostFragment fragment = new TripCostFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ENTRY_EXIT, entry_exit.toString());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            new GETHOVDirectionFirst().execute();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_trip_cost, container, false);
    }

    public void onViewCreated(View v, Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        response = (TextView)v.findViewById(R.id.response);
        rate495 = (TextView)v.findViewById(R.id.rate495);
        rate95 = (TextView)v.findViewById(R.id.rate95);

        submitButton = (Button) v.findViewById(R.id.trip_cost_submit_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                response.setText("");
                if (!isValidForm()) {
                    return;
                }
                try {
                    JSONArray odsArray = exitIdToODSArrayMap.get(exitObject.getString("id"));
                    String ods; // = odsArray.get(0).toString();
                    String url = "https://www.expresslanes.com/on-the-road-api?";
                    for (int i = 0; i < odsArray.length(); i++) {
                        url += ((i > 0) ? "&" : "") + "ods%5B%5D=" + odsArray.get(i);
                    }
                    ConnectivityManager connMgr = (ConnectivityManager)
                            getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                    if (networkInfo != null && networkInfo.isConnected()) {
                        new DownloadTripCostResponseTask().execute(url);
                    } else {
                        response.setText("No network connection available.");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private static class GETHOVDirectionFirst extends GetHOVDirection {

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }


    private static class DownloadTripCostResponseTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {
            // params comes from the execute() call: params[0] is the url.
            try {
                HttpClient httpClient = new HttpClient();
                HttpResponse httpResponse = httpClient.makeHttpRequest(
                        url[0],
                        "GET",
                        "application/json",
                        null,
                        null
                );
                Log.d("serviceResponse: ", httpResponse.toString());
                return httpResponse.getResponseBody();

            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject responseObject = new JSONObject(result);
                JSONArray rates = responseObject.getJSONArray("rates");
                JSONObject rateObject;
                String rate, road, direction, duration;
                String rateSummary;
                TextView cursor;
                rate495.setText("n/A");
                rate95.setText("n/A");
                for (int i = 0; i < rates.length(); i++) {
                    rateObject = new JSONObject(rates.get(i).toString());
                    rate = rateObject.getString("rate");
                    road = rateObject.getString("road");
                    direction = rateObject.getString("direction");
                    duration = rateObject.getString("duration");
                    rateSummary = "$" + rate + " on " + road + direction + " " + duration + "m";
                    if ("495".equals(road)) {
                        cursor = rate495;
                    } else {
                        expressLanesStatusRetriever.updateStatus();
                        if (ExpressLanesStatusRetriever.CLOSED.equals(
                                expressLanesStatusRetriever.getDirection())) {
                            rateSummary = "Closed";
                        }
                        cursor = rate95;
                    }
                    cursor.setText(rateSummary);
                }

            } catch (JSONException e) {
                response.setText(result);
                e.printStackTrace();
            }
            Log.d("serviceResponse: ", result);
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}

