package com.evilcorp.firebaseintegration.db;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by hristo.stoyanov on 01-Feb-17.
 */

public class AddressTable {
    public static final String TABLE_NAME = "addresses";
    public static final String ID = "_id";
    public static final String ADDRESS_LINE0 = "address_line_0";
    public static final String ADDRESS_LINE1 = "address_line_1";
    public static final String COUNTRY_NAME = "country_name";
    public static final String LAT_LONG = "lat_long";
    public static final String TIMESTAMP = "timestamp";

    public static final String[] COLUMNS = {ID,ADDRESS_LINE0,ADDRESS_LINE1,COUNTRY_NAME,LAT_LONG,TIMESTAMP};

    private static final String CREATE_DB  =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                    ID + " integer primary key autoincrement," +
                    ADDRESS_LINE0 + " TEXT," +
                    ADDRESS_LINE1 + " TEXT,"+
                    COUNTRY_NAME + " TEXT,"+
                    LAT_LONG + " TEXT,"+
                    TIMESTAMP + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    public static void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_DB);
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}
