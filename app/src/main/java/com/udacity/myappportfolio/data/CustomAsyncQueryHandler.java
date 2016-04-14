package com.udacity.myappportfolio.data;

import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import java.lang.ref.WeakReference;

/**
 * Created by Amardeep on 11/4/16.
 */
public class CustomAsyncQueryHandler extends AsyncQueryHandler {

    private WeakReference<AsyncQueryListener> mListener;
    private WeakReference<AsyncBulkInsertListener> mBulkInsertListener;
    private WeakReference<AsyncDeleteListener> mDeleteListener;
    private WeakReference<AsyncUpdateListener> mUpdateListener;
    private WeakReference<AsyncApplyBatchListener> mApplyBatchListener;

    public interface AsyncQueryListener {
        void onQueryComplete(int token, Object cookie, Cursor cursor);
    }

    public interface AsyncBulkInsertListener {
        void onBulkInsertComplete(int token, Object cookie, int result);
    }

    public interface AsyncDeleteListener {
        void onDeleteComplete(int token, Object cookie, int result);
    }

    public interface AsyncUpdateListener {
        void onUpdateComplete(int token, Object cookie, int result);
    }

    public interface AsyncApplyBatchListener {
        void onApplyBatchComplete(int token, Object cookie, ContentProviderResult[] result);
    }

    public CustomAsyncQueryHandler(ContentResolver cr) {
        super(cr);
    }

    /**
     * Assign the given {@link AsyncQueryListener} to receive query events from
     * asynchronous calls. Will replace any existing listener.
     */
    public void setQueryListener(AsyncQueryListener listener) {
        mListener = new WeakReference<>(listener);
    }

    /**
     * Assign the given {@link AsyncBulkInsertListener} to receive query events from
     * asynchronous calls. Will replace any existing listener.
     */
    public void setAsyncBulkInsertListener(AsyncBulkInsertListener listener) {
        mBulkInsertListener = new WeakReference<>(listener);
    }

    /**
     * Assign the given {@link AsyncBulkInsertListener} to receive query events from
     * asynchronous calls. Will replace any existing listener.
     */
    public void setAsyncDeleteListener(AsyncDeleteListener listener) {
        mDeleteListener = new WeakReference<>(listener);
    }

    /**
     * Assign the given {@link AsyncUpdateListener} to receive query events from
     * asynchronous calls. Will replace any existing listener.
     */
    public void setAsyncUpdateListener(AsyncUpdateListener listener) {
        mUpdateListener = new WeakReference<>(listener);
    }

    /**
     * Assign the given {@link AsyncApplyBatchListener} to receive query events from
     * asynchronous calls. Will replace any existing listener.
     */
    public void setAsyncApplyBatchListener(AsyncApplyBatchListener listener) {
        mApplyBatchListener = new WeakReference<>(listener);
    }

    @Override
    protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
        if (mListener!= null &&  mListener.get() != null) {
            mListener.get().onQueryComplete(token, cookie, cursor);
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @Override
    protected void onInsertComplete(int token, Object cookie, Uri uri) {
        super.onInsertComplete(token, cookie, uri);
    }

    @Override
    protected void onBulkInsertComplete(int token, Object cookie, int result) {
        if (mBulkInsertListener!= null &&  mBulkInsertListener.get() != null) {
            mBulkInsertListener.get().onBulkInsertComplete(token, cookie, result);
        }
    }

    @Override
    protected void onDeleteComplete(int token, Object cookie, int result) {
        if (mDeleteListener!= null &&  mDeleteListener.get() != null) {
            mDeleteListener.get().onDeleteComplete(token, cookie, result);
        }
    }

    @Override
    protected void onUpdateComplete(int token, Object cookie, int result) {
        if (mUpdateListener!= null &&  mUpdateListener.get() != null) {
            mUpdateListener.get().onUpdateComplete(token, cookie, result);
        }
    }

    @Override
    protected void onApplyBatchComplete(int token, Object cookie, ContentProviderResult[] result) {
        if (mApplyBatchListener!= null &&  mApplyBatchListener.get() != null) {
            mApplyBatchListener.get().onApplyBatchComplete(token, cookie, result);
        }
    }
}
