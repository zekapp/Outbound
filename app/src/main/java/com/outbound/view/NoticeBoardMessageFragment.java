package com.outbound.view;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.outbound.R;
import com.outbound.model.NoticeBoardMessage;
import com.outbound.model.PNoticeBoard;
import com.outbound.model.PUser;
import com.outbound.ui.util.adapters.NoticeBoardMsgListViewAdapter;
import com.outbound.util.Constants;

import java.util.List;

/**
 * Created by zeki on 19/10/2014.
 */
public class NoticeBoardMessageFragment extends BaseFragment implements NoticeBoardMsgListViewAdapter.OnNoticeBoardMsgItemClickedListener {

    private TextView actionBarTitle;
    private PNoticeBoard post;

    private ListView mListView;
    private NoticeBoardMsgListViewAdapter mAdapter ;

    @Override
    protected void setUp(Object param1, Object param2) {
        super.setUp(param1, param2);

        if(param1 instanceof PNoticeBoard)
            post = (PNoticeBoard)param1;
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
        actionBarTitle = (TextView)viewActionBar.findViewById(R.id.action_bar_title);
        actionBarTitle.setText(post.getNoticeboardTitle());
        ImageView icon = (ImageView)viewActionBar.findViewById(R.id.ab_icon_1);
        icon.setImageResource(R.drawable.icon_active);
//        icon.setVisibility(View.GONE);

        ImageView backIcon = (ImageView)viewActionBar.findViewById(R.id.ab_back_icon);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCallbacks !=null)
                    mCallbacks.backIconClicked();
            }
        });


        ActionBar actionBar = activity.getActionBar();
        if(actionBar!=null) {
            actionBar.setCustomView(viewActionBar);
        }
    }

    private  View rootView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setUpActionBar(getActivity());
        rootView= inflater.inflate(R.layout.notice_board_message_detail, container, false);

        setUpListView();
        return rootView;
    }

    private void setUpListView() {
        mListView = (ListView)rootView.findViewById(R.id.nb_message_list);
        mListView.setDivider(new ColorDrawable(this.getResources().getColor(R.color.gray)));
        mListView.setDividerHeight(1);
        mAdapter = new NoticeBoardMsgListViewAdapter(getActivity());
        mAdapter.addOnNoticeBoardMsgItemClickedListener(this);
        feedTheAdapter();
        mListView.setAdapter(mAdapter);
    }

    private void feedTheAdapter() {
        List<NoticeBoardMessage> messageList = post.getAllMessages();

        if(mAdapter != null && messageList != null){
            mAdapter.clear();
            mAdapter.addAll(messageList);
            updateView();
        }
    }

    private void updateView() {
        if(mAdapter != null)
            mAdapter.notifyDataSetChanged();
    }

    @Override
    public void profilePictureClicked(PUser user) {
        if(mCallbacks != null)
            mCallbacks.deployFragment(Constants.PEOPLE_PROFILE_FRAG_ID, user, null);
    }
}
