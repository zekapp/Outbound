package com.outbound.view;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.outbound.R;
import com.outbound.model.PFriendRequest;
import com.outbound.model.PUser;
import com.outbound.ui.util.SwipeRefreshLayout;
import com.outbound.ui.util.adapters.PeopleFriendAdapter;
import com.outbound.util.Constants;
import com.parse.FindCallback;
import com.parse.ParseException;

import java.util.List;

import static com.outbound.util.LogUtils.*;

/**
 * Created by zeki on 1/10/2014.
 */
public class PeopleProfileFriendFragment extends BaseFragment {
    private static final String TAG = makeLogTag(BaseFragment.class);

    private PeopleFriendAdapter adapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private PUser user;
    @Override
    protected void setUp(Object param1, Object param2) {
        super.setUp(param1,param2);

        if(param1 instanceof PUser)
            user = (PUser)param1; // you dont need to call fetchIfNeededInBackground all field is full
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        setUpActionBar(activity);
    }

    private void setUpActionBar(Activity activity) {
        View viewActionBar = activity.getLayoutInflater().inflate(R.layout.custom_ab_back_button, null);
        TextView title = (TextView)viewActionBar.findViewById(R.id.action_bar_title);
        title.setText(getResources().getString(R.string.people_friend_fragment_title));
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
        final View view = inflater.inflate(R.layout.list_view_layout, container, false);
        setUpSwipeRefreshLayout(view);
        setUpPeopleFriendListView(view);
        return view;
    }

    private void setUpPeopleFriendListView(View v) {
        adapter = new PeopleFriendAdapter(getActivity());

//        for (int i = 0; i < 50; i++) {
//            adapter.add(new Object());
//        }
        if(user != null){
            PFriendRequest.findFriends(user, new FindCallback<PUser>() {
                @Override
                public void done(List<PUser> pUsers, ParseException e) {
                    if(e == null){
                        for (PUser pUser : pUsers){
                            if(adapter != null){
                                adapter.add(pUser);
                            }
                        }
                        updateView();
                    }
                }
            });
        }else
        {
            LOGE(TAG, "user is null....");
        }

        ListView list = (ListView) v.findViewById(R.id.list_view);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(mCallbacks != null)
                    mCallbacks.deployFragment(Constants.PEOPLE_PROFILE_FRAG_ID,parent.getAdapter().getItem(position),null);
            }
        });
        updateView();
    }

    private void updateView() {
        if(adapter !=null)
            adapter.notifyDataSetChanged();
    }

    private void setUpSwipeRefreshLayout(View view) {
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.lw_swipe_refresh_layout);
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

        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setEnabled(false);
        }
    }
}
