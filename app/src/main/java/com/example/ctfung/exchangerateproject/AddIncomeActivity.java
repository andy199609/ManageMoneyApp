package com.example.ctfung.exchangerateproject;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.codetroopers.betterpickers.numberpicker.NumberPickerBuilder;
import com.codetroopers.betterpickers.numberpicker.NumberPickerDialogFragment;
import com.gc.materialdesign.views.ButtonRectangle;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import dmax.dialog.SpotsDialog;

public class AddIncomeActivity extends AppCompatActivity implements View.OnClickListener,NumberPickerDialogFragment.NumberPickerDialogHandlerV2, DatePickerDialog.OnDateSetListener {
    TextView tvType, tvMoney, tvTime, tvWrite, tvCurrency;
    LinearLayout linearType, linearMoney, linearTime, linearWrite, linearCurrency;
    ButtonRectangle btnPost, btnCancel;
    final String [] currencyName = {"港幣","美金","人民幣","台幣","日圓","韓元","歐元","泰幣","加拿幣","英鎊"};
    final String [] currencyUnit = {"HKD","USD","CNY","TWD","JPY","KRW","EUR","THB","CAD","GBP"};
    int tempCurrencyPosition = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_income);
        Toolbar toolbar = (android.support.v7.widget.Toolbar) this.findViewById(R.id.toolbar);
        toolbar.setTitle("新增收入");
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        this.setSupportActionBar(toolbar);
        tvType = (TextView)findViewById(R.id.tvType);
        tvMoney = (TextView)findViewById(R.id.tvMoney);
        tvTime = (TextView)findViewById(R.id.tvTime);
        tvWrite = (TextView)findViewById(R.id.tvWrite);
        tvCurrency = (TextView)findViewById(R.id.tvCurrency);
        linearType = (LinearLayout)findViewById(R.id.linearType);
        linearMoney = (LinearLayout)findViewById(R.id.linearMoney);
        linearTime = (LinearLayout)findViewById(R.id.linearTime);
        linearWrite = (LinearLayout)findViewById(R.id.linearWrite);
        linearCurrency = (LinearLayout)findViewById(R.id.linearCurrency);
        btnPost = (ButtonRectangle)findViewById(R.id.btnPost);
        btnCancel = (ButtonRectangle)findViewById(R.id.btnCancel);
        btnPost.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        linearType.setOnClickListener(this);
        linearWrite.setOnClickListener(this);
        linearMoney.setOnClickListener(this);
        linearTime.setOnClickListener(this);
        linearCurrency.setOnClickListener(this);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        tvTime.setText(dateFormat.format(date));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.linearType:
                final String [] type = new String [] {"公司收入","投資收入","其他"};
                int position = 0;
                for(int i=0;i<type.length;i++){
                    if(type[i].equals(tvType.getText().toString())){
                        position=i;
                    }
                }
                new MaterialDialog.Builder(this)
                        .title("分類")
                        .items(type)
                        .itemsCallbackSingleChoice(position, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                tvType.setText(type[which]);

                                dialog.setSelectedIndex(which);
                                return true;
                            }
                        })
                        .positiveText("確定")
                        .show();
                break;
            case R.id.linearMoney:
                NumberPickerBuilder npb = new NumberPickerBuilder()
                        .setFragmentManager(getSupportFragmentManager())
                        .setStyleResId(R.style.BetterPickersDialogFragment_Light)
                        .setMinNumber(BigDecimal.valueOf(0));
                npb.show();
                break;
            case R.id.linearCurrency:
                for(int i=0;i<currencyUnit.length;i++){
                    if(currencyUnit[i].equals(tvCurrency.getText().toString())){
                        tempCurrencyPosition=i;
                    }
                }
                new MaterialDialog.Builder(this)
                        .title("貨幣")
                        .items(currencyName)
                        .itemsCallbackSingleChoice(tempCurrencyPosition, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                tvCurrency.setText(currencyUnit[which]);

                                dialog.setSelectedIndex(which);
                                return true;
                            }
                        })
                        .positiveText("確定")
                        .show();
                break;
            case R.id.linearTime:
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        AddIncomeActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "Datepickerdialog");
                break;
            case R.id.linearWrite:
                String tempInput = tvWrite.getText().toString();
                if(tempInput.equals("空"))
                    tempInput="";
                new MaterialDialog.Builder(this)
                        .title("備註")
                        .input("", tempInput, new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog, CharSequence input) {
                                if (input.length()==0)
                                    tvWrite.setText("空");
                                else
                                    tvWrite.setText(input);
                            }
                        }).show();
                break;
            case R.id.btnPost:
                if(!tvMoney.getText().equals("0"))
                    new UpdateData().execute();
                else
                    Toast.makeText(getBaseContext(),"現金必需大於0",Toast.LENGTH_LONG).show();
                break;
            case R.id.btnCancel:
                onBackPressed();
                break;
            default:
                break;
        }
    }

    @Override
    public void onDialogNumberSet(int reference, BigInteger number, double decimal, boolean isNegative, BigDecimal fullNumber) {
        tvMoney.setText(fullNumber.intValue()+"");
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        tvTime.setText(year+"-"+(++monthOfYear)+"-"+dayOfMonth);
    }

    public class UpdateData extends AsyncTask<Void, Void, Void> {     //This class use to load the data from server first time
        //declare other objects as per your need
        AlertDialog dialog;
        String type,cash,currencyUnit,time,note;
        int newIncome;
        @Override
        protected void onPreExecute()
        {
            dialog = new SpotsDialog(AddIncomeActivity.this,"正在儲存資料...");
            dialog.show();
            type = tvType.getText().toString();
            cash = tvMoney.getText().toString();
            currencyUnit = tvCurrency.getText().toString();
            time = tvTime.getText().toString();
            note = tvWrite.getText().toString();
            newIncome = Integer.parseInt(LocalDB.getDataBySQL("SELECT income FROM SettingRecord",0,"income"))+Integer.parseInt(cash);
            //progressWheel.setVisibility(View.VISIBLE);
            //do initialization of required objects objects here
        };
        @Override
        protected Void doInBackground(Void... params)
        {
            LocalDB.fullInsert("MoneyRecord",new String [] {"'"+type+"'","'"+cash+"'","'"+currencyUnit+"'","'"+time+"'",
                "'"+note+"'","'Y'"});
            LocalDB.update("SettingRecord",new String [] {"income = "+newIncome},new String[]{"1=1"});
            return null;
        }
        @Override
        protected void onPostExecute(Void result)
        {
            super.onPostExecute(result);
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                    dialog.dismiss();
                            onBackPressed();
                    //progressWheel.setVisibility(View.GONE);
                }
            }, 1000);
        };
    }
}
