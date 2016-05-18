package com.example.ctfung.exchangerateproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class MoneyRecordActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private Toolbar toolbar;
    private Spinner spinner_nav;
    String [] types = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_money_record);
        types = new String[]{"日","月","年"};
        toolbar = (Toolbar)findViewById(R.id.app_bar);
        spinner_nav = (Spinner) findViewById(R.id.spinner_nav);
        ArrayAdapter<String > aa = new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,types);
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
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
