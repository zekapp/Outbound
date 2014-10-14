package com.outbound.ui.util.adapters;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.outbound.R;
import com.outbound.model.PTrip;
import com.outbound.model.PUser;
import com.outbound.ui.util.RoundedImageView;
import com.parse.GetCallback;
import com.parse.ParseException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import static com.outbound.util.LogUtils.*;

/**
 * Created by zeki on 2/10/2014.
 */
public class MyTripsDetailAdapter extends ArrayAdapter<PUser>{
    private static final String TAG = makeLogTag(MyTripsDetailAdapter.class);
    private LayoutInflater inflater;
    private PTrip trip;
    private SimpleDateFormat formatter;
    public MyTripsDetailAdapter(Context context, PTrip trip) {
        super(context,0);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.trip = trip;
        formatter = new SimpleDateFormat("dd/MM/yyyy");
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        // If a view hasn't been provided inflate on
        if(view == null){
            view = inflater.inflate(R.layout.item_my_trips_detail_fragment, parent, false);
            holder = new ViewHolder();
            holder.photo = (RoundedImageView)view.findViewById(R.id.pm_user_photo);
            holder.name = (TextView)view.findViewById(R.id.mt_name_surname);
            holder.date = (TextView)view.findViewById(R.id.mt_date);
            // Tag for lookup later
            view.setTag(holder);
        }else
        {
            holder = (ViewHolder) view.getTag();
        }
        final PUser user = getItem(position);

        holder.name.setText(user.getUserName());
        holder.photo.setParseFile(user.getProfilePicture());
        holder.photo.loadInBackground();

        final TextView date = holder.date;
        PTrip.findUserTripInSpecificTripDateInterval(trip, user, new GetCallback<PTrip>() {
            @Override
            public void done(PTrip pTrip, ParseException e) {
                if(e == null){
                    date.setHint(formatter.format(pTrip.getFromDate()) + " to " + formatter.format(pTrip.getToDate()));
                }else
                {
                    LOGD(TAG, "PTrip.findUserTripInSpecificTripDateInterval  e: "+ e.getMessage());
                }
            }
        });

        return view;
    }

    public static class ViewHolder{
        RoundedImageView photo;
        TextView name;
        TextView date;
    }

}
