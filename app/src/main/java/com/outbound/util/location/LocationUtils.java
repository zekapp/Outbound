/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.outbound.util.location;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;
import com.outbound.model.PUser;
import com.outbound.util.GeoCodeCallback;
import com.parse.ParseGeoPoint;

import java.text.ParseException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Defines app-wide constants and utilities
 */
public final class LocationUtils {

    // Debugging tag for the application
    public static final String APPTAG = "LocationSample";

    // Name of shared preferences repository that stores persistent state
    public static final String SHARED_PREFERENCES =
            "com.outbound.location.SHARED_PREFERENCES";

    // Key for storing the "updates requested" flag in shared preferences
    public static final String KEY_UPDATES_REQUESTED =
            "com.outbound.location.KEY_UPDATES_REQUESTED";

    /*
     * Define a request code to send to Google Play services
     * This code is returned in Activity.onActivityResult
     */
    public final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    /*
     * Constants for location update parameters
     */
    // Milliseconds per second
    public static final int MILLISECONDS_PER_SECOND = 1000;

    // The update interval
    public static final int UPDATE_INTERVAL_IN_SECONDS = 25;

    // A fast interval ceiling
    public static final int FAST_CEILING_IN_SECONDS = 1;

    // Update interval in milliseconds
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS =
            MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;

    // A fast ceiling of update intervals, used when the app is visible
    public static final long FAST_INTERVAL_CEILING_IN_MILLISECONDS =
            MILLISECONDS_PER_SECOND * FAST_CEILING_IN_SECONDS;

    // Create an empty string for initializing strings
    public static final String EMPTY_STRING = new String();


    public static List<PUser> orderFriendsAccordingDistance(List<PUser> pUsers) {
        final PUser currentUser = PUser.getCurrentUser();
        Collections.sort(pUsers, new Comparator<PUser>() {
            @Override
            public int compare(PUser lhs, PUser rhs) {

                ParseGeoPoint lhsCurrentLoc = lhs.getCurrentLocation();
                if(lhsCurrentLoc == null)
                    lhsCurrentLoc = new ParseGeoPoint(0,0);
                ParseGeoPoint rhsCurrentLoc = rhs.getCurrentLocation();
                if(rhsCurrentLoc == null)
                    rhsCurrentLoc = new ParseGeoPoint(0,0);

                if(currentUser.getCurrentLocation() == null)
                    return 0;
                else {
                    Double dist1 = lhsCurrentLoc.distanceInKilometersTo(currentUser.getCurrentLocation());
                    Double dist2 = rhsCurrentLoc.distanceInKilometersTo(currentUser.getCurrentLocation());

                    return dist1.compareTo(dist2);
                }
            }
        });
        return pUsers;
    }

    public static void findGeoLocationFromAddress(String[] addr, Context context, GeoCodeCallback callback ){
        GeoLocationTask geoLocationTask = new GeoLocationTask(context,callback);
        geoLocationTask.execute(addr);
    }

    private static class GeoLocationTask extends AsyncTask<String[], Void, ParseGeoPoint>{
        private  GeoCodeCallback callback;
        private Context localContext;

        public GeoLocationTask(Context context,GeoCodeCallback cb) {
            // Required by the semantics of AsyncTask
            super();
            localContext = context;
            callback = cb;
        }

        @Override
        protected ParseGeoPoint doInBackground(String[]... params) {
            String[] addr = params[0];
            if(addr[0] == null){
                addr[0] = "";
            }
            if(addr[1] == null){
                addr[1] = "";
            }
            if(addr[2] == null){
                addr[2] = "";
            }

            String place = addr[0] + " " + addr[1] + " " + addr[2];

            ParseGeoPoint latLng = new ParseGeoPoint();
            try {
                Geocoder selected_place_geocoder = new Geocoder(localContext);
                List<Address> address;

                address = selected_place_geocoder.getFromLocationName(place, 5);

                if (address == null) {
//                    d.dismiss();
                } else {
                    Address location = address.get(0);
                    latLng.setLatitude(location.getLatitude());
                    latLng.setLongitude(location.getLongitude());
                }

                return latLng;

            } catch (Exception e) {
                e.printStackTrace();
//                FetchLatLongFromService fetch_latlng_from_service_abc = new FetchLatLongFromService(
//                        place.replaceAll("\\s+", ""));
//                fetch_latlng_from_service_abc.execute();
                return null;

            }
        }

        @Override
        protected void onPostExecute(ParseGeoPoint parseGeoPoint) {
            if(parseGeoPoint == null){
                callback.done(null, new ParseException("no Location found for this place", 0));
            }else{
                callback.done(parseGeoPoint,null);
            }
        }
    }

}
