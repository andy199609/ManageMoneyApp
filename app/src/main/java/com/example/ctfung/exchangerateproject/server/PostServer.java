package com.example.ctfung.exchangerateproject.server;

import android.content.Context;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by andy199609 on 2/10/2015.
 */
public class PostServer implements Runnable{
    private String[] data;      //post data
    private String target, result="", param="";
    private Context context;
    private URL url;

    public PostServer(Context context, String[] data, String target){
        this.context=context;
        this.data=data;
        this.target=target;         //server url
    }

    public void run() {
        try {
            url = new URL(target);
            HttpURLConnection urlConn = (HttpURLConnection)url.openConnection();
            urlConn.setRequestMethod("POST");       //use post method
            urlConn.setDoInput(true);
            urlConn.setDoOutput(true);
            urlConn.setUseCaches(false);
            urlConn.setInstanceFollowRedirects(true);
            urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            DataOutputStream out = new DataOutputStream(urlConn.getOutputStream());
            param="";
            if(data.length>1){      //when post data more than 1
                String [] parts = data[0].split("==");
                String item = parts[0];     //post name
                String value = parts[1];    //post value
                param+=parts[0]+"="+ URLEncoder.encode(parts[1].toString(), "utf-8");
                for(int i=1;i<data.length;i++){
                    parts = data[i].split("==");
                    item = parts[0];     //post name
                    value = parts[1];    //post value
                    param+="&"+parts[0]+"="+ URLEncoder.encode(parts[1].toString(),"utf-8");
                }
            }
            else{
                String [] parts = data[0].split("==");
                String item = parts[0];     //post name
                String value = parts[1];    //post value
                param+=parts[0]+"="+ URLEncoder.encode(parts[1].toString(),"utf-8");
            }
            out.writeBytes(param);
            out.flush();
            out.close();
            if(urlConn.getResponseCode()==HttpURLConnection.HTTP_OK){
                InputStreamReader in = new InputStreamReader(urlConn.getInputStream());
                BufferedReader buffer = new BufferedReader(in);
                String inputLine = null;
                result="";
                while((inputLine=buffer.readLine())!=null){
                    result+=inputLine;
                }
                in.close();
            }
            urlConn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            result="error";
        }
    }

    public String getResult(){
        return result;
    }
}
