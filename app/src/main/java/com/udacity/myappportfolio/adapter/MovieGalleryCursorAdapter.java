package com.udacity.myappportfolio.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.udacity.myappportfolio.R;
import com.udacity.myappportfolio.data.CustomAsyncQueryHandler;
import com.udacity.myappportfolio.data.MovieContract;
import com.udacity.myappportfolio.databinding.ItemMovieGalleryBinding;
import com.udacity.myappportfolio.fragment.MovieGalleryFragment;
import com.udacity.myappportfolio.model.response.MovieResult;
import com.udacity.myappportfolio.utility.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Amardeep on 23/2/16.
 */
public class MovieGalleryCursorAdapter extends CursorRecyclerViewAdapter<MovieGalleryCursorAdapter.MovieGalleryViewHolder> {

    private static final String TAG = MovieGalleryCursorAdapter.class.getSimpleName();
    private final OnItemClickListener mOnItemClickListener;
    private int previousSelection;

    public MovieGalleryCursorAdapter(Context context, Cursor cursor, OnItemClickListener itemClickListener) {
        super(context, cursor);
        mOnItemClickListener = itemClickListener;
        mLayoutInflater = LayoutInflater.from(context);
    }


    public interface OnItemClickListener {
        void OnItemClicked(int movieId);
        void loadMovieDetailOnLaunch(int movieId);
    }

    private final LayoutInflater mLayoutInflater;

    @Override
    public MovieGalleryCursorAdapter.MovieGalleryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemMovieGalleryBinding binding = DataBindingUtil.inflate(mLayoutInflater, R.layout.item_movie_gallery, parent, false);
        return new MovieGalleryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(MovieGalleryCursorAdapter.MovieGalleryViewHolder viewHolder, Cursor cursor) {
        viewHolder.bindItem(getMovieResultFromCursor(cursor));
    }

    private MovieResult getMovieResultFromCursor(Cursor cursor) {
        MovieResult movie = new MovieResult();
        movie.setId(cursor.getInt(MovieGalleryFragment.COLUMN_MOVIE_ID));
        movie.setBackdropPath(cursor.getString(MovieGalleryFragment.COLUMN_BACK_DROP_PATH));
        movie.setPosterPath(cursor.getString(MovieGalleryFragment.COLUMN_POSTER_PATH));
        movie.setFavourite(cursor.getInt(MovieGalleryFragment.COLUMN_FAVOURITE) > 0);
        movie.setSelected(cursor.getInt(MovieGalleryFragment.COLUMN_IS_SELECTED) > 0);
        return movie;
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
            /*final int adapterPosition = getAdapterPosition();
            if (previousSelection != adapterPosition) {
                mMovieResult.get(previousSelection).setSelected(false);
            }
            MovieResult movie = mMovieResult.get(adapterPosition);
            movie.setSelected(true);
            mOnItemClickListener.OnItemClicked(movie.getId());
            previousSelection = adapterPosition;*/
        }

        public void OnFavouriteClicked(View view) {
            if (mCursor != null && mCursor.moveToPosition(getAdapterPosition())) {
                CustomAsyncQueryHandler queryHandler = new CustomAsyncQueryHandler(view.getContext().getContentResolver());
                ContentValues values = new ContentValues();
                values.put(MovieContract.MovieEntry.COLUMN_FAVOURITE, mCursor.getInt(MovieGalleryFragment.COLUMN_FAVOURITE) == 0 ? Integer.valueOf(1) : Integer.valueOf(0));

                queryHandler.startUpdate(1, null, MovieContract.MovieEntry.CONTENT_URI,
                        values, MovieContract.MovieEntry._ID + " = ?",
                        new String[]{mCursor.getString(MovieGalleryFragment.COLUMN_ID)});
            }
        }
    }

    public void resetSelection() {
        previousSelection = 0;
    }
}
