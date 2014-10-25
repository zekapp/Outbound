package com.outbound.ui.util.adapters;

import android.app.Dialog;
import android.content.Context;
import android.location.Location;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.outbound.R;
import com.outbound.model.PFriendRequest;
import com.outbound.model.PUser;
import com.outbound.ui.util.RoundedImageView;
import com.outbound.util.ResultCallback;
import com.outbound.util.location.LocationUtils;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseImageView;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import static com.outbound.util.LogUtils.*;

/**
 * Created by zeki on 23/09/2014.
 */
public class MyFriendsAdapter extends ArrayAdapter<PUser> {
    private static final String TAG = makeLogTag(MyFriendsAdapter.class);
    private boolean isPending;
    private PUser currentUser = PUser.getCurrentUser();
    private LayoutInflater inflater;
    private FriendAcceptenceCallbacks callbacks;
    private Context context;
    private Dialog progress;

    public interface FriendAcceptenceCallbacks{
        void friendRequestAccepted();
        void friendRequestDeclined();
    }

    public MyFriendsAdapter(Context contex, boolean isPendingFriends, Fragment fragment){
        super(contex, R.layout.friends_list_item);
        isPending = isPendingFriends;

        inflater = (LayoutInflater) getContext().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);

        this.context = contex;
        try {
            callbacks = (FriendAcceptenceCallbacks) fragment;
        } catch (ClassCastException e) {
            throw new ClassCastException("Fragment must implement FriendAcceptenceCallbacks.");
        }
    }


    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;

        if(view == null || view.getTag() == null){
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
            setUpAcceptanceButton(holder,user);
        return view;
    }

    private void setUpAcceptanceButton(final ViewHolder holder, final PUser user) {
        holder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disableButtons(holder);
                showToasMessege("Friend request from " + user.getUserName() + " accepting...");
                PFriendRequest.acceptFriendRequest(user, new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e == null){
                            remove(user);
                            notifyDataSetChanged();
                            if(callbacks!=null)
                                callbacks.friendRequestAccepted();
                            showToasMessege("Friend request accepted!");
                        }else
                        {
                            LOGD(TAG, "setUpAcceptanceButton e:" + e.getMessage());
                        }
                    enableButtons(holder);
                    }
                });
            }
        });

        holder.decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disableButtons(holder);
                showToasMessege("Friend request from " + user.getUserName() + " declining...");
                PFriendRequest.declineFriendReuest(user, new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        enableButtons(holder);
                        if(e == null){
                            remove(user);
                            notifyDataSetChanged();
                            if(callbacks!=null)
                                callbacks.friendRequestDeclined();
                            showToasMessege("Friend request declined!");
                        }else
                        {
                            showToasMessege("Network error...");
                            LOGD(TAG, "setUpAcceptanceButton -decline e:" + e.getMessage());
                        }
                    }
                });

            }
        });
    }

    private void showToasMessege(String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    private void enableButtons(ViewHolder holder) {
        holder.accept.setEnabled(true);
        holder.decline.setEnabled(true);
        holder.accept.setAlpha((float) 1);
        holder.decline.setAlpha((float) 1);
    }

    private void disableButtons(ViewHolder holder) {
        holder.accept.setEnabled(false);
        holder.decline.setEnabled(false);
        holder.accept.setAlpha((float) 0.2);
        holder.decline.setAlpha((float) 0.2);
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
