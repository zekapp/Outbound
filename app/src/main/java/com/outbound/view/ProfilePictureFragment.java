package com.outbound.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.outbound.R;

/**
 * Created by zeki on 19/09/2014.
 */
public class ProfilePictureFragment extends Fragment {

    private Listener mCallBackFragment = null;

    public interface Listener {
        public void onPictureFragmentViewCreated(View root, Fragment fragment);
    }

    public void setUp(Fragment mCallerFragment){
        if(mCallerFragment instanceof Listener){
            mCallBackFragment = (Listener)mCallerFragment;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        final View view = inflater.inflate(R.layout.profile_picture_fragment, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        super.onViewCreated(view, savedInstanceState);
        if(mCallBackFragment!=null)
            mCallBackFragment.onPictureFragmentViewCreated(view,this);
    }
}
