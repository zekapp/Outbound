package com.outbound.view;


import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.app.ActionBar;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

import com.outbound.DispatchActivity;
import com.outbound.R;
import com.outbound.model.PUser;
import com.outbound.ui.util.SoftKeyboardStateHelper;
import com.outbound.ui.util.SwipeRefreshLayout;
import com.outbound.util.location.LocationUtils;
import com.parse.ParseGeoPoint;

import java.util.ArrayList;
import java.util.List;

import static com.outbound.util.Constants.*;
import static com.outbound.util.LogUtils.*;

/**
 * Created by zeki on 3/09/2014.
 */
public class BaseActivity extends FragmentActivity implements
        BaseFragment.BaseFragmentCallbacks,
        LocationListener,
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener {
    private static final String TAG = makeLogTag(BaseActivity.class);

    private FrameLayout mBaseFrameLayout;

    private ViewGroup mTabItemsListContainer;

    private static int mCurrentFragmentItemId = 0;

    private static final String SELECTED_FRAGMENT_ITEM = "selected_fragment_position_in_array";
    private ActionBar actionBar;

    // When set, these components will be shown/hidden in sync with the action bar
    // to implement the "quick recall" effect (the Action Bar and the header views disappear
    // when you scroll down a list, and reappear quickly when you scroll up).
    private ArrayList<View> mHideableHeaderViews = new ArrayList<View>();

    // When new fragment created new fragment added end of array.
    // track for array
    private static ArrayList<FragmentContainer> fragContainer = new ArrayList<FragmentContainer>();

    private boolean mActionBarAutoHideEnabled = false;
    private int mActionBarAutoHideMinY = 0;
    private int mActionBarAutoHideSensivity = 0;
    private int mActionBarAutoHideSignal = 0;
    private boolean mActionBarShown = true;
    private ObjectAnimator mStatusBarColorAnimator;
    private static final TypeEvaluator ARGB_EVALUATOR = new ArgbEvaluator();

    private int mThemedStatusBarColor;

    // Durations for certain animations we use:
    private static final int HEADER_HIDE_ANIM_DURATION = 300;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private int mProgressBarTopWhenActionBarShown;

    private LinearLayout mTabBar;

    public static boolean isLocationConnected = false;

    SharedPreferences mPrefs;
    SharedPreferences.Editor mEditor;
    boolean mUpdatesRequested = true;
    private LocationRequest mLocationRequest;
    private LocationClient mLocationClient;

    private ParseGeoPoint userCurrentGeoPoint;

    private static class FragmentContainer{
        public BaseFragment mFragment;
        private String mTag;
        private int mFragId;
        private Object mParam1;
        private Object mParam2;

        FragmentContainer(BaseFragment fragment, Object param1, Object param2, String tag, int fragId){
            mTag = tag;
            mFragment = fragment;
            mFragment.setUpBaseLayout(R.id.base_layout);
            mParam1 = param1;
            mParam2 = param2;
            mFragId = fragId;
            mFragment.setUp(param1, param2);
        }

        public Object getmParam1() {
            return mParam1;
        }

        public void setmParam1(Object mParam1) {
            this.mParam1 = mParam1;
        }

        public Object getmParam2() {
            return mParam2;
        }

        public void setmParam2(Object mParam2) {
            this.mParam2 = mParam2;
        }
    }

    /*increment one this array for each fragment*/
    private final static FragmentContainer[] FRAGMENTS = new FragmentContainer[24];

    /*
    *       Indices must correspond to array {@link #Constants} items.
    * */
    private final static BaseFragment[] BASE_FRAGMENTS = {
            new ProfileFragment(),
            new ExploreTripsFragment(),
            new EventsFragment(),
            new SearchPeopleFragment(),
            new NoticeBoardFragment(),
            new MyFriendsFragment(),
            new MyTripsFragment(),
            new TravelHistoryFragment(),
            new MyEventsFragment(),
            new SettingsFragment(),
            new ExploreTripsSearchFragment(),
            new ExploreTripsAddFragment(),
            new EventDetailsFragment(),
            new SearchPeopleDetailFragment(),
            new PeopleProfileFragment(),
            new PeopleProfileFriendFragment(),
            new NoticeBoardSearchMsgFrag(),
            new NoticeBoardCreateMsgFrag(),
            new EventSearchFragment(),
            new MyTripsDetailFragment(),
            new CreateEventFragment(),
            new TripsResultFragment(),
            new SearchPeopleFragment(),
            new EventsFragment()

    };

//    /*
//    *       Indices must correspond to array {@link #Constants} items.
//    * */
//    private final static FragmentContainer[] FRAGMENTS = {
//            new FragmentContainer(new ProfileFragment(),            null, null, "ProfileFragment"),    //Profile
//            new FragmentContainer(new ExploreTripsFragment() ,      null, null, "ExploreTrips"),    //ExploreTrips
//            new FragmentContainer(new EventsFragment(),             null, null, "Events"),    //Events
//            new FragmentContainer(new SearchPeopleFragment(),       null, null, "SearchPeopleFragment"),    //SearchPeopleFragment
//            new FragmentContainer(new NoticeBoardFragment(),        null, null, "Noticeboard"),    //Noticeboard
//            new FragmentContainer(new MyFriendsFragment(),          null, null, "MyFriendsFragment"),    //MyFriendsFragment
//            new FragmentContainer(new MyTripsFragment(),            null, null, "MyTripsFragment"),    //MyTripsFragment
//            new FragmentContainer(new TravelHistoryFragment(),      null, null, "TravelHistoryFragment"),    //TravelHistoryFragment
//            new FragmentContainer(new MyEventsFragment(),           null, null, "MyEventsFragment"),    //MyEventsFragment
//            new FragmentContainer(new SettingsFragment(),           null, null, "SettingsFragment"),    //SettingsFragment
//            new FragmentContainer(new ExploreTripsSearchFragment(), null, null, "ExploreTripsSearchFragment"),    //ExploreTripsSearchFragment
//            new FragmentContainer(new ExploreTripsAddFragment(),    null, null, "ExploreTripsAddFragment"),    //ExploreTripsAddFragment
//            new FragmentContainer(new EventDetailsFragment(),       null, null, "EventDetailsFragment"),    //EventDetailsFragment
//            new FragmentContainer(new SearchPeopleDetailFragment(), null, null, "SearchPeopleDetailFragment"),    //SearchPeopleDetailFragment
//            new FragmentContainer(new PeopleProfileFragment(),      null, null, "PeopleProfileFragment"),    //PeopleProfileFragment
//            new FragmentContainer(new PeopleProfileFriendFragment(),null, null, "PeopleProfileFriendFragment"),    //PeopleProfileFriendFragment
//            new FragmentContainer(new NoticeBoardSearchMsgFrag(),   null, null, "NoticeBoardSearchMsgFrag"),    //NoticeBoardSearchMsgFrag
//            new FragmentContainer(new NoticeBoardCreateMsgFrag(),   null, null, "NoticeBoardCreateMsgFrag"),    //NoticeBoardCreateMsgFrag
//            new FragmentContainer(new EventSearchFragment(),        null, null, "EventSearchFragment")     //EventSearchFragment
//    };

    // icons for tab bar items (indices must correspond to above array)
    private static final int[] TAB_BAR_ICON_RES_ID = new int[] {
            R.drawable.tab_profile,     // Profile
            R.drawable.tab_explore,     // Explore trips
            R.drawable.tab_events,      // Events
            R.drawable.tab_search,      // Search
            R.drawable.tab_noticeboard, // Noticeboard
    };

    // icons for tab bar items (indices must correspond to above array)
    private static final int[] TAB_BAR_ICON_RES_ID_PRESSED = new int[] {
            R.drawable.tab_profile_pressed,     // Profile
            R.drawable.tab_explore_pressed,     // Explore trips
            R.drawable.tab_events_pressed,      // Events
            R.drawable.tab_search_pressed,      // Search
            R.drawable.tab_noticeboard_pressed, // Noticeboard
    };



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_fragment_activity_layout);
        setUpLocation();
        setUpActionBar();
        fragmentInitialize(savedInstanceState);
