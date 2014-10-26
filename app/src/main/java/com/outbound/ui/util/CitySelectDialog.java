package com.outbound.ui.util;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import com.outbound.R;
import com.outbound.ui.util.adapters.CityAdapter;
import com.outbound.util.DBManager;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import static com.outbound.util.LogUtils.*;

/**
 * Created by zeki on 9/10/2014.
 */
public class CitySelectDialog extends Dialog{
    private static final String TAG = makeLogTag(CitySelectDialog.class);

    private CityAdapter mAdapter;
    private String countryCode;
    private SearchView search;
    private Dialog progressDialog;

    public interface CityDialogListener {
        void onCitySelected(String countryName, String countryCode, String cityName);
    }

    private final List<CityDialogListener> listeners = new LinkedList<CityDialogListener>();

    public void addCityDialogListener(CityDialogListener listener) {
        listeners.add(listener);
    }

    public CitySelectDialog(Context context, String selectedCountryCode) {
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
        search = (SearchView) findViewById(R.id.search_city);
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

    private void setUpAdapter(final ListView ltv) {
        CitySelectDialog.this.progressDialog = ProgressDialog.show(
                getContext(), "", "Fetching Cities...", true);

        DBManager db = new DBManager(getContext());
        db.addCityFetcherListener(countryCode, new DBManager.DbFetchingListener() {
            @Override
            public void onCitiesFetched(ArrayList<String> cities) {
                mAdapter = new CityAdapter(getContext(), cities);
                ltv.setAdapter(mAdapter);
                dismissProgress();
            }

            @Override
            public void onError(String e) {
                dismissProgress();
            }
        });
    }

    private void dismissProgress() {
        CitySelectDialog.this.progressDialog.dismiss();
    }

//    private ArrayList<City> generateCityList() {
//        String next[] = {};
//        ArrayList<City> cityArrayList = new ArrayList<City>();
//        try{
//            AssetManager mng = getContext().getAssets();
//            InputStream is = mng.open("GeoLiteCityLocation.csv");
//            CSVReader reader = new CSVReader(new InputStreamReader(is));
//
//            for(;;){
//                next = reader.readNext();
//                if(next != null){
//                    if(next[0].compareTo(countryCode) == 0){
//                        City city = new City();
//                        city.setCityName(next[1]);
//                        cityArrayList.add(city);
//                    }
//                }else
//                    break;
//            }
//        }catch(IOException ioe){
//            LOGD(TAG,"generateCityList: " + ioe.getMessage());
//        }
//
//        return cityArrayList;
//    }

    private void setUpListView() {
        ListView ltv = (ListView)findViewById(R.id.list_view_city);
        setUpAdapter(ltv);
        ltv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for (CityDialogListener listener : listeners) {
                    if (listener != null) {
                        String city = (String)parent.getAdapter().getItem(position);
                        listener.onCitySelected(new Locale("",countryCode).getDisplayName(), countryCode, city);
                        dismiss();
                    }
                }
            }
        });
    }


}
