package com.evilcorp.firebaseintegration.helper;

import android.net.Uri;

/**
 * Created by hristo.stoyanov on 15-Feb-17.
 */

public interface FirebaseCallback<T> {
    void success(T result);
    void fail(Exception exception);
}
