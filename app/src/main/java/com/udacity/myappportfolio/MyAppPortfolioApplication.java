package com.udacity.myappportfolio;

import android.app.Application;

import com.udacity.myappportfolio.utility.PreferenceManager;

/**
 * Created by Amardeep on 4/3/16.
 */
public class MyAppPortfolioApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        PreferenceManager.init(getApplicationContext());
    }
}
