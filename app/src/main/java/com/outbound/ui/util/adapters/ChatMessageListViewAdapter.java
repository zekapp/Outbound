package com.outbound.ui.util.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.outbound.R;
import com.outbound.model.PUser;
import com.outbound.ui.util.RoundedImageView;
import com.outbound.util.GenericMessage;
import com.outbound.util.MessageDummyClass;
import com.outbound.util.TimeUtil;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseImageView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

/**
 * Created by zeki on 25/09/2014.
 */
public class ChatMessageListViewAdapter extends ArrayAdapter<GenericMessage>{

    private static final int TYPE_LEFT = 0;
    private static final int TYPE_RIGHT = 1;
    private static final int TYPE_TIMESTAMP = 2;
    private static final int TYPE_MAX_COUNT = TYPE_TIMESTAMP + 1;

    private List<GenericMessage> mData = new ArrayList<GenericMessage>();
    private LayoutInflater mInflater;

//    private TreeSet mLeftItemSeperatorSet = new TreeSet();
    private List<Integer> mItemSeperator = new ArrayList<Integer>();

    public interface OnChatMessageItemClickedListener{
        void profilePictureClicked(PUser user);
    }

    private OnChatMessageItemClickedListener listener = null;
    public void addOnItemclickedListener(OnChatMessageItemClickedListener ltnr){
//        if( frag instanceof OnChatMessageItemClickedListener)
            listener = ltnr;
    }

    public ChatMessageListViewAdapter(Context context) {
        super(context, R.layout.message_list_view_item_right);
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void addTimeStampView(final GenericMessage item) {
        mData.add(item);
        mItemSeperator.add(TYPE_TIMESTAMP);
    }

    public void addItemRight(final GenericMessage item){
        mData.add(item);
        mItemSeperator.add(TYPE_RIGHT);
//        notifyDataSetChanged();
    }

    public void addItemleft(final GenericMessage item){
        mData.add(item);
        mItemSeperator.add(TYPE_LEFT);
//        mLeftItemSeperatorSet.add(mData.size() != 0? mData.size() - 1 : 0);
        notifyDataSetChanged();

    }

    @Override
    public int getItemViewType(int position) {
        return mItemSeperator.get(position);
//        return mLeftItemSeperatorSet.contains(position)?TYPE_LEFT:TYPE_RIGHT;
    }
    @Override
    public int getViewTypeCount() {
        return TYPE_MAX_COUNT;
    }

    @Override
    public GenericMessage getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        int type = getItemViewType(position);

        if (view == null){
            holder = new ViewHolder();
            switch (type){
                case TYPE_LEFT:
                    view = mInflater.inflate(R.layout.message_list_view_item_left, null);
                    holder.message_text = (TextView)view.findViewById(R.id.ma_message_text);
                    holder.photo = (RoundedImageView)view.findViewById(R.id.ma_user_photo);
                    holder.user_name = (TextView)view.findViewById(R.id.ma_user_name);
                    holder.message_time = (TextView)view.findViewById(R.id.ma_message_time);
                    break;
                case TYPE_RIGHT:
                    view = mInflater.inflate(R.layout.message_list_view_item_right, null);
                    holder.message_text = (TextView)view.findViewById(R.id.ma_message_text);
                    holder.photo = (RoundedImageView)view.findViewById(R.id.ma_user_photo);
                    holder.user_name = (TextView)view.findViewById(R.id.ma_user_name);
                    holder.message_time = (TextView)view.findViewById(R.id.ma_message_time);
                    break;
                case TYPE_TIMESTAMP:
                    view = mInflater.inflate(R.layout.message_list_view_item_timestamp, null);
                    holder.message_time = (TextView)view.findViewById(R.id.ma_message_time);
                    break;
            }
//            convertView.setTag(holder);
            view.setTag(holder);

        }else{
            holder = (ViewHolder)view.getTag();
        }

        final GenericMessage msg = getItem(position);

        if(type == TYPE_TIMESTAMP){
            holder.message_time.setHint(TimeUtil.convertDateFormatAccordingToday(msg.getCreatedAt()));
        }else{
            holder.photo.setParseFile(msg.getProfilePicture());
            holder.photo.loadInBackground();
            holder.user_name.setHint(msg.getUserName());
            holder.message_text.setText(msg.getMessage());
            holder.message_time.setHint(TimeUtil.convertTimeFormat(msg.getCreatedAt()));

            holder.photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PUser.fetchTheSpesificUserFromId(msg.getUserID(), new GetCallback<PUser>() {
                        @Override
                        public void done(PUser pUser, ParseException e) {
                            if(listener != null)
                                listener.profilePictureClicked(pUser);
                        }
                    });
                }
            });
        }


        return view;
    }

    public static class ViewHolder {
        public TextView message_text;
        public RoundedImageView photo;
        public TextView user_name;
        public TextView message_time;
    }

    @Override
    public void clear() {
        super.clear();
        mData.clear();
        mItemSeperator.clear();
    }
}
//DateFormat dateFormat = new SimpleDateFormat("hh:mm a");
//Date date = new Date();
//
//MessageDummyClass obj = getItem(position);
//
//convertView = LayoutInflater.
//        from(parent.getContext()).
//        inflate(obj.isOwnerMessage ?
//        R.layout.message_list_view_item_right :
//        R.layout.message_list_view_item_left, parent, false);
//
//        TextView textView = (TextView)convertView.findViewById(R.id.ma_message_time);
//        String message = dateFormat.format(date);
//        textView.setHint(" ");
//        textView.setHint(message);
//
//
//        TextView textMessageView = (TextView)convertView.findViewById(R.id.ma_message_text);
//        textMessageView.setText(" ");
//        if(obj.message != null)
//        textMessageView.setText(obj.message);
//        else
//        textMessageView.setText(R.string.test_message_short);
//
//        return convertView;