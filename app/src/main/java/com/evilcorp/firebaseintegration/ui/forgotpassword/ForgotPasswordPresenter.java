package com.evilcorp.firebaseintegration.ui.forgotpassword;

import com.evilcorp.firebaseintegration.data.firebase.FirebaseCallback;

class ForgotPasswordPresenter implements ForgotPasswordContract.Presenter {
    private final ForgotPasswordContract.View mForgotPasswordView;
    private final ForgotPasswordInteractor mForgotPasswordInteractor;

    ForgotPasswordPresenter(ForgotPasswordContract.View view, ForgotPasswordInteractor interactor) {
        mForgotPasswordView = view;
        mForgotPasswordInteractor = interactor;
    }

    @Override
    public void validatePasscode(String passcode) {
        if (!isEmpty(passcode)) {
            mForgotPasswordInteractor.validatePasscode(passcode, new FirebaseCallback<String>() {
                @Override
                public void success(String result) {
                    mForgotPasswordView.validatePasscodeSuccess();

                }

                @Override
                public void fail(Exception exception) {
                    mForgotPasswordView.validatePasscodeFail();
                }
            });
        } else {
            mForgotPasswordView.validatePasscodeFail();
        }
    }

    private boolean isEmpty(String string) {
        return string.equals("");
    }

    @Override
    public void sendResetEmail(String email) {
        if (!isEmpty(email)) {
            mForgotPasswordInteractor.sendResetEmail(email, new FirebaseCallback<Void>() {
                @Override
                public void success(Void result) {
                    mForgotPasswordView.sendEmailSuccess();
                }

                @Override
                public void fail(Exception exception) {
                    mForgotPasswordView.sendEmailFail();
                }
            });
        }
    }
}
