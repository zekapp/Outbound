package com.outbound.view;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.outbound.R;

/**
 * Created by zeki on 23/09/2014.
 */
public class MyFriendsListSubFragment extends ListFragment{
    private String mContentDescription = null;
    private View mRoot = null;
    private Listener mCallBackFragment = null;

    public interface Listener {
        public void onFragmentViewCreated(ListFragment fragment);
        public void onFragmentAttached(MyFriendsListSubFragment fragment);
        public void onFragmentDetached(MyFriendsListSubFragment fragment);
    }

    public void setUp(Fragment mCallerFragment){
        if(mCallerFragment instanceof Listener){
            mCallBackFragment = (Listener)mCallerFragment;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRoot = inflater.inflate(R.layout.my_friends_sub_fragment, container, false);
        if (mContentDescription != null) {
            mRoot.setContentDescription(mContentDescription);
        }
        return mRoot;
    }

    public void setContentDescription(String desc) {
        mContentDescription = desc;
        if (mRoot != null) {
            mRoot.setContentDescription(mContentDescription);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(mCallBackFragment!=null)
            mCallBackFragment.onFragmentViewCreated(this);
    }

//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        mCallBackFragment.onFragmentAttached(this);
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mCallBackFragment.onFragmentDetached(this);
//    }
}
