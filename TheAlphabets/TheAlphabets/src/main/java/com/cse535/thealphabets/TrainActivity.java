package com.cse535.thealphabets;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.thalmic.myo.AbstractDeviceListener;
import com.thalmic.myo.DeviceListener;
import com.thalmic.myo.Hub;
import com.thalmic.myo.Quaternion;
import com.thalmic.myo.Vector3;
import com.thalmic.myo.scanner.ScanActivity;

import java.util.ArrayList;
import java.util.List;

public class TrainActivity extends Activity implements AdapterView.OnItemSelectedListener{


    private DeviceListener mListener = new AbstractDeviceListener(){};
    private Button trainButton;
    private ReadMyo myoReader;

    private String selected;

    private List<Vector3> accelerometerData = new ArrayList<Vector3>();
    private List<Quaternion> orienationData = new ArrayList<Quaternion>();
    private List<Vector3> gyroscopeData = new ArrayList<Vector3>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train);

        Spinner dropdown = (Spinner) findViewById(R.id.alphabets);
        String[] items = new String[]{"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(TrainActivity.this);

        final Hub hub = Hub.getInstance();
        if (!hub.init(this, getPackageName())) {
            // We can't do anything with the Myo device if the Hub can't be initialized, so exit.
            Toast.makeText(this, "Couldn't initialize Hub", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Next, register for DeviceListener callbacks.
        hub.addListener(mListener);
        hub.setLockingPolicy(Hub.LockingPolicy.NONE);


        trainButton = (Button) findViewById(R.id.button);
        trainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                trainButton.setEnabled(false);
                myoReader = new ReadMyo(hub);
                myoReader.reset();
                myoReader.start();

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        myoReader.stop();
                        trainButton.setEnabled(true);
                        accelerometerData = myoReader.getAccelerometerData();

                        /*System.out.println("Accelerometer Data");

                        for (int i=0; i <accelerometerData.size(); i++){
                            System.out.println(accelerometerData.get(i));
                        }

                        System.out.println("Gyroscope Data");

                        gyroscopeData = myoReader.getGyroscopeData();
                        for (int i=0; i <gyroscopeData.size(); i++){
                            System.out.println(gyroscopeData.get(i));
                        }
                        */
                        System.out.println("Orientation Data");
                        orienationData = myoReader.getOrienationData();
                        for (int i=0; i <orienationData.size(); i++){
                            System.out.println(orienationData.get(i));
                        }
                    }
                }, 5000);




            }
        });

    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        ImageView image;
        image = (ImageView) findViewById(R.id.imageView);
        image.setScaleType(ImageView.ScaleType.FIT_CENTER);


        if (parent.getItemAtPosition(pos).toString() == "A") {
            image.setImageResource(R.drawable.ic_alphabeta);
            selected = "A";
        } else if (parent.getItemAtPosition(pos).toString() == "B") {
            image.setImageResource(R.drawable.ic_alphabetb);
            selected = "B";
        } else if (parent.getItemAtPosition(pos).toString() == "C") {
            image.setImageResource(R.drawable.ic_alphabetc);
            selected = "C";
        } else if (parent.getItemAtPosition(pos).toString() == "D") {
            image.setImageResource(R.drawable.ic_alphabetd);
            selected = "D";
        } else if (parent.getItemAtPosition(pos).toString() == "E") {
            image.setImageResource(R.drawable.ic_alphabete);
            selected = "E";
        } else if (parent.getItemAtPosition(pos).toString() == "F") {
            image.setImageResource(R.drawable.ic_alphabetf);
            selected = "F";
        } else if (parent.getItemAtPosition(pos).toString() == "G") {
            image.setImageResource(R.drawable.ic_alphabetg);
            selected = "G";
        } else if (parent.getItemAtPosition(pos).toString() == "H") {
            image.setImageResource(R.drawable.ic_alphabeth);
            selected = "H";
        } else if (parent.getItemAtPosition(pos).toString() == "I") {
            image.setImageResource(R.drawable.ic_alphabeti);
            selected = "I";
        } else if (parent.getItemAtPosition(pos).toString() == "J") {
            image.setImageResource(R.drawable.ic_alphabetj);
            selected = "J";
        } else if (parent.getItemAtPosition(pos).toString() == "K") {
            image.setImageResource(R.drawable.ic_alphabetk);
            selected = "K";
        } else if (parent.getItemAtPosition(pos).toString() == "L") {
            image.setImageResource(R.drawable.ic_alphabetl);
            selected = "L";
        } else if (parent.getItemAtPosition(pos).toString() == "M") {
            image.setImageResource(R.drawable.ic_alphabetm);
            selected = "M";
        } else if (parent.getItemAtPosition(pos).toString() == "N") {
            image.setImageResource(R.drawable.ic_alphabetn);
            selected = "N";
        } else if (parent.getItemAtPosition(pos).toString() == "O") {
            image.setImageResource(R.drawable.ic_alphabeto);
            selected = "O";
        } else if (parent.getItemAtPosition(pos).toString() == "P") {
            image.setImageResource(R.drawable.ic_alphabetp);
            selected = "P";
        } else if (parent.getItemAtPosition(pos).toString() == "Q") {
            image.setImageResource(R.drawable.ic_alphabetq);
            selected = "Q";
        } else if (parent.getItemAtPosition(pos).toString() == "R") {
            image.setImageResource(R.drawable.ic_alphabetr);
            selected = "R";
        } else if (parent.getItemAtPosition(pos).toString() == "S") {
            image.setImageResource(R.drawable.ic_alphabets);
            selected = "S";
        } else if (parent.getItemAtPosition(pos).toString() == "T") {
            image.setImageResource(R.drawable.ic_alphabett);
            selected = "T";
        } else if (parent.getItemAtPosition(pos).toString() == "U") {
            image.setImageResource(R.drawable.ic_alphabetu);
            selected = "U";
        } else if (parent.getItemAtPosition(pos).toString() == "V") {
            image.setImageResource(R.drawable.ic_alphabetv);
            selected = "V";
        } else if (parent.getItemAtPosition(pos).toString() == "W") {
            image.setImageResource(R.drawable.ic_alphabetw);
            selected = "W";
        } else if (parent.getItemAtPosition(pos).toString() == "X") {
            image.setImageResource(R.drawable.ic_alphabetx);
            selected = "X";
        } else if (parent.getItemAtPosition(pos).toString() == "Y") {
            image.setImageResource(R.drawable.ic_alphabety);
            selected = "Y";
        } else if (parent.getItemAtPosition(pos).toString() == "Z") {
            image.setImageResource(R.drawable.ic_alphabetz);
            selected = "Z";
        } else if (parent.getItemAtPosition(pos).toString() == "0") {
            image.setImageResource(R.drawable.ic_number0);
            selected = "0";
        } else if (parent.getItemAtPosition(pos).toString() == "1") {
            image.setImageResource(R.drawable.ic_number1);
            selected = "1";
        } else if (parent.getItemAtPosition(pos).toString() == "2") {
            image.setImageResource(R.drawable.ic_number2);
            selected = "2";
        } else if (parent.getItemAtPosition(pos).toString() == "3") {
            image.setImageResource(R.drawable.ic_number3);
            selected = "3";
        } else if (parent.getItemAtPosition(pos).toString() == "4") {
            image.setImageResource(R.drawable.ic_number4);
            selected = "4";
        } else if (parent.getItemAtPosition(pos).toString() == "5") {
            image.setImageResource(R.drawable.ic_number5);
            selected = "5";
        } else if (parent.getItemAtPosition(pos).toString() == "6") {
            image.setImageResource(R.drawable.ic_number6);
            selected = "6";
        } else if (parent.getItemAtPosition(pos).toString() == "7") {
            image.setImageResource(R.drawable.ic_number7);
            selected = "7";
        } else if (parent.getItemAtPosition(pos).toString() == "8") {
            image.setImageResource(R.drawable.ic_number8);
            selected = "8";
        } else if (parent.getItemAtPosition(pos).toString() == "9") {
            image.setImageResource(R.drawable.ic_number9);
            selected = "9";
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (R.id.action_scan == id) {
            onScanActionSelected();
            return true;
        }
        else if (R.id.action_login == id) {
            LoginActivity.mAuthTask = false;
            LoginActivity.user = null;
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
    }

    private void onScanActionSelected() {
        // Launch the ScanActivity to scan for Myos to connect to.
        Intent intent = new Intent(this, ScanActivity.class);
        startActivity(intent);
    }
}