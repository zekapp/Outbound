package com.outbound.ui.util.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.outbound.R;
import com.parse.ParseImageView;

/**
 * Created by zeki on 1/10/2014.
 */
public class NoticeBoardMessageAdapter extends ArrayAdapter<Object>{

    private LayoutInflater inflater;
    public NoticeBoardMessageAdapter(Context context){
        super(context, 0);

        inflater = (LayoutInflater)getContext().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;

        if(view == null){
            view = inflater.inflate(R.layout.item_notice_board_list, parent, false);
            holder = new ViewHolder();

            view.setTag(holder);
        }
        else
        {
            holder = (ViewHolder)view.getTag();
        }
        return view;
    }

    public static class ViewHolder{
        ParseImageView photo;
        TextView title;
        TextView postCount;
        TextView createDate;
        TextView message;
        TextView report;
        TextView bar;
        TextView block;
    }
}
