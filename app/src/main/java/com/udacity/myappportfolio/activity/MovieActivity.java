package com.udacity.myappportfolio.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuInflater;

import com.udacity.myappportfolio.R;
import com.udacity.myappportfolio.adapter.MovieGalleryAdapter;
import com.udacity.myappportfolio.databinding.ActivityMovieBinding;
import com.udacity.myappportfolio.fragment.MovieDetailFragment;
import com.udacity.myappportfolio.fragment.MovieGalleryFragment;
import com.udacity.myappportfolio.utility.Constants;
import com.udacity.myappportfolio.utility.PreferenceManager;

/**
 * Class to show the Movie related UI
 * Created by Amardeep on 18/2/16.
 */
public class MovieActivity extends BaseActivity implements MovieGalleryAdapter.OnItemClickListener {
    private MovieDetailFragment mMovieDetailFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMovieBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_movie);
        binding.toolbar.setTitle(R.string.app_name);
        setSupportActionBar(binding.toolbar);

        if (getResources().getBoolean(R.bool.isTablet)) {
            mMovieDetailFragment = (MovieDetailFragment) getSupportFragmentManager().findFragmentById(R.id.movie_detail_fragment);
        } else {
            if (findViewById(R.id.fragment_container) != null) {
                if (savedInstanceState != null) {
                    return;
                }

                // Create a new Fragment to be placed in the activity layout
                MovieGalleryFragment firstFragment = new MovieGalleryFragment();
                firstFragment.setArguments(getIntent().getExtras());
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_container, firstFragment).commit();
            }
        }
    }

    @Override
    public void OnItemClicked(int movieId) {
        if (!getResources().getBoolean(R.bool.isTablet)) {
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container,
                    MovieDetailFragment.getInstance(movieId)).addToBackStack(null).commit();
        } else if (mMovieDetailFragment != null) {
            mMovieDetailFragment.loadMovieDetails(movieId);
        }
    }

    @Override
    public void loadMovieDetailOnLaunch(int movieId) {
        if (mMovieDetailFragment != null) {
            mMovieDetailFragment.loadMovieDetails(movieId);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (!getResources().getBoolean(R.bool.isTablet)
                && getSupportFragmentManager().findFragmentById(R.id.fragment_container)instanceof MovieDetailFragment) {
            //Hiding menu for detail fragment in case of phone
            menu.findItem(R.id.sort_by_popular).setVisible(false);
            menu.findItem(R.id.sort_by_highest_rated).setVisible(false);
        } else if (PreferenceManager.getInstance().getInt(Constants.BundleKeys.SORT_PREFERENCE,
                Constants.SortPreference.SORT_BY_POPULARITY) == Constants.SortPreference.SORT_BY_POPULARITY) {
            menu.findItem(R.id.sort_by_popular).setChecked(true);
        } else {
            menu.findItem(R.id.sort_by_highest_rated).setChecked(true);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (!getResources().getBoolean(R.bool.isTablet)) {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle(R.string.app_name);
                actionBar.setDisplayHomeAsUpEnabled(false);
            }
        }
    }
}
