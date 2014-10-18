package com.outbound.view;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.TextView;

import com.outbound.R;
import com.outbound.model.PNoticeBoard;
import com.outbound.model.PUser;
import com.outbound.ui.util.SlidingTabLayout;
import com.outbound.ui.util.SwipeRefreshLayout;
import com.outbound.ui.util.adapters.BaseFragmentStatePagerAdapter;
import com.outbound.ui.util.adapters.NoticeBoardMessageAdapter;
import com.outbound.util.Constants;
import com.outbound.util.ResultCallback;
import com.parse.FindCallback;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.List;

import static com.outbound.util.LogUtils.*;

/**
 * Created by zeki on 15/09/2014.
 */
public class NoticeBoardFragment extends BaseFragment implements NoticeBoardSubListFragment.Listener ,
        NoticeBoardMessageAdapter.OnNoticeBoardItemClickedListener{
    private static final String TAG = makeLogTag(NoticeBoardFragment.class);
    private static final String ARG_FRIEND_STATUS_INDEX
            = "com.outbound.ARG_NOTICE_BOARD_INDEX";

    //four adapter for four different distance.
    private NoticeBoardMessageAdapter[] adapter = new NoticeBoardMessageAdapter[4];
    private ViewPager mViewPager = null;
    private OurViewPagerAdapter mViewPagerAdapter = null;
    private SlidingTabLayout mSlidingTabLayout = null;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        setUpActionBar(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int i;
        for (i = 0; i < 4; i++) {
            adapter[i] = new NoticeBoardMessageAdapter(getActivity());
            adapter[i].addOnItemclickedListener(this);
        }
    }

    private void setUpActionBar(Activity activity) {
        View viewActionBar = activity.getLayoutInflater().inflate(R.layout.custom_ab_noticeboard, null);
        TextView title = (TextView)viewActionBar.findViewById(R.id.action_bar_title);
        title.setText(getResources().getString(R.string.action_bar_noticebard_title));

        ImageView icon1 = (ImageView)viewActionBar.findViewById(R.id.ab_noticeboard_add_post);
        icon1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //add a post
                if(mCallbacks != null)
                    mCallbacks.deployFragment(Constants.NOTICE_BOARD_CREATE_MESSAGE_FRAG_ID,null,null);
            }
        });

        ImageView icon2 = (ImageView)viewActionBar.findViewById(R.id.ab_noticeboard_search);
        icon2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //search
                if(mCallbacks != null)
                    mCallbacks.deployFragment(Constants.NOTICE_BOARD_SEARCH_MESSAGE_FRAG_ID,null,null);
            }
        });

        ActionBar actionBar = activity.getActionBar();
        if(actionBar!=null) {
            actionBar.setCustomView(viewActionBar);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        final View view = inflater.inflate(R.layout.notice_board_fragment, container, false);

        setUpNoticeBoardAdapter();
        setUpViewPager(view);
        setUpSlidingTabLayout(view);

        return view;
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

        mSlidingTabLayout.setContentDescription(0,"20km");
        mSlidingTabLayout.setContentDescription(1,"100km");
        mSlidingTabLayout.setContentDescription(2,"Country");
        mSlidingTabLayout.setContentDescription(3,"Worldwide");
    }

    private void setUpViewPager(View view) {
        mViewPager = (ViewPager) view.findViewById(R.id.view_pager);
        mViewPagerAdapter = new OurViewPagerAdapter(getChildFragmentManager());
        mViewPager.setAdapter(mViewPagerAdapter);
    }

    private void setUpNoticeBoardAdapter() {
        updateAdapterWhitin20Km(null);
        updateAdapterWhitin100Km(null);
        updateAdapterWhitinCountry(null);
        updateAdapterWhitinWorld(null);
    }

    private void updateAdapterWhitinWorld(final SwipeRefreshLayout swipeRefreshLayout) {
        PNoticeBoard.findPostsWhitinWorld(new FindCallback<PNoticeBoard>() {
            @Override
            public void done(List<PNoticeBoard> pNoticeBoards, ParseException e) {
                if(swipeRefreshLayout != null)
                    swipeRefreshLayout.setRefreshing(false);

                if (e == null) {
                    adapter[3].clear();
                    adapter[3].addAll(pNoticeBoards);
                    updateView(3);
                } else {
                    LOGD(TAG, "findPostsWhitinWorld e: " + e.getMessage());
                    showToastMessage("Noetwork Error. Check connection");
                }
            }
        });
    }

    private void updateAdapterWhitinCountry(final SwipeRefreshLayout swipeRefreshLayout) {
        PNoticeBoard.findPostsWhitinCountry(new FindCallback<PNoticeBoard>() {
            @Override
            public void done(List<PNoticeBoard> pNoticeBoards, ParseException e) {
                if(swipeRefreshLayout != null)
                    swipeRefreshLayout.setRefreshing(false);

                if (e == null) {
                    adapter[2].clear();
                    adapter[2].addAll(pNoticeBoards);
                    updateView(2);
                } else {
                    LOGD(TAG, "findPostsWhitinCountry e: " + e.getMessage());
                    showToastMessage("Noetwork Error. Check connection");
                }
            }
        });
    }

    private void updateAdapterWhitin100Km(final SwipeRefreshLayout swipeRefreshLayout) {
        PNoticeBoard.findPostsWhitinKm((float)100, new FindCallback<PNoticeBoard>() {
            @Override
            public void done(List<PNoticeBoard> pNoticeBoards, ParseException e) {
                if(swipeRefreshLayout != null)
                    swipeRefreshLayout.setRefreshing(false);
                if(e == null){
                    adapter[1].clear();
                    adapter[1].addAll(pNoticeBoards);
                    updateView(1);

                }else
                {
                    LOGD(TAG, "findPostsWhitinKm 100 e: " + e.getMessage());
                    showToastMessage("Noetwork Error. Check connection");
                }
            }
        });
    }

    private void updateAdapterWhitin20Km(final SwipeRefreshLayout swipeRefreshLayout) {
        PNoticeBoard.findPostsWhitinKm((float)20, new FindCallback<PNoticeBoard>() {
            @Override
            public void done(List<PNoticeBoard> pNoticeBoards, ParseException e) {
                if(swipeRefreshLayout != null)
                    swipeRefreshLayout.setRefreshing(false);

                if(e == null){
                    adapter[0].clear();
                    adapter[0].addAll(pNoticeBoards);
                    updateView(0);
                }else
                {
                    LOGD(TAG, "findPostsWhitinKm 20 e: " + e.getMessage());
                    showToastMessage("Noetwork Error. Check connection");
                }
            }
        });
    }

    private void updateView(int fragId) {
        adapter[fragId].notifyDataSetChanged();
    }

    @Override
    public void onFragmentViewCreated(ListFragment fragment) {
        //        fragment.getListView().addHeaderView(
//                getLayoutInflater().inflate(R.layout.reserve_action_bar_space_header_view, null));
        int fragIndex = fragment.getArguments().getInt(ARG_FRIEND_STATUS_INDEX, 0);
        LOGD(TAG, "onFragmentViewCreated index: " + fragIndex);
        fragment.setListAdapter(adapter[fragIndex]);
    }

    @Override
    public void onFragmentAttached(ListFragment fragment) {

    }

    @Override
    public void onFragmentDetached(ListFragment fragment) {

    }

    @Override
    public void onFragmentSwipeRefreshed(ListFragment fragment,SwipeRefreshLayout swipeRefreshLayout) {
        //fragIndex Refrehsed
        int fragIndex = fragment.getArguments().getInt(ARG_FRIEND_STATUS_INDEX, 0);

        switch (fragIndex){
            case 0:
                updateAdapterWhitin20Km(swipeRefreshLayout);
                break;
            case 1:
                updateAdapterWhitin100Km(swipeRefreshLayout);
                break;
            case 2:
                updateAdapterWhitinCountry(swipeRefreshLayout);
                break;
            case 3:
                updateAdapterWhitinWorld(swipeRefreshLayout);
                break;
        }


    }

    @Override
    public void profilePictureClicked(PUser user) {
        if(mCallbacks != null)
            mCallbacks.deployFragment(Constants.PEOPLE_PROFILE_FRAG_ID,user,null);
    }

    private class OurViewPagerAdapter extends BaseFragmentStatePagerAdapter {

        CharSequence[] title = {"20km" , "100km", "Country", "World"};
        public OurViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            NoticeBoardSubListFragment frag = new NoticeBoardSubListFragment();
            frag.setUp(NoticeBoardFragment.this);
            Bundle args = new Bundle();
            args.putInt(ARG_FRIEND_STATUS_INDEX, position);
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


