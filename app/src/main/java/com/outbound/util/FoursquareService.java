package com.outbound.util;

import android.os.AsyncTask;

import com.outbound.model.Place;
import com.outbound.model.WifiSpot;
import com.parse.ParseGeoPoint;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by zeki on 29/10/2014.
 */
public class FoursquareService {

    private static final String API_URL = "https://api.foursquare.com/v2";
    public static final String CLIENT_ID = "MRGOIQVNT5H1XWCRMTOUIQEPN4CU1K1NJDBJTNXYJBWFDSFW";
    public static final String CLIENT_SECRET = "UNGX1SAH2I2TVM4PZUBZ4Y3AO2BZPLO414ESB0YPJ35TEIOH";

    private static RetrievePlaceDetailTask mRetrievePlaceDetailTask = null;
    public static void fetchPlacesNearMe(ParseGeoPoint currentLocation, final PlacesCallback<WifiSpot> placesCallback) {
        mRetrievePlaceDetailTask = new RetrievePlaceDetailTask(placesCallback);
        mRetrievePlaceDetailTask.execute(currentLocation);
    }

    private static class RetrievePlaceDetailTask extends AsyncTask<ParseGeoPoint, Void, List<WifiSpot>>{

        private PlacesCallback<WifiSpot> listener;
        RetrievePlaceDetailTask(PlacesCallback<WifiSpot> placesCallback){
            super();
            this.listener = placesCallback;
        }
        @Override
        protected List<WifiSpot> doInBackground(ParseGeoPoint... params) {
            ArrayList<WifiSpot> venueList = new ArrayList<WifiSpot>();

            ParseGeoPoint location = params[0];

            double latitude= location.getLatitude();
            double longitude = location.getLongitude();
            HttpURLConnection conn = null;
            try{
                String v	= timeMilisToString(System.currentTimeMillis());
                String ll 	= String.valueOf(latitude) + "," + String.valueOf(longitude);
                URL url 	= new URL(API_URL + "/venues/search?client_id="+CLIENT_ID+"&client_secret="+CLIENT_SECRET+"&ll=" + ll + "&v=" + v);

                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                //urlConnection.setDoOutput(true);

                conn.connect();

                String response		= streamToString(conn.getInputStream());
                JSONObject jsonObj 	= (JSONObject) new JSONTokener(response).nextValue();

                JSONArray venues	= (JSONArray) jsonObj.getJSONObject("response").getJSONArray("venues");

                int length			= venues.length();

                if (length > 0) {
                    for (int i = 0; i < length; i++) {
                        WifiSpot wifiSpot 	= new WifiSpot();
                        JSONObject venue 	= (JSONObject) venues.get(i);

                        //Wifi Name
                        wifiSpot.setWifiName(venue.getString("name"));

                        JSONObject locationJson = venue.getJSONObject("location");

                        //Address
                        String address = "";
                        if(locationJson.has("formattedAddress")){
                            JSONArray addresArray = locationJson.getJSONArray("formattedAddress");
                            int size = addresArray.length();
                            for (int j = 0; j < size; j++) {
                                address = address + addresArray.getString(j);
                                if(j != (size -1))
                                    address = address + ", ";
                            }
                        }
                        else if(locationJson.has("address"))
                            address = locationJson.getString("address");

                        wifiSpot.setWifiAddress(address);

                        // Geo Location
                        double lat = Double.valueOf(locationJson.getString("lat"));
                        double lng = Double.valueOf(locationJson.getString("lng"));
                        wifiSpot.setWifiLocation(new ParseGeoPoint(lat,lng));

                        //Type
                        wifiSpot.setWifiType("");
                        venueList.add(wifiSpot);
                    }
                    return venueList;
                }else
                    return null;
            }catch (Exception ex){
                return null;
            }finally{
                if (conn != null) {
                    conn.disconnect();
                }
            }
        }

        @Override
        protected void onPostExecute(List<WifiSpot> wifiSpots) {
            if(listener!=null){
                if(wifiSpots != null)
                    listener.done(wifiSpots,null);
                else
                    listener.done(null, new Exception("no result found"));
            }
        }

        private String streamToString(InputStream is) throws IOException {
            String str  = "";

            if (is != null) {
                StringBuilder sb = new StringBuilder();
                String line;

                try {
                    BufferedReader reader 	= new BufferedReader(new InputStreamReader(is));

                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }

                    reader.close();
                } finally {
                    is.close();
                }

                str = sb.toString();
            }

            return str;
        }

        private String timeMilisToString(long milis) {
            SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd");
            Calendar calendar   = Calendar.getInstance();

            calendar.setTimeInMillis(milis);

            return sd.format(calendar.getTime());
        }
    }
}
