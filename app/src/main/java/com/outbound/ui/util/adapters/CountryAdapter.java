package com.outbound.ui.util.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.outbound.R;
import com.outbound.model.Country;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by zeki on 6/10/2014.
 */
public class CountryAdapter extends ArrayAdapter<Country> {

    private LayoutInflater inflater;
    private ArrayList<Country> mCountryArrayList;
    private ArrayList<Country> originalCountryList;

    public CountryAdapter(Context context, ArrayList<Country> countryArrayList) {
        super(context, 0, countryArrayList);
        inflater = (LayoutInflater) getContext().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);

        this.mCountryArrayList = new ArrayList<Country>();
        this.mCountryArrayList.addAll(countryArrayList);
        this.originalCountryList = new ArrayList<Country>();
        this.originalCountryList.addAll(countryArrayList);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        Country country = getItem(position);
        View rowView = inflater.inflate(R.layout.country_list_item, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.country_name);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.country_flag);
        textView.setText(country.getName());
        imageView.setImageResource(getContext().getResources().
                getIdentifier("drawable/" + country.getCode().toLowerCase(),
                        null, getContext().getPackageName()));
/*
        ViewHolder holder;

        // If a view hasn't been provided inflate on
        if(view == null){

            Country country = getItem(position);
            view = inflater.inflate(R.layout.country_list_item, parent, false);
            holder = new ViewHolder();
            holder.countryName = (TextView)view.findViewById(R.id.country_name);
            holder.countryFlag = (ImageView)view.findViewById(R.id.country_flag);

            holder.countryName.setText(country.getName());
            holder.countryFlag.setImageResource(getContext().getResources().
                    getIdentifier("drawable/" + country.getCode().toLowerCase(),
                            null, getContext().getPackageName()));
            // Tag for lookup later
            view.setTag(holder);
        }else
        {
            holder = (ViewHolder) view.getTag();
        }
*/

        return rowView;
    }

    @Override
    public Country getItem(int position) {
        return mCountryArrayList.get(position);
    }

    @Override
    public int getCount() {
        return mCountryArrayList.size();
    }

    private static class ViewHolder {
        TextView countryName;
        ImageView countryFlag;
    }


    public void filterData(String query) {
        query = query.toLowerCase();
        mCountryArrayList.clear();

        if(query.isEmpty()){
            mCountryArrayList.addAll(originalCountryList);
        }else
        {
            for(Country country:originalCountryList){
                if(country.getName().toLowerCase().contains(query)){
                    mCountryArrayList.add(country);
                }
            }
        }
        notifyDataSetChanged();
    }
}
