package com.udacity.myappportfolio.utility;

/**
 * Created by Amardeep on 29/2/16.
 */
public class Constants {


    public interface BundleKeys {
        String ID = "com.udacity.myappportfolio." + "id";
        String SORT_PREFERENCE = "com.udacity.myappportfolio." + "sort_preference";
        String PAGE_NUMBER = "com.udacity.myappportfolio." + "page_number";
    }

    public interface SortPreference {
        int SORT_BY_POPULARITY = 1001;
        int SORT_BY_VOTE_AVG = 1002;
        int SORT_BY_FAVOURITE = 1003;
    }
}
