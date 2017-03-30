package com.evilcorp.firebaseintegration.ui.forgotpassword;


import com.evilcorp.firebaseintegration.data.firebase.FirebaseCallback;

interface ForgotPasswordContract {
    interface View {

        void sendEmailSuccess();

        void sendEmailFail();

        void validatePasscodeSuccess();

        void validatePasscodeFail();

    }

    interface Presenter {

        void validatePasscode(String passcode);

        void sendResetEmail(String email);
    }

    interface Interactor {

        void validatePasscode(String passcode, FirebaseCallback<String> callback);

        void sendResetEmail(String email, FirebaseCallback<Void> callback);
    }
}