//        final FrameLayout rootView = (FrameLayout)findViewById(R.id.base_layout);
        softKeyboardSetUp();

        setUpUserCurrentGeoPoint();

    }

    private void setUpUserCurrentGeoPoint() {
        userCurrentGeoPoint = new ParseGeoPoint();
    }

    @Override
    protected void onStop() {

//        if (mLocationClient.isConnected()) {
//            stopPeriodicUpdates();
//        }
//        mLocationClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onPause() {
        mEditor.putBoolean(LocationUtils.KEY_UPDATES_REQUESTED, mUpdatesRequested);
        mEditor.commit();
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mLocationClient.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // If the app already has a setting for getting location updates, get it
        if (mPrefs.contains(LocationUtils.KEY_UPDATES_REQUESTED)) {
            mUpdatesRequested = mPrefs.getBoolean(LocationUtils.KEY_UPDATES_REQUESTED, false);
            LOGD(TAG,"onResume mUpdatesRequested: " + Boolean.toString(mUpdatesRequested));
            // Otherwise, turn off location updates until requested
        } else {
            LOGD(TAG,"onResume turn off location update");
            mEditor.putBoolean(LocationUtils.KEY_UPDATES_REQUESTED, false);
            mEditor.commit();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Choose what to do based on the request code
        switch (requestCode) {

            // If the request code matches the code sent in onConnectionFailed
            case LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST :

                switch (resultCode) {
                    // If Google Play services resolved the problem
                    case Activity.RESULT_OK:

                        // Log the result
                        LOGD(TAG, "GooglePlay Service: Error resolved.");

                        // Display the result

                        break;

                    // If any other result was returned by Google Play services
                    default:
                        // Log the result
                        Log.d(TAG, "Google Play services: unable to resolve connection error.");

                        // Display the result
//                        mConnectionState.setText(R.string.disconnected);
//                        mConnectionStatus.setText(R.string.no_resolution);

                        break;
                }

                // If any other request code was received
            default:
                // Report that this Activity received an unknown requestCode
                LOGD(TAG, getString(R.string.unknown_activity_request_code, requestCode));

                break;
        }
    }

    private void startPeriodicUpdates() {
        mLocationClient.requestLocationUpdates(mLocationRequest, this);
    }

    private boolean servicesConnected(){
        // Check that Google Play services is available
        int resultCode =
                GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            LOGD(TAG, getString(R.string.play_services_available));

            // Continue
            return true;
            // Google Play services was not available for some reason
        } else {
            // Display an error dialog
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this, 0);
            if (dialog != null) {
                ErrorDialogFragment errorFragment = new ErrorDialogFragment();
                errorFragment.setDialog(dialog);
                errorFragment.show(getSupportFragmentManager(), LocationUtils.APPTAG);
            }
            return false;
        }
    }

    @Override
    public Location getLocation(){
        if (servicesConnected())
            return mLocationClient.getLastLocation(); // todo:  Not connected. Call connect() and wait for onConnected() to be called.
        else
            return null;
    }



    private void stopPeriodicUpdates() {
        mLocationClient.removeLocationUpdates((com.google.android.gms.location.LocationListener) this);
    }

    private void setUpLocation() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(LocationUtils.UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setFastestInterval(LocationUtils.FAST_INTERVAL_CEILING_IN_MILLISECONDS);
        // Note that location updates are off until the user turns them on
        mUpdatesRequested = true;
        mPrefs = getSharedPreferences(LocationUtils.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        mEditor = mPrefs.edit();
        mLocationClient = new LocationClient(this, this, this);
    }

    private void softKeyboardSetUp() {
        final SoftKeyboardStateHelper softKeyboardStateHelper = new SoftKeyboardStateHelper(findViewById(R.id.base_layout));
        softKeyboardStateHelper.addSoftKeyboardStateListener(new SoftKeyboardStateHelper.SoftKeyboardStateListener() {
            @Override
            public void onSoftKeyboardOpened(int keyboardHeightInPx) {
                hideTabbar();
            }

            @Override
            public void onSoftKeyboardClosed() {
                showTabbar();
            }
        });
    }



    private void fragmentInitialize(Bundle savedInstanceState) {
        mCurrentFragmentItemId = getIntent().getExtras().getBoolean(Extra.IS_LAUNCHER,false)?
                AppInitialStates.LAUNCHER_FRAGMENT_ID:savedInstanceState != null?
                savedInstanceState.getInt(SELECTED_FRAGMENT_ITEM):0;

        mThemedStatusBarColor = getResources().getColor(R.color.theme_primary_dark);

        mTabBar = (LinearLayout)findViewById(R.id.tabbar);

        for (int i = 0; i < FRAGMENTS.length; i++) {
            FRAGMENTS[i] = new FragmentContainer(BASE_FRAGMENTS[i], null, null, Integer.toString(i), i );
        }
    }

    private void setUpActionBar() {
        actionBar = getActionBar();

        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(false); // disable the button
            actionBar.setDisplayHomeAsUpEnabled(false); // remove the left caret
            actionBar.setDisplayShowHomeEnabled(false); // remove the icon

        }
    }

        /*
    *  LOCATION RELATED Callbacks
    * */
    //----------------------------------------------------------------------------------------------

    //LocationListener callbacks
    @Override
    public void onLocationChanged(Location location) {
        LOGD(TAG,"onLocationChanged locationchanged");
        updateUserLocationOnDatabase(location);
    }

    private void updateUserLocationOnDatabase(Location location) {
        PUser currentUser = PUser.getCurrentUser();

        if(location != null){
            userCurrentGeoPoint.setLatitude(location.getLatitude());
            userCurrentGeoPoint.setLongitude(location.getLongitude());
            currentUser.setCurrentLocation(userCurrentGeoPoint);
            currentUser.saveInBackground();
        }
    }

    //GooglePlayServicesClient.ConnectionCallbacks
    @Override
    public void onConnected(Bundle bundle) {
        LOGD(TAG, "onConnected...");
        startPeriodicUpdates();
        isLocationConnected = true;
//        if (mUpdatesRequested) {
//            LOGD(TAG, "onConnected... mUpdatesRequested not null");
//            startPeriodicUpdates();
//        }
    }

    @Override
    public void onDisconnected() {
        isLocationConnected = false;
    }
    //GooglePlayServicesClient.OnConnectionFailedListener
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {

                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(
                        this,
                        LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);
                isLocationConnected = false;
                /*
                * Thrown if Google Play services canceled the original
                * PendingIntent
                */

            } catch (IntentSender.SendIntentException e) {

                // Log the error
                LOGD(TAG, "onConnectionFailed: " + e.getMessage());
                e.printStackTrace();
            }
        } else {

            // If no resolution is available, display a dialog to the user with the error.
//            showErrorDialog(connectionResult.getErrorCode());
        }
    }

    //----------------------------------------------------------------------------------------------

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SELECTED_FRAGMENT_ITEM,mCurrentFragmentItemId);
    }

    @Override
    public void logOut() {
        Intent intent = new Intent(this, DispatchActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void deployFragment(final  int itemId, Object param1, Object param2) {
        LOGD(TAG,"deployFragment: " + FRAGMENTS[itemId].mTag );

        addFragmentToOrderArray(itemId, param1, param2 );

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransactiong = fragmentManager.beginTransaction().
                replace(R.id.container, FRAGMENTS[itemId].mFragment);
        fragmentTransactiong.commit();

    }

    @Override
    public void onBackPressed() {
        LOGD(TAG,"fragViewer size: " + fragContainer.size() );
        FragmentContainer displayFrag;
        int fragViewerSize = fragContainer.size();
        if(fragContainer.size() > 2){
            displayFrag = fragContainer.get(fragViewerSize-2);
            fragContainer.remove(fragViewerSize - 1);
            deployFragment(displayFrag.mFragId, displayFrag.getmParam1(), displayFrag.getmParam2());
        }else if (fragContainer.size() == 2)
        {
            fragContainer.remove(1);
            onTabBarItemClicked(0);
            displayFrag = fragContainer.get(0);
            deployFragment(displayFrag.mFragId,displayFrag.getmParam1(),displayFrag.getmParam2());

        }else
        {
            super.onBackPressed();
        }
    }

    private void addFragmentToOrderArray(int itemId, Object param1, Object param2) {

        //if item is one of the main fragment
        int fragmentSize = fragContainer.size();
        if(itemId <= 4 && fragmentSize>0)
        {
            //delete all fragment in the array except profile
            fragContainer.subList(1,fragmentSize).clear();
        }

        FRAGMENTS[itemId].setmParam1(param1);
        FRAGMENTS[itemId].setmParam2(param2);
        FRAGMENTS[itemId].mFragment.setUp(param1,param2);

        if(fragContainer.isEmpty()){
            fragContainer.add(FRAGMENTS[itemId]);
        }else {
            if(fragContainer.get(fragContainer.size() - 1).mFragId != itemId)
                fragContainer.add(FRAGMENTS[itemId]);
        }

    }

    @Override
    public void registerSwipeRefreshProgressBarAsTop( SwipeRefreshLayout swipeRefreshLayout , int progressBarTopWhenActionBarShown) {
        if(swipeRefreshLayout!=null) {
            mSwipeRefreshLayout = swipeRefreshLayout;
            mProgressBarTopWhenActionBarShown = progressBarTopWhenActionBarShown;
        }
        else
            mSwipeRefreshLayout = null;
    }

    @Override
    public void backIconClicked() {
        //back
        onBackPressed();
    }

    @Override
    public void hideTabbar() {
        if(mTabBar != null) {
            mTabBar.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean isLocationServiceConnected() {
        return isLocationConnected;
    }


    private void showTabbar() {
        if(mTabBar != null){
            mTabBar.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void registerHideableHeaderView(View hideableHeaderView) {
        if(!mHideableHeaderViews.contains(hideableHeaderView)){
            mHideableHeaderViews.add(hideableHeaderView);
        }
    }

    @Override
    public void enableActionBarAutoHide(ListView listView) {
        initActionBarAutoHide();
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            final static int ITEMS_THRESHOLD = 3;
            int lastFvi = 0;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                onMainContentScrolled(firstVisibleItem <= ITEMS_THRESHOLD ? 0 : Integer.MAX_VALUE,
                        lastFvi - firstVisibleItem > 0 ? Integer.MIN_VALUE :
                                lastFvi == firstVisibleItem ? 0 : Integer.MAX_VALUE
                );
                lastFvi = firstVisibleItem;
            }
        });
    }

    /**
     * Indicates that the main content has scrolled (for the purposes of showing/hiding
     * the action bar for the "action bar auto hide" effect). currentY and deltaY may be exact
     * (if the underlying view supports it) or may be approximate indications:
     * deltaY may be INT_MAX to mean "scrolled forward indeterminately" and INT_MIN to mean
     * "scrolled backward indeterminately".  currentY may be 0 to mean "somewhere close to the
     * start of the list" and INT_MAX to mean "we don't know, but not at the start of the list"
     */
    private void onMainContentScrolled(int currentY, int deltaY) {
        // currentY: 0, +∞
        // deltaY : -∞, 0, +∞

        if (deltaY > mActionBarAutoHideSensivity) {
            deltaY = mActionBarAutoHideSensivity;
        } else if (deltaY < -mActionBarAutoHideSensivity) {
            deltaY = -mActionBarAutoHideSensivity;
        }

        if (Math.signum(deltaY) * Math.signum(mActionBarAutoHideSignal) < 0) {
            // deltaY is a motion opposite to the accumulated signal, so reset signal
            mActionBarAutoHideSignal = deltaY;
        } else {
            // add to accumulated signal
            mActionBarAutoHideSignal += deltaY;
        }

        boolean shouldShow = currentY < mActionBarAutoHideMinY ||
                (mActionBarAutoHideSignal <= -mActionBarAutoHideSensivity);
        autoShowOrHideActionBar(shouldShow);
    }

    private void autoShowOrHideActionBar(boolean show) {
        if (show == mActionBarShown) {
            return;
        }

        mActionBarShown = show;
        if (show) {
//            getActionBar().show();
        } else {
//            getActionBar().hide();
        }
        onActionBarAutoShowOrHide(show);
    }

    private void onActionBarAutoShowOrHide(boolean shown) {
        if (mStatusBarColorAnimator != null) {
            mStatusBarColorAnimator.cancel();
        }
        mStatusBarColorAnimator = ObjectAnimator.ofInt(this, "statusBarColor",
                shown ? mThemedStatusBarColor : Color.BLACK).setDuration(250);
        mStatusBarColorAnimator.setEvaluator(ARGB_EVALUATOR);
        mStatusBarColorAnimator.start();

        updateSwipeRefreshProgressBarTop();

        for (View view : mHideableHeaderViews) {
            if (shown) {
                view.animate()
                        .translationY(0)
                        .alpha(1)
                        .setDuration(HEADER_HIDE_ANIM_DURATION)
                        .setInterpolator(new DecelerateInterpolator());
                view.setVisibility(View.VISIBLE);
            } else {
                view.animate()
                        .translationY(-view.getBottom())
                        .alpha(0)
                        .setDuration(HEADER_HIDE_ANIM_DURATION)
                        .setInterpolator(new DecelerateInterpolator());
                view.setVisibility(View.GONE);
            }
        }
    }

    private void updateSwipeRefreshProgressBarTop() {
        if (mSwipeRefreshLayout == null) {
            return;
        }

        if (mActionBarShown) {
            mSwipeRefreshLayout.setProgressBarTop(mProgressBarTopWhenActionBarShown);
        } else {
            mSwipeRefreshLayout.setProgressBarTop(0);
        }
    }
    /**
     * Initializes the Action Bar auto-hide (aka Quick Recall) effect.
     */
    private void initActionBarAutoHide() {
        mActionBarAutoHideEnabled = true;
        mActionBarAutoHideMinY = getResources().getDimensionPixelSize(
                R.dimen.action_bar_auto_hide_min_y);
        mActionBarAutoHideSensivity = getResources().getDimensionPixelSize(
                R.dimen.action_bar_auto_hide_sensivity);
    }
    // list of tab bar items that were actually added to the navdrawer, in order
    private ArrayList<Integer> mTabBarItems = new ArrayList<Integer>();

    // views that correspond to each tab bar item, null if not yet created
    private View[] mTabBarItemViews = null;

    /**
     * Sets up the tab bar as appropriate.
     */
    private void setupTabBar(){
        int selfItem = mCurrentFragmentItemId;

        mBaseFrameLayout = (FrameLayout) findViewById(R.id.base_layout);

        if (mBaseFrameLayout == null) {
            return;
        }

        if (selfItem == ITEM_INVALID) {
            // do not show a nav drawer
            View navDrawer = mBaseFrameLayout.findViewById(R.id.tabbar);
            if (navDrawer != null) {
                ((ViewGroup) navDrawer.getParent()).removeView(navDrawer);
            }
            mBaseFrameLayout = null;
            return;
        }

        populateTabBar();
    }

    /** Populates the tab bar with the appropriate items. */
    private void populateTabBar(){

        mTabBarItems.clear();

        mTabBarItems.add(TAB_BAR_ITEM_PROFILE_FRAG_ID);
        mTabBarItems.add(TAB_BAR_ITEM_EXPLORE_TRIPS_FRAG_ID);
        mTabBarItems.add(TAB_BAR_ITEM_EVENTS_FRAG_ID);
        mTabBarItems.add(TAB_BAR_ITEM_SEARCH_FRAG_ID);
        mTabBarItems.add(TAB_BAR_ITEM_NOTICEBOARD_FRAG_ID);

        createTabBarItems();

        onTabBarItemClicked(mCurrentFragmentItemId);
    }

    private void createTabBarItems(){

        mTabItemsListContainer = (ViewGroup) findViewById(R.id.tab_bar_items_list);

        if (mTabItemsListContainer == null) {
            return;
        }

        mTabBarItemViews = new View[mTabBarItems.size()];
        mTabItemsListContainer.removeAllViews();

        int i = 0;
        for (int itemId : mTabBarItems) {
            mTabBarItemViews[i] = makeTabBarItem(itemId,mTabItemsListContainer);
            mTabItemsListContainer.addView(mTabBarItemViews[i]);
            ++i;
        }
    }

    private View makeTabBarItem(final int itemId, ViewGroup container) {
        boolean selected = mCurrentFragmentItemId == itemId;

        View view = getLayoutInflater().inflate(R.layout.tab_bar_item, container, false);

        ImageView iconView = (ImageView) view.findViewById(R.id.icon);

        int iconId = itemId >= 0 && itemId < TAB_BAR_ICON_RES_ID.length ?
                selected?
                        TAB_BAR_ICON_RES_ID_PRESSED[itemId]:TAB_BAR_ICON_RES_ID[itemId] : 0;

        if (iconId > 0) {
            iconView.setImageResource(iconId);
        }

//        formatTabBarItem(view, itemId, selected);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTabBarItemClicked(itemId);
            }
        });

        return view;
    }

    private void onTabBarItemClicked(final int itemId){

        mCurrentFragmentItemId = itemId;

        setItemAsClicked(itemId);

        deployFragment(itemId,null,null);

    }

    private void setItemAsClicked(final int itemId){
        int i = 0;
        for (int item : mTabBarItems) {
            View tabBarItemlayout = mTabBarItemViews[item];
            ImageView iconView = (ImageView)tabBarItemlayout.findViewById(R.id.icon);
            int iconId = itemId >= 0 && itemId < TAB_BAR_ICON_RES_ID.length ?
                    itemId == item?
                            TAB_BAR_ICON_RES_ID_PRESSED[item]:TAB_BAR_ICON_RES_ID[item] : 0;

            if (iconId > 0) {
                iconView.setImageResource(iconId);
            }

        }
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setupTabBar();
    }

    /**
     * Define a DialogFragment to display the error dialog generated in
     * showErrorDialog.
     */
    public static class ErrorDialogFragment extends DialogFragment {

        // Global field to contain the error dialog
        private Dialog mDialog;

        /**
         * Default constructor. Sets the dialog field to null
         */
        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }

        /**
         * Set the dialog to display
         *
         * @param dialog An error dialog
         */
        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }

        /*
         * This method must return a Dialog to the DialogFragment.
         */
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }
}
