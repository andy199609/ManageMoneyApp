package com.example.ctfung.exchangerateproject.Fragment;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ctfung.exchangerateproject.LocalDB;
import com.example.ctfung.exchangerateproject.R;
import com.example.ctfung.exchangerateproject.RecyclerviewAdapter.MoneyAdapter;
import com.example.ctfung.exchangerateproject.RecyclerviewAdapter.MoneyRecord;
import com.example.ctfung.exchangerateproject.common.MyLinearLayoutManager;
import com.example.ctfung.exchangerateproject.common.ScrollViewExt;
import com.example.ctfung.exchangerateproject.common.ScrollViewListener;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class AllRecordFragment extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener,ScrollViewListener {
    private RecyclerView recyclerView;
    private ProgressWheel loading,beginLoading;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ScrollViewExt scrollView;
    private List<MoneyRecord> moneyRecordList = null;
    private MoneyAdapter moneyAdapter = null;
    private int tempLatestRow=0;
    private boolean canLoadMore=true;

    public AllRecordFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_record_list, container, false);
        recyclerView = (RecyclerView)view.findViewById(R.id.myEventList);
        recyclerView.setLayoutManager(new MyLinearLayoutManager(getActivity(), 1, false));
        recyclerView.setNestedScrollingEnabled(false);
        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipeRefresh);
        loading = (ProgressWheel)view.findViewById(R.id.loading);
        beginLoading = (ProgressWheel)view.findViewById(R.id.beginLoading);
        moneyRecordList = new ArrayList<MoneyRecord>();
        scrollView = (ScrollViewExt)view.findViewById(R.id.scrollView);
        scrollView.setScrollViewListener(this);
        swipeRefreshLayout.setOnRefreshListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onRefresh() {
        refresh();
    }

    @Override
    public void onScrollChanged(ScrollViewExt scrollView, int x, int y, int oldx, int oldy) {
        View view = (View) scrollView.getChildAt(scrollView.getChildCount() - 1);
        int diff = (view.getBottom() - (scrollView.getHeight() + scrollView.getScrollY()));
        // if diff is zero, then the bottom has been reached
        if (diff == 0) {
            // do stuff
            if(canLoadMore) {
                LoadMoreData task = new LoadMoreData();
                task.execute();
            }
        }
    }

    public void onResume(){
        super.onResume();
        InitData task = new InitData();
        task.execute();
    }

    public int getTotalHeight(RecyclerView recyclerView){
        int height=0;
        for(int i=0;i<recyclerView.getChildCount();i++){
            height+=recyclerView.getChildAt(i).getHeight();
            Log.d("Height",height+"");
        }
        return height;
    }

    public void refresh(){
        InitData task = new InitData();
        task.execute();
    }

    public class InitData extends AsyncTask<Void, Void, Void> {     //This class use to load the data from server first time
        //declare other objects as per your need
        @Override
        protected void onPreExecute() {
            if(!swipeRefreshLayout.isRefreshing()){
                swipeRefreshLayout.setRefreshing(true);
            }
            recyclerView.setVisibility(View.GONE);
            recyclerView.setHasFixedSize(true);
            //do initialization of required objects objects here
        }
        @Override
        protected Void doInBackground(Void... params) {
            try {
                moneyRecordList.clear();
                tempLatestRow = 0;
                canLoadMore = true;
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String lastDay = dateFormat.format(new Date());
                String [] dateArray = lastDay.split("-");
                String firstDay = dateArray[0]+"-01-01";
                Log.d("First Day",firstDay);
                Log.d("Last Day",lastDay);
                int rowCount = LocalDB.getTableRowCount("MoneyRecord ORDER BY date(time) DESC LIMIT " + tempLatestRow + ",10;");
                for (int i = 0; i < rowCount; i++) {
                    int mid,iconId;
                    String title, date, money, note;
                    mid = Integer.parseInt(LocalDB.getDataBySQL("SELECT mid FROM MoneyRecord ORDER BY date(time) DESC LIMIT " + tempLatestRow + ",10;", i, "mid"));
                    iconId = Integer.parseInt(LocalDB.getDataBySQL("SELECT iconId FROM MoneyRecord ORDER BY date(time) DESC LIMIT " + tempLatestRow + ",10;", i, "iconId"));
                    if (LocalDB.getDataBySQL("SELECT isIncome FROM MoneyRecord ORDER BY date(time) DESC LIMIT " + tempLatestRow + ",10;", i, "isIncome").equals("Y")) {
                        title = "收入 - " + LocalDB.getDataBySQL("SELECT type FROM MoneyRecord ORDER BY date(time) DESC LIMIT " + tempLatestRow + ",10;", i, "type");
                        money = "+" + LocalDB.getDataBySQL("SELECT cash FROM MoneyRecord ORDER BY date(time) DESC LIMIT " + tempLatestRow + ",10;", i, "cash") + " " + LocalDB.getDataBySQL("SELECT currencyUnit FROM MoneyRecord ORDER BY date(time) DESC LIMIT " + tempLatestRow + ",10;", i, "currencyUnit");
                    } else {
                        title = "支出 - " + LocalDB.getDataBySQL("SELECT type FROM MoneyRecord ORDER BY date(time) DESC LIMIT " + tempLatestRow + ",10;", i, "type");
                        money = "-" + LocalDB.getDataBySQL("SELECT cash FROM MoneyRecord ORDER BY date(time) DESC LIMIT " + tempLatestRow + ",10;", i, "cash") + " " + LocalDB.getDataBySQL("SELECT currencyUnit FROM MoneyRecord ORDER BY date(time) DESC LIMIT " + tempLatestRow + ",10;", i, "currencyUnit");
                    }
                    date = LocalDB.getDataBySQL("SELECT time FROM MoneyRecord ORDER BY date(time) DESC LIMIT " + tempLatestRow + ",10;", i, "time");
                    note = LocalDB.getDataBySQL("SELECT note FROM MoneyRecord ORDER BY date(time) DESC LIMIT " + tempLatestRow + ",10;", i, "note");
                    moneyRecordList.add(new MoneyRecord(mid,iconId, title, date, money, note));
                }
                moneyAdapter = new MoneyAdapter(getActivity(), moneyRecordList);
                if (rowCount < 10) {
                    tempLatestRow += 10;
                    canLoadMore = true;
                } else
                    canLoadMore = false;
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            beginLoading.setVisibility(View.GONE);
            if(swipeRefreshLayout.isRefreshing()){
                swipeRefreshLayout.setRefreshing(false);
            }
            if(canLoadMore) {
                recyclerView.setVisibility(View.VISIBLE);
                recyclerView.setAdapter(moneyAdapter);
                recyclerView.setMinimumHeight(getTotalHeight(recyclerView));
            }
        }
    }

    public class LoadMoreData extends AsyncTask<Void, Void, Void> {
        //declare other objects as per your need
        @Override
        protected void onPreExecute() {
            loading.setVisibility(View.VISIBLE);
            //do initialization of required objects objects here
        }
        @Override
        protected Void doInBackground(Void... params) {
            try {
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String lastDay = dateFormat.format(new Date());
                String [] dateArray = lastDay.split("-");
                String firstDay = dateArray[0]+"-01-01";
                Log.d("First Day", firstDay);
                Log.d("Last Day", lastDay);
                int rowCount = LocalDB.getTableRowCount("MoneyRecord ORDER BY date(time) DESC ORDER BY date(time) DESC LIMIT " + tempLatestRow + ",10;");
                for (int i = 0; i < rowCount; i++) {
                    int mid,iconId;
                    String title, date, money, note;
                    mid = Integer.parseInt(LocalDB.getDataBySQL("SELECT mid FROM MoneyRecord ORDER BY date(time) DESC LIMIT " + tempLatestRow + ",10;", i, "mid"));
                    iconId = Integer.parseInt(LocalDB.getDataBySQL("SELECT iconId FROM MoneyRecord ORDER BY date(time) DESC LIMIT " + tempLatestRow + ",10;", i, "iconId"));
                    if (LocalDB.getDataBySQL("SELECT isIncome FROM MoneyRecord ORDER BY date(time) DESC LIMIT " + tempLatestRow + ",10;", i, "isIncome").equals("Y")) {
                        title = "收入 - " + LocalDB.getDataBySQL("SELECT type FROM MoneyRecord ORDER BY date(time) DESC LIMIT " + tempLatestRow + ",10;", i, "type");
                        money = "+" + LocalDB.getDataBySQL("SELECT cash FROM MoneyRecord ORDER BY date(time) DESC LIMIT " + tempLatestRow + ",10;", i, "cash") + " " + LocalDB.getDataBySQL("SELECT currencyUnit FROM MoneyRecord ORDER BY date(time) DESC LIMIT " + tempLatestRow + ",10;", i, "currencyUnit");
                    } else {
                        title = "支出 - " + LocalDB.getDataBySQL("SELECT type FROM MoneyRecord ORDER BY date(time) DESC LIMIT " + tempLatestRow + ",10;", i, "type");
                        money = "-" + LocalDB.getDataBySQL("SELECT cash FROM MoneyRecord ORDER BY date(time) DESC LIMIT " + tempLatestRow + ",10;", i, "cash") + " " + LocalDB.getDataBySQL("SELECT currencyUnit FROM MoneyRecord ORDER BY date(time) DESC LIMIT " + tempLatestRow + ",10;", i, "currencyUnit");
                    }
                    date = LocalDB.getDataBySQL("SELECT time FROM MoneyRecord ORDER BY date(time) DESC LIMIT " + tempLatestRow + ",10;", i, "time");
                    note = LocalDB.getDataBySQL("SELECT note FROM MoneyRecord ORDER BY date(time) DESC LIMIT " + tempLatestRow + ",10;", i, "note");
                    moneyRecordList.add(new MoneyRecord(mid,iconId, title, date, money, note));
                }
                moneyAdapter = new MoneyAdapter(getActivity(), moneyRecordList);
                if (rowCount < 10) {
                    tempLatestRow += 10;
                    canLoadMore = true;
                } else
                    canLoadMore = false;
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            recyclerView.setMinimumHeight(getTotalHeight(recyclerView));
            loading.setVisibility(View.GONE);
        }
    }
}
