package cse535.asu.com.assignment2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;

import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;


import android.app.Dialog;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.io.FileOutputStream;
import java.io.InputStream;
import android.view.Window;


import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import android.Manifest;
import android.app.Activity;
import android.support.v4.app.ActivityCompat;
import android.content.pm.PackageManager;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLConnection;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.database.Cursor;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class MainActivity extends AppCompatActivity implements SensorEventListener
{

    //Global variables
    private final String DATABASE_NAME = "Patient_Info_Database";
    private String DATABASE_LOCATION, SDCARD_LOCATION;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private String name, gender_string, age;
    private Integer id;
    private String tableName;
    private boolean tableCreated;
    Button uploadbutton;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    int serverResponseCode = 0;

    // String upLoadServerUri = null;
    final String upLoadServerUri = "https://impact.asu.edu/Appenstance/UploadToServer.php";


    public int uploadFile(String sourceFileUri){

        String fileName = sourceFileUri;
        System.out.println("Location: " + sourceFileUri);
        HttpsURLConnection conn = null;
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
            Log.e("uploadFile", "Source File not exist :" + sourceFileUri );
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
                conn = (HttpsURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", fileName);
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

                    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
                } catch (KeyManagementException ex) {
                    ex.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
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

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

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
                ex.printStackTrace();
                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("Upload file Exception", "Exception : " + e.getMessage(), e);
            }

            System.out.println("SERVER RESPONSE CODE : " + serverResponseCode);
            return serverResponseCode;
        } // End else block

    }

    int downloadedSize = 0;
    int totalSize = 0;
    String dwnload_file_path = "https://impact.asu.edu/Appenstance/Download/cksum.c";

    void downloadFile(){

        try {
            URL url = new URL(dwnload_file_path);
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();

            urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(true);

            //connect
            urlConnection.connect();

            //set the path where we want to save the file
            File SDCardRoot = Environment.getExternalStorageDirectory();
            //create a new file, to save the downloaded file
            File file = new File(SDCardRoot,"downloaded_file1.c");

            FileOutputStream fileOutput = new FileOutputStream(file);
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

                urlConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            } catch (KeyManagementException ex) {
                ex.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
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
            //close the output stream when complete //
            fileOutput.close();

        } catch (final MalformedURLException e) {
            showError("Error : MalformedURLException " + e);
            e.printStackTrace();
        } catch (final IOException e) {
            showError("Error : IOException " + e);
            e.printStackTrace();
        }
        catch (final Exception e) {
            showError("Error : Please check your internet connection " + e);
        }
    }

    void showError(final String err){
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(MainActivity.this, err, Toast.LENGTH_LONG).show();
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        System.out.println("Hi, Launching App");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Things for initializing graphs
        SDCARD_LOCATION = Environment.getExternalStorageDirectory().getAbsolutePath();

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
        DATABASE_LOCATION = SDCARD_LOCATION + "/Assignment2DB";
        System.out.println(DATABASE_LOCATION);

        GraphView graph = (GraphView) findViewById(R.id.graph);
        graph.getGridLabelRenderer().setHorizontalAxisTitle("Time");
        graph.getGridLabelRenderer().setVerticalAxisTitle("Rate");
        graph.setTitle("Health Monitoring");
        graph.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.HORIZONTAL);

        //register the sensors
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        //mSensorManager.registerListener();

        final Button uploadbutton = (Button)findViewById(R.id.uploadButton);

        uploadbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("Demo", "onClick: starting srvice");
                new Thread(new Runnable() {
                    public void run() {

                        uploadFile(DATABASE_LOCATION);

                    }
                }).start();
            }
        });

        final Button downloadbutton = (Button)findViewById(R.id.downloadButton);

        downloadbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("Demo", "onClick: starting srvice");

                new Thread(new Runnable() {
                    public void run() {
                        downloadFile();
                    }
                }).start();
            }
        });



        final Button runButton = (Button)findViewById(R.id.runbutton);
        runButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(MainActivity.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

                EditText patientName = (EditText) findViewById(R.id.editText);
                name = patientName.getText().toString();
                EditText patientID = (EditText) findViewById(R.id.EditText01);
                id = Integer.parseInt(patientID.getText().toString());
                EditText patientAge = (EditText) findViewById(R.id.EditText02);
                age = patientAge.getText().toString();

                RadioButton male = (RadioButton) findViewById(R.id.male);
                RadioButton female = (RadioButton) findViewById(R.id.female);
                RadioButton other = (RadioButton) findViewById(R.id.other);

                RadioGroup gender = (RadioGroup) findViewById(R.id.radioGroup);
                Integer genderId = gender.getCheckedRadioButtonId();

                if (genderId == male.getId())
                {
                    gender_string = "male";
                }
                else if (genderId == female.getId())
                {
                    gender_string = "female";
                }
                else
                {
                    gender_string = "other";
                }

                tableName = name
                        + "_" + id
                        + "_" + age
                        + "_" + gender_string;

                final SQLiteDatabase db1 = SQLiteDatabase.openDatabase(DATABASE_LOCATION, null, SQLiteDatabase.OPEN_READWRITE);
                //final SQLiteDatabase db1 = this.openOrCreateDatabase(DATABASE_LOCATION,MODE_ENABLE_WRITE_AHEAD_LOGGING,null);
                final String readQuery = "SELECT xValues, yValues, zValues FROM " + tableName +
                        " LIMIT 10 OFFSET (SELECT COUNT(*) FROM " + tableName + " )-10";



                Runnable runnable1 = new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Cursor c1 = db1.rawQuery(readQuery, null);
                        LineGraphSeries<DataPoint> mSeriesX = new LineGraphSeries<DataPoint>();
                        LineGraphSeries<DataPoint> mSeriesY = new LineGraphSeries<DataPoint>();
                        LineGraphSeries<DataPoint> mSeriesZ = new LineGraphSeries<DataPoint>();
                        double graph2LastXValue = 0d;

                        if (c1 !=null)
                        {
                            if (c1.moveToFirst())
                            {
                                do
                                {
                                    Double x = c1.getDouble(c1.getColumnIndex("xValues"));
                                    mSeriesX.appendData(new DataPoint(graph2LastXValue, x), true, 101);
                                    Double y = c1.getDouble(c1.getColumnIndex("yValues"));
                                    mSeriesY.appendData(new DataPoint(graph2LastXValue, y), true, 101);
                                    Double z = c1.getDouble(c1.getColumnIndex("zValues"));
                                    mSeriesZ.appendData(new DataPoint(graph2LastXValue, z), true, 101);
                                    graph2LastXValue += 1d;
                                }
                                while (c1.moveToNext());

                            }

                            GraphView graph = (GraphView) findViewById(R.id.graph);
                            graph.removeAllSeries();
                            graph.setTitle("Accelerometer data  for " + name);
                            graph.setBackgroundColor(Color.argb(60, 255, 0, 255));
                            graph.setTitleColor(Color.MAGENTA);
                            graph.getViewport().setScalable(true);
                            graph.getViewport().setScrollable(true);
                            graph.getViewport().setXAxisBoundsManual(true);
                            graph.getViewport().setMinX(0);
                            graph.getViewport().setMaxX(15);

                            graph.addSeries(mSeriesX);
                            graph.addSeries(mSeriesY);
                            graph.addSeries(mSeriesZ);
                            mSeriesX.setColor(Color.MAGENTA);
                            mSeriesY.setColor(Color.GREEN);
                            mSeriesZ.setColor(Color.BLUE);
                            mSeriesX.setThickness(4);
                            mSeriesY.setThickness(4);
                            mSeriesZ.setThickness(4);
                            mSeriesX.setDataPointsRadius(10);
                            mSeriesX.setTitle("X");
                            mSeriesY.setTitle("Y");
                            mSeriesZ.setTitle("Z");
                            graph.getLegendRenderer().setVisible(true);
                            graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);

                            c1.close();
                        }

                    }
                };

                Handler handlerGraph = new Handler();
                handlerGraph.post(runnable1);
            }

        });

        final Button clearButton = (Button) findViewById(R.id.clearbutton);
        clearButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                GraphView graph = (GraphView) findViewById(R.id.graph);
                graph.removeAllSeries();
            }
        });

        final Button stopButton = (Button) findViewById(R.id.stopbutton);
        stopButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                stopAcclService();
                //Put code to stop reading accelerometer values and end write db connection
            }
        });

        final Button startButton = (Button) findViewById(R.id.startbutton);
        startButton.setOnClickListener(new View.OnClickListener()
        {
            @Override

            public void onClick(View v)
            {

                //Toast.makeText(MainActivity.this, "Waiting for db update", Toast.LENGTH_LONG).show();


                //TODO put some ty catch magic here, Alert Dialog for invalid Values
                EditText patientName = (EditText) findViewById(R.id.editText);
                name = patientName.getText().toString();
                EditText patientID = (EditText) findViewById(R.id.EditText01);
                id = Integer.parseInt(patientID.getText().toString());
                EditText patientAge = (EditText) findViewById(R.id.EditText02);
                age = patientAge.getText().toString();

                RadioButton male = (RadioButton) findViewById(R.id.male);
                RadioButton female = (RadioButton) findViewById(R.id.female);
                RadioButton other = (RadioButton) findViewById(R.id.other);

                RadioGroup gender = (RadioGroup) findViewById(R.id.radioGroup);
                Integer genderId = gender.getCheckedRadioButtonId();

                if (genderId == male.getId())
                {
                    gender_string = "male";
                }
                else if (genderId == female.getId())
                {
                            gender_string = "female";
                }
                else
                {
                    gender_string = "other";
                }

                tableName = name
                        + "_" + id
                        + "_" + age
                        + "_" + gender_string;

                createTable();
                startAcclService();
                startButton.setEnabled(false);
                runButton.setEnabled(false);
                clearButton.setEnabled(false);
                stopButton.setEnabled(false);

                //Toast.makeText(MainActivity.this, "database creation is done", Toast.LENGTH_LONG).show();
                System.out.println("Database creation is done");

                Runnable runnable = new Runnable()
                {
                    @Override
                    public void run()
                    {
                        startButton.setEnabled(true);
                        runButton.setEnabled(true);
                        clearButton.setEnabled(true);
                        stopButton.setEnabled(true);
                        //Toast.makeText(MainActivity.this, "Good morning, where is my bacon", Toast.LENGTH_LONG).show();
                        System.out.println("Good Morning");

                    }
                };

                Handler handler = new Handler();
                handler.postDelayed(runnable, 10000);

            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void startAcclService()
    {

        //put all this in a method in a thread
        mSensorManager.registerListener(MainActivity.this, mAccelerometer, 1000000);
        //This get the accls values and puts in in the database
        double[] x = new double[100];
        double[] y = new double[100];
        double[] z = new double[100];

    }

    private void stopAcclService()
    {
        mSensorManager.unregisterListener(MainActivity.this);
    }



    @Override
    public void onSensorChanged(SensorEvent event)
    {
        Toast.makeText(this, event.values.toString(), Toast.LENGTH_SHORT  );
        writeToTable(event.values[0], event.values[1], event.values[2]);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {

    }

    private void writeToTable(float x, float y, float z)
    {

        SQLiteDatabase db = SQLiteDatabase.openDatabase(DATABASE_LOCATION, null, SQLiteDatabase.OPEN_READWRITE);

        Log.i("writeTable", "in WriteTable");

        db.beginTransaction();
        try
        {
            db.execSQL("insert into " + tableName + "(xValues, yValues, zValues) values (" + (double) x + ", " + (double) y + ", " + (double) z + ");");
            db.setTransactionSuccessful();
        }
        catch (SQLiteException e)
        {
            Log.i("Database", e.getMessage());
        }
        finally
        {
            db.endTransaction();
        }
        db.close();

    }

    public void createTable()
    {
        //create the database
        System.out.println("Creating database");
        SQLiteDatabase db;


            //db = this.openOrCreateDatabase(DATABASE_LOCATION, MODE_PRIVATE, null);
           db = SQLiteDatabase.openDatabase(DATABASE_LOCATION, null, SQLiteDatabase.CREATE_IF_NECESSARY);
        //}
        /*catch(SQLiteException se){

            Toast.makeText(MainActivity.this, se.getMessage(), Toast.LENGTH_LONG).show();
        }*/
        Log.i("createTable", "in CreateTable");
        String CREATE_TABLE_SQL = "create table if not exists " + tableName + " ("
                        + "timeStamp integer PRIMARY KEY autoincrement, "
                        + "xValues double, "
                        + "yValues double, "
                        + "zValues double ); ";

        db.beginTransaction();
        try
        {
            db.execSQL(CREATE_TABLE_SQL);
            //db.execSQL(INSERT_DUMMY_VALUES_SQL);
            db.setTransactionSuccessful();
        }
        catch (SQLiteException e)
        {
            Log.i("Database", e.getMessage());
            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
        finally
        {
            db.endTransaction();
            tableCreated = true;
            db.close();
        }
    }
}
