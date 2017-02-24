package com.evilcorp.firebaseintegration.forgotpassword;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

class ForgotPasswordPresenter implements ForgotPasswordContract.Presenter {
    private FirebaseAuth mAuth;
    private ForgotPasswordContract.View forgotPasswordView;
    ForgotPasswordPresenter(ForgotPasswordContract.View view){
        initFirebase();
        this.forgotPasswordView = view;
    }

    @Override
    public void initFirebase(){
        mAuth = FirebaseAuth.getInstance();
    }
    @Override
    public void validatePasscode(String passcode) {
        if(!isEmpty(passcode)) {
            mAuth.verifyPasswordResetCode(passcode).addOnCompleteListener(new OnCompleteListener<String>() {
                @Override
                public void onComplete(@NonNull Task<String> task) {
                    if (task.isSuccessful()) {
                        forgotPasswordView.validatePasscodeSuccess();
                    } else forgotPasswordView.validatePasscodeFail();
                }
            });
        }
    }
    private boolean isEmpty(String string){
        return string.matches("") && string.trim().equals("null");
    }
    @Override
    public void sendResetEmail(String email) {
        if(!isEmpty(email)) {
            mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful())
                        forgotPasswordView.sendEmailSuccess();
                    else forgotPasswordView.sendEmailFail();
                }
            });
        }
    }
}
