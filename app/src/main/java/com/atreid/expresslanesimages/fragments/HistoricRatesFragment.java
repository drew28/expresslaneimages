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
import android.widget.AdapterView;
import android.widget.TextView;

import com.atreid.expresslanesimages.R;
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
public class HistoricRatesFragment extends FormBaseFragment {

    private OnFragmentInteractionListener mListener;

    public HistoricRatesFragment() {
        // Required empty public constructor
    }

    private static TextView response;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param entry_exit Set of north and southbound entries and exits
     * @return A new instance of fragment HistoricRatesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HistoricRatesFragment newInstance(JSONObject entry_exit) {
        HistoricRatesFragment fragment = new HistoricRatesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ENTRY_EXIT, entry_exit.toString());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_historic_rates, container, false);
    }

    public void onViewCreated(View v, Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        response = (TextView)v.findViewById(R.id.response);
        exit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    String exitSelected = parent.getItemAtPosition(position).toString();
                    exitObject = exitLabelToExitObjectMap.get(exitSelected);
                    response.setText("");
                    try {
                        JSONArray odsArray = exitIdToODSArrayMap.get(exitObject.getString("id"));
                        String ods; // = odsArray.get(0).toString();
                        String url = "https://www.expresslanes.com/historic-rate-data";
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
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private static class DownloadTripCostResponseTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {
            // params comes from the execute() call: params[0] is the url.
            try {
                HttpClient httpClient = new HttpClient();
                HttpResponse httpResponse = httpClient.makeHttpRequest(
                        url[0],
                        "POST",
                        "application/x-www-form-urlencoded",
                        null,
                        "" +
                                "date=03%2F11%2F2016+03%3A00+PM" +
                                "&direction=" + directionSelected +
                                "&origin=" + entryObject.getString("id") +
                                "&destination=" + exitObject.getString("id")
                );
                Log.d("serviceResponse: ", httpResponse.toString());
                return httpResponse.getResponseBody();

            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            } catch (JSONException e) {
                return "There was a problem parsing JSON.";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            response.setText(result);
            try {
                JSONObject jsonObject = new JSONObject(result);
                String content = jsonObject.getString("content").trim();
                content = content.replaceAll("\\<.*?>|\\t", "")
                        .replaceAll("\\s+", " ")
                        .replaceAll("was:", "was:\r\n")
                        .replaceAll("495 E", "\r\n495 E")
                        .replaceAll(" 95 E", " \r\n95 E")
                        .replaceAll("Please note", "\r\n\r\nPlease Note");
                response.setText(content);
                Log.d("serviceResponse: ", result);
                Log.d("content: ", content);
            } catch (JSONException e) {
                e.printStackTrace();
            }
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

