package com.udacity.myappportfolio.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.udacity.myappportfolio.R;
import com.udacity.myappportfolio.databinding.ItemMovieGalleryBinding;
import com.udacity.myappportfolio.model.response.MovieResult;
import com.udacity.myappportfolio.utility.CollectionUtils;
import com.udacity.myappportfolio.utility.DialogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Amardeep on 23/2/16.
 */
public class MovieGalleryAdapter  extends RecyclerView.Adapter<MovieGalleryAdapter.MovieGalleryViewHolder> {

    private final OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void OnItemClicked(int movieId);
    }

    private final LayoutInflater mLayoutInflater;
    private final List<MovieResult> mMovieResult;
    private final Context mContext;

    public MovieGalleryAdapter(Context context, OnItemClickListener itemClickListener) {
        mContext = context;
        mOnItemClickListener = itemClickListener;
        mMovieResult = new ArrayList<>();
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public MovieGalleryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemMovieGalleryBinding binding = DataBindingUtil.inflate(mLayoutInflater, R.layout.item_movie_gallery, parent, false);
        return new MovieGalleryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(MovieGalleryViewHolder holder, int position) {
        holder.bindItem(mMovieResult.get(position));
    }

    @Override
    public int getItemCount() {
        return mMovieResult.size();
    }

    public void reSetMovieList(List<MovieResult> movieList) {
        mMovieResult.clear();
        mMovieResult.addAll(movieList);
        notifyDataSetChanged();
    }

    public void setMovieList(List<MovieResult> movieList) {
        if (!CollectionUtils.isEmpty(movieList)) {
            mMovieResult.addAll(movieList);
            notifyItemRangeInserted(getItemCount(), movieList.size());
        }
    }

    public class MovieGalleryViewHolder extends RecyclerView.ViewHolder {

        private final ItemMovieGalleryBinding mItemBinding;

        public MovieGalleryViewHolder(ItemMovieGalleryBinding binding) {
            super(binding.getRoot());
            mItemBinding = binding;
        }

        public void bindItem(MovieResult movieResult) {
            mItemBinding.setClickHandler(this);
            mItemBinding.setData(movieResult);
        }

        public void OnItemClicked(View view) {
            mOnItemClickListener.OnItemClicked(mMovieResult.get(getAdapterPosition()).getId());
            DialogUtils.showToast("Item Clicked", mContext);
        }
    }
}
