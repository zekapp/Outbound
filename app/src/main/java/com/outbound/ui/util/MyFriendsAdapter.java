package com.outbound.ui.util;

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
public class MyFriendsAdapter extends ArrayAdapter<Object> {
    private boolean isPending;
    public MyFriendsAdapter(Context contex, ArrayList<Object> objectArrayList, boolean isPendingFriends){
        super(contex, R.layout.friends_list_item,objectArrayList);
        isPending = isPendingFriends;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null)
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.friends_list_item,parent,false);

        TextView userName = (TextView)convertView.findViewById(R.id.f_user_name);
        TextView distance = (TextView)convertView.findViewById(R.id.f_distance);

        convertView.findViewById(R.id.fp_accept_button_layout).setVisibility(isPending?View.VISIBLE:View.GONE);
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
