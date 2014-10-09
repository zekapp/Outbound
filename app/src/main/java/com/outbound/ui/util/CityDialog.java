package com.outbound.ui.util;

import android.app.Dialog;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import com.outbound.R;
import com.outbound.model.City;
import com.outbound.ui.util.adapters.CityAdapter;
import com.outbound.util.LogUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import au.com.bytecode.opencsv.CSVReader;

import static com.outbound.util.LogUtils.*;

/**
 * Created by zeki on 9/10/2014.
 */
public class CityDialog extends Dialog{
    private static final String TAG = makeLogTag(CityDialog.class);

    private CityAdapter mAdapter;
    private String countryCode;
    private SearchView search;

    public interface CityDialogListener {
        void onCitySelected(String countryName, String countryCode, String cityName);
    }

    private final List<CityDialogListener> listeners = new LinkedList<CityDialogListener>();

    public CityDialog(Context context, String selectedCountryCode) {
        super(context);
        countryCode = selectedCountryCode;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.city_dialog);
        setUpListView();
        setUpSearch();
    }

    private void setUpSearch() {
        search = (SearchView) findViewById(R.id.search_country);
        search.setIconifiedByDefault(false);
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(mAdapter != null)
                    mAdapter.filterData(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                if(mAdapter != null)
                    mAdapter.filterData(query);
                return false;
            }
        });
        search.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                if(mAdapter != null)
                    mAdapter.filterData("");
                return false;
            }
        });
    }

    private void setUpAdapter() {
//        ArrayList<City> cityList = generateCityList();
        ArrayList<City> cityList = generateCityListFrom(countryCode);
        mAdapter = new CityAdapter(getContext(), cityList);
    }

    private ArrayList<City> generateCityListFrom(String countryCode) {
        return null;
    }

    private ArrayList<City> generateCityList() {
        String next[] = {};
        ArrayList<City> cityArrayList = new ArrayList<City>();
        try{
            AssetManager mng = getContext().getAssets();
            InputStream is = mng.open("GeoLiteCityLocation.csv");
            CSVReader reader = new CSVReader(new InputStreamReader(is));

            for(;;){
                next = reader.readNext();
                if(next != null){
                    if(next[0].compareTo(countryCode) == 0){
                        City city = new City();
                        city.setCityName(next[1]);
                        cityArrayList.add(city);
                    }
                }
            }
        }catch(IOException ioe){
            LOGD(TAG,"generateCityList: " + ioe.getMessage());
        }

        return cityArrayList;
    }

    private void setUpListView() {
        ListView ltv = (ListView)findViewById(R.id.list_view_city);
        setUpAdapter();
        ltv.setAdapter(mAdapter);
        ltv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for (CityDialogListener listener : listeners) {
                    if (listener != null) {
                        City city = (City)parent.getAdapter().getItem(position);
                        listener.onCitySelected(new Locale("",countryCode).getDisplayName(), countryCode, city.getCityName());
                        dismiss();
                    }
                }
            }
        });
    }

    public void addCityDialogListener(CityDialogListener listener) {
        listeners.add(listener);
    }
}
