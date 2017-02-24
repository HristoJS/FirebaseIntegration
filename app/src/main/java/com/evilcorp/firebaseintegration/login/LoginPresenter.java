package com.evilcorp.firebaseintegration.login;

import com.evilcorp.firebaseintegration.R;
import com.evilcorp.firebaseintegration.helper.FirebaseCallback;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;


public class LoginPresenter implements LoginContract.Presenter, FirebaseCallback<Void> {
    private static final String TAG = LoginPresenter.class.getSimpleName();

    private LoginContract.View loginView;
    private LoginInteractor interactor;

    public LoginPresenter(LoginContract.View view, LoginInteractor loginInteractor){
        this.loginView = view;
        this.interactor = loginInteractor;
        interactor.setCallback(this);
    }

    @Override
    public LoginInteractor.TwitterLoginHandler getTwitterHandler() {
        return interactor.getTwitterLoginHandler();
    }

    @Override
    public LoginInteractor.FacebookLoginHandler getFacebookHandler() {
        return interactor.getFacebookLoginHandler();
    }

    @Override
    public void onDestroy() {
        interactor.setCallback(null);
        interactor.destroy();
        interactor = null;
    }

    @Override
    public boolean loginWithEmail(String email, String password) {
        // Reset errors.
        loginView.setEmailError(0);
        loginView.setPasswordError(0);

        // Check for a valid email address.
        if (!isEmailValid(email)) {
            loginView.setEmailError(R.string.error_invalid_email);
            return false;
        }

        // Check for a valid password, if the user entered one.
        if (!isPasswordValid(password)) {
            loginView.setPasswordError(R.string.error_incorrect_password);
            return false;
        }

        interactor.loginWithEmail(email,password);
        return true;
    }

    @Override
    public void loginWithGoogle(GoogleSignInResult result) {
        if (result.isSuccess()) {
            interactor.loginWithGoogle(result);
        } else {
            loginView.loginFail("Google sign-in failed.");
        }
    }

    @Override
    public void loginAsGuest() {
        interactor.loginAsGuest();
        //mAuth.signInAnonymously().addOnCompleteListener(this);
    }

    @Override
    public boolean autoLogin() {
        return interactor.isUserLoggedIn();
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
        loginView.loginSuccess(true);
    }

    @Override
    public void fail(Exception exception) {
        loginView.loginFail(exception.getMessage());
    }
}
