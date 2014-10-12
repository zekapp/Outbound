package com.outbound.ui.util.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.outbound.R;
import com.outbound.model.PEvent;
import com.outbound.model.PUser;
import com.outbound.ui.util.RoundedImageView;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;

import java.text.SimpleDateFormat;

/**
 * Created by zeki on 24/09/2014.
 */
public class EventsAdapter extends ArrayAdapter<PEvent> {
    private SimpleDateFormat dateFormat;
    private SimpleDateFormat timeFormat;
    public EventsAdapter(Context context) {
        super(context, R.layout.events_list_item);
        dateFormat = new SimpleDateFormat("dd/MM/yy");
        timeFormat = new SimpleDateFormat("K:mm a");
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        PEvent event = getItem(position);

        if(convertView == null)
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.events_list_item,parent,false);

        TextView eventName = (TextView)convertView.findViewById(R.id.el_title);
        TextView date = (TextView)convertView.findViewById(R.id.el_date);
        TextView time = (TextView)convertView.findViewById(R.id.el_time);
        TextView attendingCount = (TextView)convertView.findViewById(R.id.el_attending_count);
        final RoundedImageView photo = (RoundedImageView)convertView.findViewById(R.id.el_user_photo);

        eventName.setText(event.getEventName());
        date.setHint(dateFormat.format(event.getStartDate()));
        time.setHint(timeFormat.format(event.getStartDate()));
//        attendingCount.setText(event.getOutboundersGoing().size());

        event.getParseObject("createdBy").fetchIfNeededInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject pUser, ParseException e) {
                photo.setParseFile(((PUser)pUser).getProfilePicture());
                photo.loadInBackground();
            }
        });
//        photo.setParseFile(event.getCreatedBy().getProfilePicture());
//        photo.loadInBackground();

        return convertView;
    }
}
