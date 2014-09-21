package com.outbound.view;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import android.support.v4.app.Fragment;

import com.outbound.R;
import com.outbound.ui.util.BaseFragmentStatePagerAdapter;
import com.outbound.ui.util.ZoomOutPageTransformer;

/**
 * Created by zeki on 2/09/2014.
 */
public class ProfileFragment extends BaseFragment {

    private FrameLayout mFrameLayout;
    private PagerAdapter mPagerAdapter;
    private ViewPager mViewPager;

    View dots[] = new View[2];
    @Override
    protected void setUp(int baseActivityFrameLayoutId) {
        super.setUp(baseActivityFrameLayoutId);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        View viewActionBar = activity.getLayoutInflater().inflate(R.layout.custom_ab_profile, null);
        TextView title = (TextView)viewActionBar.findViewById(R.id.action_bar_title);
        title.setText(getResources().getString(R.string.profile_fragment_title));
        ActionBar actionBar = activity.getActionBar();
        if(actionBar!=null) {
            actionBar.setCustomView(viewActionBar);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        final View view = inflater.inflate(R.layout.profile_fragment, container, false);

        dots[0] = view.findViewById(R.id.profile_left_dot);
        dots[1] = view.findViewById(R.id.profile_right_dot);

        mViewPager = (ViewPager) view.findViewById(R.id.profil_pager);
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
        return view;
    }

    private class ScreenSlidePagerAdapter extends BaseFragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {


            return position==0?new ProfilePictureFragment():new ProfileAboutFragment();
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
