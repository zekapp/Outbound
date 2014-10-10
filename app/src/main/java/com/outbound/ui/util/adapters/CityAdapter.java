package com.outbound.ui.util.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.outbound.R;

import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * Created by zeki on 9/10/2014.
 */
public class CityAdapter extends ArrayAdapter<String> {

    private LayoutInflater inflater;
    private ArrayList<String> mCityArrayList;
    private ArrayList<String> originalCityList;


    public CityAdapter(Context context, ArrayList<String> cityArrayList) {
        super(context, 0, cityArrayList);
        inflater = (LayoutInflater) getContext().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);

        this.mCityArrayList = new ArrayList<String>();
        this.mCityArrayList.addAll(cityArrayList);
        this.originalCityList = new ArrayList<String>();
        this.originalCityList.addAll(cityArrayList);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

//        Charset UTF_8 = Charset.forName("UTF-8");
//        Charset ISO_8859_1 = Charset.forName("ISO-8859-1");
//
//        String city = new String(getItem(position).getBytes(ISO_8859_1), UTF_8);
        String city = getItem(position);

        View rowView = inflater.inflate(R.layout.city_list_item, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.city_name);
        textView.setText(city);

        return rowView;
    }

    @Override
    public String getItem(int position) {
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
            for(String city:originalCityList){
                if(city.toLowerCase().contains(query)){
                    mCityArrayList.add(city);
                }
            }
        }
        notifyDataSetChanged();
    }
}
