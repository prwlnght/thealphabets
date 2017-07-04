package edu.asu.impact.thealphabets;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import eu.darken.myolib.BaseMyo;
import eu.darken.myolib.Myo;
import eu.darken.myolib.MyoCmds;
import eu.darken.myolib.MyoConnector;
import eu.darken.myolib.msgs.MyoMsg;
import eu.darken.myolib.processor.emg.EmgData;
import eu.darken.myolib.processor.emg.EmgProcessor;
import eu.darken.myolib.processor.imu.ImuData;
import eu.darken.myolib.processor.imu.ImuProcessor;

public class TestActivity extends AppCompatActivity implements Myo.BatteryCallback{


    private Button testButton, correctButton, incorrectButton;
    String DATABASE_LOCATION, SDCARD_LOCATION;
    private TextView mTextView;
    private TextView LetterText;
    TextToSpeech t2;
    TextToSpeech t3;
    String correct="..correct";
    String incorrect="..incorrect";

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
    private int attempt ;
    private File savedMyoFile = null;
    private String selected;

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

    private DownloadFromServer downloadTask;
    public AlphabetMatcher match;
    TextToSpeech t1;
    int classNumber = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        mTextView = (TextView) findViewById(R.id.text);
        LetterText = (TextView) findViewById(R.id.textView2) ;
        SDCARD_LOCATION =  getApplicationContext().getExternalFilesDir(null).getAbsolutePath(); // Environment.getExternalStorageDirectory().getAbsolutePath();
        attempt =1;


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

        final File checkFile = new File(SDCARD_LOCATION+ File.separator + "app_downloads"+ File.separator +LoginActivity.user);
        if (!(checkFile.exists() && checkFile.isDirectory())) {
            downloadTask = new DownloadFromServer(this);
            downloadTask.execute(SDCARD_LOCATION);
        }

        Intent intent = this.getIntent();
        if ((intent.getStringExtra("DeviceName") != null)){

            String DeviceName = intent.getStringExtra("DeviceName");
            DeviceAddress =intent.getStringExtra("DeviceAddress");
            mTextView.setTextColor(Color.CYAN);


            mHandler = new Handler();
            mMyoConnector = new MyoConnector(getApplicationContext());
            mMyoConnector.scan(5000, mScannerCallback);
            myoConnection = true;

            CurrentfilePath = SDCARD_LOCATION + "/Test/" +LoginActivity.user;;

            File isPath = new File(CurrentfilePath);
            if (!isPath.isDirectory())
                isPath.mkdirs();


        }
        else
            mTextView.setTextColor(Color.RED);

