package user.com.trafficcontroller.traffic;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;

import user.com.trafficcontroller.R;
import user.com.trafficcontroller.webservices.JSONPARSE;
import user.com.trafficcontroller.webservices.RestAPI;
import user.com.trafficcontroller.webservices.Utility;


public class AutoFragment extends Fragment {
    private static final String TAG = "Auto";
    private EditText editLane1, editLane2, editLane3, editLane4, editLane5;
    private Switch swtLane1, swtLane2, swtLane3, swtLane4, swtLane5, swtMain;
    private Button button_submit;

    private static EditText edit_auto;
    private static EditText[] editViews;
    private static Switch[] switch_auto;
    private static ArrayList<String> id, name, time, status;

    public AutoFragment() {

    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_auto, container, false);
        editLane1 = view.findViewById(R.id.edittext_lane1);
        editLane2 = view.findViewById(R.id.edittext_lane2);
        editLane3 = view.findViewById(R.id.edittext_lane3);
        editLane4 = view.findViewById(R.id.edittext_lane4);
        editLane5 = view.findViewById(R.id.edittext_lane5);

        swtMain = view.findViewById(R.id.switch_main);
        swtLane1 = view.findViewById(R.id.switch_lane1);
        swtLane2 = view.findViewById(R.id.switch_lane2);
        swtLane3 = view.findViewById(R.id.switch_lane3);
        swtLane4 = view.findViewById(R.id.switch_lane4);
        swtLane5 = view.findViewById(R.id.switch_lane5);

        edit_auto = view.findViewById(R.id.edittext_auto);

        editViews = new EditText[]{editLane1, editLane2, editLane3, editLane4, editLane5};
        switch_auto = new Switch[]{swtMain, swtLane1, swtLane2, swtLane3, swtLane4, swtLane5};
        button_submit = view.findViewById(R.id.button_auto_submit);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        new GetLanes(getActivity()).execute();

        editLane5.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    closeKeyboard(v);
                    return true;
                }
                return false;
            }
        });

        edit_auto.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    closeKeyboard(v);
                    setAutoValues();
                    return true;
                }
                return false;
            }
        });


        button_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validate()) {
                    ArrayList<String> times = new ArrayList<>();
                    ArrayList<String> status = new ArrayList<>();

                    for (EditText time : editViews) {
                        times.add(time.getText().toString());
                    }

                    for (int i = 1; i < switch_auto.length; i++) {
                        status.add(switch_auto[i].isChecked() ? "yes" : "No");
                    }

//                    for(int i = 0;i<status.size();i++){
//                        Log.d(TAG, String.format("OnClick Status : %s", status.get(i)));
//                    }
//
//                    for(int i = 0;i<times.size();i++){
//                        Log.d(TAG, String.format("OnClick Times : %s", times.get(i)));
//                    }

                    new UpdateLanes(getActivity(), times, id, status).execute();
                }
            }
        });
    }


    private static void setSwitches(boolean check, Switch[] switches) {
        for (Switch swi : switches) {
            swi.setChecked(check);
        }
    }

    private boolean validate() {
        boolean validate = false;
        for (EditText editView : editViews) {
            if (editView.getText().length() == 0 || Integer.parseInt(editView.getText().toString()) == 0) {
                editView.requestFocus();
                Snackbar.make(button_submit, "Please, Enter Some Time Or Time Greater than Zero", Snackbar.LENGTH_SHORT)
                        .show();
                validate = false;
                break;
            } else {
                validate = true;
            }
        }
        return validate;
    }

    private void setAutoValues() {
        for (EditText time : editViews) {
            time.setText(edit_auto.getText().toString());
            time.setSelection(time.getText().length());
        }
    }

    private void closeKeyboard(TextView v) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    private static boolean isCheckEnabled = true;

    private static CompoundButton.OnCheckedChangeListener checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            setSwitches(isChecked, switch_auto);
        }
    };

    private static class UpdateLanes extends AsyncTask<Void, JSONObject, String> {

        private WeakReference<FragmentActivity> mActivityWeakReference;
        private ProgressDialog progressDialog;
        private ArrayList<String> ids, time, status;

        UpdateLanes(FragmentActivity activity
                , @NonNull ArrayList<String> times
                , @NonNull ArrayList<String> Ids
                , @NonNull ArrayList<String> Status) {
            mActivityWeakReference = new WeakReference<>(activity);
            progressDialog = new ProgressDialog(mActivityWeakReference.get());
            this.ids = Ids;
            this.time = times;
            this.status = Status;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Please Wait");
            progressDialog.show();
        }

        @Override
        protected final String doInBackground(Void... voids) {
            String a;
            RestAPI api = new RestAPI();
            try {
                JSONObject json = api.UpdateAuto(time, ids, status);
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
                        if (error.contains("Index was outside the bounds")) {
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
                JSONObject json = api.getAuto();
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
                        time = new ArrayList<String>();
                        status = new ArrayList<String>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            id.add(jsonObject.getString("data0"));
                            name.add(jsonObject.getString("data1"));
                            time.add(jsonObject.getString("data2"));
                            status.add(jsonObject.getString("data3"));
                        }

                        SetValues();

                    } else if (StatusValue.compareTo("no") == 0) {
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
        for (int i = 0; i < time.size(); i++) {
            editViews[i].setText(time.get(i));
            editViews[i].setSelection(editViews[i].length());
        }


        int largest = 0;

        for (int i = 0; i < time.size(); i++) {
            if (i == 0) {
                largest = Integer.parseInt(time.get(i));
            } else if (i == time.size() - 1) {
                if (largest < Integer.parseInt(time.get(i))) {
                    largest = Integer.parseInt(time.get(i));
                }
            } else {
                if (largest < Integer.parseInt(time.get(i + 1))) {
                    largest = Integer.parseInt(time.get(i + 1));
                }
            }
        }

        boolean checked = !(status.contains("No"));

        Log.d(TAG, "SetValues: " + checked);

        if (checked) {
            isCheckEnabled = true;
            switch_auto[0].setOnCheckedChangeListener(checkedChangeListener);
            switch_auto[0].setChecked(true);
        } else {
            for (int i = 0; i < status.size(); i++) {
                if (status.get(i).compareTo("yes") == 0)
                    switch_auto[i + 1].setChecked(true);
            }
            switch_auto[0].setOnCheckedChangeListener(null);
            switch_auto[0].setChecked(false);
            switch_auto[0].setOnCheckedChangeListener(checkedChangeListener);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    isCheckEnabled = true;
                }
            }, 100);
        }

        edit_auto.setText(largest + "");
        edit_auto.setSelection(edit_auto.getText().length());
        Log.d(TAG, "SetValues: Largest - " + largest);
    }

}
