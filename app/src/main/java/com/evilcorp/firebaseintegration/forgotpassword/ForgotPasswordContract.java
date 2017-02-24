package com.evilcorp.firebaseintegration.forgotpassword;


interface ForgotPasswordContract {
    interface View{

        void sendEmailSuccess();

        void sendEmailFail();

        void validatePasscodeSuccess();

        void validatePasscodeFail();

    }

    interface Presenter {

        void initFirebase();

        void validatePasscode(String passcode);

        void sendResetEmail(String email);
    }
}
