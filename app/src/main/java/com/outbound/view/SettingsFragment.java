package com.outbound.view;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.outbound.R;
import com.outbound.model.PUser;
import com.outbound.ui.util.CityDialog;
import com.outbound.ui.util.CountryDialog;
import com.outbound.ui.util.RoundedImageView;
import com.outbound.ui.util.TravellerTypeDialog;
import com.outbound.util.ConnectionDetector;
import com.outbound.util.Constants;
import com.outbound.util.CountryCodes;
import com.outbound.util.LogUtils;
import com.parse.ParseException;
import com.parse.ParseImageView;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.outbound.util.LogUtils.*;

/**
 * Created by zeki on 24/09/2014.
 */
public class SettingsFragment extends BaseFragment {
    private static final String TAG = makeLogTag(SettingsFragment.class);

    private static final int SELECT_PROFILE_PICTURE = 1;
    private static final int SELECT_BACKGROUND_PICTURE = 2;

    private RadioButton viewedByAll;
    private RadioButton viewedByMale;
    private RadioButton viewedByFemale;

    private EditText aboutUser;
    private Button homeTown;
    private String selectedCountry = null;

    private Button countrySelectionButton;
    private EditText mEmailView;
    private EditText mUserName;
    private EditText mPasswordView;

    private boolean profilePhotoAttached = false;
    private String selectedCity = null;
    private String selectedCountryCode;

    private String selectedImagePath;
    private String filemanagerstring;
    private RoundedImageView mPhoto;
    private ParseImageView mBackgroundPhoto;

    //Traveller Type
    private String[] travellerTypeList;
    private boolean[] trvTypeBooleanList;
    private ArrayList<Integer> travSelList = new ArrayList<Integer>();

    //Sexual Preferences
    private String[] prefTypeList;
    private boolean[] prefTypeBooleanList;
    private ArrayList<Integer> prefSelList = new ArrayList<Integer>();


    //PUser
    private PUser user = PUser.getCurrentUser();


