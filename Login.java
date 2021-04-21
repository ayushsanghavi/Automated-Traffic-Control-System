package user.com.trafficcontroller;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONObject;

import java.lang.ref.WeakReference;

import user.com.trafficcontroller.webservices.JSONPARSE;
import user.com.trafficcontroller.webservices.RestAPI;
import user.com.trafficcontroller.webservices.Utility;

public class Login extends AppCompatActivity {
    private static final String TAG = "Login";
    private EditText EmailText, PasswordText;
    private Button LoginBtn;
    private static ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        mDialog = new ProgressDialog(Login.this);

        if (!PreferenceManager.LoggedIn(Login.this)) {
            setContentView(R.layout.activit_login);
            init();
        } else {
            Intent intent = new Intent(Login.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public void init() {

        EmailText = (EditText) findViewById(R.id.email_text);
        PasswordText = (EditText) findViewById(R.id.pass_text);
        LoginBtn = (Button) findViewById(R.id.loginBtn);

        LoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (EmailText.getText().toString().equals("")) {
                    Snackbar.make(LoginBtn, "Enter Email", Snackbar.LENGTH_SHORT).show();
                    EmailText.requestFocus();

                } else if (PasswordText.getText().toString().equals("")) {
                    Snackbar.make(LoginBtn, "Password is required", Snackbar.LENGTH_LONG).show();
                    PasswordText.requestFocus();

                } else {

                    new LoginTask(Login.this).execute(EmailText.getText().toString().trim(), PasswordText.getText().toString());

                }

            }

        });

    }

    private static class LoginTask extends AsyncTask<String, JSONObject, String> {

        private final WeakReference<Login> mActivityWeakReference;

        LoginTask(Login activity) {
            mActivityWeakReference = new WeakReference<>(activity);
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
                JSONObject json = api.ALogin(params[0], params[1]);
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

                    if (StatusValue.compareTo("false") == 0) {
                        Snackbar.make(mActivityWeakReference.get().LoginBtn, "Invalid Credential", Snackbar.LENGTH_SHORT).show();

                    } else if (StatusValue.compareTo("true") == 0) {

                        PreferenceManager.Login(mActivityWeakReference.get(), mActivityWeakReference.get().EmailText.getText().toString().trim(), true);

                        Intent intent = new Intent(mActivityWeakReference.get(), MainActivity.class);
                        mActivityWeakReference.get().startActivity(intent);
                        mActivityWeakReference.get().finish();

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

}
