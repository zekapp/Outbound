package com.outbound.view;

import android.app.ActionBar;
import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.outbound.R;

/**
 * Created by zeki on 15/09/2014.
 */
public class EventsFragment extends BaseFragment {

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        View viewActionBar = activity.getLayoutInflater().inflate(R.layout.custom_ab_events, null);
        TextView title = (TextView)viewActionBar.findViewById(R.id.action_bar_title);
        title.setText(getResources().getString(R.string.action_bar_events_title));
        ActionBar actionBar = activity.getActionBar();
        if(actionBar!=null) {
            actionBar.setCustomView(viewActionBar);
        }
    }
}
