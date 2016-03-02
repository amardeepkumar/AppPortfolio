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
import com.udacity.myappportfolio.network.Config;
import com.udacity.myappportfolio.network.NetworkManager;
import com.udacity.myappportfolio.utility.DialogUtils;
import com.udacity.myappportfolio.utility.KeyConstants;

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
    private String mSortBy;
    private Callback<DiscoverMovieResponse> mCallBack = new Callback<DiscoverMovieResponse>() {
        @Override
        public void onResponse(Call<DiscoverMovieResponse> call, Response<DiscoverMovieResponse> response) {
            loading = false;
            if (response != null && response.isSuccess()
                    && response.body() != null) {
                mCurrentPage = response.body().getPage();
                ((MovieGalleryAdapter) binding.movieList.getAdapter()).reSetMovieList(response.body().getResults());
                Log.d(TAG, "response = " + response);
                DialogUtils.hideProgressDialog();
            }
        }

        @Override
        public void onFailure(Call<DiscoverMovieResponse> call, Throwable t) {
            loading = false;
            DialogUtils.showToast("response failed", mContext);
            DialogUtils.hideProgressDialog();
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSortBy = Config.UrlConstants.SORT_POPULARITY_DESC;
        loadMore(this);
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
        return binding.getRoot();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sort_by_popular:
                if (!item.isChecked()) {
                    item.setChecked(!item.isChecked());
                    mSortBy = Config.UrlConstants.SORT_POPULARITY_DESC;
                    mCurrentPage = 0;
                    loadMore(mCallBack);
                }
                return true;

            case R.id.sort_by_highest_rated:
                if (!item.isChecked()) {
                    item.setChecked(!item.isChecked());
                    mSortBy = Config.UrlConstants.SORT_VOTE_AVERAGE_DESC;
                    mCurrentPage = 0;
                    loadMore(mCallBack);
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void loadMore(Callback<DiscoverMovieResponse> callBack) {
        loading = true;
        DialogUtils.displayProgressDialog(mContext);
        NetworkManager.requestMovies(mSortBy, KeyConstants.API_KEY, mCurrentPage + 1, callBack);
        DialogUtils.showToast(R.string.loading_more_movie_list, mContext);
    }

    @Override
    public void onResponse(Call<DiscoverMovieResponse> call, Response<DiscoverMovieResponse> response) {
        loading = false;
        if (response != null && response.isSuccess()
                && response.body() != null) {
            mCurrentPage = response.body().getPage();
            ((MovieGalleryAdapter) binding.movieList.getAdapter()).setMovieList(response.body().getResults());
            Log.d(TAG, "response = " + response);
            DialogUtils.hideProgressDialog();
        }
    }

    @Override
    public void onFailure(Call<DiscoverMovieResponse> call, Throwable t) {
        loading = false;
        DialogUtils.showToast("response failed", mContext);
        DialogUtils.hideProgressDialog();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.movieList.clearOnScrollListeners();
    }
}
