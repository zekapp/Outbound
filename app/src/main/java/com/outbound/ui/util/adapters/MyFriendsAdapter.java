package com.outbound.ui.util.adapters;

import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.outbound.R;
import com.outbound.model.PUser;
import com.parse.ParseGeoPoint;
import com.parse.ParseImageView;

import java.util.ArrayList;

/**
 * Created by zeki on 23/09/2014.
 */
public class MyFriendsAdapter extends ArrayAdapter<PUser> {
    private boolean isPending;
    private PUser currentUser = PUser.getCurrentUser();

    public MyFriendsAdapter(Context contex, boolean isPendingFriends){
        super(contex, R.layout.friends_list_item);
        isPending = isPendingFriends;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null)
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.friends_list_item,parent,false);

        PUser user = getItem(position);
        TextView userName = (TextView)convertView.findViewById(R.id.f_user_name);
        TextView distanceText = (TextView)convertView.findViewById(R.id.f_distance);
        ParseImageView photo = (ParseImageView)convertView.findViewById(R.id.f_user_photo);

        userName.setText(user.getUserName());
        photo.setParseFile(user.getCoverPicture());
        Double distance = currentUser.getCurrentLocation().distanceInKilometersTo(user.getCurrentLocation());
        String str = String.format("%1f",distance);
        distanceText.setText(str);

        convertView.findViewById(R.id.fp_accept_button_layout).setVisibility(isPending?View.VISIBLE:View.GONE);
        return convertView;
    }

    @Override
    public PUser getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

}
