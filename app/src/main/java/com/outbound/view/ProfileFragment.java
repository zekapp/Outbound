package com.outbound.view;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import android.support.v4.app.Fragment;

import com.outbound.R;
import com.outbound.ui.util.BaseFragmentStatePagerAdapter;
import com.outbound.ui.util.ProfileMessageListViewAdapter;
import com.outbound.ui.util.SwipeRefreshLayout;
import com.outbound.ui.util.UIUtils;
import com.outbound.ui.util.ZoomOutPageTransformer;
import com.outbound.util.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by zeki on 2/09/2014.
 */
public class ProfileFragment extends BaseFragment {

    private FrameLayout mFrameLayout;
    private PagerAdapter mPagerAdapter;
    private ViewPager mViewPager;
    private SwipeRefreshLayout mSwipeRefreshLayoutNews;
    private ListView mMessageListView;

    View dots[] = new View[2];
    @Override
    protected void setUp(int baseActivityFrameLayoutId) {
        super.setUp(baseActivityFrameLayoutId);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        View viewActionBar = activity.getLayoutInflater().inflate(R.layout.custom_ab_profile, null);
        TextView title = (TextView)viewActionBar.findViewById(R.id.action_bar_title);
        title.setText(getResources().getString(R.string.profile_fragment_title));
        ActionBar actionBar = activity.getActionBar();
        if(actionBar!=null) {
            actionBar.setCustomView(viewActionBar);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        final View view = inflater.inflate(R.layout.profile_fragment, container, false);

//        String[] values = new String[] { "Profile 1", "Profile 2", "Profile 3",
//                "Profile 4", "Profile 5", "Profile 6", "Profile 7", "Profile 8",
//                "Profile 9", "Profile 10", "Profile 11", "Profile 12", "Profile 13", "Profile 14",
//                "Profile 15", "Profile 16", "Profile 17", "Profile 18", "Profile 19", "Profile 20",
//                "Profile 21", "Profile 22", "Profile 23" };
//
//        ArrayList<String> list = new ArrayList<String>();
//        for (int i = 0; i < values.length; ++i) {
//            list.add(values[i]);
//        }
//
//        StableArrayAdapter adapter = new StableArrayAdapter(getActivity(),
//                android.R.layout.simple_list_item_1, list);
        setUpProfileFunctionLayout(view);

        dots[0] = view.findViewById(R.id.profile_left_dot);
        dots[1] = view.findViewById(R.id.profile_right_dot);

        mViewPager = (ViewPager) view.findViewById(R.id.profile_pager);
        mViewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        mPagerAdapter = new ScreenSlidePagerAdapter(getChildFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);

        mSwipeRefreshLayoutNews = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        if (mSwipeRefreshLayoutNews != null){
            mSwipeRefreshLayoutNews.setColorScheme(
                    R.color.refresh_progress_1,
                    R.color.refresh_progress_2,
                    R.color.refresh_progress_3,
                    R.color.refresh_progress_4);

            mSwipeRefreshLayoutNews.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    //get the latest message for this user.
                }
            });
        }

        mMessageListView = (ListView)view.findViewById(R.id.profile_message_list);


        ArrayList<Object> testItem = new ArrayList<Object>();
        for (int i=0;i<50;i++){
            testItem.add(new Object());
        }
        ProfileMessageListViewAdapter adapter = new ProfileMessageListViewAdapter(getActivity(),testItem);
        mMessageListView.setAdapter(adapter);



        ((ImageView)dots[mViewPager.getCurrentItem()]).setImageResource(R.drawable.circle_white_dot);

        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                ((ImageView)dots[position]).setImageResource(R.drawable.circle_white_dot);
                ((ImageView)dots[(position+1)%2]).setImageResource(R.drawable.circle_clear_dot);
            }
        });

//        mCallbacks.registerHideableHeaderView(view.findViewById(R.id.profile_pager_layout));
//        mCallbacks.registerHideableHeaderView(view.findViewById(R.id.profile_user_info_id));
//        mCallbacks.registerHideableHeaderView(view.findViewById(R.id.profile_function_layout_id));
//        mCallbacks.registerHideableHeaderView(view.findViewById(R.id.layout_container_id));

//        mCallbacks.registerSwipeRefreshProgressBarAsTop(mSwipeRefreshLayoutNews,getSwipewRefreshLayoutTopClearance());
        mCallbacks.enableActionBarAutoHide(mMessageListView);
        return view;
    }

    private void setUpProfileFunctionLayout(View view) {
        RelativeLayout relativeLayoutFriend = (RelativeLayout)view.findViewById(R.id.pf_friends_layout);
        relativeLayoutFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallbacks.deployFragment(Constants.PROFILE_FRIENDS_ITEM);
            }
        });
        LinearLayout myTripsLayout = (LinearLayout)view.findViewById(R.id.pf_my_trips_layout);
        myTripsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallbacks.deployFragment(Constants.PROFILE_MY_TRIP_ITEM);
            }
        });
        LinearLayout travellerHistory = (LinearLayout)view.findViewById(R.id.pf_traveller_history_layout);
        travellerHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallbacks.deployFragment(Constants.PROFILE_TRAVEL_HISTORY_ITEM);
            }
        });
        LinearLayout events = (LinearLayout)view.findViewById(R.id.pf_my_events_layout);
        events.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallbacks.deployFragment(Constants.PROFILE_MY_EVENTS_ITEM);
            }
        });
    }

    private int getSwipewRefreshLayoutTopClearance(){
        int actionBarClearance = UIUtils.calculateActionBarSize(getActivity());
        int profileUserInfoClearance = getResources().getDimensionPixelSize(R.dimen.profile_user_info_height);
        int profileFunctionLayoutHeight = getResources().getDimensionPixelSize(R.dimen.profile_function_layout_height);

        return actionBarClearance + profileUserInfoClearance + profileFunctionLayoutHeight;

    }

    public void friendsLayoutClicked(View view) {
    }

    private class ScreenSlidePagerAdapter extends BaseFragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return position==0?new ProfilePictureFragment():new ProfileAboutFragment();
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

//    private class StableArrayAdapter extends ArrayAdapter<String> {
//
//        HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();
//
//        public StableArrayAdapter(Context context, int textViewResourceId,
//                                  List<String> objects) {
//            super(context, textViewResourceId, objects);
//            for (int i = 0; i < objects.size(); ++i) {
//                mIdMap.put(objects.get(i), i);
//            }
//        }
//
//        @Override
//        public long getItemId(int position) {
//            String item = getItem(position);
//            return mIdMap.get(item);
//        }
//
//        @Override
//        public boolean hasStableIds() {
//            return true;
//        }
//
//    }
}
