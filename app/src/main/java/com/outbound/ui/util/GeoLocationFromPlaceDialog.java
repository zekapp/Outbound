package com.outbound.ui.util;

import android.app.Dialog;
import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;

import com.outbound.R;
import com.outbound.model.Place;
import com.outbound.ui.util.adapters.PlacesAutoCompleteAdapter;
import com.outbound.util.PlacesService;
import com.parse.ParseGeoPoint;

/**
 * Created by zeki on 28/10/2014.
 */
public class GeoLocationFromPlaceDialog extends Dialog{

    private GeolocationDialogListener listener;
    private AutoCompleteTextView autoCompView;
    private Context context;
    private PlacesAutoCompleteAdapter mAdapter;
    private RetrievePlaceDetailTask mRetrievePlaceDetailTask;

    public interface GeolocationDialogListener{
        void onGeolocationOfSelectedItem(ParseGeoPoint location);
    }

    public void addGeoLocationDialogListener(GeolocationDialogListener listener){
        this.listener = listener;
    }
    public GeoLocationFromPlaceDialog(Context context) {
        super(context);
        this.context = context;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.geolocation_dialog);

        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);



        mAdapter = new PlacesAutoCompleteAdapter(context, R.layout.list_item);
        autoCompView = (AutoCompleteTextView)findViewById(R.id.auto_complete_text);
        autoCompView.setAdapter(mAdapter);


        autoCompView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PlacesAutoCompleteAdapter adptr = (PlacesAutoCompleteAdapter)parent.getAdapter();

                Place place = adptr.getPlaceItem(position);

                mRetrievePlaceDetailTask = new RetrievePlaceDetailTask();
                mRetrievePlaceDetailTask.execute(place.getReference());

                dismiss();
            }
        });
    }

    private class RetrievePlaceDetailTask extends AsyncTask<String, Void, Place> {

        @Override
        protected Place doInBackground(String... params) {
            String reference = params[0];
            Place detailedPlace = PlacesService.details(reference);
            return detailedPlace;
        }

        @Override
        protected void onPostExecute(Place place) {
            if(listener!=null) {
                if(place != null)
                    listener.onGeolocationOfSelectedItem(place.getLocation());
                else
                    listener.onGeolocationOfSelectedItem(null);
            }
        }
    }
}
