package user.com.trafficcontroller.traffic;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;

import user.com.trafficcontroller.Login;
import user.com.trafficcontroller.MainActivity;
import user.com.trafficcontroller.PreferenceManager;
import user.com.trafficcontroller.R;
import user.com.trafficcontroller.webservices.JSONPARSE;
import user.com.trafficcontroller.webservices.RestAPI;
import user.com.trafficcontroller.webservices.Utility;


/**
 * A simple {@link Fragment} subclass.
 */
public class ManualFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "Manual";
    private Switch switchLane1, switchLane2, switchLane3, switchLane4, switchLane5;
    private TextView textLane1, textLane2, textLane3, textLane4, textLane5;
    private Button button_submit;

    private static ArrayList<String> id, name, status;
    private static TextView[] textViews;
    private static Switch[] switches;

    public ManualFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_manual, container, false);
        switchLane1 = view.findViewById(R.id.switch_lane1);
        switchLane2 = view.findViewById(R.id.switch_lane2);
        switchLane3 = view.findViewById(R.id.switch_lane3);
        switchLane4 = view.findViewById(R.id.switch_lane4);
        switchLane5 = view.findViewById(R.id.switch_lane5);

        textLane1 = view.findViewById(R.id.lane_name1);
        textLane2 = view.findViewById(R.id.lane_name2);
        textLane3 = view.findViewById(R.id.lane_name3);
        textLane4 = view.findViewById(R.id.lane_name4);
        textLane5 = view.findViewById(R.id.lane_name5);

        textViews = new TextView[]{textLane1, textLane2, textLane3, textLane4, textLane5};
        switches = new Switch[]{switchLane1, switchLane2, switchLane3, switchLane4, switchLane5};

        button_submit = view.findViewById(R.id.button_man_submit);

        new GetLanes(getActivity()).execute();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        switchLane1.setOnClickListener(this);
        switchLane2.setOnClickListener(this);
        switchLane3.setOnClickListener(this);
        switchLane4.setOnClickListener(this);
        switchLane5.setOnClickListener(this);

        button_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateLanes(switchLane1, switchLane2, switchLane3, switchLane4, switchLane5);
            }
        });
    }

    private void updateLanes(Switch... switches) {
        ArrayList<String> status = new ArrayList<>();
        for (Switch swtch : switches) {
            if (swtch.isChecked())
                status.add("yes");
            else
                status.add("no");
        }

        new UpdateLanes(getActivity()).execute(status, id);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.switch_lane1:
                disableSwitch(switchLane2, switchLane3, switchLane4, switchLane5);
                break;
            case R.id.switch_lane2:
                disableSwitch(switchLane1, switchLane3, switchLane4, switchLane5);
                break;
            case R.id.switch_lane3:
                disableSwitch(switchLane1, switchLane2, switchLane4, switchLane5);
                break;
            case R.id.switch_lane4:
                disableSwitch(switchLane1, switchLane2, switchLane3, switchLane5);
                break;
            case R.id.switch_lane5:
                disableSwitch(switchLane1, switchLane2, switchLane3, switchLane4);
                break;
        }
    }

    private void disableSwitch(Switch... switches) {
        for (Switch switchs : switches) {
            switchs.setChecked(false);
        }
    }

    private static class UpdateLanes extends AsyncTask<ArrayList<String>, JSONObject, String> {

        private WeakReference<FragmentActivity> mActivityWeakReference;
        private ProgressDialog progressDialog;

        UpdateLanes(FragmentActivity activity) {
            mActivityWeakReference = new WeakReference<>(activity);
            progressDialog = new ProgressDialog(mActivityWeakReference.get());
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Please Wait");
            progressDialog.show();
        }

        @SafeVarargs
        @Override
        protected final String doInBackground(ArrayList<String>... strings) {
            String a;
            RestAPI api = new RestAPI();
            try {
                JSONObject json = api.UpdateManual(strings[0], strings[1]);
                JSONPARSE jp = new JSONPARSE();
                a = jp.parse(json);
            } catch (Exception e) {
                a = e.getMessage();
            }
            return a;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            Log.d(TAG, "onPostExecute: " + s);
            if (Utility.checkConnection(s)) {
                Pair<String, String> pair = Utility.GetErrorMessage(s);
                Utility.ShowAlertDialog(mActivityWeakReference.get(), pair.first, pair.second, false);
            } else {

                try {
                    JSONObject json = new JSONObject(s);
                    String StatusValue = json.getString("status");

                    if (StatusValue.compareTo("true") == 0) {

                        Toast.makeText(mActivityWeakReference.get(), "Successfully Updated", Toast.LENGTH_SHORT).show();

                        new GetLanes(mActivityWeakReference.get()).execute();

                    } else {
                        String error = json.getString("Data");
                        Log.d(TAG, "onPostExecute: Error : " + error);

                        if(error.contains("Index was outside the bounds")){
                            Toast.makeText(mActivityWeakReference.get(), "Something went Wrong, Check if you have Added 5 Lanes", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    Log.d(TAG, "onPostExecute: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    private static class GetLanes extends AsyncTask<String, JSONObject, String> {

        private WeakReference<FragmentActivity> mActivityWeakReference;
        private ProgressDialog progressDialog;

        GetLanes(FragmentActivity activity) {
            mActivityWeakReference = new WeakReference<>(activity);
            progressDialog = new ProgressDialog(mActivityWeakReference.get());
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Please Wait");
            progressDialog.show();
        }

        @Override
        protected final String doInBackground(String... strings) {
            String a;
            RestAPI api = new RestAPI();
            try {
                JSONObject json = api.getManual();
                JSONPARSE jp = new JSONPARSE();
                a = jp.parse(json);
            } catch (Exception e) {
                a = e.getMessage();
            }
            return a;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            Log.d(TAG, "onPostExecute: " + s);
            if (Utility.checkConnection(s)) {
                Pair<String, String> pair = Utility.GetErrorMessage(s);
                Utility.ShowAlertDialog(mActivityWeakReference.get(), pair.first, pair.second, false);
            } else {

                try {
                    JSONObject json = new JSONObject(s);
                    String StatusValue = json.getString("status");

                    if (StatusValue.compareTo("ok") == 0) {
                        JSONArray jsonArray = json.getJSONArray("Data");
                        id = new ArrayList<String>();
                        name = new ArrayList<String>();
                        status = new ArrayList<String>();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            id.add(jsonObject.getString("data0"));
                            name.add(jsonObject.getString("data1"));
                            status.add(jsonObject.getString("data2"));
                        }

                        SetValues();

                    }else if (StatusValue.compareTo("no") == 0) {
                        AlertDialog alertDialog = new AlertDialog.Builder(mActivityWeakReference.get())
                                .setTitle("No Details")
                                .setMessage("Could not find any Details, Please Add At-least 5 lanes")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .setCancelable(false)
                                .create();
                        alertDialog.show();
                    } else {
                        String error = json.getString("Data");

                        Log.d(TAG, "onPostExecute: Error : " + error);
                    }
                } catch (Exception e) {
                    Log.d(TAG, "onPostExecute: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    private static void SetValues() {
        int temp = -1;
        for (int i = 0; i < name.size(); i++) {
            temp = i;
            textViews[i].setText("LANE " + (temp + 1) + " : ");

            if (status.get(i).compareTo("Yes") == 0)
                switches[i].setChecked(true);
            else
                switches[i].setChecked(false);
        }
    }

}
