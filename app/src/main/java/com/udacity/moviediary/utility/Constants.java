package com.udacity.moviediary.utility;

/**
 * Created by Amardeep on 29/2/16.
 */
public class Constants {


    public interface BundleKeys {
        String ID = "com.udacity.moviediary." + "id";
        String SORT_PREFERENCE = "com.udacity.moviediary." + "sort_preference";
    }

    public interface SortPreference {
        int SORT_BY_POPULARITY = 1001;
        int SORT_BY_VOTE_AVG = 1002;
        int SORT_BY_FAVOURITE = 1003;
    }

    public static final String PREV_SELECTION = "prev_selection";
}
