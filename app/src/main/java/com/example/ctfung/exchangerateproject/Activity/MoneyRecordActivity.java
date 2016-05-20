package com.example.ctfung.exchangerateproject.Activity;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import com.example.ctfung.exchangerateproject.Fragment.AllRecordFragment;
import com.example.ctfung.exchangerateproject.Fragment.DayRecordFragment;
import com.example.ctfung.exchangerateproject.Fragment.MonthRecordFragment;
import com.example.ctfung.exchangerateproject.Fragment.WeekRecordFragment;
import com.example.ctfung.exchangerateproject.Fragment.YearRecordFragment;
import com.example.ctfung.exchangerateproject.R;
import com.gc.materialdesign.views.ButtonRectangle;

public class MoneyRecordActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {
    private Toolbar toolbar;
    private Spinner spinner_nav;
    ButtonRectangle btnSelect;
    String [] types = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_money_record);
        types = new String[]{"本日","本星期","本月","本年","全部"};
        toolbar = (Toolbar)findViewById(R.id.app_bar);
        spinner_nav = (Spinner) findViewById(R.id.spinner_nav);
        btnSelect = (ButtonRectangle)findViewById(R.id.btnSelect);
        btnSelect.setOnClickListener(this);
        ArrayAdapter<String > aa = new ArrayAdapter<String>(this,R.layout.spinner_toolbar_item,types);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_nav.setAdapter(aa);
        spinner_nav.setOnItemSelectedListener(this);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //getSupportActionBar().setHomeAsUpIndicator(R.drawable.cancel_button);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction();
        transaction.replace(R.id.root_frame, new DayRecordFragment());

        transaction.commit();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction();
        if(position==0)
            transaction.replace(R.id.root_frame, new DayRecordFragment());
        else if(position==1)
            transaction.replace(R.id.root_frame, new WeekRecordFragment());
        else if(position==2)
            transaction.replace(R.id.root_frame, new MonthRecordFragment());
        else if(position==3)
            transaction.replace(R.id.root_frame, new YearRecordFragment());
        else if(position==4)
            transaction.replace(R.id.root_frame, new AllRecordFragment());
        transaction.commit();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.btnSelect){

        }
    }

}
