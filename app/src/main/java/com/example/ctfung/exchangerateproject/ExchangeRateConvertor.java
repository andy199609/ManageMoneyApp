package com.example.ctfung.exchangerateproject;

import android.content.Context;
import android.util.Log;
import com.example.ctfung.exchangerateproject.server.GetServer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by fung on 12/28/2015.
 */
public class ExchangeRateConvertor {
    String [] targetUnit = {"HKD","USD","CNY","TWD","JPY","KRW","EUR","THB","CAD","GBP"};
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    String currencyUnit;
    Context c;

    public ExchangeRateConvertor(Context c,String currencyUnit){
        this.c = c;
        this.currencyUnit = currencyUnit;
        if(currencyUnit.equals("null"))
            this.currencyUnit = "HKD";
    }

    public void updateExchangeTable(){
        int rowCount = LocalDB.getTableRowCount("ExchangeRate");
        String today =  dateFormat.format(new Date());
        if(rowCount==0||!today.equals(LocalDB.getDataBySQL("SELECT date FROM ExchangeRate", 0, "date"))){    //table not any rows and the exchange rate day not equals today
            if(rowCount>0)
                LocalDB.delete("ExchangeRate", new String[]{"2=2"});
            ArrayList<String> pair = new ArrayList<>();
            pair.clear();
            for(int a=0;a<targetUnit.length;a++){
                pair.add(currencyUnit+targetUnit[a]);     //combine the pair of currency to select exchange rate
            }

            String url = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.xchange%20where%20pair%20in%20(%22"+pair.get(0)+"%22";
            for(int i=1;i<pair.size();i++)
                url+="%2C%22"+pair.get(i)+"%22";
            url+=")&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=";
            Log.d("Exchange table",url);
            GetServer g = new GetServer(url);
            Thread t = new Thread(g);
            t.start();
            try {
                t.join();
                Log.d("json",g.getData());
                JSONObject jsonObject = new JSONObject(g.getData());
                JSONArray rate = jsonObject.getJSONObject("query").getJSONObject("results").getJSONArray("rate");
                for(int i=0;i<rate.length();i++){
                    String name = rate.getJSONObject(i).getString("Name");
                    String exchangeRate = rate.getJSONObject(i).getString("Rate");
                    String date = dateFormat.format(new Date());
                    String [] parts = name.split("/"); //split Name: HKD/USD
                    String unit = parts[0];
                    String target = parts[1];
                    LocalDB.fullInsert("ExchangeRate",new String[]{"'"+unit+"'","'"+target+"'","'"+exchangeRate+"'","'"+date+"'"});
                    Log.d("ExchangeRate", unit + "," + target + "," + exchangeRate + "," + date);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static String convertRate(String targetUnit,int money){
        try {
            String currencyUnit = LocalDB.getDataBySQL("SELECT currencyUnit FROM ExchangeRate", 0, "currencyUnit"); //the currencyUnit which user default to choose
            double exchangeRate = Double.parseDouble(LocalDB.getDataBySQL("SELECT targetRate FROM ExchangeRate WHERE currencyUnit = '" + currencyUnit + "' AND targetUnit = '" + targetUnit + "'", 0, "targetRate"));
            int convertMoney = (int) (money / exchangeRate);
            return convertMoney+"";
        }catch (Exception e){
            return money+"";
        }
    }

    public static int convertRate(String currencyUnit,String targetUnit,int money){
        String url = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.xchange%20where%20pair%20in%20(%22"+currencyUnit+targetUnit+"%22)&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=";
        GetServer g = new GetServer(url);
        Thread t = new Thread(g);
        t.start();
        try {
            t.join();
            JSONObject jsonObject = new JSONObject(g.getData());
            JSONObject rate = jsonObject.getJSONObject("query").getJSONObject("results").getJSONObject("rate");
            double exchangeRate = Double.parseDouble(rate.getString("Rate"));
            int convertMoney = (int)(money/exchangeRate);
            return convertMoney;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
