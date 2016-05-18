package com.example.ctfung.exchangerateproject;


import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.ctfung.exchangerateproject.common.MyLinearLayoutManager;
import com.example.ctfung.exchangerateproject.server.GetServer;
import com.pnikosis.materialishprogress.ProgressWheel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import dmax.dialog.SpotsDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExchangeRateFragment extends Fragment implements View.OnClickListener{
    private ArrayList<Country> countryList;
    private ScrollView scrollView;
    private CardView cardView;
    private String [] currencyName = {"港幣","美金","人民幣","台幣","日圓","韓元","歐元","泰幣","加拿幣","英鎊"};
    private String [] currencyUnit = {"HKD","USD","CNY","TWD","JPY","KRW","EUR","THB","CAD","GBP"};
    private int [] flagId = {R.drawable.hkd,R.drawable.usd,R.drawable.cny,R.drawable.twd,R.drawable.jpy,
            R.drawable.krw,R.drawable.eur,R.drawable.thb,R.drawable.cad,R.drawable.gbp};
    private Spinner spUnit;
    private EditText etMoney;
    private Button btnPost;
    private RecyclerView recyclerView;
    private ProgressWheel progressWheel;
    private String defaultCurrenctUnit = "HKD";
    private LinearLayout linear1;

    public ExchangeRateFragment() {
        // Required empty public constructor
    }

    public static ExchangeRateFragment newInstance() {
        ExchangeRateFragment fragment = new ExchangeRateFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_exchange_rate, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new MyLinearLayoutManager(getActivity(),1,false));
        recyclerView.setNestedScrollingEnabled(false);
        scrollView = (ScrollView)view.findViewById(R.id.scrollView);
        cardView = (CardView)view.findViewById(R.id.summaryCard);
        progressWheel = (ProgressWheel)view.findViewById(R.id.progress_wheel);
        spUnit = (Spinner)view.findViewById(R.id.spUnit);
        etMoney = (EditText)view.findViewById(R.id.etMoney);
        linear1 = (LinearLayout)view.findViewById(R.id.linear1);
        btnPost = (Button)view.findViewById(R.id.btnOk);
        btnPost.setOnClickListener(this);
        UpdateExchangeRate task = new UpdateExchangeRate();
        task.execute();
        return view;
    }

    public int getTotalHeight(RecyclerView recyclerView){
        int height=0;
        for(int i=0;i<recyclerView.getChildCount();i++){
            height+=recyclerView.getChildAt(i).getHeight();
            Log.d("Height",height+"");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            height=4000;
        }
        return height;
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.btnOk){
            if(etMoney.getText().length()!=0){
                UpdateData task = new UpdateData();
                task.execute();
            }
        }
    }

    public class UpdateExchangeRate extends AsyncTask<Void, Void, Void> {     //This class use to load the data from server first time
        //declare other objects as per your need
        AlertDialog dialog;
        CountryAdapter adapter;

        @Override
        protected void onPreExecute()
        {
            progressWheel.setVisibility(View.VISIBLE);
            cardView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            recyclerView.setHasFixedSize(true);
            dialog = new SpotsDialog(getActivity(),"正在更新最新匯率...");
            dialog.show();
            //dialog.setTitle();
            //dialog.show();
            //do initialization of required objects objects here
        };
        @Override
        protected Void doInBackground(Void... params)
        {
            ExchangeRateConvertor exchangeRateConvertor = new ExchangeRateConvertor(getActivity(),LocalDB.getDataBySQL("SELECT currency FROM SettingRecord",0,"currency"));
            exchangeRateConvertor.updateExchangeTable();
            countryList = new ArrayList<>();
            countryList.clear();
            for(int i=0;i<10;i++){
                String exchangeRate = LocalDB.getDataBySQL("SELECT targetRate FROM ExchangeRate WHERE targetUnit = '"+currencyUnit[i]+"'",0,"targetRate");
                String unit = LocalDB.getDataBySQL("SELECT currencyUnit FROM ExchangeRate",0,"currencyUnit");
                String target = LocalDB.getDataBySQL("SELECT targetUnit FROM ExchangeRate WHERE targetUnit = '"+currencyUnit[i]+"'",0,"targetUnit");
                countryList.add(new Country(i,flagId[i],target, "---", "1 "+target+" : "+exchangeRate+" "+unit));
            }
            adapter = new CountryAdapter(getActivity(),countryList);

            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {
            super.onPostExecute(result);
            dialog.dismiss();
            recyclerView.setAdapter(new CountryAdapter(getActivity(),countryList));
            progressWheel.setVisibility(View.GONE);
            cardView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
            //recyclerView.setMinimumHeight(getTotalHeight(recyclerView)); //for device 5.0
            //Toast.makeText(getActivity(),recyclerView.getAdapter().getItemCount()+"",Toast.LENGTH_LONG).show();
            recyclerView.setMinimumHeight(getTotalHeight(recyclerView)); //for device v10
            //InitData task = new InitData();
            //task.execute();
        };
    }

    public class UpdateData extends AsyncTask<Void, Void, Void> {     //This class use to load the data from server first time
        //declare other objects as per your need
        AlertDialog dialog;
        String inputMoney;
        String currency;
        CountryAdapter adapter;
        @Override
        protected void onPreExecute()
        {
            dialog = new SpotsDialog(getActivity(),"正在更新最新匯率...");
            dialog.show();
            //progressWheel.setVisibility(View.VISIBLE);
            cardView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            recyclerView.setHasFixedSize(true);
            inputMoney = etMoney.getText().toString();
            defaultCurrenctUnit = currencyUnit[spUnit.getSelectedItemPosition()];
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(linear1.getWindowToken(), 0);
            //do initialization of required objects objects here
        };
        @Override
        protected Void doInBackground(Void... params)
        {
            ArrayList<String> pair = new ArrayList<>();
            countryList = new ArrayList<>();
            countryList.clear();
            for(int a=0;a<currencyUnit.length;a++)
                pair.add(defaultCurrenctUnit+currencyUnit[a]);     //combine the pair of currency to select exchange rate
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
                Log.d("json", g.getData());
                JSONObject jsonObject = new JSONObject(g.getData());
                JSONArray rate = jsonObject.getJSONObject("query").getJSONObject("results").getJSONArray("rate");
                for (int i = 0; i < rate.length(); i++) {
                    String name = rate.getJSONObject(i).getString("Name");
                    String exchangeRate = rate.getJSONObject(i).getString("Rate");
                    String[] parts = name.split("/"); //split Name: HKD/USD
                    String unit = parts[0];
                    String target = parts[1];
                    NumberFormat formatter = new DecimalFormat("#0.00");
                    double finalMoney = Double.parseDouble(inputMoney)*Double.parseDouble(exchangeRate);
                    countryList.add(new Country(i,flagId[i],target, formatter.format(finalMoney)+" "+target, "1 "+target+" : "+exchangeRate+" "+defaultCurrenctUnit));
                }
                adapter = new CountryAdapter(getActivity(),countryList);
            }catch (Exception e){

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {
            super.onPostExecute(result);
            recyclerView.setAdapter(new CountryAdapter(getActivity(),countryList));
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    dialog.dismiss();
                    //progressWheel.setVisibility(View.GONE);
                    cardView.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);
                    recyclerView.setMinimumHeight(getTotalHeight(recyclerView)); //for device 5.0
                }
            }, 500);
        };
    }
}

