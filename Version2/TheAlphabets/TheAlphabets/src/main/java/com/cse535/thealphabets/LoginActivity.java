package com.cse535.thealphabets;


import android.app.Activity;
import android.content.Intent;

import android.os.AsyncTask;

import android.os.Bundle;

import android.os.StrictMode.*;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.thalmic.myo.AbstractDeviceListener;
import com.thalmic.myo.DeviceListener;
import com.thalmic.myo.Hub;
import com.thalmic.myo.scanner.ScanActivity;

import java.net.*;

public class LoginActivity extends Activity {


    public static Boolean mAuthTask = false;
    public static String user;
    public String email;

    private DeviceListener mListener = new AbstractDeviceListener(){};
    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

    }

    private void attemptLogin() {
        if (mAuthTask != false) {
            return;
        }

        mEmailView.setError(null);
        mPasswordView.setError(null);

        email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        boolean cancel = false;
        View focusView = null;

        if (cancel) {

            focusView.requestFocus();
        } else {

            //Toast.makeText(this, "Attempt Login", Toast.LENGTH_SHORT).show();
            new RetrieveFeedTaskImpl().execute("http://10.143.6.126/" + email);

        }
    }

    public void returnValue(Boolean result) {

        if(result){
            Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();
            mAuthTask = true;
            user = email;
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);

        }
        else {
            Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show();
        }

    }


    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }*/

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (R.id.action_scan == id) {
            onScanActionSelected();
            return true;
        }
        else if (R.id.action_login == id) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            return true;
        }
        else if (R.id.action_home == id) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            return true;
        }
        else if (R.id.action_train == id) {
            Intent intent = new Intent(this, TrainActivity.class);
            startActivity(intent);
            return true;
        }
        else if (R.id.action_test == id) {
            Intent intent = new Intent(this, TestActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }*/

    /*private void onScanActionSelected() {
        Intent intent = new Intent(this, ScanActivity.class);
        startActivity(intent);
    }*/


    class RetrieveFeedTaskImpl extends AsyncTask<String, Void, Boolean> {

        Boolean x;
        private Exception exception;
        @Override
        protected Boolean doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection.setFollowRedirects(false);
                HttpURLConnection con =
                        (HttpURLConnection) url.openConnection();
                con.setRequestMethod("HEAD");
                x=(con.getResponseCode() == HttpURLConnection.HTTP_OK);
                return x;
            } catch (Exception e) {
                this.exception = e;
                x=false;
                return x;
            }
        }

        @Override
        protected void onPostExecute(Boolean feed) {
            returnValue(x);
        }

    }

}
