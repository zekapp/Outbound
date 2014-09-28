package com.outbound.ui.util.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.outbound.R;
import com.outbound.util.MessageDummyClass;
import com.parse.ParseImageView;

import java.util.ArrayList;
import java.util.TreeSet;

/**
 * Created by zeki on 25/09/2014.
 */
public class MessageListViewAdapter extends ArrayAdapter<MessageDummyClass>{

    private static final int TYPE_LEFT = 0;
    private static final int TYPE_RIGHT = 1;
    private static final int TYPE_MAX_COUNT = TYPE_RIGHT + 1;

    private ArrayList<MessageDummyClass> mData = new ArrayList<MessageDummyClass>();
    private LayoutInflater mInflater;

    private TreeSet mLeftItemSeperatorSet = new TreeSet();

    public MessageListViewAdapter(Context context, ArrayList<MessageDummyClass> messages) {
        super(context, R.layout.message_list_view_item_right,messages);
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void addItemRight(final MessageDummyClass item){
        mData.add(item);
        notifyDataSetChanged();
    }

    public void addItemleft(final MessageDummyClass item){
        mData.add(item);

        mLeftItemSeperatorSet.add(mData.size() != 0? mData.size() - 1 : 0);
        notifyDataSetChanged();

    }

    @Override
    public int getItemViewType(int position) {
        return mLeftItemSeperatorSet.contains(position)?TYPE_LEFT:TYPE_RIGHT;
    }
    @Override
    public int getViewTypeCount() {
        return TYPE_MAX_COUNT;
    }

    @Override
    public MessageDummyClass getItem(int position) {
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        int type = getItemViewType(position);

        if (convertView == null){
            holder = new ViewHolder();
            switch (type){
                case TYPE_LEFT:
                    convertView = mInflater.inflate(R.layout.message_list_view_item_left, null);
                    holder.message_text = (TextView)convertView.findViewById(R.id.ma_message_text);
                    break;
                case TYPE_RIGHT:
                    convertView = mInflater.inflate(R.layout.message_list_view_item_right, null);
                    holder.message_text = (TextView)convertView.findViewById(R.id.ma_message_text);
                    break;
            }
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }

        if(mData.get(position).isOwnerMessage)
            holder.message_text.setText(R.string.test_message_short);
        else
            holder.message_text.setText(R.string.test_message);

        return convertView;
    }

    public static class ViewHolder {
        public TextView message_text;
        public ParseImageView  parseImageView;
        public TextView user_name;
        public TextView message_time;
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