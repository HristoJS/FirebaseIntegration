package com.evilcorp.firebaseintegration.ui.forgotpassword;


import com.evilcorp.firebaseintegration.data.firebase.FirebaseCallback;
import com.evilcorp.firebaseintegration.ui.base.BaseContract;

interface ForgotPasswordContract {
    interface View extends BaseContract.View {

        void sendEmailSuccess();

        void validatePasscodeSuccess();

    }

    interface Presenter extends BaseContract.Presenter {

        void validatePasscode(String passcode);

        void sendResetEmail(String email);
    }

    interface Interactor extends BaseContract.Interactor {

        void validatePasscode(String passcode, FirebaseCallback<String> callback);

        void sendResetEmail(String email, FirebaseCallback<Void> callback);
    }
}
