package com.evilcorp.firebaseintegration.ui.login;

import com.evilcorp.firebaseintegration.R;
import com.evilcorp.firebaseintegration.data.firebase.FirebaseCallback;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;


public class LoginPresenter implements LoginContract.Presenter, FirebaseCallback<Void> {
    private static final String TAG = LoginPresenter.class.getSimpleName();

    private final LoginContract.View mLoginView;
    private LoginInteractor mLoginInteractor;

    public LoginPresenter(LoginContract.View view, LoginInteractor loginInteractor) {
        this.mLoginView = view;
        this.mLoginInteractor = loginInteractor;
        mLoginInteractor.setCallback(this);
    }

    @Override
    public LoginInteractor.TwitterLoginHandler getTwitterHandler() {
        return mLoginInteractor.getTwitterLoginHandler();
    }

    @Override
    public LoginInteractor.FacebookLoginHandler getFacebookHandler() {
        return mLoginInteractor.getFacebookLoginHandler();
    }

    @Override
    public void onDestroy() {
        mLoginInteractor.setCallback(null);
        mLoginInteractor.destroy();
        mLoginInteractor = null;
    }

    @Override
    public boolean loginWithEmail(String email, String password) {
        // Reset errors.
        mLoginView.setEmailError(0);
        mLoginView.setPasswordError(0);

        // Check for a valid email address.
        if (!isEmailValid(email)) {
            mLoginView.setEmailError(R.string.error_invalid_email);
            return false;
        }

        // Check for a valid password, if the user entered one.
        if (!isPasswordValid(password)) {
            mLoginView.setPasswordError(R.string.error_incorrect_password);
            return false;
        }

        mLoginInteractor.loginWithEmail(email, password);
        return true;
    }

    @Override
    public void loginWithGoogle(GoogleSignInResult result) {
        if (result.isSuccess()) {
            mLoginInteractor.loginWithGoogle(result);
        } else {
            mLoginView.loginFail("Google sign-in failed.");
        }
    }

    @Override
    public void loginAsGuest() {
        mLoginInteractor.loginAsGuest();
        //mAuth.signInAnonymously().addOnCompleteListener(this);
    }

    @Override
    public boolean autoLogin() {
        return mLoginInteractor.isUserLoggedIn();
    }

    @Override
    public boolean isEmailValid(String email) {
        return email.contains("@");
    }

    @Override
    public boolean isPasswordValid(String password) {
        return password.length() > 5 && password.length() < 20;
    }

    @Override
    public void success(Void v) {
        mLoginView.loginSuccess();
    }

    @Override
    public void fail(Exception exception) {
        mLoginView.loginFail(exception.getMessage());
    }
}
