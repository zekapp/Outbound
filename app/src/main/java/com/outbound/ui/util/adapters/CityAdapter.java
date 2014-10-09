package com.outbound.ui.util.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.outbound.R;
import com.outbound.model.City;
import com.outbound.model.Country;

import java.util.ArrayList;

/**
 * Created by zeki on 9/10/2014.
 */
public class CityAdapter extends ArrayAdapter<City> {

    private LayoutInflater inflater;
    private ArrayList<City> mCityArrayList;
    private ArrayList<City> originalCityList;


    public CityAdapter(Context context, ArrayList<City> cityArrayList) {
        super(context, 0, cityArrayList);
        inflater = (LayoutInflater) getContext().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);

        this.mCityArrayList = new ArrayList<City>();
        this.mCityArrayList.addAll(cityArrayList);
        this.originalCityList = new ArrayList<City>();
        this.originalCityList.addAll(cityArrayList);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        City city = getItem(position);
        View rowView = inflater.inflate(R.layout.city_list_item, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.city_name);
        textView.setText(city.getCityName());

        return rowView;
    }

    @Override
    public City getItem(int position) {
        return mCityArrayList.get(position);
    }

    @Override
    public int getCount() {
        return mCityArrayList.size();
    }

    public void filterData(String query) {
        query = query.toLowerCase();
        mCityArrayList.clear();

        if(query.isEmpty()){
            mCityArrayList.addAll(originalCityList);
        }else
        {
            for(City city:originalCityList){
                if(city.getCityName().toLowerCase().contains(query)){
                    mCityArrayList.add(city);
                }
            }
        }
        notifyDataSetChanged();
    }
}
