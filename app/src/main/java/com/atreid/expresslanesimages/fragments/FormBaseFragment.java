package com.atreid.expresslanesimages.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.atreid.expresslanesimages.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.R.layout.select_dialog_item;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HistoricRatesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HistoricRatesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FormBaseFragment extends Fragment {
    protected static final String ARG_ENTRY_EXIT = "entry_exit";

    // TODO: Rename and change types of parameters
    protected JSONObject mEntry_exit;
    protected ArrayAdapter<String> northboundEntries;
    protected ArrayAdapter<String> southboundEntries;
    protected Map<String, String> entryLabelToCodeMap;
    protected Map<String, JSONObject> exitLabelToExitObjectMap;
    protected Spinner direction;
    protected Spinner entry;
    protected Spinner exit;
    protected String directionSelected;

    public FormBaseFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            try {
                mEntry_exit = new JSONObject(getArguments().getString(ARG_ENTRY_EXIT));
                entryLabelToCodeMap = new HashMap<String, String>();
                northboundEntries = getEntryAdapter("Northbound");
                southboundEntries = getEntryAdapter("Southbound");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    protected ArrayAdapter<String> getEntryAdapter(String direction) throws JSONException {
        JSONObject directionObject = mEntry_exit.getJSONObject(direction);
        JSONObject entries = directionObject.getJSONObject("entries");
        JSONObject entryObject;
        JSONArray entryCodes = entries.names();
        String label;
        List<String> list = new ArrayList<>();
        Log.d("entryCodes", entryCodes.toString());
        list.add("Choose Your Entry");
        for (int i = 0; i < entryCodes.length(); i++) {
            entryObject = entries.getJSONObject(entryCodes.get(i).toString());
            label = entryObject.getString("label");
            list.add(label);
            entryLabelToCodeMap.put(label, entryCodes.get(i).toString());
        }
        Log.d("entryLabels", list.toString());
        return new ArrayAdapter<>(getContext(), select_dialog_item, list);
    }

    protected ArrayAdapter<String> getExitAdapter(String direction, JSONArray exitCodes) throws JSONException {
        JSONObject directionObject = mEntry_exit.getJSONObject(direction);
        JSONObject exits = directionObject.getJSONObject("exits");
        JSONObject exitCodeObject;
        JSONObject exitObject;
        String exitCode;
        String label;
        List<String> list = new ArrayList<>();
        list.add("Choose Your Exit");
        exitLabelToExitObjectMap = new HashMap<>();
        for (int i = 0; i < exitCodes.length(); i++) {
            exitCodeObject = (JSONObject)exitCodes.get(i);
            exitCode = exitCodeObject.getString("id");
            Log.d("exitCode", exitCode);
            exitObject = exits.getJSONObject(exitCode);
            label = exitObject.getString("label");
            list.add(label);
            exitLabelToExitObjectMap.put(label, exitObject);
        }
        Log.d("exitLabels", list.toString());
        return new ArrayAdapter<>(getContext(), select_dialog_item, list);
    }

    public void onViewCreated(View v, Bundle savedInstanceState) {
        direction = (Spinner)v.findViewById(R.id.direction);
        direction.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    directionSelected = parent.getItemAtPosition(position).toString();
                    //Northern
                    if ("Northbound".equals(directionSelected)) {
                        entry.setAdapter(northboundEntries);
                    } else {
                        entry.setAdapter(southboundEntries);
                    }
                }
                exit.setAdapter(null);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        entry = (Spinner)v.findViewById(R.id.entry);
        entry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    Toast.makeText(parent.getContext(),
                            "OnItemSelectedListener : " + parent.getItemAtPosition(position).toString(),
                            Toast.LENGTH_SHORT).show();
                    try {
                        String selectedEntry = parent.getItemAtPosition(position).toString();
                        String entryCode = entryLabelToCodeMap.get(selectedEntry);
                        JSONObject directionObject = mEntry_exit.getJSONObject(directionSelected);
                        JSONObject entryObject = directionObject.getJSONObject("entries").getJSONObject(entryCode);
                        JSONArray exitsFromEntry = entryObject.getJSONArray("exits");
                        ArrayAdapter<String> exitAdapter = getExitAdapter(directionSelected, exitsFromEntry);
                        exit.setAdapter(exitAdapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        exit = (Spinner)v.findViewById(R.id.exit);
        exit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    Toast.makeText(parent.getContext(),
                            "OnItemSelectedListener : " + parent.getItemAtPosition(position).toString(),
                            Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
}

