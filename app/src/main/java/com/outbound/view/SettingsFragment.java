package com.outbound.view;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.outbound.R;
import com.outbound.ui.util.CountryDialog;
import com.outbound.ui.util.RoundedImageView;
import com.outbound.ui.util.TravellerTypeDialog;
import com.parse.ParseImageView;

import java.util.ArrayList;

/**
 * Created by zeki on 24/09/2014.
 */
public class SettingsFragment extends BaseFragment {

    private static final int SELECT_PROFILE_PICTURE = 1;
    private static final int SELECT_BACKGROUND_PICTURE = 2;

    private String selectedImagePath;
    private String filemanagerstring;
    private RoundedImageView mPhoto;
    private ParseImageView mBackgroundPhoto;

    @Override
    protected void setUp(Object param1, Object param2) {
        super.setUp(param1,param2);
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        setUpActionBar(activity);
    }
    private void setUpActionBar(Activity activity) {
        View viewActionBar = activity.getLayoutInflater().inflate(R.layout.custom_ab_back_button, null);
        TextView title = (TextView)viewActionBar.findViewById(R.id.action_bar_title);
        title.setText(getResources().getString(R.string.settings_fragment_title));
        ImageView icon = (ImageView)viewActionBar.findViewById(R.id.ab_icon_1);
        icon.setImageResource(R.drawable.action_save);
        ActionBar actionBar = activity.getActionBar();
        if(actionBar!=null) {
            actionBar.setCustomView(viewActionBar);
        }

        ImageView backIcon = (ImageView)viewActionBar.findViewById(R.id.ab_back_icon);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallbacks.backIconClicked();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        final View view = inflater.inflate(R.layout.settings_layout, container, false);

        setUpPhoto(view);
        setBackgroundPic(view);
        setUpNationality(view);
        setUpTravellerType(view);
        return view;
    }

    private void setUpTravellerType(View view) {
        Button btn = (Button)view.findViewById(R.id.s_travellerType_button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTravellerDialo(v);
            }
        });
    }

    private void openTravellerDialo(View v) {
//        TravellerTypeDialog ttd = new TravellerTypeDialog(getActivity());

        String[] travellerTypeList = getActivity().getResources().getStringArray(R.array.traveller_type);
        boolean bl[] = new boolean[travellerTypeList.length];
        ArrayList<Integer> selList=new ArrayList();

        final AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
        ad.setTitle("Select Traveller Type")
            .setMultiChoiceItems(travellerTypeList, bl, new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                }
            }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).show();
    }

    private void setUpNationality(View view) {
        Button nationalityButton = (Button)view.findViewById(R.id.s_nationality_button);

        nationalityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCountryDialog(v);
            }
        });
    }

    private void openCountryDialog(final View v) {
        CountryDialog cd = new CountryDialog(getActivity());
        cd.addCountryDialogListener(new CountryDialog.CountryDialogListener() {
            @Override
            public void onCountrySelected(String countryName, String countryCode) {
                ((Button)v).setHint(countryName);
            }
        });
        cd.show();


    }

    private void setBackgroundPic(View view) {
        mBackgroundPhoto = (ParseImageView) view.findViewById(R.id.s_photo_background);
        (view.findViewById(R.id.s_change_background)).
                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openGallery(SELECT_BACKGROUND_PICTURE);
                    }
                });
    }

    private void setUpPhoto(View view) {
        mPhoto = (RoundedImageView) view.findViewById(R.id.s_photo);
        mPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery(SELECT_PROFILE_PICTURE);
            }
        });
    }

    private void openGallery(int openPurpose){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,
                "Select Picture"), openPurpose);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == -1) {
            if (requestCode == SELECT_PROFILE_PICTURE) {
                Uri selectedImageUri = data.getData();
                filemanagerstring = selectedImageUri.getPath();
                selectedImagePath = getPath(selectedImageUri);
                if(selectedImagePath!=null){
                    if(mPhoto != null){
//                        Bitmap resized = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(selectedImagePath), 191, 191, true);
////                        Bitmap conv_bm = getRoundedRectBitmap(resized, 191);
//                        Bitmap conv_bm = getRoundedCornerBitmap(resized, 40);
//                        mPhoto.setImageBitmap(conv_bm);

                        mPhoto.setImageBitmap(BitmapFactory.decodeFile(selectedImagePath));
                    }
                }

            }else if(requestCode == SELECT_BACKGROUND_PICTURE){
                Uri selectedImageUri = data.getData();
                filemanagerstring = selectedImageUri.getPath();
                selectedImagePath = getPath(selectedImageUri);
                if(selectedImagePath!=null){
                    if(mBackgroundPhoto != null){

                        mBackgroundPhoto.setImageBitmap(BitmapFactory.decodeFile(selectedImagePath));
                    }
                }
            }
        }
    }
    public String getPath(Uri uri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = getActivity().getContentResolver().query(uri, proj, null, null, null);
        if(cursor!=null)
        {

            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        else return null;

    }

//    public static Bitmap getRoundedRectBitmap(Bitmap bitmap, int pixels) {
//        Bitmap result = null;
//        try {
//            result = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888);
//            Canvas canvas = new Canvas(result);
//
//            int color = 0xff424242;
//            Paint paint = new Paint();
//            Rect rect = new Rect(0, 0, 200, 200);
//
//            paint.setAntiAlias(true);
//            canvas.drawARGB(0, 0, 0, 0);
//            paint.setColor(color);
//            canvas.drawCircle(50, 50, 50, paint);
//            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
//            canvas.drawBitmap(bitmap, rect, rect, paint);
//
//        } catch (NullPointerException e) {
//        } catch (OutOfMemoryError o) {
//        }
//        return result;
//    }


//    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
//        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
//                .getHeight(), Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(output);
//
//        final int color = 0xff424242;
//        final Paint paint = new Paint();
//        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
//        final RectF rectF = new RectF(rect);
//        final float roundPx = pixels;
//
//        paint.setAntiAlias(true);
//        canvas.drawARGB(0, 0, 0, 0);
//        paint.setColor(color);
//        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
//
//        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
//        canvas.drawBitmap(bitmap, rect, rect, paint);
//
//        return output;
//    }
}
