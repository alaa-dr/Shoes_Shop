package com.example.shoes_shop.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MyContentProvider extends ContentProvider {

    private DbHelper mDbHelper;
    public static final UriMatcher URI_MATCHER = buildUriMatcher();
    public static final String PATH = "orders";
    public static final int PATH_ORDERS_TOKEN = 100;
    public static final String PATH_FOR_ID = "orders/*";
    public static final int PATH_FOR_ID_ORDERS_TOKEN = 200;

    // Uri Matcher for the content provider
    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = DbContract.AUTHORITY;
        matcher.addURI(authority, PATH, PATH_ORDERS_TOKEN);
        matcher.addURI(authority, PATH_FOR_ID, PATH_FOR_ID_ORDERS_TOKEN);
        return matcher;
    }


    @Override
    public boolean onCreate() {
        mDbHelper = new DbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        final int match = URI_MATCHER.match(uri);
        switch (match) {
            // retrieve orders list
            case PATH_ORDERS_TOKEN: {

                return db.query(DbContract.TABLE_ORDER_NAME, projection, selection, selectionArgs, null, null, sortOrder);
            }
            case PATH_FOR_ID_ORDERS_TOKEN: {
                int tvShowId = (int) ContentUris.parseId(uri);
                selection = DbContract.ORDER_ID + " =?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return db.query(DbContract.TABLE_ORDER_NAME, projection, selection, selectionArgs, null, null, sortOrder);
            }
            default:
                return null;
        }
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int token = URI_MATCHER.match(uri);
        switch (token) {
            case PATH_ORDERS_TOKEN: {
                long id = db.insert(DbContract.TABLE_ORDER_NAME, null, contentValues);
                if (id != -1) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return ContentUris.withAppendedId(uri, id);
            }
            case PATH_FOR_ID_ORDERS_TOKEN: {

            }
            default: {
                throw new UnsupportedOperationException("URI: " + uri + " not supported.");
            }
        }

    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int token = URI_MATCHER.match(uri);
        int rowsDeleted = -1;
        switch (token) {
            case (PATH_ORDERS_TOKEN):
                rowsDeleted = db.delete(DbContract.TABLE_ORDER_NAME, selection, selectionArgs);
                break;
            case (PATH_FOR_ID_ORDERS_TOKEN):
                String ShowIdWhereClause = DbContract.ORDER_ID + "=" + uri.getLastPathSegment();
                if (!TextUtils.isEmpty(selection))
                    ShowIdWhereClause += " AND " + selection;
                rowsDeleted = db.delete(DbContract.TABLE_ORDER_NAME, ShowIdWhereClause, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        // Notifying the changes, if there are any
        if (rowsDeleted != -1)
            getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
