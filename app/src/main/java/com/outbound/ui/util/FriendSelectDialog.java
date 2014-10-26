package com.outbound.ui.util;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;

import com.outbound.R;
import com.outbound.model.PFriendRequest;
import com.outbound.model.PUser;
import com.outbound.ui.util.adapters.CityAdapter;
import com.outbound.ui.util.adapters.MyFriendsAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;

import java.util.List;

import static com.outbound.util.LogUtils.LOGD;
import static com.outbound.util.LogUtils.makeLogTag;

/**
 * Created by zeki on 26/10/2014.
 */
public class FriendSelectDialog extends Dialog{
    private static final String TAG = makeLogTag(FriendSelectDialog.class);

    private MyFriendsAdapter mAdapter = null;
    private ListView mListView;
    private Dialog progressDialog;
    private Context context;
    private FriendSelectDialogListener listener;

    public interface FriendSelectDialogListener{
        void onFriendSelected(PUser selectedUser);
    }

    public void addFriendSelectDialogListener(FriendSelectDialogListener listener){
        this.listener = listener;
    }
    public FriendSelectDialog(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.friends_dialog);

        mListView = (ListView)findViewById(R.id.list_view_friends);

        setUpListView();
    }

    private void setUpListView() {
        if(mAdapter == null)
            mAdapter  = new MyFriendsAdapter(context,false);

        feedAdapter();
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(listener != null)
                    listener.onFriendSelected((PUser)parent.getAdapter().getItem(position));

                dismiss();
            }
        });
    }

    private void feedAdapter() {
        startProgress();
        PFriendRequest.findFriends(PUser.getCurrentUser(), new FindCallback<PUser>() {
            @Override
            public void done(List<PUser> pUsers, ParseException e) {
                stopProgress();
                if (e == null) {
                    if (mAdapter != null) {
                        if (pUsers.size() > mAdapter.getCount()) {
                            LOGD(TAG, "updateFriendListAdapter: size: " + pUsers.size());
                            mAdapter.clear();
                            mAdapter.addAll(pUsers);
                            updateView();
                        }
                    } else {
                        LOGD(TAG, "mAdapter is NULL");
                    }
                } else {
                    LOGD(TAG, "updateFriendListAdapter e: " + e.getMessage());
                }
            }
        });

    }

    private void updateView() {
        if(mAdapter != null)
            mAdapter.notifyDataSetChanged();
    }

    private void startProgress() {
        progressDialog = ProgressDialog.show(
                getContext(), "", "Your friends fetching...", true);
    }

    private void stopProgress() {
        progressDialog.dismiss();
    }
}