    ConnectionDetector cd;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cd = new ConnectionDetector(getActivity());
    }

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

        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attempToUpdate();

            }
        });
    }

    private void attempToUpdate() {
        if(cd.isConnectingToInternet())
            updateUser();
        else
            showAlertDialog("No Internet Connection",
                    "You don't have internet connection.", false);
    }

    private void updateUser() {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mUserName.setError(null);
        countrySelectionButton.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String username = mUserName.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        //check for name is filled
        if(TextUtils.isEmpty(username)){
            mUserName.setError(getString(R.string.error_field_required));
            focusView = mUserName;
            cancel = true;
        }

        if(selectedCountry == null){
            countrySelectionButton.setError(getString(R.string.error_invalid_country));
            focusView = countrySelectionButton;
            cancel = true;
        }

        if(!profilePhotoAttached)
        {
            warnTheUser();
            return;
        }

        if(cancel){
            focusView.requestFocus();
        }else{
            try{
                update(username,email,password);
            }catch (Exception e){
                LOGD(TAG, "attempToSingUp() : " + e.getMessage());
            }
        }

    }

    private void update(String name, String email, String password) {

        startProgress("User Profile Updating...");

        user.setUserName(name);
        user.setEmail(email);
        if(password!=null)
            user.setPassword(password);
        user.setNationality(selectedCountry);
        user.setCountryCode(new CountryCodes().getCode(selectedCountry).trim());
        if(selectedCity != null )
            user.setHometown(selectedCity);
        if(travSelList.size() > 0)
            user.setTravelerType(getTravTypeArray(travSelList));
        if(aboutUser.getText().toString() != null )
            user.setShortDescription(aboutUser.getText().toString());
        if(prefSelList.size()>0)
            user.setSexualPref(getSexTypeArray(prefSelList));
        user.setViewedBy(viewedByAll.isChecked()?
                "All":viewedByMale.isChecked()?"Male":"Female");

        //public location
        //blocked Users
        //deactivate account
        //About Us
        //Terms and conditions
        //Privacy Policy
        //Logout

        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                finishProgress();

                if(e == null)
                    updateSuccessful();
                else
                    updateFailed(e.getMessage());
            }
        });

    }

    private void updateFailed(String msg) {
        showToastMessage(msg);
    }

    private void updateSuccessful() {
        if(mCallbacks != null)
            mCallbacks.deployFragment(Constants.TAB_BAR_ITEM_PROFILE_FRAG_ID, null, null);
    }

    private String[] getSexTypeArray(ArrayList<Integer> prefSelList) {
        String[] types = new String[prefSelList.size()];
        int i= 0;
        for(Integer item:prefSelList){
            types[i] = prefTypeList[item];
            i++;
        }
        return types;
    }

    private String[] getTravTypeArray(ArrayList<Integer> travList) {
        String[] types = new String[travList.size()];
        int i= 0;
        for(Integer item:travList){
            types[i] = travellerTypeList[item];
            i++;
        }
        return types;
    }

    private void warnTheUser() {
        final AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
        ad.setTitle("Missing Profile Picture")
                .setMessage("Please select a profile picture")
                .setPositiveButton("Ok, Let's do this", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        openGallery(SELECT_PROFILE_PICTURE);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }
    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        final View view = inflater.inflate(R.layout.settings_layout, container, false);

        setUpAbout(view);
        setUpEditText(view);
        setUpProfilePhoto(view);
        setBackgroundPic(view);
        setUpCountrySelection(view);
        setUpHomeTown(view);
        setUpTravellerType(view);
        setUpSexualPreferences(view);
        setUpViewedBy(view);
        logout(view);

        return view;
    }

    private void logout(View view) {
        view.findViewById(R.id.s_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PUser.logOut();
                if(mCallbacks != null)
                    mCallbacks.logOut();
            }
        });
    }

    private void setUpViewedBy(View view) {
        viewedByAll =(RadioButton)view.findViewById(R.id.s_viewed_all);
        viewedByMale =(RadioButton)view.findViewById(R.id.s_viewed_male);
        viewedByFemale =(RadioButton)view.findViewById(R.id.s_viewed_female);
    }

    private void setUpAbout(View v) {
        aboutUser = (EditText)v.findViewById(R.id.s_about_yourself);
    }

    private void setUpEditText(View v) {
        mUserName = (EditText)v.findViewById(R.id.s_user_name);
        mUserName.setText(user.getUserName() != null?user.getUserName():"");
        mEmailView = (EditText)v.findViewById(R.id.s_email);
        mEmailView.setText(user.getEmail() != null?user.getEmail():"");

        mPasswordView = (EditText)v.findViewById(R.id.s_password);
    }
    private void setUpSexualPreferences(View view) {
        prefTypeList = getActivity().getResources().getStringArray(R.array.sexual_preference_type);
        prefTypeBooleanList = new boolean[prefTypeList.length];

        Button btn = (Button)view.findViewById(R.id.s_sexual_preference_button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSexualPrefDialog(v);
            }
        });
    }

    private void openSexualPrefDialog(final View v) {
        final AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
        ad.setTitle("Select Sexual Preferences")
                .setMultiChoiceItems(prefTypeList, prefTypeBooleanList, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if(isChecked)
                        {
                            // If user select a item then add it in selected items
                            prefTypeBooleanList[which] = true;
                            prefSelList.add(which);
                        }
                        else if (prefSelList.contains(which))
                        {
                            // if the item is already selected then remove it
                            prefSelList.remove(Integer.valueOf(which));
                            prefTypeBooleanList[which] = false;
                        }
                    }
                }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String msg="";
                for (int i = 0; i < prefSelList.size(); i++) {
                    if(i == prefSelList.size() - 1)
                        msg=msg + prefTypeList[prefSelList.get(i)];
                    else
                        msg=msg + prefTypeList[prefSelList.get(i)] + ", ";
                }

                if(prefSelList.isEmpty())
                   ((Button)v).setHint("Select");
                else
                    ((Button)v).setHint(msg);

            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).show();
    }

    private void setUpTravellerType(View view) {
        travellerTypeList = getActivity().getResources().getStringArray(R.array.traveller_type);
        trvTypeBooleanList = new boolean[travellerTypeList.length];

        Button btn = (Button)view.findViewById(R.id.s_travellerType_button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTravellerDialog(v);
            }
        });

        String msg = "";
        List<String> travellerType = user.getTravelerType();
        travSelList.clear();
        int bufCount = travellerType.size();
        if(travellerType!=null) {
            for (int i=0; i<travellerTypeList.length;i++) {
                if(travellerType.contains(travellerTypeList[i])){

                    if(bufCount == 1)
                        msg=msg + travellerTypeList[i];
                    else
                        msg=msg + travellerTypeList[i] + ", ";

                    trvTypeBooleanList[i] = true;
                    travSelList.add(i);
                    bufCount--;
                }else
                    trvTypeBooleanList[i] = false;

            }

            btn.setHint(msg.isEmpty()?"Select":msg);
        }
    }

    private void openTravellerDialog(final View v) {
        final AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
        ad.setTitle("Select Traveller Type")
            .setMultiChoiceItems(travellerTypeList, trvTypeBooleanList, new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                    if(isChecked)
                    {
                        // If user select a item then add it in selected items
                        trvTypeBooleanList[which] = true;
                        travSelList.add(which);
                    }
                    else if (travSelList.contains(which))
                    {
                        // if the item is already selected then remove it
                        travSelList.remove(Integer.valueOf(which));
                        trvTypeBooleanList[which] = false;
                    }
                }
            }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String msg="";
                for (int i = 0; i < travSelList.size(); i++) {

                    if(i == travSelList.size() - 1)
                        msg=msg + travellerTypeList[travSelList.get(i)];
                    else
                        msg=msg + travellerTypeList[travSelList.get(i)] + ", ";
                }

                if(travSelList.isEmpty())
                    ((Button)v).setHint("Select");
                else
                    ((Button)v).setHint(msg);


            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).show();
    }

    private void setUpCountrySelection(final View view) {
        countrySelectionButton = (Button)view.findViewById(R.id.s_nationality_button);
        selectedCountryCode = user.getCountryCode();
        selectedCountry = getCountryName();
        countrySelectionButton.setHint(user.getNationality());
        countrySelectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteSelectedCity(view);
                openCountryDialog(v);
            }
        });
    }

    private String getCountryName() {
        Locale loc = new Locale("",selectedCountryCode);
        return loc.getDisplayCountry().trim();

    }

    private void deleteSelectedCity(View view) {
        if(homeTown == null)
            homeTown = (Button)view.findViewById(R.id.s_hometown);

        homeTown.setHint("Select (optional)");

        selectedCity = null;
    }

    private void setUpHomeTown(View view) {
        if(homeTown == null)
            homeTown = (Button)view.findViewById(R.id.s_hometown);

        selectedCity = user.getHometown();
        if(selectedCity == null )
            homeTown.setHint("Select (optional)");
        else{
            if(!selectedCity.isEmpty())
                homeTown.setHint(selectedCity);
            else
                homeTown.setHint("Select (optional)");
        }

        homeTown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedCountryCode != null)
                    openCityDialog(v);
            }
        });
    }

    private void openCityDialog(final View v) {
        CityDialog cd = new CityDialog(getActivity(), selectedCountryCode);
        cd.addCityDialogListener(new CityDialog.CityDialogListener() {
            @Override
            public void onCitySelected(String countryName, String countryCode, String cityName) {
                ((Button)v).setHint(cityName);
                selectedCity = cityName;
            }
        });
        cd.show();
    }

    private void openCountryDialog(final View v) {
        CountryDialog cd = new CountryDialog(getActivity());
        cd.addCountryDialogListener(new CountryDialog.CountryDialogListener() {
            @Override
            public void onCountrySelected(String countryName, String countryCode) {
                ((Button)v).setHint(countryName);
                selectedCountryCode = countryCode;
                selectedCountry = countryName;
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
        if(user.getCoverPicture() == null)
            mBackgroundPhoto.setImageResource(R.drawable.bg_profile_cover);
        else {
            mBackgroundPhoto.setParseFile(user.getCoverPicture());
            mBackgroundPhoto.loadInBackground();
        }
    }

    private void setUpProfilePhoto(View view) {
        mPhoto = (RoundedImageView) view.findViewById(R.id.s_photo);
        mPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery(SELECT_PROFILE_PICTURE);
            }
        });

        if(user.getProfilePicture() == null) {
            mPhoto.setImageResource(R.drawable.profile_empty_edit_new);
            profilePhotoAttached = false;
        }else{
            mPhoto.setParseFile(user.getProfilePicture());
            mPhoto.loadInBackground();
            profilePhotoAttached = true;
        }
    }

    private void openGallery(int openPurpose){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,
                "Select Picture"), openPurpose);
    }

    //Todo: check for large image
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
                        mPhoto.setImageBitmap(scaledDownIfLarge(selectedImagePath));
//                        mBackgroundPhoto.setImageBitmap(BitmapFactory.decodeFile(selectedImagePath));
                        profilePhotoAttached = true;
                    }
                }

            }else if(requestCode == SELECT_BACKGROUND_PICTURE){
                Uri selectedImageUri = data.getData();
                filemanagerstring = selectedImageUri.getPath();
                selectedImagePath = getPath(selectedImageUri);
                if(selectedImagePath!=null){
                    if(mBackgroundPhoto != null){
                        mBackgroundPhoto.setImageBitmap(scaledDownIfLarge(selectedImagePath));
//                        mBackgroundPhoto.setImageBitmap(BitmapFactory.decodeFile(selectedImagePath));
                    }
                }
            }
        }
    }

    private Bitmap scaledDownIfLarge(String selectedImagePath) {

        Bitmap resized = BitmapFactory.decodeFile(selectedImagePath);

        int nh = (int) ( resized.getHeight() * (512.0 / resized.getWidth()) );

        Bitmap scaled = Bitmap.createScaledBitmap(resized, 512, nh, true);

        return scaled;
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
