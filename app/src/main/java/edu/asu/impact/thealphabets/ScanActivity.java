package edu.asu.impact.thealphabets;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import eu.darken.myolib.Myo;

import eu.darken.myolib.msgs.MyoMsg;
import eu.darken.myolib.MyoConnector;


public class ScanActivity extends AppCompatActivity  {
    private ListView mListView;
    private ArrayAdapter<String> mAdapter;
    private MyoConnector mMyoConnector;
    private Context mContext;
    private Handler mHandler,mHandler2;
    private ArrayList<Myo> Myos = new ArrayList<>();
    private ArrayList<String> mMyoAddress = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        mAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.custom_listview);
        mListView = (ListView) findViewById(R.id.scanListView);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(onClickListItem);
        mContext = getApplicationContext();
        mMyoConnector = new MyoConnector(mContext);
        mHandler = new Handler();
        mHandler2 = new Handler();

    }

    @Override
    public void onResume() {
        mAdapter.clear();
        mMyoConnector.scan(5000, mScannerCallback);
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private MyoConnector.ScannerCallback mScannerCallback = new MyoConnector.ScannerCallback() {
        @Override
        public void onScanFinished(final List<Myo> myos) {
            if (mHandler == null) {
                return;
            }
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    for (final Myo myo : myos) {
                        mMyoAddress.add( myo.getDeviceAddress());
                        myo.readDeviceName(new Myo.ReadDeviceNameCallback() {
                            @Override
                            public void onDeviceNameRead(Myo myo, MyoMsg msg, final String deviceName) {
                                mHandler2.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        mAdapter.add(deviceName);
                                        Iterator iterator = Myos.iterator();
                                        while (iterator.hasNext()) {
                                            Myo myo = (Myo) iterator.next();
                                            myo.disconnect();
                                        }
                                    }
                                });
                            }
                        });
                        Myos.add(myo);
                    }

                }
            });
        }

    };

    private OnItemClickListener onClickListItem = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

            Intent intent;

            if ((getIntent().getExtras().getString("Caller")).equals("Main"))
                intent = new Intent(ScanActivity.this, MainActivity.class);
            else if ((getIntent().getExtras().getString("Caller")).equals("Train"))
                intent = new Intent(ScanActivity.this, TrainActivity.class);
            else
                intent = new Intent(ScanActivity.this, TestActivity.class);

            intent.putExtra("DeviceName",((TextView)arg1).getText().toString());
            intent.putExtra("DeviceAddress", mMyoAddress.get(arg2) );
            Toast.makeText(getApplicationContext(),((TextView)arg1).getText().toString() + " is selected!",Toast.LENGTH_SHORT).show();
            startActivity(intent);
        }
    };

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
        if (id == R.id.action_scan){
            mAdapter.clear();
            mMyoConnector.scan(5000, mScannerCallback);
        }
        return super.onOptionsItemSelected(item);
    }


}
