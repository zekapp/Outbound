package com.outbound.view;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.media.Image;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import android.support.v4.app.Fragment;

import com.outbound.R;
import com.outbound.model.ChatMessage;
import com.outbound.model.PChatActivity;
import com.outbound.model.PFriendRequest;
import com.outbound.model.PUser;
import com.outbound.ui.util.ParallaxParseImageView;
import com.outbound.ui.util.RoundedImageView;
import com.outbound.ui.util.adapters.BaseFragmentStatePagerAdapter;
import com.outbound.ui.util.adapters.ProfileMessageListViewAdapter;
import com.outbound.ui.util.SwipeRefreshLayout;
import com.outbound.ui.util.UIUtils;
import com.outbound.ui.util.ZoomOutPageTransformer;
import com.outbound.util.Constants;
import com.outbound.util.FindAddressCallback;
import com.outbound.util.GenericMessage;
import com.outbound.util.MessagesResultCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseImageView;
import com.parse.SaveCallback;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.outbound.util.LogUtils.*;

/**
 * Created by zeki on 2/09/2014.
 */
public class ProfileFragment extends BaseFragment implements ProfilePictureFragment.Listener,
        ProfileAboutFragment.Listener, ProfileMessageListViewAdapter.OnChatActivityMsgItemClickedListener {
    private static final String TAG = makeLogTag(BaseFragment.class);

    private static final String ARG_PROFILE_INDEX
            = "com.outbound.ARG_PROFILE_INDEX";


    private TextView friendBadge;

    private FrameLayout mFrameLayout;
    private PagerAdapter mPagerAdapter;
    private ViewPager mViewPager;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ListView mListView;

    private PUser currentUser = PUser.getCurrentUser();
    private View dots[] = new View[2];

    private Runnable currentLocationAction;
    private Runnable friendBadgeAction;
    private Runnable newPostAction;
    private TextView currenLocText;

    private ParallaxParseImageView coverPicture;
    private ProfileMessageListViewAdapter messageAdapter = null;

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
        View viewActionBar = activity.getLayoutInflater().inflate(R.layout.custom_ab_profile, null);
        TextView title = (TextView)viewActionBar.findViewById(R.id.action_bar_title);
        title.setText(getResources().getString(R.string.profile_fragment_title));
        ImageView setIcon = (ImageView)viewActionBar.findViewById(R.id.ab_setting_icon);
        ActionBar actionBar = activity.getActionBar();
        if(actionBar!=null) {
            actionBar.setCustomView(viewActionBar);
        }

        setIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCallbacks != null)
                    mCallbacks.deployFragment(Constants.PROFILE_SETTINGS_FRAG_ID,null,null);
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private static final String CURRENT_CITY_INSTANCE = "selected_city_name";
    private static final String CURRENT_COUNTRY_NAME_INSTANCE = "selected_country_name_name";
    private static final String CURRENT_COUNTRY_CODE_INSTANCE = "selected_country_code";
    private String currentCity = "-";
    private String currentCountryName = "-";
    private String currentCountryCode = "-";
    private static  boolean firstInitializeDone = false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        final View view = inflater.inflate(R.layout.profile_fragment, container, false);
        final View header = inflater.inflate(R.layout.profile_header,null);

//        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
//        currentCity = sp.getString(CURRENT_CITY_INSTANCE, "-");
//        currentCountryName = sp.getString(CURRENT_COUNTRY_NAME_INSTANCE, "-");
//        currentCountryCode = sp.getString(CURRENT_COUNTRY_CODE_INSTANCE, "-");


        setUpHeaderUserInfo(header);
        setUpheaderFunction(header);
        setUpListView(view, header);
        setUpProfileFunctionLayout(view);
        setUpViewPager(view);
        setUpSwipeRefreshLayout(view);

        registerForHideableViews(view);

        setUpNewPostCheckThread();

//        setUpTimer();
        return view;
    }

    private void setUpNewPostCheckThread() {

        if(mListView != null){
            newPostAction = new Runnable() {
                @Override
                public void run() {
                    if(messageAdapter != null && firstInitializeDone){
                        LOGD(TAG, "setUpNewMessageCheckThread called");
                        List<PChatActivity> pChatActivityList = messageAdapter.getAllItem();
                        PChatActivity.fetchedNewPostsThatIParticipated(pChatActivityList, new FindCallback<PChatActivity>() {
                            @Override
                            public void done(List<PChatActivity> pChatActivities, ParseException e) {
                                if(e==null){
                                    if(pChatActivities.size()>0){
                                        // new post created.
                                        messageAdapter.addAll(pChatActivities);
                                        messageAdapter.orderItemsAccordingTimeOrder();
                                        updateView();
                                    }
                                }else{
                                    LOGD(TAG, "setUpNewMessageCheckThread e: " + e.getMessage());
                                }
                            }
                        });


//                        int count =  messageAdapter.getCount();
//                        for(int i =0 ; i<count ; i++){
//                            final PChatActivity post = messageAdapter.getItem(i);
//                            List<GenericMessage> msg = post.getAllMessages();
//                            GenericMessage finalMsg = msg.get(msg.size()-1);
//                        }
                    }
                    mListView.postDelayed(this,5000);
                }
            };

            mListView.post(newPostAction);
        }
    }

    //    private void setUpTimer() {
