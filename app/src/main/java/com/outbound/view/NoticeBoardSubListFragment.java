package com.outbound.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.outbound.R;
import com.outbound.ui.util.SwipeRefreshLayout;

/**
 * Created by zeki on 1/10/2014.
 */
public class NoticeBoardSubListFragment extends ListFragment{

    private String mContentDescription = null;
    private View mRoot = null;
    private Listener mCallBackFragment = null;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    public interface Listener {
        public void onFragmentViewCreated(ListFragment fragment);
        public void onFragmentAttached(ListFragment fragment);
        public void onFragmentDetached(ListFragment fragment);
        public void onFragmentSwipeRefreshed(ListFragment fragment);
    }

    public void setUp(Fragment mCallerFragment){
        if(mCallerFragment instanceof Listener){
            mCallBackFragment = (Listener)mCallerFragment;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRoot = inflater.inflate(R.layout.notice_board_sub_fragment, container, false);
        if (mContentDescription != null) {
            mRoot.setContentDescription(mContentDescription);
        }
        setUpSwipeRefreshLayout(mRoot);

        return mRoot;
    }

    public void setContentDescription(String desc) {
        mContentDescription = desc;
        if (mRoot != null) {
            mRoot.setContentDescription(mContentDescription);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(mCallBackFragment!=null)
            mCallBackFragment.onFragmentViewCreated(this);
    }


    private void setUpSwipeRefreshLayout(View view) {
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        if (mSwipeRefreshLayout != null){
            mSwipeRefreshLayout.setColorScheme(
                    R.color.refresh_progress_1,
                    R.color.refresh_progress_2,
                    R.color.refresh_progress_3,
                    R.color.refresh_progress_4);

            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    //get the latest message for this user.

                    if(mCallBackFragment!=null)
                        mCallBackFragment.onFragmentSwipeRefreshed(NoticeBoardSubListFragment.this);
                }
            });
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        ListAdapter adapter = l.getAdapter();
        Object obj = adapter.getItem(position);

        Intent intent = new Intent(getActivity(), NoticeBoardMessageActivity.class);
        startActivity(intent);
    }
}
