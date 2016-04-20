package com.cse535.thealphabets;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.thalmic.myo.AbstractDeviceListener;
import com.thalmic.myo.DeviceListener;
import com.thalmic.myo.Hub;
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

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import android.os.Environment;


public class TestActivity extends Activity {

    private DeviceListener mListener = new AbstractDeviceListener(){};
    private Button testButton;
    String DATABASE_LOCATION, SDCARD_LOCATION;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

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

        /*Spinner dropdown = (Spinner) findViewById(R.id.alphabets);
        String[] items = new String[]{"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "9"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);*/
        DATABASE_LOCATION = SDCARD_LOCATION + "/Assignment2DB";

        testButton = (Button) findViewById(R.id.button);
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d("Demo", "onClick: starting srvice");
                new Thread(new Runnable() {
                    public void run() {

                        uploadFile(DATABASE_LOCATION);

                    }
                }).start();
                Toast.makeText(TestActivity.this, "DB uploaded", Toast.LENGTH_SHORT).show();


                ImageView image;
                image = (ImageView) findViewById(R.id.imageViewTest);
                image.setScaleType(ImageView.ScaleType.FIT_CENTER);

            }
        });

      /*  Hub hub = Hub.getInstance();
        if (!hub.init(this, getPackageName())) {
            // We can't do anything with the Myo device if the Hub can't be initialized, so exit.
            Toast.makeText(this, "Couldn't initialize Hub", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Next, register for DeviceListener callbacks.
        hub.addListener(mListener);*/
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

  /*  public static void verifyStoragePermissions(Activity activity) {
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
*/
    int serverResponseCode = 0;

    // String upLoadServerUri = null;
    final String upLoadServerUri = "http://10.143.7.90/UploadToServer.php";
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
                System.out.println(parsedString);
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
        final String dwnload_file_path = "http://10.143.6.11/UploadToServer.php";

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