package com.outbound.view;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.outbound.R;
import com.outbound.model.PTrip;
import com.outbound.model.PUser;
import com.outbound.ui.util.SwipeRefreshLayout;
import com.outbound.ui.util.adapters.MyTravelHistoryAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.List;

import static com.outbound.util.LogUtils.*;

/**
 * Created by zeki on 24/09/2014.
 */
public class TravelHistoryFragment extends BaseFragment {
    private static final String TAG = makeLogTag(TravelHistoryFragment.class);

    private MyTravelHistoryAdapter mAdapter;
    private ListView mListView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void setUp(Object param1, Object param2) {
        super.setUp(param1,param2);
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        setUpActionBar(activity);
    }
    private void setUpActionBar(Activity activity) {
        View viewActionBar = activity.getLayoutInflater().inflate(R.layout.custom_ab_back_button, null);
        TextView title = (TextView)viewActionBar.findViewById(R.id.action_bar_title);
        title.setText(getResources().getString(R.string.my_travel_history_fragment_title));
        ImageView icon = (ImageView)viewActionBar.findViewById(R.id.ab_icon_1);
        icon.setImageResource(R.drawable.action_add);
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
        final View view = inflater.inflate(R.layout.list_view_layout, container, false);
        setUpSwipeRefreshLayout(view);
        setUpMyTravelHistoryListView(view);
        return view;
    }

    private void setUpMyTravelHistoryListView(View v) {

        mAdapter = new MyTravelHistoryAdapter(getActivity());

        PTrip.findUserSpecificHistoryTrips(PUser.getCurrentUser(),new FindCallback<PTrip>() {
            @Override
            public void done(List<PTrip> pTrips, ParseException e) {
                if(e == null){
                    mAdapter.addAll(pTrips);
                }else
                {
                    LOGD(TAG, "setUpMyTravelHistoryListView: " + e.getMessage());
                    showToastMessage("Network Error. Check connection");
                }

            }
        });

        mListView = (ListView) v.findViewById(R.id.list_view);
        mListView.setAdapter(mAdapter);
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
                    //get the latest events
                }
            });
        }
    }

}
