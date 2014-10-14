package com.outbound.loginSignUp;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
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
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.outbound.R;
import com.outbound.model.PUser;
import com.outbound.ui.util.CountryDialog;
import com.outbound.ui.util.RoundedImageView;
import com.outbound.DispatchActivity;
import com.outbound.util.CountryCodes;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import static com.outbound.util.LogUtils.LOGD;
import static com.outbound.util.LogUtils.makeLogTag;


/**
 * Created by zeki on 25/08/2014.
 */
public class SignUpWithEmail extends Activity{

    private static final String TAG = makeLogTag(SignUpWithEmail.class);

    private static final int SELECT_PROFILE_PICTURE = 1;
    private static final int SELECT_BACKGROUND_PICTURE = 2;

    private boolean alreadySignUp = false;
    private boolean profilePhotoAttached = false;


    private GregorianCalendar birthCalender;
    private String birthDateFormatted;
    private Dialog progressDialog;
    private String selectedCountry = null;


    private boolean dateOfBirthSelected = false;
    private RadioButton femaleBox;
    private RadioButton maleBox;

    private EditText mEmailView;
    private EditText mUserName;
    private EditText mPasswordView;
    private Button birthDateButton;
    private Button countrySelectionButton;
    private EditText homeTown;
    private EditText aboutUser;

    private String selectedImagePathProfile = null;
    private String selectedImagePathCover = null;
    private String filemanagerstring;

    //Traveller Type
    private String[] travellerTypeList;
    private boolean[] trvTypeBooleanList;
    private ArrayList<Integer> travSelList = new ArrayList<Integer>();

    private ActionBar actionBar;

    private RoundedImageView mPhoto;
    private ParseImageView mBackgroundPhoto;

