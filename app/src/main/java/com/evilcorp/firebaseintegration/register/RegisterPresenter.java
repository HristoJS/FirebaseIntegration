package com.evilcorp.firebaseintegration.register;

import android.net.Uri;

import com.evilcorp.firebaseintegration.model.firebase.AccountType;
import com.evilcorp.firebaseintegration.model.firebase.UserAccount;


public class RegisterPresenter implements RegisterContract.Presenter, RegisterInteractor.FirebaseCallback {

    private RegisterContract.View mRegisterView;
    private RegisterInteractor mRegisterInteractor;

    public RegisterPresenter(RegisterContract.View view) {
        this.mRegisterView = view;
        this.mRegisterInteractor = new RegisterInteractor(this);
    }

    @Override
    public boolean validateFields(String email, String confirm_email, String password, String confirm_password, String name, String country, Uri imageUri) {
        if (!isConfirmEqual(email, confirm_email)) {
            mRegisterView.validationError("Confirm Email");
            return false;
        }
        if (!isConfirmEqual(password, confirm_password)) {
            mRegisterView.validationError("Confirm Password");
            return false;
        }
        if (isEmpty(email) || isEmpty(confirm_email) || isEmpty(password) || isEmpty(confirm_password) || isEmpty(name) || isEmpty(country)) {
            mRegisterView.validationError("Empty field");
            return false;
        }
        if (imageUri == null) {
            mRegisterView.validationError("Missing Avatar");
            return false;
        }
        if (!isValidEmail(email)) {
            mRegisterView.validationError("Invalid Email");
            return false;
        }
        if (!isValidPassword(password)) {
            mRegisterView.validationError("Invalid Password");
            return false;
        }
        createUserModel(email, password, name, imageUri);
        return true;
    }

    private boolean isEmpty(String string) {
        return string.matches("") && string.trim().equals("null");
    }

    private boolean isValidEmail(String email) {
        return email.contains("@");
    }

    private boolean isValidPassword(String password) {
        return password.length() > 5 && password.length() < 20;
    }

    private boolean isConfirmEqual(String one, String two) {
        return one.equals(two);
    }

    private void createUserModel(String email, String password, String name, Uri imageUri) {
        UserAccount userAccount = new UserAccount();
        userAccount.setEmail(email);
        userAccount.setPassword(password);
        userAccount.setName(name);
        userAccount.setAvatar(imageUri.toString());
        userAccount.setAccountType(AccountType.EMAIL);
        register(userAccount);
    }

    private void register(UserAccount user) {
        mRegisterInteractor.createUser(user);
    }

    @Override
    public void success() {
        mRegisterView.registerSuccess();
    }

    @Override
    public void fail(String reason) {
        mRegisterView.registerFail(reason);
    }
}
