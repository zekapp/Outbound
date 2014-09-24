package com.outbound.ui.util.adapters;

import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.outbound.R;

import java.util.ArrayList;

/**
 * Created by zeki on 24/09/2014.
 */
public class MyTravelHistoryAdapter extends ArrayAdapter<Object> {
    public MyTravelHistoryAdapter(FragmentActivity activity, ArrayList<Object> objectArrayList) {
        super(activity, R.layout.my_travel_history_list_item,objectArrayList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null)
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_travel_history_list_item,parent,false);

        TextView cityCountry = (TextView)convertView.findViewById(R.id.th_city_country);
        TextView date = (TextView)convertView.findViewById(R.id.th_date);

        return convertView;
    }
}
