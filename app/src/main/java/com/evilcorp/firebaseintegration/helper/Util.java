package com.evilcorp.firebaseintegration.helper;

/**
 * Created by hristo.stoyanov on 3/30/2017.
 */

public class Util {

    public static boolean equals(Object object1, Object object2) {
        if (object1 == null || object2 == null) {
            return false;
        }
        return object1.equals(object2);
    }
}
