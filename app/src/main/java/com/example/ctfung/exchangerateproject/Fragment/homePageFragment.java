package com.example.ctfung.exchangerateproject.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ctfung.exchangerateproject.Activity.AddIncomeActivity;
import com.example.ctfung.exchangerateproject.Activity.AddPayActivity;
import com.example.ctfung.exchangerateproject.Activity.MoneyRecordActivity;
import com.example.ctfung.exchangerateproject.LocalDB;
import com.example.ctfung.exchangerateproject.R;

import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 */
public class homePageFragment extends Fragment implements View.OnClickListener {
    private LinearLayout add_income,add_pay,show_record,show_analysis;
    private TextView tvIncome, tvPay, tvTotal, tvMonth;

    public homePageFragment() {
        // Required empty public constructor
    }

    public static homePageFragment newInstance() {
        homePageFragment fragment = new homePageFragment();
        return fragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_record_money, container, false);
        add_income = (LinearLayout)view.findViewById(R.id.add_income);
        add_pay = (LinearLayout)view.findViewById(R.id.add_pay);
        show_record = (LinearLayout)view.findViewById(R.id.show_record);
        show_analysis = (LinearLayout)view.findViewById(R.id.show_analysis);
        tvIncome = (TextView)view.findViewById(R.id.tvIncome);
        tvPay = (TextView)view.findViewById(R.id.tvPay);
        tvTotal = (TextView)view.findViewById(R.id.tvTotal);
        tvMonth = (TextView)view.findViewById(R.id.tvMonth);
        add_income.setOnClickListener(this);
        add_pay.setOnClickListener(this);
        show_record.setOnClickListener(this);
        show_analysis.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        Intent i;
        switch (v.getId()){
            case R.id.add_income:
                i = new Intent(getActivity(),AddIncomeActivity.class);
                startActivity(i);
                break;
            case R.id.add_pay:
                i = new Intent(getActivity(),AddPayActivity.class);
                startActivity(i);
                break;
            case R.id.show_record:
                i = new Intent(getActivity(),MoneyRecordActivity.class);
                startActivity(i);
                break;
            case R.id.show_analysis:
                Toast.makeText(getActivity(),"show_analysis",Toast.LENGTH_LONG).show();
                break;
            default:
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        String currency = LocalDB.getDataBySQL("SELECT currency FROM SettingRecord",0,"currency");
        int income = Integer.parseInt(LocalDB.getDataBySQL("SELECT income FROM SettingRecord",0,"income"));
        int pay = Integer.parseInt(LocalDB.getDataBySQL("SELECT pay FROM SettingRecord",0,"pay"));
        int total = income-pay;
        if(income!=0)
            tvIncome.setText("+"+income+" "+currency);
        else
            tvIncome.setText(income+" "+currency);
        if(pay!=0)
            tvPay.setText("-"+pay+" "+currency);
        else
            tvPay.setText(pay+" "+currency);
        if(total>0)
            tvTotal.setText("+"+total+" "+currency);
        else
            tvTotal.setText(total+" "+currency);
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        tvMonth.setText("本月統計資料 - "+year+"年"+(++month)+"月");
    }
}
