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
import android.widget.Toast;

import com.outbound.R;
import com.outbound.model.PTrip;
import com.outbound.ui.util.CityDialog;
import com.outbound.ui.util.CountryDialog;
import com.outbound.util.Constants;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import static com.outbound.util.LogUtils.LOGD;
import static com.outbound.util.LogUtils.makeLogTag;

/**
 * Created by zeki on 29/09/2014.
 */
public class ExploreTripsAddFragment extends BaseFragment {
    private static final String TAG = makeLogTag(ExploreTripsAddFragment.class);

    private Dialog progress;

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
        title.setText(getResources().getString(R.string.explore_trips_add_fragment_title));
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
                addTrip();
            }
        });
    }

    private String selectedCountry = null;
    private String selectedCity = null;
    private String selectedCountryCode = null;
    private GregorianCalendar tripFromDate = null;
    private GregorianCalendar tripToDate = null;


    private Button countrySelectButton;
    private Button citySelectButton;
    private Button tripFromDateButton;
    private Button tripToDateButton;


    private void addTrip() {

        resetErrors();

        boolean cancel = false;
        View focusView = null;

        if(selectedCountry == null || selectedCountryCode == null){
            countrySelectButton.setError("Country required.");
            focusView = countrySelectButton;
            cancel = true;
        }
        if(selectedCity == null){
            citySelectButton.setError("City required.");
            focusView = citySelectButton;
            cancel = true;
        }
        if(tripFromDate == null){
            tripFromDateButton.setError("Date required.");
            focusView = tripFromDateButton;
            cancel = true;
        }
        if(tripToDate == null){
            tripToDateButton.setError("Date required.");
            focusView = tripFromDateButton;
            cancel = true;
        }

        if(!cancel){
            startProgress();
            showToasMessege("Your Trip saving database...");
            PTrip.addTrip(selectedCity,selectedCountry,tripFromDate.getTime(),tripToDate.getTime(),new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if(e == null){
                        showToasMessege("Your Trip saved database");
                        if(mCallbacks != null){
                            dissmissProgress();
                            mCallbacks.deployFragment(Constants.TRIPS_RESULT_FRAGMENT_ID,null,null);
                        }
                    }else {
                        showToasMessege("Network Error. Check your connection...");
                    }
                    dissmissProgress();
                }
            });
        }else
            focusView.requestFocus();
    }

    private void showToasMessege(String message){
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    private void resetErrors() {
        countrySelectButton.setError(null);
        citySelectButton.setError(null);
        tripFromDateButton.setError(null);
        tripToDateButton.setError(null);
    }

    private void startProgress(){
        progress = ProgressDialog.show(getActivity(), "", "Your Trip adding database... ", true);
    }
    private void dissmissProgress(){
        progress.dismiss();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setUpActionBar(getActivity());

        final View view = inflater.inflate(R.layout.explore_trips_add_or_search_fragment, container, false);
        ((TextView)view.findViewById(R.id.et_info)).setText(R.string.explore_trips_add_information);


        selectedCountry = null;
        selectedCity = null;
        selectedCountryCode = null;
        tripFromDate = null;
        tripToDate = null;

        countrySelectButton = (Button)view.findViewById(R.id.et_country_button);
        citySelectButton = (Button)view.findViewById(R.id.et_city_button);
        tripFromDateButton = (Button)view.findViewById(R.id.et_from_button);
        tripToDateButton = (Button)view.findViewById(R.id.et_to_button);

        setUpSelectCountry(view);
        setUpSelectCity(view);
        setUpFromDate(view);
        setUpToDate(view);
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

    private void setUpToDate(final View v) {
        tripToDateButton.setText("Select");
        tripToDateButton.
                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showTimePickerDialog(v);
                    }
                });
    }

    private void setUpFromDate(View v) {
        tripFromDateButton.setText("Select");
        tripFromDateButton.
                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showTimePickerDialog(v);
                    }
                });
    }

    private void setUpSelectCity(View v) {
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

    private void setUpSelectCountry(final View v) {
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
