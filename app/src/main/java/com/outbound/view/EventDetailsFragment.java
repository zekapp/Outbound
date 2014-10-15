package com.outbound.view;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.outbound.R;
import com.outbound.model.PEvent;
import com.outbound.model.PUser;
import com.outbound.ui.util.HeaderGridView;
import com.outbound.ui.util.RoundedImageView;
import com.outbound.ui.util.SwipeRefreshLayout;
import com.outbound.ui.util.adapters.EventDetailsAdapter;
import com.outbound.util.Constants;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseImageView;
import com.parse.ParseObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static com.outbound.util.LogUtils.LOGD;
import static com.outbound.util.LogUtils.makeLogTag;

/**
 * Created by zeki on 29/09/2014.
 */
public class EventDetailsFragment extends BaseFragment {
    private static final String TAG = makeLogTag(EventDetailsFragment.class);

    private Object mEventDetails;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private EventDetailsAdapter adapter;
    private PEvent event;

    private SimpleDateFormat dateFormat;
    private SimpleDateFormat timeFormat;
    @Override
    protected void setUp( Object param1, Object param2) {
        super.setUp(param1, param2);
        LOGD(TAG, "param1 is null: " + (param1!=null?"No":"Yes"));
        if(param1 instanceof PEvent){
            event = (PEvent)param1;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dateFormat = new SimpleDateFormat("dd/MM/yy");
        timeFormat = new SimpleDateFormat("K:mm a");
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        setUpActionBar(activity);
    }
    private void setUpActionBar(Activity activity) {
        View viewActionBar = activity.getLayoutInflater().inflate(R.layout.custom_ab_event_details, null);
        TextView title = (TextView)viewActionBar.findViewById(R.id.action_bar_title);
        title.setText(getResources().getString(R.string.event_details_fragment_title));
        final RoundedImageView iconP = (RoundedImageView)viewActionBar.findViewById(R.id.ab_picture);

//        iconP.set
        ActionBar actionBar = activity.getActionBar();
        if(actionBar!=null) {
            actionBar.setCustomView(viewActionBar);
        }

        ImageView backIcon = (ImageView)viewActionBar.findViewById(R.id.ab_back_icon);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCallbacks!=null)
                    mCallbacks.backIconClicked();
            }
        });

        if(event != null){
            title.setText(event.getEventName());
            event.fetchEventCreater(new GetCallback<PUser>() {
                @Override
                public void done(PUser pUser, ParseException e) {
                    if(e == null)
                    {
                        iconP.setParseFile(pUser.getProfilePicture());
                        iconP.loadInBackground();
                        iconP.setBackground(null);
                    }
                }
            });
        }

//        final PUser user = PUser.getCurrentUser();
//        final PUser user = event.getCreatedBy();

        iconP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCallbacks !=null){
                    mCallbacks.deployFragment(Constants.PEOPLE_PROFILE_FRAG_ID,event.getCreatedBy(),null);
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        final View view = inflater.inflate(R.layout.event_details_fragment_layout, container, false);
        final View header = inflater.inflate(R.layout.event_details_header,container,false);

        if(event != null){
            setUpSwipeRefreshLayout(view);
            setUpHeaderField(header);
            setUpGridView(view, header);
        }

        return view;
    }

    private void setUpHeaderField(View header) {
        TextView date = (TextView)header.findViewById(R.id.ed_date);
        date.setText(dateFormat.format(event.getStartDate()) + " | " + timeFormat.format(event.getStartTime()));
        TextView placeText = (TextView)header.findViewById(R.id.ed_place);
        placeText.setText(event.getPlace());
        TextView cityCountryText = (TextView)header.findViewById(R.id.ed_city_country);
        cityCountryText.setHint(event.getCity()+", "+event.getCountry());
        TextView aboutText = (TextView)header.findViewById(R.id.ed_about);
        aboutText.setText(event.getDescription());
        TextView attendingCount = (TextView)header.findViewById(R.id.ed_attendingCount);
        attendingCount.setText(Integer.toString(event.getOutboundersGoing().size()) + " Outbounders attending");
        ImageView join = (ImageView)header.findViewById(R.id.ed_join);
        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    private void setUpGridView(View view, View header) {
        adapter = new EventDetailsAdapter(getActivity());

//        for (int i = 0; i < 50; i++) {
//            adapter.add(new Object());
//        }

//        PUser.getUserListAsDistanceOrder(event.getOutboundersGoing(), new FindCallback<PUser>() {
//            @Override
//            public void done(List<PUser> pUsers, ParseException e) {
//                for(PUser user : pUsers){
//                    adapter.add(user);
//                }
//            }
//        });



//        for(PUser user : event.getOutboundersGoing()){
//            adapter.add(user);
//        }

        final int participiantCount = event.getOutboundersGoing().size();
        for(PUser user : event.getOutboundersGoing()){
            user.fetchIfNeededInBackground(new GetCallback<PUser>() {
                List<PUser> userList = new ArrayList<PUser>();
                @Override
                public void done(PUser pUser, ParseException e) {
                    if(e == null){
                        adapter.add(pUser);
                        updateView();
//                        if(adapter.getCount() == participiantCount){
//                            adapter.filterAccourdinDistance();
//                            updateView();
//                        }
                    }
                }
            });

        }

        HeaderGridView userGridView = (HeaderGridView)view.findViewById(R.id.ed_grid_view);
        userGridView.addHeaderView(header, null, false);
        userGridView.setAdapter(adapter);
        userGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(mCallbacks != null)
                    mCallbacks.deployFragment(Constants.PEOPLE_PROFILE_FRAG_ID,parent.getAdapter().getItem(position),null);
            }
        });
    }

    private void updateView() {
        if(adapter != null)
            adapter.notifyDataSetChanged();
    }

    private void setUpSwipeRefreshLayout(View view) {
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.gw_swipe_refresh_layout);
        if (mSwipeRefreshLayout != null){
            mSwipeRefreshLayout.setColorScheme(
                    R.color.refresh_progress_1,
                    R.color.refresh_progress_2,
                    R.color.refresh_progress_3,
                    R.color.refresh_progress_4);

            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    //get the latest events
                }
            });
        }
    }
}
