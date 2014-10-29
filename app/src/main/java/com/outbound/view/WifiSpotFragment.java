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
import com.outbound.model.PUser;
import com.outbound.model.PWifiSpot;
import com.outbound.model.WifiSpot;
import com.outbound.ui.util.GeoLocationFromPlaceDialog;
import com.outbound.ui.util.SlidingTabLayout;
import com.outbound.ui.util.SwipeRefreshLayout;
import com.outbound.ui.util.adapters.BaseFragmentStatePagerAdapter;
import com.outbound.ui.util.adapters.NoticeBoardMessageAdapter;
import com.outbound.ui.util.adapters.WifiSpotAdapter;
import com.outbound.util.Constants;
import com.outbound.util.Constants.WifiStatusTypes;
import com.outbound.util.DBManager;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;

import java.util.ArrayList;
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
    private static int currentPage = 0;

    private WifiSpotAdapter[] adapter = new WifiSpotAdapter[4];

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

        iconSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSearchPlaceDialog();
            }
        });

        iconAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open foursquare result list
                if(mCallbacks != null)
                    mCallbacks.deployFragment(Constants.WIFI_SPOTS_FOURSQUARE_FRAG_ID,null,null);
            }
        });
    }

    private void openSearchPlaceDialog() {
        GeoLocationFromPlaceDialog gfp= new GeoLocationFromPlaceDialog(getActivity());
        gfp.addGeoLocationDialogListener(new GeoLocationFromPlaceDialog.GeolocationDialogListener() {
            @Override
            public void onGeolocationOfSelectedItem(ParseGeoPoint location) {
                if(location != null){
                    for(int i=0; i<4; i++){
                        adapter[i].setReferenceGeoPoint(location);
                        adapter[i].clear();
                    }
                    updateWifiAdapterTypeAll(location, null);
                    updateWifiAdapterTypeFree(location, null);
                    updateWifiAdapterTypePaid(location,null);
                    updateWifiAdapterTypePurchase(location,null);

                    mViewPager.setCurrentItem(0);
                }else
                {
                    showToastMessage("No wifi spot found.");
                }
            }
        });
        gfp.show();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int i;
        for (i = 0; i < 4; i++) {
            if(adapter[i] == null){
                adapter[i] = new WifiSpotAdapter(getActivity(),true);
                adapter[i].setReferenceGeoPoint(PUser.getCurrentUser().getCurrentLocation());
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

    private ImageView mSaveWifiSpots;
    private ImageView mViewWifiSpots;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.wifi_activity_layout, container, false);
        setUpActionBar(getActivity());

        mViewPager = (ViewPager) view.findViewById(R.id.view_pager);
        mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        mSaveWifiSpots = (ImageView)view.findViewById(R.id.save_wifi_spots);
        mViewWifiSpots = (ImageView)view.findViewById(R.id.view_wifi_spots);

        setUpWifiSpotAdapter();
        setUpViewPager();
        setUpSlidingTabLayout();
        setUpSaveWifiSpots();
        setUpViewWifiSpots();

        return view;
    }

    private void setUpViewWifiSpots() {
        mViewWifiSpots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getWifiSpots();
            }
        });
    }

    private void setUpSaveWifiSpots() {
        mSaveWifiSpots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveWifiSpots();
            }
        });
    }
    private void getWifiSpots() {
        DBManager db =new DBManager(getActivity());
        boolean wifiSpotFound = false;
        for(int i =0 ; i<4; i++){
            List<WifiSpot> pWifiSpots = db.getWifiSpots(i);
            adapter[i].clear();
            if(pWifiSpots != null){
                wifiSpotFound = true;
                adapter[i].addAll(pWifiSpots);
            }
            adapter[i].setReferenceGeoPoint(PUser.getCurrentUser().getCurrentLocation());
            updateView(i);
        }
        if(wifiSpotFound)
            showAlertDialog("Wifi Spots", "Saved wifi spots loaded",true);
        else
            showAlertDialog("Wifi Spots", "No recorded Wifi spots found",true);

    }
    private void saveWifiSpots() {
        DBManager db = new DBManager(getActivity());
        for(int i =0 ; i < 4 ; i++){
            WifiSpotAdapter tmp = adapter[i];
            List<WifiSpot> list = new ArrayList<WifiSpot>();
            for(int j =0 ; j<tmp.getCount();j++){
                list.add(tmp.getItem(j));
            }
            db.saveWifiSpots(list, i);
        }
        showAlertDialog("Wifi Spots", "Wifi spots saved.",true);
    }

    private void setUpWifiSpotAdapter() {


        if(adapter[0].isEmpty())
            updateWifiAdapterTypeAll(null,null);
        if(adapter[1].isEmpty())
            updateWifiAdapterTypeFree(null,null);
        if(adapter[2].isEmpty())
            updateWifiAdapterTypePaid(null,null);
        if(adapter[3].isEmpty())
            updateWifiAdapterTypePurchase(null,null);
    }

    private void updateWifiAdapterTypePurchase(ParseGeoPoint location, final SwipeRefreshLayout swipeRefreshLayout) {
        PWifiSpot.findPurchaseTypeWifi(location, new FindCallback<PWifiSpot>() {
            @Override
            public void done(List<PWifiSpot> pWifiSpots, ParseException e) {
                if (swipeRefreshLayout != null)
                    swipeRefreshLayout.setRefreshing(false);

                if (e == null) {
                    List<WifiSpot> list = initWifiSpotClass(pWifiSpots);
                    adapter[3].clear();
                    adapter[3].addAll(list);
                    updateView(3);
                } else {
                    LOGD(TAG, "updateWifiAdapterTypePurchase e: " + e.getMessage());
                    showToastMessage("Network Error. Check connection");
                }
            }
        });
    }

    private void updateWifiAdapterTypePaid(ParseGeoPoint location,final SwipeRefreshLayout swipeRefreshLayout) {
        PWifiSpot.findPaidTypeWifi(location, new FindCallback<PWifiSpot>() {
            @Override
            public void done(List<PWifiSpot> pWifiSpots, ParseException e) {
                if(swipeRefreshLayout != null)
                    swipeRefreshLayout.setRefreshing(false);

                if(e == null){
                    List<WifiSpot> list = initWifiSpotClass(pWifiSpots);
                    adapter[2].clear();
                    adapter[2].addAll(list);
                    updateView(2);
                }else{
                    LOGD(TAG, "updateWifiAdapterTypePaid e: " + e.getMessage());
                    showToastMessage("Network Error. Check connection");
                }
            }
        });
    }

    private void updateWifiAdapterTypeFree(ParseGeoPoint location,final SwipeRefreshLayout swipeRefreshLayout) {
        PWifiSpot.findFreeTypeWifi(location, new FindCallback<PWifiSpot>() {
            @Override
            public void done(List<PWifiSpot> pWifiSpots, ParseException e) {
                if (swipeRefreshLayout != null)
                    swipeRefreshLayout.setRefreshing(false);



                if (e == null) {
                    List<WifiSpot> list = initWifiSpotClass(pWifiSpots);
                    adapter[1].clear();
                    adapter[1].addAll(list);
                    updateView(1);
                } else {
                    LOGD(TAG, "updateWifiAdapterTypeFree e: " + e.getMessage());
                    showToastMessage("Network Error. Check connection");
                }
            }
        });
    }

    private void updateWifiAdapterTypeAll(ParseGeoPoint location, final SwipeRefreshLayout swipeRefreshLayout) {
        PWifiSpot.findAllTypeWifi(location, new FindCallback<PWifiSpot>() {
            @Override
            public void done(List<PWifiSpot> pWifiSpots, ParseException e) {
                if (swipeRefreshLayout != null)
                    swipeRefreshLayout.setRefreshing(false);

                if (e == null) {
                    List<WifiSpot> list = initWifiSpotClass(pWifiSpots);
                    adapter[0].clear();
                    adapter[0].addAll(list);
                    updateView(0);
                } else {
                    LOGD(TAG, "updateWifiAdapterTypeAll e: " + e.getMessage());
                    showToastMessage("Network Error. Check connection");
                }
            }
        });
    }

    private List<WifiSpot> initWifiSpotClass(List<PWifiSpot> pWifiSpots) {
        List<WifiSpot> wifiSpotList = new ArrayList<WifiSpot>();
        for(PWifiSpot pWifiSpot : pWifiSpots){
            WifiSpot wifiSpot = new WifiSpot();

            wifiSpot.setWifiAddress(pWifiSpot.getWifiAddress());
            wifiSpot.setWifiLocation(pWifiSpot.getWifiLocation());
            wifiSpot.setWifiName(pWifiSpot.getWifiName());
            wifiSpot.setWifiType(pWifiSpot.getWifiType());

            wifiSpotList.add(wifiSpot);
        }
        return wifiSpotList;
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

        mSlidingTabLayout.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                LOGD(TAG, "onPageSelected: " + Integer.toString(position));
                currentPage = position;
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
        mSlidingTabLayout.setContentDescription(3, "wifi_purchase");
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

        for (int i = 0; i<4 ; i++)
            adapter[i].setReferenceGeoPoint(PUser.getCurrentUser().getCurrentLocation());

        switch (fragIndex){
            case 0:
                updateWifiAdapterTypeAll(null,swipeRefreshLayout);
                break;
            case 1:
                updateWifiAdapterTypeFree(null,swipeRefreshLayout);
                break;
            case 2:
                updateWifiAdapterTypePaid(null,swipeRefreshLayout);
                break;
            case 3:
                updateWifiAdapterTypePurchase(null,swipeRefreshLayout);
                break;
        }
    }

    @Override
    public void onListItemClicked(ListFragment fragment, ListView l, View v, int position, long id) {
        int fragIndex = fragment.getArguments().getInt(ARG_WIFI_STATUS_INDEX, 0);
        WifiSpotAdapter adpt = adapter[fragIndex];
        openAlertDialog(adpt.getItem(position));
    }

    private void openAlertDialog(WifiSpot item) {
        showAlertDialog(item.getWifiName(), item.getWifiAddress(), true);
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
