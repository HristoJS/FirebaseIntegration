package com.evilcorp.firebaseintegration.data.firebase;

/**
 * Created by hristo.stoyanov on 15-Feb-17.
 */

public interface FirebaseCallback<T> {

    void success(T result);

    void fail(String error);

}
