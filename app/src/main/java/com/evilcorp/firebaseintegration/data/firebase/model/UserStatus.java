package com.evilcorp.firebaseintegration.data.firebase.model;

/**
 * Created by hristo.stoyanov on 23-Feb-17.
 */

public class UserStatus {
    public static final int OFFLINE = 0;
    public static final int ONLINE = 1;
    public static final int AWAY = 2;
    public static final int BUSY = 3;
    public static final int INVISIBLE = 4;

    public static String[] getAll() {
        return new String[]{"Offline", "Online", "Away", "Busy", "Invisible"};
    }

}
