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
import com.outbound.model.PEvent;
import com.outbound.model.PUser;
import com.outbound.ui.util.SwipeRefreshLayout;
import com.outbound.ui.util.adapters.EventsAdapter;

import java.util.ArrayList;
import java.util.List;

import com.outbound.util.Constants;
import com.parse.FindCallback;
import com.parse.ParseException;

/**
 * Created by zeki on 15/09/2014.
 */
public class EventsFragment extends BaseFragment {

    private EventsAdapter mAdapter;
    private ListView mListView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private PUser currentUser = PUser.getCurrentUser();

    private List<PEvent> eventList = null;
    @Override
    protected void setUp( Object param1, Object param2) {
        super.setUp(param1,param2);

        if(param1 instanceof List)
            eventList = (List<PEvent>)param1;
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    private void setUpActionBar(Activity activity) {
        View viewActionBar = activity.getLayoutInflater().inflate(R.layout.custom_ab_events, null);
        TextView title = (TextView)viewActionBar.findViewById(R.id.action_bar_title);
        if(eventList == null)
            title.setText(getResources().getString(R.string.action_bar_events_title));
        else
            title.setText(getResources().getString(R.string.action_bar_events_result_title));

        ImageView icon = (ImageView)viewActionBar.findViewById(R.id.ab_events_search_icon);
        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // deploy the event search fragment
                if(mCallbacks != null)
                    mCallbacks.deployFragment(Constants.EVENT_SEARCH_FRAGMENT_ID,null,null);
            }
        });
        ActionBar actionBar = activity.getActionBar();
        if(actionBar!=null) {
            actionBar.setCustomView(viewActionBar);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setUpActionBar(getActivity());
        final View view = inflater.inflate(R.layout.list_view_layout, container, false);
        setUpSwipeRefreshLayout(view);
        setUpEventsAroundMeListView(view);
        return view;
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
                    //get the arround me
                    findEventsAraoundCurrentUser();
                }
            });
        }
    }

    private void setUpEventsAroundMeListView(View v) {
//        ArrayList<Object> test = new ArrayList<Object>();
//        for (int i = 0; i < 50; i++) {
//            test.add(new Object());
//        }
        mAdapter = new EventsAdapter(getActivity());

        findEventsAraoundCurrentUser();

        mListView = (ListView) v.findViewById(R.id.list_view);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(mCallbacks != null)
                    mCallbacks.deployFragment(Constants.EVENT_DETAIL_FRAG_ID,parent.getAdapter().getItem(position),null);
            }
        });
    }

    private void findEventsAraoundCurrentUser() {
        if(eventList == null){

            if (mSwipeRefreshLayout != null) {
                mSwipeRefreshLayout.setRefreshing(true);
            }

            PEvent.findEventsAroundCurrentUser(currentUser,
                    Constants.Distance.EVENT_AROUND_PROXIMITY_IN_MILE, new FindCallback<PEvent>() {
                        @Override
                        public void done(List<PEvent> pEvents, ParseException e) {
                            if(e == null){
                                mAdapter.clear();
                                mAdapter.addAll(pEvents);
                                updateView();
                            }
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    });
        }else{
            mAdapter.addAll(eventList);
        }

    }

    private void updateView() {
        if(mAdapter != null)
            mAdapter.notifyDataSetChanged();
    }
}
