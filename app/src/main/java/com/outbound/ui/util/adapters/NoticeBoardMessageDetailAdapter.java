package com.outbound.ui.util.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.outbound.R;
import com.outbound.model.NoticeBoardMessage;
import com.outbound.model.PUser;
import com.outbound.ui.util.RoundedImageView;
import com.outbound.util.ResultCallback;
import com.parse.GetCallback;
import com.parse.ParseException;

import java.text.SimpleDateFormat;

import static com.outbound.util.LogUtils.*;

/**
 * Created by zeki on 2/10/2014.
 */
public class NoticeBoardMessageDetailAdapter extends ArrayAdapter<NoticeBoardMessage> {
    private static final String TAG = makeLogTag(NoticeBoardMessageDetailAdapter.class);
    private LayoutInflater inflater;
    private SimpleDateFormat formatter;
    private Context context;

    public interface OnNoticeBoardMsgItemClickedListener{
        void profilePictureClicked(PUser user);
    }
    private OnNoticeBoardMsgItemClickedListener listener = null;

    public void addOnNoticeBoardMsgItemClickedListener(Fragment frag){
        if( frag instanceof OnNoticeBoardMsgItemClickedListener)
            listener = (OnNoticeBoardMsgItemClickedListener)frag;
    }

    public NoticeBoardMessageDetailAdapter(Context context) {
        super(context, 0);
        this.context = context;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        formatter = new SimpleDateFormat("dd/MM/yyyy");
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if(true){
            view = inflater.inflate(R.layout.item_notice_board_message_activity,parent, false);
            holder = new ViewHolder();
            holder.photo = (RoundedImageView)view.findViewById(R.id.nb_user_photo);
            holder.nameSurname = (TextView)view.findViewById(R.id.nb_name_of_creater);
            holder.timeStamp = (TextView)view.findViewById(R.id.nb_create_date);
            holder.message = (TextView)view.findViewById(R.id.nb_message);
            holder.block = (TextView)view.findViewById(R.id.nb_block);
            holder.report = (TextView)view.findViewById(R.id.nb_report);
            view.setTag(holder);
        }else{
            holder = (ViewHolder)view.getTag();
        }

        final NoticeBoardMessage noticeBoardMessage = getItem(position);

        if(noticeBoardMessage != null){
            PUser.fetchTheSpesificUserFromId(noticeBoardMessage.getUserID(), new GetCallback<PUser>() {
                @Override
                public void done(final PUser pUser, ParseException e) {
                    if(e == null){
                        holder.photo.setParseFile(pUser.getProfilePicture());
                        holder.photo.loadInBackground();
                        holder.photo.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(listener != null){
                                    listener.profilePictureClicked(pUser);
                                }
                            }
                        });
                    }else
                    {
                        LOGD(TAG, "fetchTheSpesificUserFromId e: " + e.getMessage());
                    }
                }
            });

            holder.nameSurname.setText(noticeBoardMessage.getUserName());
//            holder.timeStamp.setHint(formatter.format(noticeBoardMessage.getDate()));
//            holder.timeStamp.setHint(noticeBoardMessage.getDate());
            holder.message.setHint(noticeBoardMessage.getText());
            holder.report.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openReportDialog(noticeBoardMessage);
                }
            });
            holder.block.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openBlockDialog(holder, noticeBoardMessage);
                }
            });
        }
        return view;
    }

    public static class ViewHolder {
        RoundedImageView photo;
        TextView nameSurname;
        TextView timeStamp; //hint
        TextView message;
        TextView block;
        TextView report;
    }

    private void openReportDialog(final NoticeBoardMessage message) {
        final AlertDialog.Builder ad = new AlertDialog.Builder(context);
        ad.setTitle("Do you want to report \""+ message.getUserName()+"\" ?");
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

    private void openBlockDialog( final ViewHolder holder, final NoticeBoardMessage noticeBoardMessage) {
        final AlertDialog.Builder ad = new AlertDialog.Builder(context);
        ad.setTitle("Do you want to block \""+ noticeBoardMessage.getUserName()+"\" ?");
        ad.setMessage("When you block this user, this user will never send you message and never join your events");
        ad.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                disableButtons(holder);
                showToastMessage(noticeBoardMessage.getUserName() + " blocking...");

                PUser.fetchTheSpesificUserFromId(noticeBoardMessage.getUserID(), new GetCallback<PUser>() {
                    @Override
                    public void done(PUser user, ParseException e) {
                        if(e == null){
                            PUser.blockThisUser(user, new ResultCallback() {
                                @Override
                                public void done(boolean res, ParseException e) {
                                    enableButtons(holder);
                                    if(e == null){
                                        remove(noticeBoardMessage);
                                        notifyDataSetChanged();
                                        showToastMessage(noticeBoardMessage.getUserName() + " blocked");
                                    }else
                                    {
                                        LOGD(TAG, "openBlockDialog - blockThisUser e: " + e.getMessage());
                                        showToastMessage("Network Error. Check your connection");
                                    }
                                }
                            });
                        }else
                        {
                            LOGD(TAG, "openBlockDialog - fetchTheSpesificUserFromId e: " + e.getMessage());
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
