package com.example.ctfung.exchangerateproject.RecyclerviewAdapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.ctfung.exchangerateproject.ExchangeRateConvertor;
import com.example.ctfung.exchangerateproject.LocalDB;
import com.example.ctfung.exchangerateproject.R;
import com.gc.materialdesign.views.Card;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by CTFung on 19/5/16.
 */
public class MoneyAdapter extends RecyclerView.Adapter<MoneyAdapter.ViewHolder> {
    static List<MoneyRecord> moneyRecordList;
    static Context context;

    public MoneyAdapter(Context context, List<MoneyRecord> moneyRecordList) {
        this.context = context;
        this.moneyRecordList = moneyRecordList;
    }

    @Override
    public MoneyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder vh;
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_money_record, parent, false);
        vh = new ViewHolder(itemLayoutView);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.itemView.setTag(moneyRecordList.get(position));
        MoneyRecord item = moneyRecordList.get(position);
        Picasso.with(context).load(item.getIconId()).fit().into(holder.icon);
        holder.tvTitle.setText(item.getTitle());
        holder.tvMoney.setText(item.getMoney());
        holder.tvDate.setText(item.getDate());
        holder.tvNote.setText(item.getNote());
        if(item.getMoney().contains("+"))
            holder.tvMoney.setTextColor(Color.parseColor("#04B404"));
        else
            holder.tvMoney.setTextColor(Color.parseColor("#FE2E2E"));
    }

    @Override
    public int getItemCount() {
        return moneyRecordList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        public ImageView icon;
        public TextView tvTitle, tvMoney, tvDate, tvNote;
        public CardView summaryCard;

        public ViewHolder(View itemView) {
            super(itemView);
            icon = (ImageView) itemView.findViewById(R.id.icon);
            tvTitle = (TextView)itemView.findViewById(R.id.tvTitle);
            tvMoney = (TextView)itemView.findViewById(R.id.tvMoney);
            tvDate = (TextView)itemView.findViewById(R.id.tvDate);
            tvNote = (TextView)itemView.findViewById(R.id.tvNote);
            summaryCard = (CardView)itemView.findViewById(R.id.summaryCard);
            summaryCard.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View v) {
            v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            new MaterialDialog.Builder(context)
                    .title("刪除紀錄")
                    .content("你確定要刪除此紀錄嗎?")
                    .positiveText("確定")
                    .negativeText("取消")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            try {
                                Log.d("position", getAdapterPosition() + "");
                                int money, newIncome, newPay;
                                String title, currencyUnit, isIncome;
                                String[] arrayText;
                                title = moneyRecordList.get(getAdapterPosition()).getMoney();
                                arrayText = title.split(" ");
                                money = Integer.parseInt(arrayText[0].substring(1, arrayText[0].length()));
                                currencyUnit = arrayText[1];
                                money = new ExchangeRateConvertor(context, LocalDB.getDataBySQL("SELECT currencyUnit FROM ExchangeRate", 0, "currencyUnit")).convertRate(LocalDB.getDataBySQL("SELECT currencyUnit FROM ExchangeRate", 0, "currencyUnit"),currencyUnit, money);
                                Log.d("test", currencyUnit+","+money);
                                isIncome = LocalDB.getDataBySQL("SELECT isIncome FROM MoneyRecord WHERE mid = " + moneyRecordList.get(getAdapterPosition()).getMid(), 0, "isIncome");
                                if (isIncome.equals("Y")) {
                                    newIncome = Integer.parseInt(LocalDB.getDataBySQL("SELECT income FROM SettingRecord", 0, "income"));  //oldIncome
                                    newIncome = newIncome - money;
                                    LocalDB.update("SettingRecord", new String[]{"income = " + newIncome}, new String[]{"1=1"});
                                } else {
                                    newPay = Integer.parseInt(LocalDB.getDataBySQL("SELECT pay FROM SettingRecord", 0, "pay"));  //oldIncome
                                    newPay = newPay - money;
                                    LocalDB.update("SettingRecord", new String[]{"pay = " + newPay}, new String[]{"1=1"});
                                }
                                LocalDB.delete("MoneyRecord", new String[]{"mid = " + moneyRecordList.get(getAdapterPosition()).getMid()});
                                removeAt(getAdapterPosition());
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    })
                    .show();
            return false;
        }

        public void removeAt(int position) {
            moneyRecordList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, moneyRecordList.size());
        }

        public int getTotalHeight(RecyclerView recyclerView){
            int height=0;
            for(int i=0;i<recyclerView.getChildCount();i++){
                height+=recyclerView.getChildAt(i).getHeight();
            }
            return height;
        }

    }
}
