package user.com.trafficcontroller.lanes;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import user.com.trafficcontroller.PreferenceManager;
import user.com.trafficcontroller.R;
import user.com.trafficcontroller.webservices.JSONPARSE;
import user.com.trafficcontroller.webservices.RestAPI;
import user.com.trafficcontroller.webservices.Utility;

public class LanesActivity extends AppCompatActivity {
    private static final String TAG = "Lanes";
    private ArrayList<String> lane_id, lane_name, lane_car, lane_c_car;
    private ListView mainHolder;
    private FloatingActionButton fab;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.US);
    private boolean isAuto = false;
    private Timer timer;
    private TimerTask timerTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lanes);
        //getSupportActionBar().setTitle("Lanes");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mainHolder = findViewById(R.id.lanesHolder);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddLaneDialog();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    private void showAddLaneDialog() {
        final EditText laneName, laneCars, laneTraffic;
        Button buttonAddLane;
        final Dialog dialog = new Dialog(LanesActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_traffic_lanes__add);

        laneName = dialog.findViewById(R.id.edittext_lane_name);
        laneCars = dialog.findViewById(R.id.edittext_lane_car);

        buttonAddLane = dialog.findViewById(R.id.button_lane_add);

        buttonAddLane.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (laneName.getText().length() == 0) {
                    Snackbar.make(v, "Enter Lane Name", Snackbar.LENGTH_SHORT)
                            .setAction("OK", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    laneName.requestFocus();
                                }
                            }).setActionTextColor(getResources().getColor(R.color.colorWhite))
                            .show();
                } else if (laneCars.getText().length() == 0) {
                    Snackbar.make(v, "Enter Number of Cars", Snackbar.LENGTH_SHORT)
                            .setAction("OK", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    laneCars.requestFocus();
                                }
                            })
                            .setActionTextColor(getResources().getColor(R.color.colorWhite))
                            .show();
                } else if (Integer.parseInt(laneCars.getText().toString()) == 0) {
                    Snackbar.make(v, "Please, enter number greater than Zero", Snackbar.LENGTH_SHORT)
                            .setAction("OK", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    laneCars.setText("");
                                    laneCars.requestFocus();
                                }
                            })
                            .setActionTextColor(getResources().getColor(R.color.colorWhite))
                            .show();
                } else {
                    dialog.dismiss();
                    Log.d(TAG, "onClick: Lane Name = " + laneName.getText().toString()
                            + "\n Lane Cars = " + laneCars.getText().toString());
                    new AddLanes(LanesActivity.this).execute(laneName.getText().toString(), laneCars.getText().toString());
                }
            }
        });

        dialog.show();
    }

    private void showUpdateDialog(final int position, final boolean isExisting) {
        final EditText laneName, laneCars, laneTraffic;
        Button buttonAddLane;
        TextView textLane;
        final Dialog dialog = new Dialog(LanesActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_traffic_lanes__add);

        laneName = dialog.findViewById(R.id.edittext_lane_name);
        laneCars = dialog.findViewById(R.id.edittext_lane_car);
        textLane = dialog.findViewById(R.id.lane_title);

        textLane.setText("Update Lane");

        laneName.setEnabled(false);
        laneName.setText(lane_name.get(position));

        if (isExisting)
            laneCars.setText(lane_c_car.get(position));
        else
            laneCars.setText(lane_car.get(position));

        buttonAddLane = dialog.findViewById(R.id.button_lane_add);
        buttonAddLane.setText("Update");

        buttonAddLane.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (laneCars.getText().length() == 0) {
                    Snackbar.make(v, "Enter Number of Cars", Snackbar.LENGTH_SHORT)
                            .setAction("OK", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    laneCars.requestFocus();
                                }
                            })
                            .setActionTextColor(getResources().getColor(R.color.colorWhite))
                            .show();
                } else {
                    dialog.dismiss();
                    Log.d(TAG, "onClick: Lane Name = " + laneName.getText().toString()
                            + "\n Lane Cars = " + laneCars.getText().toString());
                    if (isExisting)
                        new UpdateLanes(LanesActivity.this, lane_id.get(position), laneCars.getText().toString()).execute("true");
                    else
                        new UpdateLanes(LanesActivity.this, lane_id.get(position), laneCars.getText().toString()).execute("false");

                }
            }
        });

        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        new GetLanes(LanesActivity.this, true).execute();
    }

    private static class AddLanes extends AsyncTask<String, JSONObject, String> {

        private final WeakReference<LanesActivity> mActivityWeakReference;
        private ProgressDialog mDialog;
        private Calendar calendar = Calendar.getInstance();
        private String dateTime;

        AddLanes(LanesActivity activity) {
            mActivityWeakReference = new WeakReference<>(activity);
            dateTime = mActivityWeakReference.get().dateFormat.format(calendar.getTime());
            mDialog = new ProgressDialog(mActivityWeakReference.get());
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog.setMessage("Please Wait");
            mDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String a;
            RestAPI api = new RestAPI();
            try {
                JSONObject json = api.AddLanes(params[0], params[1], dateTime);
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
            mDialog.setMessage("");
            mDialog.dismiss();
            Log.d(TAG, "onPostExecute: " + s);
            if (Utility.checkConnection(s)) {
                Pair<String, String> pair = Utility.GetErrorMessage(s);
                Utility.ShowAlertDialog(mActivityWeakReference.get(), pair.first, pair.second, false);
            } else {

                try {
                    JSONObject json = new JSONObject(s);
                    String StatusValue = json.getString("status");

                    if (StatusValue.compareTo("true") == 0) {
                        //TODO - Call Get Lanes Function
                        new GetLanes(mActivityWeakReference.get(), true).execute();
                        Log.d(TAG, "onPostExecute: Response true");
                    } else {
                        String error = json.getString("Data");
                        Log.d(TAG, "onPostExecute: Error - " + error);
                    }
                } catch (Exception e) {
                    Log.d(TAG, "onPostExecute: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    private static class GetLanes extends AsyncTask<String, JSONObject, String> {

        private final WeakReference<LanesActivity> mActivityWeakReference;
        private ProgressDialog mDialog;
        private boolean isShow;

        GetLanes(LanesActivity activity, boolean show) {
            mActivityWeakReference = new WeakReference<>(activity);
            mDialog = new ProgressDialog(mActivityWeakReference.get());
            this.isShow = show;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mActivityWeakReference.get().mainHolder.setAdapter(null);
            if (isShow) {
                mDialog.setMessage("Please Wait");
                mDialog.show();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String a;
            RestAPI api = new RestAPI();
            try {
                JSONObject json = api.getLanes();
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
            if (mDialog.isShowing()) {
                mDialog.setMessage("");
                mDialog.dismiss();
            }
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
                        mActivityWeakReference.get().lane_id = new ArrayList<String>();
                        mActivityWeakReference.get().lane_name = new ArrayList<String>();
                        mActivityWeakReference.get().lane_car = new ArrayList<String>();
                        mActivityWeakReference.get().lane_c_car = new ArrayList<String>();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            mActivityWeakReference.get().lane_id.add(jsonObject.getString("data0"));
                            mActivityWeakReference.get().lane_name.add(jsonObject.getString("data1"));
                            mActivityWeakReference.get().lane_c_car.add(jsonObject.getString("data2"));
                            mActivityWeakReference.get().lane_car.add(jsonObject.getString("data3"));

                        }

                        if (mActivityWeakReference.get().lane_c_car.size() < 5) {
                            Toast.makeText(mActivityWeakReference.get(), "Add At-least 5 Lanes", Toast.LENGTH_SHORT).show();
                        }

                        PreferenceManager.SetLanesIds(mActivityWeakReference.get(), mActivityWeakReference.get().lane_id);
                        ArrayList<String> arrayList = PreferenceManager.GetLaneIds(mActivityWeakReference.get());
                        Log.d(TAG, "onPostExecute: List " + Arrays.asList(arrayList).toString());

                        mActivityWeakReference.get().setCustomAdapter(isShow);

                    } else if (StatusValue.compareTo("no") == 0) {
                        AlertDialog alertDialog = new AlertDialog.Builder(mActivityWeakReference.get())
                                .setTitle("No Lanes")
                                .setMessage("Could not find any Lanes, Please Add Lanes")
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
                        Log.d(TAG, "onPostExecute: Error - " + error);
                    }
                } catch (Exception e) {
                    Log.d(TAG, "onPostExecute: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    private static class DeleteLane extends AsyncTask<String, JSONObject, String> {

        private final WeakReference<LanesActivity> mActivityWeakReference;
        private ProgressDialog mDialog;

        DeleteLane(LanesActivity activity) {
            mActivityWeakReference = new WeakReference<>(activity);
            mDialog = new ProgressDialog(mActivityWeakReference.get());
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mActivityWeakReference.get().mainHolder.setAdapter(null);
            mDialog.setMessage("Please Wait");
            mDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String a;
            RestAPI api = new RestAPI();
            try {
                JSONObject json = api.DeleteLanes(params[0]);
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
            mDialog.setMessage("");
            mDialog.dismiss();
            Log.d(TAG, "onPostExecute: " + s);
            if (Utility.checkConnection(s)) {
                Pair<String, String> pair = Utility.GetErrorMessage(s);
                Utility.ShowAlertDialog(mActivityWeakReference.get(), pair.first, pair.second, false);
            } else {

                try {
                    JSONObject json = new JSONObject(s);
                    String StatusValue = json.getString("status");
                    if (StatusValue.compareTo("true") == 0) {
                        new GetLanes(mActivityWeakReference.get(), true).execute();
                    } else {
                        String error = json.getString("Data");
                        Log.d(TAG, "onPostExecute: Error - " + error);
                    }
                } catch (Exception e) {
                    Log.d(TAG, "onPostExecute: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    private static class UpdateLanes extends AsyncTask<String, JSONObject, String> {

        private final WeakReference<LanesActivity> mActivityWeakReference;
        private ProgressDialog mDialog;
        private ArrayList<String> carId, carNo;
        private String dateTime;
        private Calendar calendar = Calendar.getInstance();

        UpdateLanes(LanesActivity activity, @NonNull String carId, @NonNull String noOfCars) {
            mActivityWeakReference = new WeakReference<>(activity);
            this.carId = new ArrayList<String>();
            this.carId.add(carId);
            this.carNo = new ArrayList<String>();
            this.carNo.add(noOfCars);
            dateTime = mActivityWeakReference.get().dateFormat.format(calendar.getTime());
            mDialog = new ProgressDialog(mActivityWeakReference.get());
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mActivityWeakReference.get().mainHolder.setAdapter(null);
            mDialog.setMessage("Please Wait");
            mDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String a;
            RestAPI api = new RestAPI();
            try {
                JSONObject json = api.UpdateCar(carNo, carId, dateTime, params[0]);
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
            mDialog.setMessage("");
            mDialog.dismiss();
            Log.d(TAG, "onPostExecute: " + s);
            if (Utility.checkConnection(s)) {
                Pair<String, String> pair = Utility.GetErrorMessage(s);
                Utility.ShowAlertDialog(mActivityWeakReference.get(), pair.first, pair.second, false);
            } else {

                try {
                    JSONObject json = new JSONObject(s);
                    String StatusValue = json.getString("status");
                    if (StatusValue.compareTo("true") == 0) {
                        new GetLanes(mActivityWeakReference.get(), true).execute();
                    } else {
                        String error = json.getString("Data");
                        Log.d(TAG, "onPostExecute: Error - " + error);
                    }
                } catch (Exception e) {
                    Log.d(TAG, "onPostExecute: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    private void setCustomAdapter(boolean isEnable) {
        mainHolder.setAdapter(new LaneAdapter(LanesActivity.this, R.layout.lanes_item, lane_id, isEnable));
    }

    private class LaneAdapter extends ArrayAdapter<String> {
        private Context appContext;
        private ArrayList<String> ids;
        private boolean isEnable = false;

        public LaneAdapter(@NonNull Context context, int resource, @NonNull ArrayList<String> id, @NonNull boolean enable) {
            super(context, resource, id);
            this.appContext = context;
            this.ids = id;
            this.isEnable = enable;
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(appContext).inflate(R.layout.lanes_item, null, false);
                viewHolder.textName = convertView.findViewById(R.id.lane_item_name);
                viewHolder.textCar = convertView.findViewById(R.id.lane_item_cars);
                viewHolder.textCurrent = convertView.findViewById(R.id.lane_item_c_car);
                viewHolder.imageDelete = convertView.findViewById(R.id.lane_item_remove);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.textName.setText(String.format("Lane Name : %s", lane_name.get(position)));
            viewHolder.textCurrent.setText(String.format("Lane Current Cars : %s", lane_c_car.get(position)));
            viewHolder.textCar.setText(String.format("Lane Total Cars : %s", lane_car.get(position)));
            if (isEnable) {
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog alertDialog = new AlertDialog.Builder(LanesActivity.this)
                                .setTitle("What to Update?")
                                .setMessage("Do you want to update with the Existing cars or update Total cars")
                                .setPositiveButton("Existing", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        showUpdateDialog(position, true);
                                    }
                                })
                                .setNegativeButton("Total", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        showUpdateDialog(position, false);
                                    }
                                })
                                .create();

                        alertDialog.show();
                    }
                });
                viewHolder.imageDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog alertDialog = new AlertDialog.Builder(appContext)
                                .setTitle("Are you Sure?")
                                .setMessage("You want to remove this Lane")
                                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //TODO - Call Api
                                        new DeleteLane(LanesActivity.this).execute(lane_id.get(position));
                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .create();
                        alertDialog.show();

                    }
                });
            }

            return convertView;
        }

        private class ViewHolder {
            TextView textName, textCar, textCurrent;
            ImageView imageDelete;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_m, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem menuItem = menu.findItem(R.id.auto_stop);
        MenuItem menuItem1 = menu.findItem(R.id.auto_refresh);
        MenuItem menuItem2 = menu.findItem(R.id.refresh);
        if (isAuto) {
            menuItem.setVisible(true);
            menuItem1.setVisible(false);
            menuItem2.setVisible(false);
        } else {
            menuItem.setVisible(false);
            menuItem1.setVisible(true);
            menuItem2.setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.auto_refresh) {
            isAuto = true;
            fab.setEnabled(false);
//            mainHolder.setEnabled(false);
            checkLaneData();
            invalidateOptionsMenu();
        } else if (item.getItemId() == R.id.auto_stop) {
            isAuto = false;
            fab.setEnabled(true);
//            mainHolder.setEnabled(true);
            if (timer != null && timerTask != null) {
                timerTask.cancel();
                timer.cancel();
            }
            invalidateOptionsMenu();
        } else {
            new GetLanes(LanesActivity.this, false).execute();
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkLaneData() {
        timerTask = new TimerTask() {
            @Override
            public void run() {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        new GetLanes(LanesActivity.this, false).execute();
                    }
                });
            }
        };

        timer = new Timer();
        timer.schedule(timerTask, 0, 5000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null && timerTask != null) {
            timerTask.cancel();
            timer.cancel();
        }
    }
}
