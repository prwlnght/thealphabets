package edu.asu.impact.thealphabets;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import eu.darken.myolib.BaseMyo;
import eu.darken.myolib.Myo;
import eu.darken.myolib.MyoCmds;
import eu.darken.myolib.MyoConnector;
import eu.darken.myolib.msgs.MyoMsg;
import eu.darken.myolib.processor.emg.EmgData;
import eu.darken.myolib.processor.emg.EmgProcessor;
import eu.darken.myolib.processor.imu.ImuData;
import eu.darken.myolib.processor.imu.ImuProcessor;

public class TrainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, Myo.BatteryCallback {


    private Handler mHandler ;
    private MyoConnector mMyoConnector;
    private String DeviceAddress;
    private ImuProcessor mImuProcessor;
    private EmgProcessor mEmgProcessor;
    private String saveFileName = null;
    private boolean WriteMode = false;
    private HashMap<String, String> IMUDataset = new HashMap<String, String>();
    private HashMap<String, String> EMGDataset = new HashMap<String, String>();
    private String CurrentfilePath;
    private Myo CurrentMyo;
    private boolean myoConnection = false;
    private Button trainButton;
    private int attempt;
    private String selected;
    private File savedMyoFile = null;
    private ProgressBar Pbar;


    private List<Double> accelerometerXData = new ArrayList<Double>();
    private List<Double> accelerometerYData = new ArrayList<Double>();
    private List<Double> accelerometerZData = new ArrayList<Double>();
    private List<Double> gyroscopeXData = new ArrayList<Double>();
    private List<Double> gyroscopeYData = new ArrayList<Double>();
    private List<Double> gyroscopeZData = new ArrayList<Double>();
    private List<Double> orientationWData = new ArrayList<Double>();
    private List<Double> orientationXData = new ArrayList<Double>();
    private List<Double> orientationYData = new ArrayList<Double>();
    private List<Double> orientationZData = new ArrayList<Double>();
    private List<Byte> emgDataList0 = new ArrayList<Byte>();
    private List<Byte> emgDataList1 = new ArrayList<Byte>();
    private List<Byte> emgDataList2 = new ArrayList<Byte>();
    private List<Byte> emgDataList3 = new ArrayList<Byte>();
    private List<Byte> emgDataList4 = new ArrayList<Byte>();
    private List<Byte> emgDataList5 = new ArrayList<Byte>();
    private List<Byte> emgDataList6 = new ArrayList<Byte>();
    private List<Byte> emgDataList7 = new ArrayList<Byte>();



    private List<Integer> roll = new ArrayList<Integer>();
    private List<Integer> yaw = new ArrayList<Integer>();
    private List<Integer> pitch = new ArrayList<Integer>();

    private static final String COMMA_DELIMITER = ",";
    private static final String NEW_LINE_SEPARATOR = "\n";

