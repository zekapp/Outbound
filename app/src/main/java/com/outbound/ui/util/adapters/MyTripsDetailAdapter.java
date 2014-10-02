package com.outbound.ui.util.adapters;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.outbound.R;

import java.util.ArrayList;

/**
 * Created by zeki on 2/10/2014.
 */
public class MyTripsDetailAdapter extends ArrayAdapter<Object>{
    private LayoutInflater inflater;
    public MyTripsDetailAdapter(Context context) {
        super(context,0);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        // If a view hasn't been provided inflate on
        if(view == null){
            view = inflater.inflate(R.layout.item_my_trips_detail_fragment, parent, false);
            holder = new ViewHolder();
            // Tag for lookup later
            view.setTag(holder);
        }else
        {
            holder = (ViewHolder) view.getTag();
        }

        return view;
    }

    public static class ViewHolder{

    }

}
