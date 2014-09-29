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
public class EventsAdapter extends ArrayAdapter<Object> {
    public EventsAdapter(FragmentActivity activity, ArrayList<Object> objectArrayList) {
        super(activity, R.layout.events_list_item, objectArrayList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null)
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.events_list_item,parent,false);

        TextView eventName = (TextView)convertView.findViewById(R.id.me_title);
        TextView date = (TextView)convertView.findViewById(R.id.me_date);
        TextView time = (TextView)convertView.findViewById(R.id.me_time);
        TextView attendingCount = (TextView)convertView.findViewById(R.id.me_attending_count);

        return convertView;
    }
}