    String SDCARD_LOCATION;
    private TextView mTextView;
    private uploadtoserver uploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train);

        mTextView = (TextView) findViewById(R.id.text);
        Pbar = (ProgressBar)findViewById(R.id.progressBar) ;

        attempt = 1;

        SDCARD_LOCATION = getApplicationContext().getExternalFilesDir(null).getAbsolutePath();//Environment.getExternalStorageDirectory().getAbsolutePath();

        if (android.os.Build.DEVICE.contains("samsung")
                || android.os.Build.MANUFACTURER.contains("samsung")) {
            File f = new File(Environment.getExternalStorageDirectory()
                    .getParent() + "/extSdCard" + "/myDirectory");
            if (f.exists() && f.isDirectory()) {
                SDCARD_LOCATION = Environment.getExternalStorageDirectory()
                        .getParent() + "/extSdCard";
            } else {
                f = new File(Environment.getExternalStorageDirectory()
                        .getAbsolutePath() + "/external_sd" + "/myDirectory");
                if (f.exists() && f.isDirectory()) {
                    SDCARD_LOCATION = Environment
                            .getExternalStorageDirectory().getAbsolutePath()
                            + "/external_sd";
                }

            }
        }
        Log.d("myTAG","new TAG"+SDCARD_LOCATION);

        Intent intent = this.getIntent();

        if ( (intent.getStringExtra("DeviceName") != null)){ //

            String DeviceName = intent.getStringExtra("DeviceName");
            DeviceAddress =intent.getStringExtra("DeviceAddress");
            mTextView.setTextColor(Color.CYAN);


            mHandler = new Handler();
            mMyoConnector = new MyoConnector(getApplicationContext());
            mMyoConnector.scan(5000, mScannerCallback);
            myoConnection = true;

            CurrentfilePath = SDCARD_LOCATION + "/" +LoginActivity.user;
            Log.v("myTAG",CurrentfilePath);
            Log.v("myTAG",SDCARD_LOCATION);
            File isPath = new File(CurrentfilePath);
            if (!isPath.isDirectory())
                isPath.mkdirs();


        }
        else
            mTextView.setTextColor(Color.RED);

        Spinner dropdown = (Spinner) findViewById(R.id.alphabets);
        String[] items = new String[]{"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(TrainActivity.this);


        trainButton = (Button) findViewById(R.id.button);
        trainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(getApplicationContext(), "Attempt_"+selected+" "+attempt, Toast.LENGTH_SHORT).show();
                //Toast.makeText(getApplicationContext(), "Recording MYO Data for 5 seconds", Toast.LENGTH_LONG).show();
                trainButton.setEnabled(false);
                WriteMode = true;

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        double roll_w, yaw_w, pitch_w;
                        WriteMode = false;
                        trainButton.setEnabled(true);

                        System.out.print("ACCELEROMETER SIZE" + accelerometerXData.size());

                        if (accelerometerXData.size() >= 250) {
                            Pbar.setProgress(attempt);
                            attempt++;

                            if (attempt > 5) //to ensure that only 5 iterations are stored for each Alphabet per user
                                attempt = 1;

                            double w, x, y, z;


                            for (int i = 0; i < accelerometerXData.size(); i++) {

                                w = orientationWData.get(i);
                                x = orientationXData.get(i);
                                y = orientationYData.get(i);
                                z = orientationZData.get(i);

                                roll_w = Math.atan2(2.0f * ((w * x) + (y * z)), 1.0f - 2.0f * (x * x + y * y));
                                Integer roll_rad = (int) (((roll_w + (float) Math.PI) / Math.PI * 2.0f) * 180);

                                pitch_w = Math.asin(Math.max(-1.0f, Math.min(1.0f, 2.0f * (w * y - z * x))));
                                Integer pitch_rad = (int) (((pitch_w + (float) Math.PI) / Math.PI * 2.0f) * 180);

                                yaw_w = Math.atan2(2.0f * (w * z + x * y), 1.0f - 2.0f * (y * y + z * z));
                                Integer yaw_rad = (int) (((yaw_w + (float) Math.PI) / Math.PI * 2.0f) * 180);

                                roll.add(roll_rad);
                                pitch.add(pitch_rad);
                                yaw.add(yaw_rad);
                            }

                            saveFileName = CurrentfilePath + "/" + "alphabets_" + selected + "_" + attempt;
                            //System.out.print("ACCELEROMETER SIZE" + accelerometerXData.size());

                            //System.out.println("Acclerometer x:" + accelerometerXData.get(0) + " y:" + accelerometerYData.get(0) + " z:" + accelerometerZData.get(0));
                            //System.out.println("Gyroscope x:" + gyroscopeXData.get(0) + " y:" + gyroscopeYData.get(0) + " z:" + gyroscopeZData.get(0));
                           // System.out.println("Roll:" + roll.get(0) + " Pitch:" + pitch.get(0) + " Yaw:" + yaw.get(0));
                            //System.out.println("EMG 0:" + emgDataList0.get(0) + " 1:" + emgDataList1.get(0) + " 2:" + emgDataList3.get(0) + " 3:" + emgDataList3.get(0) +  " 4:" + emgDataList4.get(0) + " 5:" + emgDataList5.get(0) + " 6:" + emgDataList6.get(0) + " 7:" + emgDataList7.get(0));

                            //saveFileName = CurrentfilePath + "/" + "shibani" + "_alphabets_" + selected + "_" + attempt;
                            savedMyoFile = saveMyoData();
                        }
                        else
                            Toast.makeText(getApplicationContext(), "Not enough data, re-record", Toast.LENGTH_LONG).show();
                            clearLists();
                    }
                }, 5000);

            }
        });

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
                            myo.readBatteryLevel(TrainActivity.this);
                            mImuProcessor = new ImuProcessor();
                            myo.addProcessor(mImuProcessor);

                            mImuProcessor.addListener(new ImuProcessor.ImuDataListener() {
                                @Override
                                public void onNewImuData(ImuData imuData) {
                                    if (WriteMode) {
                                        accelerometerXData.add(imuData.getAccelerometerData()[0]);
                                        accelerometerYData.add(imuData.getAccelerometerData()[1]);
                                        accelerometerZData.add(imuData.getAccelerometerData()[2]);
                                        gyroscopeXData.add(imuData.getGyroData()[0]);
                                        gyroscopeYData.add(imuData.getGyroData()[1]);
                                        gyroscopeZData.add(imuData.getGyroData()[2]);
                                        orientationWData.add(imuData.getOrientationData()[0]);
                                        orientationXData.add(imuData.getOrientationData()[1]);
                                        orientationYData.add(imuData.getOrientationData()[2]);
                                        orientationZData.add(imuData.getOrientationData()[3]);
                                    }
                                }
                            });

                            mEmgProcessor = new EmgProcessor();
                            myo.addProcessor(mEmgProcessor);
                            mEmgProcessor.addListener(new EmgProcessor.EmgDataListener() {
                                @Override
                                public void onNewEmgData(EmgData emgData) {
                                    if (WriteMode){
                                        emgDataList0.add(Byte.valueOf(emgData.getData()[0]));
                                        emgDataList1.add(Byte.valueOf(emgData.getData()[1]));
                                        emgDataList2.add(Byte.valueOf(emgData.getData()[2]));
                                        emgDataList3.add(Byte.valueOf(emgData.getData()[3]));
                                        emgDataList4.add(Byte.valueOf(emgData.getData()[4]));
                                        emgDataList5.add(Byte.valueOf(emgData.getData()[5]));
                                        emgDataList6.add(Byte.valueOf(emgData.getData()[6]));
                                        emgDataList7.add(Byte.valueOf(emgData.getData()[7]));
                                    }

                                }
                            });

                        }

                    }

                }
            });
        }

    };

    public void clearLists(){
        emgDataList0.clear();
        emgDataList1.clear();
        emgDataList2.clear();
        emgDataList3.clear();
        emgDataList4.clear();
        emgDataList5.clear();
        emgDataList6.clear();
        emgDataList7.clear();

        accelerometerXData.clear();
        accelerometerYData.clear();
        accelerometerZData.clear();

        gyroscopeXData.clear();
        gyroscopeYData.clear();
        gyroscopeZData.clear();

        orientationWData.clear();
        orientationXData.clear();
        orientationYData.clear();
        orientationZData.clear();

    }

    public File saveMyoData() {

        String filePath = saveFileName + ".csv";

        File myoData = new File(filePath);


            try {
                myoData.createNewFile();
                FileWriter fw = new FileWriter(myoData.getAbsoluteFile(),false);
                BufferedWriter bw = new BufferedWriter(fw);

                for (int i=0; i<250; i = i + 5) {

                    bw.write(Double.toString(emgDataList0.get(i)));
                    bw.write(COMMA_DELIMITER);
                    bw.write(Double.toString(emgDataList1.get(i)));
                    bw.write(COMMA_DELIMITER);
                    bw.write(Double.toString(emgDataList2.get(i)));
                    bw.write(COMMA_DELIMITER);
                    bw.write(Double.toString(emgDataList3.get(i)));
                    bw.write(COMMA_DELIMITER);
                    bw.write(Double.toString(emgDataList4.get(i)));
                    bw.write(COMMA_DELIMITER);
                    bw.write(Double.toString(emgDataList5.get(i)));
                    bw.write(COMMA_DELIMITER);
                    bw.write(Double.toString(emgDataList6.get(i)));
                    bw.write(COMMA_DELIMITER);
                    bw.write(Double.toString(emgDataList7.get(i)));
                    bw.write(COMMA_DELIMITER);
                    bw.write(Double.toString(accelerometerXData.get(i)));
                    bw.write(COMMA_DELIMITER);
                    bw.write(Double.toString(accelerometerYData.get(i)));
                    bw.write(COMMA_DELIMITER);
                    bw.write(Double.toString(accelerometerZData.get(i)));
                    bw.write(COMMA_DELIMITER);
                    bw.write(Double.toString(gyroscopeXData.get(i)));
                    bw.write(COMMA_DELIMITER);
                    bw.write(Double.toString(gyroscopeYData.get(i)));
                    bw.write(COMMA_DELIMITER);
                    bw.write(Double.toString(gyroscopeZData.get(i)));
                    bw.write(COMMA_DELIMITER);
                    bw.write(Double.toString(roll.get(i)));
                    bw.write(COMMA_DELIMITER);
                    bw.write(Double.toString(pitch.get(i)));
                    bw.write(COMMA_DELIMITER);
                    bw.write(Double.toString(yaw.get(i)));
                    bw.write(NEW_LINE_SEPARATOR);

                }
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        clearLists();

        File returnFile = myoData;
        return returnFile;
    }

    public void onDone(View v){
        String zipFilePath = SDCARD_LOCATION+"/"+ LoginActivity.user+".zip";
        zipFolder(CurrentfilePath,zipFilePath); //SDCARD_LOCATION
        uploadTask = new uploadtoserver(this);
        uploadTask.execute(SDCARD_LOCATION);

        //Intent intent = new Intent(this, TestActivity.class);
        //startActivity(intent);
    }

    private static void zipFolder(String inputFolderPath, String outZipPath) {
        try {
            FileOutputStream fos = new FileOutputStream(outZipPath);
            ZipOutputStream zos = new ZipOutputStream(fos);
            File srcFile = new File(inputFolderPath);
            File[] files = srcFile.listFiles();
            Log.d("", "Zip directory: " + srcFile.getName());
            for (int i = 0; i < files.length; i++) {
                Log.d("", "Adding file: " + files[i].getName());
                byte[] buffer = new byte[1024];
                FileInputStream fis = new FileInputStream(files[i]);
                zos.putNextEntry(new ZipEntry(files[i].getName()));
                int length;
                while ((length = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, length);
                }
                zos.closeEntry();
                fis.close();
            }
            zos.close();
        } catch (IOException ioe) {
            Log.e("", ioe.getMessage());
        }
    }



    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        ImageView image;
        image = (ImageView) findViewById(R.id.imageView);
        image.setScaleType(ImageView.ScaleType.FIT_CENTER);
        Pbar.setProgress(0);
        attempt =1;

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
            Intent intent = new Intent(TrainActivity.this, ScanActivity.class);
            intent.putExtra("Caller","Train");
            startActivity(intent);
            return true;
        } else if (R.id.action_login == id) {
            LoginActivity.mAuthTask = false;
            LoginActivity.user = null;
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            return true;
        } else if (R.id.action_home == id) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            return true;
        }  else if (R.id.action_test == id) {
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