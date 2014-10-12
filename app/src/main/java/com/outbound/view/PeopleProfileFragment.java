package com.outbound.view;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;

import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.outbound.R;
import com.outbound.model.PEvent;
import com.outbound.model.PFriendRequest;
import com.outbound.model.PUser;
import com.outbound.ui.util.ParallaxParseImageView;
import com.outbound.ui.util.RoundedImageView;
import com.outbound.ui.util.SwipeRefreshLayout;
import com.outbound.ui.util.ZoomOutPageTransformer;
import com.outbound.ui.util.adapters.BaseFragmentStatePagerAdapter;
import com.outbound.ui.util.adapters.PeopleEventAdapter;
import com.outbound.util.Constants;
import com.outbound.util.CountryCodes;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.util.List;

/**
 * Created by zeki on 30/09/2014.
 */
public class PeopleProfileFragment extends BaseFragment implements ProfilePictureFragment.Listener, ProfileAboutFragment.Listener{

    private PeopleEventAdapter adapter = null;
    private ListView list;

    private View dots[] = new View[2];
    private ViewPager mViewPager;
    private PagerAdapter mPagerAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private LinearLayout addFriend;
    private ParallaxParseImageView coverPicture;

    private PUser person;
    @Override
    protected void setUp(Object param1, Object param2) {
        super.setUp(param1,param2);
        if(param1 instanceof PUser)
            person = (PUser)param1;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        setUpActionBar(activity);
    }

