package com.evilcorp.firebaseintegration.ui.forgotpassword;

import com.evilcorp.firebaseintegration.data.firebase.FirebaseCallback;
import com.evilcorp.firebaseintegration.ui.base.BasePresenter;

class ForgotPasswordPresenter extends BasePresenter implements ForgotPasswordContract.Presenter {
    private final ForgotPasswordContract.View mView;
    private final ForgotPasswordContract.Interactor mInteractor;

    ForgotPasswordPresenter(ForgotPasswordContract.View view, ForgotPasswordContract.Interactor interactor) {
        super(view, interactor);
        mView = view;
        mInteractor = interactor;
    }

    @Override
    public void validatePasscode(String passcode) {
        if (!isEmpty(passcode)) {
            mInteractor.validatePasscode(passcode, new FirebaseCallback<String>() {
                @Override
                public void success(String result) {
                    mView.validatePasscodeSuccess();

                }

                @Override
                public void fail(String error) {
                    if (mView.isActive()) {
                        mView.onError(error);
                    }
                }
            });
        } else {
            mView.onError("Please enter a valid passcode");
        }
    }

    @Override
    public void onDestroy() {
        mInteractor.destroyAllListeners();
    }

    @Override
    public void sendResetEmail(String email) {
        if (!isValidEmail(email)) {
            mInteractor.sendResetEmail(email, new FirebaseCallback<Void>() {
                @Override
                public void success(Void result) {
                    mView.sendEmailSuccess();
                }

                @Override
                public void fail(String error) {
                    if (mView.isActive()) {
                        mView.onError(error);
                    }
                }
            });
        }
    }
}
