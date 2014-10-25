package com.outbound.ui.util.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.outbound.R;
import com.outbound.model.ChatMessage;
import com.outbound.model.PChatActivity;
import com.outbound.model.PUser;
import com.outbound.ui.util.RoundedImageView;
import com.outbound.util.GenericMessage;
import com.outbound.util.TimeUtil;
import com.parse.GetCallback;
import com.parse.ParseException;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import static com.outbound.util.LogUtils.LOGD;
import static com.outbound.util.LogUtils.makeLogTag;


/**
 * Created by zeki on 23/09/2014.
 */

public class ProfileMessageListViewAdapter extends ArrayAdapter<PChatActivity> {
    private static final String TAG = makeLogTag(ProfileMessageListViewAdapter.class);
    private LayoutInflater inflater;


    public interface OnChatActivityMsgItemClickedListener{
        void profilePictureClicked(PUser user);
    }
    private OnChatActivityMsgItemClickedListener listener = null;

    public void addOnNoticeBoardMsgItemClickedListener(Fragment frag){
        if( frag instanceof OnChatActivityMsgItemClickedListener)
            listener = (OnChatActivityMsgItemClickedListener)frag;
    }

    public ProfileMessageListViewAdapter(Context contex){
        super(contex, R.layout.profile_message_list_view_item);

        inflater = (LayoutInflater)getContext().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        final ViewHolder holder;

        if(view == null || view.getTag() == null){
            view = inflater.inflate(R.layout.profile_message_list_view_item, parent, false);
            holder = new ViewHolder();
            holder.photo = (RoundedImageView)view.findViewById(R.id.pm_user_photo);
            holder.nameSurname = (TextView)view.findViewById(R.id.pm_user_name);
            holder.messageTime = (TextView)view.findViewById(R.id.pm_message_time);
            holder.message = (TextView)view.findViewById(R.id.pm_message);
            view.setTag(holder);
        }else{
            holder = (ViewHolder)view.getTag();
        }

        final  PChatActivity post = getItem(position);

        List<GenericMessage> messageList = post.getAllMessages();
        if (messageList == null) {
            LOGD(TAG, "messageList- Unexpected Null. Check database.... ");
            return  view;
        }

        final GenericMessage msg = messageList.get(messageList.size() - 1);

        holder.photo.setParseFile(msg.getProfilePicture());
        holder.photo.loadInBackground();
        holder.photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PUser.fetchTheSpesificUserFromId(msg.getUserID(), new GetCallback<PUser>() {
                    @Override
                    public void done(PUser pUser, ParseException e) {
                        if(listener != null){
                            listener.profilePictureClicked(pUser);
                        }
                    }
                });
            }
        });
        holder.nameSurname.setText(msg.getUserName());
        holder.message.setHint(msg.getMessage());
        holder.messageTime.setHint(TimeUtil.convertDateProperFormat(msg.getCreatedAt()));
//        holder.messageTime.setHint(convertDateProperFormat(new Date()));

        return view;
    }

    public List<PChatActivity> getAllItem() {
        int count = getCount();
        List<PChatActivity> currentPost = new ArrayList<PChatActivity>();
        for (int i = 0; i < count; i++) {
            PChatActivity post = getItem(i);
            currentPost.add(post);
        }
        return currentPost;
    }


    @Override
    public boolean hasStableIds() {
        return true;
    }

    public static class ViewHolder{
        RoundedImageView photo;
        TextView nameSurname;
        TextView messageTime;
        TextView message;
    }

    public void orderItemsAccordingTimeOrder(){
        int count = getCount();
        List<PChatActivity> currentPost = new ArrayList<PChatActivity>();
        for (int i = 0; i < count; i++) {
            PChatActivity post = getItem(i);
            currentPost.add(post);
        }

        Collections.sort(currentPost,new Comparator<PChatActivity>() {
            @Override
            public int compare(PChatActivity lhs, PChatActivity rhs) {
                List<GenericMessage> messagesLhs = lhs.getAllMessages();
                GenericMessage lastMessageLhs = messagesLhs.get(messagesLhs.size()-1);

                List<GenericMessage> messagesRhs = rhs.getAllMessages();
                GenericMessage lastMessageRhs = messagesRhs.get(messagesRhs.size()-1);


                Date date1 = lastMessageLhs.getCreatedAt();
                Date date2 = lastMessageRhs.getCreatedAt();
                return date2.compareTo(date1);
            }
        });
        clear();
        addAll(currentPost);
//        notifyDataSetChanged();
    }
}
