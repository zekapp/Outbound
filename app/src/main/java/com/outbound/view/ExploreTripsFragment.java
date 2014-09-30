package com.outbound.view;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.outbound.R;
import com.outbound.util.Constants;

import static com.outbound.util.Constants.*;
/**
 * Created by zeki on 15/09/2014.
 */
public class ExploreTripsFragment extends BaseFragment {
    private int  mBaseActivityFrameLayoutId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        final View view = inflater.inflate(R.layout.explore_trips_fragment, container, false);

        setUpButtonsFunction(view);

        return view;
    }

    private void setUpButtonsFunction(View view) {
        ImageView exploreTrips = (ImageView)view.findViewById(R.id.et_explore_trips);
        exploreTrips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallbacks.deployFragment(EXPLORE_TRIPS_SEARCH_TRIP_ID,null,null);
            }
        });
        ImageView addTrip = (ImageView)view.findViewById(R.id.et_add_new_trip);
        addTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallbacks.deployFragment(EXPLORE_TRIPS_ADD_NEW_TRIPS_ID,null,null);
            }
        });
    }

    @Override
    protected void setUp(int baseActivityFrameLayoutId, Object param1, Object param2) {
        super.setUp(baseActivityFrameLayoutId,param1,param2);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        ActionBar.LayoutParams params = new ActionBar.LayoutParams(
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);

        View viewActionBar = activity.getLayoutInflater().inflate(R.layout.custom_ab_explore_trips, null);
        TextView title = (TextView)viewActionBar.findViewById(R.id.action_bar_title);
        title.setText(getResources().getString(R.string.action_bar_explore_trips_title));
        ActionBar actionBar = activity.getActionBar();
        if(actionBar!=null) {
            actionBar.setCustomView(viewActionBar,params);
        }
    }
}
