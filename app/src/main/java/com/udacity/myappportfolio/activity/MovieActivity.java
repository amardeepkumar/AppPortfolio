package com.udacity.myappportfolio.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.udacity.myappportfolio.R;
import com.udacity.myappportfolio.adapter.MovieGalleryAdapter;
import com.udacity.myappportfolio.databinding.ActivityMovieBinding;
import com.udacity.myappportfolio.fragment.MovieDetailFragment;
import com.udacity.myappportfolio.fragment.MovieGalleryFragment;

/**
 * Created by Amardeep on 18/2/16.
 */
public class MovieActivity extends BaseActivity implements MovieGalleryAdapter.OnItemClickListener {
    private MovieDetailFragment mMovieDetailFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*setContentView(R.layout.activity_movie);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
*/
        ActivityMovieBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_movie);
        binding.toolbar.setTitle("Popular Movies");
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
    public void onActionModeStarted(ActionMode mode) {
        super.onActionModeStarted(mode);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (!getResources().getBoolean(R.bool.isTablet)) {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setTitle("Popular Movies");
            actionBar.setDisplayHomeAsUpEnabled(false);
        }
    }
}
