package com.udacity.myappportfolio.fragment;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.udacity.myappportfolio.R;
import com.udacity.myappportfolio.databinding.FragmentMovieDetailBinding;
import com.udacity.myappportfolio.databinding.FragmentMovieGalleryBinding;
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
            actionBar.setTitle("Movie Details");
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
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
        DialogUtils.displayProgressDialog(mContext);
        NetworkManager.requestMovieDetails(KeyConstants.API_KEY, mMovieId, this);
    }

    public void loadMovieDetails(int movieId) {
        mMovieId = movieId;
        DialogUtils.displayProgressDialog(mContext);
        NetworkManager.requestMovieDetails(KeyConstants.API_KEY, mMovieId, this);
    }

    @Override
    public void onResponse(Call<MovieDetailResponse> call, Response<MovieDetailResponse> response) {
        if (response != null && response.isSuccess()
                && response.body() != null) {
            binding.setData(response.body());
            Log.d(TAG, "response = " + response);
            DialogUtils.hideProgressDialog();
        }
    }

    @Override
    public void onFailure(Call<MovieDetailResponse> call, Throwable t) {
        DialogUtils.showToast("response failed", mContext);
        DialogUtils.hideProgressDialog();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                Toast.makeText(getActivity(), "Back from fragment", Toast.LENGTH_SHORT).show();
                getActivity().onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
