package com.udacity.myappportfolio.fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.udacity.myappportfolio.R;
import com.udacity.myappportfolio.adapter.MovieGalleryAdapter;
import com.udacity.myappportfolio.databinding.FragmentMovieGalleryBinding;
import com.udacity.myappportfolio.model.response.DiscoverMovieResponse;
import com.udacity.myappportfolio.model.response.MovieResult;
import com.udacity.myappportfolio.network.Config;
import com.udacity.myappportfolio.network.NetworkManager;
import com.udacity.myappportfolio.utility.CollectionUtils;
import com.udacity.myappportfolio.utility.Constants;
import com.udacity.myappportfolio.utility.DialogUtils;
import com.udacity.myappportfolio.utility.KeyConstants;
import com.udacity.myappportfolio.utility.NetworkUtil;
import com.udacity.myappportfolio.utility.PreferenceManager;

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
    private Callback<DiscoverMovieResponse> mCallBack = new Callback<DiscoverMovieResponse>() {
        @Override
        public void onResponse(Call<DiscoverMovieResponse> call, Response<DiscoverMovieResponse> response) {
            loading = false;
            if (response != null && response.isSuccess()
                    && response.body() != null) {
                mCurrentPage = response.body().getPage();
                ((MovieGalleryAdapter) binding.movieList.getAdapter()).reSetMovieList(response.body().getResults());
                Log.d(TAG, "response = " + response);
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
        item.setChecked(true);
        mCurrentPage = 0;
        loadMore(mCallBack);
        ((MovieGalleryAdapter) binding.movieList.getAdapter()).resetSelection();
    }

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

            NetworkManager.requestMovies(sortBy, KeyConstants.API_KEY, mCurrentPage + 1, callBack);
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

            if (getResources().getBoolean(R.bool.isTablet)
                    && !CollectionUtils.isEmpty(response.body().getResults())
                    && mCurrentPage == 1) {
                final MovieResult movieResult = response.body().getResults().get(0);
                movieResult.setSelected(true);
                final int movieId = movieResult.getId();
                mItemClickListener.loadMovieDetail(movieId);
            }
            ((MovieGalleryAdapter) binding.movieList.getAdapter()).setMovieList(response.body().getResults());

            Log.d(TAG, "response = " + response);
            if (binding != null) {
                binding.progressBar.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onFailure(Call<DiscoverMovieResponse> call, Throwable t) {
        loading = false;
        DialogUtils.showToast("response failed", mContext);
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
