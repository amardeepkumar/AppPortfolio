package com.udacity.myappportfolio.fragment;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.udacity.myappportfolio.R;
import com.udacity.myappportfolio.adapter.MovieGalleryCursorAdapter;
import com.udacity.myappportfolio.data.CustomAsyncQueryHandler;
import com.udacity.myappportfolio.data.MovieContract;
import com.udacity.myappportfolio.databinding.FragmentMovieGalleryBinding;
import com.udacity.myappportfolio.model.response.DiscoverMovieResponse;
import com.udacity.myappportfolio.model.response.MovieResult;
import com.udacity.myappportfolio.network.Config;
import com.udacity.myappportfolio.network.NetworkManager;
import com.udacity.myappportfolio.utility.Constants;
import com.udacity.myappportfolio.utility.DialogUtils;
import com.udacity.myappportfolio.utility.NetworkUtil;
import com.udacity.myappportfolio.utility.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Amardeep on 18/2/16.
 */
public class MovieGalleryFragment extends BaseFragment  implements LoaderManager.LoaderCallbacks<Cursor>,
        Callback<DiscoverMovieResponse> {

    private static final String TAG = MovieGalleryFragment.class.getSimpleName();

    // These indices are tied to MOVIE_PROJECTION.  If MOVIE_PROJECTION changes, these
    // must change.
    public static final int COLUMN_ID = 0;
    public static final int COLUMN_MOVIE_ID = 1;
    public static final int COLUMN_BACK_DROP_PATH = 2;
    public static final int COLUMN_POSTER_PATH = 3;
    public static final int COLUMN_FAVOURITE = 4;
    public static final int COLUMN_IS_SELECTED = 5;

    private static final String[] MOVIE_PROJECTION = {
            MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_BACK_DROP_PATH,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH,
            MovieContract.MovieEntry.COLUMN_FAVOURITE,
            MovieContract.MovieEntry.COLUMN_IS_SELECTED
    };

    private static final int MOVIE_GALLERY_LOADER = 0;

    private FragmentMovieGalleryBinding binding;
    private boolean loading;
    private int visibleItemCount;
    private int totalItemCount;
    private int firstVisibleItem;
    private int mCurrentPage;
    private MovieGalleryCursorAdapter.OnItemClickListener mItemClickListener;
    private RecyclerView.OnScrollListener mOnScrollListener;

    //Callback for reset adapter.
    private final Callback<DiscoverMovieResponse> mCallBack = new Callback<DiscoverMovieResponse>() {
        @Override
        public void onResponse(Call<DiscoverMovieResponse> call, final Response<DiscoverMovieResponse> response) {
//            final AtomicInteger responseCount = new AtomicInteger(0);
            CustomAsyncQueryHandler queryHandler = new CustomAsyncQueryHandler(getActivity().getContentResolver());
            queryHandler.setAsyncApplyBatchListener(new CustomAsyncQueryHandler.AsyncApplyBatchListener() {
                @Override
                public void onApplyBatchComplete(int token, Object cookie, ContentProviderResult[] result) {
                    saveDataAndUpdateUi(response);
                }
            });

            ArrayList<ContentProviderOperation> ops = new ArrayList<>();
            ops.add(ContentProviderOperation.newDelete(MovieContract.MovieEntry.CONTENT_URI)
                    .withSelection(null, null)
                    .build());
            ops.add(ContentProviderOperation.newDelete(MovieContract.VideoEntry.CONTENT_URI)
                    .withSelection(null, null)
                    .build());
            ops.add(ContentProviderOperation.newDelete(MovieContract.ReviewEntry.CONTENT_URI)
                    .withSelection(null, null)
                    .build());
            queryHandler.applyBatch(1, null, MovieContract.CONTENT_AUTHORITY, ops);

            /*queryHandler.startDelete(2, null, MovieContract.MovieEntry.CONTENT_URI, null, null);
            queryHandler.startDelete(3, null, MovieContract.VideoEntry.CONTENT_URI, null, null);
            queryHandler.startDelete(4, null, MovieContract.ReviewEntry.CONTENT_URI, null, null);*/
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
            mItemClickListener = (MovieGalleryCursorAdapter.OnItemClickListener) context;
        } catch (ClassCastException e) {
            throw new RuntimeException(context.toString()
                    + " must implement OnItemClickListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCurrentPage = PreferenceManager.getInstance().getInt(Constants.BundleKeys.PAGE_NUMBER, 0);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(MOVIE_GALLERY_LOADER, null, this);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_movie_gallery, container, false);
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 2);
        binding.movieList.setLayoutManager(gridLayoutManager);
        binding.movieList.setAdapter(new MovieGalleryCursorAdapter(mContext, null, mItemClickListener));

        mOnScrollListener = new RecyclerView.OnScrollListener() {
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
        };
        if (PreferenceManager.getInstance().getInt(Constants.BundleKeys.SORT_PREFERENCE,
                Constants.SortPreference.SORT_BY_POPULARITY) != Constants.SortPreference.SORT_BY_FAVOURITE) {
            binding.movieList.addOnScrollListener(mOnScrollListener);
            loadMore(this);
        }
        return binding.getRoot();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sort_by_popular:
                if (!item.isChecked()) {
                    binding.movieList.addOnScrollListener(mOnScrollListener);
                    PreferenceManager.getInstance().setInt(Constants.BundleKeys.SORT_PREFERENCE,
                            Constants.SortPreference.SORT_BY_POPULARITY);
                    getLoaderManager().restartLoader(MOVIE_GALLERY_LOADER, null, this);
                    sortList(item);
                }
                return true;

            case R.id.sort_by_highest_rated:
                if (!item.isChecked()) {
                    binding.movieList.addOnScrollListener(mOnScrollListener);
                    PreferenceManager.getInstance().setInt(Constants.BundleKeys.SORT_PREFERENCE,
                            Constants.SortPreference.SORT_BY_VOTE_AVG);
                    getLoaderManager().restartLoader(MOVIE_GALLERY_LOADER, null, this);
                    sortList(item);
                }
                return true;

            case R.id.list_favourite:
                if (!item.isChecked()) {
                    binding.movieList.clearOnScrollListeners();
                    PreferenceManager.getInstance().setInt(Constants.BundleKeys.SORT_PREFERENCE,
                            Constants.SortPreference.SORT_BY_FAVOURITE);
                    getLoaderManager().restartLoader(MOVIE_GALLERY_LOADER, null, this);
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void sortList(MenuItem item) {
        item.setChecked(true);
        mCurrentPage = 0;
        PreferenceManager.getInstance().setInt(Constants.BundleKeys.PAGE_NUMBER, mCurrentPage);
        loadMore(mCallBack);
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
        saveDataAndUpdateUi(response);
    }

    @Override
    public void onFailure(Call<DiscoverMovieResponse> call, Throwable t) {
        loading = false;
        DialogUtils.showToast(R.string.response_failed, mContext);
        if (binding != null) {
            binding.progressBar.setVisibility(View.GONE);
        }
    }

    private void saveDataAndUpdateUi(Response<DiscoverMovieResponse> response) {
        loading = false;
        if (response != null && response.isSuccess()
                && response.body() != null) {
            mCurrentPage = response.body().getPage();
            PreferenceManager.getInstance().setInt(Constants.BundleKeys.PAGE_NUMBER, mCurrentPage);

            final List<MovieResult> results = response.body().getResults();

            CustomAsyncQueryHandler queryHandler = new CustomAsyncQueryHandler(getActivity().getContentResolver());
            queryHandler.setAsyncBulkInsertListener(new CustomAsyncQueryHandler.AsyncBulkInsertListener() {
                @Override
                public void onBulkInsertComplete(int token, Object cookie, int result) {
                    if (binding != null && binding.progressBar.getVisibility() == View.VISIBLE) {
                        binding.progressBar.setVisibility(View.GONE);
                    }
                }
            });
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
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.movieList.clearOnScrollListeners();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] selectionArgs = null;
        String selection = null;
        switch (PreferenceManager.getInstance().getInt(Constants.BundleKeys.SORT_PREFERENCE,
                Constants.SortPreference.SORT_BY_POPULARITY)) {
            case Constants.SortPreference.SORT_BY_FAVOURITE:
                selection = MovieContract.MovieEntry.COLUMN_FAVOURITE + " = ?";
                selectionArgs = new String[]{"1"};
                break;

        }

        return new CursorLoader(getActivity(),
                MovieContract.MovieEntry.CONTENT_URI,
                MOVIE_PROJECTION,
                selection,
                selectionArgs,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.getCount() > 0) {
            ((MovieGalleryCursorAdapter)binding.movieList.getAdapter()).swapCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        ((MovieGalleryCursorAdapter)binding.movieList.getAdapter()).swapCursor(null);
    }
}