    private PUser user ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sign_up_with_email);

        if(PUser.getCurrentUser() == null) {
            user = new PUser();
            alreadySignUp = false;
        }
        else {
            alreadySignUp = true;
            user = PUser.getCurrentUser();
        }

        setUpActionBar();

        setUpEditText();

        setUpAbout();
        setUpProfilePhoto();
        setBackgroundPic();
        setUpBirtDate();
        setUpCountrySelection();
        setUpHomeTownSelect();
        setUpTravellerType();
        setUpGender();

    }

    private void setUpAbout() {
        aboutUser = (EditText)findViewById(R.id.about_sign_up_edit_text);
    }

    private void setUpHomeTownSelect() {
        homeTown = (EditText)findViewById(R.id.home_town_sign_up_edit_text);
    }

    private void setUpEditText() {
        mUserName = (EditText)findViewById(R.id.login_mail_name);
        mUserName.setText(user.getUserName() != null?user.getUserName():"");
        mEmailView = (EditText)findViewById(R.id.email_sign_up_edit_text);
        mEmailView.setText(user.getEmail() != null?user.getEmail():"");

        mPasswordView = (EditText)findViewById(R.id.password_sign_up_edit_text);
    }

    private void setUpGender() {
        maleBox = (RadioButton)findViewById(R.id.checkBoxMale);
        femaleBox = (RadioButton)findViewById(R.id.checkBoxFemale);
    }

    private void setBackgroundPic() {
        mBackgroundPhoto = (ParseImageView) findViewById(R.id.photo_background);

        (findViewById(R.id.change_background)).
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

    private void setUpProfilePhoto() {
        mPhoto = (RoundedImageView)findViewById(R.id.photo);
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

    private void openGallery(int openPurpose) {
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
                selectedImagePathProfile = getPath(selectedImageUri);
                if(selectedImagePathProfile!=null){
                    if(mPhoto != null){
//                        Bitmap resized = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(selectedImagePath), 191, 191, true);
////                        Bitmap conv_bm = getRoundedRectBitmap(resized, 191);
//                        Bitmap conv_bm = getRoundedCornerBitmap(resized, 40);
//                        mPhoto.setImageBitmap(conv_bm);
                        mPhoto.setImageBitmap(scaledDownIfLarge(selectedImagePathProfile));
//                        mBackgroundPhoto.setImageBitmap(BitmapFactory.decodeFile(selectedImagePath));
                        profilePhotoAttached = true;
                    }
                }

            }else if(requestCode == SELECT_BACKGROUND_PICTURE){
                Uri selectedImageUri = data.getData();
                filemanagerstring = selectedImageUri.getPath();
                selectedImagePathCover = getPath(selectedImageUri);
                if(selectedImagePathCover!=null){
                    if(mBackgroundPhoto != null){
                        mBackgroundPhoto.setImageBitmap(scaledDownIfLarge(selectedImagePathCover));
//                        mBackgroundPhoto.setImageBitmap(BitmapFactory.decodeFile(selectedImagePathCover));
                    }
                }
            }
        }
    }

    public String getPath(Uri uri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, proj, null, null, null);
        if(cursor!=null)
        {

            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        else return null;

    }

    private Bitmap scaledDownIfLarge(String selectedImagePath) {

        Bitmap resized = BitmapFactory.decodeFile(selectedImagePath);

        int nh = (int) ( resized.getHeight() * (512.0 / resized.getWidth()) );

        Bitmap scaled = Bitmap.createScaledBitmap(resized, 512, nh, true);

        return scaled;
    }

    private void setUpTravellerType() {
        travellerTypeList = getResources().getStringArray(R.array.traveller_type);
        trvTypeBooleanList = new boolean[travellerTypeList.length];

        Button btn = (Button)findViewById(R.id.traveller_type_button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTravellerDialog(v);
            }
        });
    }



    private void openTravellerDialog(final View v) {
        final AlertDialog.Builder ad = new AlertDialog.Builder(this);
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
    private void setUpCountrySelection() {
        countrySelectionButton = (Button)findViewById(R.id.country_button_sign_up);
        countrySelectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteSelectedCity();
                showCountryPickerDialog(v);
            }
        });
    }

    private void deleteSelectedCity() {

    }

    private void setUpBirtDate() {
        birthDateButton = (Button)findViewById(R.id.birth_date_button_sign_up);
        birthDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(v);
            }
        });
    }

    private void setUpActionBar() {
        View viewActionBar = getLayoutInflater().inflate(R.layout.custom_ab_back_button, null);
        TextView title = (TextView)viewActionBar.findViewById(R.id.action_bar_title);
        title.setText(getResources().getString(R.string.login_via_email_title));
        ImageView icon = (ImageView)viewActionBar.findViewById(R.id.ab_icon_1);
        icon.setImageResource(R.drawable.action_join);

        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attempToSingUp();
            }
        });

        ImageView backIcon = (ImageView)viewActionBar.findViewById(R.id.ab_back_icon);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        backIcon.setVisibility(View.GONE);

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(false); // disable the button
            actionBar.setDisplayHomeAsUpEnabled(false); // remove the left caret
            actionBar.setDisplayShowHomeEnabled(false); // remove the icon
            actionBar.setCustomView(viewActionBar);
        }
    }


    private void showCountryPickerDialog(final View v){
        CountryDialog cd = new CountryDialog(this);
        cd.addCountryDialogListener(new CountryDialog.CountryDialogListener() {
            @Override
            public void onCountrySelected(String countryName, String countryCode) {
                ((Button)v).setHint(countryName);
                selectedCountry = countryName;
            }
        });
        cd.show();
    }

    private void showTimePickerDialog(final View v) {
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
//                        String birthDate = Integer.toString(dayOfMonth)+"/"+Integer.toString(monthOfYear)+"/"+Integer.toString(year);
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
                        birthCalender = new GregorianCalendar(year,monthOfYear,dayOfMonth);
                        dateFormat.setCalendar(birthCalender);
                        birthDateFormatted = dateFormat.format(birthCalender.getTime());

                        ((Button)v).setText(birthDateFormatted);
                        dateOfBirthSelected = true;
                    }
                }, mYear, mMonth, mDay);
        dpd.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                ((Button)v).setText("dd/mm/yyyy");
                dateOfBirthSelected = false;
            }
        });
        dpd.show();
    }

    private void attempToSingUp() {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mUserName.setError(null);
        birthDateButton.setError(null);
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

        //check for date of birth
        if(!dateOfBirthSelected){
            birthDateButton.setError(getString(R.string.error_invalid_date));
            focusView = birthDateButton;
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


        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
//            ParseUser user = new PUser();
//            ParseUser parseUser = ParseUser.getCurrentUser();

            try{
                //            parseUser.setPassword(password);
//                parseUser.put("password", password);
//                parseUser.put("username", username);
//                parseUser.put("email",email);

//                singUp(username.toLowerCase(Locale.getDefault()),email,password);
                singUp(username,email,password);
            }
            catch (Exception e){
                LOGD(TAG, "attempToSingUp: " + e.getMessage());
            }
//            parseUser.signUpInBackground(new SignUpCallback() {
//                @Override
//                public void done(ParseException e) {
//                    if(e == null){
//                        Log.i("SignUpWithEmail", "user signup succesfully");
//                    }else
//                    {
//                        Log.i("SignUpWithEmail", "error: " + e.getMessage());
//                    }
//                }
//            });
        }
        
    }

    protected void loginSuccessful() {
        dissmissProgress();
        Intent intent =  new Intent(SignUpWithEmail.this, DispatchActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void setUpUserFields() {

//        ParseUser parseUser = ParseUser.getCurrentUser();
        PUser parseUser = PUser.getCurrentUser();
        try{
//            parseUser.put("gender",maleBox.isChecked()?"Male":"Female");
////            parseUser.put("age", new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.ENGLISH).parse(birthDateFormatted));
////            parseUser.put("age", new Date());
//
//            parseUser.put("age", birthCalender.getTime());
//            parseUser.put("nationality",selectedCountry);
//            if(homeTown.getText().toString() != null )
//                parseUser.put("homeTown",homeTown.getText().toString());
//            if(travellerTypeList.length > 0)
//                parseUser.put("travelType", Arrays.asList(travellerTypeList));
//            if(aboutUser.getText().toString() != null )
//                parseUser.put("shortDescription",aboutUser.getText().toString());
//
//            ParseFile saveProfileFile= new ParseFile(
//                    "profilePicture.jpg",compressAndConvertImageToByteFrom(
//                    BitmapFactory.decodeFile(selectedImagePathProfile)));
//            parseUser.put("profileImage",saveProfileFile);

            parseUser.setGender(maleBox.isChecked()?"Male":"Female");
            parseUser.setDateOfBirth(birthCalender.getTime());
            parseUser.setNationality(selectedCountry);
            parseUser.setCountryCode(new CountryCodes().getCode(selectedCountry).trim());
            if(homeTown.getText().toString() != null )
                parseUser.setHometown(homeTown.getText().toString());
            if(travSelList.size() > 0)
                parseUser.setTravelerType(getTravTypeArray(travSelList));
            if(aboutUser.getText().toString() != null )
                parseUser.setShortDescription(aboutUser.getText().toString());


            if(selectedImagePathProfile != null){
                ParseFile saveProfileFile= new ParseFile(
                        "profilePicture.jpg",compressAndConvertImageToByteFrom(
                        BitmapFactory.decodeFile(selectedImagePathProfile)));
                parseUser.setProfilePicture(saveProfileFile);
            }

            ParseFile saveCoverFile = null;
            if(selectedImagePathCover != null){
                saveCoverFile = new ParseFile("coverPicture.jpg",
                        compressAndConvertImageToByteFrom(
                                BitmapFactory.decodeFile(selectedImagePathCover)));
                parseUser.setCoverPicture(saveCoverFile);
            }
            else if(!alreadySignUp){
                saveCoverFile = new ParseFile("coverPicture.jpg"
                        ,compressAndConvertImageToByteFrom(
                        BitmapFactory.decodeResource(getResources(),
                                R.drawable.bg_profile_cover)));
                parseUser.setCoverPicture(saveCoverFile);
            }

            parseUser.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {

                    if (e == null) {
                        signUpMsg("Account Created Successfully");
                        loginSuccessful();
                    } else {
                        // Setting up didn't succeed. Look at the ParseException
                        // to figure out what went wrong
                        signUpMsg(e.getMessage());
                        LOGD(TAG," setUpUserFields-saveInBackground: " + e.getMessage());
                    }

                }
            });

        }catch (Exception e){
            dissmissProgress();
            LOGD(TAG," setUpUserFields: " + e.getMessage());
        }

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

    private void singUp(final String mUsername, String mEmail, String mPassword) {

        SignUpWithEmail.this.progressDialog = ProgressDialog.show(
                SignUpWithEmail.this, "", "Logging in...", true);

        Toast.makeText(getApplicationContext(), mUsername + " - " + mEmail, Toast.LENGTH_SHORT).show();

        user.setUsername(mUsername);
        user.setPassword(mPassword);
        user.setEmail(mEmail);

        if(!alreadySignUp){
            user.signUpInBackground(new SignUpCallback() {
                public void done(ParseException e) {
                    if (e == null) {
                        setUpUserFields();
                    } else {
                        // Sign up didn't succeed. Look at the ParseException
                        // to figure out what went wrong
                        dissmissProgress();
                        signUpMsg("Account already taken.");
                        LOGD(TAG, "singUp-signUpInBackground: " + e.getMessage());
                    }
                }
            });
        }
        else{
            user.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        setUpUserFields();
                    } else {
                        // Sign up didn't succeed. Look at the ParseException
                        // to figure out what went wrong
                        dissmissProgress();
                        LOGD(TAG, "singUp-saveInBackground: " + e.getMessage());
                    }
                }
            });
        }


    }

    private void dissmissProgress() {
        SignUpWithEmail.this.progressDialog.dismiss();
    }

    private void signUpMsg(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private void warnTheUser() {
        final AlertDialog.Builder ad = new AlertDialog.Builder(this);
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

    private byte[] compressAndConvertImageToByteFrom(Bitmap image) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }
    private boolean isEmailValid(String email) {
        return email.contains("@");
    }
    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }
}
