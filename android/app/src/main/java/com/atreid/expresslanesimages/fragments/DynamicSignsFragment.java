package com.atreid.expresslanesimages.fragments;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.atreid.expresslanesimages.ExpressLaneImage;
import com.atreid.expresslanesimages.R;
import com.atreid.expresslanesimages.adapters.ExpressLaneImageAdapter;
import com.atreid.expresslanesimages.loaders.HOVDirectionRetriever;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DynamicSignsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DynamicSignsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DynamicSignsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_IMAGES = "images";
    private static final String ARG_DIRECTION = "directions";

    private static SwipeRefreshLayout swiperefresh;
    private static Handler handler = new Handler();

    // TODO: Rename and change types of parameters
    private static JSONArray mImages;
    private static String mDirection;
    private static String direction;
    private static String status;
    private static ListView listView1;
    private static ArrayList<ExpressLaneImage> expressLaneImageData;
    private static ExpressLaneImageAdapter adapter;
    private static OnFragmentInteractionListener mListener;

    public static final String NORTH = "north";
    public static final String SOUTH = "south";
    public static final String CLOSED = "closed";

    public DynamicSignsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param images JSON Array of images.
     * @return A new instance of fragment DynamicSignsFragment.
     */
    public static DynamicSignsFragment newInstance(JSONArray images, String direction) {
        DynamicSignsFragment fragment = new DynamicSignsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_IMAGES, images.toString());
        args.putString(ARG_DIRECTION, direction);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            try {
                mImages = new JSONArray(getArguments().getString(ARG_IMAGES));
                mDirection  = getArguments().getString(ARG_DIRECTION);
                expressLaneImageData = new ArrayList<>();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dynamic_signs, container, false);
    }

    public void onViewCreated(View v, Bundle savedInstanceState) {
        try {
            JSONObject eli;
            String url, description, route, eliDirection;
            for (int i = 0; i < mImages.length(); i++) {
                // get first object from JSON
                eli = mImages.getJSONObject(i);
                url = eli.getString("url");
                description = eli.getString("description");
                route = eli.getString("route");
                eliDirection = eli.getString("direction");
                //always add images from 495, only add relevant signs from 95
                if (("495").equals(route)) {
                    expressLaneImageData.add(new ExpressLaneImage(url, description, route, mDirection));
                } else if (("95").equals(route)) {
                    if (eliDirection.equals(mDirection)) {
                        expressLaneImageData.add(new ExpressLaneImage(url, description, route, mDirection));
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // find the layout
        swiperefresh = (SwipeRefreshLayout) v.findViewById(R.id.swiperefresh);
        // the refresh listener. this would be called when the layout is pulled down
        swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                // get the new data from you data source
                adapter.invalidateImages();
                adapter.notifyDataSetChanged();
                swiperefresh.setRefreshing(false);
                // handler.post(refreshing);
            }
        });
        // sets the colors used in the refresh animation
        swiperefresh.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary,
                R.color.colorPrimaryDark, R.color.colorPrimary);

        TextView statusTextView = (TextView)v.findViewById(R.id.expressLanesStatus);
        String openOrClosed = "";
        if (SOUTH.equals(mDirection) || NORTH.equals(mDirection)){
            openOrClosed = "Open going " + mDirection;
        } else {
            openOrClosed = "Closed.";
        }
        String statusCopy = "95 Express Lanes status: " + openOrClosed; //status;
        statusTextView.setText(statusCopy);
        adapter = new ExpressLaneImageAdapter(getActivity(),
                R.layout.listview_item_row, expressLaneImageData.toArray(new ExpressLaneImage[expressLaneImageData.size()]));
        listView1 = (ListView) v.findViewById(R.id.listView1);
//        View header = (View) getActivity().getLayoutInflater().inflate(R.layout.listview_header_row, null);
//        listView1.addHeaderView(header);

        listView1.setAdapter(adapter);
        listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //get layout of row
//                    LinearLayout linearLayout = ((LinearLayout)view);
//                    TextView textView = (TextView)linearLayout.findViewById(R.id.txtTitle);
//                    String item = textView.getText().toString();

                //get row data
//                if (position > 0) {
                String item = position + ". " + parent.getItemAtPosition(position);
                Toast.makeText(view.getContext(), item, Toast.LENGTH_SHORT).show();
//                }
            }
        });
        listView1.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                boolean enable = false;
                if (listView1 != null && listView1.getChildCount() > 0) {
                    // check if the first item of the list is visible
                    boolean firstItemVisible = listView1.getFirstVisiblePosition() == 0;
                    // check if the top of the first item is visible
                    boolean topOfFirstItemVisible = listView1.getChildAt(0).getTop() == 0;
                    // enabling or disabling the refresh layout
                    enable = firstItemVisible && topOfFirstItemVisible;
                }
                swiperefresh.setEnabled(enable);
            }
        });
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

    @Override
    public void onRefresh() {

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
