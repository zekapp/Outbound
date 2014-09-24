package com.outbound.ui.util.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.outbound.R;

import java.util.ArrayList;

/**
 * Created by zeki on 23/09/2014.
 */

public class ProfileMessageListViewAdapter extends ArrayAdapter<Object> {
    // Todo: Change object appropriate with class name
    //
    public ProfileMessageListViewAdapter(Context contex, ArrayList<Object> objectArrayList){
        super(contex, R.layout.profile_message_list_view_item,objectArrayList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null)
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_message_list_view_item,parent,false);

        TextView userName = (TextView)convertView.findViewById(R.id.pm_user_name);
        TextView messageTime = (TextView)convertView.findViewById(R.id.pm_message_time);
        TextView message = (TextView)convertView.findViewById(R.id.pm_message);

        return convertView;
    }

    @Override
    public Object getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
