package com.outbound.ui.util.adapters;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.outbound.R;
import com.outbound.model.PTrip;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by zeki on 24/09/2014.
 */
public class MyTravelHistoryAdapter extends ArrayAdapter<PTrip> {

    private LayoutInflater inflater;
    private SimpleDateFormat formatter;
    public MyTravelHistoryAdapter(FragmentActivity activity) {
        super(activity, R.layout.my_travel_history_list_item);

        inflater = (LayoutInflater) getContext().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);

        formatter = new SimpleDateFormat("dd/MM/yyyy");
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if(view == null){
            view = inflater.inflate(R.layout.my_travel_history_list_item,parent,false);
            holder = new ViewHolder();
            holder.cityCountry = (TextView)view.findViewById(R.id.th_city_country);
            holder.date = (TextView)view.findViewById(R.id.th_date);
        }else{
            holder = (ViewHolder)view.getTag();
        }

        final PTrip trip = getItem(position);

        holder.cityCountry.setText(trip.getCity() + "," + trip.getCountry());
        holder.date.setHint(formatter.format(trip.getFromDate()) + " to " + formatter.format(trip.getToDate()));

        return view;
    }

    private static class ViewHolder {
        TextView cityCountry;
        TextView date;
    }
}
