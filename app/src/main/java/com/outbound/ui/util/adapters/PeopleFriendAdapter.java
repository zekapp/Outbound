package com.outbound.ui.util.adapters;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.outbound.R;
import com.parse.ParseImageView;

/**
 * Created by zeki on 1/10/2014.
 */
public class PeopleFriendAdapter extends ArrayAdapter<Object>{

    private LayoutInflater inflater;
    public PeopleFriendAdapter(Context context) {
        super(context,0);
        inflater = (LayoutInflater)getContext().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view == null){
            view = inflater.inflate(R.layout.item_people_friend_list,parent,false);
            // Cache view components into the view holder
            holder = new ViewHolder();
            holder.photo = (ParseImageView)view.findViewById(R.id.pf_user_photo);
            holder.userName = (TextView)view.findViewById(R.id.pf_user_name);
            holder.distance = (TextView)view.findViewById(R.id.pf_distance);

            view.setTag(holder);
        }else {
            holder = (ViewHolder)view.getTag();
        }

        return view;
    }

    private static class ViewHolder{
        ParseImageView photo;
        TextView userName;
        TextView distance;
    }
}
