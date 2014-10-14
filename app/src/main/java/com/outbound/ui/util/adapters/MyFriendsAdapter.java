package com.outbound.ui.util.adapters;

import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.outbound.R;
import com.outbound.model.PUser;
import com.outbound.ui.util.RoundedImageView;
import com.outbound.util.location.LocationUtils;
import com.parse.ParseGeoPoint;
import com.parse.ParseImageView;

import java.util.ArrayList;

/**
 * Created by zeki on 23/09/2014.
 */
public class MyFriendsAdapter extends ArrayAdapter<PUser> {
    private boolean isPending;
    private PUser currentUser = PUser.getCurrentUser();
    private LayoutInflater inflater;
    public MyFriendsAdapter(Context contex, boolean isPendingFriends){
        super(contex, R.layout.friends_list_item);
        isPending = isPendingFriends;

        inflater = (LayoutInflater) getContext().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;

        if(view == null){
            view = inflater.inflate(R.layout.friends_list_item, parent, false);
            holder = new ViewHolder();
            holder.userName = (TextView)view.findViewById(R.id.f_user_name);
            holder.distanceText = (TextView)view.findViewById(R.id.f_distance);
            holder.photo = (RoundedImageView)view.findViewById(R.id.f_user_photo);
            holder.acceptanceLayout = (LinearLayout)view.findViewById(R.id.fp_accept_button_layout);
            holder.accept = (ImageView)view.findViewById(R.id.fp_accept_button);
            holder.decline = (ImageView)view.findViewById(R.id.fp_decline_button);
        }else
        {
            holder = (ViewHolder)view.getTag();
        }

        final PUser user = getItem(position);

        holder.userName.setText(user.getUserName());
        Double distance = currentUser.getCurrentLocation().distanceInKilometersTo(user.getCurrentLocation());
        String str = String.format("%.2fkm",distance);
        holder.distanceText.setText(str);
        holder.photo.setParseFile(user.getProfilePicture());
        holder.photo.loadInBackground();
        holder.acceptanceLayout.setVisibility(isPending ? View.VISIBLE : View.GONE);
        if(isPending)
            setUpAcceptanceButton(holder);
        return view;
    }

    private void setUpAcceptanceButton(ViewHolder holder) {
        holder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifyDataSetChanged();
            }
        });

        holder.decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    private static class ViewHolder {
        TextView userName;
        TextView distanceText;
        RoundedImageView photo;
        LinearLayout acceptanceLayout;
        ImageView accept;
        ImageView decline;
    }

}
