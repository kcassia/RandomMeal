package com.example.randommeal;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Created by 계현 on 2015-07-04.
 */
public class SendThread extends Thread {

    private Store store;

    public SendThread(Store store)
    {
        this.store = store;
    }

    public void run()
    {
            HttpURLConnection conn = null;

            OutputStream os = null;

            URL url = null;
            try {
                url = new URL("http://172.30.126.231:8080/JDBCTest/Test.jsp");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            try {
                conn = (HttpURLConnection) url.openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
            conn.setConnectTimeout(1 * 1000);
            conn.setReadTimeout(1 * 1000);
            try {
                conn.setRequestMethod("POST");
            } catch (ProtocolException e) {
                e.printStackTrace();
            }
            conn.setRequestProperty("Cache-Control", "no-cache");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            conn.setDoInput(true);

            JSONObject json = new JSONObject();
            try {
                json.put("name",   URLEncoder.encode(store.getName(), "UTF-8"));
                json.put("grade", store.getGrade());
            } catch (JSONException e) {
                e.printStackTrace();
            } catch(UnsupportedEncodingException e)
            {
                e.printStackTrace();
            }

            try {
                os = conn.getOutputStream();
                os.write(json.toString().getBytes());
                os.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }

        int responseCode = 0;
        try {
            responseCode = conn.getResponseCode();
        } catch (IOException e) {
            e.printStackTrace();
        }


        switch(responseCode){

            case 200:


                break;

        }


    }
}
