package com.outbound.util;

/**
 * Created by zeki on 15/09/2014.
 */
public final class Constants  {

    //Fragment items
    public static final int TAB_BAR_ITEM_PROFILE = 0;
    public static final int TAB_BAR_ITEM_EXPLORE_TRIPS = 1;
    public static final int TAB_BAR_ITEM_EVENTS = 2;
    public static final int TAB_BAR_ITEM_SEARCH = 3;
    public static final int TAB_BAR_ITEM_NOTICEBOARD = 4;
    public static final int PROFILE_FRIENDS_ITEM = 5;
    public static final int PROFILE_MY_TRIP_ITEM = 6;
    public static final int PROFILE_TRAVEL_HISTORY_ITEM = 7;
    public static final int PROFILE_MY_EVENTS_ITEM = 8;
    public static final int PROFILE_SETTINGS_ITEM = 9;
    public static final int ITEM_INVALID = -1;

    public static class AppInitialStates{
        public static final int LAUNCHER_FRAGMENT_ID = TAB_BAR_ITEM_PROFILE;
    }
    public static class Extra {
        public static final String IS_LAUNCHER = "com.outbound.is_launcher";
    }


}

