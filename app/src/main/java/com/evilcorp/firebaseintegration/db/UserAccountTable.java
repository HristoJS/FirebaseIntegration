package com.evilcorp.firebaseintegration.db;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by hristo.stoyanov on 08-Feb-17.
 */

public class UserAccountTable {
    public static final String TABLE_NAME = "user_account";

    public static final String ID = "_id";
    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";
    public static final String NAME = "name";
    public static final String AVATAR = "avatar";
    public static final String ACCOUNT_TYPE = "account_type";

    public static final String[] COLUMNS = {ID,EMAIL,PASSWORD,NAME,AVATAR,ACCOUNT_TYPE};

    private static final String CREATE_DB  =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                    ID + " integer primary key autoincrement," +
                    EMAIL + " TEXT," +
                    PASSWORD + " TEXT,"+
                    NAME + " TEXT,"+
                    AVATAR + " TEXT,"+
                    ACCOUNT_TYPE + " INTEGER)";

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
