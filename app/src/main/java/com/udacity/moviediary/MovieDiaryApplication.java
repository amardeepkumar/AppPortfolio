package com.udacity.moviediary;

import android.app.Application;

import com.udacity.moviediary.utility.PreferenceManager;

/**
 * Created by Amardeep on 4/3/16.
 */
public class MovieDiaryApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        PreferenceManager.init(getApplicationContext());
    }
}
