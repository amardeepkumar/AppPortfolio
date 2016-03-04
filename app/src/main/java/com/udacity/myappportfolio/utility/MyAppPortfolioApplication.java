package com.udacity.myappportfolio.utility;

import android.app.Application;

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
