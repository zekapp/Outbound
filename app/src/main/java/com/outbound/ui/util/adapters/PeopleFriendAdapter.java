package com.outbound.ui.util.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.outbound.R;
import com.outbound.model.PUser;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseImageView;
import com.parse.ParseObject;

/**
 * Created by zeki on 1/10/2014.
 */
public class PeopleFriendAdapter extends ArrayAdapter<PUser>{

    private LayoutInflater inflater;
    public PeopleFriendAdapter(Context context) {
        super(context,0);
        inflater = (LayoutInflater)getContext().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        final ViewHolder holder;
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

        final PUser user = getItem(position);

        user.fetchIfNeededInBackground(new GetCallback<PUser>() {
            @Override
            public void done(PUser fetchedUser, ParseException e) {
                if(e == null){
//                    int flag = getContext().getResources().
//                            getIdentifier("drawable/" + fetchedUser
//                                            .getCountryCode().toLowerCase(),
//                                    null, getContext().getPackageName());

                    holder.userName.setText(user.getUserName());
//                    holder.userName.setCompoundDrawablesWithIntrinsicBounds(flag, 0, 0, 0);
                    holder.distance.setText(user.calculateDistanceinKmTo(PUser.getCurrentUser()));
                    holder.photo.setParseFile(user.getProfilePicture());
                    holder.photo.loadInBackground();
                }
            }
        });
        return view;
    }

    private static class ViewHolder{
        ParseImageView photo;
        TextView userName;
        TextView distance;
    }
}