    private void setUpActionBar(Activity activity) {
        View viewActionBar = activity.getLayoutInflater().inflate(R.layout.custom_ab_back_button, null);
        final TextView title = (TextView)viewActionBar.findViewById(R.id.action_bar_title);
        title.setText(getResources().getString(R.string.outbounder_fragment_title));
        ImageView icon = (ImageView)viewActionBar.findViewById(R.id.ab_icon_1);
        icon.setImageResource(R.drawable.action_message);
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

        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Message Intent will be run
                Intent intent = new Intent(getActivity(), MessageActivity.class);
                startActivity(intent);
            }
        });

        if(person != null)
            person.fetchIfNeededInBackground(new GetCallback<PUser>() {
                @Override
                public void done(PUser user, ParseException e) {
                    if(e == null)
                        title.setText(user.getUserName());
                }
            });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        final View view = inflater.inflate(R.layout.people_profile_fragment_layout, container, false);
        final View header = inflater.inflate(R.layout.people_profile_header,null);

        setUpSwipeRefreshLayout(view);

        //listView should be set up first
        setUpHeader(header);

        setUpListView(view,header);

        setUpViewPager(view);
        setUpProfileFunctionLayout(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        coverPicture.registerSensorManager();
    }

    @Override
    public void onPause() {
        super.onPause();
        coverPicture.unregisterSensorManager();
    }

    private void setUpHeader(View v) {

        final ImageView flag = (ImageView)v.findViewById(R.id.profile_flag);
        final TextView profileName = (TextView)v.findViewById(R.id.profile_name);
        final TextView age = (TextView)v.findViewById(R.id.profile_age);
        final TextView gender = (TextView)v.findViewById(R.id.profile_gender);
        final TextView profile_home = (TextView)v.findViewById(R.id.profile_home);
        coverPicture = (ParallaxParseImageView)v.findViewById(R.id.pp_coverPicture);
        if(person != null){
            person.fetchIfNeededInBackground(new GetCallback<PUser>() {
                @Override
                public void done(PUser pUser, ParseException e) {
                    if(e == null){
                        coverPicture.setParseFile(pUser.getCoverPicture());
                        coverPicture.loadInBackground();
                        profileName.setText(pUser.getUserName());
                        age.setText(Integer.toString(pUser.getAge()));
                        gender.setText(pUser.getGender());
                        profile_home.setText(pUser.getNationality());
                        flag.setImageResource(getResources().
                                getIdentifier("drawable/" + pUser.getCountryCode().toLowerCase(),
                                        null, getActivity().getPackageName()));
                    }
                }
            });
        }
    }


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
                    //get the latest status of this outbounder
                }
            });
        }
    }

    private void setUpProfileFunctionLayout(View view) {
        RelativeLayout relativeLayoutFriend = (RelativeLayout)view.findViewById(R.id.pf_friends_layout);
        relativeLayoutFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallbacks.deployFragment(Constants.PEOPLE_PROFILE_FRIENDS_FRAG_ID,null,null);
            }
        });
        LinearLayout travellerHistory = (LinearLayout)view.findViewById(R.id.pf_traveller_history_layout);
        travellerHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallbacks.deployFragment(Constants.PROFILE_TRAVEL_HISTORY_FRAG_ID,null,null);
            }
        });

        addFriend = (LinearLayout)view.findViewById(R.id.pf_add_friend_layout);
        addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //attemp t oSend Friend Request
                attempToSendFriendRequest();
            }
        });
    }

    private void attempToSendFriendRequest() {
        // after sending request successfull

        //add progress
        person.fetchIfNeededInBackground(new GetCallback<PUser>() {
            @Override
            public void done(PUser pUser, ParseException e) {
                if(e == null){
                    PFriendRequest.sendFriendRequest(pUser,PUser.getCurrentUser(), new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e == null){
                                addFriend.setVisibility(View.GONE);
                                createFriendRequestDialog();
                            }
                        }
                    });
                }
            }
        });


    }

    private void createFriendRequestDialog() {
        AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(getActivity());
        mDialogBuilder.setTitle(R.string.friend_request_title)
                .setMessage(R.string.friend_request_body)
                .setPositiveButton("Ok", new AlertDialog.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create().show();
    }

    private void setUpListView(View view, View header) {

        adapter = new PeopleEventAdapter(getActivity());

//        for (int i = 0; i < 50; i++) {
//            adapter.add(new Object());
//        }
        PEvent.findEventsOfSpecificUser(person, new FindCallback<PEvent>() {
            @Override
            public void done(List<PEvent> pEvents, ParseException e) {
                if(e == null){
                    for (PEvent event : pEvents){
                        adapter.add(event);
                    }
                    updateView();
                }
            }
        });

        list = (ListView) view.findViewById(R.id.people_list_view);
        list.addHeaderView(header, null, false);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(mCallbacks != null)
                    mCallbacks.deployFragment(Constants.EVENT_DETAIL_FRAG_ID,parent.getAdapter().getItem(position),null);
            }
        });

    }

    private void updateView() {
        if(adapter !=null)
            adapter.notifyDataSetChanged();
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

    @Override
    public void onAboutFragmentViewCreated(View root, Fragment fragment) {

    }

    @Override
    public void onPictureFragmentViewCreated(View v, Fragment fragment) {
        final RoundedImageView profilePhoto = (RoundedImageView)v.findViewById(R.id.pp_photo);
        final TextView homeText = (TextView)v.findViewById(R.id.pp_home_cityAndCountryCode_text);
        final TextView currenLocText = (TextView)v.findViewById(R.id.pp_current_location_text);
        person.fetchIfNeededInBackground(new GetCallback<PUser>() {
            @Override
            public void done(PUser user, ParseException e) {
                if(e == null){
                    profilePhoto.setParseFile(user.getProfilePicture());
                    profilePhoto.loadInBackground();
                    homeText.setText(user.getHometown()+", "+user.getCountryCode());
                    CountryCodes cc = new CountryCodes();
                    currenLocText.setText(user.getCurrentCity() + ", " + cc.getCode(user.getCurrentCountry()));
                }
            }
        });

//        profilePhoto.setParseFile(person.getProfilePicture());
//        profilePhoto.loadInBackground();
//        homeText.setText(person.getHometown()+", "+person.getCountryCode());
//        CountryCodes cc = new CountryCodes();
//        currenLocText.setText(person.getCurrentCity() + ", " + cc.getCode(person.getCurrentCountry()));
    }

    private class ScreenSlidePagerAdapter extends BaseFragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if(position == 0){
                ProfilePictureFragment frag = new ProfilePictureFragment();
                frag.setUp(PeopleProfileFragment.this);
//                Bundle args = new Bundle();
//                args.putInt(ARG_PROFILE_INDEX, position);
//                frag.setArguments(args);
                return frag;
            }else{
                ProfileAboutFragment frag = new ProfileAboutFragment();
                frag.setUp(PeopleProfileFragment.this);
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

}
//    private void createAlertDialog(String mesTitle, String message, String positiveBtnTitle, String negativeBtnTitle){
//        AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(getActivity());
//        mDialogBuilder.setTitle(mesTitle)
//                .setMessage(message)
//                .setPositiveButton(positiveBtnTitle, new AlertDialog.OnClickListener(){
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                        dialog.dismiss();
//                    }
//                })
//                .setNegativeButton(negativeBtnTitle, new AlertDialog.OnClickListener(){
//
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                        dialog.dismiss();
//                    }
//                }).create().show();
//    }