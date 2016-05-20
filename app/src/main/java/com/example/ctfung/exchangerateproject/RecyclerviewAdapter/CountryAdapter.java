package com.example.ctfung.exchangerateproject.RecyclerviewAdapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ctfung.exchangerateproject.R;
import com.squareup.picasso.Picasso;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by CTFung on 14/5/16.
 */
public class CountryAdapter extends RecyclerView.Adapter<CountryAdapter.ViewHolder> {
    List<Country> countries;
    Context context;

    public CountryAdapter(Context context, List<Country> countries) {
        this.countries = countries;
        this.context = context;
    }

    @Override
    public CountryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder vh;
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_exchange_country, parent, false);
        vh = new ViewHolder(itemLayoutView);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.itemView.setTag(countries.get(position));
        Country item = countries.get(position);
        Picasso.with(context).load(item.getFlagId()).fit().into(holder.icon);
        holder.tvName.setText(item.getCurrencyName());
        holder.tvMoney.setText(item.getConvertedMoney());
        holder.tvConvert.setText(item.getConvertedRate());
    }

    @Override
    public int getItemCount() {
        return countries.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView icon;
        public TextView tvName, tvMoney, tvConvert;

        public ViewHolder(View itemView) {
            super(itemView);
            icon = (CircleImageView)itemView.findViewById(R.id.icon);
            tvName = (TextView)itemView.findViewById(R.id.tvName);
            tvMoney = (TextView)itemView.findViewById(R.id.tvMoney);
            tvConvert = (TextView)itemView.findViewById(R.id.tvConvert);
        }
    }
}
