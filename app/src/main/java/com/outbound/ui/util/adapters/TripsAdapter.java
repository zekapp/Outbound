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
import com.parse.FindCallback;
import com.parse.ParseException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zeki on 24/09/2014.
 */
public class TripsAdapter extends ArrayAdapter<PTrip>{

    private LayoutInflater inflater;
    private SimpleDateFormat formatter;

    public TripsAdapter(Context context) {
        super(context, R.layout.my_trips_list_item);

        inflater = (LayoutInflater) getContext().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);

        formatter = new SimpleDateFormat("dd/MM/yyyy");
    }


    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;

        if(view == null || view.getTag() == null){
            view = inflater.inflate(R.layout.my_trips_list_item, parent, false);
            holder = new ViewHolder();
            holder.cityCountry = (TextView)view.findViewById(R.id.mt_city_country);
            holder.date = (TextView)view.findViewById(R.id.mt_date);
            holder.attendingCount = (TextView)view.findViewById(R.id.my_attending_count);
        }else
        {
            holder = (ViewHolder)view.getTag();
        }

        final PTrip trip = getItem(position);

        if(trip.getCity() == null && trip.getCountry() != null)
            holder.cityCountry.setText(trip.getCountry());
        else if(trip.getCity() != null &&  trip.getCountry() == null)
            holder.cityCountry.setText(trip.getCity());
        else
            holder.cityCountry.setText(trip.getCity() + ", "+ trip.getCountry());

        final TextView attendingCount = holder.attendingCount;

        holder.date.setHint(formatter.format(trip.getFromDate())+" to "+formatter.format(trip.getToDate()));
        PTrip.findUsersAttendingThisTrip(trip,new FindCallback<PUser>() {
            @Override
            public void done(List<PUser> pUsers, ParseException e) {
                attendingCount.setText(Integer.toString(pUsers.size()));
            }
        });

        return view;
    }

    private static class ViewHolder {
        TextView cityCountry;
        TextView date;
        TextView attendingCount;
    }
}
