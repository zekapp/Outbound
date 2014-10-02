package com.outbound.view;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.outbound.R;

/**
 * Created by zeki on 30/09/2014.
 */
public class SearchPeopleDetailFragment extends BaseFragment {

    @Override
    protected void setUp(Object param1, Object param2) {
        super.setUp(param1, param2);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        setUpActionBar(activity);
    }

    private void setUpActionBar(Activity activity) {
        View viewActionBar = activity.getLayoutInflater().inflate(R.layout.custom_ab_back_button, null);
        TextView title = (TextView)viewActionBar.findViewById(R.id.action_bar_title);
        title.setText(getResources().getString(R.string.search_people_detail_fragment_title));
        ImageView icon1 = (ImageView)viewActionBar.findViewById(R.id.ab_icon_1);
        icon1.setImageResource(R.drawable.action_save);
        ImageView icon2 = (ImageView)viewActionBar.findViewById(R.id.ab_icon_2);
        icon2.setImageResource(R.drawable.action_reset);
        icon2.setVisibility(View.VISIBLE);

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

        icon1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //save search properties
            }
        });

        icon2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start the search process
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        final View view = inflater.inflate(R.layout.search_people_detail_layout, container, false);
        setUpAgeRange(view);
        setUpTravellerType(view);
        setUpSexualPreference(view);
        return view;
    }

    private void setUpAgeRange(View view) {

    }

    private void setUpTravellerType(View view) {

    }

    private void setUpSexualPreference(View view) {

    }
}
