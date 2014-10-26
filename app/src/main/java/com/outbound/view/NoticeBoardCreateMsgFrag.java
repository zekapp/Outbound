package com.outbound.view;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.outbound.R;
import com.outbound.model.PNoticeBoard;
import com.outbound.model.PUser;
import com.outbound.ui.util.CitySelectDialog;
import com.outbound.ui.util.CountrySelectDialog;
import com.outbound.util.Constants;
import com.outbound.util.GeoCodeCallback;
import com.outbound.util.location.LocationUtils;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.SaveCallback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.outbound.util.LogUtils.*;

/**
 * Created by zeki on 1/10/2014.
 */
public class NoticeBoardCreateMsgFrag extends BaseFragment {
    private static final String TAG = makeLogTag(BaseActivity.class);

    private Button countrySelectButton;
    private Button citySelectButton;
    private Button travellerTypeButton;


    private String selectedCountry = null;
    private String selectedCountryCode = null;
    private String selectedCity = null;
    private String place = null;
    private String title = null;
    private String description = null;
    private EditText noticeBoardPlaceEdit;
    private EditText noticeBoardTitleEdit;
    private EditText noticeBoardDescriptionEdit;

    private String[] travellerTypeList;
    private boolean[] trvTypeBooleanList;
    private ArrayList<Integer> travSelList = new ArrayList<Integer>();
    private SimpleDateFormat formatter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        formatter = new SimpleDateFormat("dd/MM/yyyy");
    }

    @Override
    protected void setUp( Object param1, Object param2) {
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
        title.setText(getResources().getString(R.string.notice_board_fragment_create_message_title));
        ImageView icon = (ImageView)viewActionBar.findViewById(R.id.ab_icon_1);
        icon.setImageResource(R.drawable.action_add);
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
                attempToCreateNoticeBoardPost();
            }
        });
    }

    private void attempToCreateNoticeBoardPost() {
        resetErrors();

        boolean cancel = false;
        View focusView = null;

        place = noticeBoardPlaceEdit.getText().toString();
        title = noticeBoardTitleEdit.getText().toString();
        description = noticeBoardDescriptionEdit.getText().toString();

        if(selectedCountry == null){
            countrySelectButton.setError("Country required.");
            focusView = countrySelectButton;
            cancel = true;
        }
        if(selectedCity == null){
            citySelectButton.setError("City required.");
            focusView = citySelectButton;
            cancel = true;
        }

        if(cancel){
            focusView.requestFocus();
        }else{
            createEvent();
        }

    }

    private void createEvent() {
        startProgress("Your notice is creating");
        //find geoLocation
        String[] arr = {selectedCountry,selectedCity,place};
        PUser currentUser = PUser.getCurrentUser();

//        NoticeBoardMessage message = new NoticeBoardMessage();
//        message.setText(description);
//        message.setProfilePicture(currentUser.getProfilePicture());
////        noticeBoardMessage.setDate(new Date());
//        message.setDate(formatter.format(new Date()));
//        message.setUserID(currentUser.getObjectId());
//        message.setUserName(currentUser.getUserName());


        final PNoticeBoard noticeBoard = new PNoticeBoard();
        noticeBoard.setCountry(selectedCountry);
        noticeBoard.setCity(selectedCity);
        noticeBoard.setPlace(place);
        noticeBoard.setCreatedBy(PUser.getCurrentUser());
        noticeBoard.setNoticeboardTitle(title);
        noticeBoard.setDescription(description);

        if(travSelList.size() > 0)
            noticeBoard.setTravellerType(getTravTypeArray(travSelList));
        noticeBoard.setParticipants(currentUser);

        LocationUtils.findGeoLocationFromAddress(arr, getActivity(), new GeoCodeCallback() {
            @Override
            public void done(ParseGeoPoint location, Exception e) {

                if(e == null)
                    noticeBoard.setNoticeBoardLocation(location);
                else
                    noticeBoard.setNoticeBoardLocation(new ParseGeoPoint());

                noticeBoard.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        stopProgress();
                        if(e == null){
                            showToastMessage("Your notice was created.");
                            if(mCallbacks != null)
                                mCallbacks.deployFragment(Constants.TAB_BAR_ITEM_NOTICEBOARD_FRAG_ID,null,null);
                        }else
                        {
                            LOGD(TAG, "createEvent-saveInBackground e: " + e.getMessage());
                            showToastMessage("Network Error. Check connection");
                        }
                    }
                });
            }
        });
    }

    private void resetErrors() {
        countrySelectButton.setError(null);
        citySelectButton.setError(null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        final View view = inflater.inflate(R.layout.notice_board_create_message, container, false);

        selectedCountry = null;
        selectedCity = null;

        countrySelectButton = (Button)view.findViewById(R.id.cm_country_button);
        citySelectButton = (Button)view.findViewById(R.id.cm_city_button);
        travellerTypeButton = (Button)view.findViewById(R.id.cm_traveller_type_button);
        noticeBoardPlaceEdit = (EditText)view.findViewById(R.id.cm_place);
        noticeBoardTitleEdit = (EditText)view.findViewById(R.id.s_message_title);
        noticeBoardDescriptionEdit = (EditText)view.findViewById(R.id.s_message_body);

        setUpSelectCountry();
        setUpSelectCity();
        setUpTravellerTypeButton();

        return view;
    }

    private void setUpTravellerTypeButton() {
        travellerTypeList = getActivity().getResources().getStringArray(R.array.traveller_type);
        trvTypeBooleanList = new boolean[travellerTypeList.length];


        travellerTypeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTravellerDialog(v);
            }
        });


        List<String> travellerType = new ArrayList<String>();
        Collections.addAll(travellerType, travellerTypeList);

        travSelList.clear();

        for(int i=0; i<travellerTypeList.length;i++){
            travSelList.add(i);
            trvTypeBooleanList[i] = true;
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
                    ((Button)v).setHint(travSelList.size() == travellerTypeList.length ? "All": msg);


            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).show();
    }

    private void setUpSelectCountry() {
        countrySelectButton.setHint("Select");
        countrySelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countrySelectButton.setError(null);
                selectedCity = null;
                citySelectButton.setHint("Select");
                openCountryDialog(v);
            }
        });
    }

    private void setUpSelectCity() {
        citySelectButton.setHint("Select");
        citySelectButton.
                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        citySelectButton.setError(null);
                        if (selectedCountry == null) {
                            countrySelectButton.setError("Required Field!");
                            countrySelectButton.requestFocus();
                        } else {
                            openCityDialog(v);
                        }
                    }
                });
    }

    private void openCountryDialog(final View v) {
        CountrySelectDialog cd = new CountrySelectDialog(getActivity());
        cd.addCountryDialogListener(new CountrySelectDialog.CountryDialogListener() {
            @Override
            public void onCountrySelected(String countryName, String countryCode) {
                ((Button)v).setHint(countryName);
                selectedCountry = countryName;
                selectedCountryCode=countryCode;
            }
        });
        cd.show();
    }
    private void openCityDialog(final View v) {
        CitySelectDialog cd = new CitySelectDialog(getActivity(), selectedCountryCode);
        cd.addCityDialogListener(new CitySelectDialog.CityDialogListener() {
            @Override
            public void onCitySelected(String countryName, String countryCode, String cityName) {
                ((Button)v).setHint(cityName);
                selectedCity = cityName;
            }
        });
        cd.show();
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
}
