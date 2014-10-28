package com.outbound.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.outbound.R;
import com.outbound.model.PWifiSpot;
import com.outbound.model.WifiSpot;
import com.outbound.ui.util.adapters.WifiSpotAdapter;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;

import static com.outbound.util.LogUtils.*;

/**
 * Created by zeki on 9/10/2014.
 */
public class DBManager {
    private static final String TAG = makeLogTag(DBManager.class);

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Context context;
    private DataFetchingTask mAuthTask = null;
    private DatabaseInitTask mInitTask = null;

    private DbInitListener dbInitListener;

    public void saveWifiSpots(List<WifiSpot> list, int adapterIndex) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(context.getString(R.string.preferences_key_wifi_spots)+Integer.toString(adapterIndex),json);
        editor.commit();
    }

    public List<WifiSpot> getWifiSpots(int adapterIndex) {
        Gson gson = new Gson();
        String json = preferences.getString(
                context.getString(R.string.preferences_key_wifi_spots)+Integer.toString(adapterIndex),"");
        return gson.fromJson(json, new TypeToken<List<WifiSpot>>(){}.getType());
    }

    public interface  DbInitListener{
        void onDbInit(boolean res);
    }

    public interface DbFetchingListener {
        void onCitiesFetched(ArrayList<String> cities);
        void onError(String e);
    }

    private final List<DbFetchingListener> listeners = new LinkedList<DbFetchingListener>();

    public DBManager(Context contex){
        this.context = contex;
        this.preferences = PreferenceManager.getDefaultSharedPreferences(contex);
        this.editor =  preferences.edit();
    }


    public void setAsDbInitialized(boolean res) {
        editor.putBoolean(context.getString(R.string.preferences_key_is_first_init), res);
        editor.commit();
    }
    public boolean isFirstInitialize() {
        return preferences.getBoolean(
                context.getString(R.string.preferences_key_is_first_init), true);
    }

    public void addGenerateDbForCityCountryListener( DbInitListener listener) {
        dbInitListener = listener;

        mInitTask = new DatabaseInitTask(context);
        mInitTask.execute((Void) null);

    }

    public void addCityFetcherListener(String countryCode, DbFetchingListener listener) {
        listeners.add(listener);

        mAuthTask = new DataFetchingTask(countryCode);
        mAuthTask.execute((Void) null);

    }

    public class DatabaseInitTask extends AsyncTask<Void, Void, Boolean >{

        private Charset UTF_8 = Charset.forName("UTF-8");
        private Charset ISO_8859_1 = Charset.forName("ISO-8859-1");

        private Context context;
        DatabaseInitTask(Context context ) {
            this.context = context;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            String next[] = {};

            HashMap<String, ArrayList<String>> hashMap = new HashMap<String, ArrayList<String>>();
            try{
                AssetManager mng = context.getAssets();
                InputStream is = mng.open("GeoLiteCityLocation.csv");
                CSVReader reader = new CSVReader(new InputStreamReader(is));
                for(;;){
                    next = reader.readNext();
                    if(next != null){
                        next[0] = new String(next[0].getBytes(ISO_8859_1), UTF_8);
                        next[1] = new String(next[1].getBytes(ISO_8859_1), UTF_8);

                        ArrayList<String> arrayList = hashMap.get(next[0]);
                        if(arrayList == null){
                            arrayList = new ArrayList<String>();
                            arrayList.add(next[1]);
                            hashMap.put(next[0],arrayList);
                        }else
                        {
                            arrayList.add(next[1]);
                            hashMap.put(next[0],arrayList);
                        }
                    }else
                        break;
                }

                for(String key:hashMap.keySet()){
                    saveCitiesOfCountry(key, hashMap.get(key));
                }
                return true;
            }catch (Exception e){
                LOGD(TAG,"DatabaseInitTask-doInBackground: " + e.getMessage());
                return false;
            }
        }

        private void saveCitiesOfCountry(String countryCode, ArrayList<String> cityList){
            Gson gson = new Gson();
            String json = gson.toJson(cityList);
            editor.putString(context.getString(R.string.preferences_key_country)+countryCode,json);
            editor.commit();
        }

        @Override
        protected void onPostExecute(Boolean res) {
            super.onPostExecute(res);
            dbInitListener.onDbInit(res);
        }
    }

    public class DataFetchingTask extends AsyncTask<Void, Void, ArrayList<String> >{

        private String countryCode;
        DataFetchingTask(String countryCode ) {
            this.countryCode = countryCode;
        }

        @Override
        protected ArrayList<String>  doInBackground(Void... params) {
            try{
                Gson gson = new Gson();
                String json = preferences.getString(context.getString(R.string.preferences_key_country)+countryCode, "");
                return gson.fromJson(json, new TypeToken<ArrayList<String>>(){}.getType());
            }catch (Exception e){
                LOGD(TAG, "doInBackground: " + e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<String> cities) {
            super.onPostExecute(cities);
            mAuthTask = null;
            if(cities != null){
                if(cities.size() > 0 ){
                    for (DbFetchingListener listener : listeners) {
                        if (listener != null) {
                            listener.onCitiesFetched(cities);
                        }
                    }
                }else
                {
                    for (DbFetchingListener listener : listeners) {
                        if (listener != null) {
                            listener.onError("Database: Cities is empty");
                            LOGD(TAG, "onPostExecute: Database Cities is empty");
                        }
                    }
                }
            }else{
                for (DbFetchingListener listener : listeners) {
                    if (listener != null) {
                        listener.onError("Database: Fetching Error");
                        LOGD(TAG, "onPostExecute: Database Fetching Error");
                    }
                }
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            mAuthTask = null;
        }
    }
}
