package com.outbound.view;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.outbound.R;
import com.outbound.ui.util.SwipeRefreshLayout;

/**
 * Created by zeki on 27/10/2014.
 */
public class WifiListSubFragment extends ListFragment {

    private String mContentDescription = null;
    private Listener mCallBackFragment = null;
    private View mRoot = null;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    public interface Listener {
        public void onFragmentViewCreated(ListFragment fragment, View view);
        public void onFragmentAttached(WifiListSubFragment fragment);
        public void onFragmentDetached(WifiListSubFragment fragment);
        public void onFragmentSwipeRefreshed(ListFragment fragment,SwipeRefreshLayout swipeRefreshLayout );
        public void onListItemClicked(ListFragment fragment,ListView l, View v, int position, long id );
    }


    public void setUp(Fragment mCallerFragment){
        if(mCallerFragment instanceof Listener){
            mCallBackFragment = (Listener)mCallerFragment;
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRoot = inflater.inflate(R.layout.wifi_sub_fragment, container, false);
        if (mContentDescription != null) {
            mRoot.setContentDescription(mContentDescription);
        }
        setUpSwipeRefreshLayout(mRoot);
        return mRoot;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(mCallBackFragment!=null)
            mCallBackFragment.onFragmentViewCreated(this,view);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        if(mCallBackFragment != null)
            mCallBackFragment.onListItemClicked(this,l,v,position,id);
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

                    if(mCallBackFragment!=null)
                        mCallBackFragment.onFragmentSwipeRefreshed(WifiListSubFragment.this,mSwipeRefreshLayout );
                }
            });
        }
    }
}
