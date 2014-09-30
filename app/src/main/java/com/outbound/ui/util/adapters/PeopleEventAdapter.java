package com.outbound.ui.util.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.outbound.R;

/**
 * Created by zeki on 30/09/2014.
 */
public class PeopleEventAdapter extends ArrayAdapter<Object> {

    private LayoutInflater inflater;

    public PeopleEventAdapter(Context context) {
        super(context, 0);

        inflater = (LayoutInflater) getContext().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        ViewHolder holder;
        // If a view hasn't been provided inflate on
        if(view == null){
            view = inflater.inflate(R.layout.people_event_list_item, parent, false);
            holder = new ViewHolder();
            // Tag for lookup later
            view.setTag(holder);
        }else
        {
            holder = (ViewHolder) view.getTag();
        }

        return view;
    }

    private static class ViewHolder {

    }
}
