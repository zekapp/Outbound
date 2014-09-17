package com.outbound.view;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;


import com.outbound.R;

import java.util.ArrayList;

import static com.outbound.util.Constants.*;

/**
 * Created by zeki on 3/09/2014.
 */
public class BaseActivity extends Activity implements BaseFragment.BaseFragmentCallbacks{

    private FrameLayout mBaseFrameLayout;

    private ViewGroup mTabItemsListContainer;

    private static int mCurrentFragmentItemId = 0;

    private static final String FRAGMENT_SELECTED_

    private static class FragmentContainer{
        public BaseFragment mFragment;

        FragmentContainer(BaseFragment fragment){
            mFragment = fragment;

            mFragment.setUp(R.id.base_layout);

        }
    }

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
    public void deployFragment(final  int itemId) {
        FragmentManager fragmentManager = getFragmentManager();

        fragmentManager.beginTransaction().replace(R.id.container, FRAGMENT[itemId].mFragment).commit();
    }

    //indices must correspond to array constant Fragment items.
    private final static FragmentContainer[] FRAGMENT = {
            new FragmentContainer(new ProfileFragment()),       //Profile
            new FragmentContainer(new ExploreTripsFragment()),  //ExploreTrips
            new FragmentContainer(new EventsFragment()),  //Events
            new FragmentContainer(new SearchFragment()),  //Search
            new FragmentContainer(new NoticeBoardFragment())   //Noticeboard
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null){
//            mCurrentFragmentItemId = savedInstanceState.get()
        }
        setContentView(R.layout.base_fragment_activity_layout);

    }

    // list of tab bar items that were actually added to the navdrawer, in order
    private ArrayList<Integer> mTabBarItems = new ArrayList<Integer>();

    // views that correspond to each tab bar item, null if not yet created
    private View[] mTabBarItemViews = null;

    private int getStartFragmentItem() {
        return TAB_BAR_ITEM_PROFILE;
    }



    /**
     * Sets up the tab bar as appropriate.
     */
    private void setupTabBar(){
        // What tab bar item should be selected?
        int selfItem = getStartFragmentItem();

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
        boolean selected = getStartFragmentItem() == itemId;

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


//    private void formatTabBarItem(View view, int itemId, boolean selected) {
//        ImageView iconView = (ImageView) view.findViewById(R.id.icon);
//        iconView.setColorFilter(selected ?
//                getResources().getColor(R.color.tabbar_icon_tint_selected) :
//                getResources().getColor(R.color.tabbar_icon_tint));
//    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setupTabBar();
    }
}
