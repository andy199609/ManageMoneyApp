package com.example.ctfung.exchangerateproject.server;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by CTFung on 8/10/15.
 */
public class GetServer implements Runnable{
    private String url;
    private String data;

    public GetServer(String url) {
        this.url = url.replace(" ","%20");
    }

    public void run() {
        try {
            URL url = new URL(this.url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            InputStream inputStream = connection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            data = bufferedReader.readLine();
            String temp="";
            while ((temp = bufferedReader.readLine()) != null)
                data += temp;
        } catch (Exception e) {
            e.printStackTrace();
            data = "error";
        }
    }

    public static String encode(String content){   //avoid chinese words in url will have exception
        try {
            content = URLEncoder.encode(content, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return content;
    }

    public String getData() {
        return data;
    }
}
