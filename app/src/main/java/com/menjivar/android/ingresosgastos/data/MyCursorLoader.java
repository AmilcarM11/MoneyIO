package com.menjivar.android.ingresosgastos.data;

import android.database.Cursor;
import android.support.v4.content.AsyncTaskLoader;

import com.menjivar.android.ingresosgastos.RegistryActivity;

/**
 *
 * Created by Amilcar Menjivar on 08/01/2015.
 */
public abstract class MyCursorLoader extends AsyncTaskLoader<Cursor> {

    final ForceLoadContentObserver mObserver;

    Cursor mCursor;

    DBHelper dbHelper;

    public MyCursorLoader(RegistryActivity activity) {
        super(activity);
        this.mObserver = new ForceLoadContentObserver();
        this.dbHelper = activity.getDataBaseHelper();
    }

    public abstract DBHelper.Query getQuery();

    @Override
    public Cursor loadInBackground() {
        // TODO: find a better way to query.

        DBHelper.Query q = getQuery();
        Cursor cursor = dbHelper.query(q);

        if (cursor != null) {
            cursor.getCount();
            cursor.registerContentObserver(mObserver);
        }
        return cursor;
    }

    /* Runs on the UI thread */
    @Override
    public void deliverResult(Cursor cursor) {
        if (isReset()) {
            // An async query came in while the loader is stopped
            if (cursor != null) {
                cursor.close();
            }
            return;
        }
        Cursor oldCursor = mCursor;
        mCursor = cursor;

        if (isStarted()) {
            super.deliverResult(cursor);
        }

        if (oldCursor != null && oldCursor != cursor && !oldCursor.isClosed()) {
            oldCursor.close();
        }
    }

    @Override
    protected void onStartLoading() {
        if (mCursor != null) {
            deliverResult(mCursor);
        }
        if (takeContentChanged() || mCursor == null) {
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        // Attempt to cancel the current load task if possible.
        cancelLoad();
    }

    @Override
    public void onCanceled(Cursor cursor) {
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
    }

    @Override
    protected void onReset() {
        super.onReset();

        // Ensure the loader is stopped
        onStopLoading();

        if (mCursor != null && !mCursor.isClosed()) {
            mCursor.close();
        }
        mCursor = null;
    }

}
