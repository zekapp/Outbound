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

    private static class FragmentContainer{
        public BaseFragment mFragment;
        public String mTag;

        FragmentContainer(BaseFragment fragment, String fragmentTag){
            mFragment = fragment;
            mTag = fragmentTag;

            mFragment.setUp(R.id.base_layout);

        }
    }

//    Indices must correspond to array {@link #Constants} items.
    private final static FragmentContainer[] FRAGMENT = {
            new FragmentContainer(new ProfileFragment(),        "Profile"),             //Profile
            new FragmentContainer(new ExploreTripsFragment() ,  "ExploreTrips"),        //ExploreTrips
            new FragmentContainer(new EventsFragment(),         "Events"),              //Events
            new FragmentContainer(new SearchFragment(),         "Search"),              //Search
            new FragmentContainer(new NoticeBoardFragment(),    "Noticeboard"),         //Noticeboard
            new FragmentContainer(new MyFriendsFragment(),      "MyFriendsFragment"),   //MyFriendsFragment
            new FragmentContainer(new MyFriendsFragment(),        "MyTrips"),             //MyTrips
            new FragmentContainer(new MyFriendsFragment(),"MyTravelHistory"),     //MyTravelHistory
            new FragmentContainer(new MyFriendsFragment(),       "MyEvents")             //MyEvents
    };

    // icons for tab bar items (indices must correspond to above array)
    private static final int[] TAB_BAR_ICON_RES_ID = new int[] {
            R.drawable.tab_profile,  // Profile
            R.drawable.tab_explore,  // Explore trips
            R.drawable.tab_events,  // Events
            R.drawable.tab_search,  // Search
            R.drawable.tab_noticeboard,  // Noticeboard
    };

    // icons for tab bar items (indices must correspond to above array)
    private static final int[] TAB_BAR_ICON_RES_ID_PRESSED = new int[] {
            R.drawable.tab_profile_pressed,  // Profile
            R.drawable.tab_explore_pressed,  // Explore trips
            R.drawable.tab_events_pressed,  // Events
            R.drawable.tab_search_pressed,  // Search
            R.drawable.tab_noticeboard_pressed,  // Noticeboard
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
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SELECTED_FRAGMENT_ITEM,mCurrentFragmentItemId);
    }

    @Override
    public void deployFragment(final  int itemId) {
        LOGD(TAG,"deployFragment " + FRAGMENT[itemId].mTag );


        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransactiong = fragmentManager.beginTransaction().
                replace(R.id.container, FRAGMENT[itemId].mFragment);
        fragmentTransactiong.commit();
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

        mTabBarItems.add(TAB_BAR_ITEM_PROFILE);
        mTabBarItems.add(TAB_BAR_ITEM_EXPLORE_TRIPS);
        mTabBarItems.add(TAB_BAR_ITEM_EVENTS);
        mTabBarItems.add(TAB_BAR_ITEM_SEARCH);
        mTabBarItems.add(TAB_BAR_ITEM_NOTICEBOARD);

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

        deployFragment(itemId);

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
