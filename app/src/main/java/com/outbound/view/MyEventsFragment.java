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
import com.outbound.ui.util.adapters.MyEventsAdapter;

import java.util.ArrayList;

/**
 * Created by zeki on 24/09/2014.
 */
public class MyEventsFragment extends BaseFragment {
    private MyEventsAdapter mAdapter;
    private ListView mListView;
    @Override
    protected void setUp(int baseActivityFrameLayoutId) {
        super.setUp(baseActivityFrameLayoutId);
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        setUpActionBar(activity);
    }
    private void setUpActionBar(Activity activity) {
        View viewActionBar = activity.getLayoutInflater().inflate(R.layout.custom_ab_back_button, null);
        TextView title = (TextView)viewActionBar.findViewById(R.id.action_bar_title);
        title.setText(getResources().getString(R.string.my_events_fragment_title));
        ImageView icon = (ImageView)viewActionBar.findViewById(R.id.ab_icon_1);
        icon.setImageResource(R.drawable.action_add);
        ImageView icon2 = (ImageView)viewActionBar.findViewById(R.id.ab_icon_2);
        icon2.setImageResource(R.drawable.action_search);
        icon2.setVisibility(View.VISIBLE);
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
        setUpMyTravelHistoryListView(view);
        return view;
    }

    private void setUpMyTravelHistoryListView(View v) {
        ArrayList<Object> test = new ArrayList<Object>();
        for (int i = 0; i < 50; i++) {
            test.add(new Object());
        }
        mAdapter = new MyEventsAdapter(getActivity(), test);

        mListView = (ListView) v.findViewById(R.id.list_view);
        mListView.setAdapter(mAdapter);
    }
}
