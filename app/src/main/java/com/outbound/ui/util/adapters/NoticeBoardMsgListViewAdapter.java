package com.outbound.ui.util.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.outbound.R;
import com.outbound.util.MessageDummyClass;

/**
 * Created by zeki on 2/10/2014.
 */
public class NoticeBoardMsgListViewAdapter extends ArrayAdapter<MessageDummyClass> {

    private LayoutInflater inflater;
    public NoticeBoardMsgListViewAdapter(Context context) {
        super(context, 0);

        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if(view == null){
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.item_notice_board_message_activity, null);
            view.setTag(holder);
        }else{
            holder = (ViewHolder)view.getTag();
        }

        return view;
    }

    public static class ViewHolder{

    }
}
