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
import android.widget.RadioButton;
import android.widget.TextView;

import com.outbound.R;
import com.outbound.model.PUser;
import com.outbound.ui.util.AgeRangeDialog;
import com.outbound.ui.util.CitySelectDialog;
import com.outbound.ui.util.CountrySelectDialog;
import com.outbound.util.Constants;
import com.outbound.util.location.LocationUtils;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.outbound.util.LogUtils.*;

/**
 * Created by zeki on 30/09/2014.
 */
public class SearchPeopleDetailFragment extends BaseFragment {
    private static final String TAG = makeLogTag(SearchPeopleDetailFragment.class);
    @Override
    protected void setUp(Object param1, Object param2) {
        super.setUp(param1, param2);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        setUpActionBar(activity);
    }

    private void setUpActionBar(Activity activity) {
        View viewActionBar = activity.getLayoutInflater().inflate(R.layout.custom_ab_back_button, null);
        TextView title = (TextView)viewActionBar.findViewById(R.id.action_bar_title);
        title.setText(getResources().getString(R.string.search_people_detail_fragment_title));
        ImageView icon1 = (ImageView)viewActionBar.findViewById(R.id.ab_icon_1);
        icon1.setImageResource(R.drawable.action_save);
        ImageView icon2 = (ImageView)viewActionBar.findViewById(R.id.ab_icon_2);
        icon2.setImageResource(R.drawable.action_reset);
        icon2.setVisibility(View.VISIBLE);

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

        icon1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //save search properties
            }
        });

        icon2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attempToSearch();
            }
        });
    }

    private String selectedCountry = null;
    private String selectedNationality = null;
    private String selectedCountryCode = null;
    private String selectedCity = null;
    private Date birthDateFrom = null;
    private Date birthDateTo = null;
    private String nextTravelCountry = null;


    //Traveller Type
    private String[] travellerTypeList;
    private boolean[] trvTypeBooleanList;
    private ArrayList<Integer> travSelList = new ArrayList<Integer>();
    private List<String> selectedTravellerType = new ArrayList<String>();

    //Sexual Preferences
    private String[] prefTypeList;
    private boolean[] prefTypeBooleanList;
    private ArrayList<Integer> prefSelList = new ArrayList<Integer>();
    private List<String> selectedSexualPrefType = new ArrayList<String>();


    private EditText userNameEdit;
    private RadioButton allBtn;
    private RadioButton maleBtn;
    private RadioButton femaleBtn;
    private Button countrySelectButton;
    private Button citySelectButton;
    private Button ageSelectButton;
    private Button nationalitySelectButton;
    private Button travellerTypeSelButton;
    private Button sexualPrefButton;
    private Button nextTravelButton;

    private void attempToSearch() {
        ParseQuery<PUser> query = ParseQuery.getQuery(PUser.class);
        if(!userNameEdit.getText().toString().isEmpty())
            query.whereEqualTo(PUser.userName, userNameEdit.getText().toString());
        if(!allBtn.isChecked())
            query.whereEqualTo(PUser.gender,maleBtn.isChecked()?"Male":"Female");
        if(selectedCountry != null)
            query.whereEqualTo(PUser.currentCountry, selectedCountry);
        if(selectedCity != null)
            query.whereEqualTo(PUser.currentCity, selectedCity);
        if(birthDateFrom != null && birthDateTo != null){
            query.whereGreaterThanOrEqualTo(PUser.age, birthDateFrom);
            query.whereLessThanOrEqualTo(PUser.age, birthDateTo);
        }
        if(selectedNationality != null)
            query.whereEqualTo(PUser.nationality, selectedNationality);
        if(!selectedTravellerType.isEmpty())
            query.whereContainedIn(PUser.travelType, selectedTravellerType); // todo: check that is working
        if(!selectedSexualPrefType.isEmpty())
            query.whereContainedIn(PUser.sexualPreference, selectedSexualPrefType);
        if(nextTravelCountry != null)
            query.whereEqualTo(PUser.nextTravels, nextTravelCountry);

        startProgress("Outbounders are searching...");
        query.findInBackground(new FindCallback<PUser>() {
            @Override
            public void done(List<PUser> pUsers, ParseException e) {
                stopProgress();
                if(e == null){
                    if(pUsers.size() > 0){
                        showToastMessage(pUsers.size() + " Outbounders found");
                        if(mCallbacks != null)
                            mCallbacks.deployFragment(Constants.SEARCH_PEOPLE_RESULT_FRAG_ID, LocationUtils.orderFriendsAccordingDistance(pUsers),null);
                    }else {
                        showToastMessage("No Outbounders found");
                    }
                }else
                {
                    showToastMessage("Network Error. Check connection.");
                    LOGD(TAG, "attempToSearch  e:" + e.getMessage());
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        final View view = inflater.inflate(R.layout.search_people_detail_layout, container, false);


        travellerTypeList = getActivity().getResources().getStringArray(R.array.traveller_type);
        trvTypeBooleanList = new boolean[travellerTypeList.length];

        userNameEdit = (EditText)view.findViewById(R.id.s_user_name);
        allBtn = (RadioButton)view.findViewById(R.id.all_gender);
        maleBtn = (RadioButton)view.findViewById(R.id.male_gender);
        femaleBtn = (RadioButton)view.findViewById(R.id.female_gender);
        countrySelectButton = (Button)view.findViewById(R.id.s_country_button);
        citySelectButton = (Button)view.findViewById(R.id.s_city_button);
        ageSelectButton= (Button)view.findViewById(R.id.s_age_range_button);
        nationalitySelectButton = (Button)view.findViewById(R.id.sp_nationality_button);
        travellerTypeSelButton = (Button)view.findViewById(R.id.s_traveller_type_button);
        sexualPrefButton = (Button)view.findViewById(R.id.s_sexual_preference_button);
        nextTravelButton = (Button)view.findViewById(R.id.s_next_travel_location_button);

        setUpSelectCountry();
        setUpSelectCity();
        setUpSelectAgeRange();
        setUpNationality();
        setUpSelectTravellerType();
        setUpSexualPreferences();
        setUpSelectNextLocation();
        return view;
    }

    private void setUpSelectNextLocation() {
        nextTravelButton.setHint("Select");
        nextTravelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextTravelCountry = null;
                nextTravelButton.setHint("Select");
                openCountryDialog(v);
            }
        });
    }

    private void setUpSexualPreferences() {
        prefTypeList = getActivity().getResources().getStringArray(R.array.sexual_preference_type);
        prefTypeBooleanList = new boolean[prefTypeList.length];
        sexualPrefButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSexualPrefDialog(v);
            }
        });

    }

    private void setUpSelectTravellerType() {
        travellerTypeSelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTravellerDialog(v);
            }
        });
    }

    private void setUpNationality() {
        nationalitySelectButton.setHint("Select");
        nationalitySelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nationalitySelectButton.setError(null);
                nationalitySelectButton.setHint("Select");
                selectedNationality = null;
                openCountryDialog(v);
            }
        });
    }

    private void setUpSelectAgeRange() {
        ageSelectButton.setHint("Select");
        ageSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ageSelectButton.setHint("Select");
                birthDateFrom = null;
                birthDateTo = null;
                openAgeSelectDialog(v);
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
                        citySelectButton.setHint("Select");
                        selectedCity = null;
                        if (selectedCountry == null) {
                            countrySelectButton.setError("Required Field!");
                            countrySelectButton.requestFocus();
                        } else {
                            openCityDialog(v);
                        }
                    }
                });
    }
    private void setUpSelectCountry() {
        countrySelectButton.setHint("Select");
        countrySelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countrySelectButton.setError(null);
                selectedCity = null;
                selectedCountry = null;
                citySelectButton.setHint("Select");
                countrySelectButton.setHint("Select");
                openCountryDialog(v);
            }
        });
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
    private void openCountryDialog(final View v) {
        CountrySelectDialog cd = new CountrySelectDialog(getActivity());
        cd.addCountryDialogListener(new CountrySelectDialog.CountryDialogListener() {
            @Override
            public void onCountrySelected(String countryName, String countryCode) {
                ((Button)v).setHint(countryName);
                if(v.getId() == R.id.s_country_button){
                    selectedCountry = countryName;
                    selectedCountryCode=countryCode;
                }else if(v.getId() == R.id.sp_nationality_button){
                    selectedNationality = countryName;
                }else
                    nextTravelCountry = countryName;
            }
        });
        cd.show();
    }


    private void openAgeSelectDialog(final View v) {
        AgeRangeDialog ad = new AgeRangeDialog(getActivity());
        ad.addAgeDialogListener(new AgeRangeDialog.AgeDialogListener() {
            @Override
            public void onAgeSelected(Date dateOfBirthFrom, Date dateOfBirthTo , int ageFrom, int ageTo) {
                ((Button)v).setHint(Integer.toString(ageFrom)+ " to " + Integer.toString(ageTo));
//                SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
//                ((Button)v).setHint(formatter.format(dateOfBirthFrom)+ " to " + formatter.format(dateOfBirthTo));
                birthDateFrom = dateOfBirthFrom;
                birthDateTo = dateOfBirthTo;
            }
        });
        ad.show();
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
                String msg = "";
                for (int i = 0; i < travSelList.size(); i++) {

                    if (i == travSelList.size() - 1) {
                        msg = msg + travellerTypeList[travSelList.get(i)];
                        selectedTravellerType.add(travellerTypeList[travSelList.get(i)]);
                    } else {
                        msg = msg + travellerTypeList[travSelList.get(i)] + ", ";
                        selectedTravellerType.remove(travellerTypeList[travSelList.get(i)]);
                    }
                }

                if (travSelList.isEmpty())
                    ((Button) v).setHint("Select");
                else
                    ((Button) v).setHint(msg);


            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).show();
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
                String msg = "";
                for (int i = 0; i < prefSelList.size(); i++) {
                    if (i == prefSelList.size() - 1) {
                        msg = msg + prefTypeList[prefSelList.get(i)];
                        selectedSexualPrefType.add(prefTypeList[prefSelList.get(i)]);
                    } else {
                        msg = msg + prefTypeList[prefSelList.get(i)] + ", ";
                        selectedSexualPrefType.remove(prefTypeList[prefSelList.get(i)]);
                    }
                }

                if (prefSelList.isEmpty())
                    ((Button) v).setHint("Select");
                else
                    ((Button) v).setHint(msg);

            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).show();
    }


}
