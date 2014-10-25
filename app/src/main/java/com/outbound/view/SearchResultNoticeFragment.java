package com.outbound.view;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.outbound.R;
import com.outbound.model.PNoticeBoard;
import com.outbound.ui.util.adapters.NoticeBoardMessageAdapter;
import com.outbound.util.Constants;

import java.util.List;

/**
 * Created by zeki on 21/10/2014.
 */
public class SearchResultNoticeFragment extends BaseFragment {

    private List<PNoticeBoard> noticeList = null;
    private NoticeBoardMessageAdapter adapter;

    @Override
    protected void setUp(Object param1, Object param2) {
        super.setUp(param1, param2);
        if(param1 != null) {
            noticeList = (List)param1;
        }
    }

    private void setUpActionBar(Activity activity) {
        View viewActionBar = activity.getLayoutInflater().inflate(R.layout.custom_ab_back_button, null);
        TextView title = (TextView)viewActionBar.findViewById(R.id.action_bar_title);
        title.setText(getResources().getString(R.string.search_result_noticeboard_fragment_title));
        ImageView iconSearch = (ImageView)viewActionBar.findViewById(R.id.ab_icon_1);
        iconSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallbacks.deployFragment(Constants.NOTICE_BOARD_SEARCH_MESSAGE_FRAG_ID, null, null);
            }
        });
        ActionBar actionBar = activity.getActionBar();
        if(actionBar!=null) {
            actionBar.setCustomView(viewActionBar);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        setUpActionBar(activity);
    }

    private View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.search_result_noticeboard_fragment_layout, container, false);
        setUpListview();
        return view;
    }

    private void setUpListview() {
        if(adapter == null)
            adapter = new NoticeBoardMessageAdapter(getActivity());

        adapter.clear();
        if(noticeList!= null)
            adapter.addAll(noticeList);

        ListView listView = (ListView)view.findViewById(R.id.nb_result_list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(mCallbacks != null)
                    mCallbacks.deployFragment(Constants.NOTICE_BOARD_POST_DETAIL_FRAG_ID, parent.getAdapter().getItem(position),null);
            }
        });

    }
}
