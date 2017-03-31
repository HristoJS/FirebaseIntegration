package com.evilcorp.firebaseintegration.ui.register;

import android.net.Uri;

import com.evilcorp.firebaseintegration.data.firebase.FirebaseCallback;
import com.evilcorp.firebaseintegration.data.firebase.model.AccountType;
import com.evilcorp.firebaseintegration.data.firebase.model.user.UserAccount;
import com.evilcorp.firebaseintegration.helper.Util;
import com.evilcorp.firebaseintegration.ui.base.BasePresenter;


class RegisterPresenter extends BasePresenter implements RegisterContract.Presenter, FirebaseCallback<Void> {

    private final RegisterContract.View mRegisterView;
    private final RegisterContract.Interactor mRegisterInteractor;

    RegisterPresenter(RegisterContract.View view, RegisterContract.Interactor interactor) {
        super(view, interactor);
        this.mRegisterView = view;
        this.mRegisterInteractor = interactor;
    }

    @Override
    public void register(String email, String confirm_email, String password, String confirm_password, String name, String country, Uri imageUri) {
        if (isEmpty(email) || isEmpty(confirm_email) || isEmpty(password) || isEmpty(confirm_password) || isEmpty(name) || isEmpty(country)) {
            mRegisterView.onError("You need to fill in all the fields");
            return;
        }
        if (imageUri == null) {
            mRegisterView.onError("You need to select an avatar");
            return;
        }
        if (!Util.equals(email, confirm_email)) {
            mRegisterView.onError("Emails don't match");
            return;
        }
        if (!Util.equals(password, confirm_password)) {
            mRegisterView.onError("Passwords don't match");
            return;
        }
        if (!isValidEmail(email)) {
            mRegisterView.onError("Invalid Email");
            return;
        }
        if (!isValidPassword(password)) {
            mRegisterView.onError("Invalid Password");
            return;
        }
        UserAccount userAccount = new UserAccount(email, password, name, imageUri.toString(), AccountType.EMAIL);
        mRegisterInteractor.createUser(userAccount, this);
    }


    @Override
    public void success(Void result) {
        mRegisterView.registerSuccess();
    }

    @Override
    public void fail(String error) {
        if (mRegisterView.isActive()) {
            mRegisterView.onError(error);
        }
    }
}
