package com.evilcorp.firebaseintegration.ui.register;

import android.net.Uri;

import com.evilcorp.firebaseintegration.data.firebase.FirebaseCallback;
import com.evilcorp.firebaseintegration.data.firebase.model.AccountType;
import com.evilcorp.firebaseintegration.data.firebase.model.UserAccount;


public class RegisterPresenter implements RegisterContract.Presenter, FirebaseCallback<Void> {

    private RegisterContract.View mRegisterView;
    private RegisterInteractor mRegisterInteractor;

    public RegisterPresenter(RegisterContract.View view, RegisterInteractor interactor) {
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
        if (!email.equals(confirm_email)) {
            mRegisterView.onError("Emails don't match");
            return;
        }
        if (!password.equals(confirm_password)) {
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

    private boolean isEmpty(String string) {
        return string.equals("");
    }

    private boolean isValidEmail(String email) {
        return email.contains("@");
    }

    private boolean isValidPassword(String password) {
        return password.length() > 5 && password.length() < 20;
    }

    @Override
    public void success(Void result) {
        mRegisterView.registerSuccess();
    }

    @Override
    public void fail(Exception exception) {
        mRegisterView.onError(exception.getLocalizedMessage());
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDestroy() {
        mRegisterInteractor.destroy();
    }
}
