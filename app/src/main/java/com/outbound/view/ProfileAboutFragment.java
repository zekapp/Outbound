package com.outbound.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.outbound.R;

/**
 * Created by zeki on 19/09/2014.
 */
public class ProfileAboutFragment extends Fragment {

    private Listener mCallBackFragment = null;

    public interface Listener {
        public void onAboutFragmentViewCreated(View root, Fragment fragment);
    }

    public void setUp(Fragment mCallerFragment){
        if(mCallerFragment instanceof Listener){
            mCallBackFragment = (Listener)mCallerFragment;
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        final View view = inflater.inflate(R.layout.profile_about_fragment, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        super.onViewCreated(view, savedInstanceState);
        if(mCallBackFragment!=null)
            mCallBackFragment.onAboutFragmentViewCreated(view,this);
    }
}
