package user.com.trafficcontroller;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashSet;

public class PreferenceManager {
    private static final String TAG = "PreferenceManager";
    private static String SHARED_PREF = "TrafficController";
    private static String ADMIN_ID = "AdminId";
    private static String LOGGED_IN = "LoggedIn";
    private static String LANES_ADDED = "LanesAdded";
    private static String LANE_ID = "LaneIds";


    public static void Login(@NonNull Context context, @NonNull String UserId, boolean login){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(ADMIN_ID, UserId);
        editor.putBoolean(LOGGED_IN, login);
        editor.commit();
    }

    public static boolean LoggedIn(@NonNull Context context){
        return context.getSharedPreferences(SHARED_PREF,Context.MODE_PRIVATE).getBoolean(LOGGED_IN, false);
    }


    public static void LaneAdded(@NonNull Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(LANES_ADDED, true);
        editor.commit();
    }

    public static boolean isAllLanesAdded(@NonNull Context context){
        return context.getSharedPreferences(SHARED_PREF,Context.MODE_PRIVATE).getBoolean(LANES_ADDED, false);
    }

    public static void SetLanesIds(@NonNull Context context, @NonNull ArrayList<String> laneIds){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet(LANE_ID, new HashSet<String>(laneIds));
        editor.commit();
    }

    public static ArrayList<String> GetLaneIds(@NonNull Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        return new ArrayList<>(sharedPreferences.getStringSet(LANE_ID, new HashSet<String>()));
    }
}
