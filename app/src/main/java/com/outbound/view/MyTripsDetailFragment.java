package com.outbound.view;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.outbound.R;
import com.outbound.model.PTrip;
import com.outbound.model.PUser;
import com.outbound.ui.util.SwipeRefreshLayout;
import com.outbound.ui.util.adapters.MyTripsDetailAdapter;
import com.outbound.util.Constants;
import com.parse.FindCallback;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.List;

import static com.outbound.util.LogUtils.LOGD;
import static com.outbound.util.LogUtils.makeLogTag;

/**
 * Created by zeki on 2/10/2014.
 */
public class MyTripsDetailFragment extends BaseFragment {
    private static final String TAG = makeLogTag(MyTripsDetailFragment.class);

    private MyTripsDetailAdapter mAdapter;
    private ListView mListView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView actionBarTitle;
    private PTrip trip;

    @Override
    protected void setUp(Object param1, Object param2) {
        super.setUp(param1,param2);

        if(param1 instanceof PTrip)
            trip = (PTrip)param1;
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }
    private void setUpActionBar(Activity activity) {
        View viewActionBar = activity.getLayoutInflater().inflate(R.layout.custom_ab_back_button, null);
        actionBarTitle = (TextView)viewActionBar.findViewById(R.id.action_bar_title);
//        actionBarTitle.setText(getResources().getString(R.string.my_trips_detail_fragment_title));
        actionBarTitle.setText(trip.getCity()+", "+trip.getCountry());
        ImageView icon = (ImageView)viewActionBar.findViewById(R.id.ab_icon_1);
        icon.setEnabled(false);


        ActionBar actionBar = activity.getActionBar();
        if(actionBar!=null) {
            actionBar.setCustomView(viewActionBar);
        }

        ImageView backIcon = (ImageView)viewActionBar.findViewById(R.id.ab_back_icon);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallbacks.backIconClicked();
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setUpActionBar(getActivity());
        final View view = inflater.inflate(R.layout.my_trips_detail_fragment_layout, container, false);

        setUpSwipeRefreshLayout(view);
        setUpMyTripDetailListView(view);
        setActionBarTitle();

        return view;
    }

    private void setActionBarTitle() {
        // set the title
    }

    private void setUpMyTripDetailListView(View v) {

        mAdapter = new MyTripsDetailAdapter(getActivity(),trip);
//        ArrayList<Object> test = new ArrayList<Object>();
//        for (int i = 0; i < 50; i++) {
//            mAdapter.add(new Object());
//        }

        if(trip != null){
            PTrip.findUsersAttendingThisTrip(trip, new FindCallback<PUser>() {
                @Override
                public void done(List<PUser> pUsers, ParseException e) {
                    if(e == null){
                        for(PUser user : pUsers){
                            mAdapter.add(user);
                        }
                        updateView();
                    }else {
                        LOGD(TAG, "findUsersAttendingThisTrip: " + e.getMessage());
                    }
                }
            });
        }

        mListView = (ListView) v.findViewById(R.id.list_view);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(mCallbacks !=null)
                    mCallbacks.deployFragment(Constants.PEOPLE_PROFILE_FRAG_ID, parent.getAdapter().getItem(position), null);
            }
        });
    }

    private void updateView() {
        if(mAdapter != null)
            mAdapter.notifyDataSetChanged();
    }

    private void setUpSwipeRefreshLayout(View view) {
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.lw_swipe_refresh_layout);
        if (mSwipeRefreshLayout != null){
            mSwipeRefreshLayout.setColorScheme(
                    R.color.refresh_progress_1,
                    R.color.refresh_progress_2,
                    R.color.refresh_progress_3,
                    R.color.refresh_progress_4);

            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    //get the latest status of this outbounder
                }
            });
        }

        if (mSwipeRefreshLayout != null) {
//            mSwipeRefreshLayout.setEnabled(false);
        }
    }
}
