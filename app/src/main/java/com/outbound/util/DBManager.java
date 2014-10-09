package com.outbound.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.outbound.R;
import com.outbound.model.City;

import java.util.ArrayList;

/**
 * Created by zeki on 9/10/2014.
 */
public class DBManager {

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Context context;

    public DBManager(Context contex){
        this.context = contex;
        this.preferences = PreferenceManager.getDefaultSharedPreferences(contex);
        this.editor =  preferences.edit();
    }

    public void saveCitiesOfCountry(String countryCode, ArrayList<City> cityList){
        Gson gson = new Gson();
        String json = gson.toJson(cityList);
        editor.putString(context.getString(R.string.preferences_key_country)+countryCode,json);
        editor.commit();
    }

    public ArrayList<City> getCitiesOfCountry(String countryCode){
        Gson gson = new Gson();
        String json = preferences.getString(context.getString(R.string.preferences_key_country)+countryCode, "");
        ArrayList<City> mCityArrayList =  gson.fromJson(json, new TypeToken<ArrayList<City>>(){}.getType());
        return mCityArrayList ;
    }
}