//        new CountDownTimer(30000, 1000) {
//
//            public void onTick(long millisUntilFinished) {
//
//            }
//
//            public void onFinish() {
//                LOGD(TAG, "timer ticked");
//            }
//        }.start();
//
//    }

    private void setUpheaderFunction(View v) {
        friendBadge = (TextView)v.findViewById(R.id.friend_function_badge);

        friendBadgeAction = new Runnable() {
            @Override
            public void run() {

                PFriendRequest.findPendingUsersInBackground(currentUser, new FindCallback<PFriendRequest>() {
                    @Override
                    public void done(List<PFriendRequest> pFriendRequests, ParseException e) {
                        if(e == null){
                            if(pFriendRequests.size() > 0){
                                LOGD(TAG,"updateFriendBadge friendReuestSize: " + pFriendRequests.size());
                                friendBadge.setVisibility(View.VISIBLE);
                                friendBadge.setText(Integer.toString(pFriendRequests.size()));
                            }else
                                friendBadge.setVisibility(View.GONE);
                        }else
                        {
                            LOGD(TAG,"updateFriendBadge friendRequestSize: " + e.getMessage());
                            showToastMessage("Network Error. Check connection.");
                        }

                    }
                });
                friendBadge.postDelayed(this,10000);
            }
        };

        friendBadge.post(friendBadgeAction);
//        updateFriendBadge();
    }
