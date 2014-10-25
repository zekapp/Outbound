package com.outbound.view;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.widget.WebDialog;
import com.outbound.R;
import com.outbound.model.PNoticeBoard;
import com.outbound.model.PUser;
import com.outbound.ui.util.SlidingTabLayout;
import com.outbound.ui.util.SwipeRefreshLayout;
import com.outbound.ui.util.adapters.BaseFragmentStatePagerAdapter;
import com.outbound.ui.util.adapters.NoticeBoardMessageAdapter;
import com.outbound.util.Constants;
import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;

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
    private final int REQUIRED_FRIEND_COUNT = 5;

    //four adapter for four different distance.
    private NoticeBoardMessageAdapter[] adapter = new NoticeBoardMessageAdapter[4];
    private ViewPager mViewPager = null;
    private OurViewPagerAdapter mViewPagerAdapter = null;
    private SlidingTabLayout mSlidingTabLayout = null;

    private static int invitedFriendCount = 0;
    private static final String INVITED_FACEBOOK_FRIEND_COUNT = "invoted_facebook_friend_count";

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        setUpActionBar(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
//        invitedFriendCount = sp.getInt(INVITED_FACEBOOK_FRIEND_COUNT, 0);

        int i;
        for (i = 0; i < 4; i++) {
            if(adapter[i] == null){
                adapter[i] = new NoticeBoardMessageAdapter(getActivity());
                adapter[i].addOnItemclickedListener(this);
            }
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
        mSlidingTabLayout.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if(position == 3){
                    if(invitedFriendCount < REQUIRED_FRIEND_COUNT){
                        openFacebookRequestDialog();
                    }
                }
            }
        });;

    }

    private void openFacebookRequestDialog() {
        final AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
        ad.setTitle("Invite your Friends");
        ad.setMessage("This application is better with friends! Would you like" +
                " to invite them. Inviting 5 friends will activate the WorldWide noticeboard ");
        ad.setPositiveButton("Invite", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                inviteFriends(new FunctionCallback<Integer>() {
                    @Override
                    public void done(Integer count, ParseException e) {
                        if(count != null){
                            if(count >= (REQUIRED_FRIEND_COUNT - invitedFriendCount)){
                                // update parse database
                                invitedFriendCount = count + invitedFriendCount;
                                saveFriendRequestCount(invitedFriendCount);
                                showToastMessage("WorldWide noticeboard opened");
                                updateAdapterWhitinWorld(null);
                            }else
                            {
                                invitedFriendCount = count + invitedFriendCount;
                                saveFriendRequestCount(invitedFriendCount);
                                showToastMessage("You have to invite at least "+
                                        Integer.toString(REQUIRED_FRIEND_COUNT-invitedFriendCount)+" friends...");
                                mViewPager.setCurrentItem(2);
                            }

                        }else
                        {
                            mViewPager.setCurrentItem(2);
                        }

                    }
                });
                dialog.dismiss();
            }
        });
        ad.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mViewPager.setCurrentItem(2);
                dialog.dismiss();
            }
        });
        ad.show();

    }

    private void saveFriendRequestCount(int count) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sp.edit().putInt(INVITED_FACEBOOK_FRIEND_COUNT, count).commit();
    }

    private void inviteFriends(final FunctionCallback<Integer> callback) {
        final Session session = ParseFacebookUtils.getSession();
        if(session != null && session.isOpened()){
            LOGD(TAG, "facebook session is open");
            Bundle params = new Bundle();
            params.putString("message", "Hey. Have you try the \"Outbounders\". It is an awesome application for backpackers.");

            WebDialog requestsDialog = (
                    new WebDialog.RequestsDialogBuilder(getActivity(),
                            Session.getActiveSession(),
                            params))
                    .setOnCompleteListener(new WebDialog.OnCompleteListener() {
                        @Override
                        public void onComplete(Bundle values,
                                               FacebookException error) {
                            if (error != null) {
                                if (error instanceof FacebookOperationCanceledException) {
                                    Toast.makeText(getActivity().getApplicationContext(),
                                            "Request cancelled",
                                            Toast.LENGTH_SHORT).show();
                                    callback.done(null,null);
                                } else {
                                    Toast.makeText(getActivity().getApplicationContext(),
                                            "Network Error",
                                            Toast.LENGTH_SHORT).show();
                                    callback.done(null,null);
                                }
                            } else {
                                ArrayList<String> to = new ArrayList<String>();
                                final String requestId = values.getString("request");
                                int i = 0;
                                while (true) {
                                    String x = values.getString("to["+i+"]");
                                    if (x == null) {
                                        break;
                                    } else {
                                        to.add(x);
                                        i++;
                                    }
                                }
                                if (requestId != null) {
                                    Toast.makeText(getActivity().getApplicationContext(),
                                            "Request sent",
                                            Toast.LENGTH_SHORT).show();
                                    callback.done(to.size(),null);// you need count;
                                } else {
                                    Toast.makeText(getActivity().getApplicationContext(),
                                            "Request cancelled",
                                            Toast.LENGTH_SHORT).show();
                                    callback.done(null,null);
                                }
                            }
                        }
                    })
                    .build();
            requestsDialog.show();

        }else{
            LOGD(TAG, "facebook session is closed");
            Toast.makeText(getActivity().getApplicationContext(),
                    "facebook session is closed",
                    Toast.LENGTH_SHORT).show();
            callback.done(null,null);
        }
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

        if(adapter[0].isEmpty())
            updateAdapterWhitin20Km(null);
        if(adapter[1].isEmpty())
            updateAdapterWhitin100Km(null);
        if(adapter[2].isEmpty())
            updateAdapterWhitinCountry(null);

        if(invitedFriendCount >= REQUIRED_FRIEND_COUNT) {
            if(adapter[3].isEmpty())
                updateAdapterWhitinWorld(null);
        }
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

    // NoticeBoardSubListFragment.Listener
    //--------------------------------------------------------
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
                if(invitedFriendCount >= REQUIRED_FRIEND_COUNT)
                    updateAdapterWhitinWorld(swipeRefreshLayout);
                break;
        }
    }

    @Override
    public void onListFragmentAnItemClicked(ListFragment fragment, int itemPositionInList) {
        int fragIndex = fragment.getArguments().getInt(ARG_FRIEND_STATUS_INDEX, 0);
        //store fragment index to user mViewPager.setCurrentItem(2);
        //store adapter[4] to regenerate the this fragment

        PNoticeBoard post = adapter[fragIndex].getItem(itemPositionInList);

        if(mCallbacks != null)
            mCallbacks.deployFragment(Constants.NOTICE_BOARD_POST_DETAIL_FRAG_ID,post,null);
    }

    //-------------------------------------------

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


