package com.atreid.expresslanesimages;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
public class DynamicSignsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_IMAGES = "images";
    private static final String ARG_DIRECTION = "direction";
    private static final String ARG_STATUS = "status";

    // TODO: Rename and change types of parameters
    private JSONArray mImages;
    private String mDirection;
    private String mStatus;
    private ArrayList<ExpressLaneImage> expressLaneImageData;

    private OnFragmentInteractionListener mListener;

    public DynamicSignsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param images JSON Array of images.
     * @param direction Direction of the flow of traffic on 95 express lanes
     * @return A new instance of fragment DynamicSignsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DynamicSignsFragment newInstance(JSONArray images, String direction, String status) {
        DynamicSignsFragment fragment = new DynamicSignsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_IMAGES, images.toString());
        args.putString(ARG_DIRECTION, direction);
        args.putString(ARG_STATUS, status);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            try {
                mImages = new JSONArray(getArguments().getString(ARG_IMAGES));
                mDirection = getArguments().getString(ARG_DIRECTION);
                mStatus = getArguments().getString(ARG_STATUS);
                JSONObject eli;
                String url, description, route, direction;
                expressLaneImageData = new ArrayList<>();
                for (int i = 0; i < mImages.length(); i++) {
                    // get first object from JSON
                    eli = mImages.getJSONObject(i);
                    url = eli.getString("url");
                    description = eli.getString("description");
                    route = eli.getString("route");
                    direction = eli.getString("direction");
                    //always add images from 495, only add relevant signs from 95
                    if (("495").equals(route)) {
                        expressLaneImageData.add(new ExpressLaneImage(url, description, route, direction));
                    } else if (("95").equals(route)) {
                        if (direction.equals(mDirection)) {
                            expressLaneImageData.add(new ExpressLaneImage(url, description, route, direction));
                        }
                    }
                }
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
        TextView statusTextView = (TextView)v.findViewById(R.id.expressLanesStatus);
        statusTextView.setText("95 Express Lanes status: " + mStatus);
        ExpressLaneImageAdapter adapter = new ExpressLaneImageAdapter(getActivity(),
                R.layout.listview_item_row, expressLaneImageData.toArray(new ExpressLaneImage[expressLaneImageData.size()]));
        ListView listView1 = (ListView) v.findViewById(R.id.listView1);
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
