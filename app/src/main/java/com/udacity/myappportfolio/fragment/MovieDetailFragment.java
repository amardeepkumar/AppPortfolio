package com.udacity.myappportfolio.fragment;

import android.content.ContentValues;
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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.udacity.myappportfolio.R;
import com.udacity.myappportfolio.data.CustomAsyncQueryHandler;
import com.udacity.myappportfolio.data.MovieContract;
import com.udacity.myappportfolio.databinding.FragmentMovieDetailBinding;
import com.udacity.myappportfolio.model.response.MovieDetailResponse;
import com.udacity.myappportfolio.model.response.MovieResult;
import com.udacity.myappportfolio.model.response.MovieReviewResponse;
import com.udacity.myappportfolio.model.response.MovieVideoResponse;
import com.udacity.myappportfolio.model.response.ReviewResult;
import com.udacity.myappportfolio.model.response.VideoResult;
import com.udacity.myappportfolio.network.NetworkManager;
import com.udacity.myappportfolio.utility.Constants;
import com.udacity.myappportfolio.utility.DatabaseUtils;
import com.udacity.myappportfolio.utility.DialogUtils;
import com.udacity.myappportfolio.utility.NetworkUtil;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Amardeep on 18/2/16.
 */
public class MovieDetailFragment extends BaseFragment implements Callback<MovieDetailResponse>,
        View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {

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
    public static final int COLUMN_CONTENT = 13;
    public static final int COLUMN_AUTHOR = 14;
    private static final int MOVIE_DETAIL_LOADER = 1;

    private String mMovieId;
    private FragmentMovieDetailBinding binding;
    private Cursor mCursor;

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
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_movie_detail, container, false);

        binding.setClickHandler(this);
        if (mMovieId != null) {
            loadMovieDetails();
        }

        return binding.getRoot();
    }

    private void loadMovieDetails() {
        if (NetworkUtil.isConnectionAvailable(mContext)) {
            if (binding != null) {
                binding.progressBar.setVisibility(View.VISIBLE);
            }
//            NetworkManager.requestMovieDetails(mMovieId, this);
            NetworkManager.requestMovieTrailers(mMovieId, new Callback<MovieVideoResponse>() {
                @Override
                public void onResponse(Call<MovieVideoResponse> call, Response<MovieVideoResponse> response) {
                    saveTrailers(response);
                }

                @Override
                public void onFailure(Call<MovieVideoResponse> call, Throwable t) {

                }
            });

            NetworkManager.requestMovieReviews(mMovieId, new Callback<MovieReviewResponse>() {
                @Override
                public void onResponse(Call<MovieReviewResponse> call, Response<MovieReviewResponse> response) {
                    saveReviews(response);
                }

                @Override
                public void onFailure(Call<MovieReviewResponse> call, Throwable t) {

                }
            });
        } else {
            DialogUtils.showToast(R.string.no_network, mContext);
            if (binding != null) {
                binding.progressBar.setVisibility(View.GONE);
            }
        }
    }

    public void loadMovieDetails(String movieId) {
        mMovieId = movieId;
        getLoaderManager().restartLoader(MOVIE_DETAIL_LOADER, null, this);
        loadMovieDetails();
    }

    @Override
    public void onResponse(Call<MovieDetailResponse> call, Response<MovieDetailResponse> response) {
        if (response != null && response.isSuccess()
                && response.body() != null) {

        }
    }

    @Override
    public void onFailure(Call<MovieDetailResponse> call, Throwable t) {
        DialogUtils.showToast(R.string.response_failed, mContext);

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

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.favourite:
                /*CustomAsyncQueryHandler queryHandler = new CustomAsyncQueryHandler(getContext().getContentResolver());
                ContentValues values = new ContentValues();
                values.put(MovieContract.MovieEntry.COLUMN_FAVOURITE, mMovieId);

                queryHandler.startUpdate(1, null, MovieContract.MovieEntry.CONTENT_URI,
                        values, MovieContract.MovieEntry._ID + " = ?",
                        new String[]{mCursor.getString(MovieGalleryFragment.COLUMN_ID)});*/

                if (mCursor != null && mMovieId != null) {
                    DatabaseUtils.setFavourite(v.getContext(), mMovieId,
                            mCursor.getInt(MovieGalleryFragment.COLUMN_FAVOURITE) == 0 ? 1 : 0);
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
        return new CursorLoader(getActivity(),
                uri,
                MOVIE_DETAIL_PROJECTION,
                selection,
                selectionArgs,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        String movieId = null;
        if (data != null && data.moveToFirst()) {
            mCursor = data;
            movieId = data.getString(COLUMN_MOVIE_ID);
            if (binding != null) {
                binding.setData(getMovieResultFromCursor(data));
                binding.progressBar.setVisibility(View.GONE);
            }
        }
        Toast.makeText(getActivity(), "movie Id = " + movieId, Toast.LENGTH_SHORT).show();
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

    private void saveTrailers(Response<MovieVideoResponse> response) {
        final List<VideoResult> results = response.body().getResults();
        if (results.size() > 0) {
            CustomAsyncQueryHandler queryHandler = new CustomAsyncQueryHandler(getActivity().getContentResolver());
            queryHandler.setAsyncBulkInsertListener(new CustomAsyncQueryHandler.AsyncBulkInsertListener() {
                @Override
                public void onBulkInsertComplete(int token, Object cookie, int result) {
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
//            getLoaderManager().restartLoader(MOVIE_DETAIL_LOADER, null, this);
        }
    }

    private void saveReviews(Response<MovieReviewResponse> response) {
        final List<ReviewResult> results = response.body().getResults();
        if (results.size() > 0) {
            CustomAsyncQueryHandler queryHandler = new CustomAsyncQueryHandler(getActivity().getContentResolver());
            queryHandler.setAsyncBulkInsertListener(new CustomAsyncQueryHandler.AsyncBulkInsertListener() {
                @Override
                public void onBulkInsertComplete(int token, Object cookie, int result) {
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
