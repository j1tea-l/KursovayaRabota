package com.example.kursovaya;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;

public class HTTPRequestRunnable implements Runnable {
    private final String method;
    private final String address;
    private final HashMap<String, String> requestBody;
    private String responseBody;
    private static final String TAG = "HTTPRequestRunnable";

    public HTTPRequestRunnable(String method, String address, HashMap<String, String> requestBody) {
        this.method = method;
        this.address = address;
        this.requestBody = requestBody;
    }

    public String getResponseBody() {
        return responseBody;
    }

    private String generateStringBody(){
        StringBuilder sbParams = new StringBuilder();
        if(requestBody != null && !requestBody.isEmpty()){
            int i = 0;
            for(String key:this.requestBody.keySet()){
                try {
                    if(i!=0){
                        sbParams.append("&");
                    }
                    sbParams.append(key).append("=")
                            .append(URLEncoder.encode(this.requestBody.get(key), "UTF-8"));
                }catch (Exception e) {
                    Log.d(TAG, "generateStringBody: generateStringBody error");
                }
                i++;
            }
        }
        return sbParams.toString();
    }

    private void doPostRequest(){
        if(this.address != null && !this.address.isEmpty()) {
            try {
                URL url = new URL(this.address);
                URLConnection connection = url.openConnection();
                HttpURLConnection httpConnection = (HttpURLConnection) connection;
                httpConnection.setRequestMethod("POST");
                httpConnection.setDoOutput(true);

                OutputStreamWriter osw = new OutputStreamWriter(httpConnection.getOutputStream());
                osw.write(generateStringBody());
                osw.flush();

                int responseCode = httpConnection.getResponseCode();
                if (responseCode == 200) {
                    InputStreamReader isr = new InputStreamReader(httpConnection.getInputStream());
                    BufferedReader br = new BufferedReader(isr);
                    String currentLine;
                    StringBuilder sbResponse = new StringBuilder();
                    while ((currentLine = br.readLine()) != null) {
                        sbResponse.append(currentLine);
                    }
                    responseBody = sbResponse.toString();
                }

            } catch (Exception e) {
                Log.d(TAG, "doPostRequest: doPostRequest error");
            }
        }
    }

    @Override
    public void run() {
        if(method.equals("POST")) {
            doPostRequest();
        }
    }
}
