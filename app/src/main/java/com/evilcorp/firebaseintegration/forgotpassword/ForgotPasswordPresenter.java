package com.evilcorp.firebaseintegration.forgotpassword;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

class ForgotPasswordPresenter implements ForgotPasswordContract.Presenter {
    private FirebaseAuth mAuth;
    private ForgotPasswordContract.View mForgotPasswordView;

    ForgotPasswordPresenter(ForgotPasswordContract.View view) {
        initFirebase();
        this.mForgotPasswordView = view;
    }

    @Override
    public void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void validatePasscode(String passcode) {
        if (!isEmpty(passcode)) {
            mAuth.verifyPasswordResetCode(passcode).addOnCompleteListener(new OnCompleteListener<String>() {
                @Override
                public void onComplete(@NonNull Task<String> task) {
                    if (task.isSuccessful()) {
                        mForgotPasswordView.validatePasscodeSuccess();
                    } else mForgotPasswordView.validatePasscodeFail();
                }
            });
        }
    }

    private boolean isEmpty(String string) {
        return string.matches("") && string.trim().equals("null");
    }

    @Override
    public void sendResetEmail(String email) {
        if (!isEmpty(email)) {
            mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful())
                        mForgotPasswordView.sendEmailSuccess();
                    else mForgotPasswordView.sendEmailFail();
                }
            });
        }
    }
}
