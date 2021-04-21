package user.com.trafficcontroller;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import user.com.trafficcontroller.lanes.LanesActivity;
import user.com.trafficcontroller.traffic.TrafficController;

public class MainActivity extends AppCompatActivity {

    private Button btn_Lanes, btn_Traffic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_Lanes = findViewById(R.id.button_lanes);
        btn_Traffic = findViewById(R.id.button_traffic);

        btn_Traffic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TrafficController.class);
                startActivity(intent);
            }
        });

        btn_Lanes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LanesActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_logout)
            logout();
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        PreferenceManager.Login(MainActivity.this, "", false);

        Intent intent = new Intent(MainActivity.this, Login.class);
        startActivity(intent);
        finish();
    }
}
