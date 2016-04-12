package com.udacity.myappportfolio.data;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import java.lang.ref.WeakReference;

/**
 * Created by Amardeep on 11/4/16.
 */
public class CustomAsyncQueryHandler extends AsyncQueryHandler {

    private WeakReference<AsyncQueryListener> mListener;

    public interface AsyncQueryListener {
        void onQueryComplete(int token, Object cookie, Cursor cursor);
    }

    public CustomAsyncQueryHandler(ContentResolver cr, AsyncQueryListener listener) {
        super(cr);
        mListener = new WeakReference<AsyncQueryListener>(listener);
    }

    public CustomAsyncQueryHandler(ContentResolver cr) {
        super(cr);
    }

    /**
     * Assign the given {@link AsyncQueryListener} to receive query events from
     * asynchronous calls. Will replace any existing listener.
     */
    public void setQueryListener(AsyncQueryListener listener) {
        mListener = new WeakReference<AsyncQueryListener>(listener);
    }

    @Override
    protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
        final AsyncQueryListener listener = mListener.get();
        if (listener != null) {
            listener.onQueryComplete(token, cookie, cursor);
        } else if (cursor != null) {
            cursor.close();
        }
    }

    @Override
    protected void onInsertComplete(int token, Object cookie, Uri uri) {
        super.onInsertComplete(token, cookie, uri);
    }

    @Override
    protected void onBulkInsertComplete(int token, Object cookie, int result) {
        super.onBulkInsertComplete(token, cookie, result);
    }

    @Override
    protected void onDeleteComplete(int token, Object cookie, int result) {
        super.onDeleteComplete(token, cookie, result);
    }

    @Override
    protected void onUpdateComplete(int token, Object cookie, int result) {
        super.onUpdateComplete(token, cookie, result);
    }
}
