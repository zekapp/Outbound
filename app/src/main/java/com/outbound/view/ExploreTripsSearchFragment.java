package com.outbound.view;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import com.outbound.R;
import com.outbound.model.PTrip;
import com.outbound.ui.util.CityDialog;
import com.outbound.ui.util.CountryDialog;
import com.outbound.util.Constants;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import static com.outbound.util.LogUtils.*;

/**
 * Created by zeki on 29/09/2014.
 */
public class ExploreTripsSearchFragment extends BaseFragment {
    private static final String TAG = makeLogTag(ExploreTripsSearchFragment.class);

    private String selectedCountry = null;
    private String selectedCity = null;
    private String selectedCountryCode = null;

    private Button countrySelectButton;
    private Button citySelectButton;
    private Button tripFromDateButton;
    private Button tripToDateButton;

    private GregorianCalendar tripFromDate = null;
    private GregorianCalendar tripToDate = null;

    private Dialog progress;

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
        title.setText(getResources().getString(R.string.explore_trips_search_fragment_title));
        ImageView icon = (ImageView)viewActionBar.findViewById(R.id.ab_icon_1);
        icon.setImageResource(R.drawable.action_search);
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
                searchTrips();
            }
        });
    }
    private void startProgress(){
        progress = ProgressDialog.show(getActivity(),"","Trips Searching... ", true);
    }

    private void dissmissProgress(){
        progress.dismiss();
    }
    private void searchTrips() {
        ParseQuery<PTrip> query = ParseQuery.getQuery(PTrip.class);
        if(selectedCountry != null)
            query.whereEqualTo(PTrip.strCountry, selectedCountry);
        if(selectedCity !=null)
            query.whereEqualTo(PTrip.strCity, selectedCity);
        if(tripFromDate != null)
            query.whereGreaterThan(PTrip.strFromDate, tripFromDate.getTime());
        if (tripToDate != null)
            query.whereLessThanOrEqualTo(PTrip.strToDate, tripToDate.getTime());

        startProgress();
        query.findInBackground(new FindCallback<PTrip>() {
            @Override
            public void done(List<PTrip> pTrips, ParseException e) {
                dissmissProgress();
                if(e == null){
                    LOGD(TAG, "Trips count: " + Integer.toString(pTrips.size()));

                    if(mCallbacks != null)
                        mCallbacks.deployFragment(Constants.TRIPS_RESULT_FRAGMENT_ID, pTrips, null);
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        final View view = inflater.inflate(R.layout.explore_trips_add_or_search_fragment, container, false);
        ((TextView)view.findViewById(R.id.et_info)).setText(R.string.explore_trips_search_information);

        selectedCountry = null;
        selectedCity = null;
        selectedCountryCode = null;
        tripFromDate = null;
        tripToDate = null;

        countrySelectButton = (Button)view.findViewById(R.id.et_country_button);
        citySelectButton = (Button)view.findViewById(R.id.et_city_button);
        tripFromDateButton = (Button)view.findViewById(R.id.et_from_button);
        tripToDateButton = (Button)view.findViewById(R.id.et_to_button);

        setUpSelectCountry();
        setUpSelectCity();
        setUpFromDate();
        setUpToDate();
        return view;
    }

    private void showTimePickerDialog(final View v) {
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
                        if(v.getId() == R.id.et_from_button){
                            tripFromDate = new GregorianCalendar(year,monthOfYear,dayOfMonth);
                            dateFormat.setCalendar(tripFromDate);
                            dateFormatted = dateFormat.format(tripFromDate.getTime());
                        }
                        else{
                            tripToDate = new GregorianCalendar(year,monthOfYear,dayOfMonth);
                            dateFormat.setCalendar(tripToDate);
                            dateFormatted = dateFormat.format(tripToDate.getTime());
                        }

                        ((Button)v).setText(dateFormatted);

                    }
                }, mYear, mMonth, mDay);
        dpd.show();
    }

    private void setUpToDate() {
        tripToDateButton.setText("Select");
        tripToDateButton.
                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showTimePickerDialog(v);
                    }
                });
    }

    private void setUpFromDate() {
        tripFromDateButton.setText("Select");
        tripFromDateButton.
                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showTimePickerDialog(v);
                    }
                });
    }

    private void setUpSelectCity() {
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

    private void setUpSelectCountry() {
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
                selectedCountryCode = countryCode;
                selectedCountry = countryName;
            }
        });
        cd.show();
    }
}
