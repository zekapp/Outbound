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
import com.outbound.model.PUser;
import com.outbound.model.WifiSpot;
import com.outbound.ui.util.SwipeRefreshLayout;
import com.outbound.ui.util.adapters.WifiSpotAdapter;
import com.outbound.util.Constants;
import com.outbound.util.FoursquareService;
import com.outbound.util.LocationUtils;
import com.outbound.util.PlacesCallback;

import java.util.List;

/**
 * Created by zeki on 29/10/2014.
 */
public class WifiSpotsFoursquareFragment extends BaseFragment {

    private ListView mListView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private WifiSpotAdapter mAdapter;

//    private EasyFoursquareAsync async;

    @Override
    protected void setUp(Object param1, Object param2) {
        super.setUp(param1, param2);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if(mCallbacks != null){
            mCallbacks.hideTabbar();
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
        if(mCallbacks != null){
            mCallbacks.showTabbar();
        }
    }

    private void setUpActionBar(Activity activity) {
        View viewActionBar = activity.getLayoutInflater().inflate(R.layout.custom_ab_back_button, null);
        TextView title = (TextView)viewActionBar.findViewById(R.id.action_bar_title);
        title.setText(getResources().getString(R.string.wifi_foursquare_fragment_title));
        ImageView icon = (ImageView)viewActionBar.findViewById(R.id.ab_icon_1);
        icon.setImageResource(R.drawable.action_add);
        icon.setAlpha(0);
        icon.setBackground(null);
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        final View view = inflater.inflate(R.layout.list_view_wifi_layout, container, false);
        setUpActionBar(getActivity());

        mListView = (ListView) view.findViewById(R.id.list_view);
        setUpListView();
        setUpSwipeRefreshLayout(view);
        return view;
    }

    private void setUpListView() {
        if(mAdapter == null){
            if (mSwipeRefreshLayout != null) {
                    mSwipeRefreshLayout.setRefreshing(true);
            }
            mAdapter =  new WifiSpotAdapter(getActivity(), false);
            mAdapter.setReferenceGeoPoint(PUser.getCurrentUser().getCurrentLocation());
            fetchPlaceNearMe();
        }
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(mCallbacks != null)
                    mCallbacks.deployFragment(Constants.WIFI_SPOT_ADD_FRAG_ID, parent.getAdapter().getItem(position),null);
            }
        });

    }

    private void fetchPlaceNearMe() {
        FoursquareService.fetchPlacesNearMe(PUser.getCurrentUser().getCurrentLocation(), new PlacesCallback<WifiSpot>() {
            @Override
            public void done(List<WifiSpot> wifiSpots, Exception e) {
                mSwipeRefreshLayout.setRefreshing(false);
                if(e == null){
                    LocationUtils.orderWifiSpotsAccordingDistance(wifiSpots);
                    mAdapter.addAll(wifiSpots);
                    updateView();
                }else
                {
                    showToastMessage("No wifi spots found near you");
                }
            }
        });
    }

    private void updateView() {
        if(mAdapter != null)
            mAdapter.notifyDataSetChanged();
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
                    //get the latest wifi spots
                    if(mAdapter != null)
                        mAdapter.clear();

                    fetchPlaceNearMe();
                }
            });
        }

    }
}
