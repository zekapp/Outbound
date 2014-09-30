package com.outbound.util;

/**
 * Created by zeki on 15/09/2014.
 */
public final class Constants  {

    //Fragment items
    public static final int TAB_BAR_ITEM_PROFILE_ID = 0;
    public static final int TAB_BAR_ITEM_EXPLORE_TRIPS_ID = 1;
    public static final int TAB_BAR_ITEM_EVENTS_ID = 2;
    public static final int TAB_BAR_ITEM_SEARCH_ID = 3;
    public static final int TAB_BAR_ITEM_NOTICEBOARD_ID = 4;
    public static final int PROFILE_FRIENDS_ID = 5;
    public static final int PROFILE_MY_TRIP_ID = 6;
    public static final int PROFILE_TRAVEL_HISTORY_ID = 7;
    public static final int PROFILE_MY_EVENTS_ID = 8;
    public static final int PROFILE_SETTINGS_ID = 9;
    public static final int EXPLORE_TRIPS_SEARCH_TRIP_ID = 10;
    public static final int EXPLORE_TRIPS_ADD_NEW_TRIPS_ID = 11;
    public static final int EVENT_DETAIL_FRAGMENT_ID = 12;
    public static final int SEARCH_PEOPLE_DETAIL_FRAGMENT_ID = 13;
    public static final int PEOPLE_PROFILE_FRAGMENT_ID = 14;
    public static final int ITEM_INVALID = -1;

    public static class AppInitialStates{
        public static final int LAUNCHER_FRAGMENT_ID = TAB_BAR_ITEM_PROFILE_ID;
    }
    public static class Extra {
        public static final String IS_LAUNCHER = "com.outbound.is_launcher";
    }


}

