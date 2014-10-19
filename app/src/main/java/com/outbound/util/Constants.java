package com.outbound.util;

import com.outbound.model.PUser;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by zeki on 15/09/2014.
 */
public final class Constants  {

    //Fragment items
    public static final int TAB_BAR_ITEM_PROFILE_FRAG_ID = 0;
    public static final int TAB_BAR_ITEM_EXPLORE_TRIPS_FRAG_ID = 1;
    public static final int TAB_BAR_ITEM_EVENTS_FRAG_ID = 2;
    public static final int TAB_BAR_ITEM_SEARCH_FRAG_ID = 3;
    public static final int TAB_BAR_ITEM_NOTICEBOARD_FRAG_ID = 4;
    public static final int PROFILE_FRIENDS_FRAG_ID = 5;
    public static final int PROFILE_MY_TRIP_FRAG_ID = 6;
    public static final int PROFILE_TRAVEL_HISTORY_FRAG_ID = 7;
    public static final int PROFILE_MY_EVENTS_FRAG_ID = 8;
    public static final int PROFILE_SETTINGS_FRAG_ID = 9;
    public static final int EXPLORE_TRIPS_SEARCH_TRIP_FRAG_ID = 10;
    public static final int EXPLORE_TRIPS_ADD_NEW_TRIPS_FRAG_ID = 11;
    public static final int EVENT_DETAIL_FRAG_ID = 12;
    public static final int SEARCH_PEOPLE_DETAIL_FRAG_ID = 13;
    public static final int PEOPLE_PROFILE_FRAG_ID = 14;
    public static final int PEOPLE_PROFILE_FRIENDS_FRAG_ID = 15;
    public static final int NOTICE_BOARD_SEARCH_MESSAGE_FRAG_ID = 16;
    public static final int NOTICE_BOARD_CREATE_MESSAGE_FRAG_ID = 17;
    public static final int EVENT_SEARCH_FRAGMENT_ID = 18;
    public static final int PROFILE_MY_TRIP_DETAIL_FRAG_ID = 19;
    public static final int EVENT_CREATE_FRAGMENT_ID = 20;
    public static final int TRIPS_RESULT_FRAGMENT_ID = 21;
    public static final int SEARCH_PEOPLE_RESULT_FRAG_ID = 22;
    public static final int SEARCH_EVENTS_RESULT_FRAG_ID = 23;
    public static final int NOTICE_BOARD_POST_DETAIL_FRAG_ID = 24;
    public static final int ITEM_INVALID = -1;


    public static class AppInitialStates{
        public static final int LAUNCHER_FRAGMENT_ID = TAB_BAR_ITEM_PROFILE_FRAG_ID;
    }
    public static class Extra {
        public static final String IS_LAUNCHER = "com.outbound.is_launcher";
    }
    public static class Distance{
        public static final double EVENT_AROUND_PROXIMITY_IN_MILE = 124.274238; // 200 km
    }

}

