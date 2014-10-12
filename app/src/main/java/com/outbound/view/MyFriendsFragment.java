package com.outbound.view;

import android.app.ActionBar;
import android.app.Activity;
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
import com.outbound.model.PFriendRequest;
import com.outbound.model.PUser;
import com.outbound.ui.util.adapters.BaseFragmentStatePagerAdapter;
import com.outbound.ui.util.adapters.MyFriendsAdapter;
import com.outbound.ui.util.SlidingTabLayout;
import com.outbound.util.Constants;
import com.parse.FindCallback;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zeki on 23/09/2014.
 */
public class MyFriendsFragment extends BaseFragment implements MyFriendsListSubFragment.Listener{

    private static final String ARG_FRIEND_STATUS_INDEX
            = "com.outbound.ARG_FRIEND_STATUS_INDEX";

    private SlidingTabLayout mSlidingTabLayout = null;
    private ViewPager mViewPager = null;
    private OurViewPagerAdapter mViewPagerAdapter = null;

    private MyFriendsAdapter mMyFriendsAdapter;
    private MyFriendsAdapter mMyFriendsPendingAdapter;

    private PUser currentUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentUser = PUser.getCurrentUser();
    }

    @Override
    protected void setUp(Object param1, Object param2) {
        super.setUp(param1,param2);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        setUpActionBar(activity);
    }

    private void setUpActionBar(Activity activity) {
        View viewActionBar = activity.getLayoutInflater().inflate(R.layout.custom_ab_back_button, null);
        TextView title = (TextView)viewActionBar.findViewById(R.id.action_bar_title);
        title.setText(getResources().getString(R.string.my_friends_fragment_title));
        ImageView icon = (ImageView)viewActionBar.findViewById(R.id.ab_icon_1);
        icon.setImageResource(R.drawable.action_search);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        final View view = inflater.inflate(R.layout.my_friends_fragment, container, false);

        setUpFriendListAdapter();
        setUpViewPager(view);
        setUpSlidingTabLayout(view);

        return view;
    }

    private void setUpFriendListAdapter() {
        //Test date
//        ArrayList<Object> arrayList = new ArrayList<Object>();
//
//        for (int i = 0; i < 50; i++) {
//            arrayList.add(new Object());
//        }

        mMyFriendsAdapter = new MyFriendsAdapter(getActivity(), false);
        PFriendRequest.findAcceptedCurrentUserRequest(currentUser, new FindCallback<PUser>() {
            @Override
            public void done(List<PUser> pUsers, ParseException e) {
                if(mMyFriendsAdapter!=null){
                    for (PUser user : pUsers){
                        mMyFriendsAdapter.add(user);
                        updateView(mMyFriendsAdapter);
                    }
                }
            }
        });

        mMyFriendsPendingAdapter = new MyFriendsAdapter(getActivity(), true);
        PFriendRequest.findPendingCurrentUserRequest(currentUser, new FindCallback<PUser>() {
            @Override
            public void done(List<PUser> pUsers, ParseException e) {
                if(mMyFriendsPendingAdapter!=null){
                    for (PUser user : pUsers){
                        mMyFriendsPendingAdapter.add(user);
                        updateView(mMyFriendsPendingAdapter);
                    }
                }
            }
        });
    }

    private void updateView(MyFriendsAdapter adapter) {
        // Update the adapter view
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private void setUpViewPager(View view) {
        mViewPager = (ViewPager) view.findViewById(R.id.view_pager);
        mViewPagerAdapter = new OurViewPagerAdapter(getChildFragmentManager());
        mViewPager.setAdapter(mViewPagerAdapter);
    }

    private void setUpSlidingTabLayout(View view) {
        mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setCustomTabView(R.layout.tab_indicator, android.R.id.text1);

        setSlidingTabLayoutContentDescriptions();

        Resources res = getResources();
        mSlidingTabLayout.setSelectedIndicatorColors(res.getColor(R.color.tab_selected_strip));
        mSlidingTabLayout.setDistributeEvenly(true);
        mSlidingTabLayout.setViewPager(mViewPager);
    }
    private void setSlidingTabLayoutContentDescriptions() {

        mSlidingTabLayout.setContentDescription(0,"my_friends");
        mSlidingTabLayout.setContentDescription(1,"my_friends_pending");
    }

    @Override
    public void onFragmentViewCreated(ListFragment fragment) {
//        fragment.getListView().addHeaderView(
//                getLayoutInflater().inflate(R.layout.reserve_action_bar_space_header_view, null));
        int fragIndex = fragment.getArguments().getInt(ARG_FRIEND_STATUS_INDEX, 0);
        if(fragIndex == 0)
        {
            fragment.setListAdapter(mMyFriendsAdapter);
        }else{
            fragment.setListAdapter(mMyFriendsPendingAdapter);
        }

    }

    @Override
    public void onFragmentAttached(MyFriendsListSubFragment fragment) {

    }

    @Override
    public void onFragmentDetached(MyFriendsListSubFragment fragment) {

    }

    @Override
    public void onListItemClicked(ListFragment fragment, ListView l, View v, int position, long id) {
        int fragIndex = fragment.getArguments().getInt(ARG_FRIEND_STATUS_INDEX, 0);

        if(mCallbacks != null)
            mCallbacks.deployFragment(Constants.PEOPLE_PROFILE_FRAG_ID, null, null);
    }

    private class OurViewPagerAdapter extends BaseFragmentStatePagerAdapter {

        CharSequence[] title = {"Friends" , "Pending Requests"};
        public OurViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            MyFriendsListSubFragment frag = new MyFriendsListSubFragment();
            frag.setUp(MyFriendsFragment.this);
            Bundle args = new Bundle();
            args.putInt(ARG_FRIEND_STATUS_INDEX, position);
            frag.setArguments(args);
            return frag;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return title[position];
        }
    }
}
