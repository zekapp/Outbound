package com.outbound.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.outbound.R;

/**
 * Created by zeki on 2/09/2014.
 */
public class ProfileFragment extends BaseFragment {

    private FrameLayout mFrameLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        final View view = inflater.inflate(R.layout.profile_fragment, container, false);

        return view;
    }

    @Override
    protected void setUp(int baseActivityFrameLayoutId) {
        super.setUp(baseActivityFrameLayoutId);
    }
}
