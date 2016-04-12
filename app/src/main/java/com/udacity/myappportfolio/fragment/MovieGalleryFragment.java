package com.udacity.myappportfolio.fragment;

import android.content.ContentValues;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.udacity.myappportfolio.R;
import com.udacity.myappportfolio.adapter.MovieGalleryAdapter;
import com.udacity.myappportfolio.data.CustomAsyncQueryHandler;
import com.udacity.myappportfolio.data.MovieContract;
import com.udacity.myappportfolio.databinding.FragmentMovieGalleryBinding;
import com.udacity.myappportfolio.model.response.DiscoverMovieResponse;
import com.udacity.myappportfolio.model.response.MovieResult;
import com.udacity.myappportfolio.network.Config;
import com.udacity.myappportfolio.network.NetworkManager;
import com.udacity.myappportfolio.utility.CollectionUtils;
import com.udacity.myappportfolio.utility.Constants;
import com.udacity.myappportfolio.utility.DialogUtils;
import com.udacity.myappportfolio.utility.NetworkUtil;
import com.udacity.myappportfolio.utility.PreferenceManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Amardeep on 18/2/16.
 */
public class MovieGalleryFragment extends BaseFragment implements Callback<DiscoverMovieResponse> {

    private static final String TAG = MovieGalleryFragment.class.getSimpleName();
    private FragmentMovieGalleryBinding binding;
    private boolean loading;
    private int visibleItemCount;
    private int totalItemCount;
    private int firstVisibleItem;
    private int mCurrentPage;
    private MovieGalleryAdapter.OnItemClickListener mItemClickListener;

