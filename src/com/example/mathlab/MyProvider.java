
package com.example.mathlab;

import java.util.concurrent.atomic.AtomicInteger;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class MyProvider extends ContentProvider {
    private static final String TAG = "MyProvider";

    public static final String AUTHORITY_STRING = "content://com.example.mathlab";
    public static final Uri CONTENT_URI = Uri.parse(AUTHORITY_STRING);
    public static final Uri ADDRESS_CONTENT_URI = Uri.parse(AUTHORITY_STRING
            + "/" + DatabaseHelper.TABLE_MATHDATA);
    public static final Uri ADDRESS_CONTENT_URI2 = Uri.parse(AUTHORITY_STRING
            + "/" + DatabaseHelper.TABLE_ERRORDATA);
    

    /**
     * The count of how many known (handled by MyProvider) database mutations
     * are currently being handled. Used by sFileObserver to not reload the
     * database when it's ourselves modifying it.
     */
    private static final AtomicInteger sKnownMutationsInFlight = new AtomicInteger(0);

    private DatabaseHelper mOpenHelper;

    /**
     * Decode a content URL into the table, projection, and arguments used to
     * access the corresponding database rows.
     */
    private static class SqlArguments {
        public String table;
        public final String where;
        public final String[] args;

        /** Operate on existing rows. */
        SqlArguments(Uri url, String where, String[] args) {
            if (url.getPathSegments().size() == 1) {
                this.table = url.getPathSegments().get(0);
                if (!DatabaseHelper.isValidTable(this.table)) {
                    throw new IllegalArgumentException("Bad root path: " + this.table);
                }
                this.where = where;
                this.args = args;
            } else if (url.getPathSegments().size() != 2) {
                throw new IllegalArgumentException("Invalid URI: " + url);
            } else if (!TextUtils.isEmpty(where)) {
                throw new UnsupportedOperationException("WHERE clause not supported: " + url);
            } else {
                this.table = url.getPathSegments().get(0);
                if (!DatabaseHelper.isValidTable(this.table)) {
                    throw new IllegalArgumentException("Bad root path: " + this.table);
                }
                this.where = "_id=" + ContentUris.parseId(url);
                this.args = null;
            }
        }

        /** Insert new rows (no where clause allowed). */
        SqlArguments(Uri url) {
            if (url.getPathSegments().size() == 1) {
                this.table = url.getPathSegments().get(0);
                if (!DatabaseHelper.isValidTable(this.table)) {
                    throw new IllegalArgumentException("Bad root path: " + this.table);
                }
                this.where = null;
                this.args = null;
            } else {
                throw new IllegalArgumentException("Invalid URI: " + url);
            }
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SqlArguments args = new SqlArguments(uri, selection, selectionArgs);

        sKnownMutationsInFlight.incrementAndGet();
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count = db.delete(args.table, args.where, args.args);
        sKnownMutationsInFlight.decrementAndGet();
        Log.v(TAG, args.table + ": " + count + " row(s) deleted");
        return count;
    }

    @Override
    public String getType(Uri uri) {
        // If SqlArguments supplies a where clause, then it must be an item
        // (because we aren't supplying our own where clause).
        SqlArguments args = new SqlArguments(uri, null, null);
        if (TextUtils.isEmpty(args.where)) {
            return "vnd.android.cursor.dir/" + args.table;
        } else {
            return "vnd.android.cursor.item/" + args.table;
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SqlArguments args = new SqlArguments(uri);

        sKnownMutationsInFlight.incrementAndGet();
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final long rowId = db.insert(args.table, null, values);
        sKnownMutationsInFlight.decrementAndGet();
        if (rowId <= 0)
            return null;

        Log.v(TAG, args.table + " <- " + values);
        uri = getUriFor(uri, values, rowId);
        return uri;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new DatabaseHelper(getContext());

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
        SqlArguments args = new SqlArguments(uri, selection, selectionArgs);
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(args.table);

        Cursor ret = qb.query(db, projection, args.where, args.args, null, null, sortOrder);
        ret.setNotificationUri(getContext().getContentResolver(), uri);
        return ret;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
            String[] selectionArgs) {
        SqlArguments args = new SqlArguments(uri, selection, selectionArgs);

        sKnownMutationsInFlight.incrementAndGet();
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count = db.update(args.table, values, args.where, args.args);
        sKnownMutationsInFlight.decrementAndGet();
        Log.v(TAG, args.table + ": " + count + " row(s) <- " + values);
        return count;
    }

    /**
     * Get the content URI of a row added to a table.
     * 
     * @param tableUri of the entire table
     * @param values found in the row
     * @param rowId of the row
     * @return the content URI for this particular row
     */
    private Uri getUriFor(Uri tableUri, ContentValues values, long rowId) {
        if (tableUri.getPathSegments().size() != 1) {
            throw new IllegalArgumentException("Invalid URI: " + tableUri);
        }
        String table = tableUri.getPathSegments().get(0);

        return ContentUris.withAppendedId(tableUri, rowId);
    }

}
