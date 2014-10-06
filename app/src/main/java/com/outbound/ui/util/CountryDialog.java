package com.outbound.ui.util;

import android.app.Activity;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;

import com.outbound.R;
import com.outbound.model.Country;
import com.outbound.ui.util.adapters.CountryAdapter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * Created by zeki on 6/10/2014.
 */
public class CountryDialog extends Dialog {

    private CountryAdapter mAdapter;
    private SearchView search;


    public interface CountryDialogListener {
        void onCountrySelected(String countryName, String countryCode);
    }

    private final List<CountryDialogListener> listeners = new LinkedList<CountryDialogListener>();

    public CountryDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.country_dialog);
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
        ArrayList<Country> countryList = generateCountryList(getContext().getResources().getStringArray(R.array.CountryCodes));
        mAdapter = new CountryAdapter(getContext(), countryList);
    }

    private ArrayList<Country> generateCountryList(String[] stringArray) {

        ArrayList<Country> countryArrayListList = new ArrayList<Country>();
        for (int i=0; i<stringArray.length;i++ ){
            Country country = new Country();
            String[] countryCode = stringArray[i].split(",");
            country.setCode(countryCode[1].trim());
            country.setName(GetCountryZipCode(countryCode[1]).trim());
            countryArrayListList.add(country);
        }

        return countryArrayListList;
    }

    private String GetCountryZipCode(String ssid){
        Locale loc = new Locale("", ssid);

        return loc.getDisplayCountry().trim();
    }
    private void setUpListView() {
        ListView ltv = (ListView)findViewById(R.id.list_view_country);

        setUpAdapter();

        ltv.setAdapter(mAdapter);

        ltv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for (CountryDialogListener listener : listeners) {
                    if (listener != null) {
                        Country country = (Country)parent.getAdapter().getItem(position);
                        listener.onCountrySelected(country.getName(), country.getCode());
                        dismiss();
                    }
                }
            }
        });
    }


    public void addCountryDialogListener(CountryDialogListener listener) {
        listeners.add(listener);
    }
}