    //Callback for reset adapter.
    private final Callback<DiscoverMovieResponse> mCallBack = new Callback<DiscoverMovieResponse>() {
        @Override
        public void onResponse(Call<DiscoverMovieResponse> call, Response<DiscoverMovieResponse> response) {
            loading = false;
            if (response != null && response.isSuccess()
                    && response.body() != null) {
                mCurrentPage = response.body().getPage();
                ((MovieGalleryAdapter) binding.movieList.getAdapter()).reSetMovieList(response.body().getResults());
                binding.progressBar.setVisibility(View.GONE);
            }
        }

        @Override
        public void onFailure(Call<DiscoverMovieResponse> call, Throwable t) {
            loading = false;
            DialogUtils.showToast("response failed", mContext);
            binding.progressBar.setVisibility(View.GONE);
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mItemClickListener = (MovieGalleryAdapter.OnItemClickListener) context;
        } catch (ClassCastException e) {
            throw new RuntimeException(context.toString()
                    + " must implement OnItemClickListener");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_movie_gallery, container, false);
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 2);
        binding.movieList.setLayoutManager(gridLayoutManager);
        binding.movieList.setAdapter(new MovieGalleryAdapter(mContext, mItemClickListener));
        binding.movieList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                /*check for scroll down*/
                if (dy > 0) {
                    visibleItemCount = gridLayoutManager.getChildCount();
                    totalItemCount = gridLayoutManager.getItemCount();
                    firstVisibleItem = gridLayoutManager.findFirstCompletelyVisibleItemPosition();

                    if (!loading) {
                        if ((visibleItemCount + firstVisibleItem) >= totalItemCount) {
                            loadMore(MovieGalleryFragment.this);
                        }
                    }
                }
            }
        });
        loadMore(this);
        return binding.getRoot();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sort_by_popular:
                if (!item.isChecked()) {
                    PreferenceManager.getInstance().setInt(Constants.BundleKeys.SORT_PREFERENCE,
                            Constants.SortPreference.SORT_BY_POPULARITY);
                    sortList(item);
                }
                return true;

            case R.id.sort_by_highest_rated:
                if (!item.isChecked()) {
                    PreferenceManager.getInstance().setInt(Constants.BundleKeys.SORT_PREFERENCE,
                            Constants.SortPreference.SORT_BY_VOTE_AVG);
                    sortList(item);
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void sortList(MenuItem item) {

/*

        ContentValues locationValues = new ContentValues();

        // Then add the data, along with the corresponding name of the data type,
        // so the content provider knows what kind of value is being inserted.
        locationValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, "12345");
        locationValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, "New Movie");
        locationValues.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE, "New Movie");
        locationValues.put(MovieContract.MovieEntry.COLUMN_BACK_DROP_PATH, "New Movie");
        locationValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, "New Movie");
        locationValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, "New Movie");
        locationValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, "New Movie");
        locationValues.put(MovieContract.MovieEntry.COLUMN_FAVOURITE, "New Movie");
        locationValues.put(MovieContract.MovieEntry.COLUMN_IS_SELECTED, 1);


        Uri insertedUri = mContext.getContentResolver().insert(
                MovieContract.MovieEntry.CONTENT_URI,
                locationValues);
        long locationId = ContentUris.parseId(insertedUri);
        Log.d(TAG, "movieId" + locationId);*/
        item.setChecked(true);
        mCurrentPage = 0;
        loadMore(mCallBack);
        ((MovieGalleryAdapter) binding.movieList.getAdapter()).resetSelection();
    }

    /**
     * Loading the movie list
     * @param callBack
     */
    private void loadMore(Callback<DiscoverMovieResponse> callBack) {
        if (NetworkUtil.isConnectionAvailable(mContext)) {
            loading = true;
            if (binding != null) {
                binding.progressBar.setVisibility(View.VISIBLE);
            }

            String sortBy;
            if (PreferenceManager.getInstance().getInt(Constants.BundleKeys.SORT_PREFERENCE,
                    Constants.SortPreference.SORT_BY_POPULARITY) == Constants.SortPreference.SORT_BY_POPULARITY) {
                sortBy = Config.UrlConstants.SORT_POPULARITY_DESC;
            } else {
                sortBy = Config.UrlConstants.SORT_VOTE_AVERAGE_DESC;
            }

            NetworkManager.requestMovies(sortBy, mCurrentPage + 1, callBack);
        } else {
            DialogUtils.showToast(R.string.no_network, mContext);
            if (binding != null) {
                binding.progressBar.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onResponse(Call<DiscoverMovieResponse> call, Response<DiscoverMovieResponse> response) {
        loading = false;
        if (response != null && response.isSuccess()
                && response.body() != null) {
            mCurrentPage = response.body().getPage();

            final List<MovieResult> results = response.body().getResults();
            if (getResources().getBoolean(R.bool.isTablet)
                    && CollectionUtils.hasItems(results)
                    && mCurrentPage == 1) {
                final MovieResult movieResult = results.get(0);
                movieResult.setSelected(true);
                final int movieId = movieResult.getId();
                mItemClickListener.loadMovieDetailOnLaunch(movieId);
            }

            CustomAsyncQueryHandler queryHandler = new CustomAsyncQueryHandler(getActivity().getContentResolver());
            ContentValues[] contentValues = new ContentValues[results.size()];
            int i = 0;
            for (MovieResult movieResult:
                 results) {
                ContentValues contentValue = new ContentValues();
                contentValue.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movieResult.getId());
                contentValue.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, movieResult.getPosterPath());
                contentValue.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE, movieResult.getOriginalTitle());
                contentValue.put(MovieContract.MovieEntry.COLUMN_BACK_DROP_PATH, movieResult.getBackdropPath());
                contentValue.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, movieResult.getReleaseDate());
                contentValue.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, movieResult.getVoteAverage());
                contentValue.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, movieResult.getOverview());
                contentValues[i++] = contentValue;
            }
            queryHandler.startBulkInsert(1, null, MovieContract.MovieEntry.CONTENT_URI, contentValues);
            ((MovieGalleryAdapter) binding.movieList.getAdapter()).setMovieList(results);

            if (binding != null) {
                binding.progressBar.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onFailure(Call<DiscoverMovieResponse> call, Throwable t) {
        loading = false;
        DialogUtils.showToast(R.string.response_failed, mContext);
        if (binding != null) {
            binding.progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.movieList.clearOnScrollListeners();
    }
}
