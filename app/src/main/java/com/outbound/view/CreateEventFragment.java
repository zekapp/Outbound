package com.outbound.view;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.outbound.R;
import com.outbound.model.PEvent;
import com.outbound.model.PUser;
import com.outbound.ui.util.CityDialog;
import com.outbound.ui.util.CountryDialog;
import com.outbound.util.Constants;
import com.outbound.util.GeoCodeCallback;
import com.outbound.util.location.LocationUtils;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.SaveCallback;

import java.sql.Time;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by zeki on 3/10/2014.
 */
public class CreateEventFragment extends BaseFragment {
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
        title.setText(getResources().getString(R.string.event_create_fragment_title));
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
                //create event on Parse
                attempToCreateEvent();
            }
        });
    }

    private String eventName = null;
    private String selectedCountry = null;
    private String selectedCountryCode = null;
    private String selectedCity = null;
    private String description = null;
    private String place = null;
    private GregorianCalendar eventFromDate = null;
    private GregorianCalendar eventFromTime = null;
    private GregorianCalendar eventToDate = null;
    private GregorianCalendar eventToTime = null;

    private Button countrySelectButton;
    private Button citySelectButton;
    private Button eventFromDateButton;
    private Button eventFromTimeButton;
    private Button eventToDateButton;
    private Button eventToTimeButton;

    private EditText eventNameEdit;
    private EditText eventDescriptionEdit;
    private EditText eventPlaceEdit;

    private void attempToCreateEvent() {

        resetErrors();

        boolean cancel = false;
        View focusView = null;


        eventName = eventNameEdit.getText().toString();
        place = eventPlaceEdit.getText().toString();
        description = eventDescriptionEdit.getText().toString();

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
        if(eventFromDate == null){
            eventFromDateButton.setError("Date required.");
            focusView = eventFromDateButton;
            cancel = true;
        }

        if(eventFromTime == null){
            eventFromTimeButton.setError("Time required.");
            focusView = eventFromTimeButton;
            cancel = true;
        }

        if(eventToDate == null){
            eventToDateButton.setError("Date required.");
            focusView = eventToDateButton;
            cancel = true;
        }

        if(eventToTime == null){
            eventToTimeButton.setError("Time required.");
            focusView = eventToTimeButton;
            cancel = true;
        }

        if(cancel){
            focusView.requestFocus();
        }else{
            createEvent();
        }
    }

    private void createEvent() {
        startProgress("Event saving database...");

        //find geoLocation
        String[] arr = {selectedCountry, selectedCity,place};

        LocationUtils.findGeoLocationFromAddress(arr,getActivity(), new GeoCodeCallback() {
            @Override
            public void done(ParseGeoPoint location, Exception e) {
                List<PUser> attendingList = new ArrayList<PUser>();
                attendingList.add(PUser.getCurrentUser());

                PEvent event = new PEvent();
                event.setEventName(eventName);
                event.setCountry(selectedCountry);
                event.setCity(selectedCity);
                event.setCreatedBy(PUser.getCurrentUser());
                event.setDescription(description);
                event.setStartDate(eventFromDate.getTime());
                event.setStartTime(eventFromTime.getTime());
                event.setEndDate(eventToDate.getTime());
                event.setEndTime(eventToTime.getTime());
                event.setPlace(place);
                event.setOutboundersGoing(attendingList);
                if(e == null)
                    event.setLocation(location);
                else
                    event.setLocation(new ParseGeoPoint());

                event.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        stopProgress();
                        if(e == null){
                            showToastMessage("Your event created successfully");
                            if(mCallbacks != null){
                                mCallbacks.deployFragment(Constants.TAB_BAR_ITEM_EVENTS_FRAG_ID, null, null);
                            }
                        }else
                        {
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
        eventFromDateButton.setError(null);
        eventToDateButton.setError(null);
        eventToTimeButton.setError(null);
        eventFromTimeButton.setError(null);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        final View view = inflater.inflate(R.layout.create_event_fragment, container, false);

        selectedCountry = null;
        selectedCity = null;
        eventFromDate = null;
        eventFromTime = null;
        eventToDate = null;
        eventToTime = null;
        selectedCountryCode = null;

        countrySelectButton = (Button)view.findViewById(R.id.ec_country_button);
        citySelectButton = (Button)view.findViewById(R.id.ec_city_button);
        eventFromDateButton = (Button)view.findViewById(R.id.ec_start_date_button);
        eventFromTimeButton = (Button)view.findViewById(R.id.ec_start_time_button);
        eventToDateButton = (Button)view.findViewById(R.id.ec_end_date_button);
        eventToTimeButton = (Button)view.findViewById(R.id.ec_end_time_button);
        eventNameEdit= (EditText)view.findViewById(R.id.ce_event_name);
        eventDescriptionEdit= (EditText)view.findViewById(R.id.ce_event_description);
        eventPlaceEdit = (EditText)view.findViewById(R.id.ec_hometown);

        setUpSelectCountry(view);
        setUpSelectCity(view);
        setUpStartDate(view);
        setUpEndDate(view);
        setUpStartTime(view);
        setUpEndTime(view);
        setUpCreateEventEdit(view);
        setUpPlaceEdit(view);
        setUpDescriptionEdit(view);

        return view;
    }

    private void setUpCreateEventEdit(View view) {
        (view.findViewById(R.id.ce_event_name)).
                setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (!hasFocus) {
                            hideKeyboard(v);
                        }
                    }
                });
    }

    private void setUpPlaceEdit(View view) {
        (view.findViewById(R.id.ec_hometown)).
                setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (!hasFocus) {
                            hideKeyboard(v);
                        }
                    }
                });
    }
    private void setUpDescriptionEdit(View view) {
        (view.findViewById(R.id.ce_event_description)).
                setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (!hasFocus) {
                            hideKeyboard(v);
                        }
                    }
                });
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
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
                        if(v.getId() == R.id.ec_start_date_button){
                            eventFromDate = new GregorianCalendar(year,monthOfYear,dayOfMonth);
                            dateFormat.setCalendar(eventFromDate);
                            dateFormatted = dateFormat.format(eventFromDate.getTime());
                        }
                        else{
                            eventToDate = new GregorianCalendar(year,monthOfYear,dayOfMonth);
                            dateFormat.setCalendar(eventToDate);
                            dateFormatted = dateFormat.format(eventToDate.getTime());
                        }

                        ((Button)v).setText(dateFormatted);

                    }
                }, mYear, mMonth, mDay);
        dpd.show();
    }


    private void showTimePickerDialog(final View v) {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        TimePickerDialog dpd = new TimePickerDialog(getActivity(),new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("K:mm a");
                String dateFormatted = null;
                if(v.getId() == R.id.ec_start_time_button){
                    eventFromTime = new GregorianCalendar(0,0,0,hourOfDay,minute);
                    dateFormat.setCalendar(eventFromTime);
                    dateFormatted = dateFormat.format(eventFromTime.getTime());
                }
                else{
                    eventToTime = new GregorianCalendar(0,0,0,hourOfDay,minute);
                    dateFormat.setCalendar(eventToTime);
                    dateFormatted = dateFormat.format(eventToTime.getTime());
                }

                ((Button)v).setText(dateFormatted);
            }
        },hour,minute,false);
        dpd.show();
    }

    private void setUpEndTime(View view) {
        eventToTimeButton.setText("Select");
        eventToTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventToTimeButton.setError(null);
                showTimePickerDialog(v);
            }
        });
    }

    private void setUpStartTime(View view) {
        eventFromTimeButton.setText("Select");
        eventFromTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventFromTimeButton.setError(null);
                showTimePickerDialog(v);
            }
        });
    }
    private void setUpStartDate(final View view) {
        eventFromDateButton.setText("Select");
        eventFromDateButton.
                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        eventFromDateButton.setError(null);
                        showDatePickerDialog(v);
                    }
                });
    }

    private void setUpEndDate(View view) {
        eventToDateButton.setText("Select");
        eventToDateButton.
                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        eventToDateButton.setError(null);
                        showDatePickerDialog(v);
                    }
                });
    }

    private void setUpSelectCity(View view) {
        citySelectButton.setText("Select");
        citySelectButton.
                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        countrySelectButton.setError(null);
                        if (selectedCountry == null) {
                            countrySelectButton.setError("Required Field!");
                            countrySelectButton.requestFocus();
                        } else {
                            openCityDialog(v);
                        }
                    }
                });
    }

    private void setUpSelectCountry(View view) {
        countrySelectButton.setText("Select");
        countrySelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countrySelectButton.setError(null);
                selectedCity = null;
                citySelectButton.setText("Select");
                openCountryDialog(v);
            }
        });
    }
    private void openCityDialog(final View v) {
        CityDialog cd = new CityDialog(getActivity(), selectedCountryCode);
        cd.addCityDialogListener(new CityDialog.CityDialogListener() {
            @Override
            public void onCitySelected(String countryName, String countryCode, String cityName) {
                ((Button)v).setText(cityName);
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
                ((Button)v).setText(countryName);
                selectedCountry = countryName;
                selectedCountryCode=countryCode;
            }
        });
        cd.show();
    }
}
