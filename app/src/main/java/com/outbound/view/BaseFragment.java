package com.outbound.view;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by zeki on 15/09/2014.
 */
public abstract class BaseFragment extends Fragment {

    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    protected BaseFragmentCallbacks mCallbacks;

    protected int mBaseActivityFrameLayoutId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (BaseFragmentCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    /**
     * Callbacks interface that the activity using this fragment must implement.
     */
    public interface BaseFragmentCallbacks{
        void deployFragment(final  int itemId);

    }

    /**
     *
     * @param baseActivityFrameLayoutId  The android:id containing this fragment's UI.
     */
    protected void setUp(int baseActivityFrameLayoutId){
        mBaseActivityFrameLayoutId = baseActivityFrameLayoutId;
    }

}
