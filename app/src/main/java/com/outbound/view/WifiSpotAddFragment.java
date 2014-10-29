package com.outbound.view;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.outbound.R;
import com.outbound.model.PUser;
import com.outbound.model.PWifiSpot;
import com.outbound.model.WifiSpot;
import com.outbound.util.Constants;
import com.parse.ParseException;
import com.parse.SaveCallback;

/**
 * Created by zeki on 29/10/2014.
 */
public class WifiSpotAddFragment extends BaseFragment {
    private RadioButton m_one, m_two, m_three;
    private WifiSpot wifiSpot;
    @Override
    protected void setUp(Object param1, Object param2) {
        super.setUp(param1, param2);
        if(param1 instanceof WifiSpot) {
            wifiSpot = (WifiSpot) param1;
            wifiSpot.setWifiType("free");
        }
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
        title.setText(getResources().getString(R.string.wifi_spot_add_fragment_title));
        ImageView icon = (ImageView)viewActionBar.findViewById(R.id.ab_icon_1);
        icon.setImageResource(R.drawable.action_submit);
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
                attemptTosaveWifi();
            }
        });
    }

    private void attemptTosaveWifi() {
        startProgress("Wifi spot saving to database...");
        PWifiSpot pWifiSpot = new PWifiSpot();
        pWifiSpot.setWifiType(wifiSpot.getWifiType());
        pWifiSpot.setWifiName(wifiSpot.getWifiName());
        pWifiSpot.setWifiAddress(wifiSpot.getWifiAddress());
        pWifiSpot.setWifiLocation(wifiSpot.getWifiLocation());
        pWifiSpot.setIsUserCreated(true);
        pWifiSpot.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                stopProgress();
                if(e == null){
                    if(mCallbacks != null)
                        mCallbacks.deployFragment(Constants.WIFI_SPOTS_FOURSQUARE_FRAG_ID,null,null);

                    showToastMessage("Wifi spot saved to database");
                }else
                {
                    showToastMessage("Network error. Check your connection");
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        final View view = inflater.inflate(R.layout.wifi_add_layout, container, false);
        setUpActionBar(getActivity());

        m_one = (RadioButton) view.findViewById(R.id.free_radio);
        m_two = (RadioButton) view.findViewById(R.id.paid_radio);
        m_three = (RadioButton) view.findViewById(R.id.purchase_radio);

        setUpWifiLayout(view);
        setUpRadioButtons();

        return view;
    }

    private void setUpWifiLayout(View view) {
        TextView name = (TextView)view.findViewById(R.id.wifi_name);
        TextView address = (TextView)view.findViewById(R.id.wifi_address);
        TextView distanceText = (TextView)view.findViewById(R.id.wifi_distance);

        name.setText(wifiSpot.getWifiName());
        address.setText(wifiSpot.getWifiAddress());
        double distance = wifiSpot.getWifiLocation().distanceInKilometersTo(PUser.getCurrentUser().getCurrentLocation());
        String str = String.format("%.2fkm", distance);
        distanceText.setText(str);
    }

    private void setUpRadioButtons() {
        m_one.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                m_one.setChecked(true);
                m_two.setChecked(false);
                m_three.setChecked(false);
                wifiSpot.setWifiType("free");
            }
        });

        m_two.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                m_one.setChecked(false);
                m_two.setChecked(true);
                m_three.setChecked(false);
                wifiSpot.setWifiType("paid");
            }
        });

        m_three.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                m_one.setChecked(false);
                m_two.setChecked(false);
                m_three.setChecked(true);
                wifiSpot.setWifiType("withPurchase");
            }
        });
    }
}
