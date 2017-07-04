package edu.asu.impact.thealphabets;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by Gautam on 7/2/2016.
 */
public class DownloadFromServer extends AsyncTask<String, Integer , String> {

    Activity mParentActivity;
    ProgressDialog progressDialog;

    public DownloadFromServer( Activity act){
        this.mParentActivity = act;
    }
    // private PowerManager.WakeLock mWakeLock;

    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog((TestActivity)mParentActivity);
        progressDialog.setMessage("Downloading...");
        progressDialog.setIndeterminate(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(true);
        progressDialog.show();
    }

    @Override
    protected String doInBackground(String... params) {
;
        InputStream input = null;
        OutputStream output = null;
        HttpsURLConnection connection = null; //s
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
            //new code
             HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                 @Override
                 public boolean verify(String s, SSLSession sslSession) {
                     return true;
                 }
             });
              //end code

            SSLContext sc = SSLContext.getInstance("TLS");

            sc.init(null, trustAllCerts, new java.security.SecureRandom());

            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            String pathToOurFile = params[0];
            String NameofFile = "feature_selection_working.csv";
            String url_string = "https://10.218.110.136/test/uploads" + File.separator + LoginActivity.user + File.separator+"features"+File.separator+NameofFile; //"https://impact.asu.edu/Appenstance"+ File.separator+NameofFile;
            URL url = new URL(url_string);// sUrl[0]
            connection = (HttpsURLConnection) url.openConnection(); //https

            connection.connect();

            // expect HTTP 200 OK, so we don't mistakenly save error report
            // instead of the file
            Log.v("downloader", String.valueOf(connection.getResponseCode()));
            if (connection.getResponseCode() != 200) {//HttpsURLConnection.HTTP_OK
                return "Server returned HTTP " + connection.getResponseCode()
                        + " " + connection.getResponseMessage();
            }

            // this will be useful to display download percentage
            // might be -1: server did not report the length
            int fileLength = connection.getContentLength();

            //downloadButton.setText(Integer.toString(fileLength));
            // download the file
            input = connection.getInputStream();
            pathToOurFile = pathToOurFile + File.separator + "app_downloads"+ File.separator +LoginActivity.user;
            File isPath = new File(pathToOurFile);
            if (!isPath.isDirectory())
                isPath.mkdirs();

            pathToOurFile = pathToOurFile + File.separator +NameofFile;
            output = new FileOutputStream(pathToOurFile);
            //downloadButton.setText("Connecting .....");
            byte data[] = new byte[4096];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                // allow canceling with back button
                if (isCancelled()) {
                    input.close();
                    return null;
                }
                total += count;

                output.write(data, 0, count);
                //code to read downloaded file
                File file = new File(pathToOurFile);
                if(file.exists()){
                    //listf(pathToOurFile);
                    StringBuilder text = new StringBuilder();

                    BufferedReader br = new BufferedReader(new FileReader(file));
                    String line;

                    while ((line = br.readLine()) != null) {
                        text.append(line);
                        text.append('\n');
                    }
                    br.close();
                    Log.v("downloader", "Content: "+text);
                }
            }
        } catch (Exception e) {
            return e.toString();
        } finally {
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException ignored) {
            }

            if (connection != null)
                connection.disconnect();
        }
        return null;


    }



    @Override
    protected void onPostExecute(String result) {
        // mWakeLock.release();
        this.progressDialog.dismiss();
        if (result != null){
            // Toast.makeText(context, "Download error: " + result, Toast.LENGTH_LONG).show();
            Log.v("uploader", "Result: Failed");
        }else{
             Log.v("uploader", "Result: success");
            //  Toast.makeText(context,"File downloaded", Toast.LENGTH_SHORT).show();
            /*
            String pathToOurFile = "/data/GraphView/downloads/";
            File file = new File(pathToOurFile,"FoodBank.db");
            if(file.exists()){
                //listf(pathToOurFile);
                StringBuilder text = new StringBuilder();
                try {
                    BufferedReader br = new BufferedReader(new FileReader(file));
                    String line;

                    while ((line = br.readLine()) != null) {
                        text.append(line);
                        text.append('\n');
                    }
                    br.close();
                } catch(FileNotFoundException e){
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.v("uploader", "Content: " + text);
            }*/
        }
    }
}
