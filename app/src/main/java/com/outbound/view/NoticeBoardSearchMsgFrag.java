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
import com.outbound.ui.util.CitySelectDialog;
import com.outbound.ui.util.CountrySelectDialog;
import com.outbound.util.Constants;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import static com.outbound.util.LogUtils.LOGD;
import static com.outbound.util.LogUtils.makeLogTag;

/**
 * Created by zeki on 1/10/2014.
 */
public class NoticeBoardSearchMsgFrag extends BaseFragment {
    private static final String TAG = makeLogTag(NoticeBoardSearchMsgFrag.class);
    private Button countrySelectButton;
    private Button citySelectButton;
    private Button travellerTypeSelButton;
    private EditText postPlaceEdit;

    //Traveller Type
    private String[] travellerTypeList;
    private boolean[] trvTypeBooleanList;
    private ArrayList<Integer> travSelList = new ArrayList<Integer>();
    private List<String> selectedTravellerType = new ArrayList<String>();

    private String place = "";
    private String selectedCity = null;
    private String selectedCountry = null;
    private String selectedCountryCode = null;

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
        title.setText(getResources().getString(R.string.notice_board_fragment_search_message_title));
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
                if(mCallbacks !=null)
                    mCallbacks.backIconClicked();
            }
        });

        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attempToFindPosts();
            }
        });
    }

    private void attempToFindPosts() {
        resetErrors();

        boolean cancel = false;
        View focusView = null;

        place = postPlaceEdit.getText().toString();

        if(selectedCountry == null){
            countrySelectButton.setError("Country required.");
            focusView = countrySelectButton;
            cancel = true;
        }

        if(cancel){
            focusView.requestFocus();
        }else
        {
            searchPosts();
        }
    }

    private void searchPosts() {
        ParseQuery<PNoticeBoard> query = ParseQuery.getQuery(PNoticeBoard.class);
        query.whereEqualTo(PNoticeBoard.country, selectedCountry);
        if(selectedCity != null)
            query.whereEqualTo(PNoticeBoard.city, selectedCity);
        if(!place.isEmpty())
            query.whereStartsWith(PNoticeBoard.noticeBoardPlace, place);
        if(!selectedTravellerType.isEmpty())
            query.whereContainedIn(PNoticeBoard.travellerType, selectedTravellerType);

        startProgress("Notices are searching...");
        PNoticeBoard.findPostsWhitSearcRequest(query, new FindCallback<PNoticeBoard>() {
            @Override
            public void done(List<PNoticeBoard> pNoticeBoards, ParseException e) {
                stopProgress();
                if(e == null){
                    if(pNoticeBoards.size() > 0){
                        showToastMessage(pNoticeBoards.size() + " notices found");
                        if (mCallbacks != null)
                            mCallbacks.deployFragment(Constants.SEARCH_NOTICE_RESULT_FRAG_ID, pNoticeBoards, null);
                    }else{
                        showToastMessage("No notices found");
                    }
                }else{
                    showToastMessage("Network Error. Check connection.");
                    LOGD(TAG, "attempToSearch  e:" + e.getMessage());
                }
            }
        });

    }

    private void resetErrors() {
        countrySelectButton.setError(null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        final View view = inflater.inflate(R.layout.notice_board_search_message, container, false);

        travellerTypeList = getActivity().getResources().getStringArray(R.array.traveller_type);
        trvTypeBooleanList = new boolean[travellerTypeList.length];

        postPlaceEdit = (EditText)view.findViewById(R.id.sm_place);
        citySelectButton = (Button)view.findViewById(R.id.sm_city_button);
        travellerTypeSelButton = (Button)view.findViewById(R.id.sm_traveller_type_button);
        countrySelectButton = (Button)view.findViewById(R.id.sm_country_button);

        selectedTravellerType.clear();
        place = null;
        selectedCity = null;
        selectedCountry = null;
        selectedCountryCode = null;


        setUpSelectCountry();
        setUpSelectCity();
        setUpSelectTravellerType();

        return view;
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
                selectedCountryCode=countryCode;
            }
        });
        cd.show();
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

    private void setUpSelectTravellerType() {
        travellerTypeSelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTravellerDialog(v);
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
}
