package com.udacity.myappportfolio.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.udacity.myappportfolio.R;
import com.udacity.myappportfolio.databinding.FragmentMovieDetailBinding;
import com.udacity.myappportfolio.model.response.MovieDetailResponse;
import com.udacity.myappportfolio.network.NetworkManager;
import com.udacity.myappportfolio.utility.Constants;
import com.udacity.myappportfolio.utility.DialogUtils;
import com.udacity.myappportfolio.utility.KeyConstants;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Amardeep on 18/2/16.
 */
public class MovieDetailFragment extends BaseFragment implements Callback<MovieDetailResponse> {

    private static final String TAG = MovieDetailFragment.class.getSimpleName();
    private int mMovieId;
    private FragmentMovieDetailBinding binding;

    public static MovieDetailFragment getInstance(int movieId) {
        MovieDetailFragment fragment = new MovieDetailFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.BundleKeys.ID, movieId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mMovieId = getArguments().getInt(Constants.BundleKeys.ID, 0);
        }

        if (!getResources().getBoolean(R.bool.isTablet)) {
            ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle(R.string.movie_details);
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setHomeButtonEnabled(true);
            }
        }

        loadMovieDetails();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_movie_detail, container, false);

        return binding.getRoot();
    }

    private void loadMovieDetails() {
        if (binding != null) {
            binding.progressBar.setVisibility(View.VISIBLE);
        }
        NetworkManager.requestMovieDetails(KeyConstants.API_KEY, mMovieId, this);
    }

    public void loadMovieDetails(int movieId) {
        mMovieId = movieId;
        loadMovieDetails();
    }

    @Override
    public void onResponse(Call<MovieDetailResponse> call, Response<MovieDetailResponse> response) {
        if (response != null && response.isSuccess()
                && response.body() != null) {
            binding.setData(response.body());
            Log.d(TAG, "response = " + response);
            if (binding != null) {
                binding.progressBar.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onFailure(Call<MovieDetailResponse> call, Throwable t) {
        DialogUtils.showToast("response failed", mContext);

        if (binding != null) {
            binding.progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
