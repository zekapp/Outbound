package com.outbound.ui.util.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.outbound.R;
import com.outbound.model.PEvent;
import com.outbound.model.PUser;
import com.outbound.ui.util.RoundedImageView;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;

import java.text.SimpleDateFormat;
import java.util.List;

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
    public View getView(int position, View view, ViewGroup parent) {

        ViewHolder holder;

        if(view == null){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.events_list_item,parent,false);
            holder = new ViewHolder();
            holder.eventName = (TextView)view.findViewById(R.id.el_title);
            holder.date = (TextView)view.findViewById(R.id.el_date);
            holder.time =(TextView)view.findViewById(R.id.el_time);
            holder.attendingCount = (TextView)view.findViewById(R.id.el_attending_count);
            holder.photo = (RoundedImageView)view.findViewById(R.id.el_user_photo);
            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }

        final PEvent event = getItem(position);

        holder.eventName.setText(event.getEventName());
        holder.date.setHint(dateFormat.format(event.getStartDate()));
        holder.time.setHint(timeFormat.format(event.getStartTime()));

        final RoundedImageView photo = holder.photo;
        List<PUser> attendingPeople = event.getOutboundersGoing();
        if(attendingPeople != null){
            event.fetchEventCreater(new GetCallback<PUser>() {
                @Override
                public void done(PUser pUser, ParseException e) {
                    if(e == null) {
                        photo.setParseFile(pUser.getProfilePicture());
                        photo.loadInBackground();
                    }
                }
            });
            holder.attendingCount.setText(Integer.toString(attendingPeople.size()));
        }

//        event.fetchEventCreater(new );
//        attendingCount.setText(event.getOutboundersGoing().size());

//        event.getParseObject("createdBy").fetchIfNeededInBackground(new GetCallback<ParseObject>() {
//            @Override
//            public void done(ParseObject pUser, ParseException e) {
//                if(e == null){
//                    photo.setParseFile(((PUser)pUser).getProfilePicture());
//                    photo.loadInBackground();
//                }
//            }
//        });
//        photo.setParseFile(event.getCreatedBy().getProfilePicture());
//        photo.loadInBackground();

        return view;
    }

    private static class ViewHolder {
        RoundedImageView photo;
        TextView eventName;
        TextView date;
        TextView time;
        TextView attendingCount;
    }
}
