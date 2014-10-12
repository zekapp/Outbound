package com.outbound.ui.util.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.outbound.R;
import com.outbound.model.PEvent;

import java.text.SimpleDateFormat;

/**
 * Created by zeki on 30/09/2014.
 */
public class PeopleEventAdapter extends ArrayAdapter<PEvent> {

    private LayoutInflater inflater;
    private SimpleDateFormat dateFormat;
    private SimpleDateFormat timeFormat;

    public PeopleEventAdapter(Context context) {
        super(context, 0);

        inflater = (LayoutInflater) getContext().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);

        dateFormat = new SimpleDateFormat("dd/MM/yy");
        timeFormat = new SimpleDateFormat("K:mm a");
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        ViewHolder holder;
        // If a view hasn't been provided inflate on
        if(view == null){
            view = inflater.inflate(R.layout.people_event_list_item, parent, false);
            holder = new ViewHolder();
            holder.title = (TextView)view.findViewById(R.id.me_title);
            holder.date = (TextView)view.findViewById(R.id.me_date);
            holder.time = (TextView)view.findViewById(R.id.me_time);
            holder.attendingCount = (TextView)view.findViewById(R.id.me_attending_count);
            // Tag for lookup later
            view.setTag(holder);
        }else
        {
            holder = (ViewHolder) view.getTag();
        }
        final PEvent event = getItem(position);

        holder.title.setText(event.getEventName());
        holder.date.setHint(dateFormat.format(event.getStartDate()));
        holder.time.setHint(timeFormat.format(event.getStartDate()));

        if(event.getOutboundersGoing() != null)
            holder.attendingCount.setText(Integer.toString(event.getOutboundersGoing().size()));
        else
            holder.attendingCount.setText(" - ");

        return view;
    }

    private static class ViewHolder {
        TextView title;
        TextView date;
        TextView time;
        TextView attendingCount;
    }
}
