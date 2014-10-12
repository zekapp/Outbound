package com.outbound.ui.util.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.outbound.R;
import com.outbound.model.PUser;
import com.outbound.ui.util.RoundedImageView;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseImageView;
import com.parse.ParseObject;

import static com.outbound.util.LogUtils.*;

/**
 * Created by zeki on 30/09/2014.
 */
public class EventDetailsAdapter extends ArrayAdapter<PUser>{

    private static final String TAG = makeLogTag(EventDetailsAdapter.class);

    private LayoutInflater inflater;
    private PUser currentUser = PUser.getCurrentUser();

    public EventDetailsAdapter(Context context) {
        super(context, 0);

        inflater = (LayoutInflater) getContext().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        // If a view hasn't been provided inflate on
        if(view == null){
            view = inflater.inflate(R.layout.grid_item, parent, false);
            holder = new ViewHolder();
            // Tag for lookup later
            holder.photo = (RoundedImageView)view.findViewById(R.id.gi_photo);
            view.setTag(holder);
        }else
        {
            holder = (ViewHolder) view.getTag();
        }
        final PUser people = getItem(position);

        final RoundedImageView photo = holder.photo;

        people.fetchIfNeededInBackground(new GetCallback<PUser>() {
            @Override
            public void done(PUser pUser, ParseException e) {
                if (e == null) {
                    LOGD(TAG, "userName: " + pUser.getUserName());
                    photo.setParseFile(pUser.getProfilePicture());
                    photo.loadInBackground();
                }
            }
        });

        return view;
    }

    private static class ViewHolder {
        RoundedImageView photo;
    }
}
