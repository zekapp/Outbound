package com.outbound.view;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.outbound.R;
import com.outbound.model.PChatActivity;
import com.outbound.model.PWifiSpot;
import com.outbound.ui.util.SlidingTabLayout;
import com.outbound.ui.util.SwipeRefreshLayout;
import com.outbound.ui.util.adapters.BaseFragmentStatePagerAdapter;
import com.outbound.ui.util.adapters.NoticeBoardMessageAdapter;
import com.outbound.ui.util.adapters.WifiSpotAdapter;
import com.outbound.util.Constants;
import com.outbound.util.Constants.WifiStatusTypes;
import com.parse.FindCallback;
import com.parse.ParseException;

import java.util.List;

import static com.outbound.util.LogUtils.LOGD;
import static com.outbound.util.LogUtils.makeLogTag;

/**
 * Created by zeki on 27/10/2014.
 */
public class WifiSpotFragment extends BaseFragment implements WifiListSubFragment.Listener {
    private static final String TAG = makeLogTag(WifiSpotFragment.class);

    private static final String ARG_WIFI_STATUS_INDEX = "com.outbound.ARG_WIFI_STATUS_INDEX";

    private  View view;
    private ViewPager mViewPager = null;
    private OurViewPagerAdapter mViewPagerAdapter = null;
    private SlidingTabLayout mSlidingTabLayout = null;

    private WifiSpotAdapter[] adapter = new WifiSpotAdapter[4];
    private WifiSpotAdapter wifiAdapterTypeAll_;
    private WifiSpotAdapter wifiAdapterTypeFree_;
    private WifiSpotAdapter wifiAdapterTypePaid_;
    private WifiSpotAdapter wifiAdapterTypePurchase_;

    private void setUpActionBar(Activity activity) {
        View viewActionBar = activity.getLayoutInflater().inflate(R.layout.custom_ab_back_button, null);
        TextView title = (TextView)viewActionBar.findViewById(R.id.action_bar_title);
        title.setText(getResources().getString(R.string.wifi_activity_title));
        ImageView iconAdd = (ImageView)viewActionBar.findViewById(R.id.ab_icon_1);
        iconAdd.setImageResource(R.drawable.action_add);

        ImageView iconSearch = (ImageView)viewActionBar.findViewById(R.id.ab_icon_2);
        iconSearch.setImageResource(R.drawable.action_search);
        iconSearch.setVisibility(View.VISIBLE);

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int i;
        for (i = 0; i < 4; i++) {
            if(adapter[i] == null){
                adapter[i] = new WifiSpotAdapter(getActivity());
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if(mCallbacks != null){
            mCallbacks.hideTabbar();
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
        if(mCallbacks != null){
            mCallbacks.showTabbar();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.wifi_activity_layout, container, false);
        setUpActionBar(getActivity());

        mViewPager = (ViewPager) view.findViewById(R.id.view_pager);
        mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);

        setUpWifiSpotAdapter();
        setUpViewPager();
        setUpSlidingTabLayout();

        return view;
    }

    private void setUpWifiSpotAdapter() {

        if(adapter[0].isEmpty())
            updateWifiAdapterTypeAll(null);
        if(adapter[1].isEmpty())
            updateWifiAdapterTypeFree(null);
        if(adapter[2].isEmpty())
            updateWifiAdapterTypePaid(null);
        if(adapter[3].isEmpty())
            updateWifiAdapterTypePurchase(null);


//        if(wifiAdapterTypeAll == null)
//            wifiAdapterTypeAll = new WifiSpotAdapter(getActivity());
//        if(wifiAdapterTypeFree == null)
//            wifiAdapterTypeFree = new WifiSpotAdapter(getActivity());
//        if(wifiAdapterTypePaid == null)
//            wifiAdapterTypePaid = new WifiSpotAdapter(getActivity());
//        if(wifiAdapterTypePurchase == null)
//            wifiAdapterTypePurchase = new WifiSpotAdapter(getActivity());
//
//        updateWifiAdapterTypeAll();
//        updateWifiAdapterTypeFree();
//        updateWifiAdapterTypePaid();
//        updateWifiAdapterTypePurchase();
    }

    private void updateWifiAdapterTypePurchase(final SwipeRefreshLayout swipeRefreshLayout) {
        PWifiSpot.findPurchaseTypeWifi(new FindCallback<PWifiSpot>() {
            @Override
            public void done(List<PWifiSpot> pWifiSpots, ParseException e) {
                if(swipeRefreshLayout != null)
                    swipeRefreshLayout.setRefreshing(false);

                if(e == null){
                    adapter[3].clear();
                    adapter[3].addAll(pWifiSpots);
                    updateView(3);
                }else{
                    LOGD(TAG, "updateWifiAdapterTypePurchase e: " + e.getMessage());
                    showToastMessage("Network Error. Check connection");
                }
            }
        });
    }

    private void updateWifiAdapterTypePaid(final SwipeRefreshLayout swipeRefreshLayout) {
        PWifiSpot.findPaidTypeWifi(new FindCallback<PWifiSpot>() {
            @Override
            public void done(List<PWifiSpot> pWifiSpots, ParseException e) {
                if(swipeRefreshLayout != null)
                    swipeRefreshLayout.setRefreshing(false);

                if(e == null){
                    adapter[2].clear();
                    adapter[2].addAll(pWifiSpots);
                    updateView(2);
                }else{
                    LOGD(TAG, "updateWifiAdapterTypePaid e: " + e.getMessage());
                    showToastMessage("Network Error. Check connection");
                }
            }
        });
    }

    private void updateWifiAdapterTypeFree(final SwipeRefreshLayout swipeRefreshLayout) {
        PWifiSpot.findFreeTypeWifi(new FindCallback<PWifiSpot>() {
            @Override
            public void done(List<PWifiSpot> pWifiSpots, ParseException e) {
                if(swipeRefreshLayout != null)
                    swipeRefreshLayout.setRefreshing(false);

                if(e == null){
                    adapter[1].clear();
                    adapter[1].addAll(pWifiSpots);
                    updateView(1);
                }else{
                    LOGD(TAG, "updateWifiAdapterTypeFree e: " + e.getMessage());
                    showToastMessage("Network Error. Check connection");
                }
            }
        });
    }

    private void updateWifiAdapterTypeAll(final SwipeRefreshLayout swipeRefreshLayout) {
        PWifiSpot.findAllTypeWifi(new FindCallback<PWifiSpot>() {
            @Override
            public void done(List<PWifiSpot> pWifiSpots, ParseException e) {
                if(swipeRefreshLayout != null)
                    swipeRefreshLayout.setRefreshing(false);

                if(e == null){
                    adapter[0].clear();
                    adapter[0].addAll(pWifiSpots);
                    updateView(0);
                }else{
                    LOGD(TAG, "updateWifiAdapterTypeAll e: " + e.getMessage());
                    showToastMessage("Network Error. Check connection");
                }
            }
        });
    }

    private void updateView(int fragId) {
        adapter[fragId].notifyDataSetChanged();
    }

    private void setUpSlidingTabLayout() {
        mSlidingTabLayout.setCustomTabView(R.layout.tab_indicator, android.R.id.text1);
        setSlidingTabLayoutContentDescriptions();

        Resources res = getResources();
        mSlidingTabLayout.setSelectedIndicatorColors(res.getColor(R.color.tab_selected_strip));
        mSlidingTabLayout.setDistributeEvenly(true);
        mSlidingTabLayout.setViewPager(mViewPager);

        mSlidingTabLayout.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageSelected(int position) {
                LOGD(TAG, "onPageSelected: " + Integer.toString(position));
//                wifiSpotAdapter.filterAccordingToTheSpotType();
//                if(position == 0)
//                    updateFriendListAdapter();
//                else if(position == 1)
//                    updatePendingListAdapter();
            }
        });
    }

