package com.outbound.view;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.outbound.ui.util.SwipeRefreshLayout;

import java.lang.reflect.Field;


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
        
        // registrer the view which is dissepear when list view scolling
        void registerHideableHeaderView(View view);

        // whic listview will be lissened
        void enableActionBarAutoHide(final ListView listView);

        // after unvisible of the up views swipelayout top location
        void registerSwipeRefreshProgressBarAsTop(SwipeRefreshLayout swipeRefreshLayout, int progressBarTopWhenActionBarShown);

        // similar action like back button
        void backIconClicked();
    }

    /**
     *
     * @param baseActivityFrameLayoutId  The android:id containing this fragment's UI.
     */
    protected void setUp(int baseActivityFrameLayoutId){
        mBaseActivityFrameLayoutId = baseActivityFrameLayoutId;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
