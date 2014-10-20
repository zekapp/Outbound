package com.outbound.ui.util.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.outbound.R;
import com.outbound.model.NoticeBoardMessage;
import com.outbound.model.PNoticeBoard;
import com.outbound.model.PUser;
import com.outbound.ui.util.RoundedImageView;
import com.outbound.util.ResultCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseImageView;

import java.text.SimpleDateFormat;
import java.util.List;

import static com.outbound.util.LogUtils.*;

/**
 * Created by zeki on 1/10/2014.
 */
public class NoticeBoardMessageAdapter extends ArrayAdapter<PNoticeBoard>{
    private static final String TAG = makeLogTag(NoticeBoardMessageAdapter.class);
    private LayoutInflater inflater;
    private SimpleDateFormat formatter;
    private Context context;

    public interface OnNoticeBoardItemClickedListener{
        void profilePictureClicked(PUser user);
    }

    private OnNoticeBoardItemClickedListener listener = null;
    public void addOnItemclickedListener(Fragment frag){
        if( frag instanceof OnNoticeBoardItemClickedListener)
            listener = (OnNoticeBoardItemClickedListener)frag;
    }

    public NoticeBoardMessageAdapter(Context context){
        super(context, 0);

        this.context = context;

        inflater = (LayoutInflater)getContext().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);

        formatter = new SimpleDateFormat("dd/MM/yyyy");
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        final ViewHolder holder;

        if(view == null || view.getTag() == null){
            view = inflater.inflate(R.layout.item_notice_board_list, parent, false);
            holder = new ViewHolder();
            holder.photo = (RoundedImageView)view.findViewById(R.id.nb_user_photo);
            holder.title = (TextView)view.findViewById(R.id.nb_title);
            holder.nameSurname = (TextView)view.findViewById(R.id.nb_name_of_creater);
            holder.postCount = (TextView)view.findViewById(R.id.nb_post_count);
            holder.createDate = (TextView)view.findViewById(R.id.nb_create_date);
            holder.message = (TextView)view.findViewById(R.id.nb_message);
            holder.report = (TextView)view.findViewById(R.id.nb_report);
            holder.block = (TextView)view.findViewById(R.id.nb_block);
            view.setTag(holder);
        }
        else
        {
            holder = (ViewHolder)view.getTag();
        }

        final PNoticeBoard post = getItem(position);
        holder.photo.setParseFile(post.getCreatedBy().getProfilePicture());
        holder.photo.loadInBackground();
        holder.title.setText(post.getNoticeboardTitle());
        holder.postCount.setText("(" + Integer.toString(post.getMessagesCount()) + ")");
        holder.createDate.setHint(formatter.format(post.getCreatedAt()));
        holder.nameSurname.setText(post.getCreatedBy().getUserName());
        holder.report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openReportDialog(post.getCreatedBy());
            }
        });
        holder.block.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBlockDialog(post.getCreatedBy(), holder, post);
            }
        });
        holder.photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null)
                    listener.profilePictureClicked(post.getCreatedBy());
            }
        });

        holder.message.setHint(post.getDescription());

        return view;
    }

    private void openReportDialog(final PUser user) {
        final AlertDialog.Builder ad = new AlertDialog.Builder(context);
        ad.setTitle("Do you want to report \""+ user.getUserName()+"\" ?");
        ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        ad.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        ad.show();
    }

    private void openBlockDialog(final PUser user , final ViewHolder holder, final PNoticeBoard post) {
        final AlertDialog.Builder ad = new AlertDialog.Builder(context);
        ad.setTitle("Do you want to block \""+ user.getUserName()+"\" ?");
        ad.setMessage("When you block this user, this user will never send you message and never join your events");
        ad.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                disableButtons(holder);
                showToastMessage(user.getUserName() + " blocking...");
                PUser.blockThisUser(user, new ResultCallback() {
                    @Override
                    public void done(boolean res, ParseException e) {
                        enableButtons(holder);
                        if(e == null){
                            remove(post);
                            notifyDataSetChanged();
                            showToastMessage(user.getUserName() + " blocked");
                        }else
                        {
                            LOGD(TAG, "openBlockDialog - blockThisUser e: " + e.getMessage());
                            showToastMessage("Network Error. Check your connection");
                        }
                    }
                });
                dialog.dismiss();
            }
        });
        ad.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        ad.show();
    }

    public static class ViewHolder{
        RoundedImageView photo;
        TextView title;
        TextView nameSurname;
        TextView postCount;
        TextView createDate;
        TextView message;
        TextView report;
        TextView block;
    }


    private void showToastMessage(String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    private void enableButtons(ViewHolder holder) {
        holder.report.setEnabled(true);
        holder.block.setEnabled(true);
        holder.report.setAlpha((float) 1);
        holder.block.setAlpha((float) 1);
    }

    private void disableButtons(ViewHolder holder) {
        holder.report.setEnabled(false);
        holder.block.setEnabled(false);
        holder.report.setAlpha((float) 0.2);
        holder.block.setAlpha((float) 0.2);
    }
}
