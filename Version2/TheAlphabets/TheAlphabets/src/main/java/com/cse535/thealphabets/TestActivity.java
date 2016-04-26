package com.cse535.thealphabets;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.speech.tts.TextToSpeech;

import com.thalmic.myo.AbstractDeviceListener;
import com.thalmic.myo.DeviceListener;
import com.thalmic.myo.Hub;
import com.thalmic.myo.Myo;
import com.thalmic.myo.scanner.ScanActivity;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
import java.util.Locale;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import android.os.Environment;

public class TestActivity extends Activity {


    private Button testButton;
    String DATABASE_LOCATION, SDCARD_LOCATION;
    private TextView mTextView;

    private DeviceListener mListener = new AbstractDeviceListener() {

        // onConnect() is called whenever a Myo has been connected.
        @Override
        public void onConnect(Myo myo, long timestamp) {
            // Set the text color of the text view to cyan when a Myo connects.
            mTextView.setTextColor(Color.CYAN);
        }

        // onDisconnect() is called whenever a Myo has been disconnected.
        @Override
        public void onDisconnect(Myo myo, long timestamp) {
            // Set the text color of the text view to red when a Myo disconnects.
            mTextView.setTextColor(Color.RED);
        }
    };

    TextToSpeech t1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        mTextView = (TextView) findViewById(R.id.text);

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

        testButton = (Button) findViewById(R.id.button);
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d("Demo", "onClick: starting srvice");
                new Thread(new Runnable() {
                    public void run() {
                        SystemClock.sleep(5000);
                        uploadFile(DATABASE_LOCATION);

                    }
                }).start();
                Toast.makeText(TestActivity.this, "DB uploaded", Toast.LENGTH_SHORT).show();


                ImageView image;
                image = (ImageView) findViewById(R.id.imageViewTest);
                image.setScaleType(ImageView.ScaleType.FIT_CENTER);

            }
        });

      Hub hub = Hub.getInstance();
        if (!hub.init(this, getPackageName())) {
            // We can't do anything with the Myo device if the Hub can't be initialized, so exit.
            Toast.makeText(this, "Couldn't initialize Hub", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Next, register for DeviceListener callbacks.
        hub.addListener(mListener);
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

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    int serverResponseCode = 0;

    // String upLoadServerUri = null;
    final String upLoadServerUri = "http://10.143.6.126/UploadToServer.php";
    //String upLoadServerUri = "https://impact.asu.edu/Appenstance/UploadToServer.php";


    public int uploadFile(String sourceFileUri){

        String fileName = sourceFileUri;
        System.out.println("Location: " + sourceFileUri);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // We don't want any callbacks when the Activity is gone, so unregister the listener.
        Hub.getInstance().removeListener(mListener);
        if (isFinishing()) {
            // The Activity is finishing, so shutdown the Hub. This will disconnect from the Myo.
            Hub.getInstance().shutdown();
        }
    }


    public void setImage(String alpha1) {
        String alpha2= alpha1.toUpperCase();
        char alpha=alpha2.charAt(2);
        ImageView image;
        image = (ImageView) findViewById(R.id.imageViewTest);
        image.setScaleType(ImageView.ScaleType.FIT_CENTER);
        System.out.println(alpha);

        if (alpha == 'A') {
            image.setImageResource(R.drawable.ic_a);
            System.out.println(alpha);
        } else if (alpha == 'B') {
            image.setImageResource(R.drawable.ic_b);
        } else if (alpha == 'C') {
            image.setImageResource(R.drawable.ic_c);
        } else if (alpha == 'D') {
            image.setImageResource(R.drawable.ic_d);
        } else if (alpha == 'E') {
            image.setImageResource(R.drawable.ic_e);
        } else if (alpha == 'F') {
            image.setImageResource(R.drawable.ic_f);
        } else if (alpha == 'G') {
            image.setImageResource(R.drawable.ic_g);
        } else if (alpha == 'H') {
            image.setImageResource(R.drawable.ic_h);
        } else if (alpha == 'I') {
            image.setImageResource(R.drawable.ic_i);
        } else if (alpha == 'J') {
            image.setImageResource(R.drawable.ic_j);
        } else if (alpha == 'K') {
            image.setImageResource(R.drawable.ic_k);
        } else if (alpha == 'L') {
            image.setImageResource(R.drawable.ic_l);
        } else if (alpha == 'M') {
            image.setImageResource(R.drawable.ic_m);
        } else if (alpha == 'N') {
            image.setImageResource(R.drawable.ic_n);
        } else if (alpha == 'O') {
            image.setImageResource(R.drawable.ic_o);
        } else if (alpha == 'P') {
            image.setImageResource(R.drawable.ic_p);
        } else if (alpha == 'Q') {
            image.setImageResource(R.drawable.ic_q);
        } else if (alpha == 'R') {
            image.setImageResource(R.drawable.ic_r);
        } else if (alpha == 'S') {
            image.setImageResource(R.drawable.ic_s);
        } else if (alpha == 'T') {
            image.setImageResource(R.drawable.ic_t);
        } else if (alpha == 'U') {
            image.setImageResource(R.drawable.ic_u);
        } else if (alpha == 'V') {
            image.setImageResource(R.drawable.ic_v);
        } else if (alpha == 'W') {
            image.setImageResource(R.drawable.ic_w);
        } else if (alpha == 'X') {
            image.setImageResource(R.drawable.ic_x);
        } else if (alpha == 'Y') {
            image.setImageResource(R.drawable.ic_y);
        } else if (alpha == 'Z') {
            image.setImageResource(R.drawable.ic_z);
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


}