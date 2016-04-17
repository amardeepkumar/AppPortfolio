package com.udacity.myappportfolio.adapter;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
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
import com.udacity.myappportfolio.utility.DatabaseUtils;

import java.util.ArrayList;

/**
 * Created by Amardeep on 23/2/16.
 */
public class MovieGalleryCursorAdapter extends CursorRecyclerViewAdapter<MovieGalleryCursorAdapter.MovieGalleryViewHolder> {

    private static final String TAG = MovieGalleryCursorAdapter.class.getSimpleName();
    private final OnItemClickListener mOnItemClickListener;
    private int previousSelection; //Cache old selected position
    private final LayoutInflater mLayoutInflater;

    public interface OnItemClickListener {
        void OnItemClicked(String movieId);
    }

    public MovieGalleryCursorAdapter(Context context, Cursor cursor, OnItemClickListener itemClickListener) {
        super(cursor);
        mOnItemClickListener = itemClickListener;
        mLayoutInflater = LayoutInflater.from(context);
        previousSelection = -2;//Setting to a non reachable cursor position
    }

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
        movie.setId(cursor.getString(MovieGalleryFragment.COLUMN_MOVIE_ID));
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
            final int adapterPosition = getAdapterPosition();

            if (mCursor != null && mCursor.moveToPosition(adapterPosition)) {
                final String movieId = mCursor.getString(MovieGalleryFragment.COLUMN_MOVIE_ID);
                final String rowId = mCursor.getString(MovieGalleryFragment.COLUMN_ID);

                //Check for previous selection.
                if (previousSelection != adapterPosition) {
                    //Update the row in database for election
                    CustomAsyncQueryHandler queryHandler = new CustomAsyncQueryHandler(view.getContext().getContentResolver());

                    ContentValues values = new ContentValues();
                    values.put(MovieContract.MovieEntry.COLUMN_IS_SELECTED, 1);

                    //Setting the batch listener
                    queryHandler.setAsyncApplyBatchListener(new CustomAsyncQueryHandler.AsyncApplyBatchListener() {
                        @Override
                        public void onApplyBatchComplete(int token, Object cookie, ContentProviderResult[] result) {
                            mOnItemClickListener.OnItemClicked(movieId);
                        }
                    });

                    //Create the operations
                    ArrayList<ContentProviderOperation> ops = new ArrayList<>();
                    ops.add(ContentProviderOperation.newUpdate(MovieContract.MovieEntry.CONTENT_URI)
                                    .withSelection(MovieContract.MovieEntry._ID + " = ?",
                                            new String[]{rowId})
                                    .withValue(MovieContract.MovieEntry.COLUMN_IS_SELECTED, 1)
                                    .build());
                    if (mCursor.moveToPosition(previousSelection)) {
                        ops.add(ContentProviderOperation.newUpdate(MovieContract.MovieEntry.CONTENT_URI)
                                .withSelection(MovieContract.MovieEntry._ID + " = ?",
                                        new String[]{mCursor.getString(MovieGalleryFragment.COLUMN_ID)})
                                .withValue(MovieContract.MovieEntry.COLUMN_IS_SELECTED, 0)
                                .build());
                    }

                    queryHandler.applyBatch(1, null, MovieContract.CONTENT_AUTHORITY, ops);
                }
                //Setting the selection to clicked item
                previousSelection = adapterPosition;
            }
        }

        public void OnFavouriteClicked(View view) {
            if (mCursor != null && mCursor.moveToPosition(getAdapterPosition())) {
                DatabaseUtils.setFavourite(view.getContext(), mCursor.getString(MovieGalleryFragment.COLUMN_MOVIE_ID),
                        mCursor.getInt(MovieGalleryFragment.COLUMN_FAVOURITE) == 0 ? 1 : 0);
            }
        }
    }
}
