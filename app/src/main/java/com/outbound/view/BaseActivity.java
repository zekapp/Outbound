package com.outbound.view;


import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.app.ActionBar;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.outbound.R;
import com.outbound.ui.util.SwipeRefreshLayout;

import java.util.ArrayList;

import static com.outbound.util.Constants.*;
import static com.outbound.util.LogUtils.*;

/**
 * Created by zeki on 3/09/2014.
 */
public class BaseActivity extends FragmentActivity implements BaseFragment.BaseFragmentCallbacks{
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
    private static ArrayList<FragmentContainer> fragViewer = new ArrayList<FragmentContainer>();

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
    }

    /*increment one this array for each fragment*/
    private final static FragmentContainer[] FRAGMENTS = new FragmentContainer[20];

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
            new MyTripsDetailFragment()
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

        actionBar = getActionBar();

        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(false); // disable the button
            actionBar.setDisplayHomeAsUpEnabled(false); // remove the left caret
            actionBar.setDisplayShowHomeEnabled(false); // remove the icon

        }

        mCurrentFragmentItemId = getIntent().getExtras().getBoolean(Extra.IS_LAUNCHER,false)?
                AppInitialStates.LAUNCHER_FRAGMENT_ID:savedInstanceState != null?
                savedInstanceState.getInt(SELECTED_FRAGMENT_ITEM):0;

        setContentView(R.layout.base_fragment_activity_layout);

        mThemedStatusBarColor = getResources().getColor(R.color.theme_primary_dark);

        mTabBar = (LinearLayout)findViewById(R.id.tabbar);

        for (int i = 0; i < FRAGMENTS.length; i++) {
            FRAGMENTS[i] = new FragmentContainer(BASE_FRAGMENTS[i], null, null, Integer.toString(i), i );
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SELECTED_FRAGMENT_ITEM,mCurrentFragmentItemId);
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

    private void addFragmentToOrderArray(int itemId, Object param1, Object param2) {

        //if item is one of the main fragment
        int fragmentSize = fragViewer.size();
        if(itemId <= 4 && fragmentSize>0)
        {
            //delete all fragment in the array except profile
            fragViewer.subList(1,fragmentSize).clear();


        }

        FRAGMENTS[itemId].mFragment.setUp(param1,param2);

        if(fragViewer.isEmpty()){
            fragViewer.add(FRAGMENTS[itemId]);
        }else {
            if(fragViewer.get(fragViewer.size() - 1).mFragId != itemId)
                fragViewer.add(FRAGMENTS[itemId]);
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
    public void onBackPressed() {
        LOGD(TAG,"fragViewer size: " + fragViewer.size() );
        FragmentContainer displayFrag;
        int fragViewerSize = fragViewer.size();
        if(fragViewer.size() > 2){
            fragViewer.remove(fragViewerSize-1);
            displayFrag = fragViewer.get(fragViewerSize-2);
            deployFragment(displayFrag.mFragId,displayFrag.mParam1,displayFrag.mParam2);
        }else if (fragViewer.size() == 2)
        {
            fragViewer.remove(1);
            onTabBarItemClicked(0);
            displayFrag = fragViewer.get(0);
            deployFragment(displayFrag.mFragId,displayFrag.mParam1,displayFrag.mParam2);

        }else
        {
            super.onBackPressed();
        }
    }


    @Override
    public void backIconClicked() {
        //back
        onBackPressed();
//        FragmentContainer lastAddedFrag = fragViewer.get(fragViewer.size() - 2);

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
}
