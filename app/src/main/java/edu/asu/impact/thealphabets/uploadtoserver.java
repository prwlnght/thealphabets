package edu.asu.impact.thealphabets;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
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
 * Created by Gautam on 6/30/2016.
 */

public class uploadtoserver extends AsyncTask<String, String, Integer> {

    Activity mParentActivity;
    ProgressDialog progressDialog;

    public uploadtoserver(Activity act){
        this.mParentActivity = act;
    }

    protected void onPreExecute() {
        progressDialog = new ProgressDialog((TrainActivity)mParentActivity);
        progressDialog.setMessage("Uploading...");
        progressDialog.setIndeterminate(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(true);
        progressDialog.show();
    }

    @Override
    protected Integer doInBackground(String... params) {

        // Context myactivity = params[0];
        String textFile = params[0];
        //String textFile = ((TrainActivity)mParentActivity).getFilesDir().getParent() +File.separator + "databases";//+ File.separator+ "FoodBank.db";
        // '/data/data/com.example.aravind.graphapplication/databases/FoodBank.db'
        Log.v("uploader", "textFile: " + textFile);

        HttpURLConnection connection = null;
        DataOutputStream wr = null;
        DataInputStream inputStream = null;

        String pathToOurFile = textFile;
        String urlServer = "https://192.168.43.72/test/UploadToServer.php";//"https://impact.asu.edu/Appenstance/UploadToServerGPS.php";
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary =  "*****";

        int serverResponseCode = 0;
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1*1024*1024;

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

            // new <code
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {

                @Override
                public boolean verify(String s, SSLSession sslSession) {
                    return true;
                }

            });
            //code end
            SSLContext sc = SSLContext.getInstance("TLS");

            sc.init(null, trustAllCerts, new java.security.SecureRandom());

            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        try {
            String fileName = LoginActivity.user +".zip";
            File file = new File(pathToOurFile,fileName); //pathToOurFile +File.separator + "databases"+ File.separator+ "FoodBank.db";

            Log.v("uploader", "path sent:" + fileName);
            /*
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
                Log.v("uploader", "Content: "+text);
            }*/

            FileInputStream fileInputStream = new FileInputStream(file);

            URL url = new URL(urlServer);
            connection = (HttpURLConnection) url.openConnection();

            // Allow Inputs & Outputs
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);

            // Enable POST method
            connection.setRequestMethod("POST");

            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("ENCTYPE", "multipart/form-data");
            connection.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);
            connection.setRequestProperty("uploaded_file", fileName);

            //Send request
            wr = new DataOutputStream (
                    connection.getOutputStream ());

            wr.writeBytes(twoHyphens + boundary + lineEnd);
            wr.writeBytes("Content-Disposition: form-data; name='uploaded_file';filename='"+fileName+"'"+lineEnd);
            //wr.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + pathToOurFile + "\"" + lineEnd);
            wr.writeBytes(lineEnd);

            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            // Read file
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0)
            {
                wr.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            wr.writeBytes(lineEnd);
            wr.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            // Responses from the server (code and message)
            serverResponseCode = connection.getResponseCode();
            String serverResponseMessage = connection.getResponseMessage();

            Log.v("uploader", "HTTP Response is : "
                    + serverResponseMessage + ": " + serverResponseCode);

            if(serverResponseCode == 200){
                // Toast.makeText(myactivity, "File Upload Complete.",
                //              Toast.LENGTH_SHORT).show();
                Log.v("uploader","200 OK\n");
            }
            fileInputStream.close();

            wr.flush();
            //  wr.close ();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if (wr != null) {
                    wr.close();
                }
            }catch (IOException ignored) {
            }

            if (connection != null)
                connection.disconnect();
        }

        return 1;
    }

    protected void onPostExecute(Integer result) {
        this.progressDialog.dismiss();
        if (result != null){
            Log.v("uploader", "Result: Failed");


        }else {
            Log.v("uploader", "Result: success");
        }
    }

    public File[] listf(String directoryName) {

        // .............list file
        File directory = new File(directoryName);

        // get all the files from a directory
        File[] fList = directory.listFiles();

        for (File file : fList) {
            if (file.isFile()) {
                Log.v("uploader","list of files"+file.getAbsolutePath());
            } else if (file.isDirectory()) {
                listf(file.getAbsolutePath());
            }
        }
        for(int i=0;i< fList.length;i++){
            Log.v("uploader","list: " +  fList[i]);}
        return fList;
    }
}
