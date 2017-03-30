package com.evilcorp.firebaseintegration.ui.forgotpassword;

import android.support.annotation.NonNull;

import com.evilcorp.firebaseintegration.data.firebase.FirebaseCallback;
import com.evilcorp.firebaseintegration.data.firebase.FirebaseInteractor;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

/**
 * Created by hristo.stoyanov on 3/30/2017.
 */

public class ForgotPasswordInteractor extends FirebaseInteractor implements ForgotPasswordContract.Interactor {

    public ForgotPasswordInteractor() {

    }

    @Override
    public void validatePasscode(String passcode, final FirebaseCallback<String> callback) {
        mFirebaseAuth.verifyPasswordResetCode(passcode).addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isSuccessful()) {
                    callback.success(task.getResult());
                } else {
                    callback.fail(task.getException());
                }
            }
        });
    }

    @Override
    public void sendResetEmail(String email, final FirebaseCallback<Void> callback) {
        mFirebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    callback.success(task.getResult());
                } else {
                    callback.fail(task.getException());
                }
            }
        });
    }
}