//    private void updateFriendBadge() {
//        PFriendRequest.findPendingUsersInBackground(currentUser, new FindCallback<PFriendRequest>() {
//            @Override
//            public void done(List<PFriendRequest> pFriendRequests, ParseException e) {
//                if(e == null){
//                    if(pFriendRequests.size() > 0){
//                        LOGD(TAG,"updateFriendBadge friendReuestSize: " + pFriendRequests.size());
//                        friendBadge.setVisibility(View.VISIBLE);
//                        friendBadge.setText(Integer.toString(pFriendRequests.size()));
//                    }else
//                        friendBadge.setVisibility(View.GONE);
//                }else
//                {
//                    showToastMessage(e.getMessage());
//                    LOGD(TAG,"updateFriendBadge friendReuestSize: " + e.getMessage());
//                }
//
//            }
//        });
//    }

    private void setUpSwipeRefreshLayout(View view) {
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        if (mSwipeRefreshLayout != null){
            mSwipeRefreshLayout.setColorScheme(
                    R.color.refresh_progress_1,
                    R.color.refresh_progress_2,
                    R.color.refresh_progress_3,
                    R.color.refresh_progress_4);

            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    //get the latest message for this user.
                }
            });
        }

        mSwipeRefreshLayout.setEnabled(false);
    }

    private void setUpViewPager(View view) {
        dots[0] = view.findViewById(R.id.profile_left_dot);
        dots[1] = view.findViewById(R.id.profile_right_dot);
        mViewPager = (ViewPager) view.findViewById(R.id.profile_pager);
        mViewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        mPagerAdapter = new ScreenSlidePagerAdapter(getChildFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);

        ((ImageView)dots[mViewPager.getCurrentItem()]).setImageResource(R.drawable.circle_white_dot);

        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                ((ImageView)dots[position]).setImageResource(R.drawable.circle_white_dot);
                ((ImageView)dots[(position+1)%2]).setImageResource(R.drawable.circle_clear_dot);
            }
        });
    }

    private void registerForHideableViews(View view) {
//        mCallbacks.registerHideableHeaderView(view.findViewById(R.id.profile_pager_layout));
//        mCallbacks.registerHideableHeaderView(view.findViewById(R.id.profile_user_info_id));
//        mCallbacks.registerHideableHeaderView(view.findViewById(R.id.profile_function_layout_id));
//        mCallbacks.registerHideableHeaderView(view.findViewById(R.id.layout_container_id));

//        mCallbacks.registerSwipeRefreshProgressBarAsTop(mSwipeRefreshLayout,getSwipeRefreshLayoutTopClearance());
        mCallbacks.enableActionBarAutoHide(mListView);
    }

    private void setUpListView(View view, View header) {
        mListView = (ListView)view.findViewById(R.id.profile_message_list);

        if(messageAdapter == null) {
            messageAdapter = new ProfileMessageListViewAdapter(getActivity());
            messageAdapter.addOnNoticeBoardMsgItemClickedListener(this);
        }

        firstInitializeDone = false;
        feedAdapter();

        mListView.addHeaderView(header, null, false);
        mListView.setAdapter(messageAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(mCallbacks != null)
                    mCallbacks.deployFragment(Constants.CHAT_POST_DETAIL_FRAG_ID, parent.getAdapter().getItem(position), null);
//                Object messageObject = parent.getAdapter().getItem(position);
//                Intent intent = new Intent(getActivity(), MessageActivity.class);
//                startActivity(intent);
            }
        });
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                PChatActivity post = (PChatActivity)parent.getAdapter().getItem(position);
                opendDeleteDialog(post,position);
                return true;

            }
        });

    }

    private void opendDeleteDialog(final PChatActivity post, final int index) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle("Delete");
        alertDialog.setMessage("Do you want to delete this conversation?");
        alertDialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // remove the user from participiant list
                // remove this conversation from adapter. do not forget to chec timestamp.
                startProgress("Your Message deleting...");
                post.putMeInLeftArray();
                post.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        stopProgress();
                        if(e == null){
//                            startAnimation(post,index);
                            messageAdapter.remove(post);
                            updateView();
                            showToastMessage("Your message deleted.");
                        }else{
                            showToastMessage("Network Error...");
                            LOGD(TAG,"opendDeleteDialog-saveInBackground  e: " + e.getMessage() );
                        }
                    }
                });
                dialog.dismiss();
            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void startAnimation(final PChatActivity post, int index) {
        Animation anim = AnimationUtils.loadAnimation(
                getActivity(), android.R.anim.slide_out_right
        );
        anim.setDuration(500);
        mListView.getChildAt(index).startAnimation(anim );

        new Handler().postDelayed(new Runnable() {

            public void run() {
                messageAdapter.remove(post);
                updateView();
            }

        }, anim.getDuration());
    }

    private void feedAdapter() {
        PChatActivity.fetchedPostsThatIParticipated(new FindCallback<PChatActivity>() {
            @Override
            public void done(List<PChatActivity> pChatActivities, ParseException e) {
                if(e == null){
                    if(messageAdapter != null){
                        messageAdapter.clear();
                        messageAdapter.addAll(pChatActivities);
                        messageAdapter.orderItemsAccordingTimeOrder();
                        updateView();
                    }
                }else
                {
                    LOGD(TAG, "feedAdapter e: " + e.getMessage());
                }
                firstInitializeDone = true;
            }
        });
    }

    private void updateView() {
        if(messageAdapter != null)
            messageAdapter.notifyDataSetChanged();
    }

    private void setUpProfileFunctionLayout(View view) {
        RelativeLayout relativeLayoutFriend = (RelativeLayout)view.findViewById(R.id.pf_friends_layout);
        relativeLayoutFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallbacks.deployFragment(Constants.PROFILE_FRIENDS_FRAG_ID,null,null);
            }
        });
        LinearLayout myTripsLayout = (LinearLayout)view.findViewById(R.id.pf_my_trips_layout);
        myTripsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallbacks.deployFragment(Constants.PROFILE_MY_TRIP_FRAG_ID,null,null);
            }
        });
        LinearLayout travellerHistory = (LinearLayout)view.findViewById(R.id.pf_traveller_history_layout);
        travellerHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallbacks.deployFragment(Constants.PROFILE_TRAVEL_HISTORY_FRAG_ID,PUser.getCurrentUser(),null);
            }
        });
        LinearLayout events = (LinearLayout)view.findViewById(R.id.pf_my_events_layout);
        events.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallbacks.deployFragment(Constants.PROFILE_MY_EVENTS_FRAG_ID,null,null);
            }
        });
    }

    private int getSwipeRefreshLayoutTopClearance(){
        int actionBarClearance = UIUtils.calculateActionBarSize(getActivity());
        int profileUserInfoClearance = getResources().getDimensionPixelSize(R.dimen.profile_user_info_height);
        int profileFunctionLayoutHeight = getResources().getDimensionPixelSize(R.dimen.profile_function_layout_height);

        return actionBarClearance + profileUserInfoClearance + profileFunctionLayoutHeight;

    }

    public void friendsLayoutClicked(View view) {
    }



    @Override
    public void onPictureFragmentViewCreated(View v, Fragment fragment) {
        RoundedImageView profilePhoto = (RoundedImageView)v.findViewById(R.id.pp_photo);
        profilePhoto.setParseFile(currentUser.getProfilePicture());
        profilePhoto.loadInBackground();

        TextView homeText = (TextView)v.findViewById(R.id.pp_home_cityAndCountryCode_text);
        String homeTown = currentUser.getHometown();
        String countryCode = currentUser.getCountryCode();

        if(homeTown != null && countryCode != null )
            homeText.setText(homeTown+", "+countryCode);


        currenLocText = (TextView)v.findViewById(R.id.pp_current_location_text);

        currenLocText.setText(currentCity + ", " + currentCountryCode);
        currentLocationAction = new Runnable() {
            @Override
            public void run() {
                getAddress(new FindAddressCallback<Address>(this) {
                    @Override
                    public void done(String countryName, String cityName, String countryCode ,Exception e) {
                        if(e == null){
                            currentCity = cityName;
                            currentCountryName = countryName;
                            currentCountryCode = countryCode;
                            currenLocText.setText(currentCity + ", " + currentCountryCode);
                            currentUser.setCurrentCity(cityName);
                            currentUser.setCurrentCountry(countryName);
                            currentUser.saveInBackground();
                        }else
                            currenLocText.setText(currentCity + ", " + currentCountryCode);

                        currenLocText.postDelayed(this.action,10000);
                    }
                });
            }
        };

        currenLocText.post(currentLocationAction);

//        currenLocText.post(new Runnable() {
//            @Override
//            public void run() {
//                LOGD(TAG,"currentLoc ased");
//                getAddress(new FindAddressCallback<Address>() {
//                    @Override
//                    public void done(String countryName, String cityName, String countryCode) {
//                        currenLocText.setText(cityName + ", " + countryCode);
//                    }
//                });
//                currenLocText.postDelayed(this,10000);
//            }
//        });
    }


    @Override
    public void onAboutFragmentViewCreated(View v, Fragment fragment) {
        TextView aboutText = (TextView)v.findViewById(R.id.pa_about_text);
        aboutText.setText(currentUser.getShortDescription() != null ? currentUser.getShortDescription() : "...");
    }

    @Override
    public void onResume() {
        super.onResume();
        coverPicture.registerSensorManager();
    }

    @Override
    public void onPause() {
        super.onPause();
        if(currentLocationAction != null && currenLocText != null)
            currenLocText.removeCallbacks(currentLocationAction);
        if(friendBadgeAction != null && friendBadge != null)
            friendBadge.removeCallbacks(friendBadgeAction);

        if(newPostAction != null && mListView != null)
            mListView.removeCallbacks(newPostAction);

        coverPicture.unregisterSensorManager();

//        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
//        sp.edit().putString(CURRENT_CITY_INSTANCE, currentCity).commit();
//        sp.edit().putString(CURRENT_COUNTRY_NAME_INSTANCE, currentCountryName).commit();
//        sp.edit().putString(CURRENT_COUNTRY_CODE_INSTANCE, currentCountryCode).commit();

    }

    private void setUpHeaderUserInfo(View v) {
        ImageView flag = (ImageView)v.findViewById(R.id.profile_flag);

        if(currentUser.getCountryCode() != null)
            flag.setImageResource(getResources().
                    getIdentifier("drawable/" + currentUser.getCountryCode().toLowerCase(),
                            null, getActivity().getPackageName()));

        TextView profileName = (TextView)v.findViewById(R.id.profile_name);
        profileName.setText(currentUser.getUserName());

        TextView age = (TextView)v.findViewById(R.id.profile_age);
        age.setText(currentUser.getAge() > -1?Integer.toString(currentUser.getAge()):"-");

        TextView gender = (TextView)v.findViewById(R.id.profile_gender);
        gender.setText(currentUser.getGender());

        TextView profile_home = (TextView)v.findViewById(R.id.profile_home);
        profile_home.setText(currentUser.getNationality());

        coverPicture = (ParallaxParseImageView)v.findViewById(R.id.pp_coverPicture);
        coverPicture.setParseFile(currentUser.getCoverPicture());
        coverPicture.loadInBackground();

    }

    @Override
    public void profilePictureClicked(PUser user) {
        if(mCallbacks != null)
            mCallbacks.deployFragment(Constants.PEOPLE_PROFILE_FRAG_ID, user, null);
    }

    private class ScreenSlidePagerAdapter extends BaseFragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if(position == 0){
                ProfilePictureFragment frag = new ProfilePictureFragment();
                frag.setUp(ProfileFragment.this);
//                Bundle args = new Bundle();
//                args.putInt(ARG_PROFILE_INDEX, position);
//                frag.setArguments(args);
                return frag;
            }else{
                ProfileAboutFragment frag = new ProfileAboutFragment();
                frag.setUp(ProfileFragment.this);
//                Bundle args = new Bundle();
//                args.putInt(ARG_PROFILE_INDEX, position);
//                frag.setArguments(args);
                return frag;
            }


//            return position==0?new ProfilePictureFragment():new ProfileAboutFragment();
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
