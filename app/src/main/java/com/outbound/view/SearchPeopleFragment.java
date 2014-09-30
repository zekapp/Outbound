package com.outbound.view;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.outbound.R;
import com.outbound.ui.util.adapters.SearchPeopleAdapter;
import com.outbound.ui.util.HeaderGridView;
import com.outbound.ui.util.SwipeRefreshLayout;
import com.outbound.util.Constants;

/**
 * Created by zeki on 15/09/2014.
 */
public class SearchPeopleFragment extends BaseFragment {

    private SearchPeopleAdapter adapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        setUpActionBar(activity);
    }
    private void setUpActionBar(Activity activity) {
        View viewActionBar = activity.getLayoutInflater().inflate(R.layout.custom_ab_search, null);
        TextView title = (TextView)viewActionBar.findViewById(R.id.action_bar_title);
        title.setText(getResources().getString(R.string.search_fragment_title));
        ImageView iconSearch = (ImageView)viewActionBar.findViewById(R.id.ab_searchEvent_search_icon);
        iconSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallbacks.deployFragment(Constants.SEARCH_PEOPLE_DETAIL_FRAGMENT_ID, null, null);
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
        final View view = inflater.inflate(R.layout.search_people_fragment_layout, container, false);
        final View header = inflater.inflate(R.layout.search_people_grid_header,container,false);

        setUpGridView(view,header);
        setUpSwipeRefreshLayout(view);
        return view;
    }

    private void setUpGridView(View view, View header) {
        adapter = new SearchPeopleAdapter(getActivity());

        for (int i = 0; i < 50; i++) {
            adapter.add(new Object());
        }

        HeaderGridView userGridView = (HeaderGridView)view.findViewById(R.id.sp_grid_view);
        userGridView.addHeaderView(header, null, false);
        userGridView.setAdapter(adapter);
        userGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(mCallbacks != null)
                {
                    mCallbacks.deployFragment(Constants.PEOPLE_PROFILE_FRAGMENT_ID,null,null);
                }
            }
        });
    }
    private void setUpSwipeRefreshLayout(View view) {
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.sp_swipe_refresh_layout);
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
