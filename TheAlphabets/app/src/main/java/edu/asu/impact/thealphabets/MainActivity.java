package edu.asu.impact.thealphabets;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import eu.darken.myolib.BaseMyo;
import eu.darken.myolib.Myo;
import eu.darken.myolib.MyoCmds;
import eu.darken.myolib.MyoConnector;
import eu.darken.myolib.msgs.MyoMsg;
import eu.darken.myolib.processor.emg.EmgProcessor;
import eu.darken.myolib.processor.imu.ImuProcessor;




public class MainActivity extends AppCompatActivity implements Myo.BatteryCallback{

    private TextView mMyoName;
    private TextView mBatterylevel;
    private TextView mStatus;
    private Handler mHandler ;
    private MyoConnector mMyoConnector;
    private String DeviceAddress;
    private ImuProcessor mImuProcessor;
    private EmgProcessor mEmgProcessor;
    private String saveFileName = null;
    private boolean WriteMode = false;
    private boolean StartMode = false;
    private HashMap<String, String> IMUDataset = new HashMap<String, String>();
    private HashMap<String, String> EMGDataset = new HashMap<String, String>();
    private String CurrentfilePath;
    private Myo CurrentMyo;
    private boolean myoConnection = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView mTextView = (TextView) findViewById(R.id.text);
        mMyoName = (TextView) findViewById(R.id.tv_title);
        mBatterylevel = (TextView) findViewById(R.id.tv_batterylevel);
        mStatus = (TextView) findViewById(R.id.tv_status);
        Intent intent = this.getIntent();
        if (intent.getStringExtra("DeviceName") != null){

            String DeviceName = intent.getStringExtra("DeviceName");
            DeviceAddress =intent.getStringExtra("DeviceAddress");
            mTextView.setTextColor(Color.CYAN);
            mMyoName.setText(DeviceName);

            mHandler = new Handler();
            mMyoConnector = new MyoConnector(getApplicationContext());
            mMyoConnector.scan(5000, mScannerCallback);
            myoConnection = true;
        }
        else
            mTextView.setTextColor(Color.RED);

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

                        if (myo.getDeviceAddress().equals(DeviceAddress)) {

                            CurrentMyo = myo;
                            myo.connect();
                            myo.setConnectionSpeed(BaseMyo.ConnectionSpeed.HIGH);
                            myo.writeSleepMode(MyoCmds.SleepMode.NEVER, null);
                            myo.writeMode(MyoCmds.EmgMode.FILTERED, MyoCmds.ImuMode.RAW, MyoCmds.ClassifierMode.DISABLED, null);
                            myo.writeUnlock(MyoCmds.UnlockType.HOLD, null);
                            myo.readBatteryLevel(MainActivity.this);
                        }
                        mStatus.setText("Pleast Start!");
                    }

                }
            });
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
            Intent intent = new Intent(MainActivity.this, ScanActivity.class);
            intent.putExtra("Caller","Main");
            startActivity(intent);
        }
        else if (R.id.action_login == id) {
            LoginActivity.mAuthTask = false;
            LoginActivity.user = null;
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            return true;
        }  else if (R.id.action_train == id) {
            Intent intent = new Intent(this, TrainActivity.class);
            startActivity(intent);
            return true;
        } else if (R.id.action_test == id) {
            Intent intent = new Intent(this, TestActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBatteryLevelRead(Myo myo, MyoMsg msg, final int batteryLevel) {
        if(mHandler != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mBatterylevel.setText("Battery: " + batteryLevel + "%");
                }
            });
        }
    }

    @Override
    public void onResume() {
        if(myoConnection) {
            mMyoConnector.scan(5000, mScannerCallback);

        }
        super.onResume();
    }

    @Override
    public void onPause() {
        if(myoConnection) {
            CurrentMyo.setConnectionSpeed(BaseMyo.ConnectionSpeed.BALANCED);
            CurrentMyo.writeSleepMode(MyoCmds.SleepMode.NORMAL, null);
            CurrentMyo.writeMode(MyoCmds.EmgMode.NONE, MyoCmds.ImuMode.NONE, MyoCmds.ClassifierMode.DISABLED, null);
            CurrentMyo.disconnect();
        }
        super.onPause();
    }

}
