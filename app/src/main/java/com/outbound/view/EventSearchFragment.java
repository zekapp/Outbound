package com.outbound.view;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.outbound.R;
import com.outbound.model.PEvent;
import com.outbound.ui.util.CitySelectDialog;
import com.outbound.ui.util.CountrySelectDialog;
import com.outbound.util.Constants;
import com.outbound.util.GeoCodeCallback;
import com.outbound.util.LocationUtils;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import static com.outbound.util.LogUtils.*;

/**
 * Created by zeki on 2/10/2014.
 */
public class EventSearchFragment extends BaseFragment {
    private static final String TAG = makeLogTag(EventSearchFragment.class);
    @Override
    protected void setUp(Object param1, Object param2) {
        super.setUp(param1,param2);
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }
    private void setUpActionBar(Activity activity) {
        View viewActionBar = activity.getLayoutInflater().inflate(R.layout.custom_ab_back_button, null);
        TextView title = (TextView)viewActionBar.findViewById(R.id.action_bar_title);
        title.setText(getResources().getString(R.string.event_search_fragment_title));
        ImageView icon = (ImageView)viewActionBar.findViewById(R.id.ab_icon_1);
        icon.setImageResource(R.drawable.action_find);
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
                attempToSearchEvent();
            }
        });
    }

    private String selectedCountry = null;
    private String selectedCity = null;
    private String place = null;
    private String selectedCountryCode = null;
    private GregorianCalendar eventStartDate = null;
    private double proximity = 0;


    private Button countrySelectButton;
    private Button citySelectButton;
    private Button eventFromDateButton;
    private Button proximitySelectButton;
    private EditText eventPlaceEdit;

    //Proximity
    final CharSequence distanceList[] = { "1km", "5km", "10km", "20km", "50km", "100km", "100km+" };
    final double distance[] = {1,5,10,20,50,100,1000};

    private void attempToSearchEvent() {

        resetErrors();

        boolean cancel = false;
        View focusView = null;

        place = eventPlaceEdit.getText().toString();

        if(selectedCountry == null){
            countrySelectButton.setError("Country required.");
            focusView = countrySelectButton;
            cancel = true;
        }

        if(selectedCity == null ){
            proximity = 5000; //5000 km diameter search. Country search
        }

        if(place == null){
            proximity = 200; //200km km diameter search. City search
        }

        if((proximity == 0) && !selectedCity.isEmpty() && !place.isEmpty()){
            proximitySelectButton.setError("Required");
            focusView = countrySelectButton;
            cancel = true;
        }
        if(cancel){
            focusView.requestFocus();
        }else
        {
            searchEvents();
        }
    }

    private void searchEvents() {
        String[] address = {selectedCountry, selectedCity, place};
        startProgress("Events searching...");
        LocationUtils.findGeoLocationFromAddress(address, getActivity(), new GeoCodeCallback() {
            @Override
            public void done(ParseGeoPoint location, Exception e) {
                if(e == null){
                    ParseQuery<PEvent> query = ParseQuery.getQuery(PEvent.class);
                    query.whereWithinKilometers(PEvent.eventLocationPoint, location, proximity);
                    if(eventStartDate != null)
                        query.whereGreaterThanOrEqualTo(PEvent.startDate,eventStartDate.getTime());
                    query.orderByAscending(PEvent.startDate);
                    query.orderByAscending(PEvent.startTime);
                    query.findInBackground(new FindCallback<PEvent>() {
                        @Override
                        public void done(List<PEvent> pEvents, ParseException e) {
                            stopProgress();
                            if(e == null){
                                if(pEvents.size() > 0){
                                    showToastMessage(Integer.toString(pEvents.size()) + " Events found");
                                    if(mCallbacks != null)
                                        mCallbacks.deployFragment(Constants.SEARCH_EVENTS_RESULT_FRAG_ID, pEvents, null);
                                }else
                                    showToastMessage(" No Event found. Increase Proximity");
                            }else
                            {
                                LOGD(TAG, "findInBackground e: " + e.getMessage());
                                showToastMessage("Network Error. Check Connection...");
                            }
                        }
                    });
                }else{
                    LOGD(TAG, "findGeoLocationFromAddress E: " + e.getMessage());
                    showToastMessage("Network Error. Check Connection...");
                }
            }
        });
    }

    private void resetErrors() {
        countrySelectButton.setError(null);
        eventFromDateButton.setError(null);
        proximitySelectButton.setError(null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        final View view = inflater.inflate(R.layout.event_search_fragment_layout, container, false);
        setUpActionBar(getActivity());

        countrySelectButton = (Button)view.findViewById(R.id.es_country_button);
        citySelectButton= (Button)view.findViewById(R.id.es_city_button);
        eventFromDateButton= (Button)view.findViewById(R.id.es_event_date_button);
        proximitySelectButton= (Button)view.findViewById(R.id.es_proximitiy_button);
        eventPlaceEdit = (EditText)view.findViewById(R.id.es_place);

        setUpSelectCountry();
        setUpSelectCity();
        setUpDateButton();
        setUpProximityButton();

        return view;
    }

    private void setUpProximityButton() {
        proximitySelectButton.setHint("Select");
        proximitySelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                proximitySelectButton.setHint("Select");
                proximitySelectButton.setError(null);
                proximity = 0;

                if(selectedCity == null){
                    citySelectButton.setError("Required Field!");
                    citySelectButton.requestFocus();
                }else if(eventPlaceEdit.getText().toString().isEmpty()){
                    eventPlaceEdit.setError("Required Field!");
                    citySelectButton.requestFocus();
                }else
                {
                    openProximityDialogButton(v);
                }

            }
        });
    }

    private void openProximityDialogButton(final View v) {

        final AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
        ad.setTitle("Select Proximity");
        ad.setSingleChoiceItems(distanceList, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                proximity = distance[which];
                ((Button) v).setHint(distanceList[which]);
            }
        });
        ad.setPositiveButton("Set",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        ad.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                proximity = 0;
                ((Button)v).setHint("Select");
            }
        });
        ad.show();
    }

    private void setUpDateButton() {
        eventFromDateButton.setText("Select");
        eventFromDateButton.
                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDatePickerDialog(v);
                    }
                });
    }

    private void showDatePickerDialog(final View v) {
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(getActivity(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy");
                        String dateFormatted = null;
                        eventStartDate = new GregorianCalendar(year,monthOfYear,dayOfMonth);
                        dateFormat.setCalendar(eventStartDate);
                        dateFormatted = dateFormat.format(eventStartDate.getTime());
                        ((Button)v).setText(dateFormatted);

                    }
                }, mYear, mMonth, mDay);
        dpd.show();
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

    private void openCountryDialog(final View v) {
        CountrySelectDialog cd = new CountrySelectDialog(getActivity());
        cd.addCountryDialogListener(new CountrySelectDialog.CountryDialogListener() {
            @Override
            public void onCountrySelected(String countryName, String countryCode) {
                ((Button)v).setHint(countryName);
                selectedCountry = countryName;
                selectedCountryCode = countryCode;
            }
        });
        cd.show();
    }
}