        correctButton = (Button) findViewById(R.id.correct);
        correctButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                t2=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if(status != TextToSpeech.ERROR) {

                            t1.setLanguage(Locale.UK);
                            //Toast.makeText(getApplicationContext(), correct,Toast.LENGTH_SHORT).show();
                            t1.speak(correct, TextToSpeech.QUEUE_FLUSH, null);

                            //setImage(correct);
                        }
                    }
                });


            }

        });

        incorrectButton = (Button) findViewById(R.id.incorrect);
        incorrectButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                t3=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if(status != TextToSpeech.ERROR) {

                            t1.setLanguage(Locale.UK);
                            //Toast.makeText(getApplicationContext(), incorrect,Toast.LENGTH_SHORT).show();
                            t1.speak(incorrect, TextToSpeech.QUEUE_FLUSH, null);

                            //setImage(incorrect);
                        }
                    }
                });



            }

        });


        testButton = (Button) findViewById(R.id.button);
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(getApplicationContext(),"Recording MYO Data for 5 seconds",Toast.LENGTH_LONG).show();
                testButton.setEnabled(false);
                // commented by gautam
                /*
                Log.d("Demo", "onClick: starting srvice");
                new Thread(new Runnable() {
                    public void run() {
                        //testButton.setEnabled(true);
                        int classNumberToUse = (classNumber % 7) + 1;
                        DATABASE_LOCATION = SDCARD_LOCATION + "/test" +Integer.toString(classNumberToUse)+ ".csv";
                        SystemClock.sleep(5000);
                        uploadFile(DATABASE_LOCATION);
                        classNumber ++;

                    }
                }).start();
                //Toast.makeText(TestActivity.this, "Data Uploading", Toast.LENGTH_SHORT).show();
                */

                ImageView image;
                image = (ImageView) findViewById(R.id.imageViewTest);
                image.setScaleType(ImageView.ScaleType.FIT_CENTER);

                WriteMode = true;

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        double roll_w, yaw_w, pitch_w;
                        WriteMode = false;
                        testButton.setEnabled(true);



                        if (accelerometerXData.size() >= 250) {



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

                            saveFileName = CurrentfilePath + "/" + LoginActivity.user+ "_alphabets_test" + "_" + attempt;
                            //System.out.print("ACCELEROMETER SIZE" + accelerometerXData.size());

                            //System.out.println("Acclerometer x:" + accelerometerXData.get(0) + " y:" + accelerometerYData.get(0) + " z:" + accelerometerZData.get(0));
                            //System.out.println("Gyroscope x:" + gyroscopeXData.get(0) + " y:" + gyroscopeYData.get(0) + " z:" + gyroscopeZData.get(0));
                            // System.out.println("Roll:" + roll.get(0) + " Pitch:" + pitch.get(0) + " Yaw:" + yaw.get(0));
                            //System.out.println("EMG 0:" + emgDataList0.get(0) + " 1:" + emgDataList1.get(0) + " 2:" + emgDataList3.get(0) + " 3:" + emgDataList3.get(0) +  " 4:" + emgDataList4.get(0) + " 5:" + emgDataList5.get(0) + " 6:" + emgDataList6.get(0) + " 7:" + emgDataList7.get(0));

                            //saveFileName = CurrentfilePath + "/" + "shibani" + "_alphabets_" + selected + "_" + attempt;
                            savedMyoFile = saveMyoData();
                            match = new AlphabetMatcher(savedMyoFile);
                            File featureFile = new File(SDCARD_LOCATION+ File.separator + "app_downloads"+ File.separator +LoginActivity.user+File.separator+"feature_selection_working.csv");
                            match.readFile(featureFile,1);
                            double probAlphabets[] = match.checkAlphabet();
                            String Letter = match.WeightSorter(probAlphabets);
                            LetterText.setText(Letter);
                            clearLists();
                            attempt++;

                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Not enough data, re-record", Toast.LENGTH_LONG).show();
                            clearLists();
                        }

                    }
                    //here call AlphabetMatcher


                }, 5000);

            }
        });

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



        File returnFile = myoData;
        return returnFile;
    }

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
            Intent intent = new Intent(TestActivity.this, ScanActivity.class);
            intent.putExtra("Caller","Test");
            startActivity(intent);
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

        return super.onOptionsItemSelected(item);
    }

    private void onScanActionSelected() {
        // Launch the ScanActivity to scan for Myos to connect to.
        Intent intent = new Intent(this, ScanActivity.class);
        startActivity(intent);
    }

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    int serverResponseCode = 0;
    /*
    // String upLoadServerUri = null;
    final String upLoadServerUri = "http://10.143.108.143/UploadToServer.php";
    //String upLoadServerUri = "https://impact.asu.edu/Appenstance/UploadToServer.php";


    public int uploadFile(String sourceFileUri){

        String fileName = sourceFileUri;
        //System.out.println("Location: " + sourceFileUri);
        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(sourceFileUri);
        if (!sourceFile.isFile()) {

            System.out.println("File not found error");
            Log.e("uploadFile", "Source File not exist :" + sourceFileUri);
            return 0;

        }
        else
        {
            try {
                System.out.println("Inside else, file found");
                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(upLoadServerUri);
                System.out.println("URL made");
                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                //verifyStoragePermissions(TestActivity.this);

                TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                    @Override
                    public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                        // Not implemented
                    }
                    @Override
                    public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                        // Not implemented
                    }
                } };

                try {
                    SSLContext sc = SSLContext.getInstance("TLS");

                    sc.init(null, trustAllCerts, new java.security.SecureRandom());

                    //conn.setDefaultSSLSocketFactory(sc.getSocketFactory());
                } catch (KeyManagementException ex) {
                    ex.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }

                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", fileName);

                dos = new DataOutputStream(conn.getOutputStream());
                System.out.println("after DataOutputStream");

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=uploaded_file;"+"filename="
                        + fileName + " " + lineEnd);

                dos.writeBytes(lineEnd);

                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }

                // send multipart form data necessary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                InputStream is = conn.getInputStream();
                String parsedString = convertinputStreamToString(is);
                final String toSpeak= ".."+parsedString.charAt(2);
                System.out.println(parsedString);
                t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if(status != TextToSpeech.ERROR) {
                            t1.setLanguage(Locale.UK);
                            Toast.makeText(getApplicationContext(), toSpeak,Toast.LENGTH_SHORT).show();
                            t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);

                            setImage(toSpeak);
                        }
                    }
                });String serverResponseMessage = conn.getResponseMessage();

                Log.i("uploadFile", "HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode);

                if(serverResponseCode == 200){
                    System.out.println("FILE UPLOADED");
                }
                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();


            } catch (MalformedURLException ex) {
                //ex.printStackTrace();
                //Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {
                //e.printStackTrace();
                Log.e("Upload file Exception", "Exception : " + e.getMessage(), e);
                //uploadFile(sourceFileUri);

            }

            System.out.println("SERVER RESPONSE CODE : " + serverResponseCode);

            return serverResponseCode;
        } // End else block

    }

    */


    public void setImage(String alpha1) {
        String alpha2= alpha1.toUpperCase();
        char alpha=alpha2.charAt(2);
        ImageView image;
        image = (ImageView) findViewById(R.id.imageViewTest);
        image.setScaleType(ImageView.ScaleType.FIT_CENTER);
        System.out.println(alpha);

        if (alpha == 'A') {
            image.setImageResource(R.drawable.ic_a);
            selected = "A";
        } else if (alpha == 'B') {
            image.setImageResource(R.drawable.ic_b);
            selected = "B";
        } else if (alpha == 'C') {
            image.setImageResource(R.drawable.ic_c);
            selected = "C";
        } else if (alpha == 'D') {
            image.setImageResource(R.drawable.ic_d);
            selected = "D";
        } else if (alpha == 'E') {
            image.setImageResource(R.drawable.ic_e);
            selected = "E";
        } else if (alpha == 'F') {
            image.setImageResource(R.drawable.ic_f);
            selected = "F";
        } else if (alpha == 'G') {
            image.setImageResource(R.drawable.ic_g);
            selected = "G";
        } else if (alpha == 'H') {
            image.setImageResource(R.drawable.ic_h);
            selected = "H";
        } else if (alpha == 'I') {
            image.setImageResource(R.drawable.ic_i);
            selected = "I";
        } else if (alpha == 'J') {
            image.setImageResource(R.drawable.ic_j);
            selected = "J";
        } else if (alpha == 'K') {
            image.setImageResource(R.drawable.ic_k);
            selected = "K";
        } else if (alpha == 'L') {
            image.setImageResource(R.drawable.ic_l);
            selected = "L";
        } else if (alpha == 'M') {
            image.setImageResource(R.drawable.ic_m);
            selected = "M";
        } else if (alpha == 'N') {
            image.setImageResource(R.drawable.ic_n);
            selected = "N";
        } else if (alpha == 'O') {
            image.setImageResource(R.drawable.ic_o);
            selected = "O";
        } else if (alpha == 'P') {
            image.setImageResource(R.drawable.ic_p);
            selected = "P";
        } else if (alpha == 'Q') {
            image.setImageResource(R.drawable.ic_q);
            selected = "Q";
        } else if (alpha == 'R') {
            image.setImageResource(R.drawable.ic_r);
            selected = "R";
        } else if (alpha == 'S') {
            image.setImageResource(R.drawable.ic_s);
            selected = "S";
        } else if (alpha == 'T') {
            image.setImageResource(R.drawable.ic_t);
            selected = "T";
        } else if (alpha == 'U') {
            image.setImageResource(R.drawable.ic_u);
            selected = "U";
        } else if (alpha == 'V') {
            image.setImageResource(R.drawable.ic_v);
            selected = "V";
        } else if (alpha == 'W') {
            image.setImageResource(R.drawable.ic_w);
            selected = "W";
        } else if (alpha == 'X') {
            image.setImageResource(R.drawable.ic_x);
            selected = "X";
        } else if (alpha == 'Y') {
            image.setImageResource(R.drawable.ic_y);
            selected = "Y";
        } else if (alpha == 'Z') {
            image.setImageResource(R.drawable.ic_z);
            selected = "Z";
        } else if (alpha == '0') {
            image.setImageResource(R.drawable.ic_number0);
        } else if (alpha == '1') {
            image.setImageResource(R.drawable.ic_number1);
        } else if (alpha == '2') {
            image.setImageResource(R.drawable.ic_number2);
        } else if (alpha == '3') {
            image.setImageResource(R.drawable.ic_number3);
        } else if (alpha == '4') {
            image.setImageResource(R.drawable.ic_number4);
        } else if (alpha == '5') {
            image.setImageResource(R.drawable.ic_number5);
        } else if (alpha == '6') {
            image.setImageResource(R.drawable.ic_number6);
        } else if (alpha == '7') {
            image.setImageResource(R.drawable.ic_number7);
        } else if (alpha == '8') {
            image.setImageResource(R.drawable.ic_number8);
        } else if (alpha == '9') {
            image.setImageResource(R.drawable.ic_number9);
        }
    }

    public static String convertinputStreamToString(InputStream ists)
            throws IOException {
        if (ists != null) {
            StringBuilder sb = new StringBuilder();
            String line;

            try {
                BufferedReader r1 = new BufferedReader(new InputStreamReader(
                        ists, "UTF-8"));
                while ((line = r1.readLine()) != null) {
                    sb.append(line).append("\n");
                }
            } finally {
                ists.close();
            }
            return sb.toString();
        } else {
            return "";
        }
    }

    /*
    void downloadFile(String destFileUri){
        int downloadedSize = 0;
        int totalSize = 0;
        //String dwnload_file_path = "https://impact.asu.edu/Appenstance/Assignment2DB";
        final String dwnload_file_path = "http://10.143.6.126/UploadToServer.php";

        try {
            URL url = new URL(dwnload_file_path);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();


            urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(true);

            TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                    // Not implemented
                }

                @Override
                public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                    // Not implemented
                }
            } };
            try {
                SSLContext sc = SSLContext.getInstance("TLS");

                sc.init(null, trustAllCerts, new java.security.SecureRandom());

                //urlConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            } catch (KeyManagementException ex) {
                ex.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }

            //connect
            urlConnection.connect();

            File destFile = new File(destFileUri);

            //verifyStoragePermissions(TestActivity.this);
            FileOutputStream fileOutput = new FileOutputStream(destFile);
            //Stream used for reading the data from the internet
            InputStream inputStream = urlConnection.getInputStream();

            //this is the total size of the file which we are downloading
            totalSize = urlConnection.getContentLength();

            //create a buffer...
            byte[] buffer = new byte[1024];
            int bufferLength = 0;

            while ( (bufferLength = inputStream.read(buffer)) > 0 ) {
                fileOutput.write(buffer, 0, bufferLength);
                downloadedSize += bufferLength;

            }
            System.out.println("Downloaded size: "+ downloadedSize);
            //close the output stream when complete //
            fileOutput.close();
            System.out.println("File Downloaded");

        } catch (final MalformedURLException e) {
            //showError("Error : MalformedURLException " + e);
            // e.printStackTrace();
            //downloadFile(destFileUri);
        } catch (final IOException e) {
            //showError("Error : IOException " + e);
            e.printStackTrace();
            //downloadFile(destFileUri);
        }
        catch (final Exception e) {
            //showError("Error : Please check your internet connection " + e);
            // downloadFile(destFileUri);
        }
    }

    void showError(final String err){
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(TestActivity.this, err, Toast.LENGTH_LONG).show();
            }
        });
    }
    */
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
                            myo.readBatteryLevel(TestActivity.this);
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
