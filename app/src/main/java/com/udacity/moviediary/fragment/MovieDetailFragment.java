package com.udacity.moviediary.fragment;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.udacity.moviediary.R;
import com.udacity.moviediary.data.CustomAsyncQueryHandler;
import com.udacity.moviediary.data.MovieContract;
import com.udacity.moviediary.databinding.FragmentMovieDetailBinding;
import com.udacity.moviediary.databinding.ReviewLayoutBinding;
import com.udacity.moviediary.databinding.TrailerLayoutBinding;
import com.udacity.moviediary.model.response.MovieResult;
import com.udacity.moviediary.model.response.MovieReviewResponse;
import com.udacity.moviediary.model.response.MovieVideoResponse;
import com.udacity.moviediary.model.response.ReviewResult;
import com.udacity.moviediary.model.response.VideoResult;
import com.udacity.moviediary.network.NetworkManager;
import com.udacity.moviediary.utility.Constants;
import com.udacity.moviediary.utility.DatabaseUtils;
import com.udacity.moviediary.utility.DialogUtils;
import com.udacity.moviediary.utility.NetworkUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Amardeep on 18/2/16.
 */
public class MovieDetailFragment extends BaseFragment implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = MovieDetailFragment.class.getSimpleName();
    private static final String[] MOVIE_DETAIL_PROJECTION = new String[]{
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_BACK_DROP_PATH,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH,
            MovieContract.MovieEntry.COLUMN_FAVOURITE,
            MovieContract.MovieEntry.COLUMN_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE,
            MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.VideoEntry.COLUMN_VIDEO_ID,
            MovieContract.VideoEntry.COLUMN_VIDEO_NAME,
            MovieContract.VideoEntry.COLUMN_VIDEO_KEY,
            MovieContract.ReviewEntry.COLUMN_REVIEW_ID,
            MovieContract.ReviewEntry.COLUMN_CONTENT,
            MovieContract.ReviewEntry.COLUMN_AUTHOR
    };
    // These indices are tied to MOVIE_DETAIL_PROJECTION.  If MOVIE_DETAIL_PROJECTION changes, these
    // must change.
    public static final int COLUMN_ID = 0;
    public static final int COLUMN_MOVIE_ID = 1;
    public static final int COLUMN_BACK_DROP_PATH = 2;
    public static final int COLUMN_POSTER_PATH = 3;
    public static final int COLUMN_FAVOURITE = 4;
    public static final int COLUMN_OVERVIEW = 5;
    public static final int COLUMN_VOTE_AVERAGE = 6;
    public static final int COLUMN_ORIGINAL_TITLE = 7;
    public static final int COLUMN_RELEASE_DATE = 8;
    public static final int COLUMN_VIDEO_ID = 9;
    public static final int COLUMN_VIDEO_NAME = 10;
    public static final int COLUMN_VIDEO_KEY = 11;
    public static final int COLUMN_REVIEW_ID = 12;
    public static final int COLUMN_REVIEW_CONTENT = 13;
    public static final int COLUMN_REVIEW_AUTHOR = 14;

    private static final int MOVIE_DETAIL_LOADER = 1;

    private String mMovieId;
    private FragmentMovieDetailBinding binding;
    private Cursor mCursor;
    private LayoutInflater mInflater;
    private AtomicInteger mResponseCount;//Counter to reload cursor after all API response

    public static MovieDetailFragment getInstance(String movieId) {
        MovieDetailFragment fragment = new MovieDetailFragment();
        Bundle args = new Bundle();
        args.putString(Constants.BundleKeys.ID, movieId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        getLoaderManager().initLoader(MOVIE_DETAIL_LOADER, null, this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mMovieId = getArguments().getString(Constants.BundleKeys.ID, null);
        }

        if (!getResources().getBoolean(R.bool.isTablet)) {
            ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle(R.string.movie_details);
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setHomeButtonEnabled(true);
            }
        }
        mResponseCount = new AtomicInteger(0);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mInflater = inflater;
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_movie_detail, container, false);

        binding.setClickHandler(this);
        if (savedInstanceState != null) {
            mMovieId = savedInstanceState.getString(Constants.BundleKeys.ID, null);
            getLoaderManager().restartLoader(MOVIE_DETAIL_LOADER, null, MovieDetailFragment.this);
        } else if (mMovieId != null) {
            loadMovieDetails();
        }

        return binding.getRoot();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Constants.BundleKeys.ID, mMovieId);
    }

    private void loadMovieDetails() {
        if (NetworkUtil.isConnectionAvailable(mContext)) {
            if (binding != null) {
                binding.progressBar.setVisibility(View.VISIBLE);
            }
            NetworkManager.requestMovieTrailers(mMovieId, new Callback<MovieVideoResponse>() {
                @Override
                public void onResponse(Call<MovieVideoResponse> call, Response<MovieVideoResponse> response) {
                    saveTrailersInDb(response);
                }

                @Override
                public void onFailure(Call<MovieVideoResponse> call, Throwable t) {

                }
            });

            NetworkManager.requestMovieReviews(mMovieId, new Callback<MovieReviewResponse>() {
                @Override
                public void onResponse(Call<MovieReviewResponse> call, Response<MovieReviewResponse> response) {
                    saveReviewsInDb(response);
                }

                @Override
                public void onFailure(Call<MovieReviewResponse> call, Throwable t) {

                }
            });
        } else {
            getLoaderManager().restartLoader(MOVIE_DETAIL_LOADER, null, MovieDetailFragment.this);
            DialogUtils.showToast(R.string.no_network, mContext);
            if (binding != null) {
                binding.progressBar.setVisibility(View.GONE);
            }
        }
    }

    public void loadMovieDetails(String movieId) {
        mMovieId = movieId;
        loadMovieDetails();
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.favourite:
                if (mCursor != null && mMovieId != null  && mCursor.moveToFirst()) {
                    DatabaseUtils.setFavourite(v.getContext(), mMovieId,
                            mCursor.getInt(MovieGalleryFragment.COLUMN_FAVOURITE) == 0 ? 1 : 0);
                }
                break;
            case R.id.trailer:
                final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube://" + v.getTag()));
                intent.putExtra("force_fullscreen", true);
                // Verify that the intent will resolve to an activity
                if (intent.resolveActivity(mContext.getPackageManager()) != null) {
                    mContext.startActivity(intent);
                }
                break;
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = MovieContract.MovieEntry.buildMovieTrailerWithReview();
        String[] selectionArgs = null;
        String selection = null;
        if (mMovieId != null) {
            selection = MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry.COLUMN_MOVIE_ID + "=?";
            selectionArgs = new String[]{mMovieId};
        }
        return new CursorLoader(mContext,
                uri,
                MOVIE_DETAIL_PROJECTION,
                selection,
                selectionArgs,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (mMovieId != null && data != null && data.moveToFirst()) {
            mCursor = data;
            if (binding != null) {
                binding.setData(getMovieResultFromCursor(data));
                inflateTrailerUi(data);
                inflateReviewUi(data);
                binding.progressBar.setVisibility(View.GONE);
            }
        }
    }

    private void inflateTrailerUi(Cursor cursor) {
        List<String> videoKeys = new ArrayList<>();
        binding.movieDetailTrailerLayout.removeAllViews();
        do {
            final String key = cursor.getString(COLUMN_VIDEO_KEY);
            if (!TextUtils.isEmpty(key) && !videoKeys.contains(key)) {
                VideoResult videoResult = new VideoResult();
                videoResult.setId(cursor.getString(COLUMN_VIDEO_ID));
                videoResult.setKey(key);
                videoResult.setName(cursor.getString(COLUMN_VIDEO_NAME));
                TrailerLayoutBinding trailerBinding = DataBindingUtil.inflate(mInflater,
                        R.layout.trailer_layout, binding.movieDetailTrailerLayout, false);
                trailerBinding.setTrailer(videoResult);
                trailerBinding.setClickHandler(this);
                binding.movieDetailTrailerLayout.addView(trailerBinding.getRoot());
                videoKeys.add(key);
            }
        } while (cursor.moveToNext());
        binding.setTrailerCount(videoKeys.size());
        binding.movieDetailTrailerLayout.invalidate();
    }

    private void inflateReviewUi(Cursor cursor) {
        cursor.moveToFirst();
        List<String> reviewIds = new ArrayList<>();
        binding.movieDetailReviewLayout.removeAllViews();
        do {
            final String id = cursor.getString(COLUMN_REVIEW_ID);
            if (!TextUtils.isEmpty(id) && !reviewIds.contains(id)) {
                ReviewResult reviewResult = new ReviewResult();
                reviewResult.setId(cursor.getString(COLUMN_REVIEW_ID));
                reviewResult.setAuthor(cursor.getString(COLUMN_REVIEW_AUTHOR));
                reviewResult.setContent(cursor.getString(COLUMN_REVIEW_CONTENT));

                ReviewLayoutBinding reviewLayoutBinding = DataBindingUtil.inflate(mInflater,
                        R.layout.review_layout, binding.movieDetailTrailerLayout, false);
                reviewLayoutBinding.setReview(reviewResult);
                binding.movieDetailReviewLayout.addView(reviewLayoutBinding.getRoot());
                reviewIds.add(id);
            }
        } while (cursor.moveToNext());
        binding.setReviewCount(reviewIds.size());
        binding.movieDetailReviewLayout.invalidate();
    }

    private MovieResult getMovieResultFromCursor(Cursor cursor) {
        MovieResult movie = new MovieResult();
        movie.setId(cursor.getString(COLUMN_MOVIE_ID));
        movie.setBackdropPath(cursor.getString(COLUMN_BACK_DROP_PATH));
        movie.setPosterPath(cursor.getString(COLUMN_POSTER_PATH));
        movie.setFavourite(cursor.getInt(COLUMN_FAVOURITE) > 0);
        movie.setOverview(cursor.getString(COLUMN_OVERVIEW));
        movie.setVoteAverage(cursor.getFloat(COLUMN_VOTE_AVERAGE));
        movie.setOriginalTitle(cursor.getString(COLUMN_ORIGINAL_TITLE));
        movie.setReleaseDate(cursor.getString(COLUMN_RELEASE_DATE));
        return movie;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }

    private void saveTrailersInDb(Response<MovieVideoResponse> response) {
        final List<VideoResult> results = response.body().getResults();
        if (results.size() > 0) {
            CustomAsyncQueryHandler queryHandler = new CustomAsyncQueryHandler(mContext.getContentResolver());
            queryHandler.setAsyncBulkInsertListener(new CustomAsyncQueryHandler.AsyncBulkInsertListener() {
                @Override
                public void onBulkInsertComplete(int token, Object cookie, int result) {
                    refreshUi();
                }
            });

            ContentValues[] contentValues = new ContentValues[results.size()];
            int i = 0;
            for (VideoResult videoResult :
                    results) {
                ContentValues contentValue = new ContentValues();
                contentValue.put(MovieContract.VideoEntry.COLUMN_VIDEO_ID, videoResult.getId());
                contentValue.put(MovieContract.VideoEntry.COLUMN_VIDEO_KEY, videoResult.getKey());
                contentValue.put(MovieContract.VideoEntry.COLUMN_VIDEO_NAME, videoResult.getName());
                contentValue.put(MovieContract.VideoEntry.COLUMN_MOVIE_ID, response.body().getId());
                contentValues[i++] = contentValue;
            }
            queryHandler.startBulkInsert(1, null, MovieContract.VideoEntry.CONTENT_URI, contentValues);
        }
    }

    private void refreshUi() {
        //Check the response count
        if (mResponseCount.incrementAndGet() == 2 && isAdded()) {
            mResponseCount.set(0);
            //All response came
            getLoaderManager().restartLoader(MOVIE_DETAIL_LOADER, null, MovieDetailFragment.this);
        }
    }

    private void saveReviewsInDb(Response<MovieReviewResponse> response) {
        final List<ReviewResult> results = response.body().getResults();
        if (results.size() > 0) {
            CustomAsyncQueryHandler queryHandler = new CustomAsyncQueryHandler(mContext.getContentResolver());
            queryHandler.setAsyncBulkInsertListener(new CustomAsyncQueryHandler.AsyncBulkInsertListener() {
                @Override
                public void onBulkInsertComplete(int token, Object cookie, int result) {
                    refreshUi();
                }
            });

            ContentValues[] contentValues = new ContentValues[results.size()];
            int i = 0;
            for (ReviewResult reviewResult :
                    results) {
                ContentValues contentValue = new ContentValues();
                contentValue.put(MovieContract.ReviewEntry.COLUMN_REVIEW_ID, reviewResult.getId());
                contentValue.put(MovieContract.ReviewEntry.COLUMN_AUTHOR, reviewResult.getAuthor());
                contentValue.put(MovieContract.ReviewEntry.COLUMN_CONTENT, reviewResult.getContent());
                contentValue.put(MovieContract.ReviewEntry.COLUMN_URL, reviewResult.getUrl());
                contentValue.put(MovieContract.ReviewEntry.COLUMN_MOVIE_ID, response.body().getId());
                contentValues[i++] = contentValue;
            }
            queryHandler.startBulkInsert(1, null, MovieContract.ReviewEntry.CONTENT_URI, contentValues);
        }
    }
}
