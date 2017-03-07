package com.evilcorp.firebaseintegration.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.ListView;

public class AddressContentProvider extends ContentProvider {
    private static String TAG = AddressContentProvider.class.getSimpleName();
    private static String AUTHORITY = "com.evilcorp.firebaseintegration.db.AddressContentProvider";

    private DBHelper mDbHelper;
    private SQLiteDatabase db;

    //Address
    private static final int ALL_ADDRESSES = 1;
    private static final int SINGLE_ADDRESS = 2;
    public static final Uri ADDRESS_URI =
            Uri.parse("content://" + AUTHORITY +"/"+ AddressTable.TABLE_NAME);

    //UserAccount
    private static final int ALL_USERACCOUNTS = 3;
    private static final int SINGLE_USERACCOUNT = 4;
    public static final Uri USERACCOUNT_URI =
            Uri.parse("content://" + AUTHORITY +"/"+ UserAccountTable.TABLE_NAME);

    private static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, AddressTable.TABLE_NAME, ALL_ADDRESSES);
        uriMatcher.addURI(AUTHORITY, AddressTable.TABLE_NAME+"/#", SINGLE_ADDRESS);
        uriMatcher.addURI(AUTHORITY, UserAccountTable.TABLE_NAME, ALL_USERACCOUNTS);
        uriMatcher.addURI(AUTHORITY, UserAccountTable.TABLE_NAME+"/#", SINGLE_USERACCOUNT);
    }

    @Override
    public boolean onCreate() {
        //getContext().getDatabasePath("AddressDB.db").delete();
        //Log.d(TAG,getContext().getDatabasePath("AddressDB.db").toString());
        mDbHelper = new DBHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
//        switch (uriMatcher.match(uri)) {
//            case ALL_ADDRESSES:
//                return "vnd.android.cursor.dir/vnd." + AUTHORITY + AddressTable.TABLE_NAME;
//            case SINGLE_ADDRESS:
//                return "vnd.android.cursor.item/vnd." + AUTHORITY + AddressTable.TABLE_NAME;
//            default:
//                throw new IllegalArgumentException("Unsupported URI: " + uri);
//        }
        return null;
    }

    @Nullable
    @Override
    @SuppressWarnings("ConstantConditions")
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder)  {
        db = mDbHelper.getReadableDatabase();
        sortOrder = AddressTable.ID+" DESC";
        Cursor cursor = db.query(
                AddressTable.TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        return super.bulkInsert(uri, values);
    }

    @Nullable
    @Override
    @SuppressWarnings("ConstantConditions")
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        db = mDbHelper.getWritableDatabase();

        int uriType = uriMatcher.match(uri);
        switch(uriType){
            case ALL_ADDRESSES:
                long id = db.insert(AddressTable.TABLE_NAME, null, values);
                uri = ContentUris.withAppendedId(ADDRESS_URI, id);
                break;
            case ALL_USERACCOUNTS:
                id = db.insert(UserAccountTable.TABLE_NAME, null, values);
                uri = ContentUris.withAppendedId(USERACCOUNT_URI, id);
                break;
            default:
                throw new SQLException("Failed to insert row into " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return uri;
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        int uriType = uriMatcher.match(uri);
        SQLiteDatabase sqlDB = mDbHelper.getWritableDatabase();
        int rowsDeleted;
        switch (uriType) {
            case ALL_ADDRESSES:
                rowsDeleted = sqlDB.delete(AddressTable.TABLE_NAME, selection,
                        selectionArgs);
                break;
            case SINGLE_ADDRESS:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = sqlDB.delete(
                            AddressTable.TABLE_NAME,
                            AddressTable.ID + "=" + id,
                            null);
                } else {
                    rowsDeleted = sqlDB.delete(
                            AddressTable.TABLE_NAME,
                            AddressTable.ID + "=" + id
                                    + " and " + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        db = mDbHelper.getReadableDatabase();
        int uriType = uriMatcher.match(uri);
        int rowsUpdated;
        switch (uriType) {
            case ALL_ADDRESSES:
                rowsUpdated = db.update(AddressTable.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            case SINGLE_ADDRESS:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = db.update(AddressTable.TABLE_NAME,
                            values,
                            AddressTable.ID + "=" + id,
                            null);
                } else {
                    rowsUpdated = db.update(AddressTable.TABLE_NAME,
                            values,
                            AddressTable.ID + "=" + id
                                    + " and "
                                    + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }


}