    private void setSlidingTabLayoutContentDescriptions() {

        mSlidingTabLayout.setContentDescription(0,"wifi_all");
        mSlidingTabLayout.setContentDescription(1,"wifi_free");
        mSlidingTabLayout.setContentDescription(2,"wifi_paid");
        mSlidingTabLayout.setContentDescription(3,"wifi_purchase");
    }
    private void setUpViewPager() {
        mViewPagerAdapter = new OurViewPagerAdapter(getChildFragmentManager());
        mViewPager.setAdapter(mViewPagerAdapter);
    }

    // WifiListSubFragment listener
    @Override
    public void onFragmentViewCreated(ListFragment fragment, View view) {
        int fragIndex = fragment.getArguments().getInt(ARG_WIFI_STATUS_INDEX, 0);
        fragment.setListAdapter(adapter[fragIndex]);
    }

    @Override
    public void onFragmentAttached(WifiListSubFragment fragment) {

    }

    @Override
    public void onFragmentDetached(WifiListSubFragment fragment) {

    }

    @Override
    public void onFragmentSwipeRefreshed(ListFragment fragment, SwipeRefreshLayout swipeRefreshLayout) {
        int fragIndex = fragment.getArguments().getInt(ARG_WIFI_STATUS_INDEX, 0);
        switch (fragIndex){
            case 0:
                updateWifiAdapterTypeAll(swipeRefreshLayout);
                break;
            case 1:
                updateWifiAdapterTypeFree(swipeRefreshLayout);
                break;
            case 2:
                updateWifiAdapterTypePaid(swipeRefreshLayout);
                break;
            case 3:
                updateWifiAdapterTypePurchase(swipeRefreshLayout);
                break;
        }
    }

    @Override
    public void onListItemClicked(ListFragment fragment, ListView l, View v, int position, long id) {
        int fragIndex = fragment.getArguments().getInt(ARG_WIFI_STATUS_INDEX, 0);
        WifiSpotAdapter adpt = adapter[fragIndex];
        openAlertDialog(adpt.getItem(position));
    }

    private void openAlertDialog(PWifiSpot item) {
        showAlertDialog(item.getWifiName(),item.getWifiAddress(),true);
    }

    //--------------------------------------------------------------------

    private class OurViewPagerAdapter extends BaseFragmentStatePagerAdapter {

        CharSequence[] title = {"All" , "Free", "Paid" , "Purchase"};
        public OurViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            WifiListSubFragment frag = new WifiListSubFragment();
            frag.setUp(WifiSpotFragment.this);
            Bundle args = new Bundle();
            args.putInt(ARG_WIFI_STATUS_INDEX, position);
            frag.setArguments(args);
            return frag;
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return title[position];
        }
    }

}
